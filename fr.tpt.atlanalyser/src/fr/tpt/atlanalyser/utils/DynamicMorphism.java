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

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.henshin.interpreter.Match;
import org.eclipse.emf.henshin.interpreter.util.HenshinEGraph;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinPackage;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;

public class DynamicMorphism extends Morphism implements Adapter {

    private static final long serialVersionUID = 1L;

    public DynamicMorphism(Graph source, Graph target) {
        super(source, target);
        this.source.eAdapters().add(this);
        this.target.eAdapters().add(this);
    }

    public DynamicMorphism(Match match, HenshinEGraph eGraph) {
        super(match, eGraph);
        this.source.eAdapters().add(this);
        this.target.eAdapters().add(this);
    }

    public DynamicMorphism(NestedCondition cdt) {
        super(cdt);
        this.source.eAdapters().add(this);
        this.target.eAdapters().add(this);
    }

    public DynamicMorphism(NestedCondition cdt, Graph host) {
        super(cdt, host);
        this.source.eAdapters().add(this);
        this.target.eAdapters().add(this);
    }

    public DynamicMorphism(Rule rule) {
        super(rule);
        this.source.eAdapters().add(this);
        this.target.eAdapters().add(this);
    }

    public DynamicMorphism(Morphism m) {
        super(m);
        this.source.eAdapters().add(this);
        this.target.eAdapters().add(this);
    }

    @Override
    public void notifyChanged(Notification notification) {
        int eventType = notification.getEventType();
        if (eventType == Notification.REMOVE
                || eventType == Notification.REMOVE_MANY) {
            Graph graph = (Graph) notification.getNotifier();
            // TODO: if an edge is removed from origin or target graphs, update
            // edgeMap accordingly
            Object feature = notification.getFeature();
            Object oldValue = notification.getOldValue();
            Object newValue = notification.getNewValue();
            if (feature == HenshinPackage.Literals.GRAPH__EDGES) {
                if (eventType == Notification.REMOVE) {
                    if (oldValue != null && newValue != oldValue) {
                        if (oldValue instanceof Edge) {
                            if (graph == getSource()) {
                                edgeMap.remove(oldValue);
                            } else {
                                Set<Entry<Edge, Edge>> entrySet = edgeMap
                                        .entrySet();
                                for (Iterator<Entry<Edge, Edge>> iterator = entrySet
                                        .iterator(); iterator.hasNext();) {
                                    Entry<Edge, Edge> entry = iterator.next();
                                    if (entry.getValue() == oldValue) {
                                        iterator.remove();
                                    }

                                }
                            }
                        }
                    }
                } else {
                    @SuppressWarnings("unchecked")
                    List<Edge> edgesToRemove = (List<Edge>) oldValue;
                    if (graph == getSource()) {
                        for (Edge edge : edgesToRemove) {
                            edgeMap.remove(edge);
                        }
                    } else {
                        Set<Entry<Edge, Edge>> entrySet = edgeMap.entrySet();
                        for (Iterator<Entry<Edge, Edge>> iterator = entrySet
                                .iterator(); iterator.hasNext();) {
                            Entry<Edge, Edge> entry = iterator.next();
                            if (edgesToRemove.contains(entry.getValue())) {
                                iterator.remove();
                            }
                        }
                    }
                }
            } else if (feature == HenshinPackage.Literals.GRAPH__NODES) {
                if (eventType == Notification.REMOVE) {
                    if (graph == getSource()) {
                        remove((Node) oldValue, getImage((Node) oldValue));
                    } else {
                        remove(getOrigin((Node) oldValue), (Node) oldValue);
                    }
                } else {
                    @SuppressWarnings("unchecked")
                    List<Node> nodesToRemove = (List<Node>) oldValue;
                    if (graph == getSource()) {
                        for (Node node : nodesToRemove) {
                            remove(node, getImage(node));
                        }
                    } else {
                        for (Node node : nodesToRemove) {
                            remove(getOrigin(node), node);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setTarget(Notifier newTarget) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isAdapterForType(Object type) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void dispose() {
        super.dispose();
        // synchronized (source) {
        source.eAdapters().remove(this);
        // }
        // synchronized (target) {
        target.eAdapters().remove(this);
        // }
    }

    @Override
    protected void finalize() throws Throwable {
        this.dispose();
        super.finalize();
    }

}
