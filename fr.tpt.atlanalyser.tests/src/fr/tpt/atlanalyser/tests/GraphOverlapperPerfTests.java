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

import static org.junit.Assert.*;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.HenshinPackage;
import org.eclipse.emf.henshin.model.Node;
import org.javatuples.Pair;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.common.collect.Lists;

import fr.tpt.atlanalyser.overlapping.GraphOverlapper;
import fr.tpt.atlanalyser.utils.Morphism;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GraphOverlapperPerfTests {

    private static final HenshinFactory HF = HenshinFactory.eINSTANCE;
    private static Graph                graph;
    private static boolean              test1Done;
    private static boolean              test2Done;
    private static Graph                g1;
    private static Graph                g2;

    @BeforeClass
    public static void setUpClass() {
        // Force initialization of packages
        @SuppressWarnings("unused")
        EClass dummy = HenshinPackage.Literals.NODE;
        dummy = EcorePackage.Literals.EPACKAGE;

        graph = HenshinFactory.eINSTANCE.createGraph("G");

        for (int i = 0; i < 6; i++) {
            Node p1 = HenshinFactory.eINSTANCE.createNode(graph,
                    EcorePackage.Literals.EPACKAGE, "p1");
            Node c1 = HenshinFactory.eINSTANCE.createNode(graph,
                    EcorePackage.Literals.ECLASS, "c1");
            HenshinFactory.eINSTANCE.createEdge(p1, c1,
                    EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);
        }

        g1 = HF.createGraph("G1");

        for (int i = 0; i < 7; i++) {
            HF.createNode(g1, EcorePackage.Literals.EPACKAGE, "p1_" + (i + 1));
        }

        g2 = HF.createGraph("G1");

        for (int i = 0; i < 7; i++) {
            HF.createNode(g2, EcorePackage.Literals.EPACKAGE, "p2_" + (i + 1));
        }

    }

    @Test
    public void test0() {
        Lists.newArrayList(GraphOverlapper.enumerateAllSubGraphs(graph));
        GraphOverlapper.computeAllSubGraphs(graph);
        Lists.newArrayList(GraphOverlapper.enumerateOverlaps(g1, g2, false,
                true, false));
        GraphOverlapper.computeOverlaps(g1, g2);
    }

    @Test
    public void test1PerfEnumerateAllSubGraphs() {
        List<Morphism> enumeratedSubGraphs = Lists.newArrayList(GraphOverlapper
                .enumerateAllSubGraphs(graph));

        test1Done = true;

        System.out.println("test2Done = " + test2Done);

        assertEquals(15625, enumeratedSubGraphs.size());
    }

    @Test
    public void test2PerfComputeAllSubGraphs() {
        List<Morphism> subGraphs = GraphOverlapper.computeAllSubGraphs(graph);

        test2Done = true;

        System.out.println("test1Done = " + test1Done);

        assertEquals(15625, subGraphs.size());
    }

    @Test
    public void test3ComputeOverlapsPerformance() throws Exception {
        try {
            List<Pair<Morphism, Morphism>> overlaps = GraphOverlapper
                    .computeOverlaps(g1, g2);
            assertEquals(1546, overlaps.size());
        } catch (OutOfMemoryError e) {
            System.gc();
            Exception f = new Exception("Found " + GraphOverlapper.nFound
                    + " before exception: " + e);
            f.setStackTrace(e.getStackTrace());
            throw f;
        }
    }

    @Test
    public void test4EnumerateOverlapsPerformance() throws Exception {
        try {
            List<Pair<Morphism, Morphism>> overlaps = Lists
                    .newArrayList(GraphOverlapper.enumerateOverlaps(g1, g2,
                            false, true, false));

            assertEquals(1546, overlaps.size());
        } catch (OutOfMemoryError e) {
            System.gc();
            Exception f = new Exception("Found "
                    + GraphOverlapper.GraphOverlapIterator.nFound
                    + " before exception: " + e);
            f.setStackTrace(e.getStackTrace());
            throw f;
        }
    }

}
