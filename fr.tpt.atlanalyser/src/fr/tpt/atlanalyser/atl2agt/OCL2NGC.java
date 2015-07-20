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
package fr.tpt.atlanalyser.atl2agt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Annotation;
import org.eclipse.emf.henshin.model.Attribute;
import org.eclipse.emf.henshin.model.AttributeCondition;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.ModelElement;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Not;
import org.eclipse.emf.henshin.model.Parameter;
import org.eclipse.emf.henshin.model.Rule;
import org.javatuples.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import fr.tpt.atlanalyser.ATLAnalyserException;
import fr.tpt.atlanalyser.atl.ATL.LocatedElement;
import fr.tpt.atlanalyser.atl.OCL.BooleanExp;
import fr.tpt.atlanalyser.atl.OCL.CollectionOperationCallExp;
import fr.tpt.atlanalyser.atl.OCL.IfExp;
import fr.tpt.atlanalyser.atl.OCL.IteratorExp;
import fr.tpt.atlanalyser.atl.OCL.NavigationOrAttributeCallExp;
import fr.tpt.atlanalyser.atl.OCL.OclExpression;
import fr.tpt.atlanalyser.atl.OCL.OclModelElement;
import fr.tpt.atlanalyser.atl.OCL.OperationCallExp;
import fr.tpt.atlanalyser.atl.OCL.OperatorCallExp;
import fr.tpt.atlanalyser.atl.OCL.StringExp;
import fr.tpt.atlanalyser.atl.OCL.VariableExp;
import fr.tpt.atlanalyser.atl.OCL.util.OCLExpressionSimplePrinter;
import fr.tpt.atlanalyser.atl.OCL.util.OCLSwitch;
import fr.tpt.atlanalyser.overlapping.GraphOverlapper.GraphOverlapGenerator;
import fr.tpt.atlanalyser.post2pre.formulas.FormulaTransformer;
import fr.tpt.atlanalyser.utils.ErrorReporting;
import fr.tpt.atlanalyser.utils.GraphCopier;
import fr.tpt.atlanalyser.utils.HenshinUtils;
import fr.tpt.atlanalyser.utils.Morphism;
import fr.tpt.atlanalyser.utils.NGCUtils;

public class OCL2NGC extends OCLSwitch<Object> {

    private static final HenshinFactory HF           = HenshinFactory.eINSTANCE;
    private static final Logger         LOGGER       = LogManager
                                                             .getLogger(OCL2NGC.class
                                                                     .getSimpleName());
    private final Action                PRESERVE;
    private final HashMap<String, Node> varNameToNode;
    private HenshinFactory              henshinFactory;
    private ATL2Henshin                 atl2Henshin;
    private final Node                  NULL;
    List<ATLAnalyserException>          errors       = new ArrayList<ATLAnalyserException>();
    private Graph                       hostGraph;
    private NestedCondition             currentNC;
    private Formula                     currentFormula;
    static private int                  paramCounter = 1;
    private Graph                       conclusion;
    private EObject                     currentObj;
    private OCL2NGC                     parentOcl2Ngc;

    public OCL2NGC(ATL2Henshin atl2Henshin, Graph hostGraph,
            Map<String, Node> varNameToNode) {
        this.varNameToNode = new HashMap<String, Node>(varNameToNode);
        this.henshinFactory = HF;
        this.atl2Henshin = atl2Henshin;
        this.hostGraph = hostGraph;
        this.currentNC = HF.createNestedCondition();
        this.conclusion = HF.createGraph();
        this.currentNC.setConclusion(conclusion);

        NULL = henshinFactory.createNode();
        NULL.setName("NULL");
        PRESERVE = new Action(Action.Type.PRESERVE);
    }

    public OCL2NGC(OCL2NGC ocl2ngc, ATL2Henshin atl2Henshin2, Graph hostGraph2,
            Map<String, Node> varNameToNode2) {
        this(atl2Henshin2, hostGraph2, varNameToNode2);
        this.parentOcl2Ngc = ocl2ngc;
    }

    @Override
    public Object caseNavigationOrAttributeCallExp(
            NavigationOrAttributeCallExp navExp) {
        final OclExpression source = navExp.getSource();
        final Node sourceNode = (Node) this.doSwitch(source);

        final String propertyName = navExp.getName();
        EClass sourceType = sourceNode.getType();
        EStructuralFeature eStructuralFeature = sourceType
                .getEStructuralFeature(propertyName);

        if (eStructuralFeature instanceof EReference) {
            EReference eRef = (EReference) eStructuralFeature;

            Node targetNode = henshinFactory.createNode(sourceNode.getGraph(),
                    (EClass) eRef.getEType(), null);

            if (targetNode.getGraph().isLhs()) {
                targetNode.setAction(PRESERVE);
            }

            Edge edge = henshinFactory.createEdge(sourceNode, targetNode, eRef);
            if (edge.getGraph().isLhs()) {
                edge.setAction(PRESERVE);
            }

            return targetNode;
        } else if (eStructuralFeature instanceof EAttribute) {
            EAttribute eAtt = (EAttribute) eStructuralFeature;

            Rule rule = findRule();

            // Check if attribute has already been read
            Attribute existingAtt = sourceNode.getAttribute(eAtt);
            if (existingAtt != null) {
                String paramName = existingAtt.getValue();
                Parameter param = rule.getParameter(paramName);
                return param;
            } else {
                String pname = "p" + Integer.toString(paramCounter++);
                Parameter param = henshinFactory.createParameter(pname);
                rule.getParameters().add(param);
                Attribute attrib = henshinFactory.createAttribute(sourceNode,
                        eAtt, param.getName());

                if (hostGraph.isLhs()) {
                    attrib.setAction(PRESERVE);
                }

                return param;
            }
        } else {
            nys("NavigationOrAttributeCallExp not supported: {}", navExp);
        }

        return NULL;
    }

    @Override
    public Object caseBooleanExp(BooleanExp object) {
        Formula res;
        if (object.isBooleanSymbol()) {
            // return true
            return NGCUtils.createTrue();
        } else {
            // return false
            return NGCUtils.createFalse();
        }
    }

    @Override
    public Object caseOperatorCallExp(OperatorCallExp opCallExp) {
        String op = opCallExp.getOperationName();

        if ("and".equals(op)) {
            OclExpression left = opCallExp.getSource();
            OclExpression right = opCallExp.getArguments().get(0);
            Formula leftRes = new OCL2NGC(this, atl2Henshin, hostGraph,
                    varNameToNode).translateOcl2Ngc(left);
            Formula rightRes = new OCL2NGC(this, atl2Henshin, hostGraph,
                    varNameToNode).translateOcl2Ngc(right);

            return NGCUtils.createConjunction(leftRes, rightRes);
        } else if ("or".equals(op)) {
            OclExpression left = opCallExp.getSource();
            OclExpression right = opCallExp.getArguments().get(0);
            Formula leftRes = new OCL2NGC(this, atl2Henshin, hostGraph,
                    varNameToNode).translateOcl2Ngc(left);
            Formula rightRes = new OCL2NGC(this, atl2Henshin, hostGraph,
                    varNameToNode).translateOcl2Ngc(right);

            return NGCUtils.createDisjunction(leftRes, rightRes);
        } else if ("implies".equals(op)) {
            OclExpression left = opCallExp.getSource();
            OclExpression right = opCallExp.getArguments().get(0);
            Formula leftRes = new OCL2NGC(this, atl2Henshin, hostGraph,
                    varNameToNode).translateOcl2Ngc(left);
            Formula rightRes = new OCL2NGC(this, atl2Henshin, hostGraph,
                    varNameToNode).translateOcl2Ngc(right);

            return NGCUtils.createDisjunction(NGCUtils.negate(leftRes),
                    rightRes);
        } else if ("not".equals(op)) {
            OclExpression arg = opCallExp.getSource();
            Formula subFormula = new OCL2NGC(atl2Henshin, hostGraph,
                    varNameToNode).translateOcl2Ngc(arg);
            return NGCUtils.negate(subFormula);
        } else if ("=".equals(op)) {
            OclExpression left = opCallExp.getSource();
            OclExpression right = opCallExp.getArguments().get(0);
            Object leftObj = this.doSwitch(left);
            Object rightObj = this.doSwitch(right);

            if (leftObj instanceof Node && rightObj instanceof Node) {
                Node leftObjOrigin = currentNC.getMappings().getOrigin(
                        (Node) leftObj);
                Node rightObjOrigin = currentNC.getMappings().getOrigin(
                        (Node) rightObj);

                if (leftObjOrigin != null && rightObjOrigin != null) {
                    if (leftObjOrigin != rightObjOrigin) {
                        // Nodes were originally distinct so they cannot be
                        // equal
                        return NGCUtils.createFalse();
                    } else {
                        // Nodes were originally the same node, so they are
                        // already equal (condition is true), but merge nodes
                        // and keep NGC for readability.
                        mergeNodeIntoOther((Node) leftObj, (Node) rightObj);
                        HenshinUtils.annotate(currentNC, "(always TRUE)", null);
                    }
                } else {
                    mergeNodeIntoOther((Node) leftObj, (Node) rightObj);
                }
            } else {
                // This is scalar equality
                String attCond = printObj(leftObj) + " == "
                        + printObj(rightObj);

                AttributeCondition cdt = henshinFactory
                        .createAttributeCondition();
                cdt.setConditionText(attCond);
                cdt.setName(attCond);
                HenshinUtils.annotate(cdt,
                        OCLExpressionSimplePrinter.toString(opCallExp), null);

                Rule rule = findRule();
                rule.getAttributeConditions().add(cdt);
            }

            return currentNC;
        } else if ("<>".equals(op)) {
            OclExpression left = opCallExp.getSource();
            OclExpression right = opCallExp.getArguments().get(0);
            Object leftObj = this.doSwitch(left);
            Object rightObj = this.doSwitch(right);

            if (leftObj instanceof Node && rightObj instanceof Node) {
                if (leftObj == rightObj) {
                    return NGCUtils.createFalse();
                } else {
                    // If the nodes are distinct, this already implements the
                    // '<>' semantics
                    HenshinUtils.annotate(currentNC, "(always TRUE)", null);
                }
            } else {
                // This is scalar inequality
                String attCond = printObj(leftObj) + " != "
                        + printObj(rightObj);

                AttributeCondition cdt = henshinFactory
                        .createAttributeCondition();
                cdt.setConditionText(attCond);
                cdt.setName(attCond);
                HenshinUtils.annotate(cdt,
                        OCLExpressionSimplePrinter.toString(opCallExp), null);

                Rule rule = findRule();
                rule.getAttributeConditions().add(cdt);
            }

            return currentNC;
        } else {
            nys("OperatorCallExp not yet supported: {}", opCallExp);
        }

        return NULL;
    }

    private String printObj(Object obj) {
        if (obj instanceof Parameter) {
            return ((Parameter) obj).getName();
        } else if (obj instanceof String) {
            return "\"" + (String) obj + "\"";
        } else if (obj instanceof Boolean) {
            return ((Boolean) obj).booleanValue() ? "true" : "false";
        } else if (obj instanceof NestedCondition) {
            NestedCondition nc = (NestedCondition) obj;
            if (nc.isTrue()) {
                return "true";
            } else if (nc.isFalse()) {
                return "false";
            } else {
                error("Unsupported NestedCondition here");
            }
        }

        return obj.toString();
    }

    private Rule findRule() {
        if (parentOcl2Ngc != null) {
            return parentOcl2Ngc.findRule();
        } else {
            return hostGraph.getRule();
        }

    }

    @Override
    public Formula caseIfExp(IfExp ifExp) {
        OclExpression cdt = ifExp.getCondition();
        OclExpression thenExp = ifExp.getThenExpression();
        OclExpression elseExp = ifExp.getElseExpression();

        Formula cdtF = (Formula) this.doSwitch(cdt);
        Formula thenF = (Formula) this.doSwitch(thenExp);
        Formula elseF = (Formula) this.doSwitch(elseExp);

        Formula alt1 = NGCUtils.createConjunction(cdtF, thenF);
        Formula alt2 = NGCUtils.createConjunction(
                NGCUtils.negate(EcoreUtil.copy(cdtF)), elseF);
        Formula res = NGCUtils.createDisjunction(alt1, alt2);
        return res;
    }

    @Override
    public Object caseCollectionOperationCallExp(CollectionOperationCallExp exp) {
        String op = exp.getOperationName();
        Node sourceNode = (Node) this.doSwitch(exp.getSource());

        if ("includes".equals(op)) {
            Node argNode = (Node) this.doSwitch(exp.getArguments().get(0));

            if (sourceNode != argNode) {
                mergeNodeIntoOther(sourceNode, argNode);
                return currentNC;
            } else {
                return NGCUtils.createTrue();
            }

        } else {
            nys("CollectionOperationCallExp NYS: {}", exp);
        }

        return NULL;
    }

    private void mergeNodeIntoOther(Node node, Node other) {
        if (node != other) {
            EList<Edge> incomingEdges = node.getIncoming();

            Graph graph = node.getGraph();
            EObject graphContainer = graph.eContainer();

            MappingList mappings = null;

            if (graphContainer instanceof Rule) {
                Rule rule = (Rule) graphContainer;
                mappings = rule.getMappings();
            } else if (graphContainer instanceof NestedCondition) {
                NestedCondition nc = (NestedCondition) graphContainer;
                mappings = nc.getMappings();
            }

            if (mappings != null) {
                List<Mapping> toAdd = Lists.newArrayList();
                for (Iterator<Mapping> iterator = mappings.iterator(); iterator
                        .hasNext();) {
                    Mapping mapping = (Mapping) iterator.next();
                    // TODO: Here maybe we should check the image instead of the
                    // origin. The mappings come from the parent NGC.
                    if (mapping.getOrigin() == node) {
                        Node nodeImage = mapping.getImage();
                        Node otherImage = mappings.getImage(other,
                                nodeImage.getGraph());
                        if (otherImage == null) {
                            error("other node is not in mapping so cannot merge nodes");
                        } else {
                            mergeNodeIntoOther(nodeImage, otherImage);
                            // remove the mapping of node
                            iterator.remove();
                        }
                    } else if (mapping.getImage() == node) {
                        Node nodeOrigin = mapping.getOrigin();
                        Node otherOrigin = mappings.getOrigin(other);
                        if (otherOrigin != null && nodeOrigin != otherOrigin) {
                            error("Cannot merge nodes {} with {} because they are the images of 2 separate nodes",
                                    node, other);
                        } else {
                            iterator.remove();
                        }
                    }
                }
            }

            for (Edge sourceEdge : new ArrayList<Edge>(incomingEdges)) {
                sourceEdge.setTarget(other);
            }

            EcoreUtil.delete(node);
        }
    }

    @Override
    public Object caseOperationCallExp(OperationCallExp opCallExp) {
        String opName = opCallExp.getOperationName();
        OclExpression source = opCallExp.getSource();
        Node sourceNode = (Node) this.doSwitch(source);

        if ("oclIsTypeOf".equals(opName) || "oclIsKindOf".equals(opName)) {
            OclModelElement arg = (OclModelElement) opCallExp.getArguments()
                    .get(0);
            String oclType = arg.getName();
            EClass argType = atl2Henshin.resolveOclType(oclType);
            sourceNode.setType(argType);

            return currentNC;
        } else if ("oclIsUndefined".equals(opName)) {
            Formula overlappedNC = overlapWithHost();

            Not res = NGCUtils.negate(overlappedNC);

            return res;
        } else {
            nys(opName);
        }

        return NULL;
    }

    private static GraphOverlapGenerator overlapNCWithHost(NestedCondition nc,
            Graph host) {
        Graph conclusion = nc.getConclusion();
        // Create anchor
        Morphism reverseCopy = GraphCopier.reverseCopy(host);
        Graph anchorGraph = reverseCopy.getSource();
        Morphism ncMorphism = new Morphism(nc, host);
        Morphism anchorToConc = reverseCopy.compose(ncMorphism);
        Morphism anchorToHost = reverseCopy;
        anchorGraph.getEdges().removeAll(anchorToConc.getUnmappedSourceEdges());
        anchorGraph.getNodes().removeAll(anchorToConc.getUnmappedSourceNodes());

        Pair<Morphism, Morphism> anchor = Pair.with(anchorToConc, anchorToHost);
        boolean excludeEmptyOverlap = false;
        boolean enforceEMFConstraints = false;
        boolean fixEdgeAutoMapping = false;
        GraphOverlapGenerator overlapper = new GraphOverlapGenerator(
                conclusion, host, anchor, excludeEmptyOverlap,
                enforceEMFConstraints, fixEdgeAutoMapping, null);
        return overlapper;
    }

    class InjectivityOverlapper extends FormulaTransformer {

        private Graph host;

        public InjectivityOverlapper(Graph host) {
            this.host = host;
        }

        @Override
        public Formula caseNestedCondition(NestedCondition nc) {
            Graph conclusion = nc.getConclusion();
            Formula subFormula = conclusion.getFormula();

            GraphOverlapGenerator overlapper = overlapNCWithHost(nc, host);

            List<NestedCondition> newNCs = Lists.newArrayList();
            for (Pair<Morphism, Morphism> overlap : overlapper) {
                Morphism conclusionToOverlap = overlap.getValue0();
                Morphism hostToOverlap = overlap.getValue1();

                NestedCondition newNC = NGCUtils
                        .createNestedCondition(hostToOverlap);
                newNCs.add(newNC);

                for (Mapping mapping : conclusionToOverlap) {
                    Node origin = mapping.getOrigin();
                    Node image = mapping.getImage();

                    image.getAttributes().addAll(
                            EcoreUtil.copyAll(origin.getAttributes()));
                }

                if (subFormula != null) {
                    Formula newSubFormula = new InjectivityOverlapper(
                            conclusion).transform(subFormula);
                    newNC.getConclusion().setFormula(newSubFormula);
                }
            }

            if (newNCs.isEmpty()) {
                // When we cannot find any overlaps (not even the disjoint
                // union) it means that we can't overlap the two because of EMF
                // containment of EOpposite constraints. Therefore this
                // condition can never be fulfilled.
                return NGCUtils.createFalse();
            }

            return NGCUtils.createDisjunction(newNCs);
        }

    }

    private Formula overlapWithHost() {
        return new InjectivityOverlapper(hostGraph).transform(currentNC);
    }

    @Override
    public Object caseIteratorExp(IteratorExp iteratorExp) {
        String itName = iteratorExp.getName();

        Node sourceNode = (Node) this.doSwitch(iteratorExp.getSource());

        if ("exists".equals(itName)) {
            List<NestedCondition> newNCs = overlapIteratorAndBody(iteratorExp,
                    sourceNode);

            if (newNCs.isEmpty()) {
                // When we cannot find any overlaps (not even the disjoint
                // union) it means that we can't overlap the two because of EMF
                // containment of EOpposite constraints. Therefore this
                // condition can never be fulfilled.
                return NGCUtils.createFalse();
            } else {
                return NGCUtils.createDisjunction(newNCs);
            }

        } else if ("forAll".equals(itName)) {
            List<NestedCondition> newNCs = overlapIteratorAndBody(iteratorExp,
                    sourceNode);

            if (newNCs.isEmpty()) {
                // When we cannot find any overlaps (not even the disjoint
                // union) it means that we can't overlap the two because of EMF
                // containment of EOpposite constraints. Therefore this
                // forAll condition is fullfilled vacuously because we cannot
                // instantiate the iterator(s)
                return NGCUtils.createTrue();
            } else {
                // negate subFormulas
                for (NestedCondition nc : newNCs) {
                    Formula formula = nc.getConclusion().getFormula();
                    if (formula != null) {
                        nc.getConclusion().setFormula(NGCUtils.negate(formula));
                    }
                }

                return NGCUtils.createConjunction(NGCUtils.negate(newNCs));
            }
        } else {
            nys("IteratorExp not yet supported {}",
                    OCLExpressionSimplePrinter.toString(iteratorExp));
        }

        return NULL;
    }

    private List<NestedCondition> overlapIteratorAndBody(
            IteratorExp iteratorExp, Node sourceNode) {
        fr.tpt.atlanalyser.atl.OCL.Iterator iterator = iteratorExp
                .getIterators().get(0);
        sourceNode.setName(iterator.getVarName());

        GraphOverlapGenerator overlapper = overlapNCWithHost(currentNC,
                hostGraph);

        OclExpression body = iteratorExp.getBody();
        String itVarName = iterator.getVarName();
        List<NestedCondition> newNCs = Lists.newArrayList();
        for (Pair<Morphism, Morphism> overlap : overlapper) {
            Morphism conclusionToOverlap = overlap.getValue0();
            Morphism hostToOverlap = overlap.getValue1();

            SetView<Node> overlappedNodes = Sets.intersection(
                    conclusionToOverlap.getCodomainNodes(),
                    hostToOverlap.getCodomainNodes());

            Map<String, Node> newVarMap = Maps.newHashMap();
            for (Node node : overlappedNodes) {
                Node hostNode = hostToOverlap.getOrigin(node);
                Node conclusionNode = conclusionToOverlap.getOrigin(node);
                if (hostNode.getName().equals(conclusionNode.getName())) {
                    node.setName(hostNode.getName());
                } else {
                    newVarMap.put(hostNode.getName(), node);
                    newVarMap.put(conclusionNode.getName(), node);
                }
            }

            NestedCondition newNC = NGCUtils
                    .createNestedCondition(hostToOverlap);
            newNCs.add(newNC);

            for (Mapping mapping : conclusionToOverlap) {
                Node origin = mapping.getOrigin();
                Node image = mapping.getImage();

                image.getAttributes().addAll(
                        EcoreUtil.copyAll(origin.getAttributes()));
            }

            Node sourceNodeInNewNC = conclusionToOverlap.getImage(sourceNode);

            newVarMap.put("self", sourceNodeInNewNC);
            newVarMap.put(itVarName, sourceNodeInNewNC);
            Formula subFormula = new OCL2NGC(this, atl2Henshin,
                    newNC.getConclusion(), newVarMap).translateOcl2Ngc(body);

            newNC.getConclusion().setFormula(subFormula);
        }
        return newNCs;
    }

    private void nys(String fmt, Object... objects) {
        error("Not yet supported: " + fmt, objects);
    }

    @Override
    public Object doSwitch(EObject eObject) {
        if (errors.size() > 0) {
            return null;
        } else {
            EObject oldObject = currentObj;
            currentObj = eObject;
            Object result = super.doSwitch(eObject);
            currentObj = oldObject;
            return result;
        }
    }

    @Override
    public Object caseStringExp(StringExp stringExp) {
        return stringExp.getStringSymbol();
    }

    @Override
    public Node caseVariableExp(VariableExp varExp) {
        String varName = varExp.getReferredVariable().getVarName();

        if (conclusion != null && conclusion.getNode(varName) != null) {
            return conclusion.getNode(varName);
        }

        Node nodeInHost = null;
        // Check host graph
        if (hostGraph != null && hostGraph.getNode(varName) != null) {
            nodeInHost = hostGraph.getNode(varName);
        } else if (varNameToNode.containsKey(varName)) {
            nodeInHost = varNameToNode.get(varName);
        } else if (parentOcl2Ngc != null) {
            // Check parent NGC
            nodeInHost = parentOcl2Ngc.caseVariableExp(varExp);
        }

        if (nodeInHost != null) {
            if (currentNC.getMappings().getImage(nodeInHost, conclusion) != null) {
                return currentNC.getMappings().getImage(nodeInHost, conclusion);
            } else {
                Node nodeInConclusion = HF.createNode(conclusion,
                        nodeInHost.getType(), nodeInHost.getName());
                currentNC.getMappings().add(nodeInHost, nodeInConclusion);
                return nodeInConclusion;
            }
        }

        error("Couldn't find node for variable name: {}", varName);

        Node nodeFromParent = createNodeFromParentNGCs(conclusion, varName);
        if (nodeFromParent != null) {
            return nodeFromParent;
        }

        if (varNameToNode.containsKey(varName)) {
            Node node = varNameToNode.get(varName);
            hostGraph = node.getGraph();
            return node;
        } else {
            error("Couldn't find node for variable name");
        }

        return NULL;
    }

    private Node createNodeFromParentNGCs(Graph conclusion, String nodeName) {
        Graph hostGraph = ((NestedCondition) conclusion.eContainer()).getHost();

        if (hostGraph != null) {
            Node hostNode = hostGraph.getNode(nodeName);

            if (hostNode == null) {
                hostNode = createNodeFromParentNGCs(hostGraph, nodeName);
            }

            Node newNode = henshinFactory.createNode(conclusion,
                    hostNode.getType(), nodeName);
            ((NestedCondition) conclusion.eContainer()).getMappings().add(
                    hostNode, newNode);
            return newNode;
        } else {
            return null;
        }
    }

    private NestedCondition findParentNGC(NestedCondition ngc) {
        Graph host = ngc.getHost();
        if (host.isNestedCondition()) {
            return (NestedCondition) host.eContainer();
        } else {
            return null;
        }
    }

    public Formula translateOcl2Ngc(OclExpression oclExp) {
        if (oclExp != null) {
            Object result = this.doSwitch(oclExp);
            String stringExp = OCLExpressionSimplePrinter.toString(oclExp);

            if (result instanceof Parameter) {
                Parameter param = (Parameter) result;
                // This is scalar equality
                String attCond = String.format("%s == true", param.getName());

                AttributeCondition cdt = henshinFactory
                        .createAttributeCondition();
                cdt.setConditionText(attCond);
                cdt.setName(attCond);
                HenshinUtils.annotate(cdt, stringExp, null);

                Rule rule = findRule();
                rule.getAttributeConditions().add(cdt);

                result = currentNC;
            }

            // result is a Formula.
            // But we need a ModelElement to be able to annotate it. Formula
            // is abstract, and all its descendants inherit ModelElement, so
            // the following is safe.
            ModelElement formula = (ModelElement) result;
            EList<Annotation> annotations = formula.getAnnotations();
            if (annotations.size() > 0) {
                Annotation annotation = annotations.get(0);
                annotation.setKey(stringExp + " " + annotation.getKey());
            } else {
                Annotation annotation = HF.createAnnotation();
                annotation.setKey(stringExp);
                annotations.add(annotation);
            }

            return (Formula) result;
        }

        // if (errors.size() > 0) {
        // throw errors.get(0);
        // }

        return null;
    }

    private void error(String fmt, Object... args) {
        if (currentObj != null && currentObj instanceof LocatedElement) {
            error((LocatedElement) currentObj, fmt, args);
        } else {
            error(null, fmt, args);
        }
    }

    private static void error(LocatedElement elem, String fmt, Object... args) {
        ErrorReporting.error(LOGGER, elem, fmt, args);
    }
}
