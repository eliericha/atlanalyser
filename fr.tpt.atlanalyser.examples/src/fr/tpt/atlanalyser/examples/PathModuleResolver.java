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
package fr.tpt.atlanalyser.examples;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.m2m.atl.emftvm.Module;
import org.eclipse.m2m.atl.emftvm.impl.resource.EMFTVMResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.util.ModuleNotFoundException;
import org.eclipse.m2m.atl.emftvm.util.ModuleResolver;

public class PathModuleResolver implements ModuleResolver {

    public PathModuleResolver() {
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
                "emftvm", new EMFTVMResourceFactoryImpl());
    }

    @Override
    public Module resolveModule(String name) throws ModuleNotFoundException {
        File file = new File(name);
        if (!file.isFile()) {
            file = new File(name + ".emftvm");
            if (!file.isFile()) {
                throw new ModuleNotFoundException(name);
            }
        }

        ResourceSet resSet = new ResourceSetImpl();
        Resource res = resSet.createResource(URI.createFileURI(name));
        try {
            res.load(Collections.emptyMap());
        } catch (IOException e) {
            throw new ModuleNotFoundException(e);
        }

        return (Module) res.getContents().get(0);
    }

}
