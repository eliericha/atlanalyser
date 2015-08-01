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
package fr.tpt.atlanalyser.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.model.Annotation;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.javatuples.Triplet;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import fr.tpt.atlanalyser.ATLAnalyserRuntimeException;
import fr.tpt.atlanalyser.post2pre.Post2Pre;
import fr.tpt.atlanalyser.post2pre.resources.ConcurrentResource;
import fr.tpt.atlanalyser.post2pre.resources.ConcurrentResourceSet;

public class Utils {

    public static String toInstanceString(EObject obj) {
        String name = null;

        try {
            name = (String) obj
                    .eGet(obj.eClass().getEStructuralFeature("name"));
        } catch (Exception e) {
            // ignore
        }

        return String.format("%s : %s", name, obj.eClass().getName());
    }

    public static List<Triplet<EObject, EReference, EObject>> getDanglingHrefs(
            Resource resource) {
        List<Triplet<EObject, EReference, EObject>> danglingObjects = Lists
                .newArrayList();
        for (TreeIterator<EObject> iterator = resource.getAllContents(); iterator
                .hasNext();) {
            EObject obj = iterator.next();
            EClass eClass = obj.eClass();
            EList<EReference> eAllReferences = eClass.getEAllReferences();
            for (EReference eRef : eAllReferences) {
                Object value = obj.eGet(eRef);
                if (eRef.isMany()) {
                    @SuppressWarnings("unchecked")
                    List<EObject> values = (List<EObject>) value;
                    for (EObject tgtObj : values) {
                        if (tgtObj.eResource() == null) {
                            danglingObjects
                                    .add(Triplet.with(obj, eRef, tgtObj));
                        }
                    }
                } else if (value != null) {
                    EObject tgtObj = (EObject) value;
                    if (tgtObj.eResource() == null) {
                        danglingObjects.add(Triplet.with(obj, eRef, tgtObj));
                    }
                }
            }
        }
        return danglingObjects;
    }

    @SuppressWarnings("unchecked")
    public static <T extends EObject> T createProxy(T obj) {
        if (Post2Pre.DISABLE_ALL_DUMPING) {
            return obj;
        }
        InternalEObject proxy = (InternalEObject) EcoreUtil
                .create(obj.eClass());
        URI proxyUri = EcoreUtil.getURI(obj);
        proxy.eSetProxyURI(proxyUri);
        // ((ModelElement) proxy).eSetDirectResource(((InternalEObject) obj)
        // .eDirectResource());
        return (T) proxy;
    }

    public static void resolveRefs(Collection<? extends EObject> objects,
            ResourceSet context) {
        for (EObject eObject : objects) {
            EcoreUtil.resolve(eObject, context);
            for (EContentsEList.FeatureIterator featureIterator = (EContentsEList.FeatureIterator) eObject
                    .eCrossReferences().iterator(); featureIterator.hasNext();) {
                EObject eObject2 = (EObject) featureIterator.next();
                if (eObject2.eIsProxy()) {
                    EReference eReference = (EReference) featureIterator
                            .feature();
                    EObject resolvedObject = EcoreUtil.resolve(eObject2,
                            context);
                    eObject.eSet(eReference, resolvedObject);
                }
            }
        }
    }

    public static EObject resolve(EObject proxy, Resource resource) {
        URI uri = EcoreUtil.getURI(proxy);
        if (!uri.trimFragment().equals(resource.getURI())) {
            throw new IllegalArgumentException();
        }
        EObject resolved = null;
        synchronized (resource) {
            if (resource instanceof ConcurrentResource) {
                ((ConcurrentResource) resource).accessed();
            }
            if (!resource.isLoaded()) {
                try {
                    resource.load(Collections.emptyMap());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            resolved = resource.getEObject(uri.fragment());
        }
        return resolved != null ? resolved : proxy;
    }

    public static String toString(int[] id) {
        ArrayList<Integer> idList = Lists.newArrayList();
        for (int i : id) {
            idList.add(i);
        }
        String stringId = Joiner.on('.').join(idList);
        return stringId;
    }

    public static <T1, T2> void addToMap(final Map<T1, List<T2>> map, T1 key,
            T2 val) {
        if (map.containsKey(key)) {
            map.get(key).add(val);
        } else {
            ArrayList<T2> newArrayList = Lists.newArrayList();
            newArrayList.add(val);
            map.put(key, newArrayList);
        }
    }

    /**
     * Delete all objects contained in obj, even if they are in different
     * resources (EcoreUtil.delete() does not delete those).
     * 
     * @param obj
     */
    public static void deleteTree(EObject obj) {
        LinkedList<EObject> containedRootObjs = Lists.newLinkedList();
        for (TreeIterator<EObject> iterator = obj.eAllContents(); iterator
                .hasNext();) {
            EObject subObj = iterator.next();
            Internal eDirectResource = ((InternalEObject) subObj)
                    .eDirectResource();
            if (eDirectResource != null) {
                containedRootObjs.addFirst(subObj);
            }
        }
        for (EObject object : containedRootObjs) {
            Internal eDirectResource = ((InternalEObject) object)
                    .eDirectResource();
            EcoreUtil.delete(object, true);
            if (eDirectResource.getContents().size() == 0) {
                ConcurrentResourceSet resourceSet = (ConcurrentResourceSet) eDirectResource
                        .getResourceSet();
                resourceSet.lockWrite();
                try {
                    eDirectResource.delete(null);
                } catch (IOException e) {
                    throw new ATLAnalyserRuntimeException(e);
                } finally {
                    resourceSet.unlockWrite();
                }
            }
        }

        EcoreUtil.delete(obj);
    }

    public static void detachTree(EObject obj) {
        LinkedList<EObject> containedRootObjs = Lists.newLinkedList();
        Internal eResource = ((InternalEObject) obj).eDirectResource();
        if (eResource != null) {
            containedRootObjs.add(obj);
        }
        for (TreeIterator<EObject> iterator = obj.eAllContents(); iterator
                .hasNext();) {
            EObject subObj = iterator.next();
            Internal eDirectResource = ((InternalEObject) subObj)
                    .eDirectResource();
            if (eDirectResource != null) {
                containedRootObjs.addFirst(subObj);
            }
        }
        for (EObject object : containedRootObjs) {
            Internal eDirectResource = ((InternalEObject) object)
                    .eDirectResource();
            replaceWithDummy(object);
            if (eDirectResource.getContents().size() == 0
                    || allDummy(eDirectResource.getContents())) {
                ConcurrentResourceSet resourceSet = (ConcurrentResourceSet) eDirectResource
                        .getResourceSet();
                resourceSet.lockWrite();
                try {
                    eDirectResource.delete(null);
                } catch (IOException e) {
                    throw new ATLAnalyserRuntimeException(e);
                } finally {
                    resourceSet.unlockWrite();
                }
            }
        }

        if (eResource == null) {
            EcoreUtil.remove(obj);
        }
    }

    public static void replaceWithDummy(EObject object) {
        Resource eDirectResource = Preconditions
                .checkNotNull(((InternalEObject) object).eDirectResource());
        int index = eDirectResource.getContents().indexOf(object);
        eDirectResource.getContents().set(index, createDummy());
    }

    public static boolean allDummy(EList<EObject> contents) {
        for (EObject eObject : contents) {
            if (!(eObject instanceof Annotation)) {
                return false;
            }
        }
        return true;
    }

    private static EObject createDummy() {
        return HenshinFactory.eINSTANCE.createAnnotation();
    }

    /**
     * Remove from container object but not from resource (contrary to
     * EcoreUtil.remove()).
     * 
     * @param obj
     */
    public static void removeFromContainer(EObject eObject) {
        InternalEObject internalEObject = (InternalEObject) eObject;
        EObject container = internalEObject.eInternalContainer();
        if (container != null) {
            EReference feature = eObject.eContainmentFeature();
            if (feature.isMany()) {
                ((EList<?>) container.eGet(feature)).remove(eObject);
            } else {
                container.eUnset(feature);
            }
        }
    }

    public static void removeFromDirectResource(EObject obj) {
        Internal eDirectResource = ((InternalEObject) obj).eDirectResource();
        if (eDirectResource != null) {
            eDirectResource.getContents().remove(obj);
        }
    }

    public static File findExecOnPath(String execName) {
        String pathVar = System.getenv("PATH");
        List<String> pathDirs = Lists.newArrayList(Splitter.on(
                File.pathSeparator).split(pathVar));

        pathDirs.add("/usr/local/bin");

        File res = null;
        for (String dir : pathDirs) {
            File candidate = new File(dir, execName);
            if (candidate.canExecute()) {
                res = candidate;
                break;
            }
        }

        return res;
    }

    public static String executeCommand(String[] cmd, String stdin) {
        ProcessBuilder procBuilder = new ProcessBuilder(cmd);
        procBuilder.redirectErrorStream(true);

        String output = null;
        try {
            Process process = procBuilder.start();
            OutputStream outputStream = process.getOutputStream();
            outputStream.write(stdin.getBytes());
            outputStream.flush();
            outputStream.close();
            InputStream inputStream = process.getInputStream();
            InputStreamReader isReader = new InputStreamReader(inputStream);

            StringBuilder out = new StringBuilder();
            int x;
            while ((x = isReader.read()) >= 0) {
                out.appendCodePoint(x);
            }
            output = out.toString();
        } catch (IOException e) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(buffer));
            output = buffer.toString();
        }
        return output;
    }

}
