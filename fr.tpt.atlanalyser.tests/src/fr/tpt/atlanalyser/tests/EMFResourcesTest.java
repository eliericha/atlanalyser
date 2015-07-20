/*******************************************************************************
 * Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Elie Richa - initial implementation
 *******************************************************************************/
package fr.tpt.atlanalyser.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.Test;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import fr.tpt.atlanalyser.ATLAnalyserRuntimeException;
import fr.tpt.atlanalyser.post2pre.resources.ConcurrentResource;
import fr.tpt.atlanalyser.utils.CopierWithResolveErrorNotification;
import fr.tpt.atlanalyser.utils.Utils;

public class EMFResourcesTest {

    private static final int          N_WRITERS                    = 3;
    private static final int          N_READERS                    = 5;
    private static final int          WRITERS_PSEUDO_PERIOD_MILLIS = 300;
    private static final int          READERS_PSEUDO_PERIOD_MILLIS = 500;
    private static final int          TEST_DURATION_SECONDS        = 5;
    private static final EcoreFactory EF                           = EcoreFactory.eINSTANCE;

    @Test
    public void testCrossResourceRef() throws IOException {
        ResourceSet resSet = new ResourceSetImpl();
        resSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put("xmi", new XMIResourceFactoryImpl());

        Resource f1Res = resSet.createResource(URI.createFileURI("f1.xmi"));
        Resource f2Res = resSet.createResource(URI.createFileURI("f2.xmi"));

        EPackage pkg = EF.createEPackage();
        EClass c1 = EF.createEClass();
        EClass c2 = EF.createEClass();
        pkg.getEClassifiers().add(c1);
        pkg.getEClassifiers().add(c2);

        f1Res.getContents().add(pkg);
        f2Res.getContents().add(c2);

        f1Res.save(Collections.emptyMap());
        f2Res.save(Collections.emptyMap());
    }

    @Test
    public void testConcurrentResourceAccess() throws Throwable {
        // Configure log4j
        // String configStr = Joiner
        // .on("\n")
        // .join(new String[] {
        // // "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
        // "<Configuration status=\"WARN\">",
        // "  <Appenders>",
        // "    <Console name=\"Console\" target=\"SYSTEM_OUT\">",
        // "      <PatternLayout pattern=\"%8r %d{HH:mm:ss.SSS} [%t] %-5level %logger{1} - %msg%n\"/>",
        // "    </Console>",
        // "  </Appenders>",
        // "  <Loggers>",
        // "    <Logger name=\"MyResourceImpl\" level=\"trace\" additivity=\"false\">",
        // "      <AppenderRef ref=\"Console\"/>",
        // "    </Logger>",
        // "  </Loggers>",
        // "</Configuration>" });
        // InputStream stream = new
        // ByteArrayInputStream(Charset.forName("UTF-8")
        // .encode(configStr).array());
        // ConfigurationSource config = new ConfigurationSource(stream);
        // Configurator.initialize(null, config);

        ResourceSet resSet = new ResourceSetImpl();
        resSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put("tmp", new ResourceFactoryImpl() {
                    @Override
                    public Resource createResource(URI uri) {
                        return new ConcurrentResource(uri);
                    }
                });

        URI uri1 = URI.createFileURI("f1.tmp");
        Resource res1 = resSet.createResource(uri1);
        URI uri2 = URI.createFileURI("f2.tmp");
        Resource res2 = resSet.createResource(uri2);

        List<EPackage> pkgs = Lists.newArrayList();
        List<EClass> classes = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            EPackage pkg = EF.createEPackage();
            res1.getContents().add(pkg);
            pkgs.add(Utils.createProxy(pkg));
            for (int j = 0; j < 3; j++) {
                EClass eClass = EF.createEClass();
                pkg.getEClassifiers().add(eClass);
                res2.getContents().add(eClass);
                classes.add(Utils.createProxy(eClass));
            }
        }

        ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

        List<Thread> threads = Lists.newArrayList();
        LinkedBlockingQueue<Throwable> threadErrors = new LinkedBlockingQueue<Throwable>();

        UncaughtExceptionHandler eh = new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                threadErrors.add(e);
            }
        };

        for (int i = 0; i < N_READERS; i++) {
            String name = "Reader" + i;
            Thread thread = new Thread(new Runnable() {

                Logger LOG = LogManager.getLogger(name);

                @Override
                public void run() {
                    Stopwatch timer = Stopwatch.createStarted();
                    while (timer.elapsed(TimeUnit.SECONDS) < TEST_DURATION_SECONDS) {
                        try {
                            Thread.sleep(new Random()
                                    .nextInt(READERS_PSEUDO_PERIOD_MILLIS));
                        } catch (InterruptedException e) {
                            LOG.info("Interrupted. Exiting.");
                            break;
                        }
                        try {
                            lock.readLock().lock();

                            LOG.info("Reading");

                            List<EClass> resolvedClasses = Lists.newArrayList();
                            for (EClass eClass : classes) {
                                eClass = (EClass) Utils.resolve(eClass, res2);
                                assertFalse(eClass.eIsProxy());
                                resolvedClasses.add(eClass);
                            }

                            List<EPackage> resolvedPkgs = Lists.newArrayList();
                            for (EPackage ePackage : pkgs) {
                                ePackage = (EPackage) Utils.resolve(ePackage,
                                        res1);
                                assertFalse(ePackage.eIsProxy());
                                resolvedPkgs.add(ePackage);

                                synchronized (ePackage) {
                                    // Iterating ePackage.getEClassifiers() will
                                    // resolve the proxies of that list and
                                    // modify the list. So we need to
                                    // synchronize over ePackage.
                                    EList<EClassifier> eClassifiers = ePackage
                                            .getEClassifiers();
                                    assertTrue(resolvedClasses
                                            .containsAll(eClassifiers));
                                }

                            }

                            for (EClass eClass : resolvedClasses) {
                                assertNotNull(eClass.eContainer());
                                assertTrue(resolvedPkgs.contains(eClass
                                        .eContainer()));
                            }

                            LOG.info("Finished reading");
                        } catch (Throwable e) {
                            LOG.throwing(e);
                            throw e;
                        } finally {
                            lock.readLock().unlock();
                        }
                    }
                }
            }, name);
            thread.setUncaughtExceptionHandler(eh);
            threads.add(thread);
        }

        for (int i = 0; i < N_WRITERS; i++) {
            String name = "Writer" + i;
            Thread thread = new Thread(new Runnable() {
                Logger LOG = LogManager.getLogger(name);

                @Override
                public void run() {
                    Stopwatch timer = Stopwatch.createStarted();
                    while (timer.elapsed(TimeUnit.SECONDS) < TEST_DURATION_SECONDS) {
                        try {
                            Thread.sleep(new Random()
                                    .nextInt(WRITERS_PSEUDO_PERIOD_MILLIS));
                        } catch (InterruptedException e) {
                            LOG.info("Interrupted. Exiting.");
                            break;
                        }
                        try {
                            lock.writeLock().lock();

                            LOG.info("Writing");

                            for (Resource res : new Resource[] { res1, res2 }) {
                                if (res.isLoaded()) {
                                    try {
                                        res.save(Collections.emptyMap());
                                    } catch (IOException e) {
                                        throw new AssertionError(Thread
                                                .currentThread().getName(), e);
                                    }
                                    res.unload();
                                }
                            }

                            LOG.info("Finished writing");

                        } catch (Throwable e) {
                            LOG.throwing(e);
                            throw e;
                        } finally {
                            lock.writeLock().unlock();
                        }
                    }
                }
            }, name);
            thread.setUncaughtExceptionHandler(eh);
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        Throwable error = threadErrors.poll(TEST_DURATION_SECONDS,
                TimeUnit.SECONDS);

        if (error != null) {
            for (Thread thread : threads) {
                if (thread.isAlive()) {
                    thread.interrupt();
                }
            }
            throw error;
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                thread.interrupt();
            }
        }

        for (Throwable throwable : threadErrors) {
            throw throwable;
        }
    }

    @Test
    public void testCopyProxies() throws IOException {
        ResourceSet resSet = new ResourceSetImpl();
        resSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put("tmp", new ResourceFactoryImpl() {
                    @Override
                    public Resource createResource(URI uri) {
                        return new ConcurrentResource(uri);
                    }
                });

        URI uri1 = URI.createFileURI("f1.tmp");
        Resource res1 = resSet.createResource(uri1);
        URI uri2 = URI.createFileURI("f2.tmp");
        Resource res2 = resSet.createResource(uri2);

        EPackage pkg = EF.createEPackage();
        res1.getContents().add(pkg);
        List<EClass> classes = Lists.newArrayList();
        for (int j = 0; j < 3; j++) {
            EClass eClass = EF.createEClass();
            pkg.getEClassifiers().add(eClass);
            res2.getContents().add(eClass);
            classes.add(Utils.createProxy(eClass));
        }

        try {
            res2.save(null);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        res2.unload();

        InternalEList<EClassifier> eClassifiers = (InternalEList<EClassifier>) pkg
                .getEClassifiers();
        for (Iterator<EClassifier> iterator = eClassifiers.basicIterator(); iterator
                .hasNext();) {
            EClassifier eClassifier = (EClassifier) iterator.next();
            assertTrue(eClassifier.eIsProxy());
            assertFalse(res2.isLoaded());
        }

        assertFalse(res2.isLoaded());

        // Copier resolves contained proxies
        EPackage copy = EcoreUtil.copy(pkg);
        // Resource was loaded automatically by the copier
        assertTrue(res2.isLoaded());
        eClassifiers = (InternalEList<EClassifier>) copy.getEClassifiers();
        for (Iterator<EClassifier> iterator = eClassifiers.basicIterator(); iterator
                .hasNext();) {
            EClassifier eClassifier = (EClassifier) iterator.next();
            assertFalse(eClassifier.eIsProxy());
        }

        res2.unload();
        copy = CopierWithResolveErrorNotification.copy(pkg);
        assertTrue(res2.isLoaded());
        eClassifiers = (InternalEList<EClassifier>) copy.getEClassifiers();
        for (Iterator<EClassifier> iterator = eClassifiers.basicIterator(); iterator
                .hasNext();) {
            EClassifier eClassifier = (EClassifier) iterator.next();
            assertFalse(eClassifier.eIsProxy());
        }

    }

    @Test(expected = ATLAnalyserRuntimeException.class)
    public void testCopyCopierWithResolveErrors() throws IOException {
        ResourceSet resSet = new ResourceSetImpl();
        resSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put("tmp", new ResourceFactoryImpl() {
                    @Override
                    public Resource createResource(URI uri) {
                        return new ConcurrentResource(uri);
                    }
                });

        URI uri1 = URI.createFileURI("f1.tmp");
        Resource res1 = resSet.createResource(uri1);
        URI uri2 = URI.createFileURI("f2.tmp");
        Resource res2 = resSet.createResource(uri2);

        EPackage pkg = EF.createEPackage();
        res1.getContents().add(pkg);
        List<EClass> classes = Lists.newArrayList();
        for (int j = 0; j < 3; j++) {
            EClass eClass = EF.createEClass();
            pkg.getEClassifiers().add(eClass);
            res2.getContents().add(eClass);
            classes.add(Utils.createProxy(eClass));
        }

        try {
            res2.save(null);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        res2.unload();

        resSet.getResources().remove(res2);

        CopierWithResolveErrorNotification.copy(pkg);
    }
}
