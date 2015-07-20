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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class EPackageUtils {

    private static Logger         LOGGER         = LogManager
                                                         .getLogger(EPackageUtils.class
                                                                 .getSimpleName());
    private static final Registry globalRegistry = Registry.INSTANCE;

    public static EClass findEClassRecursively(EPackage pkg, String eClassName) {
        EClassifier eClassifier = pkg.getEClassifier(eClassName);

        if (eClassifier != null && eClassifier instanceof EClass) {
            return (EClass) eClassifier;
        } else {
            for (EPackage subPkg : pkg.getESubpackages()) {
                EClassifier eClass = findEClassRecursively(subPkg, eClassName);
                if (eClass != null) {
                    return (EClass) eClass;
                }
            }
            return null;
        }
    }

    public static List<EReference> findEReferencesRecursively(EPackage ePkg,
            String eRefName) {
        List<EReference> result = new ArrayList<EReference>();

        for (EClassifier eClassifier : ePkg.getEClassifiers()) {
            if (eClassifier instanceof EClass) {
                EClass eClass = (EClass) eClassifier;
                for (EReference eRef : eClass.getEAllReferences()) {
                    if (eRefName.equals(eRef.getName())) {
                        result.add(eRef);
                    }
                }
            }

            for (EPackage subPkg : ePkg.getESubpackages()) {
                result.addAll(findEReferencesRecursively(subPkg, eRefName));
            }
        }

        return result;
    }

    public static List<EPackage> loadDynamicEcore(ResourceSet rs,
            String metamodelPath) throws IOException {
        Resource resource = rs.createResource(URI.createFileURI(metamodelPath));
        String[] extraURIs = { URI.createFileURI(metamodelPath).toString() };
        try {
            resource.load(null);
        } catch (IOException e) {
            LOGGER.error("Error while loading " + metamodelPath, e);
            System.exit(1);
        }

        List<EPackage> result = new ArrayList<EPackage>();
        for (Iterator<EObject> iterator = resource.getContents().iterator(); iterator
                .hasNext();) {
            EPackage ePackage = (EPackage) iterator.next();
            result.add(ePackage);
            registerPackageAndAllSubPackages(ePackage, rs.getPackageRegistry(),
                    extraURIs);
        }

        return result;
    }

    public static void registerPackage(EPackage ePkg, Registry packageRegistry,
            String[] extraKeys) {
        registerPackageAndAllSubPackages(ePkg, packageRegistry, extraKeys);
        registerPackageAndAllSubPackages(ePkg, globalRegistry, extraKeys);

        if (extraKeys != null) {
            for (String key : extraKeys) {
                packageRegistry.put(key, ePkg);
                globalRegistry.put(key, ePkg);
            }
        }
    }

    public static void registerPackageAndAllSubPackages(EPackage root,
            EPackage.Registry registry, String[] extraKeys) {
        registry.put(root.getNsURI(), root);
        globalRegistry.put(root.getNsURI(), root);
        if (extraKeys != null) {
            for (String key : extraKeys) {
                globalRegistry.put(key, root);
            }
        }
        for (EPackage subPackage : root.getESubpackages()) {
            registerPackageAndAllSubPackages(subPackage, registry, extraKeys);
        }
    }

}
