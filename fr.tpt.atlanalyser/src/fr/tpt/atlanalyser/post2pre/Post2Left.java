package fr.tpt.atlanalyser.post2pre;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.InterpreterFactory;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.javatuples.Pair;

import com.google.common.collect.Lists;

import fr.tpt.atlanalyser.ATLAnalyserRuntimeException;
import fr.tpt.atlanalyser.overlapping.GraphOverlapper;
import fr.tpt.atlanalyser.post2pre.formulas.CopyingSimplifyingTransformer;
import fr.tpt.atlanalyser.post2pre.formulas.FullSimplifier;
import fr.tpt.atlanalyser.post2pre.ncvalidators.NestedConditionValidator;
import fr.tpt.atlanalyser.utils.Morphism;
import fr.tpt.atlanalyser.utils.NGCUtils;

public class Post2Left extends CopyingSimplifyingTransformer {

    private static final Logger          LOGGER  = LogManager
                                                         .getLogger(Post2Left.class
                                                                 .getSimpleName());

    private BlockingQueue<Runnable>      queue   = new<Task> ManagedJobQueue();

    private static final Post2PreManager manager = Post2PreManager.INSTANCE;

    private class Task implements Runnable, IWithId {

        private OverlapResult overlap;
        private Formula       subFormula;
        private int[]         id;

        public Task(int[] id, OverlapResult r, Formula subFormula) {
            this.id = id;
            this.overlap = r;
            this.subFormula = subFormula;
        }

        @Override
        public void run() {
            Post2LeftInternal post2Left = new Post2LeftInternal(id,
                    overlap.rule, overlap.anchor, leftValidator,
                    rightValidator, null);

            Formula newSubFormula = post2Left.transform(subFormula);
            if (!NGCUtils.isTrue(newSubFormula)) {
                overlap.leftNC.getConclusion().setFormula(newSubFormula);
            }
            barrier.arrive();
        }

        @Override
        public int[] getId() {
            return id;
        }
    }

    private Morphism                 rule;
    private Graph                    lhs;
    private Graph                    rhs;
    private Morphism                 anchor;
    private Graph                    host;
    private NestedConditionValidator leftValidator;
    private NestedConditionValidator rightValidator;
    private Engine                   hengine;

    private int                      jobs;

    private ThreadPoolExecutor       ex;

    public Post2Left(Morphism rule, Morphism anchor,
            NestedConditionValidator leftValidator,
            NestedConditionValidator rightValidator, Engine hengine, int jobs) {
        this.rule = rule;
        this.lhs = rule.getSource();
        this.rhs = rule.getTarget();
        this.anchor = anchor;
        this.host = anchor.getSource();
        this.leftValidator = leftValidator != null ? leftValidator
                : NestedConditionValidator.TRUE;
        this.rightValidator = rightValidator != null ? rightValidator
                : NestedConditionValidator.TRUE;
        assert this.rhs == anchor.getTarget();

        this.hengine = hengine != null ? hengine : InterpreterFactory.INSTANCE
                .createEngine();
        this.jobs = jobs;
    }

    public Post2Left(Morphism rule, Morphism anchor,
            NestedConditionValidator leftValidator,
            NestedConditionValidator rightValidator, int jobs) {
        this(rule, anchor, leftValidator, rightValidator, null, jobs);
    }

    Phaser barrier = new Phaser();

    @Override
    public Formula transform(Formula formula) {
        LOGGER.info("Starting thread pool of size {}", jobs);
        this.ex = new ThreadPoolExecutor(this.jobs, this.jobs, 1,
                TimeUnit.SECONDS, queue);
        manager.setExecutor(this.ex);
        ScheduledExecutorService monitor = Executors.newScheduledThreadPool(1);
        monitor.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                LOGGER.debug("Task queue size: {}", queue.size());
            }
        }, 0, 3, TimeUnit.SECONDS);
        barrier.register();
        Formula result = new Post2LeftInternal(new int[] { 1 }, rule, anchor,
                leftValidator, rightValidator, hengine).transform(formula);
        barrier.arriveAndAwaitAdvance();
        monitor.shutdown();
        result = new FullSimplifier().transform(result);

        this.ex.shutdown();

        try {
            this.ex.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new ATLAnalyserRuntimeException(e);
        }

        if (!ex.isTerminated()) {
            throw new ATLAnalyserRuntimeException(
                    "Unable to shutdown thread pool executor");
        }

        return result;
    }

    private class Post2LeftInternal extends CopyingSimplifyingTransformer {
        private Morphism                 rule;
        private Graph                    lhs;
        private Graph                    rhs;
        private Graph                    host;
        private Morphism                 anchor;
        private NestedConditionValidator leftValidator;
        private NestedConditionValidator rightValidator;
        private Engine                   hengine;
        private int[]                    id;

        public Post2LeftInternal(int[] id, Morphism rule, Morphism anchor,
                NestedConditionValidator leftValidator,
                NestedConditionValidator rightValidator, Engine hengine) {
            this.id = id;
            this.rule = rule;
            this.lhs = rule.getSource();
            this.rhs = rule.getTarget();
            this.anchor = anchor;
            this.host = anchor.getSource();
            this.leftValidator = leftValidator != null ? leftValidator
                    : NestedConditionValidator.TRUE;
            this.rightValidator = rightValidator != null ? rightValidator
                    : NestedConditionValidator.TRUE;
            assert this.rhs == anchor.getTarget();

            this.hengine = hengine != null ? hengine
                    : InterpreterFactory.INSTANCE.createEngine();
        }

        @Override
        public Formula caseNestedCondition(NestedCondition nc) {
            if (NGCUtils.isTrue(nc)) {
                return NGCUtils.createTrue();
            }

            Formula res = null;

            Morphism a = new Morphism(nc);
            assert a.getSource() == this.host;
            assert a.getSource() == anchor.getSource();

            boolean excludeEmptyOverlap = false;
            boolean enforceEMFConstraints = true;
            boolean fixEdgeAutoMapping = false;
            GraphOverlapper.GraphOverlapGenerator overlaps = new GraphOverlapper.GraphOverlapGenerator(
                    anchor, a, excludeEmptyOverlap, enforceEMFConstraints,
                    fixEdgeAutoMapping);

            manager.getIntCounter("AllOverlaps").incrementAndGet();

            List<OverlapResult> overlapResults = Lists.newArrayList();
            for (Pair<Morphism, Morphism> pair : overlaps) {
                Morphism newAnchor = pair.getValue1();
                Morphism newRightMorphism = pair.getValue0();

                NestedCondition newRightNC = NGCUtils
                        .createNestedCondition(newRightMorphism);

                if (!rightValidator.isValid(newRightNC)) {
                    manager.getIntCounter("ATLSemFilter").incrementAndGet();
                } else {
                    Pair<Morphism, Morphism> poComplement = Morphism
                            .getPushoutComplement(rule, newRightMorphism);
                    if (poComplement == null) {
                        manager.getIntCounter("NoPushoutFilter")
                                .incrementAndGet();
                    } else {
                        Morphism newLeftMorphism = poComplement.getValue0();
                        Morphism newRule = poComplement.getValue1();

                        NestedCondition newLeftNC = NGCUtils
                                .createNestedCondition(newLeftMorphism);

                        if (!leftValidator.isValid(newLeftNC)) {
                            manager.getIntCounter("LeftNCFilter")
                                    .incrementAndGet();
                        } else {
                            overlapResults.add(new OverlapResult(
                                    newLeftMorphism, newLeftNC, newRule,
                                    newRightMorphism, newRightNC, newAnchor));
                        }
                    }
                }
            }

            Formula subFormula = nc.getConclusion().getFormula();
            if (subFormula != null) {
                barrier.register();
                int[] jobId = Arrays.copyOf(id, id.length + 1);
                jobId[jobId.length - 1] = 0;
                for (Iterator<OverlapResult> iterator = overlapResults
                        .iterator(); iterator.hasNext();) {
                    OverlapResult r = (OverlapResult) iterator.next();

                    barrier.register();
                    jobId[jobId.length - 1]++;
                    Task task = new Task(jobId.clone(), r, subFormula);
                    ex.execute(task);
                }
                barrier.arrive();
            }

            res = NGCUtils.createDisjunction(overlapResults.stream()
                    .map(r -> r.leftNC).toArray(NestedCondition[]::new));

            // if (!NGCUtils.isTrue(res)) {
            // this.lhs.setFormula(res);
            // }

            return res;
        }

        @Override
        public Formula transform(Formula formula) {
            assert NGCUtils.getHostGraph(formula) == this.host;
            Formula leftNC = super.transform(formula);
            return leftNC;
        }
    }

    private static class OverlapResult {
        final Morphism        leftMorph;
        final NestedCondition leftNC;
        final Morphism        rule;
        final Morphism        rightMorph;
        final NestedCondition rightNC;
        final Morphism        anchor;

        public OverlapResult(Morphism leftMorph, NestedCondition leftNC,
                Morphism rule, Morphism rightMorph, NestedCondition rightNC,
                Morphism anchor) {
            this.leftMorph = leftMorph;
            this.leftNC = leftNC;
            this.rule = rule;
            this.rightMorph = rightMorph;
            this.rightNC = rightNC;
            this.anchor = anchor;
        }
    }

}
