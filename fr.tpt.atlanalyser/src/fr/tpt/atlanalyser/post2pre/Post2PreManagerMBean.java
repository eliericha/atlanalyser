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

public interface Post2PreManagerMBean {

    public int getThreadPoolSize();

    public void setThreadPoolSize(int n);

    public double getLowMemoryThreshold();

    public void setLowMemoryThreshold(double v);

    public double getStopDumpThreshold();

    public void setStopDumpThreshold(double v);

    public int getOverlapJobsQueueSize();

    public int getQueueSizeDelta();

    public void setNCsToDump(int v);

    public int getNCsToDump();

    public double getHeapMemoryUsageRate();

    public double getSmoothingFactor();

    public void setSmoothingFactor(double v);

    public int getNumLoadedResources();

    public int getNumResources();

    public int getAllOverlaps();

    public int getPerformedOverlaps();

    public int getSecondsOldResource();

    public void setSecondsOldResource(int v);

}
