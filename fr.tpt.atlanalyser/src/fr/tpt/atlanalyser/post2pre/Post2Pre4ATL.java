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
package fr.tpt.atlanalyser.post2pre;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Action.Type;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.LoopUnit;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.SequentialUnit;
import org.eclipse.emf.henshin.model.Unit;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fr.tpt.atlanalyser.post2pre.formulas.SimplifyingTransformer;
import fr.tpt.atlanalyser.post2pre.ncvalidators.ExcludeEClassesNCValidator;
import fr.tpt.atlanalyser.post2pre.ncvalidators.ExcludeEReferenceNCValidator;
import fr.tpt.atlanalyser.post2pre.ncvalidators.MaxEReferenceCountNCValidator;
import fr.tpt.atlanalyser.post2pre.ncvalidators.NestedConditionValidator;
import fr.tpt.atlanalyser.utils.NGCUtils;
import fr.tpt.atlanalyser.utils.Utils;

public class Post2Pre4ATL {

    private Unit                                instantiation;
    private Unit                                resolving;
    private EPackage                            outputMM;
    private Set<EReference>                     outputMMEReferences;
    private Set<EClass>                         outputMMEClasses;
    private EPackage                            traceMM;
    private int                                 jobs = 4;
    private Map<Unit, NestedConditionValidator> unitToLeftNCValidator;
    private Map<Unit, NestedConditionValidator> unitToRightNCValidator;
    private EClass                              rootTraceEClass;

    public Post2Pre4ATL(Module module, EPackage inputMM, EPackage outputMM,
            EPackage traceMM) {
        this.instantiation = module.getUnit("Instantiation");
        this.resolving = module.getUnit("Resolving");
        this.unitToLeftNCValidator = Maps.newHashMap();
        this.unitToRightNCValidator = Maps.newHashMap();
        reorderResolving(this.resolving);
        setupInstantiationValidators(this.instantiation);
        this.outputMM = outputMM;
        this.outputMMEReferences = new HashSet<EReference>();
        this.outputMMEClasses = new HashSet<EClass>();
        for (Iterator<EObject> iterator = this.outputMM.eAllContents(); iterator
                .hasNext();) {
            EObject obj = iterator.next();
            if (obj instanceof EReference) {
                this.outputMMEReferences.add((EReference) obj);
            } else if (obj instanceof EClass) {
                this.outputMMEClasses.add((EClass) obj);
            }
        }
        this.traceMM = traceMM;
        this.rootTraceEClass = (EClass) traceMM.getEClassifier("Trace");
    }

    private void setupInstantiationValidators(Unit instantiation2) {
        if (instantiation2 instanceof SequentialUnit) {
            SequentialUnit seq = (SequentialUnit) instantiation2;

            final Map<EClass, List<LoopUnit>> typeToCreationLoopUnits = Maps
                    .newHashMap();
            final Map<EClass, List<Rule>> typeToCreationRule = Maps
                    .newHashMap();
            final Map<Unit, List<EClass>> unitToCreatedTypes = Maps
                    .newHashMap();

            EList<Unit> subUnits = seq.getSubUnits();
            for (Unit unit : subUnits) {
                if (unit instanceof LoopUnit) {
                    LoopUnit loop = (LoopUnit) unit;
                    Rule rule = (Rule) loop.getSubUnit();
                    EList<Node> createdNodes = rule.getActionNodes(new Action(
                            Type.CREATE));
                    for (Node node : createdNodes) {
                        EClass type = node.getType();
                        Utils.addToMap(typeToCreationLoopUnits, type, loop);
                        Utils.addToMap(typeToCreationRule, type, rule);
                        Utils.addToMap(unitToCreatedTypes, rule, type);
                        Utils.addToMap(unitToCreatedTypes, loop, type);
                    }
                }
            }

            // List<EClass> createdTypes = Lists
            // .newArrayList(typeToCreationLoopUnits.keySet());
            // Collections.sort(createdTypes, new Comparator<EClass>() {
            // @Override
            // public int compare(EClass o0, EClass o1) {
            // // first compare number of units
            // int cmp = typeToCreationLoopUnits.get(o1).size()
            // - typeToCreationLoopUnits.get(o0).size();
            //
            // // if same compare sizes of RHSs of rules
            // if (cmp == 0) {
            // cmp = maxRhsSize(o1) - maxRhsSize(o0);
            // }
            //
            // // if same compare names
            // if (cmp == 0) {
            // cmp = o0.getName().compareTo(o1.getName());
            // }
            // return cmp;
            // }
            //
            // private Integer maxRhsSize(EClass arg0) {
            // List<Rule> ruleList = typeToCreationRule.get(arg0);
            // List<Integer> rhsSizes = Lists.transform(ruleList,
            // new Function<Rule, Integer>() {
            // @Override
            // public Integer apply(Rule arg0) {
            // return arg0.getRhs().getNodes().size();
            // }
            // });
            // Integer maxRhsSize = Collections.max(rhsSizes);
            // return maxRhsSize;
            // }
            // });

            for (Unit unit : subUnits) {
                List<EClass> types = unitToCreatedTypes.get(unit);
                List<EClass> toExclude = Lists.newArrayList();
                for (EClass eClass : types) {
                    if (typeToCreationLoopUnits.get(eClass).size() == 1) {
                        toExclude.add(eClass);
                    }
                }

                ExcludeEClassesNCValidator leftNcValidator = new ExcludeEClassesNCValidator(
                        toExclude);
                unitToLeftNCValidator.put(unit, leftNcValidator);
            }

            // subUnits.clear();
            // for (EClass type : createdTypes) {
            // List<LoopUnit> creationUnits = typeToCreationLoopUnits
            // .get(type);
            // SequentialUnit seqUnit = HenshinFactory.eINSTANCE
            // .createSequentialUnit();
            // seqUnit.setName("CreationOf_" + type.getName());
            // seqUnit.getSubUnits().addAll(creationUnits);
            // subUnits.add(seqUnit);
            //
            // EClass[] eClasses = null;
            // ExcludeEClassesNCValidator leftNcValidator = new
            // ExcludeEClassesNCValidator(
            // eClasses);
            //
            // // associate the validator with the first rule of the sequence
            // this.unitToLeftNCValidator.put(creationUnits.get(0),
            // leftNcValidator);
            // this.unitToLeftNCValidator.put(creationUnits.get(0)
            // .getSubUnit(), leftNcValidator);
            //
            // MaxEReferenceCountNCValidator rightNcValidator = new
            // MaxEReferenceCountNCValidator(
            // null, 1);
            // this.unitToRightNCValidator.put(creationUnits.get(0),
            // rightNcValidator);
            // this.unitToRightNCValidator.put(creationUnits.get(0)
            // .getSubUnit(), rightNcValidator);
            // }

        }
    }

    private void reorderResolving(Unit resolving2) {
        if (resolving2 instanceof SequentialUnit) {
            SequentialUnit seq = (SequentialUnit) resolving2;

            final Map<EReference, List<LoopUnit>> refToCreationLoopUnits = Maps
                    .newHashMap();
            final Map<EReference, List<Rule>> refToCreationRule = Maps
                    .newHashMap();
            final Map<Unit, EReference> unitToCreatedRef = Maps.newHashMap();

            EList<Unit> subUnits = seq.getSubUnits();
            // Get counts of created features
            for (Unit unit : subUnits) {
                if (unit instanceof LoopUnit) {
                    LoopUnit loop = (LoopUnit) unit;
                    Rule resolveRule = (Rule) loop.getSubUnit();
                    EList<Edge> createdEdges = resolveRule
                            .getActionEdges(new Action(Type.CREATE));
                    for (Edge edge : createdEdges) {
                        EReference eRef = edge.getType();
                        unitToCreatedRef.put(resolveRule, eRef);
                        unitToCreatedRef.put(loop, eRef);
                        Utils.addToMap(refToCreationLoopUnits, eRef, loop);
                        Utils.addToMap(refToCreationRule, eRef, resolveRule);
                    }
                }
            }

            List<EReference> refs = Lists.newArrayList(refToCreationLoopUnits
                    .keySet());
            Collections.sort(refs, new Comparator<EReference>() {

                @Override
                public int compare(EReference o0, EReference o1) {
                    // first compare number of units
                    int cmp = refToCreationLoopUnits.get(o1).size()
                            - refToCreationLoopUnits.get(o0).size();

                    // if same compare sizes of RHSs of rules
                    if (cmp == 0) {
                        cmp = maxRhsSize(o1) - maxRhsSize(o0);
                    }

                    // if same compare names
                    if (cmp == 0) {
                        cmp = o0.getName().compareTo(o1.getName());
                    }
                    return cmp;
                }

                private Integer maxRhsSize(EReference arg0) {
                    List<Rule> ruleList = refToCreationRule.get(arg0);
                    List<Integer> rhsSizes = Lists.transform(ruleList,
                            new Function<Rule, Integer>() {
                                @Override
                                public Integer apply(Rule arg0) {
                                    return arg0.getRhs().getNodes().size();
                                }
                            });
                    Integer maxRhsSize = Collections.max(rhsSizes);
                    return maxRhsSize;
                }
            });

            subUnits.clear();
            for (EReference eRef : refs) {
                List<LoopUnit> creationUnits = refToCreationLoopUnits.get(eRef);
                SequentialUnit seqUnit = HenshinFactory.eINSTANCE
                        .createSequentialUnit();
                seqUnit.setName("CreationOf_"
                        + eRef.getEContainingClass().getName() + "_"
                        + eRef.getName());
                seqUnit.getSubUnits().addAll(creationUnits);
                subUnits.add(seqUnit);

                ExcludeEReferenceNCValidator leftNcValidator = new ExcludeEReferenceNCValidator(
                        eRef);

                // associate the validator with the first rule of the sequence
                this.unitToLeftNCValidator.put(creationUnits.get(0),
                        leftNcValidator);
                this.unitToLeftNCValidator.put(creationUnits.get(0)
                        .getSubUnit(), leftNcValidator);

                MaxEReferenceCountNCValidator rightNcValidator = new MaxEReferenceCountNCValidator(
                        eRef, 1);
                this.unitToRightNCValidator.put(creationUnits.get(0),
                        rightNcValidator);
                this.unitToRightNCValidator.put(creationUnits.get(0)
                        .getSubUnit(), rightNcValidator);
            }
        }
    }

    public Post2Pre4ATL(Module transfo, EPackage inputMM, EPackage outputMM,
            EPackage traceMM2, int jobs) {
        this(transfo, inputMM, outputMM, traceMM2);
        this.jobs = jobs;
    }

    private class CleanupTargetEdges extends SimplifyingTransformer {

        @Override
        public Formula caseNestedCondition(NestedCondition object) {
            Graph conclusion = object.getConclusion();
            EList<Edge> edges = conclusion.getEdges();
            for (Edge edge : edges) {
                if (outputMMEReferences.contains(edge.getType())) {
                    return NGCUtils.createFalse();
                }
            }

            Formula subFormula = conclusion.getFormula();
            if (subFormula != null) {
                subFormula = this.transform(subFormula);
                conclusion.setFormula(subFormula);

                if (NGCUtils.isFalse(subFormula)) {
                    // return FALSE
                    return subFormula;
                }
            }

            return object;
        }

    }

    private class CleanupTargetNodes extends SimplifyingTransformer {

        @Override
        public Formula caseNestedCondition(NestedCondition object) {
            Graph conclusion = object.getConclusion();
            EList<Node> nodes = conclusion.getNodes();
            for (Node node : nodes) {
                EClass type = node.getType();
                if (outputMMEClasses.contains(type)
                        || rootTraceEClass.isSuperTypeOf(type)) {
                    return NGCUtils.createFalse();
                }
            }

            Formula subFormula = conclusion.getFormula();
            if (subFormula != null) {
                subFormula = this.transform(subFormula);
                conclusion.setFormula(subFormula);

                if (NGCUtils.isFalse(subFormula)) {
                    // return FALSE
                    return subFormula;
                }
            }

            return object;
        }

    }

    public Formula post2Pre(Formula postcondition, int maxNumRuleIterations) {
        boolean doIntermediateSimplifications = false;
        Post2Pre post2Pre = new Post2Pre(traceMM, jobs, unitToLeftNCValidator,
                unitToRightNCValidator);
        Formula pre = null;
        try {
            pre = post2Pre.post2Pre(postcondition, resolving,
                    maxNumRuleIterations, doIntermediateSimplifications);
            Graph hostGraph = NGCUtils.getHostGraph(pre);
            pre = new CleanupTargetEdges().transform(pre);
            hostGraph.setFormula(pre);
            // pre = NGCUtils.simplify(pre);
            // hostGraph.setFormula(pre);
            pre = post2Pre.post2Pre(pre, instantiation, maxNumRuleIterations,
                    true);
            hostGraph.setFormula(pre);
            pre = new CleanupTargetNodes().transform(pre);
            hostGraph.setFormula(pre);
            pre = NGCUtils.simplify(pre);
            hostGraph.setFormula(pre);
        } finally {
            try {
                post2Pre.finalize();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return pre;
    }

}
