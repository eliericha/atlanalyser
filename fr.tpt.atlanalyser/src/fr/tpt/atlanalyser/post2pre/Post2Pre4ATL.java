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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
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
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.model.util.HenshinSwitch;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import fr.tpt.atlanalyser.ATLAnalyserRuntimeException;
import fr.tpt.atlanalyser.post2pre.formulas.AbstractFormulaVisitor;
import fr.tpt.atlanalyser.post2pre.formulas.CombineExistsAndForAll;
import fr.tpt.atlanalyser.post2pre.formulas.SimplifyIsomorphicFormulas;
import fr.tpt.atlanalyser.post2pre.formulas.SimplifyingTransformer;
import fr.tpt.atlanalyser.post2pre.ncvalidators.ATLSemanticsValidator;
import fr.tpt.atlanalyser.post2pre.ncvalidators.ConjunctionValidator;
import fr.tpt.atlanalyser.post2pre.ncvalidators.ExcludeTypesNCValidator;
import fr.tpt.atlanalyser.post2pre.ncvalidators.NestedConditionValidator;
import fr.tpt.atlanalyser.utils.HenshinContainmentVisitor;
import fr.tpt.atlanalyser.utils.HenshinUnitPrinter;
import fr.tpt.atlanalyser.utils.NGCUtils;
import fr.tpt.atlanalyser.utils.Utils;

public class Post2Pre4ATL {

    private static final boolean                WITH_SCOPING             = false;
    private static final boolean                REORGANISE_FINAL_PRE     = false;
    private static final boolean                RULE_SELECTION           = true;
    private static final boolean                ENABLE_ELEM_CREAT_FILTER = true;
    private static final boolean                ENABLE_ATL_SEM_FILTER    = true;
    private static final boolean                ENABLE_RULE_REORDERING   = false;

    private static final HenshinFactory         HF                       = HenshinFactory.eINSTANCE;
    private static final Logger                 LOGGER                   = LogManager
                                                                                 .getLogger(Post2Pre4ATL.class
                                                                                         .getSimpleName());
    private static Unit                         instantiation;
    private static Unit                         resolving;
    private static EPackage                     inputMM;
    private static EPackage                     outputMM;
    private static EPackage                     traceMM;
    private static Set<EReference>              outputMMEReferences;
    private static Set<EClass>                  outputMMEClasses;
    private int                                 jobs                     = 4;
    private Map<Unit, NestedConditionValidator> unitToLeftNCValidator;
    private Map<Unit, NestedConditionValidator> unitToRightNCValidator;
    private EClass                              rootTraceEClass;
    private boolean                             enableValidators;
    private Multimap<ENamedElement, Unit>       typeToCreationUnits      = ArrayListMultimap
                                                                                 .create();
    private Multimap<Unit, ENamedElement>       unitToCreatedTypes       = ArrayListMultimap
                                                                                 .create();
    private Unit                                main;
    private static Module                       module;

    public Post2Pre4ATL(Module module, EPackage inputMM, EPackage outputMM,
            EPackage traceMM, boolean allowReordering, boolean enableValidators) {
        checkTransfoSupport(module);

        this.module = module;
        this.inputMM = inputMM;
        this.outputMM = outputMM;
        this.traceMM = traceMM;

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
        this.rootTraceEClass = (EClass) traceMM.getEClassifier("Trace");

        this.main = module.getUnit("Main");
        this.instantiation = module.getUnit("Instantiation");
        this.resolving = module.getUnit("Resolving");
        this.unitToLeftNCValidator = Maps.newHashMap();
        this.unitToRightNCValidator = Maps.newHashMap();

        analyseElementCreation();

        if (allowReordering && ENABLE_RULE_REORDERING) {
            reorderResolving(this.resolving);
        }

        this.enableValidators = enableValidators && ENABLE_ELEM_CREAT_FILTER;
        if (this.enableValidators) {
            // createEdgeCreationNCValidators();
            // setupInstantiationValidators();
            setupElementCreationFilters();
        }

        if (ENABLE_ATL_SEM_FILTER) {
            setupATLSemanticsFilter();
        }

    }

    private void setupATLSemanticsFilter() {
        List<Unit> allUnits = Lists.newArrayList(Iterables.concat(
                instantiation.getSubUnits(true), resolving.getSubUnits(true)));

        ATLSemanticsValidator atlSemValidator = new ATLSemanticsValidator(
                traceMM);
        allUnits.forEach(u -> {
            this.unitToRightNCValidator.put(u, atlSemValidator);
        });
    }

    private void analyseElementCreation() {
        class UnitVisitor extends HenshinSwitch<Object> {

            private List<Unit> parentUnits;

            public UnitVisitor(List<Unit> parentUnits) {
                this.parentUnits = parentUnits;
            }

            @Override
            public Object caseUnit(Unit object) {
                EList<Unit> innerUnits = object.getSubUnits(false);

                for (Unit innerUnit : innerUnits) {
                    ArrayList<Unit> newParents = Lists
                            .newArrayList(parentUnits);
                    newParents.add(object);
                    new UnitVisitor(newParents).doSwitch(innerUnit);
                }

                return object;
            }

            @Override
            public Object caseRule(Rule object) {
                EList<Node> createdNodes = object.getActionNodes(new Action(
                        Type.CREATE));
                for (Node node : createdNodes) {
                    EClass type = node.getType();
                    typeToCreationUnits.putAll(type, parentUnits);
                    typeToCreationUnits.put(type, object);
                    parentUnits.forEach(p -> {
                        unitToCreatedTypes.put(p, type);
                    });
                    unitToCreatedTypes.put(object, type);
                }
                EList<Edge> createdEdges = object.getActionEdges(new Action(
                        Type.CREATE));
                for (Edge edge : createdEdges) {
                    EReference type = edge.getType();
                    typeToCreationUnits.putAll(type, parentUnits);
                    typeToCreationUnits.put(type, object);
                    parentUnits.forEach(p -> {
                        unitToCreatedTypes.put(p, type);
                    });
                    unitToCreatedTypes.put(object, type);
                }
                return object;
            }

        }

        new UnitVisitor(Lists.newArrayList()).doSwitch(main);

    }

    private void checkTransfoSupport(Module module) {
        new HenshinContainmentVisitor<Boolean>() {

            public Boolean caseRule(Rule object) {
                if (object.getParameters().size() > 0) {
                    throw new ATLAnalyserRuntimeException(
                            "Rule parameters are not supported in: %s", object);
                }
                if (object.getAttributeConditions().size() > 0) {
                    throw new ATLAnalyserRuntimeException(
                            "Attribute conditions are not supported in: %s",
                            object);
                }
                return true;
            }

            public Boolean caseNode(Node object) {
                if (object.getAttributes().size() > 0) {
                    throw new ATLAnalyserRuntimeException(
                            "Node attributes are not supported in: %s", object);
                }
                return true;
            }

            public Boolean caseEdge(Edge object) {
                if (object.getIndex() != null && !object.getIndex().equals("")
                        && object.getIndexConstant() == null) {
                    throw new ATLAnalyserRuntimeException(
                            "Non-constant edge indexing is not supported in: %s",
                            object);
                }
                return true;
            }

        }.doSwitch(module);
    }

    private void setupElementCreationFilters() {
        // Get LoopUnits that iterate rules
        List<LoopUnit> loops = new HenshinSwitch<List<LoopUnit>>() {
            List<LoopUnit> result = Lists.newArrayList();

            @Override
            public List<LoopUnit> caseUnit(Unit object) {
                return result;
            }

            @Override
            public List<LoopUnit> caseSequentialUnit(SequentialUnit object) {
                object.getSubUnits().forEach(u -> doSwitch(u));
                return result;
            }

            @Override
            public List<LoopUnit> caseLoopUnit(LoopUnit object) {
                Unit subUnit = object.getSubUnit();
                if (subUnit instanceof Rule) {
                    result.add(object);
                } else {
                    doSwitch(subUnit);
                }
                return result;
            }
        }.doSwitch(main);

        for (LoopUnit loop : loops) {
            HashSet<ENamedElement> createdTypes = Sets
                    .newHashSet(unitToCreatedTypes.get(loop));

            List<LoopUnit> precedingLoops = loops.subList(0,
                    loops.indexOf(loop));
            Set<ENamedElement> precedingCreatedTypes = precedingLoops.stream()
                    .flatMap(l -> unitToCreatedTypes.get(l).stream())
                    .collect(Collectors.toSet());

            Set<ENamedElement> typesToFilter = createdTypes
                    .stream()
                    .filter(t1 -> precedingCreatedTypes.stream().allMatch(
                            t2 -> t1 != t2))
                    .filter(t1 -> {
                        if (t1 instanceof EReference)
                            return true;
                        else {
                            EClass c1 = (EClass) t1;
                            return precedingCreatedTypes
                                    .stream()
                                    .filter(t2 -> t2 instanceof EClass)
                                    .map(t2 -> (EClass) t2)
                                    .allMatch(
                                            t2 -> !c1.isSuperTypeOf(t2)
                                                    && !t2.isSuperTypeOf(c1));
                        }
                    }).collect(Collectors.toSet());

            if (typesToFilter.size() > 0) {
                ExcludeTypesNCValidator validator = new ExcludeTypesNCValidator(
                        typesToFilter);
                unitToLeftNCValidator.put(loop, validator);
            }
        }
    }

    private void setupInstantiationValidators() {
        if (this.instantiation instanceof SequentialUnit) {
            SequentialUnit seq = (SequentialUnit) this.instantiation;

            final Map<EClass, List<LoopUnit>> typeToCreationLoopUnits = Maps
                    .newHashMap();
            final Map<EClass, List<Unit>> typeToCreationRule = Maps
                    .newHashMap();
            final Map<Unit, List<EClass>> unitToCreatedTypes = Maps
                    .newHashMap();

            EList<Unit> subUnits = seq.getSubUnits();
            for (Unit unit : subUnits) {
                Rule rule = null;
                if (unit instanceof LoopUnit) {
                    LoopUnit loop = (LoopUnit) unit;
                    rule = (Rule) loop.getSubUnit();
                } else if (unit instanceof Rule) {
                    rule = (Rule) unit;
                }
                EList<Node> createdNodes = rule.getActionNodes(new Action(
                        Type.CREATE));
                for (Node node : createdNodes) {
                    EClass type = node.getType();
                    Utils.addToMap(typeToCreationRule, type, rule);
                    Utils.addToMap(unitToCreatedTypes, rule, type);
                    if (unit instanceof LoopUnit) {
                        Utils.addToMap(typeToCreationLoopUnits, type,
                                (LoopUnit) unit);
                        Utils.addToMap(unitToCreatedTypes, (LoopUnit) unit,
                                type);
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
                    List<?> list = typeToCreationLoopUnits.get(eClass);
                    if (unit instanceof LoopUnit) {
                        list = typeToCreationLoopUnits.get(eClass);
                    } else if (unit instanceof Rule) {
                        list = typeToCreationRule.get(eClass);
                    }
                    if (list.size() == 1) {
                        toExclude.add(eClass);
                    }
                }

                ExcludeTypesNCValidator leftNcValidator = new ExcludeTypesNCValidator(
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
                SequentialUnit seqUnit = HF.createSequentialUnit();
                seqUnit.setName("CreationOf_"
                        + eRef.getEContainingClass().getName() + "_"
                        + eRef.getName());
                seqUnit.getSubUnits().addAll(creationUnits);
                subUnits.add(seqUnit);
                //
                // ExcludeEReferenceNCValidator leftNcValidator = new
                // ExcludeEReferenceNCValidator(
                // eRef);
                //
                // // associate the validator with the first rule of the
                // sequence
                // this.unitToLeftNCValidator.put(creationUnits.get(0),
                // leftNcValidator);
                // this.unitToLeftNCValidator.put(creationUnits.get(0)
                // .getSubUnit(), leftNcValidator);
                //
                // MaxEReferenceCountNCValidator rightNcValidator = new
                // MaxEReferenceCountNCValidator(
                // eRef, 1);
                // // this.unitToRightNCValidator.put(creationUnits.get(0),
                // // rightNcValidator);
                // // this.unitToRightNCValidator.put(creationUnits.get(0)
                // // .getSubUnit(), rightNcValidator);
            }
        }
    }

    private void createEdgeCreationNCValidators() {
        List<LoopUnit> loopUnits = Lists.newArrayList();

        new HenshinSwitch<Object>() {
            public Object caseSequentialUnit(SequentialUnit object) {
                EList<Unit> sUnits = object.getSubUnits();
                for (Unit unit : sUnits) {
                    doSwitch(unit);
                }
                return object;
            }

            public Object caseLoopUnit(LoopUnit object) {
                loopUnits.add(object);
                return object;
            }
        }.doSwitch(resolving);

        Collections.reverse(loopUnits);
        loopUnits: for (int i = 0; i < loopUnits.size(); i++) {
            LoopUnit loopUnit = loopUnits.get(i);

            Set<EReference> createdRefs = Sets
                    .newHashSet(getCreatedRef(loopUnit));

            for (int j = i + 1; j < loopUnits.size(); j++) {
                LoopUnit otherUnit = loopUnits.get(j);
                Set<EReference> otherCreatedRefs = Sets
                        .newHashSet(getCreatedRef(otherUnit));
                if (Sets.intersection(createdRefs, otherCreatedRefs).size() > 0) {
                    // do not create NC validator
                    continue loopUnits;
                }
            }

            // no preceding rules, create left nc validator to be applied at
            // last iteration of the loop unit
            NestedConditionValidator leftNcValidator = new ConjunctionValidator(
                    createdRefs.stream().map(r -> Lists.newArrayList(r))
                            .map(ExcludeTypesNCValidator::new)
                            .toArray(NestedConditionValidator[]::new));
            this.unitToLeftNCValidator.put(loopUnit, leftNcValidator);
        }
    }

    private List<EReference> getCreatedRef(LoopUnit loopUnit) {
        return getCreatedRef((Rule) loopUnit.getSubUnit());
    }

    private List<EReference> getCreatedRef(Rule rule) {
        EList<Edge> createdEdges = rule.getActionEdges(new Action(Type.CREATE));
        // Preconditions.checkArgument(createdEdges.size() == 1,
        // "Error: Rule %s should create only one edge", rule);
        // return createdEdges.get(0).getType();
        return createdEdges.stream().map(e -> e.getType())
                .collect(Collectors.toList());
    }

    public Post2Pre4ATL(Module transfo, EPackage inputMM, EPackage outputMM,
            EPackage traceMM2, int jobs, boolean allowReordering,
            boolean enableValidators) {
        this(transfo, inputMM, outputMM, traceMM2, allowReordering,
                enableValidators);
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

        Map<String, Object> metrics = Post2PreManager.INSTANCE.metrics;
        metrics.put("Transformation", module.getName());
        metrics.put("Post", postcondition.eResource().getURI().lastSegment());
        metrics.put("ATL Semantics Filter", ENABLE_ATL_SEM_FILTER);
        metrics.put("Element Creation Filter", ENABLE_ELEM_CREAT_FILTER);
        metrics.put("Reorder Rules", ENABLE_RULE_REORDERING);

        List<Unit> unitsToConsider = Lists.newArrayList();
        Set<EClass> typesToConsider = Sets.newHashSet();
        Set<EReference> refsToConsider = Sets.newHashSet();
        new AbstractFormulaVisitor() {
            @Override
            public Formula caseNestedCondition(NestedCondition nc) {
                Graph conclusion = nc.getConclusion();
                conclusion.getNodes().forEach(n -> {
                    typesToConsider.add(n.getType());
                });
                conclusion.getEdges().forEach(e -> {
                    refsToConsider.add(e.getType());
                });
                conclusion.getNodes().forEach(
                        n -> unitsToConsider.addAll(typeToCreationUnits.get(n
                                .getType())));
                conclusion.getEdges().forEach(
                        e -> unitsToConsider.addAll(typeToCreationUnits.get(e
                                .getType())));
                return super.caseNestedCondition(nc);
            }
        }.transform(postcondition);

        List<Unit> allUnits = Lists.newArrayList(Iterables.concat(
                instantiation.getSubUnits(true), resolving.getSubUnits(true)));
        Set<Unit> unitsToIgnore = Sets.newHashSet();
        if (RULE_SELECTION) {
            loopOverUnits: for (LoopUnit loopUnit : allUnits.stream()
                    .filter(u -> u instanceof LoopUnit).map(u -> (LoopUnit) u)
                    .filter(u -> u.getSubUnit() instanceof Rule)
                    .toArray(LoopUnit[]::new)) {
                Rule rule = (Rule) loopUnit.getSubUnit();
                Collection<ENamedElement> createdTypes = unitToCreatedTypes
                        .get(rule);
                for (ENamedElement type : createdTypes) {
                    if (type instanceof EClass) {
                        EClass createdEClass = (EClass) type;
                        if (typesToConsider.stream().anyMatch(
                                t -> t.isSuperTypeOf(createdEClass))) {
                            continue loopOverUnits;
                        }
                    } else if (type instanceof EReference) {
                        EReference createdERef = (EReference) type;
                        if (refsToConsider.contains(createdERef)) {
                            continue loopOverUnits;
                        }
                    }
                }

                unitsToIgnore.add(loopUnit);
                unitsToIgnore.add(rule);
            }
        } else {
            unitsToIgnore = Collections.emptySet();
        }

        HenshinResourceSet resourceSet = (HenshinResourceSet) traceMM
                .eResource().getResourceSet();
        Post2Pre post2Pre = new Post2Pre(resourceSet, jobs,
                unitToLeftNCValidator, unitToRightNCValidator, unitsToIgnore);
        Formula pre = null;
        try {
            LOGGER.info("Starting Post2Pre for: {}",
                    HenshinUnitPrinter.print(resolving));
            pre = post2Pre.post2Pre(postcondition, resolving,
                    maxNumRuleIterations, WITH_SCOPING);
            Graph hostGraph = NGCUtils.getHostGraph(pre);
            if (enableValidators) {
                pre = new CleanupTargetEdges().transform(pre);
                hostGraph.setFormula(pre);
            }
            // pre = NGCUtils.simplify(pre);
            // hostGraph.setFormula(pre);
            LOGGER.info("Starting Post2Pre for: {}",
                    HenshinUnitPrinter.print(instantiation));
            pre = post2Pre.post2Pre(pre, instantiation, maxNumRuleIterations,
                    WITH_SCOPING);

            // Graph cdtGraph = HF.createGraph();
            // Formula ffalse = NGCUtils.createFalse();
            // cdtGraph.setFormula(ffalse);
            // Formula existanceOfResult = post2Pre.post2Pre(ffalse, resolving,
            // maxNumRuleIterations, true, withIterationBound);
            // cdtGraph.setFormula(existanceOfResult);
            // existanceOfResult = post2Pre.post2Pre(existanceOfResult,
            // instantiation, maxNumRuleIterations, true,
            // withIterationBound);
            // existanceOfResult = NGCUtils.negate(existanceOfResult);
            //
            // pre = NGCUtils.createConjunction(existanceOfResult, pre);

            hostGraph.setFormula(pre);
            pre = new CleanupTargetNodes().transform(pre);
            hostGraph.setFormula(pre);
            pre = NGCUtils.simplify(pre);
            hostGraph.setFormula(pre);
            pre = new SimplifyIsomorphicFormulas().transform(pre);
            hostGraph.setFormula(pre);

            if (REORGANISE_FINAL_PRE) {
                pre = new CombineExistsAndForAll().transform(pre);
                hostGraph.setFormula(pre);
            }
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
