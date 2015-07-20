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

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLParserPool;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLParserPoolImpl;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.InterpreterFactory;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.model.Annotation;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.HenshinPackage;
import org.eclipse.emf.henshin.model.IteratedUnit;
import org.eclipse.emf.henshin.model.LoopUnit;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Not;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.SequentialUnit;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fr.tpt.atlanalyser.ATLAnalyserRuntimeException;
import fr.tpt.atlanalyser.overlapping.GraphOverlapper;
import fr.tpt.atlanalyser.overlapping.GraphOverlapper.GraphOverlapGenerator;
import fr.tpt.atlanalyser.post2pre.formulas.AbstractFormulaVisitor;
import fr.tpt.atlanalyser.post2pre.formulas.LimitedSimplifier;
import fr.tpt.atlanalyser.post2pre.formulas.LimitedSimplifierRemoveDiscarded;
import fr.tpt.atlanalyser.post2pre.ncvalidators.ATLSemanticsValidator;
import fr.tpt.atlanalyser.post2pre.ncvalidators.FormulaFilter;
import fr.tpt.atlanalyser.post2pre.ncvalidators.NestedConditionValidator;
import fr.tpt.atlanalyser.post2pre.resources.ConcurrentResource;
import fr.tpt.atlanalyser.post2pre.resources.ConcurrentResourceSet;
import fr.tpt.atlanalyser.utils.CopierWithResolveErrorNotification;
import fr.tpt.atlanalyser.utils.GraphCopier;
import fr.tpt.atlanalyser.utils.Morphism;
import fr.tpt.atlanalyser.utils.NGCUtils;
import fr.tpt.atlanalyser.utils.Utils;

public class Post2Pre {

    public static final boolean                 DISABLE_ALL_DUMPING        = false;
    public static final boolean                 KEEP_NAMES                 = true;

    private static final Logger                 LOGGER                     = LogManager
                                                                                   .getLogger(Post2Pre.class
                                                                                           .getSimpleName());
    private static final Logger                 OVRLP_LOGGER               = LogManager
                                                                                   .getLogger(GraphOverlapper.class
                                                                                           .getSimpleName());
    private static final Logger                 RIGHT_NC_VALIDATION_LOGGER = LogManager
                                                                                   .getLogger("RightNCValidation");
    private static final Logger                 LEFT_NC_VALIDATION_LOGGER  = LogManager
                                                                                   .getLogger("LeftNCValidation");
    private static Post2PreManager              manager;

    private List<Throwable>                     errors                     = Collections
                                                                                   .synchronizedList(Lists
                                                                                           .newArrayList());
    private final ReentrantReadWriteLock        resLock                    = new ReentrantReadWriteLock(
                                                                                   true);

    static {
        manager = new Post2PreManager();
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = null;
        try {
            name = new ObjectName(
                    "fr.tpt.atlanalyzer.post2pre:type=Post2PreManager");
        } catch (MalformedObjectNameException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NullPointerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            mbs.registerMBean(manager, name);
        } catch (InstanceAlreadyExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotCompliantMBeanException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static final HenshinFactory         HF                         = HenshinFactory.eINSTANCE;

    private EPackage                            traceMM;
    private EClass                              traceEClass;
    private ThreadPoolExecutor                  executor                   = null;

    private int                                 jobs                       = 4;

    private int                                 overlapJobsQueueSize;
    private Map<Unit, NestedConditionValidator> unitToLeftNCValidator      = Maps.newHashMap();
    private BlockingQueue<Runnable>             queue;
    private Map<Unit, NestedConditionValidator> unitToRightNCValidator;
    private ConcurrentResourceSet               tmpResSet;
    private Resource                            cdtRes;
    private Engine[]                            henginePool;
    private AtomicInteger                       hengineIndex               = new AtomicInteger(
                                                                                   0);
    private static AtomicInteger                tmpCounter                 = new AtomicInteger(
                                                                                   0);
    private static final String                 CWD                        = System.getProperty("user.dir");

    public Post2Pre(EPackage traceMM) {
        this.traceMM = traceMM;
        if (traceMM != null) {
            this.traceEClass = (EClass) traceMM.getEClassifier("Trace");
        }
        atlSemanticsValidator = new ATLSemanticsValidator(traceEClass);
        tmpResSet = new ConcurrentResourceSet((HenshinResourceSet) traceMM
                .eResource().getResourceSet());
        manager.setResourceSet(tmpResSet);
        Resource.Factory binResFact = new Resource.Factory() {

            @Override
            public Resource createResource(URI uri) {
                return new XMIResourceImpl(uri);
            }

        };

        tmpResSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put("dat", binResFact);
        Resource.Factory myResFact = new Resource.Factory() {
            @Override
            public Resource createResource(URI uri) {
                return new ConcurrentResource(uri);
            }
        };
        tmpResSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put("tmp", myResFact);
        cdtRes = tmpResSet.createResource(URI.createFileURI(CWD
                + "/tmp/cdt.tmp"));
        xmlParserPool = new XMLParserPoolImpl(jobs, true);
    }

    public Post2Pre() {
        this(null);
    }

    public Post2Pre(EPackage traceMM2, int jobs) {
        this(traceMM2);
        this.jobs = jobs;
        manager.setThreadPoolSize(jobs);
    }

    public Post2Pre(EPackage traceMM2, int jobs2,
            Map<Unit, NestedConditionValidator> unitToLeftNCValidator,
            Map<Unit, NestedConditionValidator> unitToRightNCValidator) {
        this(traceMM2, jobs2);
        this.unitToLeftNCValidator = unitToLeftNCValidator;
        this.unitToRightNCValidator = unitToRightNCValidator;
    }

    private final class QueueMonitor implements Runnable {
        @Override
        public void run() {
            int size = queue.size();
            LOGGER.debug("Task queue size: {}", size);
        }
    }

    ReentrantReadWriteLock memDumpLock = new ReentrantReadWriteLock();

    class CompletionBarrier extends Phaser {
        protected Phaser                               parent;
        protected NestedCondition                      originalCdt;
        protected int[]                                id;
        protected Resource                             originalRes;

        private List<NestedCondition>                  result;
        private Resource                               resultRes;
        private Resource                               hostRes;
        private Triplet<Resource, EObject, EReference> destination;

        public CompletionBarrier(List<NestedCondition> result,
                NestedCondition originalCdt,
                Triplet<Resource, EObject, EReference> destination,
                Phaser parent, int[] id, Resource resRes, Resource hostRes) {
            this.parent = parent;
            this.id = id;
            this.originalRes = originalCdt.eResource();
            this.originalCdt = Utils.createProxy(originalCdt);
            this.result = result;
            this.resultRes = resRes;
            this.hostRes = hostRes;
            this.destination = destination;
        }

        @Override
        public void forceTermination() {
            super.forceTermination();
            parent.forceTermination();
        }

        @Override
        protected boolean onAdvance(int phase, int registeredParties) {
            resLock.readLock().lock();
            LOGGER.trace("Advancing: " + Utils.toString(id));
            // try {
            NestedCondition r = (NestedCondition) Utils.resolve(originalCdt,
                    originalRes);
            if (r.eIsProxy()) {
                throw new RuntimeException("Failed to resolve: "
                        + EcoreUtil.getURI(r));
            }

            if (r.eContainer() == null) {
                LOGGER.debug("Debug");
            }
            destination = destination.setAt1(Utils.resolve(
                    destination.getValue1(), destination.getValue0()));
            EObject eContainer = destination.getValue1();
            EStructuralFeature eContainingFeature = destination.getValue2();

            // Execute this statement outside of the assert statement because we
            // need it even when assertions are disabled.
            boolean contaimentResolved = eContainer.eGet(eContainingFeature) == r
                    || r.eContainer() != null;
            if (!contaimentResolved) {
                throw new RuntimeException("Failed to resolve containment");
            }

            originalCdt = r;
            result.replaceAll(p -> (NestedCondition) Utils
                    .resolve(p, resultRes));

            EList<EObject> resultContents = resultRes.getContents();

            Formula res;
            if (result.size() > 0) {
                res = NGCUtils.createDisjunction(result);
            } else {
                res = NGCUtils.createFalse();
            }

            EcoreUtil.replace(originalCdt, res);

            Utils.detachTree(originalCdt);

            Formula simplRes = new LimitedSimplifierRemoveDiscarded(1,
                    originalRes).transform(res);
            eContainer.eSet(eContainingFeature, simplRes);

            List<NestedCondition> ncs = NGCUtils.getNestedConditions(simplRes);
            for (NestedCondition nc : ncs) {
                NGCUtils.checkConsistency(nc);
            }

            if (resultContents.isEmpty() || Utils.allDummy(resultContents)) {
                try {
                    ResourceSet resourceSet = resultRes.getResourceSet();
                    if (resourceSet != null) {
                        ((ConcurrentResourceSet) resourceSet).lockWrite();
                        resultRes.delete(null);
                        ((ConcurrentResourceSet) resourceSet).unlockWrite();
                    } else {
                        resultRes.delete(null);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Could not delete resource: "
                            + resultRes.getURI(), e);
                }
            }

            // When launching sub overlap tasks to handle the
            // subformula, we move the subformula (or a copy of it) to
            // the overlap-based new nested condition. So the following
            // should always hold (unless there were no overlaps).
            // assert result.size() == 0
            // || originalCdt.getConclusion().getFormula() == null;

            // Utils.deleteTree(originalCdt);
            // } finally {
            // resLock.readLock().unlock();
            // }

            if (!Post2Pre.DISABLE_ALL_DUMPING && manager.lowMemory()) {
                try {
                    resLock.readLock().unlock();
                    resLock.writeLock().lock();
                    // Recheck memory to make sure we haven't already dumped
                    if (manager.lowMemory()) {
                        LOGGER.warn("Low Memory. Dumping at "
                                + Utils.toString(id));

                        tmpResSet.lockRead();
                        List<Resource> resources = Lists.newArrayList(tmpResSet
                                .getResources());
                        tmpResSet.unlockRead();

                        int n = 0;
                        Stopwatch timer = Stopwatch.createStarted();
                        for (Resource resource : resources) {
                            URI uri = resource.getURI();
                            if (uri.lastSegment().startsWith("tmp")
                                    && resource.isLoaded()
                                    && ((ConcurrentResource) resource)
                                            .secondsSinceLastAccessed()
                                            - timer.elapsed(TimeUnit.SECONDS) > 3) {
                                try {
                                    resource.save(Collections.emptyMap());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                resource.unload();
                                if (++n % 500 == 0) {
                                    System.gc();
                                    if (manager.getCurrentMemUsage() < manager
                                            .getStopDumpThreshold()) {
                                        LOGGER.warn("Recovered enough memory. Stop dumping.");
                                        break;
                                    }
                                }
                            }
                        }
                        System.gc();
                        LOGGER.warn("Finished dumping. Memory: "
                                + String.format("%.2f%%",
                                        manager.getCurrentMemUsage() * 100));
                    }
                } finally {
                    resLock.writeLock().unlock();
                    resLock.readLock().lock();
                }
            }

            // if (wasProxy) {
            // EList<EObject> contents = tmpRes.getContents();
            // if (NGCUtils.isFalse(res)) {
            // if (contents.size() == 0) {
            // try {
            // tmpRes.delete(Collections.emptyMap());
            // } catch (IOException e) {
            // throw new RuntimeException(e);
            // }
            // }
            // } else {
            // contents.add(res);
            // LOGGER.debug("Dumping {} to disk", tmpRes.getURI()
            // .lastSegment());
            // Stopwatch timer = Stopwatch.createStarted();
            // try {
            // tmpRes.save(Collections.emptyMap());
            // } catch (IOException e) {
            // throw new RuntimeException(e);
            // }
            // LOGGER.debug("Dumped {} in {}", tmpRes.getURI()
            // .lastSegment(), timer.stop());
            //
            // Formula proxy = Utils.createProxy(res);
            //
            // tmpRes.unload();
            //
            // EcoreUtil.replace(res, proxy);
            // res = proxy;
            //
            // System.gc();
            // }
            // }

            resLock.readLock().unlock();
            parent.arrive();
            return true;
        }

        @Override
        public String toString() {
            String stringId = Utils.toString(id);
            return super.toString() + " id: " + stringId;
        }

    }

    class PostToLeftAC extends AbstractFormulaVisitor {

        Morphism                         anchor;
        private Graph                    newHost;
        private Graph                    host;
        private Phaser                   parent;
        private int[]                    parentId;
        private int                      jobCount = 0;
        private Morphism                 ruleMorphism;
        private NestedConditionValidator leftNCValidator;

        public PostToLeftAC(Graph formulaHost, Morphism anchor,
                Morphism ruleMorphism,
                NestedConditionValidator leftNCValidator, Phaser parent,
                int[] parentId) {
            this.host = formulaHost;
            this.anchor = anchor;
            this.newHost = anchor.getTarget();
            this.ruleMorphism = ruleMorphism;
            this.leftNCValidator = leftNCValidator;
            this.parent = parent;
            this.parentId = parentId;
        }

        @Override
        public Formula caseNestedCondition(NestedCondition cdt) {
            if (!NGCUtils.isTrue(cdt)) {
                int[] jobId = Arrays.copyOf(parentId, parentId.length + 1);
                jobId[jobId.length - 1] = ++jobCount;
                parent.register();
                Engine nextEngine = henginePool[hengineIndex.incrementAndGet()
                        % henginePool.length];
                PostToLeftACTask graphOverlapTask = new PostToLeftACTask(
                        this.host, cdt, anchor, ruleMorphism, Triplet.with(cdt
                                .eContainer().eResource(), cdt.eContainer(),
                                (EReference) cdt.eContainingFeature()),
                        leftNCValidator, jobId, parent, nextEngine);
                manager.allOverlaps.incrementAndGet();
                executor.execute(graphOverlapTask);
            }
            return cdt;
        }
    }

    public Formula postToLeftAC(Formula post, Rule rule,
            NestedConditionValidator rightACValidator,
            NestedConditionValidator leftACValidator) {
        Formula leftAC = postToLeftACParallel(post, rule, jobs,
                rightACValidator, leftACValidator);

        return leftAC;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Formula postToLeftACParallel(Formula post, Rule rule,
            int numThreads, NestedConditionValidator rightACValidator,
            NestedConditionValidator leftACValidator) {
        // Initialize executor
        LOGGER.debug("Initializing executor with {} threads",
                manager.getThreadPoolSize());
        queue = new ManagedJobQueue();
        executor = new ThreadPoolExecutor(manager.getThreadPoolSize(),
                manager.getThreadPoolSize(), 1, TimeUnit.SECONDS, queue);
        manager.setExecutor(executor);

        // Initialize pool of Henshin engines
        henginePool = new Engine[jobs];
        for (int i = 0; i < henginePool.length; i++) {
            henginePool[i] = InterpreterFactory.INSTANCE.createEngine();
        }

        ScheduledExecutorService monitorExecutor = Executors
                .newScheduledThreadPool(1);
        monitorExecutor.scheduleAtFixedRate(new QueueMonitor(), 0, 3,
                TimeUnit.SECONDS);

        Formula leftAC = null;
        try {
            leftAC = postToLeftACInternal(post, rule, rightACValidator,
                    leftACValidator);
        } finally {
            monitorExecutor.shutdown();
            executor.shutdown();
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new ATLAnalyserRuntimeException(e);
            } finally {
                if (errors.size() > 0) {
                    for (Throwable ex : errors) {
                        LOGGER.error("Error in parallel task", ex);
                    }
                }
                manager.setExecutor(null);
                for (Engine e : henginePool) {
                    e.shutdown();
                }
                henginePool = null;
            }
        }

        return leftAC;
    }

    /**
     * Warning: This function assumes that `executor' has been initialized.
     * 
     * @param post
     * @param rule
     * @param rightACValidator
     * @return
     */
    private Formula postToLeftACInternal(Formula post, Rule rule,
            NestedConditionValidator rightACValidator,
            NestedConditionValidator leftACValidator) {
        // The postcondition is contained in an empty host graph
        Graph hostGraph = NGCUtils.getHostGraph(post);
        hostGraph = (Graph) EcoreUtil.resolve(hostGraph, tmpResSet);
        post = hostGraph.getFormula();
        // Initial anchor is from the empty graph to the RHS
        Morphism anchor = new Morphism(hostGraph, rule.getRhs());

        Morphism ruleMorphism = new Morphism(rule);

        Resource eResource = hostGraph.eResource();
        assert eResource != null;
        Phaser completionBarrier = new Phaser(1);

        Formula leftAC = new PostToLeftAC(hostGraph, anchor, ruleMorphism,
                leftACValidator, completionBarrier, new int[] {})
                .transform(post);
        int result;
        result = completionBarrier.arriveAndAwaitAdvance();
        if (result < 0) {
            // Execution was terminated by errors
            throw new ATLAnalyserRuntimeException(
                    "Exceptions encoutered in parallel tasks");
        }

        leftAC = hostGraph.getFormula();
        // TODO: The following call seems to take a very long time to complete
        // eResource.getContents().add(leftAC);

        anchor.dispose();
        ruleMorphism.dispose();
        return leftAC;
    }

    public static Formula leftACToExistsConstraint(Rule rule, Formula leftAC) {
        return leftACToExistsConstraint(rule, leftAC, null);
    }

    /**
     * Transforms the left application condition of the rule to a constraint
     * over graphs ensuring that there exists a match of the rule such that the
     * left application condition is satisfied.
     * 
     * @param rule
     * @param ncValidator
     * 
     * @return
     */
    public static Formula leftACToExistsConstraint(Rule rule, Formula leftAC,
            NestedConditionValidator ncValidator) {
        Preconditions.checkArgument(!rule.eIsProxy());
        Preconditions.checkArgument(!leftAC.eIsProxy());

        Graph emptyGraph = NGCUtils.getHostGraph(leftAC);

        // We need to construct exists (LHS, leftAC)
        NestedCondition cst = HF.createNestedCondition();
        emptyGraph.setFormula(cst);
        Graph lhs = rule.getLhs();
        cst.setConclusion(lhs);

        // Filter existing LHS formula
        Formula lhsFormula = lhs.getFormula();
        if (ncValidator != null) {
            lhsFormula = new FormulaFilter(ncValidator).transform(lhsFormula);
        }

        lhs.setFormula(new LimitedSimplifier().transform(NGCUtils
                .createConjunction(lhsFormula, leftAC)));

        return cst;
    }

    /**
     * Transforms the left application condition of the rule to a constraint
     * over graphs ensuring that for all matches of the rule the left
     * application condition is satisfied.
     * 
     * @param rule
     * 
     * @return
     */
    public static Formula leftACToForAllConstraint(Rule rule) {
        Graph emptyGraph = HF.createGraph();
        // We need to construct not (exists (LHS, not (leftAC)))
        Not not = HF.createNot();
        NestedCondition nc = HF.createNestedCondition();
        Not subNot = HF.createNot();
        emptyGraph.setFormula(not);
        not.setChild(nc);
        Graph lhsCopy = CopierWithResolveErrorNotification.copy(rule.getLhs());
        nc.setConclusion(lhsCopy);
        Formula leftAC = lhsCopy.getFormula() != null ? lhsCopy.getFormula()
                : NGCUtils.createTrue();
        subNot.setChild(leftAC);
        lhsCopy.setFormula(subNot);
        return not;
    }

    public Formula post2Pre(Formula post, Rule rule) {
        return post2Pre(post, rule, 1, true);
    }

    private static int                  i = 0;
    private final ATLSemanticsValidator atlSemanticsValidator;
    private XMLParserPool               xmlParserPool;

    public class Post2PreForUnits {

        private Formula                  post;
        private int                      maxIterationsForLoops;
        private boolean                  doIntermediateSimplifications;
        private EPackage                 traceMM;
        private NestedConditionValidator ncValidator;

        public Post2PreForUnits(Formula postcondition,
                int maxIterationsForLoops,
                boolean doIntermediateSimplifications, EPackage traceMM,
                NestedConditionValidator ncValidator) {
            this.post = postcondition;
            this.maxIterationsForLoops = maxIterationsForLoops;
            this.doIntermediateSimplifications = doIntermediateSimplifications;
            this.traceMM = traceMM;
            this.ncValidator = ncValidator;
        }

        public Post2PreForUnits(Formula postcondition,
                int maxIterationsForLoops, boolean doIntermediateSimplifications) {
            this(postcondition, maxIterationsForLoops,
                    doIntermediateSimplifications, null, null);
        }

        public Formula compute(Unit unit) {
            return this.doSwitch(unit);
        }

        private Formula doSwitch(Unit unit) {
            int classifierID = unit.eClass().getClassifierID();
            switch (classifierID) {
            case HenshinPackage.SEQUENTIAL_UNIT:
                return caseSequentialUnit((SequentialUnit) unit);
            case HenshinPackage.LOOP_UNIT:
                return caseLoopUnit((LoopUnit) unit);
            case HenshinPackage.ITERATED_UNIT:
                return caseIteratedUnit((IteratedUnit) unit);
            case HenshinPackage.RULE:
                return caseRule((Rule) unit);
            default:
                throw new UnsupportedOperationException("Not implemented for "
                        + unit);
            }
        }

        private Formula caseRule(Rule object) {
            LOGGER.info("Processing rule {}", object);
            Stopwatch fullTimer = Stopwatch.createStarted();

            Graph oldHostGraph = NGCUtils.getHostGraph(post);
            Resource postRes = post.eResource();

            Rule rule = CopierWithResolveErrorNotification.copy(object);
            Resource ruleRes = createResourceWithObj(rule);
            // Rule rule = object;

            rule.getLhs().setName(rule.getName() + "_LHS");
            rule.getRhs().setName(rule.getName() + "_RHS");

            LOGGER.info("Post2LeftAC...");
            Stopwatch timer = Stopwatch.createStarted();

            manager.allOverlaps.set(0);
            manager.performedOverlaps.set(0);
            Formula leftAC = postToLeftAC(post, rule, null, ncValidator);
            LOGGER.info("Done Post2LeftAC in " + timer.stop());

            timer.reset().start();
            LOGGER.info("LeftAC2Pre...");
            if (rule.eIsProxy()) {
                rule = (Rule) EcoreUtil.resolve(rule, tmpResSet);
                EcoreUtil.resolveAll(rule);
            }
            if (leftAC.eIsProxy()) {
                leftAC = (Formula) EcoreUtil.resolve(leftAC, tmpResSet);
            }

            final Rule finalRule = rule;
            new AbstractFormulaVisitor() {
                @Override
                public Formula caseNestedCondition(NestedCondition nc) {
                    for (Mapping m : nc.getMappings()) {
                        boolean resolveSuccess = m.getOrigin().eContainer() == finalRule
                                .getLhs();
                        assert resolveSuccess;
                    }
                    return nc;
                }
            }.transform(leftAC);

            Formula pre = leftACToExistsConstraint(rule, leftAC, ncValidator);
            LOGGER.info("Done LeftAC2Pre in " + timer.stop());

            assert ruleRes.getContents().size() == 1;
            assert ruleRes.getContents().get(0) == rule;
            try {
                ruleRes.delete(null);
            } catch (IOException e) {
                throw new ATLAnalyserRuntimeException(e);
            }

            Graph hostGraph = NGCUtils.getHostGraph(pre);

            hostGraph.setFormula(pre);

            hostGraph.setName("Pre_" + object.getName());

            LOGGER.info("Done rule {} in {}", object, fullTimer.stop());

            return pre;
        }

        private Formula caseSequentialUnit(SequentialUnit object) {
            LOGGER.info("Processing sequence {}", object);
            Formula pre = post;
            List<Unit> subUnits = Lists.newArrayList(object.getSubUnits());
            Collections.reverse(subUnits);
            for (Unit unit : subUnits) {
                pre = new Post2PreForUnits(pre, maxIterationsForLoops,
                        doIntermediateSimplifications, traceMM, ncValidator)
                        .doSwitch(unit);
            }
            return pre;
        }

        private Formula caseIteratedUnit(IteratedUnit object) {
            LOGGER.info("Processing bounded iteration {}", object);
            int nIterations = Integer.parseInt(object.getIterations());
            Formula pre = post;
            for (int i = 0; i < nIterations; i++) {
                pre = new Post2PreForUnits(pre, maxIterationsForLoops,
                        doIntermediateSimplifications, traceMM, ncValidator)
                        .doSwitch(object.getSubUnit());
            }
            return pre;
        }

        private Formula caseLoopUnit(LoopUnit object) {
            LOGGER.info("Processing unbounded loop {}", object);
            Formula pre = post;
            Graph hostGraph = NGCUtils.getHostGraph(pre);
            hostGraph = (Graph) EcoreUtil.resolve(hostGraph, tmpResSet);

            // copy postcondition for final result
            Formula originalPost = CopierWithResolveErrorNotification
                    .copy(post);
            originalPost = dumpToFile(originalPost);

            // init result with a copy of the precondition
            Graph resultHostGraph = HF.createGraph();
            createResourceWithObj(resultHostGraph);
            resultHostGraph.setName("Pre_" + object.getName());
            Formula result = originalPost;
            resultHostGraph.setFormula(result);

            for (int i = 0; i < maxIterationsForLoops; i++) {
                // unloadTmpFilesExcept(pre.eResource());
                NestedConditionValidator validator = null;
                if (i == maxIterationsForLoops - 1
                        && unitToLeftNCValidator.containsKey(object)) {
                    // apply the validator only at the last iteration of the
                    // subunit
                    validator = unitToLeftNCValidator.get(object);
                }
                pre = new Post2PreForUnits(pre, maxIterationsForLoops,
                        doIntermediateSimplifications, traceMM, validator)
                        .doSwitch(object.getSubUnit());

                // if this is the last iteration, filter the previous result
                // with the validator if any
                if (i == maxIterationsForLoops - 1 && validator != null) {
                    result = (Formula) EcoreUtil.resolve(result, tmpResSet);
                    result = new FormulaFilter(validator).transform(result);
                }

                if (i < maxIterationsForLoops - 1) {
                    // If there is a next iteration, store a copy of pre in the
                    // disjuntion because the next iteration will modify pre
                    result = NGCUtils.createDisjunction(result,
                            CopierWithResolveErrorNotification.copy(pre));
                } else {
                    result = NGCUtils.createDisjunction(result, pre);
                }
                resultHostGraph = (Graph) EcoreUtil.resolve(resultHostGraph,
                        tmpResSet);
                resultHostGraph.setFormula(result);
            }

            // Resource res = tmpResSet.createResource(URI.createFileURI(CWD +
            // "/"
            // + Integer.toString(i++) + "_" + object.getName() + ".xmi"));
            // res.getContents().add(EcoreUtil.copy(result));
            // try {
            // res.save(Collections.emptyMap());
            // } catch (IOException e) {
            // e.printStackTrace();
            // throw new RuntimeException(e);
            // }
            // res.unload();

            return result;
        }
    }

    class PostToLeftACTask implements Runnable, IWithId {

        private NestedCondition                        cdt;
        private Graph                                  host;
        private int[]                                  id;
        private CompletionBarrier                      completionBarrier;
        private Phaser                                 parent;
        private Morphism                               anchor;
        private Morphism                               ruleMorphism;
        private NestedConditionValidator               leftNCValidator;
        private Engine                                 hengine;
        private Resource                               ncRes;
        private Resource                               hostRes;
        private Resource[]                             anchorRes;
        private Resource[]                             ruleRes;
        private Triplet<Resource, EObject, EReference> destination;

        public PostToLeftACTask(Graph host, NestedCondition cdt,
                Morphism anchor, Morphism ruleMorphism,
                Triplet<Resource, EObject, EReference> destination,
                NestedConditionValidator leftNCValidator, int[] id,
                Phaser parent, Engine hengine) {
            this.ncRes = cdt.eResource();
            this.cdt = Utils.createProxy(cdt);
            if (!EcoreUtil.getURI(this.cdt).trimFragment()
                    .equals(this.ncRes.getURI())) {
                LOGGER.error("Error");
            }
            this.hostRes = host.eResource();
            this.host = Utils.createProxy(host);
            if (!EcoreUtil.getURI(this.host).trimFragment()
                    .equals(this.hostRes.getURI())) {
                LOGGER.error("Error");
            }

            for (Mapping m : cdt.getMappings()) {
                if (m.getOrigin().eContainer() != host) {
                    LOGGER.error("Error");
                }
            }

            this.destination = destination.setAt1(Utils.createProxy(destination
                    .getValue1()));

            this.id = id.clone();
            this.parent = parent;

            this.anchorRes = new Resource[] { anchor.getSource().eResource(),
                    anchor.getTarget().eResource() };
            this.anchor = anchor.proxify();

            this.ruleRes = new Resource[] {
                    ruleMorphism.getSource().eResource(),
                    ruleMorphism.getTarget().eResource() };
            this.ruleMorphism = ruleMorphism.proxify();
            this.leftNCValidator = leftNCValidator;
            this.hengine = hengine;
        }

        @Override
        public void run() {
            try {
                if (errors.isEmpty()) {
                    compute();
                } else {
                    parent.forceTermination();
                }
            } catch (Exception | AssertionError ex) {
                if (!(ex instanceof RejectedExecutionException)) {
                    errors.add(new RuntimeException("Error in task "
                            + Utils.toString(id), ex));
                }
                if (completionBarrier != null) {
                    completionBarrier.forceTermination();
                } else {
                    parent.forceTermination();
                }
            } finally {
                for (int i = 0; i < resLock.getReadHoldCount(); i++) {
                    resLock.readLock().unlock();
                }
            }
        }

        public void compute() {
            resLock.readLock().lock();
            resolveData();

            // new resource to store overlaps
            Resource newRes = newUnattachedTmpResource();
            EList<EObject> contents = newRes.getContents();

            List<NestedCondition> result = Lists.newArrayList();
            completionBarrier = new CompletionBarrier(result, cdt, destination,
                    parent, this.id, newRes, hostRes);
            completionBarrier.register();

            Graph rightHost = anchor.getTarget();
            Graph conclusion = cdt.getConclusion();

            OVRLP_LOGGER.trace("Starting Overlapping");
            Stopwatch timer = Stopwatch.createStarted();

            // Create a copy of the host to work on in parallel
            Morphism rightHostToRightHostCopy = GraphCopier.copy(rightHost);
            Graph rightHostCopy = rightHostToRightHostCopy.getTarget();
            Morphism anchorCopy = anchor.compose(rightHostToRightHostCopy);

            Morphism a = new Morphism(cdt, host);
            Formula nestedFormula = conclusion.getFormula();

            Morphism concToConcCopy = GraphCopier.copy(conclusion);
            Graph concCopy = concToConcCopy.getTarget();
            Morphism aCopy = a.compose(concToConcCopy);

            boolean excludeEmptyOverlap = false;
            boolean enforceEMFConstraints = true;
            boolean preventEdgeAutoMapping = false;
            GraphOverlapGenerator overlaps = new GraphOverlapGenerator(
                    rightHostCopy, concCopy, new Pair<Morphism, Morphism>(
                            anchorCopy, aCopy), excludeEmptyOverlap,
                    enforceEMFConstraints, preventEdgeAutoMapping, hengine);

            List<Triplet<NestedCondition, Morphism, Morphism>> validOveraps = Lists
                    .newArrayList();
            Iterator<Pair<Morphism, Morphism>> overlapIterator = overlaps
                    .iterator();
            try {
                List<PostToLeftACTask> subTasks = Lists.newArrayList();
                while (overlapIterator.hasNext()) {
                    Pair<Morphism, Morphism> pair = overlapIterator.next();
                    Morphism rightHostCopyToOverlap = pair.getValue0();
                    Morphism newRightCdtMorphism = rightHostToRightHostCopy
                            .compose(rightHostCopyToOverlap);

                    NestedCondition newRightCdt = NGCUtils
                            .createNestedCondition(newRightCdtMorphism);

                    Graph newConclusion = newRightCdt.getConclusion();

                    if (!atlSemanticsValidator.isValid(newRightCdt)
                            || !atlSemanticsValidator.isTraceValid(pair)) {
                        RIGHT_NC_VALIDATION_LOGGER
                                .debug("ATL semantics violation");
                    } else {

                        Pair<Morphism, Morphism> pushoutComplement;
                        synchronized (parent) {
                            pushoutComplement = Morphism.getPushoutComplement(
                                    ruleMorphism, newRightCdtMorphism);
                        }

                        if (pushoutComplement == null) {
                            LEFT_NC_VALIDATION_LOGGER
                                    .debug("No pushout complement");
                        } else {
                            Morphism newLeftCdtMorphism = pushoutComplement
                                    .getValue0();
                            NestedCondition newLeftCdt = NGCUtils
                                    .createNestedCondition(newLeftCdtMorphism);
                            if (KEEP_NAMES) {
                                newLeftCdt.getConclusion().setName(
                                        "L("
                                                + newRightCdtMorphism
                                                        .getTarget().getName()
                                                + ")");
                            }

                            if (leftNCValidator != null
                                    && !leftNCValidator.isValid(newLeftCdt)) {
                                LEFT_NC_VALIDATION_LOGGER
                                        .debug("Removing invalid NC");
                            } else {
                                contents.add(newLeftCdt);
                                result.add(Utils.createProxy(newLeftCdt));
                                if (nestedFormula != null) {
                                    contents.add(newRightCdt);
                                    Morphism newAnchorCopy = pair.getValue1();
                                    Morphism newAnchor = concToConcCopy
                                            .compose(newAnchorCopy);
                                    Morphism newRuleMorphism = pushoutComplement
                                            .getValue1();

                                    Triplet<NestedCondition, Morphism, Morphism> validOverlap = Triplet
                                            .with(newLeftCdt, newAnchor,
                                                    newRuleMorphism);
                                    validOveraps.add(validOverlap);

                                }
                            }
                        }
                    }

                    Morphism.dispose(pair.getValue0(), pair.getValue1());
                }

                manager.performedOverlaps.incrementAndGet();

                if (!contents.isEmpty()) {
                    Annotation annotation = HF.createAnnotation();
                    annotation.setKey("Task " + Utils.toString(id));
                    contents.add(annotation);
                    tmpResSet.lockWrite();
                    tmpResSet.getResources().add(newRes);
                    tmpResSet.unlockWrite();
                }

                // Create subtasks
                int[] jobId = Arrays.copyOf(id, id.length + 2);
                jobId[jobId.length - 2] = 0;
                for (Iterator<Triplet<NestedCondition, Morphism, Morphism>> iterator = validOveraps
                        .iterator(); iterator.hasNext();) {
                    Triplet<NestedCondition, Morphism, Morphism> triplet = (Triplet<NestedCondition, Morphism, Morphism>) iterator
                            .next();
                    NestedCondition newLeftCdt = triplet.getValue0();
                    Morphism newAnchor = triplet.getValue1();
                    Morphism newRuleMorphism = triplet.getValue2();

                    Formula toTransform;

                    // the subtasks will attempt in-place transformation of
                    // nestedFormula. So we need to copy it first...
                    if (iterator.hasNext()) {
                        toTransform = CopierWithResolveErrorNotification
                                .copy(nestedFormula);
                    } else {
                        toTransform = nestedFormula;
                        new AbstractFormulaVisitor() {
                            public Formula caseNestedCondition(
                                    NestedCondition nc) {
                                for (Mapping mapping : nc.getMappings()) {
                                    boolean resolveSuccess = mapping
                                            .getOrigin().eContainer() == conclusion;
                                    assert resolveSuccess;
                                }
                                return nc;
                            };
                        }.transform(toTransform);
                    }

                    newLeftCdt.getConclusion().setFormula(toTransform);

                    List<NestedCondition> nestedConditions = NGCUtils
                            .getNestedConditions(toTransform);

                    jobId[jobId.length - 2]++;
                    jobId[jobId.length - 1] = 0;
                    for (NestedCondition nc : nestedConditions) {
                        if (!NGCUtils.isTrue(nc)) {
                            jobId[jobId.length - 1]++;
                            Engine nextEngine = henginePool[hengineIndex
                                    .incrementAndGet() % henginePool.length];
                            PostToLeftACTask subTask = new PostToLeftACTask(
                                    conclusion, nc, newAnchor, newRuleMorphism,
                                    Triplet.with(nc.eContainer().eResource(),
                                            nc.eContainer(), (EReference) nc
                                                    .eContainingFeature()),
                                    leftNCValidator, jobId, completionBarrier,
                                    nextEngine);
                            subTasks.add(subTask);
                        }
                    }
                }

                manager.allOverlaps.addAndGet(subTasks.size());

                LOGGER.trace("Done task " + Utils.toString(id) + ". Starting "
                        + subTasks.size() + " subtasks");

                ((EngineImpl) hengine).clearCache();

                // start sub tasks
                for (PostToLeftACTask subTask : subTasks) {
                    completionBarrier.register();
                    executor.execute(subTask);
                }

            } finally {
                overlaps.dispose();
                a.dispose();
            }

            resLock.readLock().unlock();
            completionBarrier.arrive();
        }

        private void resolveData() {
            NestedCondition r = (NestedCondition) Utils.resolve(cdt, ncRes);
            assert !r.eIsProxy() : "Failed to resolve: " + EcoreUtil.getURI(r);
            cdt = r;

            Formula formula = cdt.getConclusion().getFormula();
            if (formula != null) {
                new AbstractFormulaVisitor() {

                    @Override
                    public Formula caseNestedCondition(NestedCondition nc) {
                        assert !nc.eIsProxy();
                        return nc;
                    }
                }.transform(formula);

            }

            host = (Graph) Utils.resolve(host, hostRes);
            // host = (Graph) EcoreUtil.resolve(host, tmpResSet);
            assert !host.eIsProxy();

            MappingList mappings = cdt.getMappings();
            synchronized (host) {
                for (Mapping m : mappings) {
                    Node origin = m.getOrigin();
                    assert !origin.eIsProxy() : "Could not resolve "
                            + EcoreUtil.getURI(origin);
                    assert origin.eContainer() == host : "host and NC mappings are not compatible";
                }
            }

            anchor = anchor.resolve(anchorRes[0], anchorRes[1]);
            ruleMorphism = ruleMorphism.resolve(ruleRes[0], ruleRes[1]);

            // Formula formula = ruleMorphism.getSource().getFormula();
            // ruleMorphism.getSource().setFormula(new AbstractFormulaVisitor()
            // {
            //
            // @Override
            // public Formula caseNestedCondition(NestedCondition nc) {
            // if (nc.eIsProxy()) {
            // nc = (NestedCondition) Utils.resolve(nc, ruleRes[0]);
            // }
            // return nc;
            // }
            // }.transform(formula));

        }

        @Override
        public int[] getId() {
            return id;
        }

        @Override
        public String toString() {
            return "PostToLeftAC id: " + Utils.toString(id);
        }
    }

    public Formula post2Pre(Formula postcondition, Unit program,
            int maxIterationsForLoops, boolean doIntermediateSimplifications) {
        Graph postHostCopy = CopierWithResolveErrorNotification.copy(NGCUtils
                .getHostGraph(postcondition));
        Formula post = postHostCopy.getFormula();
        createResourceWithObj(postHostCopy);
        return new Post2PreForUnits(post, maxIterationsForLoops,
                doIntermediateSimplifications, traceMM, null).compute(program);
    }

    public void unloadTmpFiles() {
        unloadTmpFiles(Collections.<Resource> emptyList());
    }

    public void unloadTmpFilesExcept(Resource except) {
        unloadTmpFiles(Collections.singleton(except));
    }

    public void unloadTmpFiles(Collection<Resource> except) {
        List<Resource> resources = Lists.newArrayList(tmpResSet.getResources());
        Collections.reverse(resources);
        for (Resource resource : resources) {
            URI uri = resource.getURI();
            if (uri.lastSegment().startsWith("tmp")
                    && !except.contains(resource) && resource.isLoaded()) {
                try {
                    resource.save(Collections.emptyMap());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                resource.unload();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (manager != null) {
            manager.finalize();
        }
        super.finalize();
    }

    private <T extends EObject> T dumpToFile(T obj) {
        if (DISABLE_ALL_DUMPING) {
            return obj;
        }
        Resource tmpRes = createResourceWithObj(obj);
        LOGGER.debug("Dumping {} to disk", tmpRes.getURI().lastSegment());
        Stopwatch timer = Stopwatch.createStarted();
        try {
            tmpRes.save(Collections.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LOGGER.debug("Dumped {} in {}", tmpRes.getURI().lastSegment(),
                timer.stop());

        T proxy = Utils.createProxy(obj);

        tmpRes.unload();
        return proxy;
    }

    private Resource createResourceWithObj(EObject obj) {
        Resource tmpRes = newTmpResource();
        tmpRes.getContents().add(obj);
        return tmpRes;
    }

    private Resource newUnattachedTmpResource() {
        URI uri = URI.createFileURI(String.format(CWD + "/tmp/tmp.%d.tmp",
                tmpCounter.incrementAndGet()));
        Resource tmpRes = tmpResSet.getResourceFactoryRegistry()
                .getFactory(uri).createResource(uri);
        return tmpRes;
    }

    private Resource newTmpResource() {
        Resource tmpRes = newUnattachedTmpResource();
        resLock.writeLock().lock();
        try {
            tmpResSet.getResources().add(tmpRes);
        } finally {
            resLock.writeLock().unlock();
        }

        return tmpRes;
    }

    private void dumpToFile(List<? extends EObject> objects) {
        if (DISABLE_ALL_DUMPING) {
            return;
        }

        Resource tmpRes = newTmpResource();
        EList<EObject> contents = tmpRes.getContents();
        for (EObject eObject : objects) {
            contents.add(eObject);
            EObject proxy = Utils.createProxy(eObject);
            EObject eContainer = eObject.eContainer();
            EReference eContainmentFeature = eObject.eContainmentFeature();
            eContainer.eSet(eContainmentFeature, proxy);
            EcoreUtil.replace(eObject, proxy);
        }

        LOGGER.debug("Dumping {} to disk", tmpRes.getURI().lastSegment());
        Stopwatch timer = Stopwatch.createStarted();
        try {
            tmpRes.save(Collections.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LOGGER.debug("Dumped {} in {}", tmpRes.getURI().lastSegment(),
                timer.stop());

        tmpRes.unload();
    }
}
