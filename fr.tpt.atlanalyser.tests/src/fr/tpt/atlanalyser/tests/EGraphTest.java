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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.henshin.interpreter.util.HenshinEGraph;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.junit.Before;
import org.junit.Test;

import fr.tpt.atlanalyser.utils.HenshinGraphASCIIRenderer;

public class EGraphTest {

    private static final HenshinFactory HF = HenshinFactory.eINSTANCE;
    private EPackage                    dynamicEcore;

    @Before
    public void setUp() throws IOException {
        // Write the Ecore metamodel as XMI
        XMIResource res = new XMIResourceImpl();
        res.getContents().add(EcorePackage.eINSTANCE);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        res.save(outStream, Collections.emptyMap());

        // Reload it dynamically so that a generic EObject factory is used
        ResourceSet rs = new HenshinResourceSet();
        Resource newRes = rs.createResource(URI.createFileURI("Ecore.ecore"));
        newRes.load(new ByteArrayInputStream(outStream.toByteArray()),
                Collections.emptyMap());
        EObject eObject = newRes.getContents().get(0);
        dynamicEcore = (EPackage) eObject;
    }

    @Test
    public void testHenshinEGraph() {
        EClass eStructFeat = (EClass) dynamicEcore
                .getEClassifier("EStructuralFeature");

        Graph g1 = HF.createGraph("G1");
        HF.createNode(g1, eStructFeat, "f");

        try {
            new HenshinEGraph(g1);
            fail("Expected exception did not occur");
        } catch (Exception e) {
            // The exception is normal because EStructuralFeature is abstract,
            // so we cannot instantiate it in an EGraph. Do nothing.
        }

        eStructFeat.setAbstract(false);

        HenshinEGraph eG1 = new HenshinEGraph(g1);

        assertNotNull(eG1);
        assertEquals(1, eG1.getRoots().size());

        System.out.println("First");
        System.out.println(HenshinGraphASCIIRenderer.render(g1));

        EClass eClass = (EClass) dynamicEcore.getEClassifier("EClass");
        HF.createNode(g1, eClass, "c");
        EReference eStructuralFeatures = (EReference) eClass
                .getEStructuralFeature("eStructuralFeatures");
        HF.createEdge(g1.getNode("c"), g1.getNode("f"), eStructuralFeatures);
        EReference eContainingClass = eStructuralFeatures.getEOpposite();
        HF.createEdge(g1.getNode("f"), g1.getNode("c"), eContainingClass);

        eG1.updateEGraph();

        assertEquals(2, eG1.getNode2ObjectMap().size());

        System.out.println("Second");
        System.out.println(HenshinGraphASCIIRenderer.render(g1));

        EObject newFeat = dynamicEcore.getEFactoryInstance()
                .create(eStructFeat);
        newFeat.eSet(eStructFeat.getEStructuralFeature("name"), "f2");
        eG1.add(newFeat);
        EObject rootObj = eG1.getRoots().get(0);
        @SuppressWarnings("unchecked")
        EList<EObject> feats = (EList<EObject>) rootObj
                .eGet(eStructuralFeatures);
        feats.add(newFeat);

        System.out.println("Third");
        System.out.println(HenshinGraphASCIIRenderer.render(g1));

        assertEquals(3, g1.getNodes().size());
        assertEquals(4, g1.getEdges().size());
    }

}
