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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.interpreter.Match;
import org.eclipse.emf.henshin.interpreter.util.HenshinEGraph;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.GraphElement;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.impl.MappingListImpl;
import org.javatuples.Pair;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class Morphism extends MappingListImpl {

    private static final long serialVersionUID = 1L;
    protected Graph           source;
    protected Graph           target;
    protected Map<Edge, Edge> edgeMap;

    public Morphism(Graph source, Graph target) {
        this.source = source;
        this.target = target;
        edgeMap = new LinkedHashMap<Edge, Edge>();
    }

    public Morphism(Match match, HenshinEGraph eGraph) {
        if (match.isResult()) {
            source = ((Rule) match.getUnit()).getRhs();
        } else {
            source = ((Rule) match.getUnit()).getLhs();
        }
        target = eGraph.getHenshinGraph();
        edgeMap = new LinkedHashMap<Edge, Edge>();
        add(match, eGraph);
    }

    public Morphism(NestedCondition cdt) {
        this(cdt, cdt.getHost());
    }

    public Morphism(Morphism m) {
        this(m.source, m.target);
        addAll(EcoreUtil.copyAll(m));
        edgeMap = new LinkedHashMap<Edge, Edge>(m.edgeMap);
    }

    /**
     * Sometimes the NestedCondition does not yet have a host. This forces a
     * host through the parameter 'host'.
     * 
     * @param cdt
     * @param host
     */
    public Morphism(NestedCondition cdt, Graph host) {
        this(host, cdt.getConclusion());
        for (Mapping m : cdt.getMappings()) {
            add(m.getOrigin(), m.getImage());
        }
        for (Edge e : host.getEdges()) {
            Edge image = cdt.getMappings().getImage(e, cdt.getConclusion());
            if (image != null) {
                add(e, image);
            }
        }
    }

    public Morphism(Rule rule) {
        this(rule.getLhs(), rule.getRhs());
        edgeMap = new LinkedHashMap<Edge, Edge>();
        for (Mapping m : rule.getMappings()) {
            add(m.getOrigin(), m.getImage());
        }
        for (Edge e : this.source.getEdges()) {
            Edge image = rule.getMappings().getImage(e, target);
            if (image != null) {
                add(e, image);
            }
        }
    }

    public Morphism(HenshinEGraph eg1, HenshinEGraph eg2,
            Map<EObject, EObject> isomorphMap) {
        this(eg1.getHenshinGraph(), eg2.getHenshinGraph());

        Map<Node, Node> nodeMap = Maps.newHashMap();
        for (Map.Entry<EObject, EObject> e : isomorphMap.entrySet()) {
            nodeMap.put(eg1.getObject2NodeMap().get(e.getKey()), eg2
                    .getObject2NodeMap().get(e.getValue()));
        }

        this.add(nodeMap);
    }

    public Graph getSource() {
        return source;
    }

    public Graph getTarget() {
        return target;
    }

    @Override
    public void clear() {
        super.clear();
        edgeMap.clear();
    }

    @Override
    public <E extends GraphElement> void add(E origin, E image) {
        if (origin instanceof Edge) {
            this.add((Edge) origin, (Edge) image);
        } else {
            super.add(origin, image);
        }
    }

    @Override
    public void add(Edge origin, Edge image) {
        super.add(origin, image);
        if (getImage(origin.getSource()) == image.getSource()
                && getImage(origin.getTarget()) == image.getTarget()) {
            edgeMap.put(origin, image);
        } else {
            throw new IllegalArgumentException(
                    "Edge mapping does not comply with source and target node mapping");
        }
    }

    @Override
    public Edge getImage(Edge origin, Graph imageGraph) {
        return edgeMap.get(origin);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends GraphElement> E getImage(E origin, Graph imageGraph) {
        if (origin instanceof Edge) {
            Edge res = getImage((Edge) origin, imageGraph);
            return (E) res;
        } else {
            return super.getImage(origin, imageGraph);
        }
    }

    @Override
    public Edge getOrigin(Edge image) {
        for (Entry<Edge, Edge> mapping : edgeMap.entrySet()) {
            if (mapping.getValue() == image) {
                return mapping.getKey();
            }
        }
        return null;
    }

    @Override
    public void remove(Edge origin, Edge image) {
        edgeMap.remove(origin);
        // do not call super because that would cause to unmap source and target
        // nodes.
        // super.remove(origin, image);
    }

    @Override
    public <E extends GraphElement> void remove(E origin, E image) {
        if (origin instanceof Edge) {
            this.remove((Edge) origin, (Edge) image);
        } else {
            super.remove(origin, image);
        }
    }

    @Override
    protected Mapping validate(int index, Mapping object) {
        object = super.validate(index, object);
        Node origin = object.getOrigin();
        Node image = object.getImage();
        if (origin.getGraph() != source || image.getGraph() != target) {
            throw new IllegalArgumentException(
                    "Source or target graph of mapping is different from the morphism's");
        }
        return object;
    }

    /**
     * @return true if all distinct origins have distinct images.
     */
    public boolean isInjective() {
        for (int i = 0; i < this.size(); i++) {
            for (int j = i + 1; j < this.size(); j++) {
                Mapping m1 = this.get(i);
                Mapping m2 = this.get(j);
                if (m1.getOrigin() != m2.getOrigin()
                        && m1.getImage() == m2.getImage()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * @return true if all elements in the target graph have origin elements.
     */
    public boolean isSurjective() {
        List<GraphElement> targetGraphElems = new ArrayList<GraphElement>();
        targetGraphElems.addAll(target.getNodes());
        targetGraphElems.addAll(target.getEdges());

        for (GraphElement tgtElement : targetGraphElems) {
            GraphElement origin = this.getOrigin(tgtElement);
            if (origin == null) {
                return false;
            }
        }

        return true;
    }

    public Morphism compose(Morphism next) {
        if (this.getTarget() != next.getSource()) {
            throw new IllegalArgumentException(
                    "Next morphism source graph must be this morphism's target graph.");
        }

        Morphism res = new Morphism(this.getSource(), next.getTarget());

        for (Mapping mapping : this) {
            Node image = next.getImage(mapping.getImage());
            if (image != null) {
                res.add(mapping.getOrigin(), image);
            }
        }

        for (Entry<Edge, Edge> entry : edgeMap.entrySet()) {
            Edge orig = entry.getKey();
            Edge inter = entry.getValue();
            Edge image = next.getImage(inter);
            if (image != null) {
                res.add(orig, image);
            }
        }

        return res;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();

        res.append(getSource().getName() + " ==> " + getTarget().getName()
                + "\n");

        Iterator<GraphElement> unmappedSource = getUnmappedSourceElements()
                .iterator();
        Iterator<GraphElement> unmappedTarget = getUnmappedTargetElements()
                .iterator();

        while (unmappedSource.hasNext() || unmappedTarget.hasNext()) {
            String src = unmappedSource.hasNext() ? unmappedSource.next()
                    .toString() : "   ";
            String tgt = unmappedTarget.hasNext() ? unmappedTarget.next()
                    .toString() : "";

            res.append(String.format("%s |x| %s\n", src, tgt));
        }

        String nodeMappings = Joiner.on("\n").join(this);
        String edgeMappings = Joiner.on("\n").join(
                Collections2.transform(edgeMap.entrySet(),
                        new Function<Entry<Edge, Edge>, String>() {

                            @Override
                            public String apply(Entry<Edge, Edge> input) {
                                return input.getKey() + "   -->   "
                                        + input.getValue();
                            }
                        }));

        res.append(Joiner.on("\n").join(nodeMappings, edgeMappings));

        return res.toString().trim();
    }

    public <T extends GraphElement> T getImage(T source) {
        return getImage(source, target);
    }

    public void add(Map<?, ?> map) {
        for (GraphElement e : Iterables.concat(getSource().getNodes(),
                getSource().getEdges())) {
            GraphElement image = (GraphElement) map.get(e);
            if (image != null) {
                add(e, image);
            }
        }
    }

    public void add(Match match, HenshinEGraph eGraph) {
        Map<EObject, Node> objToNodeMap = eGraph.getObject2NodeMap();
        for (Node node : getSource().getNodes()) {
            EObject targetObj = match.getNodeTarget(node);
            if (targetObj != null) {
                Node targetNode = objToNodeMap.get(targetObj);
                add(node, targetNode);
                if (targetNode.getName() == null) {
                    targetNode.setName(node.getName());
                }
            }
        }

        for (Edge edge : getSource().getEdges()) {
            // get edge image based on node mappings which is what
            // super.getImage() does since this.getImage() relies on edgeMap.
            Edge image = super.getImage(edge, getTarget());
            add(edge, image);
        }
    }

    public List<Node> getUnmappedSourceNodes() {
        ArrayList<Node> result = new ArrayList<Node>(getSource().getNodes());
        result.removeAll(Collections2.transform(this,
                new Function<Mapping, Node>() {

                    @Override
                    public Node apply(Mapping input) {
                        return input.getOrigin();
                    }
                }));

        return result;
    }

    public List<Node> getUnmappedTargetNodes() {
        ArrayList<Node> result = new ArrayList<Node>(getTarget().getNodes());
        List<Node> images = Lists.newLinkedList(Collections2.transform(this,
                new Function<Mapping, Node>() {

                    @Override
                    public Node apply(Mapping input) {
                        return input.getImage();
                    }
                }));
        result.removeAll(images);

        return result;
    }

    public List<Edge> getUnmappedSourceEdges() {
        ArrayList<Edge> result = new ArrayList<Edge>(getSource().getEdges());
        result.removeAll(this.edgeMap.keySet());
        return result;
    }

    public List<Edge> getUnmappedTargetEdges() {
        ArrayList<Edge> result = new ArrayList<Edge>(getTarget().getEdges());
        result.removeAll(this.edgeMap.values());
        return result;
    }

    public List<GraphElement> getUnmappedSourceElements() {
        List<GraphElement> result = new ArrayList<GraphElement>();
        result.addAll(getUnmappedSourceNodes());
        result.addAll(getUnmappedSourceEdges());
        return result;
    }

    public List<GraphElement> getUnmappedTargetElements() {
        List<GraphElement> result = new ArrayList<GraphElement>();
        result.addAll(getUnmappedTargetNodes());
        result.addAll(getUnmappedTargetEdges());
        return result;
    }

    public List<GraphElement> getDomain() {
        List<GraphElement> result = new ArrayList<GraphElement>();
        for (Mapping m : this) {
            result.add(m.getOrigin());
        }
        result.addAll(edgeMap.keySet());
        return result;
    }

    public Set<Edge> getDomainEdges() {
        return edgeMap.keySet();
    }

    public List<GraphElement> getCodomain() {
        List<GraphElement> result = new ArrayList<GraphElement>();
        for (Mapping m : this) {
            result.add(m.getImage());
        }
        result.addAll(Sets.newHashSet(edgeMap.values()));
        return result;
    }

    public Set<Node> getCodomainNodes() {
        return Sets.newHashSet(Iterables.transform(this,
                new Function<Mapping, Node>() {
                    @Override
                    public Node apply(Mapping input) {
                        return input.getImage();
                    }
                }));
    }

    public Set<Edge> getCodomainEdges() {
        return Collections.unmodifiableSet(Sets.newHashSet(edgeMap.values()));
    }

    public <T extends GraphElement> Set<T> getImage(Collection<T> origin) {
        Set<T> res = Sets.newLinkedHashSet();
        for (T e : origin) {
            T image = getImage(e);
            if (image != null) {
                res.add(image);
            }
        }
        return res;
    }

    public <T extends GraphElement> Set<T> getOrigin(Collection<T> image) {
        Set<T> res = Sets.newLinkedHashSet();
        for (T e : image) {
            T origin = getOrigin(e);
            if (origin != null) {
                res.add(origin);
            }
        }
        return res;
    }

    /**
     * A --a--> B |........| b1.......b |........| \/.......\/ C --a1-> D
     * 
     * @param a
     * @param b
     * @return a pair of morphisms {b1, a1} forming the pushout complement, or
     *         null if cannot compute the complement
     */
    public static Pair<Morphism, Morphism> getPushoutComplement(Morphism a,
            Morphism b) {
        Graph A = a.getSource(), D = b.getTarget();
        Morphism a1 = GraphCopier.reverseCopy(D);
        Graph C = a1.getSource();
        C.setName("C");
        Morphism b1 = new Morphism(A, C);

        Pair<Morphism, Morphism> res = new Pair<Morphism, Morphism>(b1, a1);

        // Map in b1 the nodes from A which are mapped to D by b(a(A))
        Morphism aThenB = a.compose(b);
        List<GraphElement> domain = aThenB.getDomain();
        for (GraphElement nA : domain) {
            GraphElement nD = aThenB.getImage(nA);
            GraphElement nC = a1.getOrigin(nD);
            b1.add(nA, nC);
        }

        List<GraphElement> unAnchoredElements = a.getUnmappedTargetElements();
        Set<GraphElement> toDelete = a1.getOrigin(b
                .getImage(unAnchoredElements));
        for (GraphElement el : toDelete) {
            a1.remove(el, a1.getImage(el));
            EcoreUtil.delete(el);
        }

        // If C contains dangling edges, then we haven't been able to construct
        // a complement
        for (Edge edge : C.getEdges()) {
            if (edge.getSource() == null || edge.getTarget() == null) {
                a1.dispose();
                b1.dispose();
                return null;
            }
        }

        return res;
    }

    public Morphism proxify() {
        Morphism res = new Morphism(this);

        for (Mapping m : res) {
            m.setOrigin(Utils.createProxy(m.getOrigin()));
            m.setImage(Utils.createProxy(m.getImage()));
        }

        for (Entry<Edge, Edge> entry : Sets.newHashSet(res.edgeMap.entrySet())) {
            Edge value = entry.getValue();
            Edge key = entry.getKey();
            res.edgeMap.remove(key, value);
            Edge pKey = Utils.createProxy(key);
            Edge pValue = Utils.createProxy(value);
            res.edgeMap.put(pKey, pValue);
        }

        Graph sProxy = Utils.createProxy(source);
        res.source = sProxy;
        Graph tProxy = Utils.createProxy(target);
        res.target = tProxy;

        return res;
    }

    public Morphism resolve(Resource srcRes, Resource tgtRes) {
        Graph s = (Graph) Utils.resolve(source, srcRes);
        assert !s.eIsProxy();
        Graph t = (Graph) Utils.resolve(target, tgtRes);
        assert !t.eIsProxy();

        Morphism res = new Morphism(s, t);

        for (Mapping m : this) {
            res.add((Node) Utils.resolve(m.getOrigin(), srcRes),
                    (Node) Utils.resolve(m.getImage(), tgtRes));
        }

        for (Entry<Edge, Edge> entry : edgeMap.entrySet()) {
            Edge key = entry.getKey();
            Edge value = entry.getValue();
            Edge rKey = (Edge) Utils.resolve(key, srcRes);
            assert !rKey.eIsProxy();
            Edge rValue = (Edge) Utils.resolve(value, tgtRes);
            assert !rValue.eIsProxy();
            res.add(rKey, rValue);
        }

        return res;
    }

    public void dispose() {
        clear();
    }

    public static void dispose(Morphism... morphisms) {
        for (Morphism morphism : morphisms) {
            if (morphism != null) {
                morphism.dispose();
            }
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Morphism) {
            Morphism other = (Morphism) object;
            if (this.source == other.source && this.target == other.target
                    && this.size == other.size) {
                for (Mapping m : this) {
                    if (other.get(m.getOrigin(), m.getImage()) == null) {
                        return false;
                    }
                }
                return true;
            }
        }
        return super.equals(object);
    }

}
