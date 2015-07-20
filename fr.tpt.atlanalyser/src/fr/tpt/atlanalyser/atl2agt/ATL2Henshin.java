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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.GraphElement;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.LoopUnit;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.SequentialUnit;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.exporters.HenshinAGGExporter;
import org.eclipse.emf.henshin.model.resource.HenshinResource;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fr.tpt.atlanalyser.ATLAnalyserException;
import fr.tpt.atlanalyser.ATLEnvironment;
import fr.tpt.atlanalyser.AtlParser;
import fr.tpt.atlanalyser.atl.ATL.ATLPackage;
import fr.tpt.atlanalyser.atl.ATL.Binding;
import fr.tpt.atlanalyser.atl.ATL.Helper;
import fr.tpt.atlanalyser.atl.ATL.InPatternElement;
import fr.tpt.atlanalyser.atl.ATL.LibraryRef;
import fr.tpt.atlanalyser.atl.ATL.MatchedRule;
import fr.tpt.atlanalyser.atl.ATL.Module;
import fr.tpt.atlanalyser.atl.ATL.ModuleElement;
import fr.tpt.atlanalyser.atl.ATL.OutPatternElement;
import fr.tpt.atlanalyser.atl.ATL.PatternElement;
import fr.tpt.atlanalyser.atl.ATL.Rule;
import fr.tpt.atlanalyser.atl.OCL.NavigationOrAttributeCallExp;
import fr.tpt.atlanalyser.atl.OCL.OCLFactory;
import fr.tpt.atlanalyser.atl.OCL.OclContextDefinition;
import fr.tpt.atlanalyser.atl.OCL.OclExpression;
import fr.tpt.atlanalyser.atl.OCL.OclFeature;
import fr.tpt.atlanalyser.atl.OCL.OclType;
import fr.tpt.atlanalyser.atl.OCL.OperationCallExp;
import fr.tpt.atlanalyser.atl.OCL.PropertyCallExp;
import fr.tpt.atlanalyser.atl.OCL.VariableDeclaration;
import fr.tpt.atlanalyser.atl.OCL.VariableExp;
import fr.tpt.atlanalyser.atl.OCL.util.OCLContainmentVisitor;
import fr.tpt.atlanalyser.atl.OCL.util.OCLSwitch;
import fr.tpt.atlanalyser.atl.PrimitiveTypes.PrimitiveTypesPackage;
import fr.tpt.atlanalyser.utils.CopierPreservingReferences;
import fr.tpt.atlanalyser.utils.EPackageUtils;
import fr.tpt.atlanalyser.utils.NGCUtils;

public class ATL2Henshin {

    private static final boolean WITHOUT_NACS = false;

    public static void main(String[] args) throws Exception {
        /*
         * ResourceSet resourceSet = new ResourceSetImpl();
         * resourceSet.setPackageRegistry(registry); resourceSet
         * .getResourceFactoryRegistry() .getExtensionToFactoryMap()
         * .put(Resource.Factory.Registry.DEFAULT_EXTENSION, new
         * XMIResourceFactoryImpl());
         */

        /* Prepare fr.tpt.atlanalyser.atl.OCL API */
        /*
         * EcoreEnvironmentFactory envFactory = new EcoreEnvironmentFactory(
         * registry);
         * 
         * fr.tpt.atlanalyser.atl.OCL ocl =
         * fr.tpt.atlanalyser.atl.OCL.newInstance(envFactory);
         * 
         * // Create some fr.tpt.atlanalyser.atl.OCL constraint
         * 
         * String body = "import 'http://simplemm/1.0'\n" +
         * "package codemodel\n" + "context CodeModel inv TC1: true\n" +
         * "endpackage";
         * 
         * OCLInput oclInput = new OCLInput(body);
         * 
         * List<Constraint> postConditions = ocl.parse(oclInput);
         */

        String in = null, out = null;

        (new ATL2Henshin(new File[] {
        // new File("../Transformations/03-CMGenerator/Sum.atl"),
        // new File(
        // "../Transformations/03-CMGenerator/CMGenerator.atl")
        new File(in) }, true)).translateToHenshin(out);
    }

    private ArrayList<File>     ATL_PATH                = new ArrayList<File>();

    private static final Logger LOGGER                  = LogManager
                                                                .getLogger(ATL2Henshin.class
                                                                        .getSimpleName());

    private static final Node   SELF                    = HenshinFactory.eINSTANCE
                                                                .createNode();
    private static final Node   THISMODULE              = HenshinFactory.eINSTANCE
                                                                .createNode();

    private boolean             autoLoadImportedModules = false;

    private EPackage            simplemmEPkg;
    EClass                      sourceBaseType;

    EClass                      targetBaseType;

    private List<EPackage>      ePackages               = new ArrayList<EPackage>();

    private File[]              atlFiles;
    private AtlParser           atlParser;
    private ATLEnvironment      env;

    private EPackage            inputMM;

    private EPackage            outputMM;

    public ATL2Henshin(ResourceSet rs) {
        resourceSet = rs;

        // Access package instances to force their initialization
        ATLPackage.eINSTANCE.eClass();
        OCLFactory.eINSTANCE.eClass();
        PrimitiveTypesPackage.eINSTANCE.eClass();

        SELF.setName("self");
        THISMODULE.setName("thisModule");
        atlResourceSet = new ResourceSetImpl();
        atlParser = new AtlParser(atlResourceSet);
        env = atlParser.getEnv();
    }

    public ATL2Henshin() {
        this(new ResourceSetImpl());
    }

    public ATL2Henshin(File[] atlFiles, boolean autoLoadImportedModules)
            throws Exception {
        this();

        this.atlFiles = atlFiles;
        this.autoLoadImportedModules = autoLoadImportedModules;

        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put("*", new XMIResourceFactoryImpl());
        // atlMetamodel = AtlParser.getDefault().getAtlMetamodel();

        for (File file : atlFiles) {
            parseAtlFile(file);
        }
    }

    public void registerInputMetamodel(EPackage pkg)
            throws ATLAnalyserException {
        inputMM = pkg;
        String[] extraURIs = {};
        EPackageUtils.registerPackageAndAllSubPackages(pkg,
                resourceSet.getPackageRegistry(),
                extraURIs);
        sourceBaseType = findRootMetaclass(pkg);
        if (sourceBaseType == null) {
            throw new ATLAnalyserException("Could not find root metaclass.");
        }
        ePackages.add(pkg);
    }

    public void registerOutputMetamodel(EPackage pkg)
            throws ATLAnalyserException {
        outputMM = pkg;
        String[] extraURIs = {};
        EPackageUtils.registerPackageAndAllSubPackages(pkg,
                resourceSet.getPackageRegistry(),
                extraURIs);
        targetBaseType = findRootMetaclass(pkg);
        if (targetBaseType == null) {
            throw new ATLAnalyserException("Could not find root metaclass.");
        }
        ePackages.add(pkg);
    }

    private EClass findRootMetaclass(EPackage pkg) {
        EClass res = null;

        final List<EClass> eClasses = Lists.transform(pkg.getEClassifiers(),
                new Function<EClassifier, EClass>() {
                    @Override
                    public EClass apply(EClassifier arg) {
                        if (arg instanceof EClass) {
                            return (EClass) arg;
                        } else {
                            return null;
                        }
                    }
                });

        res = eClasses.get(0);

        for (EClass eClass : eClasses) {
            if (eClass.isSuperTypeOf(res)) {
                res = eClass;
            }
        }

        return res;
    }

    public ATL2Henshin(File atlFile, boolean autoLoadImportedModules)
            throws Exception {
        this(new File[] { atlFile }, autoLoadImportedModules);
    }

    private File findFileInPATH(ArrayList<File> PATH, String targetFileName) {
        for (File file : PATH) {
            File fullPath = new File(file, targetFileName);
            if (fullPath.exists()) {
                return fullPath;
            }
        }

        return null;
    }

    private Map<String, Module> parsedFiles = new LinkedHashMap<String, Module>();

    public Module parseAtlFile(File atlFile) throws ATLAnalyserException {
        Module res = null;
        out("Parsing " + atlFile.getPath());
        if (!parsedFiles.containsKey(atlFile.getAbsolutePath())) {
            try {
                Module module = atlParser
                        .parseAtlTransformation(new FileInputStream(atlFile));
                parsedFiles.put(atlFile.getAbsolutePath(), module);
                atlModules.add(module);
                res = module;

                out("Finished parsing " + atlFile.getPath());

                // add location of ATL file to ATL_PATH
                ATL_PATH.add(atlFile.getParentFile());

                if (autoLoadImportedModules) {
                    for (LibraryRef libRef : module.getLibraries()) {
                        // lookup imported ATL libraries
                        // in ATL_PATH
                        String atlRefFileName = libRef.getName() + ".atl";
                        File atlRefFile = findFileInPATH(ATL_PATH,
                                atlRefFileName);
                        if (atlRefFile != null) {
                            parseAtlFile(atlRefFile);
                        } else {
                            String msg = "Could not find module "
                                    + libRef.getName() + " imported by "
                                    + module.getName();
                            error(msg);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                error("ATL file not found : " + atlFile.getPath());
            }
        } else {
            res = parsedFiles.get(atlFile.getAbsolutePath());
        }

        return res;
    }

    private static void out(Object obj) {
        System.out.println(obj);
    }

    // IReferenceModel atlMetamodel;
    private ArrayList<Module>                                   atlModules              = new ArrayList<Module>();
    private Map<String, Module>                                 moduleNameToModule      = new HashMap<String, Module>();
    private Map<String, Rule>                                   nameToRule              = new HashMap<String, Rule>();
    private Map<String, Set<Rule>>                              eClassToCreatorRules    = new HashMap<String, Set<Rule>>();
    private Map<Rule, List<org.eclipse.emf.henshin.model.Rule>> atlRuleToOutPatternRule = new HashMap<Rule, List<org.eclipse.emf.henshin.model.Rule>>();
    Map<Rule, EClass>                                           atlRuleToTraceEClass    = Maps.newLinkedHashMap();
    static final HenshinFactory                                 henshinFactory          = HenshinFactory.eINSTANCE;
    org.eclipse.emf.henshin.model.Module                        henshinModule;

    private org.eclipse.emf.henshin.model.Module translateToHenshin()
            throws ATLAnalyserException {
        initTracePackage();
        fillRuleMap();
        henshinModule = henshinFactory.createModule();
        henshinModule.getImports().addAll(ePackages);
        henshinModule.getImports().add(traceEPkg);
        henshinModule.setName("MyHenshinModule");

        for (Module module : atlModules) {
            analyzeAtlTransformation(module);
        }

        return henshinModule;
    }

    private EPackage initTracePackage() {
        assert sourceBaseType != null && targetBaseType != null;

        traceEPkg = EcoreFactory.eINSTANCE.createEPackage();
        traceEPkg.setName("traces");
        traceEPkg.setNsPrefix("traces");
        traceEPkg.setNsURI("http://traces/1.0");

        traceBaseEClass = EcoreFactory.eINSTANCE.createEClass();
        traceBaseEClass.setName("Trace");
        // TODO: if I set it as abstract I can't do overlapping stuff in the
        // construction of NGCs
        // traceBaseEClass.setAbstract(true);
        traceEPkg.getEClassifiers().add(traceBaseEClass);

        fromERef = EcoreFactory.eINSTANCE.createEReference();
        fromERef.setName("from");
        fromERef.setEType(sourceBaseType);
        fromERef.setLowerBound(1);
        fromERef.setUpperBound(-1);
        traceBaseEClass.getEStructuralFeatures().add(fromERef);

        toERef = EcoreFactory.eINSTANCE.createEReference();
        toERef.setName("to");
        toERef.setEType(targetBaseType);
        toERef.setLowerBound(1);
        toERef.setUpperBound(-1);
        traceBaseEClass.getEStructuralFeatures().add(toERef);

        return traceEPkg;
    }

    public org.eclipse.emf.henshin.model.Module translateToHenshin(
            File atlTransformation) throws ATLAnalyserException {
        parseAtlFile(atlTransformation);
        return translateToHenshin();
    }

    public EPackage getTraceMM() {
        return traceEPkg;
    }

    public void translateToHenshin(String outFilename) throws Exception {
        org.eclipse.emf.henshin.model.Module module = translateToHenshin();

        final File inFile = atlFiles[0];
        if (outFilename == null || outFilename.isEmpty()) {
            final String baseName = FilenameUtils.getBaseName(inFile.getPath());
            outFilename = (new File(inFile.getParentFile(), baseName
                    + ".henshin")).getPath();
        }

        outFilename = (new File(outFilename)).getCanonicalPath();

        // Write the trace metamodel to a file

        Resource res = new HenshinResource(URI.createFileURI(outFilename
                + ".traces.ecore"));
        res.getContents().add(traceEPkg);
        try {
            res.save(Collections.EMPTY_MAP);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write the Henshin module to a file
        res = new HenshinResource(URI.createFileURI(outFilename));
        res.getContents().add(module);
        LOGGER.info("Writing to {}", outFilename);
        try {
            res.save(Collections.EMPTY_MAP);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.info("Export as AGG project");

        // Export as AGG project
        HenshinAGGExporter aggExporter = new HenshinAGGExporter();
        URI aggFileUri = URI.createFileURI(outFilename
                + aggExporter.getExportFileExtensions()[0]);
        IStatus status = aggExporter.doExport(module, aggFileUri);
        if (status.getSeverity() != IStatus.OK) {
            LOGGER.info(status.getMessage());
        }

        LOGGER.info("Done!");
    }

    private void fillRuleMap() {
        for (Module module : atlModules) {
            for (ModuleElement elem : module.getElements()) {
                if (elem instanceof Rule) {
                    Rule rule = (Rule) elem;

                    nameToRule.put(rule.getName(), rule);
                }
            }
        }
    }

    private Rule getRule(String name) {
        return nameToRule.get(name);
    }

    public Rule getRuleFromTrace(EClass trace) {
        for (Entry<Rule, EClass> entry : atlRuleToTraceEClass.entrySet()) {
            if (entry.getValue() == trace) {
                return entry.getKey();
            }
        }
        return null;
    }

    public EClass getTraceFromRule(Rule rule) {
        return this.atlRuleToTraceEClass.get(rule);
    }

    private void append(Map<String, Set<Rule>> targetMap, String key, Rule value) {
        if (!targetMap.containsKey(key)) {
            targetMap.put(key, new LinkedHashSet<Rule>());
        }

        targetMap.get(key).add(value);
    }

    private void print(Map<String, Set<Rule>> map) {
        for (String type : map.keySet()) {
            StringBuilder s = new StringBuilder("[");
            for (Iterator<Rule> iterator = map.get(type).iterator(); iterator
                    .hasNext();) {
                Rule rule = iterator.next();
                s.append(rule.getName());
                if (rule instanceof MatchedRule) {
                    MatchedRule mRule = (MatchedRule) rule;
                    if (mRule.isIsAbstract()) {
                        s.append("(abstract)");
                    }
                }
                if (iterator.hasNext()) {
                    s.append(", ");
                }
            }
            s.append("]");

            out(String.format("%s is created by %s", type, s.toString()));

        }
    }

    private Map<Rule, Map<PatternElement, Node>>          atlRule2PatternMap        = new HashMap<Rule, Map<PatternElement, Node>>();

    private Map<Rule, org.eclipse.emf.henshin.model.Rule> atlRule2InstantiationRule = new HashMap<Rule, org.eclipse.emf.henshin.model.Rule>();

    EPackage                                              traceEPkg;
    EClass                                                traceBaseEClass;
    EReference                                            fromERef;
    EReference                                            toERef;

    private static final boolean                          STRONG_TYPED_TRACES       = true;

    private final Map<PatternElement, EReference>         patElemsToERef            = new HashMap<PatternElement, EReference>();

    private ResourceSet                                   atlResourceSet;

    private ResourceSet                                   resourceSet;

    private LoopUnit createLoopUnit(org.eclipse.emf.henshin.model.Rule rule) {
        LoopUnit loop = henshinFactory.createLoopUnit();
        loop.setName(rule.getName() + "_Iter");
        loop.setSubUnit(rule);

        return loop;
    }

    private List<LoopUnit> createLoopUnits(
            List<org.eclipse.emf.henshin.model.Rule> rules) {
        List<LoopUnit> result = new ArrayList<LoopUnit>(rules.size());
        for (org.eclipse.emf.henshin.model.Rule rule : rules) {
            result.add(createLoopUnit(rule));
        }

        return result;
    }

    private SequentialUnit createSequence(List<? extends Unit> units) {
        SequentialUnit result = henshinFactory.createSequentialUnit();
        result.getSubUnits().addAll(units);

        return result;
    }

    private void analyzeAtlTransformation(Module module)
            throws ATLAnalyserException {
        out("Analyzing ATL module : " + module.getName());

        List<Unit> henshinUnits = henshinModule.getUnits();

        // Create Rules
        final List<org.eclipse.emf.henshin.model.Rule> instantiationRules = createInstantiationRules(module);
        henshinUnits.addAll(instantiationRules);
        final List<org.eclipse.emf.henshin.model.Rule> resolveRules = createResolveRules(module);
        henshinUnits.addAll(resolveRules);
        final List<org.eclipse.emf.henshin.model.Rule> deletionRules = new ArrayList<org.eclipse.emf.henshin.model.Rule>();
        deletionRules.add(createDeleteSourceElementsRule());
        deletionRules.add(createDeleteTracesRule());
        henshinUnits.addAll(deletionRules);

        // Create iterations and sequencing
        final SequentialUnit instantiation = createSequence(createLoopUnits(instantiationRules));
        instantiation.setName("Instantiation");
        henshinUnits.addAll(instantiation.getSubUnits());
        henshinUnits.add(instantiation);

        final SequentialUnit resolving = createSequence(createLoopUnits(resolveRules));
        resolving.setName("Resolving");
        henshinUnits.addAll(resolving.getSubUnits());
        henshinUnits.add(resolving);

        final SequentialUnit cleanup = createSequence(createLoopUnits(deletionRules));
        cleanup.setName("Cleanup");
        henshinUnits.addAll(cleanup.getSubUnits());
        henshinUnits.add(cleanup);

        final SequentialUnit mainSeq = henshinFactory.createSequentialUnit();
        mainSeq.getSubUnits().add(instantiation);
        mainSeq.getSubUnits().add(resolving);
        mainSeq.getSubUnits().add(cleanup);
        mainSeq.setName("Main");
        henshinUnits.add(mainSeq);

        for (ModuleElement el : module.getElements()) {
            if (el instanceof MatchedRule) {
                MatchedRule rule = (MatchedRule) el;

                if (!rule.isIsAbstract()) {
                    // If the rule defines an output pattern

                    // Create a Henshin rule for the matching of the output
                    // pattern
                    // atlRuleToOutPatternRule.put(rule, henshinRules);

                    for (OutPatternElement outPatternEl : rule.getOutPattern()
                            .getElements()) {
                        OclType type = outPatternEl.getType();
                        append(eClassToCreatorRules, type.getName(), rule);
                    }
                } else {
                    LOGGER.debug(String.format("Skipping abstract rule %s",
                            rule.getName()));
                }
            } else if (el instanceof Rule) {
                Rule rule = (Rule) el;
                LOGGER.debug(String.format("Skipping non matched rule %s",
                        rule.getName()));
            }
        }
    }

    private List<org.eclipse.emf.henshin.model.Rule> createResolveRules(
            Module module) {
        List<org.eclipse.emf.henshin.model.Rule> rules = new LinkedList<org.eclipse.emf.henshin.model.Rule>();

        for (ModuleElement el : module.getElements()) {
            if (el instanceof MatchedRule) {
                MatchedRule rule = (MatchedRule) el;

                if (!rule.isIsAbstract()) {
                    // org.eclipse.emf.henshin.model.Rule henshinRule =
                    // createResolveRule(rule);
                    final List<org.eclipse.emf.henshin.model.Rule> resolveRules = createResolveRules(rule);
                    rules.addAll(resolveRules);
                }
            }
        }

        return rules;
    }

    private List<org.eclipse.emf.henshin.model.Rule> createInstantiationRules(
            Module module) throws ATLAnalyserException {
        List<org.eclipse.emf.henshin.model.Rule> rules = new LinkedList<org.eclipse.emf.henshin.model.Rule>();

        for (ModuleElement el : module.getElements()) {
            if (el instanceof MatchedRule) {
                MatchedRule rule = (MatchedRule) el;

                if (!rule.isIsAbstract()) {
                    org.eclipse.emf.henshin.model.Rule henshinRule = createInstantiationRule(rule);
                    rules.add(henshinRule);
                }
            }
        }
        return rules;
    }

    private org.eclipse.emf.henshin.model.Rule createDeleteSourceElementsRule() {
        final org.eclipse.emf.henshin.model.Rule rule = henshinFactory
                .createRule("DeleteAllSourceElements");
        LOGGER.trace("Created rule {}", rule.getName());

        EClass rootSourceMetaclass = (EClass) fromERef.getEType();

        final Node node = henshinFactory.createNode(rule.getLhs(),
                rootSourceMetaclass, null);
        node.setAction(new Action(Action.Type.DELETE));
        rule.setCheckDangling(false);

        return rule;
    }

    private org.eclipse.emf.henshin.model.Rule createDeleteTracesRule() {
        final org.eclipse.emf.henshin.model.Rule rule = henshinFactory
                .createRule("DeleteAllTraces");
        LOGGER.trace("Created rule {}", rule.getName());

        final Node node = henshinFactory.createNode(rule.getLhs(),
                traceBaseEClass, null);
        node.setAction(new Action(Action.Type.DELETE));
        rule.setCheckDangling(false);

        return rule;
    }

    EStructuralFeature getEFeatOfBinding(Binding binding) {
        EClass eClass = resolveOclTypeInPkg(binding.getOutPatternElement()
                .getType(), outputMM);
        String propertyName = binding.getPropertyName();
        EStructuralFeature targetFeature = eClass
                .getEStructuralFeature(propertyName);

        return targetFeature;
    }

    private List<org.eclipse.emf.henshin.model.Rule> createResolveRules(
            MatchedRule atlRule) {
        List<org.eclipse.emf.henshin.model.Rule> result = new ArrayList<org.eclipse.emf.henshin.model.Rule>();

        // We need one resolve rule per binding
        for (OutPatternElement outPatElem : atlRule.getOutPattern()
                .getElements()) {
            for (Binding binding : outPatElem.getBindings()) {
                final org.eclipse.emf.henshin.model.Rule resolveRule = henshinFactory
                        .createRule(atlRule.getName() + "_Res_"
                                + outPatElem.getVarName() + "_"
                                + binding.getPropertyName());
                LOGGER.trace("Created rule {}", resolveRule.getName());
                final Map<PatternElement, Node> patElemToLHSNode = Collections.EMPTY_MAP;
                // initResolveRule(atlRule, resolveRule);
                ResolveRuleBuilder visitor = new ResolveRuleBuilder(this,
                        atlRule, resolveRule, patElemToLHSNode, env);
                visitor.processBinding(binding);

                result.add(resolveRule);
                result.addAll(visitor.getAdditionalRules());
            }
        }

        return result;
    }

    private org.eclipse.emf.henshin.model.Rule createResolveRule(
            MatchedRule rule) {
        org.eclipse.emf.henshin.model.Rule resolveRule = henshinFactory
                .createRule(rule.getName() + "_Res");
        LOGGER.trace("Created rule {}", resolveRule.getName());

        Map<PatternElement, Node> patternElementToLHSNode = Collections.EMPTY_MAP;
        // initResolveRule(rule, resolveRule);

        ResolveRuleBuilder visitor = new ResolveRuleBuilder(this, rule,
                resolveRule, patternElementToLHSNode, env);

        // Create edges between objects instantiated by the
        // ATL rule
        for (OutPatternElement outPatternElement : rule.getOutPattern()
                .getElements()) {
            visitor.processOutPatternElement(outPatternElement);
        }

        createNACfromRHS(resolveRule);

        return resolveRule;
    }

    private Map<PatternElement, Node> initResolveRule(MatchedRule rule,
            org.eclipse.emf.henshin.model.Rule resolveRule) {
        org.eclipse.emf.henshin.model.Rule instantiationRule = atlRule2InstantiationRule
                .get(rule);
        // This is a map from In and Out PatternElements to nodes in the RHS
        // of the instantiation rule.
        Map<PatternElement, Node> patternElementToNode = atlRule2PatternMap
                .get(rule);

        Map<Node, Node> copyMap = new LinkedHashMap<Node, Node>(
                instantiationRule.getRhs().getNodes().size());

        // Copy the instantiation RHS into the resolve LHS. Use rule.create* so
        // that mappings to RHS are created automatically.
        for (Node node : instantiationRule.getRhs().getNodes()) {
            Node resolveNode = resolveRule.createNode(node.getType());
            resolveNode.setName(node.getName());
            copyMap.put(node, resolveNode);
        }
        for (Edge edge : instantiationRule.getRhs().getEdges()) {
            Node resolveSource = copyMap.get(edge.getSource());
            Node resolveTarget = copyMap.get(edge.getTarget());
            Edge resolveEdge = resolveRule.createEdge(resolveSource,
                    resolveTarget, edge.getType());
            resolveEdge.setIndex(edge.getIndex());
        }

        // The RHS of the instantiation rule will be copied into the LHS of the
        // resolve rule. The following is a map from In and Out PatternElements
        // to copied nodes in the resolve rule LHS.
        Map<PatternElement, Node> patternElementToCopiedNode = new HashMap<PatternElement, Node>(
                patternElementToNode.size());

        for (Map.Entry<PatternElement, Node> entry : patternElementToNode
                .entrySet()) {
            patternElementToCopiedNode.put(entry.getKey(),
                    copyMap.get(entry.getValue()));
        }
        return patternElementToCopiedNode;
    }

    private org.eclipse.emf.henshin.model.Rule createInstantiationRule(
            MatchedRule rule) throws ATLAnalyserException {
        org.eclipse.emf.henshin.model.Rule instantiationRule = henshinFactory
                .createRule(rule.getName() + "_Inst");
        LOGGER.trace("Created rule {}", instantiationRule.getName());

        List<InPatternElement> inElements = mergeInPatternElementsAcrossInheritance(rule);

        Map<PatternElement, Node> patternElementToNode = new HashMap<PatternElement, Node>(
                inElements.size() + rule.getOutPattern().getElements().size());
        atlRule2PatternMap.put(rule, patternElementToNode);

        MappingList ruleMappings = instantiationRule.getMappings();
        Graph rhs = instantiationRule.getRhs();

        EClass traceEClass;
        if (STRONG_TYPED_TRACES) {
            // Create a typed trace EClass
            traceEClass = EcoreFactory.eINSTANCE.createEClass();
            traceEClass.setName(rule.getName() + "_Trace");
            traceEClass.getESuperTypes().add(traceBaseEClass);
            traceEPkg.getEClassifiers().add(traceEClass);

            atlRuleToTraceEClass.put(rule, traceEClass);
        } else {
            traceEClass = traceBaseEClass;
        }

        // create a NAC forbidding the existance of a trace node
        final NestedCondition nac = instantiationRule.getLhs().createNAC(
                "ApplyOncePerMatch");
        final Graph nacConcl = nac.getConclusion();
        final Node nacTrace = henshinFactory.createNode(nacConcl,
                traceBaseEClass, null);

        // Create the trace node and create an EReference and a graph edge for
        // each in pattern of the rule.
        Node traceNode = henshinFactory.createNode(rhs, traceEClass, "trace");

        Map<String, Node> varName2Node = new HashMap<String, Node>();
        int i = 0;
        for (InPatternElement inPatternElement : inElements) {
            String varName = inPatternElement.getVarName();
            OclType oclVarType = inPatternElement.getType();
            EClass varType = resolveOclTypeInPkg(oclVarType, inputMM);

            Node node = instantiationRule.createNode(varType);
            node.setName(varName);

            varName2Node.put(varName, node);

            // create node image in NAC and a "from" ref from the trace in NAC
            final Node nacNode = henshinFactory.createNode(nacConcl, varType,
                    varName);
            nac.getMappings().add(node, nacNode);
            henshinFactory.createEdge(nacTrace, nacNode, fromERef).setIndex(
                    Integer.toString(i));

            // find node image in RHS
            Node rhsNode = ruleMappings.getImage(node, rhs);
            rhsNode.setName(varName);
            patternElementToNode.put(inPatternElement, rhsNode);

            henshinFactory.createEdge(traceNode, rhsNode, fromERef).setIndex(
                    Integer.toString(i));

            if (STRONG_TYPED_TRACES) {
                EReference eRef = EcoreFactory.eINSTANCE.createEReference();
                eRef.setName(node.getName());
                eRef.setEType(node.getType());
                eRef.setLowerBound(1);
                eRef.setUpperBound(1);

                traceEClass.getEStructuralFeatures().add(eRef);

                patElemsToERef.put(inPatternElement, eRef);

                henshinFactory.createEdge(traceNode, rhsNode, eRef);
            }

            // add inherited InPatternElements to the map,
            // mapping them too to the rhs node
            List<InPatternElement> allSuperInPatternElements = env
                    .getAllSuperInPatternElements(inPatternElement);
            for (InPatternElement superInPatternElement : allSuperInPatternElements) {
                patternElementToNode.put(superInPatternElement, rhsNode);
            }

            i++;
        }

        NestedCondition nacNac = nacConcl.createNAC("ExactMatch");
        Graph nacNacConcl = nacNac.getConclusion();
        Node nacNacTrace = henshinFactory.createNode(nacNacConcl,
                traceBaseEClass, null);
        nacNac.getMappings().add(nacTrace, nacNacTrace);
        Node extraFrom = henshinFactory.createNode(nacNacConcl, sourceBaseType,
                null);
        henshinFactory.createEdge(nacNacTrace, extraFrom, fromERef);

        processFromExpr(rule, instantiationRule, varName2Node);

        List<OutPatternElement> outElements = mergeOutPatternElementsAcrossInheritance(rule);

        Map<PatternElement, Node> patternElementToNode1 = atlRule2PatternMap
                .get(rule);

        // Create nodes for objects instantiated by the rule
        int j = 0;
        for (OutPatternElement outPatternElement : outElements) {
            EClass eClass = resolveOclTypeInPkg(outPatternElement.getType(),
                    outputMM);

            Node node = henshinFactory.createNode(rhs, eClass,
                    outPatternElement.getVarName());
            patternElementToNode1.put(outPatternElement, node);
            henshinFactory.createEdge(traceNode, node, toERef).setIndex(
                    Integer.toString(j));

            if (STRONG_TYPED_TRACES) {
                EReference eRef = EcoreFactory.eINSTANCE.createEReference();
                eRef.setName(node.getName());
                eRef.setEType(node.getType());
                eRef.setLowerBound(1);
                eRef.setUpperBound(1);

                traceEClass.getEStructuralFeatures().add(eRef);

                patElemsToERef.put(outPatternElement, eRef);

                henshinFactory.createEdge(traceNode, node, eRef);
            }

            j++;
        }

        atlRule2InstantiationRule.put(rule, instantiationRule);

        return instantiationRule;
    }

    private void createNACfromRHS(
            org.eclipse.emf.henshin.model.Rule instantiationRule) {
        if (WITHOUT_NACS)
            return;
        // Create a NAC identical to the RHS so that the rule gets applied once
        // for each match
        NestedCondition nac = instantiationRule.getLhs().createNAC("Once");

        // graphCopier will serve as a map between original and new copies
        Copier graphCopier = new Copier();
        Graph conclusion = (Graph) graphCopier.copy(instantiationRule.getRhs());
        graphCopier.copyReferences();

        nac.setConclusion(conclusion);

        for (Mapping mapping : instantiationRule.getMappings()) {
            nac.getMappings().add(mapping.getOrigin(),
                    (GraphElement) graphCopier.get(mapping.getImage()));
        }
    }

    private List<OutPatternElement> mergeOutPatternElementsAcrossInheritance(
            Rule rule) throws ATLAnalyserException {
        Map<String, OutPatternElement> result = new LinkedHashMap<String, OutPatternElement>();

        // add OutPatternElements of the rule
        for (OutPatternElement outPatternElement : rule.getOutPattern()
                .getElements()) {
            result.put(outPatternElement.getVarName(), outPatternElement);
        }

        // iterate inherited OutPatternElements
        for (OutPatternElement superPatElem : env
                .getAllSuperOutPatternElements(rule)) {

            // if the OutPatternElement name is already in the result, keep the
            // child one.
            if (result.containsKey(superPatElem.getVarName())) {
                Rule superRule = superPatElem.getOutPattern().getRule();
                OutPatternElement existingPatElem = result.get(superPatElem
                        .getVarName());
                Rule existingRule = existingPatElem.getOutPattern().getRule();

                if (env.isSuperRuleOf(existingRule, superRule)) {
                    // replace pattern with child one
                    result.put(superPatElem.getVarName(), superPatElem);
                } else if (env.isSuperRuleOf(superRule, existingRule)) {
                    // do nothing
                } else {
                    throw new ATLAnalyserException(String.format(
                            "Impossible case of 2 rules mutually"
                                    + " super-rules of each other: %s and %s",
                            existingRule.getName(), superRule.getName()));
                }
            } else {
                result.put(superPatElem.getVarName(), superPatElem);
            }
        }

        return new ArrayList<OutPatternElement>(result.values());
    }

    private List<InPatternElement> mergeInPatternElementsAcrossInheritance(
            MatchedRule rule) throws ATLAnalyserException {
        Map<String, InPatternElement> result = new LinkedHashMap<String, InPatternElement>();

        // add InPatternElements of the rule
        for (InPatternElement InPatternElement : rule.getInPattern()
                .getElements()) {
            result.put(InPatternElement.getVarName(), InPatternElement);
        }

        // iterate inherited InPatternElements
        for (InPatternElement superPatElem : env
                .getAllSuperInPatternElements(rule)) {

            // if the InPatternElement name is already in the result, keep the
            // child one.
            if (result.containsKey(superPatElem.getVarName())) {
                Rule superRule = superPatElem.getInPattern().getRule();
                InPatternElement existingPatElem = result.get(superPatElem
                        .getVarName());
                Rule existingRule = existingPatElem.getInPattern().getRule();

                if (env.isSuperRuleOf(existingRule, superRule)) {
                    // replace pattern with child one
                    result.put(superPatElem.getVarName(), superPatElem);
                } else if (env.isSuperRuleOf(superRule, existingRule)) {
                    // do nothing
                } else {
                    throw new ATLAnalyserException(String.format(
                            "Impossible case of 2 rules mutually"
                                    + " super-rules of each other: %s and %s",
                            existingRule.getName(), superRule.getName()));
                }
            } else {
                result.put(superPatElem.getVarName(), superPatElem);
            }
        }

        return new ArrayList<InPatternElement>(result.values());
    }

    private void processFromExpr(MatchedRule rule,
            org.eclipse.emf.henshin.model.Rule instantiationRule,
            Map<String, Node> varName2Node) throws ATLAnalyserException {
        OclExpression guard = rule.getInPattern().getFilter();

        if (guard == null) {
            return;
        }

        guard = inlineAllHelperCalls(guard);

        OCL2NGC ocl2Ngc = new OCL2NGC(this, instantiationRule.getLhs(),
                varName2Node);
        Formula ac = ocl2Ngc.translateOcl2Ngc(guard);

        if (ac != null) {
            instantiationRule.getLhs().setFormula(
                    NGCUtils.createConjunction(ac, instantiationRule.getLhs()
                            .getFormula()));
        }

        // final String guardString = (new FullOCLExpressionPrinter(this))
        // .doSwitch(guard);
        //
        // final AttributeCondition attCond = henshinFactory
        // .createAttributeCondition();
        // attCond.setName("Guard");
        // attCond.setConditionText("// " + guardString);
        // instantiationRule.getAttributeConditions().add(attCond);

        // TODO transform OCL expression into either
        // edges in the rule lhs or
        // PACs and NACs.
        // (new OclExpressionVisitor(rule, res)).doSwitch(expression);
    }

    public EClass resolveOclType(String metaclassName) {
        for (EPackage ePkg : ePackages) {
            EClass res = EPackageUtils.findEClassRecursively(ePkg,
                    metaclassName);
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    EClass resolveOclTypeInPkg(String metaclassName, EPackage pkg) {
        return EPackageUtils.findEClassRecursively(pkg, metaclassName);
    }

    EClass resolveOclTypeInPkg(OclType oclType, EPackage pkg) {
        return resolveOclTypeInPkg(oclType.getName(), pkg);
    }

    EClass resolveOclTypeInInputMM(OclType oclType) {
        EClass res = resolveOclTypeInPkg(oclType.getName(), inputMM);
        return res;
    }

    EClass resolveOclTypeInOutputMM(OclType oclType) {
        EClass res = resolveOclTypeInPkg(oclType.getName(), outputMM);
        return res;
    }

    EClass resolveOclType(OclType oclType) {
        EClass res = resolveOclType(oclType.getName());
        return res;
    }

    private String toString(Collection<Rule> creatorCombination) {
        List<String> ruleNames = new ArrayList<String>(
                creatorCombination.size());
        for (Rule rule : creatorCombination) {
            ruleNames.add(rule.getName());
        }
        return Joiner.on(", ").join(ruleNames);
    }

    private boolean isInstantiation(Rule rule, Node patternNode) {
        // It's an instantiation if the rule contains the patternNode in its
        // output pattern. Otherwise it's a resolved object.
        for (OutPatternElement patElem : rule.getOutPattern().getElements()) {
            if (patElem.getVarName().equals(patternNode.getName())) {
                return true;
            }
        }
        return false;
    }

    private OclExpression inlineAllHelperCalls(OclExpression exp) {
        OCLContainmentVisitor<Object> inlineVisitor = new OCLContainmentVisitor<Object>() {
            @Override
            public Object defaultCase(EObject object) {
                return object;
            }

            @Override
            public OclExpression caseOperationCallExp(OperationCallExp opCall) {
                OclExpression res = opCall;
                final String operationName = opCall.getOperationName();

                final Module atlModule = (Module) EcoreUtil
                        .getRootContainer(opCall);
                final Helper helper = resolveHelper(null, operationName,
                        atlModule);
                if (helper != null) {
                    res = inlineCallToHelper(opCall, helper);
                    res = inlineAllHelperCalls(res);
                }

                return res;
            }

            @Override
            public OclExpression caseNavigationOrAttributeCallExp(
                    NavigationOrAttributeCallExp navExp) {
                OclExpression res = navExp;
                final String navigatedProperty = navExp.getName();
                final Module atlModule = (Module) EcoreUtil
                        .getRootContainer(navExp);
                final Helper helper = resolveHelper(null, navigatedProperty,
                        atlModule);
                if (helper != null) {
                    res = inlineCallToHelper(navExp, helper);
                    res = inlineAllHelperCalls(res);
                }

                return res;
            }
        };

        return (OclExpression) inlineVisitor.doSwitch(exp);
    }

    @SuppressWarnings("rawtypes")
    static OclExpression inlineCallToHelper(PropertyCallExp navExp,
            Helper foundHelper) {
        final EObject eContainer = navExp.eContainer();
        final EStructuralFeature eContainingFeature = navExp
                .eContainingFeature();
        int index = 0;
        if (eContainingFeature.isMany()) {
            index = ((EList) eContainer.eGet(eContainingFeature))
                    .indexOf(navExp);
        }

        final OclExpression source = navExp.getSource();
        OclFeature feat = foundHelper.getDefinition().getFeature();

        OclExpression initExpression = null;
        if (feat instanceof fr.tpt.atlanalyser.atl.OCL.Attribute) {
            fr.tpt.atlanalyser.atl.OCL.Attribute att = (fr.tpt.atlanalyser.atl.OCL.Attribute) feat;
            initExpression = att.getInitExpression();
        } else if (feat instanceof fr.tpt.atlanalyser.atl.OCL.Operation) {
            fr.tpt.atlanalyser.atl.OCL.Operation op = (fr.tpt.atlanalyser.atl.OCL.Operation) feat;
            initExpression = op.getBody();
        } else {
            unsupported("error");
        }

        EcoreUtil.Copier copier = new EcoreUtil.Copier(true, true);

        // Copy the init expression of the helper
        final OclExpression inlinedExpression = (OclExpression) copier
                .copy(initExpression);
        copier.copyReferences();

        // Inline the expression in place of the operation call
        EcoreUtil.replace(navExp, inlinedExpression);

        // Substitute all 'self' VariableExps with
        // object.getSource()
        OCLSwitch<Boolean> substituteSelf = new OCLContainmentVisitor<Boolean>() {
            public Boolean caseVariableExp(VariableExp object) {
                final VariableDeclaration referredVariable = object
                        .getReferredVariable();
                if (referredVariable == null
                        || referredVariable.getVarName().equals("self")) {

                    final OclExpression sourceCopy = CopierPreservingReferences
                            .copy(source);
                    EcoreUtil.replace(object, sourceCopy);
                }

                return null;
            };
        };
        substituteSelf.doSwitch(inlinedExpression);

        if (eContainingFeature.isMany()) {
            return (OclExpression) ((EList) eContainer.eGet(eContainingFeature))
                    .get(index);
        } else {
            return (OclExpression) eContainer.eGet(eContainingFeature);
        }
    }

    Helper resolveHelper(final EClass callingContext, final String callName,
            Module atlModule) {
        final EList<ModuleElement> elements = atlModule.getElements();
        Helper foundHelper = null;
        for (ModuleElement elem : elements) {
            if (elem instanceof Helper) {
                Helper helper = (Helper) elem;
                final OclFeature feature = helper.getDefinition().getFeature();
                String helperName = (String) feature.eGet(feature.eClass()
                        .getEStructuralFeature("name"));

                if (!callName.equals(helperName)) {
                    continue;
                }

                final OclContextDefinition contxt = helper.getDefinition()
                        .getContext_();

                // Check that the contexts comply. If callingContext is null, we
                // do not check anything. This is to allow only name based
                // resolving.
                if ((callingContext != null && contxt != null)) {
                    final OclType contextType = contxt.getContext_();
                    final EClass contextEClass = resolveOclType(contextType);

                    if (!contextEClass.isSuperTypeOf(callingContext)) {
                        continue;
                    }
                } else if (callingContext != null) {
                    continue;
                }

                if (feature instanceof fr.tpt.atlanalyser.atl.OCL.Attribute
                        || feature instanceof fr.tpt.atlanalyser.atl.OCL.Operation) {
                    foundHelper = helper;
                    break;
                } else {
                    unsupported("Non-attribute helpers not yet supported.");
                }
            }
        }
        return foundHelper;
    }

    private static void error(String msg) throws ATLAnalyserException {
        LOGGER.error(msg);
        throw new ATLAnalyserException(msg);
    }

    private static void unsupported(String msg) {
        LOGGER.error(msg);
        throw new UnsupportedOperationException(msg);
    }

}
