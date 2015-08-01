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
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;

public class Post2PreManager implements Post2PreManagerMBean {

    private static final double         SMOOTHING_FACTOR = 0.3;
    private static final int            NUM_NCS_TO_DUMP  = 1000;
    private static final double         LOW_MEMORY_THRES = 0.5;
    private static final double         STOP_DUMP_AT     = 0.2;

    private static final Logger         LOGGER           = LogManager
                                                                 .getLogger(Post2PreManager.class
                                                                         .getSimpleName());

    public static final Post2PreManager INSTANCE         = new Post2PreManager();

    private Post2PreManager() {
    }

    static {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = null;
        try {
            name = new ObjectName(
                    "fr.tpt.atlanalyser.post2pre:type=Post2PreManager");
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            mbs.registerMBean(INSTANCE, name);
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        } catch (NotCompliantMBeanException e) {
            e.printStackTrace();
        }
    }

    private final MemoryMXBean       memoryBean         = ManagementFactory
                                                                .getMemoryMXBean();
    private int                      queueSize          = 0;
    private int                      prevQueueSize      = 0;
    private long                     lastReadTime       = 1;
    private int                      threadPoolSize     = Runtime
                                                                .getRuntime()
                                                                .availableProcessors();
    private ThreadPoolExecutor       executor           = null;
    private double                   rate;
    private double                   memUsageRate       = 0;
    private double                   lowMemThresh       = LOW_MEMORY_THRES;
    private double                   stopDumpThresh     = STOP_DUMP_AT;
    private int                      numNCsToDump       = NUM_NCS_TO_DUMP;
    // smoothing factor
    private double                   smoothingFactor    = SMOOTHING_FACTOR;
    private ScheduledExecutorService backgroundMonitor;
    private ResourceSet              resourceSet;
    private int                      numLoadedResources;
    private int                      numResources;

    private int                      secondsOldResource = 40;

    public final Map<String, Object> metrics            = Collections
                                                                .synchronizedMap(Maps
                                                                        .newLinkedHashMap());

    private Logger                   METRICS            = LogManager
                                                                .getLogger("Metrics");
    private AtomicBoolean            headerChanged      = new AtomicBoolean(
                                                                false);

    public void resetCounters() {
        for (Object o : metrics.values()) {
            if (o instanceof AtomicInteger) {
                AtomicInteger cInt = (AtomicInteger) o;
                cInt.set(0);
            } else if (o instanceof AtomicLong) {
                AtomicLong cLong = (AtomicLong) o;
                cLong.set(0);
            }
        }
    }

    public AtomicLong getIntCounter(String name) {
        Object object = metrics.get(name);
        if (object == null) {
            object = new AtomicLong(0);
            metrics.put(name, object);
            headerChanged.set(true);
        }
        if (object instanceof AtomicLong) {
            return (AtomicLong) object;
        } else {
            return null;
        }
    }

    public void dumpMetrics() {
        if (headerChanged.getAndSet(false)) {
            METRICS.info(Joiner.on(", ").join(metrics.keySet()));
        }
        METRICS.info(Joiner.on(", ").join(metrics.values()));
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
        return getIntCounter("AllOverlaps").intValue();
    }

    @Override
    public int getPerformedOverlaps() {
        return getIntCounter("PerformedOverlaps").intValue();
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

    @Override
    public int getSecondsOldResource() {
        return secondsOldResource;
    }

    @Override
    public void setSecondsOldResource(int v) {
        if (v >= 0) {
            secondsOldResource = v;
        }
    }
}