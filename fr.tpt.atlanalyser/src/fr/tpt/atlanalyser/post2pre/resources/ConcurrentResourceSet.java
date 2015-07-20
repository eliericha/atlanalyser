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

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

public class ConcurrentResourceSet extends HenshinResourceSet {

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    public ConcurrentResourceSet(HenshinResourceSet resourceSet) {
        super(resourceSet.getBaseDir().toFileString());
        setPackageRegistry(resourceSet.getPackageRegistry());
        setResourceFactoryRegistry(resourceSet.getResourceFactoryRegistry());
        setURIConverter(resourceSet.getURIConverter());
        getResources().addAll(resourceSet.getResources());
    }

    @Override
    public Resource getResource(URI uri, boolean loadOnDemand) {
        lock.readLock().lock();
        try {
            return super.getResource(uri, loadOnDemand);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void lockWrite() {
        lock.writeLock().lock();
    }

    public void unlockWrite() {
        lock.writeLock().unlock();
    }

    public void lockRead() {
        lock.readLock().lock();
    }

    public void unlockRead() {
        lock.readLock().unlock();
    }

}
