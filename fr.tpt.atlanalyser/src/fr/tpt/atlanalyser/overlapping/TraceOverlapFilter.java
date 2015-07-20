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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Node;
import org.javatuples.Pair;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import fr.tpt.atlanalyser.utils.Morphism;

public class TraceOverlapFilter extends
        IteratorFilter<Pair<Morphism, Morphism>> {

    private static final Logger LOGGER = LogManager
                                               .getLogger(TraceOverlapFilter.class
                                                       .getSimpleName());

    private EClass              traceEClass;
    private EReference          from;
    private EReference          to;

    private EClass              rootSrcEClass;

    private EClass              rootTgtEClass;

    public TraceOverlapFilter(Iterator<Pair<Morphism, Morphism>> subIterator,
            EClass traceEClass) {
        super(subIterator);
        this.traceEClass = traceEClass;
        this.from = (EReference) traceEClass.getEStructuralFeature("from");
        this.to = (EReference) traceEClass.getEStructuralFeature("to");
        this.rootSrcEClass = (EClass) from.getEType();
        this.rootTgtEClass = (EClass) to.getEType();
    }

    public static Iterable<Pair<Morphism, Morphism>> filter(
            final Iterable<Pair<Morphism, Morphism>> iterable,
            final EClass traceEClass) {
        return new Iterable<Pair<Morphism, Morphism>>() {
            @Override
            public Iterator<Pair<Morphism, Morphism>> iterator() {
                return new TraceOverlapFilter(iterable.iterator(), traceEClass);
            }
        };
    }

    @Override
    protected boolean isValid(Pair<Morphism, Morphism> nextCandidate) {
        Morphism p1 = nextCandidate.getValue0();
        Morphism p2 = nextCandidate.getValue1();
        Graph overlapGraph = p1.getTarget();

        Collection<Node> traceNodes = Collections2.filter(
                overlapGraph.getNodes(), new Predicate<Node>() {
                    @Override
                    public boolean apply(Node arg0) {
                        return traceEClass.isSuperTypeOf(arg0.getType());
                    }
                });

        for (Node trace : traceNodes) {
            Node orig1 = p1.getOrigin(trace);
            Node orig2 = p2.getOrigin(trace);
            EList<Edge> fromEdges = trace.getOutgoing(from);
            int fromCount = fromEdges.size();

            if (orig1 != null) {
                EList<Edge> outgoing1 = orig1.getOutgoing(from);
                int fromCountOrig1 = outgoing1.size();
                if (fromCountOrig1 != 0 && fromCountOrig1 != fromCount) {
                    LOGGER.trace("Discarding overlap due to incompatible trace 'from' edge count");
                    // LOGGER.info(String.format(
                    // "\norig1\n%s\n\nOverlap\n%s\n\norig2\n%s",
                    // HenshinGraphASCIIRenderer.render(p1.getSource()),
                    // HenshinGraphASCIIRenderer.render(overlapGraph),
                    // HenshinGraphASCIIRenderer.render(p2.getSource())));
                    return false;
                }
            }

            if (orig2 != null) {
                EList<Edge> outgoing2 = orig2.getOutgoing(from);
                int fromCountOrig2 = outgoing2.size();
                if (fromCountOrig2 != 0 && fromCountOrig2 != fromCount) {
                    LOGGER.trace("Discarding overlap due to incompatible trace 'from' edge count");
                    return false;
                }
            }

            // Check the 'from' tuple only if trace is an abstract trace,
            // because concrete traces may appear with only part of their from
            // tuple. Also, check only with other abstract traces and not
            // concrete ones.
            if (trace.getType() == traceEClass) {

                HashSet<Node> fromTuple = Sets
                        .newHashSet(getAbstractFromNodes(trace));
                for (Node fromNode : fromTuple) {
                    // if the trace node has a 'from' tuple, check that the
                    // tuple
                    // does not overlap with another trace node.
                    for (Edge edge : fromNode.getIncoming(from)) {
                        if (edge.getSource() == trace) {
                            continue;
                        }

                        Node otherTraceNode = edge.getSource();
                        if (otherTraceNode.getType() == traceEClass) {
                            if (fromTuple
                                    .equals(Sets
                                            .newHashSet(getAbstractFromNodes(otherTraceNode)))) {
                                LOGGER.trace("Discarding overlap due to overlapping 'from' tuples");
                                return false;
                            }
                        }
                    }
                }
                // List<Node> concreteFromNodes = getConcreteFromNodes(trace);
                // if (!fromTuple.isEmpty()
                // && !fromTuple.containsAll(concreteFromNodes)) {
                // LOGGER.trace("Discarding overlap because a concrete 'from' node is not in the abstract 'from' tuple");
                // return false;
                // }
            }
        }

        return true;
    }

    private void initTraceMM(EObject obj) {
        EPackage traceMM = obj.eResource().getResourceSet()
                .getPackageRegistry().getEPackage("http://traces/1.0");
    }

    private List<Node> getAbstractFromNodes(Node trace) {
        return Lists.transform(trace.getOutgoing(from),
                new Function<Edge, Node>() {
                    @Override
                    public Node apply(Edge arg0) {
                        return arg0.getTarget();
                    }
                });
    }

    private List<Node> getAbstractToNodes(Node trace) {
        return Lists.transform(trace.getOutgoing(to),
                new Function<Edge, Node>() {
                    @Override
                    public Node apply(Edge arg0) {
                        return arg0.getTarget();
                    }
                });
    }

    private List<Node> getConcreteFromNodes(Node trace) {
        ArrayList<Edge> concreteFromEdges = Lists.newArrayList(Collections2
                .filter(trace.getOutgoing(), new Predicate<Edge>() {
                    @Override
                    public boolean apply(Edge arg0) {
                        return arg0.getType() != from
                                && rootSrcEClass.isSuperTypeOf((EClass) arg0
                                        .getType().getEType());
                    }
                }));
        return Lists.transform(concreteFromEdges, new Function<Edge, Node>() {
            @Override
            public Node apply(Edge arg0) {
                return arg0.getTarget();
            }
        });
    }

    private List<Node> getConcreteToNodes(Node trace) {
        ArrayList<Edge> concreteToEdges = Lists.newArrayList(Collections2
                .filter(trace.getOutgoing(), new Predicate<Edge>() {
                    @Override
                    public boolean apply(Edge arg0) {
                        return arg0.getType() != to
                                && rootTgtEClass.isSuperTypeOf((EClass) arg0
                                        .getType().getEType());
                    }
                }));
        return Lists.transform(concreteToEdges, new Function<Edge, Node>() {
            @Override
            public Node apply(Edge arg0) {
                return arg0.getTarget();
            }
        });
    }

}
