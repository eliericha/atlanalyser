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
package fr.tpt.atlanalyser.post2pre.resources;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl;
import org.eclipse.emf.ecore.xmi.DanglingHREFException;
import org.javatuples.Triplet;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;

import fr.tpt.atlanalyser.post2pre.Post2Pre;
import fr.tpt.atlanalyser.utils.Utils;

public class ConcurrentResource extends BinaryResourceImpl {

    private static final Logger LOGGER     = LogManager
                                                   .getLogger(ConcurrentResource.class
                                                           .getSimpleName());
    private Stopwatch           sinceLastAccessed;
    private boolean             saved      = false;
    private boolean             isDeleting = false;

    public ConcurrentResource(URI uri) {
        super(uri);
        accessed();
    }

    public synchronized void accessed() {
        if (sinceLastAccessed != null) {
            sinceLastAccessed.stop();
        }
        sinceLastAccessed = Stopwatch.createStarted();
    }

    public long secondsSinceLastAccessed() {
        return sinceLastAccessed.elapsed(TimeUnit.SECONDS);
    }

    @Override
    public synchronized void load(Map<?, ?> options) throws IOException {
        if (!saved) {
            throw new RuntimeException("Loading before saving!");
        }
        if (isLoaded) {
            return;
        }
        accessed();
        LOGGER.trace("Loading resource: " + this.getURI().lastSegment());
        Stopwatch timer = Stopwatch.createStarted();
        Map<Object, Object> newOptions = Maps.newHashMap();
        super.load(mergeMaps(newOptions, options));
        LOGGER.trace("Done loading {} in {}", this.getURI().lastSegment(),
                timer.stop());
    }

    @Override
    public synchronized void delete(Map<?, ?> options) throws IOException {
        LOGGER.trace("Deleting resource: " + this.getURI().lastSegment());
        isDeleting = true;
        super.delete(options);
        isDeleting = false;
        LOGGER.trace("Deleted resource: " + this.getURI().lastSegment());
    }

    @Override
    protected synchronized void doUnload() {
        if (Post2Pre.DISABLE_ALL_DUMPING) {
            return;
        }

        if (!isDeleting) {
            LOGGER.trace("Unloading resource: " + this.getURI().lastSegment());
        }

        if (!saved && !isDeleting) {
            throw new RuntimeException("Unloading a non saved resource!");
        }

        Stopwatch timer = Stopwatch.createStarted();

        super.doUnload();

        if (!isDeleting) {
            LOGGER.trace("Done unloading {} in {}",
                    this.getURI().lastSegment(), timer.stop());
        }
    }

    @Override
    public synchronized void save(Map<?, ?> options) throws IOException {
        if (Post2Pre.DISABLE_ALL_DUMPING) {
            return;
        }

        if (!isLoaded) {
            throw new RuntimeException("Saving a non loaded resource!");
        }

        LOGGER.trace("Saving resource: " + this.getURI().lastSegment());
        Stopwatch timer = Stopwatch.createStarted();
        try {
            Map<String, String> myOptions = Maps.newHashMap();
            // myOptions.put(OPTION_PROCESS_DANGLING_HREF,
            // OPTION_PROCESS_DANGLING_HREF_RECORD);
            super.save(mergeMaps(myOptions, options));
            saved = true;
            if (this.getErrors().size() > 0 || this.getWarnings().size() > 0) {
                LOGGER.warn("Errors while saving {}", this.getURI()
                        .lastSegment());
            }
        } catch (IOWrappedException ex) {
            Throwable cause = ex.getCause();
            if (cause != null && cause instanceof DanglingHREFException) {
                List<Triplet<EObject, EReference, EObject>> danglingObjects = Utils
                        .getDanglingHrefs(this);

                String join = Joiner.on("\n").join(danglingObjects);
                System.err.println(join);
            }
            throw ex;
        }

        LOGGER.trace("Done saving {} in {}", this.getURI().lastSegment(),
                timer.stop());
    }

    @Override
    public synchronized EObject getEObject(String uriFragment) {
        accessed();
        return super.getEObject(uriFragment);
    }

    @Override
    public String toString() {
        return super.toString() + " loaded=" + isLoaded();
    }

}