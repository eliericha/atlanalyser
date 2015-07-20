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

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.google.common.base.Stopwatch;

public class Post2PreManager implements Post2PreManagerMBean {

    private static final double      SMOOTHING_FACTOR  = 0.3;
    private static final int         NUM_NCS_TO_DUMP   = 1000;
    private static final double      LOW_MEMORY_THRES  = 0.5;
    private static final double      STOP_DUMP_AT      = 0.2;

    private static final Logger      LOGGER            = LogManager
                                                               .getLogger(Post2PreManager.class
                                                                       .getSimpleName());
    private final MemoryMXBean       memoryBean        = ManagementFactory
                                                               .getMemoryMXBean();
    private int                      queueSize         = 0;
    private int                      prevQueueSize     = 0;
    private long                     lastReadTime      = 1;
    private int                      threadPoolSize    = Runtime
                                                               .getRuntime()
                                                               .availableProcessors();
    private ThreadPoolExecutor       executor          = null;
    private double                   rate;
    private double                   memUsageRate      = 0;
    private double                   lowMemThresh      = LOW_MEMORY_THRES;
    private double                   stopDumpThresh    = STOP_DUMP_AT;
    private int                      numNCsToDump      = NUM_NCS_TO_DUMP;
    // smoothing factor
    private double                   smoothingFactor   = SMOOTHING_FACTOR;
    private ScheduledExecutorService backgroundMonitor;
    private ResourceSet              resourceSet;
    private int                      numLoadedResources;
    private int                      numResources;
    public final AtomicInteger       allOverlaps       = new AtomicInteger(0);
    public final AtomicInteger       performedOverlaps = new AtomicInteger(0);

    public Post2PreManager() {
        // backgroundMonitor = Executors.newSingleThreadScheduledExecutor();
        // backgroundMonitor.scheduleAtFixedRate(new Runnable() {
        //
        // @Override
        // public void run() {
        // try {
        // memUsageRate = getCurrentMemUsage();
        // } catch (Throwable ex) {
        // LOGGER.error("", ex);
        // }
        //
        // }
        //
        // }, 0, 3, TimeUnit.SECONDS);
    }

    public double getCurrentMemUsage() {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        final long used = heapUsage.getUsed();
        final long max = heapUsage.getMax();
        double currentMemUsage = (double) used / max;
        memUsageRate = smoothingFactor * currentMemUsage
                + (1 - smoothingFactor) * memUsageRate;
        return memUsageRate;
    }

    @Override
    protected void finalize() throws Throwable {
        // backgroundMonitor.shutdownNow();
        super.finalize();
    }

    @Override
    public int getOverlapJobsQueueSize() {
        prevQueueSize = queueSize;
        if (executor != null) {
            BlockingQueue<Runnable> queue = executor.getQueue();
            queueSize = queue.size();
            long currentReadTime = System.currentTimeMillis();
            long timeDelta = currentReadTime - lastReadTime;
            rate = timeDelta != 0 ? (queueSize - prevQueueSize) * 1000.0
                    / timeDelta : 0;
        }
        return queueSize;
    }

    @Override
    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    @Override
    public synchronized void setThreadPoolSize(int n) {
        if (n > 0) {
            LOGGER.info("Setting Thread Pool Size to {}", n);
            threadPoolSize = n;
            if (executor != null) {
                executor.setCorePoolSize(threadPoolSize);
                executor.setMaximumPoolSize(threadPoolSize);
            }
        }
    }

    public synchronized void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Override
    public int getQueueSizeDelta() {
        return queueSize - prevQueueSize;
    }

    /**
     * 
     * @return double between 0 and 1 representing the rate of used heap memory.
     */
    @Override
    public double getHeapMemoryUsageRate() {
        return memUsageRate = getCurrentMemUsage();
    }

    @Override
    public double getLowMemoryThreshold() {
        return lowMemThresh;
    }

    @Override
    public void setLowMemoryThreshold(double v) {
        if (v >= 0 && v <= 1) {
            lowMemThresh = v;
        }
    }

    @Override
    public int getNCsToDump() {
        return numNCsToDump;
    }

    @Override
    public void setNCsToDump(int v) {
        if (v > 0) {
            numNCsToDump = v;
        }
    }

    @Override
    public double getSmoothingFactor() {
        return smoothingFactor;
    }

    @Override
    public void setSmoothingFactor(double v) {
        if (v >= 0 && v <= 1) {
            smoothingFactor = v;
        }
    }

    public boolean lowMemory() {
        return getHeapMemoryUsageRate() >= getLowMemoryThreshold();
    }

    @Override
    public int getNumLoadedResources() {
        if (resourceSet != null) {
            try {
                Stopwatch timer = Stopwatch.createStarted();
                Resource[] array = (Resource[]) resourceSet.getResources()
                        .toArray();
                int loaded = 0;
                for (Resource r : array) {
                    if (r != null && r.isLoaded()) {
                        loaded++;
                    }
                }
                numLoadedResources = loaded;
                LOGGER.trace("Update took " + timer);
            } catch (Throwable ex) {
                LOGGER.error("", ex);
            }
        } else {
            numLoadedResources = 0;
        }
        return numLoadedResources;
    }

    @Override
    public int getNumResources() {
        if (resourceSet != null) {
            try {
                numResources = resourceSet.getResources().size();
            } catch (Throwable ex) {
                LOGGER.error("", ex);
            }
        } else {
            numResources = 0;
        }
        return numResources;
    }

    public void setResourceSet(ResourceSet resSet) {
        this.resourceSet = resSet;
    }

    @Override
    public int getAllOverlaps() {
        return allOverlaps.get();
    }

    @Override
    public int getPerformedOverlaps() {
        return performedOverlaps.get();
    }

    @Override
    public double getStopDumpThreshold() {
        return stopDumpThresh;
    }

    @Override
    public void setStopDumpThreshold(double v) {
        if (v >= 0 && v <= 1) {
            stopDumpThresh = v;
        }
    }
}