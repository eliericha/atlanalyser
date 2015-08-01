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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Action.Type;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.GraphElement;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import fr.tpt.atlanalyser.overlapping.GraphOverlapper;
import fr.tpt.atlanalyser.overlapping.GraphOverlapper.InclusionIterator;
import fr.tpt.atlanalyser.overlapping.SubSetGenerator;
import fr.tpt.atlanalyser.overlapping.SubSetIteratorLargerFirst;
import fr.tpt.atlanalyser.overlapping.TraceOverlapFilter;
import fr.tpt.atlanalyser.overlapping.GraphOverlapper.GraphOverlapGenerator;
import fr.tpt.atlanalyser.post2pre.Post2Pre;
import fr.tpt.atlanalyser.utils.HenshinGraphASCIIRenderer;
import fr.tpt.atlanalyser.utils.Morphism;

public class GraphOverlapperTest {

    private static final EcorePackage   EP = EcorePackage.eINSTANCE;
    private static final EcoreFactory   EF = EcoreFactory.eINSTANCE;
    private static final HenshinFactory HF = HenshinFactory.eINSTANCE;
    private Graph                       g1;
    private Graph                       g2;
    private Graph                       g3;
    private Graph                       g4;

    public GraphOverlapperTest() {
        g1 = HF.createGraph("G1");
        g2 = HF.createGraph("G2");

        HF.createNode(g1, EcorePackage.Literals.EPACKAGE, "p1");
        HF.createNode(g1, EcorePackage.Literals.EPACKAGE, "p2");
        HF.createNode(g1, EcorePackage.Literals.EPACKAGE, "p3");

        HF.createNode(g2, EcorePackage.Literals.EPACKAGE, "p1");
        HF.createNode(g2, EcorePackage.Literals.EPACKAGE, "p2");
        HF.createNode(g2, EcorePackage.Literals.EPACKAGE, "p3");

        g3 = HF.createGraph("G1");
        g4 = HF.createGraph("G2");

        HF.createNode(g3, EcorePackage.Literals.EPACKAGE, "p1_1");
        HF.createNode(g3, EcorePackage.Literals.EPACKAGE, "p1_2");
        HF.createNode(g3, EcorePackage.Literals.EPACKAGE, "p1_3");

        HF.createNode(g4, EcorePackage.Literals.EPACKAGE, "p2_1");
        HF.createNode(g4, EcorePackage.Literals.EPACKAGE, "p2_2");
        HF.createNode(g4, EcorePackage.Literals.EPACKAGE, "p2_3");

    }

    @Test
    public void testComputeAllSubGraphs() {
        Graph graph = HenshinFactory.eINSTANCE.createGraph();
        Node p1 = HenshinFactory.eINSTANCE.createNode(graph,
                EcorePackage.Literals.EPACKAGE, "p1");
        Node c1 = HenshinFactory.eINSTANCE.createNode(graph,
                EcorePackage.Literals.ECLASS, "c1");
        Node c2 = HenshinFactory.eINSTANCE.createNode(graph,
                EcorePackage.Literals.ECLASS, "c2");

        HenshinFactory.eINSTANCE.createEdge(p1, c1,
                EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        List<Morphism> subGraphs = GraphOverlapper.computeAllSubGraphs(graph);

        // System.out.println(Joiner.on("\n--\n").join(subGraphs));

        assertEquals(10, subGraphs.size());

        HenshinFactory.eINSTANCE.createEdge(p1, c2,
                EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        subGraphs = GraphOverlapper.computeAllSubGraphs(graph);
        assertEquals(13, subGraphs.size());

        // System.out.println();
        // System.out.println(Joiner.on("\n--\n").join(subGraphs));
    }

    @Test
    public void testComputeAllSubGraphs1() {
        Graph graph = HenshinFactory.eINSTANCE.createGraph("G");
        Node p1 = HenshinFactory.eINSTANCE.createNode(graph,
                EcorePackage.Literals.EPACKAGE, "p1");
        Node c1 = HenshinFactory.eINSTANCE.createNode(graph,
                EcorePackage.Literals.ECLASS, "c1");

        HenshinFactory.eINSTANCE.createEdge(p1, c1,
                EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        List<Morphism> subGraphs = GraphOverlapper.computeAllSubGraphs(graph);

        System.out.println(Joiner.on("\n--\n").join(subGraphs));

        assertEquals(5, subGraphs.size());
    }

    @Test
    public void testComputeAllSubGraphs2() {
        Graph graph = HenshinFactory.eINSTANCE.createGraph("G");

        for (int i = 0; i < 5; i++) {
            Node p1 = HenshinFactory.eINSTANCE.createNode(graph,
                    EcorePackage.Literals.EPACKAGE, "p1");
            Node c1 = HenshinFactory.eINSTANCE.createNode(graph,
                    EcorePackage.Literals.ECLASS, "c1");
            HenshinFactory.eINSTANCE.createEdge(p1, c1,
                    EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);
        }

        List<Morphism> subGraphs = GraphOverlapper.computeAllSubGraphs(graph);

        assertEquals(3125, subGraphs.size());
    }

    @Test
    public void testEnumerateAllSubGraphs1() {
        Graph graph = HenshinFactory.eINSTANCE.createGraph("G");
        Node p1 = HenshinFactory.eINSTANCE.createNode(graph,
                EcorePackage.Literals.EPACKAGE, "p1");
        Node c1 = HenshinFactory.eINSTANCE.createNode(graph,
                EcorePackage.Literals.ECLASS, "c1");

        HenshinFactory.eINSTANCE.createEdge(p1, c1,
                EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        List<Morphism> subGraphs = GraphOverlapper.computeAllSubGraphs(graph);

        ArrayList<Morphism> enumeratedSubGraphs = Lists
                .newArrayList(GraphOverlapper.enumerateAllSubGraphs(graph));

        System.out.println(Joiner.on("\n--\n").join(enumeratedSubGraphs));

        assertEquals(5, subGraphs.size());
    }

    @Test
    public void testEnumerateAllSubGraphs2() {
        Graph graph = HenshinFactory.eINSTANCE.createGraph("G");

        for (int i = 0; i < 5; i++) {
            Node p1 = HenshinFactory.eINSTANCE.createNode(graph,
                    EcorePackage.Literals.EPACKAGE, "p1");
            Node c1 = HenshinFactory.eINSTANCE.createNode(graph,
                    EcorePackage.Literals.ECLASS, "c1");
            HenshinFactory.eINSTANCE.createEdge(p1, c1,
                    EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);
        }

        ArrayList<Morphism> enumeratedSubGraphs = Lists
                .newArrayList(GraphOverlapper.enumerateAllSubGraphs(graph));

        assertEquals(3125, enumeratedSubGraphs.size());
    }

    @Test
    public void testRuleFromMorphism() {
        Morphism m = new Morphism(g1, g2);

        m.add(g1.getNode("p1"), g2.getNode("p1"));
        g1.getNodes().remove(g1.getNode("p2"));
        g2.getNodes().remove(g2.getNode("p3"));

        Triplet<Rule, Morphism, Morphism> triplet = GraphOverlapper
                .createRuleFromMorphism(m);
        Rule r = triplet.getValue0();

        EList<Node> preserved = r.getActionNodes(new Action(Type.PRESERVE));
        assertEquals(1, preserved.size());
        assertEquals("p1", preserved.get(0).getName());
        EList<Node> created = r.getActionNodes(new Action(Type.CREATE));
        assertEquals(1, created.size());
        assertEquals("p2", created.get(0).getName());
        EList<Node> deleted = r.getActionNodes(new Action(Type.DELETE));
        assertEquals(1, deleted.size());
        assertEquals("p3", deleted.get(0).getName());
    }

    @Test
    public void testFindOverlaps() {
        Morphism inclusion = GraphOverlapper.createInclusion(g3,
                Sets.newHashSet((GraphElement) g3.getNode("p1_1")));

        List<Pair<Morphism, Morphism>> overlaps = GraphOverlapper.findOverlaps(
                inclusion, g4);

        // for (Pair<Morphism, Morphism> overlap : overlaps) {
        // System.out.println("========");
        // System.out.println(overlap.getValue0());
        // System.out.println("--");
        // System.out.println(overlap.getValue1());
        // }
        assertEquals(3, overlaps.size());
    }

    @Test
    public void testComputeOverlaps() {
        List<Pair<Morphism, Morphism>> overlaps = GraphOverlapper
                .computeOverlaps(g3, g4);
        for (Pair<Morphism, Morphism> overlap : overlaps) {
            System.out.println("========");
            System.out.println(overlap.getValue0());
            System.out.println("--");
            System.out.println(overlap.getValue1());
        }
        assertEquals(34, overlaps.size());
    }

    @Test
    public void testComputeOverlaps1() {
        Graph g5 = HF.createGraph("G5");

        Node p1 = HF.createNode(g5, EcorePackage.Literals.EPACKAGE, "p1");
        Node c1 = HF.createNode(g5, EcorePackage.Literals.ECLASS, "c1");
        HF.createEdge(p1, c1, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        Graph g6 = HF.createGraph("G6");

        Node p2 = HF.createNode(g6, EcorePackage.Literals.EPACKAGE, "p2");
        Node c2 = HF.createNode(g6, EcorePackage.Literals.ECLASS, "c2");
        HF.createEdge(p2, c2, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        List<Pair<Morphism, Morphism>> overlaps = GraphOverlapper
                .computeOverlaps(g5, g6);
        for (Pair<Morphism, Morphism> overlap : overlaps) {
            System.out.println("========");
            System.out.println(overlap.getValue0());
            System.out.println("--");
            System.out.println(overlap.getValue1());
        }

        assertEquals(5, overlaps.size());
    }

    @Test
    public void testEnumerateOverlaps() {
        List<Pair<Morphism, Morphism>> enumeratedOverlaps = Lists
                .newArrayList(GraphOverlapper.enumerateOverlaps(g3, g4, false,
                        true, false));
        assertEquals(34, enumeratedOverlaps.size());
    }

    @Test
    public void testEnumerateOverlapsExcludeEmptyOverlap() {
        List<Pair<Morphism, Morphism>> enumeratedOverlaps = Lists
                .newArrayList(GraphOverlapper.enumerateOverlaps(g3, g4, true,
                        true, false));
        assertEquals(33, enumeratedOverlaps.size());
    }

    @Test
    public void testSubsetGeneration() {
        Set<Integer> set = Sets.newLinkedHashSet(Arrays.asList(1, 2, 3));
        Set<Set<Integer>> subsets = Sets
                .newLinkedHashSet(new SubSetGenerator<Integer>(set, null, false));
        assertEquals(8, subsets.size());
        subsets = Sets.newLinkedHashSet(new SubSetGenerator<Integer>(set, null,
                true));
        System.out.println(Joiner.on("\n").join(subsets));
        assertEquals(7, subsets.size());
    }

    @Test
    public void testSubsetGenerationLargerFirst() {
        Set<Integer> set = Sets.newLinkedHashSet(Arrays.asList(1, 2, 3));

        List<Set<Integer>> subsets = Lists
                .newArrayList(new SubSetIteratorLargerFirst<Integer>(set, null,
                        false));
        System.out.println(Joiner.on("\n").join(subsets));
        assertEquals(8, subsets.size());

        subsets = Lists.newArrayList(new SubSetIteratorLargerFirst<Integer>(
                set, null, true));
        System.out.println(Joiner.on("\n").join(subsets));
        assertEquals(7, subsets.size());
    }

    @Test
    public void testSubsetGenerationAndSorting() {
        Set<Integer> set = Sets.newLinkedHashSet(Arrays.asList(1, 2, 3));
        List<Set<Integer>> subsets = Lists
                .newArrayList(new SubSetGenerator<Integer>(set, null, false));
        Collections.sort(subsets, new Comparator<Set<Integer>>() {

            @Override
            public int compare(Set<Integer> o1, Set<Integer> o2) {
                return o1.size() - o2.size();
            }
        });
        assertEquals(8, subsets.size());
        System.out.println(Joiner.on("\n").join(subsets));
    }

    @Test
    public void testEnumerateOverlaps1() {
        Graph g5 = HF.createGraph("G5");

        Node p1 = HF.createNode(g5, EcorePackage.Literals.EPACKAGE, "p1");
        Node c1 = HF.createNode(g5, EcorePackage.Literals.ECLASS, "c1");
        HF.createEdge(p1, c1, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        Graph g6 = HF.createGraph("G6");

        Node p2 = HF.createNode(g6, EcorePackage.Literals.EPACKAGE, "p2");
        Node c2 = HF.createNode(g6, EcorePackage.Literals.ECLASS, "c2");
        HF.createEdge(p2, c2, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        int i = 0;
        boolean excludeEmptyOverlap = false;
        boolean enforceEMFConstraints = false;
        boolean fixEdgeAutoMapping = false;
        Post2Pre.KEEP_NAMES = true;
        List<Pair<Morphism, Morphism>> overlaps = Lists
                .newArrayList(GraphOverlapper.enumerateOverlaps(g5, g6,
                        excludeEmptyOverlap, enforceEMFConstraints,
                        fixEdgeAutoMapping));

        System.out.println(Joiner.on("\n======\n").join(
                overlaps.stream().map(o -> o.getValue0().getTarget())
                        .map(HenshinGraphASCIIRenderer::render)
                        .toArray(String[]::new)));

        assertEquals(4, overlaps.size());
    }

    @Test
    public void testEnumerateOverlaps1ForceEMF() {
        Graph g5 = HF.createGraph("G5");

        Node p1 = HF.createNode(g5, EcorePackage.Literals.EPACKAGE, "p1");
        Node c1 = HF.createNode(g5, EcorePackage.Literals.ECLASS, "c1");
        HF.createEdge(p1, c1, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        Graph g6 = HF.createGraph("G6");

        Node p2 = HF.createNode(g6, EcorePackage.Literals.EPACKAGE, "p2");
        Node c2 = HF.createNode(g6, EcorePackage.Literals.ECLASS, "c2");
        HF.createEdge(p2, c2, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        int i = 0;
        for (Pair<Morphism, Morphism> overlap : GraphOverlapper
                .enumerateOverlaps(g5, g6, false, true, false)) {
            i++;
            System.out.println("========");
            System.out.println(overlap.getValue0());
            System.out.println("--");
            System.out.println(overlap.getValue1());
        }

        assertEquals(3, i);
    }

    @Test
    public void testEnumerateOverlaps1ForceEMFFixAutoEdgeMapping() {
        Graph g5 = HF.createGraph("G5");

        Node p1 = HF.createNode(g5, EcorePackage.Literals.EPACKAGE, "p1");
        Node c1 = HF.createNode(g5, EcorePackage.Literals.ECLASS, "c1");
        HF.createEdge(p1, c1, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        Graph g6 = HF.createGraph("G6");

        Node p2 = HF.createNode(g6, EcorePackage.Literals.EPACKAGE, "p2");
        Node c2 = HF.createNode(g6, EcorePackage.Literals.ECLASS, "c2");
        HF.createEdge(p2, c2, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        int i = 0;
        for (Pair<Morphism, Morphism> overlap : GraphOverlapper
                .enumerateOverlaps(g5, g6, false, true, true)) {
            i++;
            System.out.println("========");
            System.out.println(overlap.getValue0());
            System.out.println("--");
            System.out.println(overlap.getValue1());
        }

        assertEquals(4, i);
    }

    @Test
    public void testForcedSubGraph() {
        Edge edge = HF.createEdge(g1.getNode("p2"), g1.getNode("p3"),
                EcorePackage.Literals.EPACKAGE__ESUBPACKAGES);

        List<Morphism> subGraphs = Lists.newArrayList(new InclusionIterator(g1,
                Sets.newHashSet(g1.getNode("p3")), false, true));

        // System.out.println(Joiner.on("\n--\n").join(subGraphs));
        System.out.println(Joiner.on("====\n").join(
                subGraphs.stream().map(s -> s.getSource())
                        .map(HenshinGraphASCIIRenderer::render).toArray()));

        assertEquals(6, subGraphs.size());

        for (Morphism inclusion : subGraphs) {
            assertNotNull(inclusion.getSource().getNode("p3"));
        }

        // subGraphs = Lists.newArrayList(GraphOverlapper.enumerateAllSubGraphs(
        // g1, Sets.newHashSet((GraphElement) edge)));

        // System.out.println(Joiner.on("\n--\n").join(subGraphs));

        // assertEquals(2, subGraphs.size());

        // for (Morphism inclusion : subGraphs) {
        // assertEquals("p2", inclusion.getSource().getEdges(edge.getType())
        // .get(0).getSource().getName());
        // assertEquals("p3", inclusion.getSource().getEdges(edge.getType())
        // .get(0).getTarget().getName());
        // }
    }

    @Test
    public void testOverlappingWithAnchor() {
        Graph g5 = HF.createGraph("G5");

        Node p1 = HF.createNode(g5, EcorePackage.Literals.EPACKAGE, "p1");
        Node c1 = HF.createNode(g5, EcorePackage.Literals.ECLASS, "c1");
        HF.createEdge(p1, c1, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        Graph g6 = HF.createGraph("G6");

        Node p2 = HF.createNode(g6, EcorePackage.Literals.EPACKAGE, "p2");
        Node c2 = HF.createNode(g6, EcorePackage.Literals.ECLASS, "c2");
        HF.createEdge(p2, c2, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        Pair<Morphism, Morphism> anchor = createAnchor(g5, g6,
                new GraphElement[][] { { c1, c2 } });

        List<Pair<Morphism, Morphism>> overlaps = GraphOverlapper.getOverlaps(
                g5, g6, anchor, false, false, true);

        for (Pair<Morphism, Morphism> overlap : overlaps) {
            System.out.println("========");
            System.out.println(overlap.getValue0());
            System.out.println("--");
            System.out.println(overlap.getValue1());
        }

        assertEquals(3, overlaps.size());
    }

    @Test
    public void testOverlappingWithAnchorWithEMFConstraintsWithFixAutoEdgeMapping() {
        Graph g5 = HF.createGraph("G5");

        Node p1 = HF.createNode(g5, EcorePackage.Literals.EPACKAGE, "p1");
        Node c1 = HF.createNode(g5, EcorePackage.Literals.ECLASS, "c1");
        HF.createEdge(p1, c1, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        Graph g6 = HF.createGraph("G6");

        Node p2 = HF.createNode(g6, EcorePackage.Literals.EPACKAGE, "p2");
        Node c2 = HF.createNode(g6, EcorePackage.Literals.ECLASS, "c2");
        HF.createEdge(p2, c2, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        Pair<Morphism, Morphism> anchor = createAnchor(g5, g6,
                new GraphElement[][] { { c1, c2 } });

        List<Pair<Morphism, Morphism>> overlaps = GraphOverlapper.getOverlaps(
                g5, g6, anchor, false, true, true);

        for (Pair<Morphism, Morphism> overlap : overlaps) {
            System.out.println("========");
            System.out.println(overlap.getValue0());
            System.out.println("--");
            System.out.println(overlap.getValue1());
        }

        assertEquals(2, overlaps.size());
    }

    @Test
    public void testOverlappingWithAnchorWithEMFConstraints() {
        Graph g5 = HF.createGraph("G5");

        Node p1 = HF.createNode(g5, EcorePackage.Literals.EPACKAGE, "p1");
        Node c1 = HF.createNode(g5, EcorePackage.Literals.ECLASS, "c1");
        HF.createEdge(p1, c1, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        Graph g6 = HF.createGraph("G6");

        Node p2 = HF.createNode(g6, EcorePackage.Literals.EPACKAGE, "p2");
        Node c2 = HF.createNode(g6, EcorePackage.Literals.ECLASS, "c2");
        HF.createEdge(p2, c2, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        Pair<Morphism, Morphism> anchor = createAnchor(g5, g6,
                new GraphElement[][] { { c1, c2 } });

        List<Pair<Morphism, Morphism>> overlaps = GraphOverlapper.getOverlaps(
                g5, g6, anchor, false, true, false);

        for (Pair<Morphism, Morphism> overlap : overlaps) {
            System.out.println("========");
            System.out.println(overlap.getValue0());
            System.out.println("--");
            System.out.println(overlap.getValue1());
        }

        assertEquals(1, overlaps.size());
    }

    @Test
    public void testOverlappingWithAnchor1() {
        Graph g5 = HF.createGraph("G5");

        Node p1 = HF.createNode(g5, EcorePackage.Literals.EPACKAGE, "p1");
        Node c1 = HF.createNode(g5, EcorePackage.Literals.ECLASS, "c1");
        HF.createEdge(p1, c1, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        Graph g6 = HF.createGraph("G6");

        Node p2 = HF.createNode(g6, EcorePackage.Literals.EPACKAGE, "p2");
        Node c2 = HF.createNode(g6, EcorePackage.Literals.ECLASS, "c2");
        HF.createEdge(p2, c2, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        Pair<Morphism, Morphism> anchor = createAnchor(g5, g6,
                new GraphElement[][] { { p1, p2 } });

        List<Pair<Morphism, Morphism>> overlaps = GraphOverlapper.getOverlaps(
                g5, g6, anchor, false, false, true);

        for (Pair<Morphism, Morphism> overlap : overlaps) {
            System.out.println("========");
            System.out.println(overlap.getValue0());
            System.out.println("--");
            System.out.println(overlap.getValue1());
        }

        assertEquals(3, overlaps.size());
    }

    @Test
    public void testOverlapTypingConflict() {
        EPackage traceMM = EF.createEPackage();
        EClass traceEClass = EF.createEClass();
        traceEClass.setName("Trace");
        EClass rootIn = EF.createEClass();
        rootIn.setName("RootIn");
        EClass someIn = EF.createEClass();
        someIn.setName("SomeIn");
        someIn.getESuperTypes().add(rootIn);
        traceMM.getEClassifiers().add(traceEClass);
        traceMM.getEClassifiers().add(rootIn);
        traceMM.getEClassifiers().add(someIn);
        EClass rootOut = EF.createEClass();
        rootOut.setName("RootOut");

        EReference from = EF.createEReference();
        from.setName("from");
        from.setEType(rootIn);
        from.setUpperBound(-1);
        traceEClass.getEStructuralFeatures().add(from);

        EReference to = EF.createEReference();
        to.setName("to");
        to.setEType(rootOut);
        to.setUpperBound(-1);
        traceEClass.getEStructuralFeatures().add(to);

        Graph g1 = HF.createGraph();
        HF.createNode(g1, traceEClass, "tr");
        HF.createNode(g1, rootIn, "s");
        HF.createEdge(g1.getNode("tr"), g1.getNode("s"), from);

        Graph g2 = HF.createGraph();
        HF.createNode(g2, traceEClass, "tr");
        HF.createNode(g2, someIn, "s");
        HF.createEdge(g2.getNode("tr"), g2.getNode("s"), from);

        boolean excludeEmptyOverlap = true;
        boolean enforceEMFConstraints = true;
        boolean fixEdgeAutoMapping = false;
        ArrayList<Pair<Morphism, Morphism>> overlaps = Lists
                .newArrayList(new GraphOverlapGenerator(g1, g2, null,
                        excludeEmptyOverlap, enforceEMFConstraints,
                        fixEdgeAutoMapping, null));

        System.out.println(Joiner.on("\n").join(overlaps));

        assertEquals(3, overlaps.size());

        overlaps = Lists.newArrayList(new GraphOverlapGenerator(g2, g1, null,
                excludeEmptyOverlap, true, false, null));

        System.out.println(Joiner.on("\n").join(overlaps));

        assertEquals(3, overlaps.size());
    }

    @Test
    public void testOverlap2WayConflict() {
        EPackage ePkg = EF.createEPackage();
        EClass c1 = EF.createEClass();
        ePkg.getEClassifiers().add(c1);
        c1.setName("C1");
        EClass c2 = EF.createEClass();
        ePkg.getEClassifiers().add(c2);
        c2.setName("C2");
        c2.getESuperTypes().add(c1);

        Graph g1 = HF.createGraph("G1"), g2 = HF.createGraph("G2");
        HF.createNode(g1, c1, "n1");
        HF.createNode(g1, c2, "n2");
        HF.createNode(g2, c2, "n1");
        HF.createNode(g2, c1, "n2");

        boolean excludeEmptyOverlap = false;
        boolean enforceEMFConstraints = true;
        boolean fixEdgeAutoMapping = false;
        ArrayList<Pair<Morphism, Morphism>> overlaps = Lists
                .newArrayList(new GraphOverlapGenerator(g1, g2, null,
                        excludeEmptyOverlap, enforceEMFConstraints,
                        fixEdgeAutoMapping, null));

        System.out.println(Joiner.on("\n\n").join(overlaps));

        ArrayList<String> overlapGraphs = Lists.newArrayList(overlaps.stream()
                .map(p -> p.getValue0().getTarget())
                .map(HenshinGraphASCIIRenderer::render).toArray(String[]::new));

        System.out.println(Joiner.on("\n\n").join(overlapGraphs));

        assertEquals(7, overlaps.size());
    }

    @Test
    public void testOverlap2WayConflictWithEdges() {
        EPackage ePkg = EF.createEPackage();

        EClass rootTrace = EF.createEClass();
        ePkg.getEClassifiers().add(rootTrace);
        rootTrace.setName("Trace");

        EClass rootIn = EF.createEClass();
        ePkg.getEClassifiers().add(rootIn);
        rootIn.setName("RootIn");
        EClass rootOut = EF.createEClass();
        ePkg.getEClassifiers().add(rootOut);
        rootOut.setName("RootOut");

        EReference from = EF.createEReference();
        from.setName("from");
        from.setEType(rootIn);
        rootTrace.getEStructuralFeatures().add(from);

        EReference to = EF.createEReference();
        to.setName("to");
        to.setEType(rootOut);
        rootTrace.getEStructuralFeatures().add(to);

        EClass in1 = EF.createEClass();
        ePkg.getEClassifiers().add(in1);
        in1.setName("In1");
        in1.getESuperTypes().add(rootIn);

        EClass out1 = EF.createEClass();
        ePkg.getEClassifiers().add(out1);
        out1.setName("Out1");
        out1.getESuperTypes().add(rootOut);

        EClass r1 = EF.createEClass();
        ePkg.getEClassifiers().add(r1);
        r1.setName("R1");
        r1.getESuperTypes().add(rootTrace);
        EReference in1Ref = EF.createEReference();
        in1Ref.setName("in1");
        in1Ref.setEType(in1);
        r1.getEStructuralFeatures().add(in1Ref);
        EReference out1Ref = EF.createEReference();
        out1Ref.setName("out1");
        out1Ref.setEType(out1);
        r1.getEStructuralFeatures().add(out1Ref);

        Graph g1 = HF.createGraph("G1"), g2 = HF.createGraph("G2");
        HF.createNode(g1, r1, "t1");
        HF.createNode(g1, in1, "n1");
        HF.createEdge(g1.getNode("t1"), g1.getNode("n1"), from);
        HF.createEdge(g1.getNode("t1"), g1.getNode("n1"), in1Ref);
        HF.createNode(g1, rootTrace, "t2");

        HF.createNode(g2, r1, "t1");
        HF.createNode(g2, rootTrace, "t2");

        System.out.println(HenshinGraphASCIIRenderer.render(g1));
        System.out.println(HenshinGraphASCIIRenderer.render(g2));

        boolean excludeEmptyOverlap = false;
        boolean enforceEMFConstraints = true;
        boolean fixEdgeAutoMapping = false;
        ArrayList<Pair<Morphism, Morphism>> overlaps = Lists
                .newArrayList(new GraphOverlapGenerator(g1, g2, null,
                        excludeEmptyOverlap, enforceEMFConstraints,
                        fixEdgeAutoMapping, null));

        // System.out.println(Joiner.on("\n\n").join(overlaps));

        ArrayList<Graph> overlapGraphs = Lists.newArrayList(overlaps.stream()
                .map(p -> p.getValue0().getTarget()).toArray(Graph[]::new));

        System.out.println(Joiner.on("\n\n").join(
                overlapGraphs.stream().map(HenshinGraphASCIIRenderer::render)
                        .toArray()));

        assertEquals(7, overlaps.size());
    }

    @Test
    public void testOverlapFiltering() {
        EPackage traceMM = EF.createEPackage();
        EClass traceEClass = EF.createEClass();
        traceEClass.setName("Trace");
        EClass rootIn = EF.createEClass();
        rootIn.setName("RootIn");
        EClass someIn = EF.createEClass();
        someIn.setName("SomeIn");
        someIn.getESuperTypes().add(rootIn);
        traceMM.getEClassifiers().add(traceEClass);
        traceMM.getEClassifiers().add(rootIn);
        traceMM.getEClassifiers().add(someIn);
        EClass rootOut = EF.createEClass();
        rootOut.setName("RootOut");

        EReference from = EF.createEReference();
        from.setName("from");
        from.setEType(rootIn);
        from.setUpperBound(-1);
        traceEClass.getEStructuralFeatures().add(from);

        EReference to = EF.createEReference();
        to.setName("to");
        to.setEType(rootOut);
        to.setUpperBound(-1);
        traceEClass.getEStructuralFeatures().add(to);

        Graph g1 = HF.createGraph();
        HF.createNode(g1, traceEClass, "tr");
        HF.createNode(g1, rootIn, "s");
        HF.createEdge(g1.getNode("tr"), g1.getNode("s"), from);

        Graph g2 = HF.createGraph();
        HF.createNode(g2, traceEClass, "tr");
        HF.createNode(g2, someIn, "s");
        HF.createEdge(g2.getNode("tr"), g2.getNode("s"), from);

        boolean excludeEmptyOverlap = true;
        boolean enforceEMFConstraints = true;
        boolean fixEdgeAutoMapping = false;
        ArrayList<Pair<Morphism, Morphism>> overlaps = Lists
                .newArrayList(TraceOverlapFilter.filter(
                        new GraphOverlapGenerator(g1, g2, null,
                                excludeEmptyOverlap, enforceEMFConstraints,
                                fixEdgeAutoMapping, null), traceEClass));

        System.out.println(Joiner.on("\n").join(overlaps));

        assertEquals(1, overlaps.size());

        overlaps = Lists.newArrayList(TraceOverlapFilter.filter(
                new GraphOverlapGenerator(g2, g1, null, excludeEmptyOverlap,
                        enforceEMFConstraints, fixEdgeAutoMapping, null),
                traceEClass));

        System.out.println(Joiner.on("\n").join(overlaps));

        assertEquals(1, overlaps.size());

        Graph g3 = HF.createGraph();
        HF.createNode(g3, traceEClass, "tr");
        HF.createNode(g3, rootIn, "s1");
        HF.createNode(g3, rootIn, "s2");
        HF.createEdge(g3.getNode("tr"), g3.getNode("s1"), from);
        HF.createEdge(g3.getNode("tr"), g3.getNode("s2"), from);

        Graph g4 = HF.createGraph();
        HF.createNode(g4, traceEClass, "tr");
        HF.createNode(g4, someIn, "s3");
        HF.createNode(g4, someIn, "s4");
        HF.createEdge(g4.getNode("tr"), g4.getNode("s3"), from);
        HF.createEdge(g4.getNode("tr"), g4.getNode("s4"), from);

        overlaps = Lists.newArrayList(TraceOverlapFilter.filter(
                new GraphOverlapGenerator(g3, g4, null, excludeEmptyOverlap,
                        enforceEMFConstraints, fixEdgeAutoMapping, null),
                traceEClass));

        System.out.println(Joiner.on("\n").join(overlaps));

        ArrayList<Graph> overlapGraphs = Lists.newArrayList(Lists.transform(
                overlaps, new Function<Pair<Morphism, Morphism>, Graph>() {
                    @Override
                    public Graph apply(Pair<Morphism, Morphism> arg0) {
                        return arg0.getValue0().getTarget();
                    }
                }));

        assertEquals(6, overlaps.size());
    }

    private Pair<Morphism, Morphism> createAnchor(Graph g1, Graph g2,
            GraphElement[][] anchoredElements) {
        Graph anchorGraph = HF.createGraph("A");
        Morphism aToG1 = new Morphism(anchorGraph, g1);
        Morphism aToG2 = new Morphism(anchorGraph, g2);
        for (GraphElement[] pair : anchoredElements) {
            if (pair[0] instanceof Node) {
                Node[] nodes = new Node[] { (Node) pair[0], (Node) pair[1] };
                Node anchor = HF.createNode(anchorGraph, nodes[0].getType(),
                        null);
                aToG1.add(anchor, nodes[0]);
                aToG2.add(anchor, nodes[1]);
            } else {
                Edge[] edges = new Edge[] { (Edge) pair[0], (Edge) pair[1] };
                Edge anchor = HF.createEdge(
                        aToG1.getOrigin(edges[0].getSource()),
                        aToG1.getOrigin(edges[0].getTarget()),
                        edges[0].getType());
                aToG1.add(anchor, edges[0]);
                aToG2.add(anchor, edges[1]);
            }
        }
        Pair<Morphism, Morphism> anchor = new Pair<Morphism, Morphism>(aToG1,
                aToG2);
        return anchor;
    }

    @Test
    public void testEdgeIndexing() {
        Graph g1 = HF.createGraph("G1");
        HF.createNode(g1, EP.getEPackage(), "p");
        HF.createNode(g1, EP.getEClass(), "c");
        Edge e1 = HF.createEdge(g1.getNode("p"), g1.getNode("c"),
                EP.getEPackage_EClassifiers());
        e1.setIndex("2");

        System.out.println(HenshinGraphASCIIRenderer.render(g1));

        Graph g2 = HF.createGraph("G2");
        HF.createNode(g2, EP.getEPackage(), "p");
        HF.createNode(g2, EP.getEClass(), "c");
        Edge e2 = HF.createEdge(g2.getNode("p"), g2.getNode("c"),
                EP.getEPackage_EClassifiers());
        e2.setIndex("2");
        System.out.println(HenshinGraphASCIIRenderer.render(g2));

        boolean excludeEmptyOverlap = true;
        boolean enforceEMFConstraints = true;
        boolean fixEdgeAutoMapping = false;
        Engine hengine = null;
        GraphOverlapGenerator graphOverlapGenerator = new GraphOverlapGenerator(
                g1, g2, null, excludeEmptyOverlap, enforceEMFConstraints,
                fixEdgeAutoMapping, hengine);
        ArrayList<Pair<Morphism, Morphism>> overlaps = Lists
                .newArrayList(graphOverlapGenerator);

        System.out.println(Joiner.on("\n").join(
                overlaps.stream().map(p -> p.getValue0().getTarget())
                        .map(HenshinGraphASCIIRenderer::render)
                        .collect(Collectors.toList())));
        assertEquals(1, overlaps.size());

        e1.setIndex(null);
        graphOverlapGenerator = new GraphOverlapGenerator(g1, g2, null,
                excludeEmptyOverlap, enforceEMFConstraints, fixEdgeAutoMapping,
                hengine);
        overlaps = Lists.newArrayList(graphOverlapGenerator);

        System.out.println(Joiner.on("\n").join(
                overlaps.stream().map(p -> p.getValue0().getTarget())
                        .map(HenshinGraphASCIIRenderer::render)
                        .collect(Collectors.toList())));
        assertEquals(2, overlaps.size());

        e1.setIndex("3");
        HF.createNode(g1, EP.getEClass(), "c2");
        HF.createEdge(g1.getNode("p"), g1.getNode("c2"),
                EP.getEPackage_EClassifiers()).setIndex("4");
        System.out.println(HenshinGraphASCIIRenderer.render(g1));

        HF.createNode(g2, EP.getEClass(), "c2");
        HF.createEdge(g2.getNode("p"), g2.getNode("c2"),
                EP.getEPackage_EClassifiers()).setIndex("4");
        System.out.println(HenshinGraphASCIIRenderer.render(g2));

        graphOverlapGenerator = new GraphOverlapGenerator(g1, g2, null,
                excludeEmptyOverlap, enforceEMFConstraints, fixEdgeAutoMapping,
                hengine);
        overlaps = Lists.newArrayList(graphOverlapGenerator);

        System.out.println(Joiner.on("\n").join(
                overlaps.stream().map(p -> p.getValue0().getTarget())
                        .map(HenshinGraphASCIIRenderer::render)
                        .collect(Collectors.toList())));
        assertEquals(1, overlaps.size());

        e1.setIndex(null);
        System.out.println(HenshinGraphASCIIRenderer.render(g1));
        System.out.println(HenshinGraphASCIIRenderer.render(g2));

        graphOverlapGenerator = new GraphOverlapGenerator(g1, g2, null,
                excludeEmptyOverlap, enforceEMFConstraints, fixEdgeAutoMapping,
                hengine);
        overlaps = Lists.newArrayList(graphOverlapGenerator);

        System.out.println(Joiner.on("\n").join(
                overlaps.stream().map(p -> p.getValue0().getTarget())
                        .map(HenshinGraphASCIIRenderer::render)
                        .collect(Collectors.toList())));
        assertEquals(2, overlaps.size());
    }
}
