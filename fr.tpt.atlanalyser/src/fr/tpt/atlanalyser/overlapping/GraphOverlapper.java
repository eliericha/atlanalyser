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
package fr.tpt.atlanalyser.overlapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.InterpreterFactory;
import org.eclipse.emf.henshin.interpreter.Match;
import org.eclipse.emf.henshin.interpreter.RuleApplication;
import org.eclipse.emf.henshin.interpreter.util.HenshinEGraph;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.GraphElement;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import fr.tpt.atlanalyser.post2pre.Post2Pre;
import fr.tpt.atlanalyser.utils.DynamicMorphism;
import fr.tpt.atlanalyser.utils.GraphCopier;
import fr.tpt.atlanalyser.utils.HenshinUtils;
import fr.tpt.atlanalyser.utils.Morphism;
import fr.tpt.atlanalyser.utils.Utils;

public class GraphOverlapper {

    public static int                       nFound = 0;
    private static final InterpreterFactory IF     = InterpreterFactory.INSTANCE;

    public static List<Pair<Morphism, Morphism>> computeOverlaps(Graph g1,
            Graph g2) {
        List<Morphism> subGraphs = computeAllSubGraphs(g1);

        List<Pair<Morphism, Morphism>> result = new ArrayList<Pair<Morphism, Morphism>>();

        nFound = 0;
        for (Morphism inclusion : subGraphs) {
            List<Pair<Morphism, Morphism>> overlapsForSubgraph = findOverlaps(
                    inclusion, g2);
            nFound += overlapsForSubgraph.size();
            result.addAll(overlapsForSubgraph);
        }

        return result;
    }

    public static Iterable<Pair<Morphism, Morphism>> enumerateOverlaps(
            Graph g1, Graph g2, Pair<Morphism, Morphism> anchor,
            boolean excludeEmptyOverlap, boolean enforceEMFConstraints,
            boolean fixEdgeAutoMapping) {
        return new GraphOverlapGenerator(g1, g2, anchor, excludeEmptyOverlap,
                enforceEMFConstraints, fixEdgeAutoMapping, null);
    }

    public static List<Pair<Morphism, Morphism>> getOverlaps(Graph g1,
            Graph g2, Pair<Morphism, Morphism> anchor,
            boolean excludeEmptyOverlap, boolean enforceEMFConstraints,
            boolean fixEdgeAutoMapping) {
        return Lists
                .newArrayList(enumerateOverlaps(g1, g2, anchor,
                        excludeEmptyOverlap, enforceEMFConstraints,
                        fixEdgeAutoMapping));
    }

    public static final class GraphOverlapGenerator implements
            Iterable<Pair<Morphism, Morphism>> {
        private Graph                    g1;
        private Graph                    g2;
        private Pair<Morphism, Morphism> anchor;
        private boolean                  excludeEmptyOverlap;
        private boolean                  enforceEMFConstraints;
        private boolean                  fixEdgeAutoMapping;
        private GraphOverlapIterator     graphOverlapIterator;
        private Engine                   hengine;
        private boolean                  invertResult;

        public GraphOverlapGenerator(Graph g1, Graph g2,
                Pair<Morphism, Morphism> anchor, boolean excludeEmptyOverlap,
                boolean enforceEMFConstraints, boolean fixEdgeAutoMapping,
                Engine hengine) {
            this.g1 = g1;
            this.g2 = g2;
            this.anchor = anchor;
            this.excludeEmptyOverlap = excludeEmptyOverlap;
            this.enforceEMFConstraints = enforceEMFConstraints;
            this.fixEdgeAutoMapping = fixEdgeAutoMapping;
            this.hengine = hengine != null ? hengine : IF.createEngine();
        }

        public GraphOverlapGenerator(Morphism m1, Morphism m2,
                boolean excludeEmptyOverlap, boolean enforceEMFConstraints,
                boolean fixEdgeAutoMapping) {
            this(m1.getSource(), m2.getSource(), new Pair<Morphism, Morphism>(
                    m1, m2), excludeEmptyOverlap, enforceEMFConstraints,
                    fixEdgeAutoMapping, null);
            assert m1.getSource() == m2.getSource();
        }

        @Override
        public Iterator<Pair<Morphism, Morphism>> iterator() {
            graphOverlapIterator = new GraphOverlapIterator(g1, g2, anchor,
                    excludeEmptyOverlap, enforceEMFConstraints,
                    fixEdgeAutoMapping, hengine);
            return graphOverlapIterator;
        }

        public void dispose() {
            if (graphOverlapIterator != null) {
                graphOverlapIterator.dispose();
            }
        }
    }

    public static final class GraphOverlapIterator implements
            Iterator<Pair<Morphism, Morphism>> {

        private static final Logger                 LOGGER                  = LogManager
                                                                                    .getLogger(GraphOverlapIterator.class
                                                                                            .getSimpleName());
        private static final boolean                GRAPH_SIZE_OPTIMISATION = true;
        private Graph                               g1;
        private Graph                               g2;
        private boolean                             computedNextOverlap;
        private Pair<Morphism, Morphism>            nextOverlap;
        private InclusionIterator                   g1Inclusions;
        private OverlapIterator                     overlapIterator;
        private Set<Node>                           forcedSubSet;
        private Pair<Morphism, Morphism>            anchor;
        public static int                           nFound                  = 0;
        public AtomicInteger                        i;
        private boolean                             excludeEmptyOverlap;
        private boolean                             enforceEMFConstraints;
        private boolean                             fixEdgeAutoMapping;
        private Morphism                            inclusion;
        private Engine                              hengine;
        private boolean                             invertResult;
        private static final Function<Node, EClass> funcGetType             = new Function<Node, EClass>() {
                                                                                @Override
                                                                                public EClass apply(
                                                                                        Node arg0) {
                                                                                    return arg0
                                                                                            .getType();
                                                                                }
                                                                            };
        private HashSet<EClass>                     g1Types;
        private HashSet<EClass>                     g2Types;
        private boolean                             typingConflict;
        private List<EClass>                        g2SupertypesOfG1;
        private Map<EClass, List<EClass>>           g2SubtypesOfG1;
        private Map<EClass, List<EClass>>           g1SubtypesOfG2;
        private boolean                             handlingConflicts       = false;
        private Morphism                            originalG2ToG2Copy;

        public GraphOverlapIterator(Graph g1, Graph g2,
                Pair<Morphism, Morphism> anchor, boolean excludeEmptyOverlap,
                boolean enforceEMFConstraints, boolean fixEdgeAutoMapping,
                Engine hengine) {
            this.g1 = g1;
            this.g2 = g2;
            g1Types = Sets.newHashSet(Lists.transform(g1.getNodes(),
                    funcGetType));
            g2Types = Sets.newHashSet(Lists.transform(g2.getNodes(),
                    funcGetType));
            this.anchor = anchor;

            if (GRAPH_SIZE_OPTIMISATION) {
                int g1Size = this.g1.getNodes().size()
                        + this.g1.getEdges().size();
                int g2Size = this.g2.getNodes().size()
                        + this.g2.getEdges().size();
                if (g1Size > g2Size) {
                    this.exchangeG1G2();
                }
            }

            g2SupertypesOfG1 = Lists.newArrayList();
            g2SubtypesOfG1 = Maps.newHashMap();
            g1SubtypesOfG2 = Maps.newHashMap();
            this.typingConflict = this.checkTypingConflict();

            this.excludeEmptyOverlap = excludeEmptyOverlap;
            this.enforceEMFConstraints = enforceEMFConstraints;
            this.fixEdgeAutoMapping = fixEdgeAutoMapping;
            this.hengine = hengine;
            i = new AtomicInteger(0);
            if (anchor == null) {
                this.forcedSubSet = null;
            } else {
                Morphism anchorToG1 = this.anchor.getValue0();
                this.forcedSubSet = Sets.newLinkedHashSet(anchorToG1
                        .getCodomainNodes());
            }
            g1Inclusions = new InclusionIterator(this.g1, this.forcedSubSet,
                    this.excludeEmptyOverlap);
        }

        /**
         * Check that there are no types in g2 that are supertypes of those in
         * g1.
         */
        private boolean checkTypingConflict() {
            boolean g1SuperOfG2 = false, g2SuperOfG1 = false;
            Pair<EClass, EClass> g2SuperOfG1Types = null;
            Pair<EClass, EClass> g1SuperOfG2Types = null;
            for (EClass g2Type : g2Types) {
                for (EClass g1Type : g1Types) {
                    if (g2Type != g1Type) {
                        if (g2Type.isSuperTypeOf(g1Type)) {
                            g2SuperOfG1 = true;
                            g2SupertypesOfG1.add(g2Type);
                            Utils.addToMap(g1SubtypesOfG2, g1Type, g2Type);
                            g2SuperOfG1Types = Pair.with(g1Type, g2Type);
                        } else if (g1Type.isSuperTypeOf(g2Type)) {
                            g1SuperOfG2 = true;
                            Utils.addToMap(g2SubtypesOfG1, g2Type, g1Type);
                            g1SuperOfG2Types = Pair.with(g1Type, g2Type);
                        }
                    }
                }
            }

            if (g1SuperOfG2 && g2SuperOfG1) {
                Message msg = LOGGER
                        .getMessageFactory()
                        .newMessage(
                                "Typing conflict: g1 contains supertype {} of {} "
                                        + " in g2 and g2 contains supertype {} of {} in g1."
                                        + " Some overlaps may be missing.",
                                g1SuperOfG2Types.getValue0().getName(),
                                g1SuperOfG2Types.getValue1().getName(),
                                g2SuperOfG1Types.getValue1().getName(),
                                g2SuperOfG1Types.getValue0().getName());
                LOGGER.trace(msg);
                return true;
            } else if (g2SuperOfG1) {
                // LOGGER.trace("g2 contains supertypes of g1: exchanging g1 and g2");
                // exchangeG1G2();
                return true;
            } else {
                return false;
            }
        }

        private void exchangeG1G2() {
            Graph tmp = this.g1;
            this.g1 = this.g2;
            this.g2 = tmp;
            if (anchor != null) {
                this.anchor = Pair.with(this.anchor.getValue1(),
                        this.anchor.getValue0());
            }
            HashSet<EClass> tmpT = this.g1Types;
            this.g1Types = this.g2Types;
            this.g2Types = tmpT;
            invertResult = !invertResult;
        }

        @Override
        public boolean hasNext() {
            if (!computedNextOverlap) {
                computeNextOverlap();
                computedNextOverlap = true;
            }
            return nextOverlap != null;
        }

        private void computeNextOverlap() {
            nextOverlap = null;
            Pair<Morphism, Morphism> ovlp = null;

            ovlp = iterateOverlaps();
            if (ovlp != null) {
                nextOverlap = ovlp;
                return;
            }

            ovlp = iterateInclusions();
            if (ovlp != null) {
                nextOverlap = ovlp;
                return;
            }
        }

        private List<EClass> getSupertypesInGraph(EClass t, Graph g) {
            List<EClass> res = Lists.newArrayList();
            for (Node n : g.getNodes()) {
                EClass type = n.getType();
                if (type != null && type != t && type.isSuperTypeOf(t)) {
                    res.add(type);
                }
            }
            return res;
        }

        private Pair<Morphism, Morphism> iterateOverlaps() {
            while (overlapIterator != null && overlapIterator.hasNext()) {
                Pair<Morphism, Morphism> ovlp = overlapIterator.next();
                return ovlp;
            }
            return null;
        }

        private Pair<Morphism, Morphism> iterateInclusions() {
            while (g1Inclusions != null && g1Inclusions.hasNext()) {
                if (inclusion != null) {
                    // Free previous resources
                    inclusion.dispose();
                    overlapIterator.dispose();
                }
                inclusion = g1Inclusions.next();

                overlapIterator = new OverlapIterator(inclusion, g2, anchor,
                        enforceEMFConstraints, fixEdgeAutoMapping, i, hengine,
                        typingConflict, g1SubtypesOfG2);
                Pair<Morphism, Morphism> ovlp = iterateOverlaps();
                if (ovlp != null) {
                    return ovlp;
                }
            }
            return null;
        }

        @Override
        public Pair<Morphism, Morphism> next() {
            if (hasNext()) {
                computedNextOverlap = false;
            }
            nFound++;
            if (invertResult) {
                nextOverlap = Pair.with(nextOverlap.getValue1(),
                        nextOverlap.getValue0());
                nextOverlap
                        .getValue0()
                        .getTarget()
                        .setName(
                                String.format("Ov(%s, %s)[%d]", g2.getName(),
                                        g1.getName(), i.intValue()));
            }
            if (handlingConflicts) {
                Morphism newVal0 = originalG2ToG2Copy.compose(nextOverlap
                        .getValue0());
                nextOverlap = nextOverlap.setAt0(newVal0);
            }
            return nextOverlap;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void dispose() {
            if (inclusion != null) {
                inclusion.dispose();
            }
            if (overlapIterator != null) {
                overlapIterator.dispose();
            }
        }

    }

    public static class InclusionGenerator implements Iterable<Morphism> {

        private Graph     graph;
        private Set<Node> forcedSubSet;
        private boolean   excludeEmptySubGraph;

        public InclusionGenerator(Graph graph, Set<Node> forcedSubSet,
                boolean excludeEmptySubGraph) {
            this.graph = graph;
            this.forcedSubSet = forcedSubSet;
            this.excludeEmptySubGraph = excludeEmptySubGraph;
        }

        @Override
        public Iterator<Morphism> iterator() {
            return new InclusionIterator(graph, forcedSubSet,
                    excludeEmptySubGraph);
        }

    }

    public static class InclusionIterator implements Iterator<Morphism> {

        private SubSetIterator<Node> subSetIterator;
        private Graph                graph;
        private boolean              computedNextInclusion;
        private Morphism             nextInclusion;

        public InclusionIterator(Graph graph, Set<Node> forcedSubSet,
                boolean excludeEmptySubGraph) {
            this.graph = graph;
            Set<Node> elSet = new LinkedHashSet<Node>(graph.getNodes());
            // elSet.addAll(graph.getEdges());
            this.subSetIterator = new SubSetIteratorLargerFirst<Node>(elSet,
                    forcedSubSet, excludeEmptySubGraph);
        }

        @Override
        public boolean hasNext() {
            if (!computedNextInclusion) {
                computeNextInclusion();
                computedNextInclusion = true;
            }
            return nextInclusion != null;
        }

        private void computeNextInclusion() {
            while (subSetIterator.hasNext()) {
                Set<GraphElement> subSet = Sets.newHashSet(subSetIterator
                        .next());
                if (validateSubsetAsGraph(subSet)) {
                    nextInclusion = createInclusion(graph, subSet);
                    return;
                }
            }
            nextInclusion = null;
        }

        @Override
        public Morphism next() {
            if (hasNext()) {
                computedNextInclusion = false;
            }
            return nextInclusion;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    public static List<Morphism> computeAllSubGraphs(Graph g1) {
        Set<GraphElement> graphElements = new LinkedHashSet<GraphElement>();
        graphElements.addAll(g1.getNodes());
        graphElements.addAll(g1.getEdges());
        Set<Set<GraphElement>> powerSet = Sets.powerSet(graphElements);

        List<Morphism> result = new ArrayList<Morphism>();
        for (Set<GraphElement> subset : powerSet) {
            if (validateSubsetAsGraph(subset)) {
                result.add(createInclusion(g1, subset));
            }
        }

        return result;
    }

    /**
     * A set of GraphElement is a valid graph if there are no dangling edges,
     * i.e. all edges have both their source and target nodes in the same
     * subset.
     * 
     * @param subset
     * @return
     */
    private static boolean validateSubsetAsGraph(Set<GraphElement> subset) {
        for (GraphElement graphElement : subset) {
            if (graphElement instanceof Edge) {
                Edge edge = (Edge) graphElement;
                if (!subset.contains(edge.getSource())
                        || !subset.contains(edge.getTarget())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Iterable<Morphism> enumerateAllSubGraphs(Graph g,
            Set<Node> forcedElements) {
        return new InclusionGenerator(g, forcedElements, false);
    }

    public static Morphism createInclusion(Graph g1, Set<GraphElement> subset) {
        Graph origin = HenshinFactory.eINSTANCE.createGraph();
        Morphism inclusion = new Morphism(origin, g1);

        // Process nodes first
        for (GraphElement elem : Collections2.filter(subset,
                Predicates.instanceOf(Node.class))) {
            if (elem.getGraph() != g1) {
                throw new IllegalArgumentException(
                        "Element in subset but not in graph");
            }
            Node nodeCopy = (Node) EcoreUtil.copy(elem);
            origin.getNodes().add(nodeCopy);
            inclusion.add(nodeCopy, elem);
        }

        // Then edges
        for (GraphElement elem : Collections2.filter(subset,
                Predicates.instanceOf(Edge.class))) {
            Edge edge = (Edge) elem;
            if (subset.contains(edge.getSource())
                    && subset.contains(edge.getTarget())) {
                Edge edgeCopy = EcoreUtil.copy(edge);
                edgeCopy.setSource(inclusion.getOrigin(edge.getSource()));
                edgeCopy.setTarget(inclusion.getOrigin(edge.getTarget()));
                origin.getEdges().add(edgeCopy);
                inclusion.add(edgeCopy, edge);
            } else {
                // It's a dangling edge. Discard the subset.
                inclusion.dispose();
                return null;
            }
        }

        return inclusion;
    }

    public static Triplet<Rule, Morphism, Morphism> createRuleFromMorphism(
            Morphism m) {
        Rule rule = HenshinFactory.eINSTANCE.createRule();

        Morphism sourceToLhs = GraphCopier.copy(m.getSource());
        Morphism targetToRhs = GraphCopier.copy(m.getTarget());

        Graph lhs = sourceToLhs.getTarget();
        Graph rhs = targetToRhs.getTarget();

        rule.setLhs(lhs);
        rule.setRhs(rhs);

        MappingList mappings = rule.getMappings();

        for (Mapping mapping : m) {
            mappings.add(sourceToLhs.getImage(mapping.getOrigin()),
                    targetToRhs.getImage(mapping.getImage()));
        }

        return Triplet.with(rule, sourceToLhs, targetToRhs);
    }

    public static List<Pair<Morphism, Morphism>> findOverlaps(
            Morphism inclusion, Graph g2) {
        Triplet<Rule, Morphism, Morphism> triplet = createRuleFromMorphism(inclusion);
        Rule rule = triplet.getValue0();
        Morphism targetToRhs = triplet.getValue2();

        Morphism g2ToG2Copy = GraphCopier.copy(g2);
        Graph g2Copy = g2ToG2Copy.getTarget();
        HenshinEGraph g2EGraph = new HenshinEGraph(g2Copy);

        Engine hengine = IF.createEngine();

        List<Pair<Morphism, Morphism>> overlaps = Lists.newArrayList();

        RuleApplication ruleApp = IF.createRuleApplication(hengine);
        ruleApp.setUnit(rule);
        ruleApp.setEGraph(g2EGraph);
        Iterable<Match> matches = hengine.findMatches(rule, g2EGraph, null);

        for (Match match : matches) {
            ruleApp.setCompleteMatch(match);

            if (ruleApp.execute(null)) {
                Match resultMatch = ruleApp.getResultMatch();
                Morphism coMatch = new Morphism(resultMatch, g2EGraph);

                Morphism g2CopyToOverlap = GraphCopier.copy(g2Copy);
                Graph overlapGraph = g2CopyToOverlap.getTarget();
                overlapGraph.setName(String.format("Overlap(%s, %s)", inclusion
                        .getTarget().getName(), g2.getName()));

                Morphism g1ToOverlap = targetToRhs.compose(coMatch).compose(
                        g2CopyToOverlap);
                Morphism g2ToOverlap = g2ToG2Copy.compose(g2CopyToOverlap);

                Pair<Morphism, Morphism> overlap = new Pair<Morphism, Morphism>(
                        g1ToOverlap, g2ToOverlap);

                removeNonMappedEdges(overlap);
                assignNamesInOverlapGraph(overlap);

                overlaps.add(overlap);

                ruleApp.undo(null);
            }
        }

        return overlaps;
    }

    public static final class OverlapIterator implements
            Iterator<Pair<Morphism, Morphism>> {

        private static final Logger                                   LOGGER                = LogManager
                                                                                                    .getLogger(OverlapIterator.class
                                                                                                            .getSimpleName());
        private static final Function<? super Node, ? extends EClass> funcGetType;

        static {
            funcGetType = new Function<Node, EClass>() {
                @Override
                public EClass apply(Node arg0) {
                    return arg0.getType();
                }
            };
        }

        private Morphism                                              inclusion;
        private Graph                                                 g2;
        private boolean                                               computedNextOverlap;
        private Pair<Morphism, Morphism>                              nextOverlap;
        private Rule                                                  rule;
        private Morphism                                              targetToRhs;
        private HenshinEGraph                                         g2EGraph;
        private Iterator<Match>                                       matches;
        private Morphism                                              sourceToLhs;
        private AtomicInteger                                         overlapCounter;
        private boolean                                               enforceEMFConstraints = true;
        private boolean                                               preventEdgeAutoMapping;
        private boolean                                               typingConflict;
        private ArrayList<Node>                                       conflictNodes;
        private Map<EClass, List<EClass>>                             g1SubtypesOfG2;
        private Morphism                                              originalInclusion;
        private boolean                                               discardEdgeAutoMapping;
        private Pair<Morphism, Morphism>                              anchor;
        private Engine                                                hengine;

        public OverlapIterator(Morphism inclusion, Graph g2,
                Pair<Morphism, Morphism> anchor, boolean enforceEMFConstraints,
                boolean fixEdgeAutoMapping, AtomicInteger overlapCounter,
                Engine hengine, boolean overallTypingConflict,
                Map<EClass, List<EClass>> g1SubtypesOfG2) {
            this.enforceEMFConstraints = enforceEMFConstraints;
            this.preventEdgeAutoMapping = fixEdgeAutoMapping;
            this.overlapCounter = overlapCounter;
            this.inclusion = inclusion;
            this.originalInclusion = inclusion;
            this.g2 = g2;

            this.g1SubtypesOfG2 = g1SubtypesOfG2;
            this.conflictNodes = Lists.newArrayList();

            this.typingConflict = false;
            if (overallTypingConflict) {
                this.inclusion = GraphCopier.reverseCopy(inclusion.getSource())
                        .compose(inclusion);
                inclusion = this.inclusion;
                // Upgrade nodes of the inclusion to their supertypes in G2
                // so that they can match onto them
                for (Entry<EClass, List<EClass>> entry : this.g1SubtypesOfG2
                        .entrySet()) {
                    EClass t = entry.getKey();
                    List<EClass> superTypes = entry.getValue();

                    // TODO: this solution only works if there is one
                    // subtype
                    assert superTypes.size() == 1;

                    EClass superType = superTypes.get(0);
                    EList<EReference> superERefs = superType.getEReferences();
                    Graph subGraph = this.inclusion.getSource();
                    EList<Node> nodes = subGraph.getNodes(t);
                    for (Node node : nodes) {
                        this.typingConflict = true;
                        this.conflictNodes.add(node);
                        node.setType(superType);
                        List<Edge> outgoing = Lists.newArrayList(node
                                .getOutgoing());
                        for (Edge edge : outgoing) {
                            if (!superERefs.contains(edge.getType())) {
                                this.inclusion.remove(edge,
                                        this.inclusion.getImage(edge));
                                subGraph.removeEdge(edge);
                            }
                        }
                    }
                }
            }

            Triplet<Rule, Morphism, Morphism> triplet = createRuleFromMorphism(this.inclusion);
            rule = triplet.getValue0();
            sourceToLhs = triplet.getValue1();
            targetToRhs = triplet.getValue2();
            this.conflictNodes.replaceAll(n -> sourceToLhs.getImage(n));

            g2EGraph = new HenshinEGraph(g2);

            this.hengine = hengine == null ? IF.createEngine() : hengine;

            Match partialMatch = null;

            this.anchor = anchor;
            if (anchor != null) {
                partialMatch = IF.createMatch(rule, false);
                Morphism anchorToG1 = anchor.getValue0();
                Morphism anchorToG2 = anchor.getValue1();
                for (Mapping mapping : anchorToG1) {
                    // Find anchor node in LHS
                    Node nodeInAnchor = mapping.getOrigin();
                    Node nodeInG1 = mapping.getImage();
                    Node nodeInSubGraph = inclusion.getOrigin(nodeInG1);
                    Node nodeInLhs = sourceToLhs.getImage(nodeInSubGraph);

                    // Find anchor node in EGraph
                    Node nodeInG2 = anchorToG2.getImage(nodeInAnchor);
                    if (nodeInG2 != null) {
                        EObject eObjInEGraph = g2EGraph.getNode2ObjectMap()
                                .get(nodeInG2);

                        // Partial match
                        partialMatch.setNodeTarget(nodeInLhs, eObjInEGraph);
                    }
                }
            }

            matches = this.hengine.findMatches(rule, g2EGraph, partialMatch)
                    .iterator();
        }

        @Override
        public boolean hasNext() {
            if (!computedNextOverlap) {
                computeNextOverlap();
                computedNextOverlap = true;
            }
            return nextOverlap != null;
        }

        private void computeNextOverlap() {
            nextOverlap = null;
            LOGGER.trace("Finding next overlap...");
            while (nextOverlap == null && matches.hasNext()) {
                LOGGER.trace("Found!");
                Match match = matches.next();
                boolean lhsMatchInvalid = !match.isValid();

                // Create a copy of G2, transpose the match to it and apply the
                // rule there.
                DynamicMorphism g2ToG2Copy = new DynamicMorphism(
                        GraphCopier.copy(g2));
                Graph g2Copy = g2ToG2Copy.getTarget();
                if (this.typingConflict) {
                    // Downgrade conflict nodes from the supertype in G2 to the
                    // subtype in G1.
                    for (Node n : this.conflictNodes) {
                        EObject tgtObj = match.getNodeTarget(n);
                        Node tgtNode = g2EGraph.getObject2NodeMap().get(tgtObj);
                        Node subTypeNode = g2ToG2Copy.getImage(tgtNode);
                        EClass subType = rule.getMappings()
                                .getImage(n, rule.getRhs()).getType();
                        if (tgtObj.eClass() != subType
                                && tgtObj.eClass().isSuperTypeOf(subType)) {
                            subTypeNode.setType(subType);
                        }
                    }
                }
                HenshinEGraph g2CopyEGraph = new HenshinEGraph(g2Copy);
                Match newMatch = transposeMatch(match, g2EGraph, g2CopyEGraph,
                        g2ToG2Copy);
                RuleApplication ruleApp = IF.createRuleApplication(hengine);
                ruleApp.setUnit(rule);
                ruleApp.setEGraph(g2CopyEGraph);
                ruleApp.setCompleteMatch(newMatch);

                boolean success = false;
                try {
                    success = ruleApp.execute(null);
                } catch (Exception ex) {
                    // could not execute so there must be typing issues with the
                    // overlap
                }

                if (success) {
                    Match resultMatch = ruleApp.getResultMatch();

                    boolean typesInvalid = !checkTypes(resultMatch);
                    boolean resultMatchValid = resultMatch.isValid();

                    // If the match is invalid because of type incompatibility,
                    // then keep the match. We'll fix the types later.
                    if (enforceEMFConstraints && !resultMatchValid
                            && !typesInvalid) {
                        // When the rule creates containment edges, this may
                        // cause other containment edges to disappear (e.g. if
                        // the target node was already contained by an existing
                        // edge). This typically indicates condition graphs that
                        // could never match in actual graphs.
                        // TODO we may want to eliminate such overlaps
                        LOGGER.trace("Discarded!");
                        continue;
                    }

                    if (lhsMatchInvalid && resultMatchValid) {
                        throw new IllegalArgumentException(
                                "Match was invalid. How can the co-match be valid?");
                    }

                    Morphism coMatch = new Morphism(resultMatch, g2CopyEGraph);

                    Graph overlapGraph = g2Copy;
                    overlapGraph.setName(String.format("Ov(%s, %s)[%d]",
                            inclusion.getTarget().getName(), g2.getName(),
                            overlapCounter.intValue()));

                    Morphism g1ToOverlap = targetToRhs.compose(coMatch);
                    Morphism g2ToOverlap = g2ToG2Copy;
                    Pair<Morphism, Morphism> overlap = Pair.with(g1ToOverlap,
                            g2ToOverlap);

                    removeNonMappedEdges(overlap);
                    assignNamesInOverlapGraph(overlap);

                    // Check if we have violated the injectivity of conditions
                    // .<--b--..
                    // |.......|
                    // ap......a
                    // \/......\/
                    // .<--bp--.
                    if (anchor != null) {
                        Morphism a = this.anchor.getValue0(), b = this.anchor
                                .getValue1(), bp = g1ToOverlap, ap = g2ToOverlap;
                        Set<GraphElement> image1 = bp.getImage(a
                                .getUnmappedTargetElements());
                        Set<GraphElement> image2 = ap.getImage(b.getImage(a
                                .getUnmappedSourceElements()));
                        if (!Sets.intersection(image1, image2).isEmpty()) {
                            // discard
                            Morphism.dispose(coMatch, g1ToOverlap, g2ToOverlap);
                            LOGGER.trace("Discarded!");
                            continue;
                        }
                    }

                    // This morphism may contain wrongfully mapped edges because
                    // Henshin assumes that when a source and target node are
                    // mapped by the morphism, any edges of the same type
                    // between them are also mapped whereas this may not be the
                    // case in the inclusion morphism.
                    Set<Edge> overlappedEdges = Sets.intersection(
                            g1ToOverlap.getCodomainEdges(),
                            g2ToOverlap.getCodomainEdges());
                    Set<Edge> superfluousEdgesInOverlap = Sets.intersection(
                            overlappedEdges, g1ToOverlap
                                    .getImage(originalInclusion
                                            .getUnmappedTargetEdges()));

                    if (superfluousEdgesInOverlap.size() > 0) {
                        if (preventEdgeAutoMapping) {
                            for (Edge edge : superfluousEdgesInOverlap) {
                                // Remove the existing mapping
                                Edge prevOrigin = g1ToOverlap.getOrigin(edge);
                                g1ToOverlap.remove(prevOrigin, edge);
                                // Create a new similar image edge and map to it
                                Edge newImage = HenshinFactory.eINSTANCE
                                        .createEdge(edge.getSource(),
                                                edge.getTarget(),
                                                edge.getType());
                                g1ToOverlap.add(prevOrigin, newImage);
                            }
                        } else if (discardEdgeAutoMapping) {
                            ruleApp.undo(null);
                            Morphism.dispose(coMatch, g1ToOverlap, g2ToOverlap);
                            LOGGER.trace("Discarded!");
                            continue;
                        }
                    }

                    // This morphism may be lacking edges due to the rule
                    // application (i.e. setting a containment EReference to an
                    // already contained object causes the old EReference to
                    // disappear.
                    List<Edge> unmappedEdges = g2ToOverlap
                            .getUnmappedSourceEdges();
                    if (enforceEMFConstraints && unmappedEdges.size() > 0) {
                        Morphism.dispose(coMatch, g1ToOverlap, g2ToOverlap);
                        LOGGER.trace("Discarded!");
                        continue;
                    } else {
                        for (Edge edge : unmappedEdges) {
                            EReference eRef = edge.getType();
                            Edge newImage = HenshinFactory.eINSTANCE
                                    .createEdge(g2ToOverlap.getImage(edge
                                            .getSource()), g2ToOverlap
                                            .getImage(edge.getTarget()), eRef);
                            g2ToOverlap.add(edge, newImage);
                        }
                    }

                    LOGGER.trace("Kept!");
                    nextOverlap = overlap;
                    overlapCounter.incrementAndGet();

                    Morphism.dispose(coMatch);
                }
            }
            if (nextOverlap == null) {
                LOGGER.trace("Found no overlaps");
            }
        }

        private static Match transposeMatch(Match oldMatch,
                HenshinEGraph oldEGraph, HenshinEGraph newEGraph,
                Morphism copyMorphism) {
            Rule rule = oldMatch.getRule();
            Match newMatch = IF.createMatch(rule, false);
            for (Node lhsNode : rule.getLhs().getNodes()) {
                EObject oldTarget = oldMatch.getNodeTarget(lhsNode);
                Node oldTgtNode = oldEGraph.getObject2NodeMap().get(oldTarget);
                Node newTgtNode = copyMorphism.getImage(oldTgtNode);
                EObject newTgtObj = newEGraph.getNode2ObjectMap().get(
                        newTgtNode);
                newMatch.setNodeTarget(lhsNode, newTgtObj);
            }
            return newMatch;
        }

        private boolean checkTypes(Match match) {
            Graph g = match.isResult() ? match.getRule().getRhs() : match
                    .getRule().getLhs();
            for (Node rhsNode : g.getNodes()) {
                EClass type = rhsNode.getType();
                EObject nodeTarget = match.getNodeTarget(rhsNode);
                EClass targetType = nodeTarget.eClass();
                if (type != targetType && !type.isSuperTypeOf(targetType)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public Pair<Morphism, Morphism> next() {
            if (hasNext()) {
                computedNextOverlap = false;
            }
            return nextOverlap;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void dispose() {
            Morphism.dispose(sourceToLhs, targetToRhs);
            HenshinUtils.dispose(g2EGraph);
        }

    }

    public final class SubSetIteratorSmallerFirst<T> extends SubSetIterator<T> {

        public SubSetIteratorSmallerFirst(Set<T> set, Set<T> forcedSubSet,
                boolean excludeEmptySubSet) {
            super(set, forcedSubSet, excludeEmptySubSet);
        }

        @Override
        protected void computeNextSubSet() {
            nextSubSet = null;

            if (tail == null) {
                // no more subgraphs
                return;
            }

            if (head == null) {
                nextSubSet = Sets.newLinkedHashSet(forcedSubSet);
                tail = null;
                return;
            }

            if (currentTailSubSet == null) {
                if (tailSubSets.hasNext()) {
                    Set<T> tailSubSet = (Set<T>) tailSubSets.next();
                    nextSubSet = Sets.newLinkedHashSet(tailSubSet);
                    nextSubSet.addAll(forcedSubSet);
                    currentTailSubSet = tailSubSet;
                    if (excludeEmptySubSet && nextSubSet.isEmpty()) {
                        nextSubSet = null;
                    } else {
                        return;
                    }
                } else {
                    // no more subgraphs
                    return;
                }
            }

            if (currentTailSubSet != null) {
                Set<T> newSubSet = Sets.newLinkedHashSet();
                newSubSet.add(head);
                newSubSet.addAll(currentTailSubSet);
                newSubSet.addAll(forcedSubSet);
                currentTailSubSet = null;
                nextSubSet = newSubSet;
            }
        }

    }

    /**
     * When the overlap graph is created by applying a rule, additional unwanted
     * edges may be added due to EOpposite references. This method cleans them
     * up by removing all edges not mapped to any of the original graphs.
     * 
     * @param overlap
     */
    private static void removeNonMappedEdges(Pair<Morphism, Morphism> overlap) {
        Morphism m1 = overlap.getValue0();
        Morphism m2 = overlap.getValue1();

        List<Edge> unmappedTargetEdges = m1.getUnmappedTargetEdges();
        EList<Edge> targetEdges = m1.getTarget().getEdges();
        for (Edge edge : unmappedTargetEdges) {
            if (m2.getOrigin(edge) == null) {
                edge.setSource(null);
                edge.setTarget(null);
                targetEdges.remove(edge);
            }
        }
    }

    private static void assignNamesInOverlapGraph(
            Pair<Morphism, Morphism> overlap) {
        if (!Post2Pre.KEEP_NAMES) {
            return;
        }

        Morphism m1 = overlap.getValue0();
        Morphism m2 = overlap.getValue1();
        Graph overlapGraph = m1.getTarget();

        for (Node node : overlapGraph.getNodes()) {
            Node o1 = m1.getOrigin(node);
            Node o2 = m2.getOrigin(node);

            if (o1 != null && o2 != null) {
                node.setName(String.format("%s,%s", o1.getName(), o2.getName()));
            } else if (o1 != null) {
                node.setName(o1.getName());
            } else if (o2 != null) {
                node.setName(o2.getName());
            }
        }
    }

    public static Iterable<Morphism> enumerateAllSubGraphs(Graph graph) {
        return enumerateAllSubGraphs(graph, null);
    }

    public static Iterable<Pair<Morphism, Morphism>> enumerateOverlaps(
            Graph g1, Graph g2, boolean excludeEmptyOverlap,
            boolean enforceEMFConstraints, boolean fixEdgeAutoMapping) {
        return enumerateOverlaps(g1, g2, null, excludeEmptyOverlap,
                enforceEMFConstraints, fixEdgeAutoMapping);
    }

}
