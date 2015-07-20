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
package fr.tpt.atlanalyser.post2pre.ncvalidators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;
import org.javatuples.Pair;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import fr.tpt.atlanalyser.utils.Morphism;

public class ATLSemanticsValidator implements NestedConditionValidator {

    private EClass                            traceEClass;
    private EClass                            rootSrcType;
    private EClass                            rootTgtType;
    private EReference                        from;
    private EReference                        to;
    private static final Function<Edge, Node> edgeToTarget;
    private static final Comparator<Edge>     edgeIndexComparator;

    static {
        edgeToTarget = new Function<Edge, Node>() {
            @Override
            public Node apply(Edge arg0) {
                return arg0.getTarget();
            }
        };
        edgeIndexComparator = new Comparator<Edge>() {
            @Override
            public int compare(Edge o1, Edge o2) {
                String idx1 = o1.getIndex();
                String idx2 = o2.getIndex();
                if (idx1 != null && idx2 != null) {
                    try {
                        int i1 = Integer.parseInt(idx1);
                        int i2 = Integer.parseInt(idx2);
                        return Integer.compare(i1, i2);
                    } catch (NumberFormatException ex) {
                        return 0;
                    }
                } else {
                    return 0;
                }
            }
        };
    }

    public ATLSemanticsValidator(EClass traceEClass) {
        this.traceEClass = traceEClass;
        from = (EReference) traceEClass.getEStructuralFeature("from");
        rootSrcType = (EClass) from.getEType();
        to = (EReference) traceEClass.getEStructuralFeature("to");
        rootTgtType = (EClass) to.getEType();
    }

    public boolean isTraceValid(Pair<Morphism, Morphism> overlap) {
        // TODO: deactivate trace validation because it is wrong in some
        // situations
        if (true)
            return true;
        Morphism g1ToOverlap = overlap.getValue0();
        Morphism g2ToOverlap = overlap.getValue1();
        Graph ovGraph = g1ToOverlap.getTarget();

        List<Node> abstractTraceNodes = getAbstractTraceNodes(ovGraph);

        for (Node trace : abstractTraceNodes) {
            Node tr1 = g1ToOverlap.getOrigin(trace);
            Node tr2 = g2ToOverlap.getOrigin(trace);

            int nFrom = getAbstractFromNodes(trace).size();
            if (tr1 != null) {
                if (getAbstractFromNodes(tr1).size() != nFrom) {
                    return false;
                }
            }

            if (tr2 != null) {
                if (getAbstractFromNodes(tr2).size() != nFrom) {
                    return false;
                }
            }

        }

        return true;
    }

    @Override
    public boolean isValid(NestedCondition nc) {
        Graph conclusion = nc.getConclusion();

        Graph overlapGraph = conclusion;

        List<Node> traceNodes = getTraceNodes(overlapGraph);

        for (int i = 0; i < traceNodes.size(); i++) {
            Node t1 = traceNodes.get(i);
            for (int j = i + 1; j < traceNodes.size(); j++) {
                Node t2 = traceNodes.get(j);
                if (t1 == t2) {
                    continue;
                }

                // an object cannot be created by different traces
                List<Node> to1 = getAbstractToNodes(t1);
                List<Node> to2 = getAbstractToNodes(t2);
                for (Node node : to1) {
                    if (to2.contains(node)) {
                        return false;
                    }
                }

                // 2 abstract traces cannot have identical from tuples
                if (t1.getType() == traceEClass && t2.getType() == traceEClass) {
                    List<Node> from1 = getAbstractFromNodes(t1);
                    List<Node> from2 = getAbstractFromNodes(t2);
                    if (from1.equals(from2)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private List<Node> getTraceNodes(Graph graph) {
        return Lists.newArrayList(Collections2.filter(graph.getNodes(),
                new Predicate<Node>() {
                    @Override
                    public boolean apply(Node arg0) {
                        return traceEClass.isSuperTypeOf(arg0.getType());
                    }
                }));
    }

    private List<Node> getAbstractTraceNodes(Graph graph) {
        return graph.getNodes(traceEClass);
    }

    private List<Node> getAbstractFromNodes(Node trace) {
        ArrayList<Edge> fromTuple = Lists.newArrayList(trace.getOutgoing(from));
        Collections.sort(fromTuple, edgeIndexComparator);
        return Lists.newArrayList(Lists.transform(fromTuple, edgeToTarget));
    }

    private List<Node> getAbstractToNodes(Node trace) {
        ArrayList<Edge> toTuple = Lists.newArrayList(trace.getOutgoing(to));
        Collections.sort(toTuple, edgeIndexComparator);
        return Lists.newArrayList(Lists.transform(toTuple, edgeToTarget));
    }

    private List<Node> getConcreteFromNodes(Node trace) {
        ArrayList<Edge> concreteFromEdges = Lists.newArrayList(Collections2
                .filter(trace.getOutgoing(), new Predicate<Edge>() {
                    @Override
                    public boolean apply(Edge arg0) {
                        return arg0.getType() != from
                                && rootSrcType.isSuperTypeOf((EClass) arg0
                                        .getType().getEType());
                    }
                }));
        Collections.sort(concreteFromEdges, edgeIndexComparator);
        return Lists.transform(concreteFromEdges, edgeToTarget);
    }

    private List<Node> getConcreteToNodes(Node trace) {
        ArrayList<Edge> concreteToEdges = Lists.newArrayList(Collections2
                .filter(trace.getOutgoing(), new Predicate<Edge>() {
                    @Override
                    public boolean apply(Edge arg0) {
                        return arg0.getType() != to
                                && rootTgtType.isSuperTypeOf((EClass) arg0
                                        .getType().getEType());
                    }
                }));
        return Lists.transform(concreteToEdges, edgeToTarget);
    }

}
