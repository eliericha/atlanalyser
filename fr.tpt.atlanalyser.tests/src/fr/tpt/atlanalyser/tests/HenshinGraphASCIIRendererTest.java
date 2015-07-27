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

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.junit.Test;

import fr.tpt.atlanalyser.utils.HenshinGraphASCIIRenderer;

public class HenshinGraphASCIIRendererTest {

    private static final EcorePackage   EP = EcorePackage.eINSTANCE;
    private static final HenshinFactory HF = HenshinFactory.eINSTANCE;

    @Test
    public void test() {
        Graph graph = HF.createGraph();
        HF.createNode(graph, EP.getEPackage(), "p");
        HF.createNode(graph, EP.getEClass(), "c1");
        HF.createNode(graph, EP.getEClass(), "c2");

        HF.createEdge(graph.getNode("p"), graph.getNode("c1"),
                EP.getEPackage_EClassifiers());
        HF.createEdge(graph.getNode("p"), graph.getNode("c2"),
                EP.getEPackage_EClassifiers());
        HF.createEdge(graph.getNode("c1"), graph.getNode("c2"),
                EP.getEClass_ESuperTypes());

        System.out.println(HenshinGraphASCIIRenderer.render(graph));
    }

}
