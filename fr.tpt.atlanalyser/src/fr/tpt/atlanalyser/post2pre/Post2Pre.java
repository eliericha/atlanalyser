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
import java.util.DoubleSummaryStatistics;
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
import org.eclipse.emf.henshin.model.BinaryFormula;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.IndependentUnit;
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
import org.eclipse.emf.henshin.model.util.HenshinSwitch;
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

    public static final boolean                 DISABLE_ALL_DUMPING        = true;
    public static boolean                       KEEP_NAMES                 = false;
    public static final boolean                 DUMP_AFTER_EACH_LOOPUNIT   = false;

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
    private static final Post2PreManager        manager                    = Post2PreManager.INSTANCE;

    private List<Throwable>                     errors                     = Collections
                                                                                   .synchronizedList(Lists
                                                                                           .newArrayList());
    private final ReentrantReadWriteLock        resLock                    = new ReentrantReadWriteLock(
                                                                                   true);

    private static final HenshinFactory         HF                         = HenshinFactory.eINSTANCE;

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
    private Collection<Unit>                    unitsToIgnore;
    private static AtomicInteger                tmpCounter                 = new AtomicInteger(
                                                                                   0);
    private static final String                 CWD                        = System.getProperty("user.dir");

    public Post2Pre(HenshinResourceSet resourceSet) {
        if (resourceSet != null) {
            tmpResSet = new ConcurrentResourceSet(resourceSet);
        } else {
            tmpResSet = new ConcurrentResourceSet();
        }
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

    public Post2Pre(HenshinResourceSet resSet, int jobs) {
        this(resSet);
        this.jobs = jobs;
        manager.setThreadPoolSize(jobs);
    }

    public Post2Pre(HenshinResourceSet resSet, int jobs2,
            Map<Unit, NestedConditionValidator> unitToLeftNCValidator,
            Map<Unit, NestedConditionValidator> unitToRightNCValidator,
            Collection<Unit> unitsToIgnore) {
        this(resSet, jobs2);
        this.unitToLeftNCValidator = unitToLeftNCValidator;
        this.unitToRightNCValidator = unitToRightNCValidator;
        this.unitsToIgnore = unitsToIgnore;
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

            if (resultContents.isEmpty() || Utils.allDummy(resultContents)
                    || NGCUtils.isTrue(simplRes) || NGCUtils.isFalse(simplRes)) {
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
                                            - timer.elapsed(TimeUnit.SECONDS) > manager
                                                .getSecondsOldResource()) {
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
        private NestedConditionValidator rightNCValidator;

        public PostToLeftAC(Graph formulaHost, Morphism anchor,
                Morphism ruleMorphism,
                NestedConditionValidator leftNCValidator,
                NestedConditionValidator rightNCValidator, Phaser parent,
                int[] parentId) {
            this.host = formulaHost;
            this.anchor = anchor;
            this.newHost = anchor.getTarget();
            this.ruleMorphism = ruleMorphism;
            this.leftNCValidator = leftNCValidator;
            this.rightNCValidator = rightNCValidator;
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
                        leftNCValidator, rightNCValidator, jobId, parent,
                        nextEngine);
                // manager.allOverlaps.incrementAndGet();
                // manager.getIntCounter("AllOverlaps").incrementAndGet();
                executor.execute(graphOverlapTask);
            }
            return cdt;
        }
    }

    public Formula postToLeftAC(Formula post, Rule rule,
            NestedConditionValidator leftACValidator,
            NestedConditionValidator rightACValidator) {
        Formula leftAC = postToLeftACParallel(post, rule, jobs,
                leftACValidator, rightACValidator);

        return leftAC;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Formula postToLeftACParallel(Formula post, Rule rule,
            int numThreads, NestedConditionValidator leftACValidator,
            NestedConditionValidator rightACValidator) {
        // Initialize executor
        LOGGER.debug("Initializing executor with {} threads",
                manager.getThreadPoolSize());
        queue = new ManagedJobQueue();
        if (executor == null) {
            executor = new ThreadPoolExecutor(manager.getThreadPoolSize(),
                    manager.getThreadPoolSize(), 1, TimeUnit.SECONDS, queue);
        }
        manager.setExecutor(executor);

        // Initialize pool of Henshin engines
        if (henginePool == null) {
            henginePool = new Engine[jobs];
            for (int i = 0; i < henginePool.length; i++) {
                henginePool[i] = InterpreterFactory.INSTANCE.createEngine();
            }
        }

        ScheduledExecutorService monitorExecutor = Executors
                .newScheduledThreadPool(1);
        monitorExecutor.scheduleAtFixedRate(new QueueMonitor(), 0, 3,
                TimeUnit.SECONDS);

        Formula leftAC = null;
        try {
            leftAC = postToLeftACInternal(post, rule, leftACValidator,
                    rightACValidator);
        } finally {
            monitorExecutor.shutdown();
            // executor.shutdown();
            try {
                // executor.awaitTermination(10, TimeUnit.SECONDS);
                // } catch (InterruptedException e) {
                // throw new ATLAnalyserRuntimeException(e);
            } finally {
                if (errors.size() > 0) {
                    for (Throwable ex : errors) {
                        LOGGER.error("Error in parallel task", ex);
                    }
                }
                manager.setExecutor(null);
                // for (Engine e : henginePool) {
                // e.shutdown();
                // }
                // henginePool = null;
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
            NestedConditionValidator leftACValidator,
            NestedConditionValidator rightACValidator) {
        // The postcondition is contained in an empty host graph
        Graph hostGraph = NGCUtils.getHostGraph(post);
        hostGraph = (Graph) EcoreUtil.resolve(hostGraph, tmpResSet);
        post = hostGraph.getFormula();
        // Initial anchor is from the empty graph to the RHS
        Morphism anchor = new Morphism(hostGraph, rule.getRhs());

        Morphism ruleMorphism = new Morphism(rule);

        // Resource eResource = hostGraph.eResource();
        // assert eResource != null;
        Phaser completionBarrier = new Phaser(1);

        Formula leftAC = new PostToLeftAC(hostGraph, anchor, ruleMorphism,
                leftACValidator, rightACValidator, completionBarrier,
                new int[] {}).transform(post);
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
    public static Formula leftACToForAllConstraint(Rule rule, Formula leftAC,
            NestedConditionValidator ncValidator) {
        Preconditions.checkArgument(!rule.eIsProxy());
        Preconditions.checkArgument(!leftAC.eIsProxy());

        Graph emptyGraph = NGCUtils.getHostGraph(leftAC);
        assert emptyGraph.getNodes().isEmpty()
                && emptyGraph.getEdges().isEmpty();

        // We need to construct forAll (LHS, leftAC)
        // which is not ( exists (LHS, not (leftAC)))
        NestedCondition cst = HF.createNestedCondition();
        emptyGraph.setFormula(NGCUtils.negate(cst));
        Graph lhs = rule.getLhs();
        cst.setConclusion(lhs);
        Not notLeftAC = NGCUtils.negate(leftAC);
        lhs.setFormula(notLeftAC);

        // Simplify result
        emptyGraph.setFormula(new LimitedSimplifier(1).transform(emptyGraph
                .getFormula()));

        return emptyGraph.getFormula();
    }

    public Formula post2Pre(Formula post, Rule rule) {
        return post2Pre(post, rule, 1, true);
    }

    private static int    i = 0;
    private XMLParserPool xmlParserPool;

    public class Wlp extends HenshinSwitch<Formula> {

        private Formula                  post;
        private int                      maxIterationsForLoops;
        private boolean                  doIntermediateSimplifications;
        private NestedConditionValidator leftValidator;
        private NestedConditionValidator rightValidator;
        private boolean                  withIterationBound;

        public Wlp(Formula postcondition, int maxIterationsForLoops,
                NestedConditionValidator leftValidator,
                NestedConditionValidator rightValidator,
                boolean withIterationBound) {
            this.post = postcondition;
            this.maxIterationsForLoops = maxIterationsForLoops;
            this.leftValidator = leftValidator;
            this.rightValidator = rightValidator;
            this.withIterationBound = withIterationBound;
        }

        public Wlp(Formula postcondition, int maxIterationsForLoops,
                boolean doIntermediateSimplifications,
                boolean withIterationBound) {
            this(postcondition, maxIterationsForLoops, null, null,
                    withIterationBound);
        }

        @Override
        public Formula doSwitch(EObject theEObject) {
            if (unitsToIgnore.contains(theEObject)) {
                manager.metrics.put("Unit", ((Unit) theEObject).getName());
                manager.getIntCounter("AllOverlaps").set(-1);
                manager.dumpMetrics();
                if (leftValidator != null) {
                    post = new FormulaFilter(leftValidator).transform(post);
                }
                return post;
            } else {
                return super.doSwitch(theEObject);
            }
        }

        @Override
        public Formula caseRule(Rule object) {
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

            this.rightValidator = this.rightValidator != null ? this.rightValidator
                    : unitToRightNCValidator.get(object);

            post = new LimitedSimplifier(1).transform(post);
            oldHostGraph.setFormula(post);

            Formula leftAC = null;
            boolean old = false;
            // old = false;
            if (old) {
                leftAC = postToLeftAC(post, rule, leftValidator, rightValidator);
            } else {
                leftAC = new Post2Left(new Morphism(rule), new Morphism(
                        NGCUtils.getHostGraph(post), rule.getRhs()),
                        leftValidator, rightValidator, jobs).transform(post);
            }

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

            // Filter existing LHS formula
            Formula lhsFormula = rule.getLhs().getFormula();
            if (leftValidator != null) {
                lhsFormula = new FormulaFilter(leftValidator)
                        .transform(lhsFormula);
            }

            Formula implication = NGCUtils.createDisjunction(
                    NGCUtils.negate(lhsFormula), leftAC);

            oldHostGraph.setFormula(implication);

            // Formula pre = leftACToExistsConstraint(rule, leftAC,
            // ncValidator);
            Formula pre = leftACToForAllConstraint(rule, implication,
                    leftValidator);
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

        @Override
        public Formula caseSequentialUnit(SequentialUnit object) {
            LOGGER.info("Processing sequence {}", object);
            Formula pre = post;
            List<Unit> subUnits = Lists.newArrayList(object.getSubUnits());
            Collections.reverse(subUnits);
            for (Unit unit : subUnits) {
                pre = new Wlp(pre, maxIterationsForLoops, leftValidator,
                        rightValidator, withIterationBound).doSwitch(unit);
            }
            return pre;
        }

        @Override
        public Formula caseIteratedUnit(IteratedUnit object) {
            LOGGER.info("Processing bounded iteration {}", object);
            int nIterations = Integer.parseInt(object.getIterations());
            Formula pre = post;
            for (int i = 0; i < nIterations; i++) {
                pre = new Wlp(pre, maxIterationsForLoops, leftValidator,
                        rightValidator, withIterationBound).doSwitch(object
                        .getSubUnit());
            }
            return pre;
        }

        @Override
        public Formula caseIndependentUnit(IndependentUnit object) {
            EList<Unit> subUnits = object.getSubUnits();

            List<Formula> preconditions = Lists.newArrayList();

            for (Unit unit : subUnits) {
                Graph newHostGraph = HF.createGraph();
                Resource newRes = createResourceWithObj(newHostGraph);
                Formula newPost = CopierWithResolveErrorNotification.copy(post);
                newHostGraph.setFormula(newPost);
                Formula newPre = new Wlp(newPost, maxIterationsForLoops,
                        leftValidator, rightValidator, withIterationBound)
                        .doSwitch(unit);

                preconditions.add(newPre);
            }

            Formula result = NGCUtils.createConjunction(preconditions);

            return result;
        }

        @Override
        public Formula caseLoopUnit(LoopUnit object) {
            LOGGER.info("Processing unbounded loop {}", object);

            manager.metrics.put("Unit", object.getName());
            Stopwatch timer = Stopwatch.createStarted();

            // initialise counters if they don't exist
            manager.getIntCounter("AllOverlaps");
            manager.getIntCounter("ATLSemFilter");
            manager.getIntCounter("NoPushoutFilter");
            manager.getIntCounter("LeftNCFilter");

            List<NestedCondition> postNCs = NGCUtils
                    .getAllNestedConditions(post);
            manager.metrics.put("PostSize", postNCs.size());

            DoubleSummaryStatistics numNodesStat = postNCs.stream()
                    .mapToDouble(nc -> nc.getConclusion().getNodes().size())
                    .summaryStatistics();
            DoubleSummaryStatistics numEdgesStat = postNCs.stream()
                    .mapToDouble(nc -> nc.getConclusion().getEdges().size())
                    .summaryStatistics();
            DoubleSummaryStatistics graphSizeStat = postNCs
                    .stream()
                    .mapToDouble(
                            nc -> nc.getConclusion().getNodes().size()
                                    + nc.getConclusion().getEdges().size())
                    .summaryStatistics();

            manager.metrics.put("PostGraphSizeAvg", graphSizeStat.getAverage());
            manager.metrics.put("PostNodesAvg", numNodesStat.getAverage());
            manager.metrics.put("PostEdgesAvg", numEdgesStat.getAverage());

            manager.getIntCounter("PerformedOverlaps");
            manager.resetCounters();

            Formula originalPost = CopierWithResolveErrorNotification
                    .copy(post);
            Graph emptyGraph = HF.createGraph();
            emptyGraph.setFormula(originalPost);
            createResourceWithObj(emptyGraph);

            Formula pre;
            boolean doImplication = true;
            if (doImplication) {
                Resource eResource = post.eResource();
                Graph g = HF.createGraph("Plop");
                eResource.getContents().add(g);
                g.setFormula(NGCUtils.createFalse());

                Formula wlpFalse = new Wlp(g.getFormula(),
                        maxIterationsForLoops, null, rightValidator,
                        withIterationBound).doSwitch(object.getSubUnit());

                // Formula wlpFalse = leftACToForAllConstraint(
                // (Rule) EcoreUtil.copy(object.getSubUnit()), g.getFormula(),
                // null);

                Formula implication = NGCUtils.createDisjunction(
                        NGCUtils.negate(wlpFalse), post);
                // implication = new
                // LimitedSimplifier(1).transform(implication);
                pre = implication;
                // Formula pre = post;
                g.setFormula(pre);
                // Formula pre = post;
            } else {
                pre = post;
            }

            Formula result = caseInfiniteLoop(object, pre,
                    maxIterationsForLoops - 1);

            // construct N applications of the subunit
            SequentialUnit seq = HF.createSequentialUnit();
            for (int i = 0; i < maxIterationsForLoops; i++) {
                seq.getSubUnits().add(object.getSubUnit());
            }

            NestedConditionValidator leftValidator = unitToLeftNCValidator
                    .get(object);
            NestedConditionValidator rightValidator = unitToRightNCValidator
                    .get(object);
            Formula lastPre = new Wlp(originalPost, maxIterationsForLoops,
                    leftValidator, rightValidator, withIterationBound)
                    .doSwitch(seq);

            result = NGCUtils.createConjunction(result, lastPre);

            // Unit subUnit = object.getSubUnit();
            //
            // Graph cdtGraph = HF.createGraph();
            // createResourceWithObj(cdtGraph);
            // cdtGraph.setName("LimitExecutionsOf" + subUnit.getName());
            // Formula ffalse = NGCUtils.createFalse();
            // cdtGraph.setFormula(ffalse);
            //
            // Formula wlpFalse = new Wlp(ffalse, maxIterationsForLoops,
            // doIntermediateSimplifications, traceMM, null,
            // withIterationBound).doSwitch(object.getSubUnit());
            //
            // Formula implication = NGCUtils.createDisjunction(
            // NGCUtils.negate(wlpFalse), NGCUtils.createFalse());
            // cdtGraph.setFormula(implication);
            // // Formula pre = post;
            //
            // Formula existanceResult =
            // NGCUtils.negate(caseInfiniteLoop(object,
            // implication));

            // result = NGCUtils.createConjunction(existanceResult, result);

            boolean useWlpFalse = true;
            if (withIterationBound && !useWlpFalse) {
                // Add condition that limits matches of the iterated rule
                Rule rule = (Rule) object.getSubUnit();
                Graph lhs = rule.getLhs();
                Graph cdtGraph = HF.createGraph();
                cdtGraph.setName("LimitMatchesOf" + rule.getName());
                for (int i = 0; i < maxIterationsForLoops + 1; i++) {
                    Graph lhsCopy = CopierWithResolveErrorNotification
                            .copy(lhs);
                    cdtGraph.getNodes().addAll(lhsCopy.getNodes());
                    cdtGraph.getEdges().addAll(lhsCopy.getEdges());
                    if (lhsCopy.getFormula() != null) {
                        cdtGraph.setFormula(NGCUtils.createConjunction(
                                cdtGraph.getFormula(), lhsCopy.getFormula()));
                    }
                }
                Not neg = HF.createNot();
                NestedCondition nc = HF.createNestedCondition();
                neg.setChild(nc);
                nc.setConclusion(cdtGraph);

                Formula filteredNeg = neg;
                if (leftValidator != null) {
                    filteredNeg = new FormulaFilter(leftValidator)
                            .transform(neg);
                }

                result = NGCUtils.createConjunction(filteredNeg, result);
            } else if (withIterationBound && useWlpFalse) {
                Unit subUnit = object.getSubUnit();
                IteratedUnit iteratedUnit = HF.createIteratedUnit();
                iteratedUnit.setSubUnit(subUnit);
                iteratedUnit.setIterations(Integer
                        .toString(maxIterationsForLoops + 1));

                Graph cdtGraph = HF.createGraph();
                createResourceWithObj(cdtGraph);
                cdtGraph.setName("LimitExecutionsOf" + subUnit.getName());
                Formula ffalse = NGCUtils.createFalse();
                cdtGraph.setFormula(ffalse);
                Formula execLimit = new Wlp(ffalse, maxIterationsForLoops,
                        leftValidator, rightValidator, withIterationBound)
                        .doSwitch(iteratedUnit);

                result = NGCUtils.createConjunction(execLimit, result);
            }

            emptyGraph.setFormula(result);
            result = new LimitedSimplifier(1).transform(result);

            emptyGraph.setFormula(result);

            if (DUMP_AFTER_EACH_LOOPUNIT) {
                Resource res = tmpResSet.createResource(URI.createFileURI(CWD
                        + "/" + Integer.toString(i++) + "_" + object.getName()
                        + ".xmi"));
                res.getContents().add(EcoreUtil.copy(result));
                try {
                    res.save(Collections.emptyMap());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                res.unload();
            }

            // manager.ruleDurationMillis.set(timer.stop().elapsed(
            // TimeUnit.MILLISECONDS));
            manager.metrics.put("RuleDurationMillis",
                    timer.stop().elapsed(TimeUnit.MILLISECONDS));

            // manager.preSize.set(NGCUtils.getAllNestedConditions(result).size());
            List<NestedCondition> preNCs = NGCUtils
                    .getAllNestedConditions(result);
            manager.metrics.put("PreSize", preNCs.size());
            numNodesStat = preNCs.stream()
                    .mapToDouble(nc -> nc.getConclusion().getNodes().size())
                    .summaryStatistics();
            numEdgesStat = preNCs.stream()
                    .mapToDouble(nc -> nc.getConclusion().getEdges().size())
                    .summaryStatistics();
            graphSizeStat = preNCs
                    .stream()
                    .mapToDouble(
                            nc -> nc.getConclusion().getNodes().size()
                                    + nc.getConclusion().getEdges().size())
                    .summaryStatistics();

            manager.metrics.put("PreGraphSizeAvg", graphSizeStat.getAverage());
            manager.metrics.put("PreNodesAvg", numNodesStat.getAverage());
            manager.metrics.put("PreEdgesAvg", numEdgesStat.getAverage());

            // manager.dumpCounters(object.getName());
            manager.dumpMetrics();

            return result;
        }

        private Formula caseInfiniteLoop(LoopUnit object, Formula postCdt,
                int maxIterations) {
            Graph hostGraph = NGCUtils.getHostGraph(postCdt);
            hostGraph = (Graph) EcoreUtil.resolve(hostGraph, tmpResSet);

            // copy postcondition for final result
            Formula originalPost = CopierWithResolveErrorNotification
                    .copy(postCdt);

            NestedConditionValidator preValidator = unitToLeftNCValidator
                    .get(object);

            if (preValidator != null) {
                originalPost = new FormulaFilter(preValidator)
                        .transform(originalPost);
            }

            // if (originalPost instanceof Or) {
            // Or or = (Or) originalPost;
            // originalPost = or.getRight();
            // }
            originalPost = dumpToFile(originalPost);

            // init result with a copy of the postcondition
            Graph resultHostGraph = HF.createGraph();
            createResourceWithObj(resultHostGraph);
            resultHostGraph.setName("Pre_" + object.getName());
            Formula result = originalPost;
            resultHostGraph.setFormula(result);

            for (int i = 0; i < maxIterations; i++) {
                // unloadTmpFilesExcept(pre.eResource());
                NestedConditionValidator validator = null;

                // apply the validator only at the last iteration of the
                // subunit
                validator = i == maxIterations - 1 ? preValidator : null;
                postCdt = new Wlp(postCdt, maxIterations, validator,
                        rightValidator, withIterationBound).doSwitch(object
                        .getSubUnit());

                resultHostGraph = (Graph) EcoreUtil.resolve(resultHostGraph,
                        tmpResSet);
                if (i < maxIterations - 1) {
                    // If there is a next iteration, store a copy of postCdt in
                    // the conjunction because the next iteration will modify
                    // postCdt. The copy should be filtered.
                    Formula postCdtCopy = CopierWithResolveErrorNotification
                            .copy(postCdt);
                    if (preValidator != null) {
                        postCdtCopy = new FormulaFilter(preValidator)
                                .transform(postCdtCopy);
                    }
                    result = NGCUtils.createConjunction(result, postCdtCopy);
                } else {
                    result = NGCUtils.createConjunction(result, postCdt);
                }
                resultHostGraph.setFormula(result);

                if (NGCUtils.isTrue(postCdt)) {
                    break;
                }
            }

            result = (Formula) EcoreUtil.resolve(result, tmpResSet);
            result = new LimitedSimplifier(0).transform(result);
            resultHostGraph.setFormula(result);

            return result;
        }
    }

    class PostToLeftACTask implements Runnable, IWithId {

        private NestedCondition                        post;
        private Graph                                  host;
        private int[]                                  id;
        private CompletionBarrier                      completionBarrier;
        private Phaser                                 parent;
        private Morphism                               anchor;
        private Morphism                               ruleMorphism;
        private NestedConditionValidator               leftNCValidator;
        private NestedConditionValidator               rightNCValidator;
        private Engine                                 hengine;
        private Resource                               ncRes;
        private Resource                               hostRes;
        private Resource[]                             anchorRes;
        private Resource[]                             ruleRes;
        private Triplet<Resource, EObject, EReference> destination;

        public PostToLeftACTask(Graph host, NestedCondition cdt,
                Morphism anchor, Morphism ruleMorphism,
                Triplet<Resource, EObject, EReference> destination,
                NestedConditionValidator leftNCValidator,
                NestedConditionValidator rightNCValidator, int[] id,
                Phaser parent, Engine hengine) {
            manager.getIntCounter("AllOverlaps").incrementAndGet();

            this.ncRes = cdt.eResource();
            this.post = Utils.createProxy(cdt);
            if (!EcoreUtil.getURI(this.post).trimFragment()
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
            this.leftNCValidator = leftNCValidator != null ? leftNCValidator
                    : NestedConditionValidator.TRUE;
            this.rightNCValidator = rightNCValidator != null ? rightNCValidator
                    : NestedConditionValidator.TRUE;
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
            completionBarrier = new CompletionBarrier(result, post,
                    destination, parent, this.id, newRes, hostRes);
            completionBarrier.register();

            Graph rightHost = anchor.getTarget();
            Graph postConclusion = post.getConclusion();

            OVRLP_LOGGER.trace("Starting Overlapping");
            Stopwatch timer = Stopwatch.createStarted();

            // Create a copy of the host to work on in parallel
            Morphism rightHostToRightHostCopy = GraphCopier.copy(rightHost);
            Graph rightHostCopy = rightHostToRightHostCopy.getTarget();
            Morphism anchorCopy = anchor.compose(rightHostToRightHostCopy);

            Morphism a = new Morphism(post, host);
            Formula nestedPost = postConclusion.getFormula();

            Morphism concToConcCopy = GraphCopier.copy(postConclusion);
            Graph postConcCopy = concToConcCopy.getTarget();
            Morphism aCopy = a.compose(concToConcCopy);

            boolean excludeEmptyOverlap = false;
            boolean enforceEMFConstraints = true;
            boolean preventEdgeAutoMapping = false;
            GraphOverlapGenerator overlaps = new GraphOverlapGenerator(
                    rightHostCopy, postConcCopy, new Pair<Morphism, Morphism>(
                            anchorCopy, aCopy), excludeEmptyOverlap,
                    enforceEMFConstraints, preventEdgeAutoMapping, hengine);

            List<Triplet<NestedCondition, Morphism, Morphism>> validOveraps = Lists
                    .newArrayList();
            Iterator<Pair<Morphism, Morphism>> overlapIterator = overlaps
                    .iterator();
            while (overlapIterator.hasNext()) {
                Pair<Morphism, Morphism> pair = overlapIterator.next();
                Morphism rightHostCopyToOverlap = pair.getValue0();
                Morphism newRightCdtMorphism = rightHostToRightHostCopy
                        .compose(rightHostCopyToOverlap);

                NestedCondition newRightCdt = NGCUtils
                        .createNestedCondition(newRightCdtMorphism);

                Graph newRightConclusion = newRightCdt.getConclusion();

                if (!rightNCValidator.isValid(newRightCdt)) {
                    // manager.atlSemanticsFilter.incrementAndGet();
                    manager.getIntCounter("ATLSemFilter").incrementAndGet();
                    RIGHT_NC_VALIDATION_LOGGER.debug("ATL semantics violation");
                } else {
                    Pair<Morphism, Morphism> pushoutComplement;
                    synchronized (parent) {
                        pushoutComplement = Morphism.getPushoutComplement(
                                ruleMorphism, newRightCdtMorphism);
                    }

                    if (pushoutComplement == null) {
                        // manager.noPushoutFilter.incrementAndGet();
                        manager.getIntCounter("NoPushoutFilter")
                                .incrementAndGet();
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
                                            + newRightCdtMorphism.getTarget()
                                                    .getName() + ")");
                        }

                        if (leftNCValidator != null
                                && !leftNCValidator.isValid(newLeftCdt)) {
                            // manager.leftNCFilter.incrementAndGet();
                            manager.getIntCounter("LeftNCFilter")
                                    .incrementAndGet();
                            LEFT_NC_VALIDATION_LOGGER
                                    .debug("Removing invalid NC");
                        } else {
                            contents.add(newLeftCdt);
                            result.add(Utils.createProxy(newLeftCdt));
                            if (nestedPost != null) {
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

                // Morphism.dispose(pair.getValue0());
                // Morphism.dispose(pair.getValue1());
            }

            // manager.performedOverlaps.incrementAndGet();
            manager.getIntCounter("PerformedOverlaps").incrementAndGet();

            if (!contents.isEmpty()) {
                Annotation annotation = HF.createAnnotation();
                annotation.setKey("Task " + Utils.toString(id));
                contents.add(annotation);
                tmpResSet.lockWrite();
                tmpResSet.getResources().add(newRes);
                tmpResSet.unlockWrite();
            }

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
                Resource toTransformRes = null;

                // the subtasks will attempt in-place transformation of
                // nestedFormula. So we need to copy it first...
                if (iterator.hasNext()) {
                    toTransform = CopierWithResolveErrorNotification
                            .copy(nestedPost);
                    toTransformRes = createResourceWithObj(toTransform);
                } else {
                    toTransform = nestedPost;
                    new AbstractFormulaVisitor() {
                        public Formula caseNestedCondition(NestedCondition nc) {
                            for (Mapping mapping : nc.getMappings()) {
                                boolean resolveSuccess = mapping.getOrigin()
                                        .eContainer() == postConclusion;
                                assert resolveSuccess;
                            }
                            return nc;
                        };
                    }.transform(toTransform);
                }

                newLeftCdt.getConclusion().setFormula(toTransform);

                List<NestedCondition> nestedConditions = NGCUtils
                        .getNestedConditions(toTransform);

                // Create subtasks
                List<PostToLeftACTask> subTasks = Lists.newArrayList();
                jobId[jobId.length - 2]++;
                jobId[jobId.length - 1] = 0;
                for (NestedCondition nc : nestedConditions) {
                    if (!NGCUtils.isTrue(nc)) {
                        jobId[jobId.length - 1]++;
                        Engine nextEngine = henginePool[hengineIndex
                                .incrementAndGet() % henginePool.length];
                        PostToLeftACTask subTask = new PostToLeftACTask(
                                postConclusion, nc, newAnchor, newRuleMorphism,
                                Triplet.with(nc.eContainer().eResource(),
                                        nc.eContainer(),
                                        (EReference) nc.eContainingFeature()),
                                leftNCValidator, rightNCValidator, jobId,
                                completionBarrier, nextEngine);
                        subTasks.add(subTask);
                    }
                }

                if (toTransformRes != null) {
                    saveAndUnload(toTransformRes);
                }

                // manager.allOverlaps.addAndGet(subTasks.size());
                // manager.getIntCounter("AllOverlaps").addAndGet(subTasks.size());

                LOGGER.trace("Done task " + Utils.toString(id) + ". Starting "
                        + subTasks.size() + " subtasks");

                ((EngineImpl) hengine).clearCache();

                // start sub tasks
                for (PostToLeftACTask subTask : subTasks) {
                    completionBarrier.register();
                    executor.execute(subTask);
                }

            }

            overlaps.dispose();

            resLock.readLock().unlock();
            completionBarrier.arrive();
        }

        private void resolveData() {
            NestedCondition r = (NestedCondition) Utils.resolve(post, ncRes);
            assert !r.eIsProxy() : "Failed to resolve: " + EcoreUtil.getURI(r);
            post = r;

            Formula formula = post.getConclusion().getFormula();
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

            MappingList mappings = post.getMappings();
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
            int maxIterationsForLoops, boolean withIterationBound) {
        Graph postHostCopy = CopierWithResolveErrorNotification.copy(NGCUtils
                .getHostGraph(postcondition));
        Formula post = postHostCopy.getFormula();
        createResourceWithObj(postHostCopy);
        return new Wlp(post, maxIterationsForLoops, null, null,
                withIterationBound).doSwitch(program);
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
        if (executor != null) {
            executor.shutdownNow();
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

    private void saveAndUnload(Resource tmpRes) {
        if (DISABLE_ALL_DUMPING) {
            return;
        }
        try {
            tmpRes.save(Collections.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        tmpRes.unload();
    }

    private Resource newTmpResource() {
        Resource tmpRes = newUnattachedTmpResource();
        tmpResSet.lockWrite();
        try {
            tmpResSet.getResources().add(tmpRes);
        } finally {
            tmpResSet.unlockWrite();
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
