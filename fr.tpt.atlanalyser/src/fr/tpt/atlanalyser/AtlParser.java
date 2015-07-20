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
package fr.tpt.atlanalyser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.m2m.atl.core.ATLCoreException;

import fr.tpt.atlanalyser.atl.ATL.ATLFactory;
import fr.tpt.atlanalyser.atl.ATL.ATLPackage;
import fr.tpt.atlanalyser.atl.ATL.Module;
import fr.tpt.atlanalyser.atl.OCL.OCLFactory;
import fr.tpt.atlanalyser.atl.OCL.OCLPackage;
import fr.tpt.atlanalyser.atl.PrimitiveTypes.PrimitiveTypesFactory;
import fr.tpt.atlanalyser.atl.PrimitiveTypes.PrimitiveTypesPackage;

public class AtlParser {

    private static final Logger LOGGER = LogManager.getLogger(AtlParser.class
                                               .getSimpleName());
    private ResourceSet         atlResourceSet;
    private Map<String, Module> moduleNameToModule;
    private ATLEnvironment      env;

    public AtlParser() {
        this(new ResourceSetImpl(), new ATLEnvironment());
    }

    public AtlParser(ResourceSet rs) {
        this(rs, new ATLEnvironment());
    }

    public AtlParser(ResourceSet rs, ATLEnvironment env) {
        atlResourceSet = rs;
        this.env = env;
        registerAtlMetamodels(atlResourceSet);
        moduleNameToModule = new HashMap<String, Module>();
    }

    public ATLEnvironment getEnv() {
        return env;
    }

    public Module parseAtlTransformation(File atlTranformation)
            throws FileNotFoundException, ATLAnalyserException {
        return parseAtlTransformation(new FileInputStream(atlTranformation));
    }

    public Module parseAtlTransformation(InputStream atlTransformation)
            throws ATLAnalyserException {
        org.eclipse.m2m.atl.engine.parser.AtlParser atlParser = org.eclipse.m2m.atl.engine.parser.AtlParser
                .getDefault();

        try {
            EObject[] parseResult = atlParser
                    .parseWithProblems(atlTransformation);
            EObject module = parseResult[0];
            if (module == null) {
                // Parse error
                for (int i = 1; i < parseResult.length; i++) {
                    EObject problem = parseResult[i];
                    EEnumLiteral severity = (EEnumLiteral) problem.eGet(problem
                            .eClass().getEStructuralFeature("severity"));
                    String location = (String) problem.eGet(problem.eClass()
                            .getEStructuralFeature("location"));
                    String desc = (String) problem.eGet(problem.eClass()
                            .getEStructuralFeature("description"));
                    LOGGER.error("{} - {} - {}", severity.getLiteral(),
                            location, desc);
                }

                System.exit(1);
            }
            String moduleName = (String) module.eGet(module.eClass()
                    .getEStructuralFeature("name"));

            // Convert to a better typed model by saving it and reloading it
            // after having registered the ATL packages.
            Resource res = module.eResource();
            ByteArrayOutputStream tempOutStream = new ByteArrayOutputStream();
            res.save(tempOutStream, Collections.EMPTY_MAP);
            tempOutStream.close();

            Resource newRes = atlResourceSet.createResource(URI
                    .createURI(moduleName));
            newRes.load(new ByteArrayInputStream(tempOutStream.toByteArray()),
                    Collections.EMPTY_MAP);

            Module wellTypedModule = null;

            for (EObject obj : newRes.getContents()) {
                if (obj instanceof Module) {
                    wellTypedModule = (Module) obj;
                    break;
                }
            }

            if (wellTypedModule != null) {
                if (moduleNameToModule.containsKey(wellTypedModule.getName())) {
                    LOGGER.warn(String
                            .format("Module %s was already parsed. Keeping previous parsed version.",
                                    wellTypedModule.getName()));
                    wellTypedModule = moduleNameToModule.get(wellTypedModule
                            .getName());
                } else {
                    moduleNameToModule.put(wellTypedModule.getName(),
                            wellTypedModule);
                }

                env.add(wellTypedModule);
                return wellTypedModule;
            } else {
                throw new ATLAnalyserException(
                        "ATL transformation did not contain a Module element");
            }
        } catch (ATLCoreException e) {
            LOGGER.error("Failed to parse ATL transformation");
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.error("Failed to convert ATL transformation to well typed model");
            e.printStackTrace();
        }

        return null;
    }

    public static void registerAtlMetamodels(ResourceSet targetResSet) {
        EPackage.Registry reg = targetResSet.getPackageRegistry();

        reg.put(ATLPackage.eNS_URI, ATLFactory.eINSTANCE);
        reg.put(OCLPackage.eNS_URI, OCLFactory.eINSTANCE);
        reg.put(PrimitiveTypesPackage.eNS_URI, PrimitiveTypesFactory.eINSTANCE);

        targetResSet
                .getResourceFactoryRegistry()
                .getExtensionToFactoryMap()
                .put(Resource.Factory.Registry.DEFAULT_EXTENSION,
                        new XMIResourceFactoryImpl());
    }

}
