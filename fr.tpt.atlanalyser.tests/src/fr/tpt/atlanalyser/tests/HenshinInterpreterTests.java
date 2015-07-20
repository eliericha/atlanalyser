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
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.staticanalysis.PathFinder;
import org.junit.Test;

import fr.tpt.atlanalyser.utils.NGCUtils;

public class HenshinInterpreterTests {

    private static final HenshinFactory HF = HenshinFactory.eINSTANCE;

    @Test
    public void testPacConsistsOnlyOfPaths() {
        Rule rule = HF.createRule();
        Graph graph = HF.createGraph();
        rule.setLhs(graph);
        NestedCondition pac = graph.createPAC(null);
        Graph conc = pac.getConclusion();
        HF.createNode(graph, EcorePackage.Literals.EPACKAGE, "p");
        HF.createNode(graph, EcorePackage.Literals.ECLASS, "c");
        HF.createNode(conc, EcorePackage.Literals.EPACKAGE, "p");
        HF.createNode(conc, EcorePackage.Literals.ECLASS, "c");
        pac.getMappings().add(graph.getNode("p"), conc.getNode("p"));
        pac.getMappings().add(graph.getNode("c"), conc.getNode("c"));

        HF.createEdge(conc.getNode("p"), conc.getNode("c"),
                EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        assertFalse(PathFinder.pacConsistsOnlyOfPaths(pac));
    }

    @Test
    public void testTrueFalse() {
        assertTrue(NGCUtils.isTrue(NGCUtils.createTrue()));
        assertTrue(NGCUtils.isFalse(NGCUtils.createFalse()));
        assertFalse(NGCUtils.isTrue(NGCUtils.createFalse()));
        assertFalse(NGCUtils.isFalse(NGCUtils.createTrue()));
        
        assertTrue(NGCUtils.createTrue().isTrue());
        assertTrue(NGCUtils.createFalse().isFalse());
        assertFalse(NGCUtils.createFalse().isTrue());
        assertFalse(NGCUtils.createTrue().isFalse());
    }

}
