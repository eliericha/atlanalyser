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
package fr.tpt.atlanalyser.examples;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.DiagnosticException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.IOWrappedException;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.DanglingHREFException;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.henshin.interpreter.ApplicationMonitor;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.RuleApplication;
import org.eclipse.emf.henshin.interpreter.UnitApplication;
import org.eclipse.emf.henshin.interpreter.impl.AssignmentImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.interpreter.util.InterpreterUtil;
import org.eclipse.emf.henshin.model.ConditionalUnit;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.HenshinPackage;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceFactory;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.model.util.HenshinSwitch;
import org.eclipse.m2m.atl.emftvm.EmftvmFactory;
import org.eclipse.m2m.atl.emftvm.ExecEnv;
import org.eclipse.m2m.atl.emftvm.Metamodel;
import org.eclipse.m2m.atl.emftvm.Model;
import org.eclipse.m2m.atl.emftvm.ModelDeclaration;
import org.eclipse.m2m.atl.emftvm.impl.resource.EMFTVMResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.util.ExecEnvPool;
import org.javatuples.Triplet;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fr.tpt.atlanalyser.ATLAnalyserException;
import fr.tpt.atlanalyser.atl2agt.ATL2Henshin;
import fr.tpt.atlanalyser.post2pre.Post2Pre4ATL;
import fr.tpt.atlanalyser.utils.EPackageUtils;
import fr.tpt.atlanalyser.utils.Utils;

public class ExampleRunner {

    private static final HenshinFactory  HF                   = HenshinFactory.eINSTANCE;

    // public static void main(String[] args) throws IOException,
    // atlanalyserException {
    // ExampleRunner ex = new ExampleRunner("examples/SimpleCMG");
    // ex.compileAndTransform();
    // Resource atlRes = ex.applyAtlTransformation(
    // "examples/SimpleCMG/InputModels/in1.xmi",
    // "examples/SimpleCMG/ATLOutput/in1_atl.xmi");
    // Resource henshinRes = ex.applyHenshinTransformation(
    // "examples/SimpleCMG/InputModels/in1.xmi",
    // "examples/SimpleCMG/HenshinOutput/in1_henshin.xmi");
    // ex.compareModels(atlRes, henshinRes);
    // }

    protected static final Logger        LOGGER               = LogManager
                                                                      .getLogger(ExampleRunner.class
                                                                              .getSimpleName());

    protected final File                 baseDir;
    protected final File                 inputMMDir;
    protected final File                 outputMMDir;
    protected final File                 transformationDir;
    protected final File                 inputDir;
    protected final File                 atlOutputDir;
    protected final File                 henshinOutputDir;
    protected final ATL2Henshin          atl2Henshin;
    protected final HenshinResourceSet   resourceSet;
    private File                         compiledTransfo;
    private static final EmftvmFactory   emftvmFactory        = EmftvmFactory.eINSTANCE;

    private static final boolean         IGNORE_EREF_ORDERING = false;
    protected EPackage                   inputMM;
    protected EPackage                   outputMM;
    private File                         atlTransformation;

    protected int                        jobs;

    protected static Map<Object, Object> xmiSaveOptions;

    static {
        xmiSaveOptions = Maps.newHashMap();
        xmiSaveOptions.put(XMIResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
        // xmiSaveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF,
        // XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
    }

    public ExampleRunner(String baseDir) {
        this.baseDir = new File(baseDir);
        this.inputMMDir = new File(this.baseDir, "InputMM");
        this.outputMMDir = new File(this.baseDir, "OutputMM");
        this.transformationDir = new File(this.baseDir, "Transformation");
        this.inputDir = new File(this.baseDir, "InputModels");
        this.atlOutputDir = new File(this.baseDir, "ATLOutput");
        this.henshinOutputDir = new File(this.baseDir, "HenshinOutput");
        this.resourceSet = new HenshinResourceSet(this.baseDir.getPath());
        Map<String, Object> extensionToFactoryMap = this.resourceSet
                .getResourceFactoryRegistry().getExtensionToFactoryMap();
        extensionToFactoryMap.put(Resource.Factory.Registry.DEFAULT_EXTENSION,
                new XMIResourceFactoryImpl());
        extensionToFactoryMap.put("ecore", new EcoreResourceFactoryImpl());
        extensionToFactoryMap.put("emftvm", new EMFTVMResourceFactoryImpl());
        extensionToFactoryMap.put("henshin", new HenshinResourceFactory());

        EPackage.Registry.INSTANCE.put(HenshinPackage.eNS_URI,
                HenshinPackage.eINSTANCE);

        this.atl2Henshin = new ATL2Henshin(resourceSet);
    }

    public ExampleRunner(String baseDir2, int jobs) {
        this(baseDir2);
        this.jobs = jobs;
    }

    public void executePost2Pre(File postFile) throws IOException {
        executePost2Pre(postFile, 1);
    }

    public void executePost2Pre(File postFile, int maxNumRuleIterations)
            throws IOException {
        // checkDirs();
        inputMM = getInputMMEPkg();
        outputMM = getOutputMMEPkg();
        makeAbstractClassesInstantiable(inputMM);
        makeAbstractClassesInstantiable(outputMM);
        Module transfo = loadHenshinTransformation();

        EcoreUtil.resolveAll(transfo);

        EPackage traceMM = resourceSet.getPackageRegistry().getEPackage(
                "http://traces/1.0");

        Resource postRes = resourceSet.getResource(
                URI.createFileURI(postFile.getCanonicalPath()), true);
        Module postModule = (Module) postRes.getContents().get(0);
        Module preModule = HF.createModule();
        preModule.setName("Preconditions");

        EList<EPackage> imports = preModule.getImports();
        imports.add(inputMM);
        imports.add(outputMM);
        imports.add(EcorePackage.eINSTANCE);

        LOGGER.info("Starting Post2Pre for {}", transfo.getName());

        ScheduledExecutorService memMonitorExecutor = Executors
                .newScheduledThreadPool(1);
        memMonitorExecutor.scheduleAtFixedRate(new Runnable() {

            private final MemoryMXBean memoryBean = ManagementFactory
                                                          .getMemoryMXBean();
            private final Logger       LOGGER     = LogManager
                                                          .getLogger("MemMon");

            @Override
            public void run() {
                // LOGGER.setAdditivity(false);

                // for (Enumeration iterator =
                // AGGWrapper.LOGGER.getAllAppenders(); iterator
                // .hasMoreElements();) {
                // Appender appender = (Appender) iterator.nextElement();
                // LOGGER.addAppender(appender);
                // }

                MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
                final long used = heapUsage.getUsed();
                double usedGB = (double) used / (1 << 30);
                final long max = heapUsage.getMax();
                double maxGB = (double) max / (1 << 30);
                LOGGER.info(String.format(
                        "Memory use : %6.3fGB of %6.3fGB (%.2f%%)", usedGB,
                        maxGB, (float) used / max * 100));
            }
        }, 0, 10, TimeUnit.SECONDS);

        boolean allowReordering = true;
        boolean enableValidators = true;
        Post2Pre4ATL post2Pre = new Post2Pre4ATL(transfo, inputMM, outputMM,
                traceMM, jobs, allowReordering, enableValidators);

        try {
            EList<Unit> units = postModule.getUnits();
            for (Unit unit : units) {
                if (unit instanceof Rule) {
                    Rule postRule = (Rule) unit;

                    // Store copy of post in result file
                    Rule copy = EcoreUtil.copy(postRule);
                    // copy.setName("Post");
                    preModule.getUnits().add(copy);

                    Formula formula = postRule.getLhs().getFormula();
                    Formula pre = post2Pre.post2Pre(formula,
                            maxNumRuleIterations);

                    Rule preRule = HF.createRule(postRule.getName());
                    preRule.setName(String.format(
                            "wlp\\left( SimpleATL_{\\leq1}, %s \\right)",
                            postRule.getName()));
                    Graph preLhs = HF.createGraph();
                    preLhs.setFormula(EcoreUtil.copy(pre));
                    preRule.setLhs(preLhs);
                    preModule.getUnits().add(preRule);

                    createEvalUnit(preModule, preRule);
                }
            }
        } finally {
            memMonitorExecutor.shutdown();
        }

        File outputDir = new File(baseDir, "Preconditions");
        if (!outputDir.isDirectory()) {
            outputDir.mkdir();
        }

        String preFileName = postFile.getName() + "_pre.henshin";
        File preFile = new File(new File(this.baseDir, "Preconditions"),
                preFileName);
        Resource outputRes = resourceSet.createResource(URI
                .createFileURI(preFile.getAbsolutePath()));

        outputRes.getContents().add(preModule);

        LOGGER.info("Writing Precondition in {}", outputRes.getURI().toString());
        outputRes.save(xmiSaveOptions);

        File refFile = new File(new File(this.baseDir, "Preconditions_Ref"),
                preFileName);
        URI preRefFile = URI.createFileURI(refFile.getAbsolutePath());
        LOGGER.info("Comparing to ref Precondition {}", preRefFile.toString());
        try {
            Resource refResource = resourceSet.getResource(preRefFile, true);
            compareModels(refResource, outputRes);
        } catch (WrappedException ex) {
            // ref file does not exist
            LOGGER.warn("Ref Precondition {} does not exist",
                    preRefFile.toString());
        }

        LOGGER.info("Done!");
    }

    public ConditionalUnit createEvalUnit(Module preModule, Rule preRule) {
        ConditionalUnit condUnit = HF.createConditionalUnit();
        condUnit.setName("Eval " + preRule.getName());
        condUnit.setIf(preRule);

        Rule preSatisfiedRule = HF.createRule();
        preSatisfiedRule.setName("PreSat");
        Node eval = HF.createNode(preSatisfiedRule.getRhs(),
                EcorePackage.Literals.ECLASS, "eval");
        HF.createAttribute(eval, EcorePackage.Literals.ENAMED_ELEMENT__NAME,
                "\"Pre Satisfied\"");
        preModule.getUnits().add(preSatisfiedRule);

        condUnit.setThen(preSatisfiedRule);

        Rule preUnsatisfiedRule = HF.createRule();
        preUnsatisfiedRule.setName("PreUnsat");
        eval = HF.createNode(preUnsatisfiedRule.getRhs(),
                EcorePackage.Literals.ECLASS, "eval");
        HF.createAttribute(eval, EcorePackage.Literals.ENAMED_ELEMENT__NAME,
                "\"Pre Unsatisfied\"");
        preModule.getUnits().add(preUnsatisfiedRule);
        condUnit.setElse(preUnsatisfiedRule);

        preModule.getUnits().add(0, condUnit);

        return condUnit;
    }

    protected void makeAbstractClassesInstantiable(EPackage pkg) {
        TreeIterator<EObject> eAllContents = pkg.eAllContents();
        for (TreeIterator<EObject> iterator = eAllContents; iterator.hasNext();) {
            EObject obj = iterator.next();
            if (obj instanceof EClass) {
                EClass eClass = (EClass) obj;
                if (eClass.isAbstract()) {
                    eClass.setAbstract(false);
                }
            }
        }
    }

    public void compileAndTransform() throws IOException, ATLAnalyserException {
        // checkDirs();
        LOGGER.info("Loading input metamodel");

        inputMM = getInputMMEPkg();
        assertNotNull(inputMM);
        atl2Henshin.registerInputMetamodel(inputMM);

        LOGGER.info("Loading output metamodel");

        outputMM = getOutputMMEPkg();
        assertNotNull(outputMM);
        atl2Henshin.registerOutputMetamodel(outputMM);

        LOGGER.info("Translating ATL transformation to Henshin");

        atlTransformation = transformationDir
                .listFiles((FilenameFilter) new SuffixFileFilter(".atl"))[0];

        File henshinTransformation = new File(
                atlTransformation.getParentFile(),
                FilenameUtils.getBaseName(atlTransformation.getPath())
                        + ".henshin");

        File tracesMM = new File(atlTransformation.getParentFile(),
                FilenameUtils.getBaseName(atlTransformation.getPath())
                        + ".traces.ecore");

        deleteIfExists(henshinTransformation);
        deleteIfExists(tracesMM);

        Module henshinModule = atl2Henshin
                .translateToHenshin(atlTransformation);

        assertNotNull(henshinModule);
        assertTrue(
                "Resulting module contains no rules!",
                EcoreUtil.getObjectsByType(henshinModule.getUnits(),
                        HenshinPackage.Literals.RULE).size() > 0);

        LOGGER.info("Writing Henshin transformation to {}",
                henshinTransformation.getPath());

        Resource henshinRes = resourceSet.createResource(URI
                .createFileURI(henshinTransformation.getCanonicalPath()));
        henshinRes.getContents().add(henshinModule);

        Resource tracesRes = resourceSet.createResource(URI
                .createFileURI(tracesMM.getCanonicalPath()));
        tracesRes.getContents().add(atl2Henshin.getTraceMM());

        tracesRes.save(xmiSaveOptions);

        try {
            henshinRes.save(xmiSaveOptions);
        } catch (IOWrappedException ex) {
            Throwable cause = ex.getCause();
            if (cause != null && cause instanceof DanglingHREFException) {
                List<Triplet<EObject, EReference, EObject>> danglingObjects = Utils
                        .getDanglingHrefs(henshinRes);

                String join = Joiner.on("\n").join(danglingObjects);
                System.out.println(join);
            } else {
                ex.printStackTrace();
            }
        }
    }

    protected static void deleteIfExists(File file) {
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    /**
     * 
     * @param inputModelPath
     * @param outputModelPath
     * @return Resource containing the result of the transformation
     * @throws IOException
     */
    public Resource applyAtlTransformation(String inputModelPath,
            String outputModelPath) throws IOException {
        LOGGER.info("Executing ATL Transformation");
        LOGGER.info("    input: {}", inputModelPath);
        LOGGER.info("   output: {}", outputModelPath);

        ExecEnvPool pool = new ExecEnvPool();
        ExecEnv execEnv = pool.getExecEnv();

        compiledTransfo = transformationDir
                .listFiles((FilenameFilter) new SuffixFileFilter(".emftvm"))[0];

        assertTrue(compiledTransfo.isFile());

        PathModuleResolver resolver = new PathModuleResolver();
        org.eclipse.m2m.atl.emftvm.Module emftvmModule = resolver
                .resolveModule(compiledTransfo.getPath());

        ModelDeclaration inModelDecl = emftvmModule.getInputModels().get(0);
        ModelDeclaration outModelDecl = emftvmModule.getOutputModels().get(0);

        Metamodel inputMetamodel = emftvmFactory.createMetamodel();
        inputMetamodel.setResource(getInputMMEPkg().eResource());
        execEnv.registerMetaModel(inModelDecl.getMetaModelName(),
                inputMetamodel);

        Metamodel outputMetamodel = emftvmFactory.createMetamodel();
        outputMetamodel.setResource(getOutputMMEPkg().eResource());
        execEnv.registerMetaModel(outModelDecl.getMetaModelName(),
                outputMetamodel);

        execEnv.loadModule(resolver, compiledTransfo.getPath());

        Model inModel = emftvmFactory.createModel();
        File inFile = new File(inputModelPath);
        Resource inRes = resourceSet.createResource(URI.createFileURI(inFile
                .getCanonicalPath()));
        inRes.load(Collections.emptyMap());
        inModel.setResource(inRes);
        execEnv.registerInputModel(inModelDecl.getModelName(), inModel);

        Model outModel = emftvmFactory.createModel();
        File outFile = new File(outputModelPath);
        outModel.setResource(resourceSet.createResource(URI
                .createFileURI(outFile.getCanonicalPath())));
        execEnv.registerOutputModel(outModelDecl.getModelName(), outModel);

        execEnv.run(null);

        outModel.getResource().save(xmiSaveOptions);

        return outModel.getResource();
    }

    public Module loadHenshinTransformation() throws IOException {
        getTracesMM();

        File henshinFile = transformationDir
                .listFiles((FilenameFilter) new SuffixFileFilter(".henshin"))[0];

        Module henshinModule = (Module) resourceSet
                .getResource(URI.createFileURI(henshinFile.getCanonicalPath()),
                        true).getContents().get(0);

        return henshinModule;
    }

    private EPackage getTracesMM() throws IOException {
        File[] listFiles = transformationDir
                .listFiles((FilenameFilter) new SuffixFileFilter(
                        ".traces.ecore"));
        EPackage traceMM = null;
        if (listFiles.length > 0) {
            File tracesFile = listFiles[0];

            traceMM = (EPackage) resourceSet
                    .getResource(
                            URI.createFileURI(tracesFile.getCanonicalPath()),
                            true).getContents().get(0);

            String[] extraURIs = { URI.createFileURI(
                    tracesFile.getCanonicalPath()).toString() };
            EPackageUtils.registerPackageAndAllSubPackages(traceMM,
                    resourceSet.getPackageRegistry(), extraURIs);
        }

        return traceMM;
    }

    public Resource applyHenshinTransformation(String inputModelPath,
            String outputModelPath) throws FileNotFoundException, IOException {
        LOGGER.info("Executing Henshin Transformation");
        LOGGER.info("    input: {}", inputModelPath);
        LOGGER.info("   output: {}", outputModelPath);

        File inFile = new File(inputModelPath);
        Resource inputRes = resourceSet.getResource(
                URI.createFileURI(inFile.getCanonicalPath()), true);
        Engine engine = new EngineImpl();

        Module henshinModule = loadHenshinTransformation();
        Unit mainUnit = henshinModule.getUnit("Main");

        ApplicationMonitor monitor = new ApplicationMonitor() {

            @Override
            public void notifyUndo(UnitApplication application, boolean succeess) {
                // TODO Auto-generated method stub

            }

            @Override
            public void notifyRedo(UnitApplication application, boolean success) {
                // TODO Auto-generated method stub

            }

            @Override
            public void notifyExecute(UnitApplication application,
                    boolean success) {
                String name = application.getUnit().toString();
                if (success) {
                    LOGGER.debug("  Applied {}", name);
                    if (application instanceof RuleApplication) {
                        RuleApplication ruleApp = (RuleApplication) application;
                        LOGGER.debug("    {}", ruleApp.getCompleteMatch()
                                .toString());
                    }
                } else {
                    LOGGER.debug("  Failed to apply {}", name);
                }

                EGraph graph = application.getEGraph();
                graph.size();
                // if (application instanceof RuleApplication || success
                // && name.contains("i1, j1")) {
                // LOGGER.debug("   Applied {} with match\n{}", name,
                // ((RuleApplication) application).getCompleteMatch());
                // } else {
                // LOGGER.debug("   Applied {}", name);
                // }
            }

            @Override
            public boolean isUndo() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isCanceled() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void cancelAndUndo() {
                // TODO Auto-generated method stub

            }

            @Override
            public void cancel() {
                // TODO Auto-generated method stub

            }
        };

        InterpreterUtil.applyToResource(new AssignmentImpl(mainUnit), engine,
                inputRes, monitor);

        File outFile = new File(outputModelPath);
        Resource outputRes = resourceSet.createResource(URI
                .createFileURI(outFile.getCanonicalPath()));
        outputRes.getContents().addAll(inputRes.getContents());

        outputRes.save(xmiSaveOptions);

        return outputRes;
    }

    public void compareModels(Resource left, Resource right) throws IOException {
        LOGGER.info("Comparing output models");
        IEObjectMatcher matcher = DefaultMatchEngine
                .createDefaultEObjectMatcher(UseIdentifiers.NEVER);
        IComparisonFactory comparisonFactory = new DefaultComparisonFactory(
                new DefaultEqualityHelperFactory());
        IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl(
                matcher, comparisonFactory);
        matchEngineFactory.setRanking(20);
        IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
        matchEngineRegistry.add(matchEngineFactory);
        EMFCompare comparator = EMFCompare.builder()
                .setMatchEngineFactoryRegistry(matchEngineRegistry).build();

        IComparisonScope scope = EMFCompare.createDefaultScope(left, right);
        Comparison comp = comparator.compare(scope);

        File rightFile = new File(right.getURI().path());
        File diffFile = new File(rightFile.getParentFile(),
                FilenameUtils.getBaseName(rightFile.getPath()) + ".diff");
        Resource diffRes = resourceSet.createResource(URI
                .createFileURI(diffFile.getPath()));

        LOGGER.info("Writing diff to {}", diffFile.getPath());

        diffRes.getContents().add(comp);
        diffRes.save(xmiSaveOptions);

        EList<Diff> differences = comp.getDifferences();

        if (IGNORE_EREF_ORDERING) {
            for (Diff diff : differences) {
                assertTrue("Found non-move diff: " + diff.toString(),
                        diff.getKind() == DifferenceKind.MOVE);
            }
        } else {
            assertTrue("Found diffs: " + diffFile.getPath(),
                    differences.size() == 0);
        }

    }

    protected EPackage getOutputMMEPkg() throws IOException {
        return EPackageUtils.loadDynamicEcore(resourceSet,
                getOutputMM().getCanonicalPath()).get(0);
    }

    protected File getOutputMM() {
        return outputMMDir.listFiles((FilenameFilter) new SuffixFileFilter(
                ".ecore"))[0];
    }

    protected EPackage getInputMMEPkg() throws IOException {
        return EPackageUtils.loadDynamicEcore(resourceSet,
                getInputMM().getCanonicalPath()).get(0);
    }

    protected File getInputMM() {
        return inputMMDir.listFiles((FilenameFilter) new SuffixFileFilter(
                ".ecore"))[0];
    }

}
