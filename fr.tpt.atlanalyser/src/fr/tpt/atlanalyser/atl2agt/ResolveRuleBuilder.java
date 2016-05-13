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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.And;
import org.eclipse.emf.henshin.model.Attribute;
import org.eclipse.emf.henshin.model.AttributeCondition;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.GraphElement;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Not;
import org.eclipse.emf.henshin.model.Parameter;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fr.tpt.atlanalyser.ATLEnvironment;
import fr.tpt.atlanalyser.atl.ATL.Binding;
import fr.tpt.atlanalyser.atl.ATL.Helper;
import fr.tpt.atlanalyser.atl.ATL.InPatternElement;
import fr.tpt.atlanalyser.atl.ATL.LocatedElement;
import fr.tpt.atlanalyser.atl.ATL.MatchedRule;
import fr.tpt.atlanalyser.atl.ATL.Module;
import fr.tpt.atlanalyser.atl.ATL.OutPatternElement;
import fr.tpt.atlanalyser.atl.ATL.PatternElement;
import fr.tpt.atlanalyser.atl.ATL.Rule;
import fr.tpt.atlanalyser.atl.OCL.BooleanExp;
import fr.tpt.atlanalyser.atl.OCL.CollectionExp;
import fr.tpt.atlanalyser.atl.OCL.CollectionOperationCallExp;
import fr.tpt.atlanalyser.atl.OCL.IfExp;
import fr.tpt.atlanalyser.atl.OCL.IntegerExp;
import fr.tpt.atlanalyser.atl.OCL.IteratorExp;
import fr.tpt.atlanalyser.atl.OCL.NavigationOrAttributeCallExp;
import fr.tpt.atlanalyser.atl.OCL.OclExpression;
import fr.tpt.atlanalyser.atl.OCL.OclModelElement;
import fr.tpt.atlanalyser.atl.OCL.OclType;
import fr.tpt.atlanalyser.atl.OCL.OperationCallExp;
import fr.tpt.atlanalyser.atl.OCL.OperatorCallExp;
import fr.tpt.atlanalyser.atl.OCL.OrderedSetExp;
import fr.tpt.atlanalyser.atl.OCL.SequenceExp;
import fr.tpt.atlanalyser.atl.OCL.SetExp;
import fr.tpt.atlanalyser.atl.OCL.StringExp;
import fr.tpt.atlanalyser.atl.OCL.VariableDeclaration;
import fr.tpt.atlanalyser.atl.OCL.VariableExp;
import fr.tpt.atlanalyser.atl.OCL.util.OCLSwitch;
import fr.tpt.atlanalyser.utils.ErrorReporting;
import fr.tpt.atlanalyser.utils.FullOCLExpressionPrinter;
import fr.tpt.atlanalyser.utils.NGCUtils;

public class ResolveRuleBuilder extends OCLSwitch<Node> {

    private final ATL2Henshin                                                              atl2Henshin;
    Graph                                                                                  lhs;
    Map<VariableDeclaration, Node>                                                         varDeclToNode;
    Edge                                                                                   lastCreatedEdge;
    private org.eclipse.emf.henshin.model.Rule                                             rule;
    private Binding                                                                        currentBinding;
    private int                                                                            itCounter           = 1;
    private final Set<org.eclipse.emf.henshin.model.Rule>                                  additionalRules;
    private int                                                                            paramCounter        = 1;
    private Stack<String>                                                                  attExpStack         = new Stack<String>();
    private Node                                                                           selfNode;
    private Rule                                                                           atlRule;
    private static final Logger                                                            LOGGER              = LogManager
                                                                                                                       .getLogger(ResolveRuleBuilder.class
                                                                                                                               .getSimpleName());
    private static final HenshinFactory                                                    HF                  = HenshinFactory.eINSTANCE;
    private ATLEnvironment                                                                 env;
    private int                                                                            nodeCounter         = 0;
    private Map<GraphElement, List<GraphElement>>                                          elemToOrderNacElems = new HashMap<GraphElement, List<GraphElement>>();
    private Map<Edge, Triplet<List<Parameter>, List<AttributeCondition>, NestedCondition>> edgeToOrderingNac   = new HashMap<Edge, Triplet<List<Parameter>, List<AttributeCondition>, NestedCondition>>();
    private EObject                                                                        currentObj;
    private boolean                                                                        inSortedBy;
    private String                                                                         prevIndex;
    private String                                                                         currentIndex;

    public ResolveRuleBuilder(ATL2Henshin atl2Henshin, Rule atlRule,
            org.eclipse.emf.henshin.model.Rule rule,
            Map<? extends VariableDeclaration, Node> varDeclToNode,
            ATLEnvironment env) {
        this.atl2Henshin = atl2Henshin;
        this.atlRule = atlRule;
        this.rule = rule;
        lhs = rule.getLhs();
        this.varDeclToNode = new HashMap<VariableDeclaration, Node>(
                varDeclToNode);
        this.env = env;
        additionalRules = new LinkedHashSet<org.eclipse.emf.henshin.model.Rule>();
    }

    @Override
    public Node doSwitch(EObject eObject) {
        currentObj = eObject;
        return super.doSwitch(eObject);
    }

    @Override
    public Node caseVariableExp(VariableExp object) {
        final VariableDeclaration referredVariable = object
                .getReferredVariable();
        Node node = varDeclToNode.get(referredVariable);

        if (node == null) {
            if (referredVariable instanceof PatternElement) {
                node = getNodeForPatternElem((PatternElement) referredVariable);
            } else if ("self".equals(referredVariable.getVarName())) {
                if (selfNode != null) {
                    return selfNode;
                } else {
                    error("Could not find 'self' node");
                }
            } else if ("thisModule".equals(referredVariable.getVarName())) {
                return null;
            } else {
                error("Could not find node of referred variable.");
            }
        }

        return node;
    }

    public String getAttributeValue() {
        return attExpStack.pop();
    }

    @Override
    public Node caseNavigationOrAttributeCallExp(
            NavigationOrAttributeCallExp navExp) {
        final OclExpression source = navExp.getSource();
        final String propertyName = navExp.getName();
        final Node lastNodeInRhs = this.doSwitch(source);
        if (lastNodeInRhs == null) {
            // This is an access to thisModule
            final Helper helper = this.atl2Henshin.resolveHelper(null,
                    propertyName, getAtlModule());
            fr.tpt.atlanalyser.atl.OCL.Attribute attr = (fr.tpt.atlanalyser.atl.OCL.Attribute) helper
                    .getDefinition().getFeature();
            final OclExpression initExpression = attr.getInitExpression();
            return this.doSwitch(initExpression);
        } else {
            // This is a call from the last created node
            final Node lastNodeInLhsTree = toLhs(lastNodeInRhs);
            final EClass lastNodeType = lastNodeInRhs.getType();

            final EStructuralFeature eStructuralFeature = lastNodeType
                    .getEStructuralFeature(propertyName);

            if (eStructuralFeature == null) {
                // This is a helper, look it up.
                Helper foundHelper = this.atl2Henshin.resolveHelper(
                        lastNodeType, propertyName, getAtlModule());

                if (foundHelper == null) {
                    error("Could not resolve helper {}", propertyName);
                }

                final OclExpression inlinedExpression = ATL2Henshin
                        .inlineCallToHelper(navExp, foundHelper);

                return this.doSwitch(inlinedExpression);
            } else if (eStructuralFeature instanceof EReference) {
                // This is a navigation of an EReference
                EReference eRef = (EReference) eStructuralFeature;

                Map<Pair<Node, EReference>, Pair<Edge, Node>> navigationMap = new HashMap<Pair<Node, EReference>, Pair<Edge, Node>>();
                final Pair<Node, EReference> keyPair = new Pair<Node, EReference>(
                        lastNodeInRhs, eRef);
                if (navigationMap.containsKey(keyPair)) {
                    final Pair<Edge, Node> valuePair = navigationMap
                            .get(lastNodeInRhs);
                    lastCreatedEdge = valuePair.getValue0();
                    return valuePair.getValue1();
                } else {
                    final EClass targetType = (EClass) eRef.getEType();

                    final Node targetNodeInLhs = lhs.getRule().createNode(
                            targetType);
                    final Node targetNodeInRhs = rule.getMappings().getImage(
                            targetNodeInLhs, rule.getRhs());
                    final Edge newEdgeInLhs = lhs.getRule().createEdge(
                            lastNodeInLhsTree, targetNodeInLhs, eRef);
                    final Edge newEdgeInRhs = rule.getMappings().getImage(
                            newEdgeInLhs, rule.getRhs());
                    lastCreatedEdge = newEdgeInRhs;

                    navigationMap.put(new Pair<Node, EReference>(lastNodeInRhs,
                            eRef), new Pair<Edge, Node>(newEdgeInRhs,
                            targetNodeInRhs));

                    if (!ATL2Henshin.DISABLE_ORDERED_REFS) {
                        // Replicate the navigation in nacs, if any
                        List<Node> nacNodes = getOrderNacElems(lastNodeInRhs);
                        for (Node lastNodeInNac : nacNodes) {
                            Graph nacGraph = lastNodeInNac.getGraph();
                            Node targetNodeInOrderNac = HF.createNode(nacGraph,
                                    targetType,
                                    "n" + Integer.toString(nodeCounter++));
                            Edge edgeInNac = HF.createEdge(lastNodeInNac,
                                    targetNodeInOrderNac, eRef);
                            addToNacOrderMap(targetNodeInRhs,
                                    targetNodeInOrderNac);
                            addToNacOrderMap(newEdgeInRhs, edgeInNac);
                        }
                    }

                    boolean isInIndexingExp = false;
                    EObject eContainer = navExp.eContainer();
                    if (eContainer instanceof CollectionOperationCallExp) {
                        CollectionOperationCallExp containerExp = (CollectionOperationCallExp) eContainer;
                        if ("at".equals(containerExp.getOperationName())) {
                            isInIndexingExp = true;
                        }
                    }

                    if (!ATL2Henshin.DISABLE_ORDERED_REFS && eRef.isMany()
                            && !isInIndexingExp
                            && !elemToOrderNacElems.containsKey(lastNodeInRhs)) {
                        int counter = paramCounter++;
                        prevIndex = "i" + Integer.toString(counter);
                        currentIndex = "j" + Integer.toString(counter);
                        Parameter prevIndexParam = HF
                                .createParameter(prevIndex);
                        rule.getParameters().add(prevIndexParam);
                        Parameter currentIndexParam = HF
                                .createParameter(currentIndex);
                        rule.getParameters().add(currentIndexParam);

                        // Node traceNodeInLhs = rule.getLhs()
                        // .getNodes(getTraceEClass()).get(0);
                        // Node traceNodeInRhs = toRhs(traceNodeInLhs);
                        // henshinFactory.createAttribute(traceNodeInLhs,
                        // counterEAtt, lastHandledIdx);
                        // henshinFactory.createAttribute(traceNodeInRhs,
                        // counterEAtt, currentIndex);

                        AttributeCondition attCdt1 = HF
                                .createAttributeCondition();
                        attCdt1.setName("OrderedMatching");
                        attCdt1.setConditionText(String.format("%s < %s",
                                prevIndex, currentIndex));
                        rule.getAttributeConditions().add(attCdt1);

                        if (!inSortedBy) {
                            newEdgeInLhs.setIndex(currentIndex);
                            newEdgeInRhs.setIndex(currentIndex);
                        }

                        NestedCondition nac = rule.getLhs().createNAC(
                                "OrderedMatching");
                        Node lastNodeInNac = HF.createNode(nac.getConclusion(),
                                lastNodeType,
                                "n" + Integer.toString(nodeCounter++));
                        MappingList nacMappings = nac.getMappings();
                        nacMappings.add(lastNodeInLhsTree, lastNodeInNac);

                        Node targetNodeInNac = HF.createNode(
                                nac.getConclusion(), targetType,
                                "n" + Integer.toString(nodeCounter++));

                        Edge edgeInNac = HF.createEdge(lastNodeInNac,
                                targetNodeInNac, eRef);

                        if (!inSortedBy) {
                            edgeInNac.setIndex(prevIndex);
                        }

                        addToNacOrderMap(lastNodeInRhs, lastNodeInNac);
                        addToNacOrderMap(targetNodeInRhs, targetNodeInNac);
                        addToNacOrderMap(newEdgeInRhs, edgeInNac);

                        edgeToOrderingNac
                                .put(newEdgeInRhs,
                                        new Triplet<List<Parameter>, List<AttributeCondition>, NestedCondition>(
                                                Lists.newArrayList(
                                                        prevIndexParam,
                                                        currentIndexParam),
                                                Lists.newArrayList(attCdt1),
                                                nac));
                    }

                    return targetNodeInRhs;
                }
            } else if (eStructuralFeature instanceof EAttribute) {
                final EAttribute eAtt = (EAttribute) eStructuralFeature;
                String paramName;
                // Check if parameter has already been read
                if (lastNodeInLhsTree.getAttribute(eAtt) != null) {
                    // use existing parameter
                    paramName = lastNodeInLhsTree.getAttribute(eAtt).getValue();
                } else {
                    // create new parameter
                    paramName = "p" + Integer.toString(paramCounter);
                    paramCounter++;
                    final Parameter param = HF.createParameter(paramName);
                    param.setType(eAtt.getEType());
                    rule.getParameters().add(param);

                    final Attribute lhsAtt = HF.createAttribute(
                            lastNodeInLhsTree, eAtt, paramName);
                    if (lhs.getNodes().contains(lastNodeInLhsTree)) {
                        // If the node was at the root of the LHS tree (in the
                        // LHS) we should map it to the RHS so it is preserved.
                        final Attribute rhsAtt = HF.createAttribute(
                                lastNodeInRhs, eAtt, paramName);
                        rule.getMappings().add(lhsAtt, rhsAtt);
                    }
                }

                attExpStack.push(paramName);

                return lastNodeInRhs;
            } else {
                error("{} in OCL expression NYI", eStructuralFeature.eClass()
                        .getName());
            }
        }

        return super.caseNavigationOrAttributeCallExp(navExp);
    }

    private <T extends GraphElement> void addToNacOrderMap(
            final T graphElemInRhs, T graphElemInNac) {
        if (elemToOrderNacElems.containsKey(graphElemInRhs)) {
            elemToOrderNacElems.get(graphElemInRhs).add(graphElemInNac);
        } else {
            List<GraphElement> list = new ArrayList<GraphElement>();
            list.add(graphElemInNac);
            elemToOrderNacElems.put(graphElemInRhs, list);
        }
    }

    private <T extends GraphElement> List<T> getOrderNacElems(
            final T graphElemInRhs) {
        @SuppressWarnings("unchecked")
        List<T> result = (List<T>) elemToOrderNacElems.get(graphElemInRhs);

        if (result == null) {
            result = Collections.emptyList();
        }

        return result;
    }

    @Override
    public Node caseIfExp(IfExp object) {
        // First copy the rule, then continue processing with the
        // if-condition as a guard. Then set the negation of the
        // if-condition as a guard of the copied rule.

        Map<String, String> varSub;
        if (selfNode != null) {
            varSub = new HashMap<String, String>();
            varSub.put("self", selfNode.getName());
        } else {
            varSub = Collections.emptyMap();
        }

        final OclExpression ifCondition = object.getCondition();

        EcoreUtil.Copier atlRuleCopier = new EcoreUtil.Copier();
        final MatchedRule atlRuleCopy = (MatchedRule) atlRuleCopier
                .copy(atlRule);
        atlRuleCopier.copyReferences();
        this.atl2Henshin.atlRuleToTraceEClass
                .put(atlRuleCopy, getTraceEClass());

        final IfExp ifExpCopy = (IfExp) atlRuleCopier.get(object);

        final String oclCondition = (new FullOCLExpressionPrinter(
                this.atl2Henshin, varSub)).doSwitch(ifCondition);

        final org.eclipse.emf.henshin.model.Rule elseRule = HF.createRule();
        elseRule.setName(rule.getName() + "_else");
        LOGGER.trace("Created rule {}", elseRule.getName());
        additionalRules.add(elseRule);

        // Copy attribute conditions from the current rule
        elseRule.getAttributeConditions().addAll(
                EcoreUtil.copyAll(rule.getAttributeConditions()));

        // final AttributeCondition elseAttCdt = HF.createAttributeCondition();
        // elseAttCdt.setName("else");
        // elseAttCdt.setConditionText("// not (" + oclCondition + ")");
        // elseRule.getAttributeConditions().add(elseAttCdt);

        EcoreUtil.replace(ifExpCopy, ifExpCopy.getElseExpression());
        EcoreUtil.delete(ifExpCopy);

        final Binding newBinding = (Binding) atlRuleCopier.get(currentBinding);

        ResolveRuleBuilder newVisitor = new ResolveRuleBuilder(
                this.atl2Henshin, atlRuleCopy, elseRule, Collections.EMPTY_MAP,
                env);

        newVisitor.processBinding(newBinding);
        OclExpression conditionCopy = ifExpCopy.getCondition();

        Formula conditionNGC = new OCL2NGC(atl2Henshin, elseRule.getLhs(),
                Collections.<String, Node> emptyMap())
                .translateOcl2Ngc(conditionCopy);

        NGCUtils.appendToAC(elseRule.getLhs(), NGCUtils.negate(conditionNGC));

        additionalRules.addAll(newVisitor.getAdditionalRules());

        rule.setName(rule.getName() + "_then");

        final OclExpression thenExpression = object.getThenExpression();
        EcoreUtil.replace(object, thenExpression);
        Node result = this.doSwitch(thenExpression);

        conditionNGC = new OCL2NGC(atl2Henshin, lhs,
                Collections.<String, Node> emptyMap())
                .translateOcl2Ngc(ifCondition);
        NGCUtils.appendToAC(lhs, conditionNGC);

        // final AttributeCondition thenAttCdt = HF.createAttributeCondition();
        // thenAttCdt.setName("then");
        // thenAttCdt.setConditionText("// " + oclCondition);
        // rule.getAttributeConditions().add(thenAttCdt);
        return result;
    }

    private Module getAtlModule() {
        final Module atlModule = atlRule.getModule();
        return atlModule;
    }

    @Override
    public Node caseOperatorCallExp(OperatorCallExp object) {
        final String op = object.getOperationName();

        if ("+".equals(op)) {
            final Node leftNode = this.doSwitch(object.getSource());
            final String leftExp = this.attExpStack.pop();

            final Node rightNode = this.doSwitch(object.getArguments().get(0));
            final String rightExp = this.attExpStack.pop();

            this.attExpStack.push(String.format("(%s) + (%s)", leftExp,
                    rightExp));
        }

        return super.caseOperatorCallExp(object);
    }

    @Override
    public Node caseStringExp(StringExp object) {
        final String stringLiteral = object.getStringSymbol();
        attExpStack.push(String.format("\"%s\"", stringLiteral));

        return super.caseStringExp(object);
    }

    @Override
    public Node caseBooleanExp(BooleanExp object) {
        if (object.isBooleanSymbol())
            attExpStack.push("true");
        else
            attExpStack.push("false");
        return super.caseBooleanExp(object);
    }

    @Override
    public Node caseIteratorExp(IteratorExp object) {
        final String op = object.getName();
        if ("collect".equals(op)) {
            final Node lastNode = this.doSwitch(object.getSource());
            final VariableDeclaration iterator = object.getIterators().get(0);
            varDeclToNode.put(iterator, lastNode);
            return this.doSwitch(object.getBody());
        } else if ("select".equals(op)) {
            // we need to create an application condition corresponding to
            // the body

            // Replace references to the iterator with references to the
            // parent node.

            final Node lastNode = this.doSwitch(object.getSource());

            final VariableDeclaration iterator = object.getIterators().get(0);

            // // if lastNode has no name, create a name based on the iterator
            // if (lastNode.getName() == null || lastNode.getName().isEmpty()) {
            // String iterName = iterator.getVarName() + "_"
            // + Integer.toString(itCounter);
            // itCounter++;
            //
            // lastNode.setName(iterName);
            // toLhs(lastNode).setName(iterName);
            // }
            //
            // final String oldIteratorName = iterator.getVarName();
            // iterator.setVarName(lastNode.getName());
            //
            // Map<String, String> varSub;
            // if (selfNode != null) {
            // varSub = new HashMap<String, String>();
            // varSub.put("self", selfNode.getName());
            // } else {
            // varSub = Collections.EMPTY_MAP;
            // }

            Map<String, Node> varNameToNode = Maps.newHashMap();
            for (Map.Entry<VariableDeclaration, Node> entry : varDeclToNode
                    .entrySet()) {
                varNameToNode.put(entry.getKey().getVarName(),
                        toLhs(entry.getValue()));
            }

            varNameToNode.put("self", toLhs(lastNode));
            varNameToNode.put(iterator.getVarName(), toLhs(lastNode));

            OCL2NGC ocl2Ngc = new OCL2NGC(atl2Henshin, lhs, varNameToNode);
            Formula ac = null;
            ac = ocl2Ngc.translateOcl2Ngc(object.getBody());
            NGCUtils.appendToAC(lhs, ac);

            List<Node> nacNodes = getOrderNacElems(lastNode);
            for (Node lastNodeInNac : nacNodes) {
                varNameToNode = Maps.newHashMap();
                for (Map.Entry<VariableDeclaration, Node> entry : varDeclToNode
                        .entrySet()) {
                    varNameToNode.put(entry.getKey().getVarName(),
                            toLhs(entry.getValue()));
                }
                varNameToNode.put("self", lastNodeInNac);
                varNameToNode.put(iterator.getVarName(), lastNodeInNac);
                Graph nacGraph = lastNodeInNac.getGraph();
                ocl2Ngc = new OCL2NGC(atl2Henshin, nacGraph, varNameToNode);
                Formula selectAC;
                selectAC = ocl2Ngc.translateOcl2Ngc(object.getBody());
                NGCUtils.appendToAC(nacGraph, selectAC);
            }

            // String oclUnparse = (new
            // FullOCLExpressionPrinter(this.atl2Henshin,
            // varSub)).doSwitch(object.getBody());
            // iterator.setVarName(oldIteratorName);

            // final AttributeCondition attCond = henshinFactory
            // .createAttributeCondition();
            // attCond.setName("AttCondForSelect");
            // attCond.setConditionText("// " + oclUnparse);
            //
            // rule.getAttributeConditions().add(attCond);

            return lastNode;
        } else if ("sortedBy".equals(op)) {
            inSortedBy = true;
            final Node lastNode = this.doSwitch(object.getSource());
            final VariableDeclaration iterator = object.getIterators().get(0);
            varDeclToNode.put(iterator, lastNode);
            this.doSwitch(object.getBody());
            String currentAttVal = attExpStack.pop();

            EcoreUtil.delete(rule.getParameter(currentAttVal));
            lastNode.getAttributes().stream()
                    .filter(a -> currentAttVal.equals(a.getValue()))
                    .forEach(a -> a.setValue(currentIndex));

            toLhs(lastNode).getAttributes().stream()
                    .filter(a -> currentAttVal.equals(a.getValue()))
                    .forEach(a -> a.setValue(currentIndex));

            final List<Node> orderNacElems = getOrderNacElems(lastNode);
            for (Node node : orderNacElems) {
                varDeclToNode.put(iterator, node);
                this.doSwitch(object.getBody());
                String prevAttVal = this.attExpStack.pop();
                EcoreUtil.delete(rule.getParameter(prevAttVal));
                node.getAttributes().stream()
                        .filter(a -> prevAttVal.equals(a.getValue()))
                        .forEach(a -> a.setValue(prevIndex));
            }
            inSortedBy = false;
            return lastNode;
        } else {
            error("Iterator expression '{}' NYI", object.getName());
            return null;
        }
    }

    @Override
    public Node caseCollectionOperationCallExp(CollectionOperationCallExp object) {
        final String op = object.getOperationName();

        Node node = null;

        if ("at".equals(op)) {
            node = this.doSwitch(object.getSource());

            final OclExpression arg = object.getArguments().get(0);
            if (arg instanceof IntegerExp) {
                IntegerExp intExp = (IntegerExp) arg;
                final int intValue = intExp.getIntegerSymbol();

                final String strIndex = Integer.toString(intValue - 1);
                lastCreatedEdge.setIndex(strIndex);
                rule.getMappings().getOrigin(lastCreatedEdge)
                        .setIndex(strIndex);

                if (!ATL2Henshin.DISABLE_ORDERED_REFS) {
                    // Remove any associated ordering NAC since we are forcing a
                    // specific index
                    Triplet<List<Parameter>, List<AttributeCondition>, NestedCondition> triplet = edgeToOrderingNac
                            .get(lastCreatedEdge);

                    if (triplet != null) {
                        List<Parameter> params = triplet.getValue0();
                        for (Parameter parameter : params) {
                            EcoreUtil.remove(parameter);
                        }

                        List<AttributeCondition> attCdts = triplet.getValue1();
                        for (AttributeCondition attributeCondition : attCdts) {
                            EcoreUtil.remove(attributeCondition);
                        }

                        NestedCondition nc = triplet.getValue2();
                        for (Edge edge : nc.getConclusion().getEdges()) {
                            removeFromOrderNacMap(edge);
                        }
                        for (Node nacNode : nc.getConclusion().getNodes()) {
                            removeFromOrderNacMap(nacNode);
                        }
                        Not not = (Not) nc.eContainer();
                        EObject eContainer = not.eContainer();

                        if (eContainer instanceof And) {
                            And and = (And) eContainer;
                            Formula other = and.getRight();
                            if (other == not) {
                                other = and.getLeft();
                            }
                            EcoreUtil.replace(and, other);
                        }

                        EcoreUtil.remove(not);

                        edgeToOrderingNac.remove(lastCreatedEdge);
                    }
                }

            } else {
                LOGGER.warn("Only indexing with literals is supported");
            }
        } else if ("flatten".equals(op) || "first".equals(op)
                || "asSet".equals(op)) {
            return this.doSwitch(object.getSource());
        } else if ("union".equals(op)) {
            Node lastNode = this.doSwitch(object.getSource());

            Map<VariableDeclaration, Node> newMap = Collections.emptyMap();
            // new HashMap<VariableDeclaration, Node>();
            // Copier copier = new EcoreUtil.Copier();
            final org.eclipse.emf.henshin.model.Rule newRule = HF.createRule();
            // copyRule(newMap,
            // copier);
            newRule.setName(rule.getName() + "_union");
            additionalRules.add(newRule);
            LOGGER.trace("Created rule {}", newRule.getName());

            ResolveRuleBuilder newVisitor = new ResolveRuleBuilder(
                    this.atl2Henshin, atlRule, newRule, newMap, env);

            newVisitor.processBindingForValue(currentBinding, object
                    .getArguments().get(0));
            additionalRules.addAll(newVisitor.getAdditionalRules());

            return lastNode;
        } else {
            error("Collection operation '{}' NYI", op);
        }

        return node;
    }

    private void removeFromOrderNacMap(GraphElement nacElem) {
        Collection<List<GraphElement>> values = elemToOrderNacElems.values();
        for (List<GraphElement> list : values) {
            if (list.remove(nacElem)) {
                break;
            }
        }
    }

    private org.eclipse.emf.henshin.model.Rule copyRule(
            Map<VariableDeclaration, Node> newVarDecl2NodeMap,
            EcoreUtil.Copier copier) {
        final org.eclipse.emf.henshin.model.Rule newRule = (org.eclipse.emf.henshin.model.Rule) copier
                .copy(rule);
        copier.copyReferences();
        additionalRules.add(newRule);

        // Create an updated map
        for (Map.Entry<VariableDeclaration, Node> entry : varDeclToNode
                .entrySet()) {
            newVarDecl2NodeMap.put(entry.getKey(),
                    (Node) copier.get(entry.getValue()));
        }

        return newRule;
    }

    @Override
    public Node caseSequenceExp(SequenceExp object) {
        return caseSequenceAndOrderedSetExp(object);
    }

    @Override
    public Node caseOrderedSetExp(OrderedSetExp object) {
        return caseSequenceAndOrderedSetExp(object);
    }

    public Node caseSequenceAndOrderedSetExp(CollectionExp object) {
        final EList<OclExpression> elements = object.getElements();

        final boolean isERef = this.atl2Henshin
                .getEFeatOfBinding(currentBinding) instanceof EReference;

        int i = 0;
        for (OclExpression element : elements) {
            this.processBindingForValue(currentBinding, element);
            if (isERef) {
                lastCreatedEdge.setIndex(Integer.toString(i));
                i++;
            }
        }

        return null;
    }

    @Override
    public Node caseSetExp(SetExp object) {
        final EList<OclExpression> elements = object.getElements();

        for (OclExpression element : elements) {
            this.processBindingForValue(currentBinding, element);
        }

        return null;
    }

    class ResolveArgumentVisitor extends OCLSwitch<List<Node>> {

        ResolveRuleBuilder                                    regularVisitor;
        private org.eclipse.emf.henshin.model.Rule            rule;
        private HashMap<VariableDeclaration, Node>            varDeclToNode;
        private ArrayList<org.eclipse.emf.henshin.model.Rule> additionalRules;
        private Rule                                          atlRule;
        private ATLEnvironment                                env;

        public ResolveArgumentVisitor(Rule atlRule,
                org.eclipse.emf.henshin.model.Rule rule,
                Map<? extends VariableDeclaration, Node> patElemToNode,
                ATLEnvironment env, ResolveRuleBuilder regularVisitor) {
            this.atlRule = atlRule;
            this.rule = rule;
            rule.getLhs();
            this.varDeclToNode = new HashMap<VariableDeclaration, Node>(
                    patElemToNode);
            this.env = env;
            this.additionalRules = new ArrayList<org.eclipse.emf.henshin.model.Rule>();

            this.regularVisitor = regularVisitor;
            // new ResolveRuleBuilder(atl2Henshin, atlRule,
            // this.rule, this.varDeclToNode, env);
        }

        @Override
        public List<Node> doSwitch(EObject eObject) {
            final List<Node> result;
            if (eObject instanceof SequenceExp) {
                result = this.caseSequenceExp((SequenceExp) eObject);
                regularVisitor.varDeclToNode.putAll(this.varDeclToNode);
                return result;
            } else {
                result = new ArrayList<Node>();
                result.add(regularVisitor.doSwitch(eObject));
                this.varDeclToNode.putAll(regularVisitor.varDeclToNode);
                return result;
            }
        }

        @Override
        public List<Node> caseSequenceExp(SequenceExp object) {
            final EList<OclExpression> elements = object.getElements();
            final List<Node> result = new ArrayList<Node>();

            for (OclExpression oclExpression : elements) {
                result.addAll(this.doSwitch(oclExpression));
            }

            return result;
        }
    }

    @Override
    public Node caseOperationCallExp(OperationCallExp object) {
        final String operationName = object.getOperationName();
        final OclExpression source = object.getSource();

        if ("resolveTemp".equals(operationName)) {
            final EList<OclExpression> args = object.getArguments();
            List<Node> nodesToResolve = (new ResolveArgumentVisitor(atlRule,
                    rule, varDeclToNode, env, this)).doSwitch(args.get(0));

            nodesToResolve = Lists.newArrayList(Lists.transform(nodesToResolve,
                    new Function<Node, Node>() {
                        @Override
                        public Node apply(Node arg0) {
                            return toLhs(arg0);
                        }
                    }));

            String outPatName = null;
            if (args.size() > 1) {
                final StringExp outPatStringExp = (StringExp) args.get(1);
                outPatName = outPatStringExp.getStringSymbol();
            } else {
                error("resolveTemp must have an output pattern name as a second argument");
            }

            final EClass targetType = (EClass) getTgtTypeOfBinding(currentBinding);

            Node resolvedNode = nonDefaultResolveSourceNodes(nodesToResolve,
                    targetType, outPatName);

            if (!ATL2Henshin.DISABLE_ORDERED_REFS) {
                // Resolve same nodes in ordering NACs
                ArrayList<List<Node>> nacNodes = Lists.newArrayList(Lists
                        .transform(nodesToResolve,
                                new Function<Node, List<Node>>() {
                                    @Override
                                    public List<Node> apply(Node input) {
                                        return getOrderNacElems(toRhs(input));
                                    }
                                }));

                List<List<Node>> resolveTuples = transpose(nacNodes);

                for (List<Node> tuple : resolveTuples) {
                    Node resolvedNodeInNac = nonDefaultResolveSourceNodes(
                            tuple, targetType, outPatName);
                    addToNacOrderMap(toRhs(resolvedNode), resolvedNodeInNac);
                }
            }

            return toRhs(resolvedNode);
        } else if ("allInstances".equals(operationName)) {
            OclModelElement sourceType = (OclModelElement) source;
            final EClass sourceEClass = this.atl2Henshin
                    .resolveOclTypeInInputMM(sourceType);

            return toRhs(rule.createNode(sourceEClass));
        } else if (!(object instanceof OperatorCallExp)
                && !(object instanceof CollectionOperationCallExp)) {
            // Parsing the source here creates duplicate elements because we
            // parse it again in the inlinedExpression, so we shouldn't parse
            // it. In that case we are not able to figure out the type of the
            // source, which is OK because resolving helpers can work based only
            // on names (i.e. it will not check for the context type of the
            // helper).
            // Node lastNode = this.doSwitch(source);
            // EClass lastNodeType = lastNode != null ? lastNode.getType() :
            // null;
            final Helper helper = this.atl2Henshin.resolveHelper(null,
                    operationName, getAtlModule());
            if (helper != null) {
                final OclExpression inlinedExpression = ATL2Henshin
                        .inlineCallToHelper(object, helper);
                return this.doSwitch(inlinedExpression);
            } else {
                error("Could not resolve operation call {}", object.print());
            }
        }

        return super.caseOperationCallExp(object);
    }

    static <T> List<List<T>> transpose(List<List<T>> table) {
        List<List<T>> ret = new ArrayList<List<T>>();
        final int N = table.get(0).size();
        for (int i = 0; i < N; i++) {
            List<T> col = new ArrayList<T>();
            for (List<T> row : table) {
                col.add(row.get(i));
            }
            ret.add(col);
        }
        return ret;
    }

    private class ResolveCandidate {
        public final Rule       atlRule;
        public final EClass     trace;
        public final EReference fromRef;
        public final EReference toRef;

        public ResolveCandidate(EClass trace, EReference fromRef,
                EReference toRef, Rule atlRule) {
            this.atlRule = atlRule;
            this.trace = trace;
            this.fromRef = fromRef;
            this.toRef = toRef;
        }

    }

    private List<ResolveCandidate> findResolveCandidates(String outPatName,
            EClass expectedEClass) {
        List<ResolveCandidate> result = new ArrayList<ResolveRuleBuilder.ResolveCandidate>();

        for (EClassifier eClassifier : this.atl2Henshin.traceEPkg
                .getEClassifiers()) {
            EClass trace = (EClass) eClassifier;

            final EList<EReference> eRefs = trace.getEAllReferences();
            final List<EReference> fromRefs = new ArrayList<EReference>();
            final List<EReference> toRefs = new ArrayList<EReference>();

            for (EReference eRef : eRefs) {
                final EClass eType = (EClass) eRef.getEType();
                if (this.atl2Henshin.sourceBaseType.isSuperTypeOf(eType)) {
                    // this is a "from" link
                    fromRefs.add(eRef);
                } else {
                    toRefs.add(eRef);
                }
            }

            EReference foundToERef = null;

            for (EReference eReference : toRefs) {
                if (!eReference.getName().equals("to")
                        && outPatName.equals(eReference.getName())
                        && expectedEClass.isSuperTypeOf((EClass) eReference
                                .getEType())) {
                    foundToERef = eReference;
                    break;
                }
            }

            if (foundToERef == null) {
                continue;
            }

            EReference foundFromERef = null;
            // Just use the "from" link straight away because the object to
            // resolve may have a type which is a supertype of the one
            // matched by the rule, and we have no static way of determine
            // whether the object is downcastable.
            foundFromERef = this.atl2Henshin.fromERef;

            // for (EReference eReference : fromRefs) {
            // if (!eReference.getName().equals("from")
            // && ((EClass) eReference.getEType())
            // .isSuperTypeOf(eClassToResolve)) {
            // foundFromERef = eReference;
            // break;
            // }
            // }

            if (foundFromERef == null) {
                continue;
            }

            Rule atlCandidateRule = atl2Henshin.getRuleFromTrace(trace);
            result.add(new ResolveCandidate(trace, foundFromERef, foundToERef,
                    atlCandidateRule));
        }

        return result;
    }

    public void processOutPatternElement(OutPatternElement outPatternElement) {
        List<Binding> bindings = env
                .mergeBindingsOverInheritance(outPatternElement);

        for (Binding binding : bindings) {
            processBinding(binding);
        }
    }

    public void processBinding(Binding binding) {
        processBindingForValue(binding, binding.getValue());
    }

    private EClassifier getTgtTypeOfBinding(Binding binding) {
        final EStructuralFeature eFeat = this.atl2Henshin
                .getEFeatOfBinding(binding);

        if (eFeat instanceof EReference) {
            return eFeat.getEType();
        } else {
            return null;
        }
    }

    private <T extends GraphElement> T toLhs(T rhsElem) {
        T res;
        if (rhsElem.getGraph().isLhs()) {
            res = rhsElem;
        } else {
            final org.eclipse.emf.henshin.model.Rule containingRule = rhsElem
                    .getGraph().getRule();
            res = containingRule.getMappings().getOrigin(rhsElem);
        }

        return res != null ? res : rhsElem;
    }

    private <T extends GraphElement> T toRhs(T lhsElem) {
        if (lhsElem.getGraph().isRhs()) {
            return lhsElem;
        } else {
            final org.eclipse.emf.henshin.model.Rule containingRule = lhsElem
                    .getGraph().getRule();
            return containingRule.getMappings().getImage(lhsElem,
                    containingRule.getRhs());
        }
    }

    private Node nonDefaultResolveSourceNodes(List<Node> sourceNodes,
            final EClass targetType, String outPatName) {

        final List<ResolveCandidate> resolveCandidates = findResolveCandidates(
                outPatName, targetType);

        if (resolveCandidates.size() == 0) {
            error("No resolve candidates found for binding {}.{}.{}",
                    currentBinding.getOutPatternElement().getOutPattern()
                            .getRule().getName(), currentBinding
                            .getOutPatternElement().getVarName(),
                    currentBinding.getPropertyName());
        }

        Graph host = sourceNodes.get(0).getGraph();

        // Create Trace
        final Node traceNode = HF.createNode(host, atl2Henshin.traceBaseEClass,
                null);

        if (host.isLhs()) {
            traceNode.setAction(new Action(Action.Type.PRESERVE));
        }

        int i = 0;
        for (Node srcNode : sourceNodes) {
            Edge edge = HF.createEdge(traceNode, srcNode, atl2Henshin.fromERef);
            edge.setIndex(Integer.toString(i));
            if (host.isLhs()) {
                edge.setAction(new Action(Action.Type.PRESERVE));
            }
            i++;
        }

        // Create resolved object, of the type of
        // the binding eRef
        final Node resolvedObject = HF.createNode(host, targetType, null);
        if (host.isLhs()) {
            resolvedObject.setAction(new Action(Action.Type.PRESERVE));
        }
        Edge edge = HF
                .createEdge(traceNode, resolvedObject, atl2Henshin.toERef);
        if (host.isLhs()) {
            edge.setAction(new Action(Action.Type.PRESERVE));
        }

        // Create a NAC that prevents more "from" elements in the
        // trace
        final NestedCondition nac = host.createNAC("OtherObjInFrom");
        final Graph conclusion = nac.getConclusion();
        final Node concTraceNode = EcoreUtil.copy(traceNode);
        conclusion.getNodes().add(concTraceNode);
        nac.getMappings().add(traceNode, concTraceNode);
        final Node forbiddenSrcElem = HF.createNode(conclusion,
                this.atl2Henshin.sourceBaseType, null);
        HF.createEdge(concTraceNode, forbiddenSrcElem, atl2Henshin.fromERef);

        List<NestedCondition> resolvePacs = Lists.newArrayList(Lists.transform(
                resolveCandidates,
                new Function<ResolveCandidate, NestedCondition>() {
                    @Override
                    public NestedCondition apply(
                            ResolveCandidate resolveCandidate) {
                        NestedCondition pac = HF.createNestedCondition();
                        Graph conc = HF.createGraph(resolveCandidate.atlRule
                                .getName());
                        pac.setConclusion(conc);
                        Node typedTraceNode = HF.createNode(conc,
                                resolveCandidate.trace, null);
                        pac.getMappings().add(traceNode, typedTraceNode);

                        Node resolvedNodeInPac = HF.createNode(conc,
                                targetType, null);
                        pac.getMappings()
                                .add(resolvedObject, resolvedNodeInPac);

                        HF.createEdge(typedTraceNode, resolvedNodeInPac,
                                resolveCandidate.toRef);
                        return pac;
                    }
                }));

        host.setFormula(NGCUtils.createConjunction(host.getFormula(),
                NGCUtils.createDisjunction(resolvePacs)));

        return resolvedObject;
    }

    private Node defaultResolveSourceNodes(List<Node> sourceNodes,
            EClass targetType, EClass traceType, EReference fromERef,
            EReference toERef) {
        Graph host = sourceNodes.get(0).getGraph();

        // Create Trace
        Node traceNode = HF.createNode(host, traceType, null);

        if (host.isLhs()) {
            traceNode.setAction(new Action(Action.Type.PRESERVE));
        }

        int i = 0;
        for (Node srcNode : sourceNodes) {
            Edge edge = HF.createEdge(traceNode, srcNode, fromERef);
            if (fromERef == atl2Henshin.fromERef) {
                edge.setIndex(Integer.toString(i));
            }
            if (host.isLhs()) {
                edge.setAction(new Action(Action.Type.PRESERVE));
            }
            i++;
        }

        // Create resolved object, of the type of
        // the binding eRef
        Node resolvedObject = HF.createNode(host, targetType, null);
        if (host.isLhs()) {
            resolvedObject.setAction(new Action(Action.Type.PRESERVE));
        }
        Edge edge = HF.createEdge(traceNode, resolvedObject, toERef);
        edge.setIndex("0");
        if (host.isLhs()) {
            edge.setAction(new Action(Action.Type.PRESERVE));
        }

        // Create a NAC that prevents more "from" elements in the
        // trace
        final NestedCondition nac = host.createNAC("OtherObjInFrom");
        final Graph conclusion = nac.getConclusion();
        final Node concTraceNode = EcoreUtil.copy(traceNode);
        conclusion.getNodes().add(concTraceNode);
        nac.getMappings().add(traceNode, concTraceNode);
        final Node forbiddenSrcElem = HF.createNode(conclusion,
                this.atl2Henshin.sourceBaseType, null);
        HF.createEdge(concTraceNode, forbiddenSrcElem, atl2Henshin.fromERef);

        return resolvedObject;
    }

    private Node defaultResolveSourceNode(Node sourceNode, EClass targetType) {
        return defaultResolveSourceNodes(Collections.singletonList(sourceNode),
                targetType, this.atl2Henshin.traceBaseEClass,
                this.atl2Henshin.fromERef, this.atl2Henshin.toERef);
    }

    private void createPreservedEdge(Node src, Node tgt, EReference eRef,
            int index) {
        final Edge lhsEdge = HF.createEdge(src, tgt, eRef);
        final org.eclipse.emf.henshin.model.Rule containingRule = src
                .getGraph().getRule();
        final Graph rhsGraph = containingRule.getRhs();
        final Edge rhsEdge = HF.createEdge(
                rule.getMappings().getImage(src, rhsGraph), rule.getMappings()
                        .getImage(tgt, rhsGraph), eRef);
        rule.getMappings().add(lhsEdge, rhsEdge);

        if (index >= 0) {
            final String strIndex = Integer.toString(index);
            lhsEdge.setIndex(strIndex);
            rhsEdge.setIndex(strIndex);
        }

    }

    private EReference getEReferenceForPatElem(PatternElement patElem) {
        final EClass traceEClass = getTraceEClass();
        final String varName = patElem.getVarName();
        return (EReference) traceEClass.getEStructuralFeature(varName);
    }

    private Node getNodeForPatternElem(PatternElement patElem) {
        if (varDeclToNode.containsKey(patElem)) {
            return varDeclToNode.get(patElem);
        }

        final String varName = patElem.getVarName();
        final OclType oclVarType = patElem.getType();

        final EClass varEClass;
        if (patElem instanceof InPatternElement) {
            varEClass = this.atl2Henshin.resolveOclTypeInInputMM(oclVarType);
        } else {
            varEClass = this.atl2Henshin.resolveOclTypeInOutputMM(oclVarType);
        }

        final Node lhsNode = rule.createNode(varEClass);
        lhsNode.setName(varName);
        final Node rhsNode = toRhs(lhsNode);
        rhsNode.setName(varName);

        varDeclToNode.put(patElem, rhsNode);

        final EClass traceEClass = getTraceEClass();
        final Node lhsTraceNode = rule.getLhs().getNodes(traceEClass).get(0);
        final Node rhsTraceNode = toRhs(lhsTraceNode);

        if (patElem instanceof InPatternElement) {
            InPatternElement inPatElem = (InPatternElement) patElem;

            final int fromIndex = inPatElem.getInPattern().getElements()
                    .indexOf(inPatElem);
            createPreservedEdge(lhsTraceNode, lhsNode,
                    this.atl2Henshin.fromERef, fromIndex);
            createPreservedEdge(lhsTraceNode, lhsNode,
                    getEReferenceForPatElem(inPatElem), -1);
        } else {
            OutPatternElement outPatElem = (OutPatternElement) patElem;

            final int toIndex = outPatElem.getOutPattern().getElements()
                    .indexOf(outPatElem);
            createPreservedEdge(lhsTraceNode, lhsNode, this.atl2Henshin.toERef,
                    toIndex);
            createPreservedEdge(lhsTraceNode, lhsNode,
                    getEReferenceForPatElem(outPatElem), -1);
        }

        return rhsNode;
    }

    private Node getTraceNode() {
        final EList<Node> traceNodes = rule.getLhs().getNodes(getTraceEClass());
        if (traceNodes.size() > 0) {
            return traceNodes.get(0);
        } else {
            Node trace = rule.createNode(getTraceEClass());
            trace.setName("trace");
            rule.getMappings().getImage(trace, rule.getRhs()).setName("trace");
            return trace;
        }
    }

    public void processBindingForValue(Binding binding, OclExpression value) {
        currentBinding = binding;

        final Node trace = getTraceNode();

        // find source node in the RHS
        Node source = getNodeForPatternElem(binding.getOutPatternElement());
        EStructuralFeature targetFeature = this.atl2Henshin
                .getEFeatOfBinding(binding);

        if (targetFeature == null) {
            error("Could not find target feature {}::{}", binding
                    .getOutPatternElement().getType().getName(),
                    binding.getPropertyName());
        }

        if (targetFeature instanceof EReference) {
            EReference targetERef = (EReference) targetFeature;
            final EClass targetType = (EClass) targetERef.getEType();

            final Node referencedNodeInRhs = this.doSwitch(value);

            if (referencedNodeInRhs == null) {
                return;
            }

            // At this point we need to determine within value, which
            // objects are instantiated by the same rule (VariableExp to
            // a OutPatternElement), and which are resolved from other
            // rules.

            final EClass referencedType = referencedNodeInRhs.getType();

            Edge rhsNewEdge = null;
            Node resolvedObject = null;
            if (this.atl2Henshin.targetBaseType.isSuperTypeOf(referencedType)) {
                // The object is already in the target metamodel so it's either
                // instantiated by the same rule or resolved using a resolveTemp

                if (source.getGraph() != rule.getRhs()
                        || referencedNodeInRhs.getGraph() != rule.getRhs()) {
                    LOGGER.info("one of source or target is not in RHS!");
                }

                resolvedObject = referencedNodeInRhs;
                rhsNewEdge = HF.createEdge(source, referencedNodeInRhs,
                        targetERef);
            } else {
                // This is an object from the source model, so we need
                // to resolve it.
                resolvedObject = defaultResolveSourceNode(
                        toLhs(referencedNodeInRhs), targetType);

                // Now create links to objects in the RHS
                // instantiated by the instantiation rule
                Node resolvedObjInRhs = toRhs(resolvedObject);
                rhsNewEdge = HF
                        .createEdge(source, resolvedObjInRhs, targetERef);

                // Resolve same object in Ordering NACs
                List<Node> nacNodes = getOrderNacElems(referencedNodeInRhs);
                for (Node nodeToResolve : nacNodes) {
                    Node resolvedObjInNac = defaultResolveSourceNode(
                            nodeToResolve, targetType);
                    addToNacOrderMap(toLhs(resolvedObjInRhs), resolvedObjInNac);
                }
            }

            // Add sub NACs to Ordering NACs
            List<Node> nacNodes = getOrderNacElems(resolvedObject);
            for (Node resolvedNodeInNac : nacNodes) {
                Graph nacGraph = resolvedNodeInNac.getGraph();
                NestedCondition nac = (NestedCondition) nacGraph.eContainer();

                Node sourceInLhs = toLhs(source);
                Node sourceInNac = nac.getMappings().getImage(sourceInLhs,
                        nacGraph);
                if (sourceInNac == null) {
                    sourceInNac = HF.createNode(nacGraph,
                            sourceInLhs.getType(), sourceInLhs.getName());
                    nac.getMappings().add(sourceInLhs, sourceInNac);
                    addToNacOrderMap(sourceInLhs, sourceInNac);
                }

                NestedCondition subNac = nacGraph.createNAC("BindingExists");
                Graph subNacGraph = subNac.getConclusion();
                Node sourceInSubNac = HF.createNode(subNacGraph,
                        sourceInNac.getType(), sourceInNac.getName());
                subNac.getMappings().add(sourceInNac, sourceInSubNac);
                Node targetInSubNac = HF.createNode(subNacGraph, targetType,
                        null);
                subNac.getMappings().add(resolvedNodeInNac, targetInSubNac);
                HF.createEdge(sourceInSubNac, targetInSubNac, targetERef);
            }

            lastCreatedEdge = rhsNewEdge;

            // Create a NAC forbidding the existence of the new edge
            final Node lhsSource = rule.getMappings().getOrigin(
                    rhsNewEdge.getSource());
            final Node lhsTarget = rule.getMappings().getOrigin(
                    rhsNewEdge.getTarget());

            final NestedCondition nac = rule.getLhs().createNAC(
                    "ApplyOncePerMatch");
            final Graph nacConcl = nac.getConclusion();
            final Node nacSource = HF.createNode(nacConcl, lhsSource.getType(),
                    lhsSource.getName());
            final Node nacTarget = HF.createNode(nacConcl, lhsTarget.getType(),
                    lhsTarget.getName());
            HF.createEdge(nacSource, nacTarget, rhsNewEdge.getType());
            nac.getMappings().add(lhsSource, nacSource);
            nac.getMappings().add(lhsTarget, nacTarget);
        } else {
            EAttribute targetEAttribute = (EAttribute) targetFeature;

            final Node referencedNode = this.doSwitch(value);

            if (referencedNode != null
                    && !this.atl2Henshin.sourceBaseType
                            .isSuperTypeOf(referencedNode.getType())) {
                error("Referenced node is not in source model. Cannot navigate target model in bindings.");
            }

            final String attVal = this.getAttributeValue();
            HF.createAttribute(source, targetEAttribute, attVal);

            final Node lhsTargetNode = toLhs(source);

            String newParamName = "p" + Integer.toString(paramCounter++);
            final Parameter newParam = HF.createParameter(newParamName);
            newParam.setType(targetEAttribute.getEType());
            rule.getParameters().add(newParam);

            HF.createAttribute(lhsTargetNode, targetEAttribute, newParamName);

            final AttributeCondition attCond = HF.createAttributeCondition();
            rule.getAttributeConditions().add(attCond);
            attCond.setConditionText(String.format("%s != (%s)", newParamName,
                    attVal));
        }
    }

    private EClass getTraceEClass() {
        return this.atl2Henshin.atlRuleToTraceEClass.get(atlRule);
    }

    public Set<org.eclipse.emf.henshin.model.Rule> getAdditionalRules() {
        return additionalRules;
    }

    private void error(String fmt, Object... args) {
        if (currentObj != null && currentObj instanceof LocatedElement) {
            error((LocatedElement) currentObj, fmt, args);
        } else {
            error(null, fmt, args);
        }
    }

    private static void error(LocatedElement elem, String fmt, Object... args) {
        ErrorReporting.error(ResolveRuleBuilder.LOGGER, elem, fmt, args);
    }
}