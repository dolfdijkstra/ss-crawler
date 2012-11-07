/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fatwire.dta.sscrawler;

public interface RenderingThreadPoolMBean {

    /**
     * @return activeCount
     * @see java.util.concurrent.ThreadPoolExecutor#getActiveCount()
     */
    public int getActiveCount();

    /**
     * @return completedTaskCount
     * @see java.util.concurrent.ThreadPoolExecutor#getCompletedTaskCount()
     */
    public long getCompletedTaskCount();

    /**
     * @return corePoolSize
     * @see java.util.concurrent.ThreadPoolExecutor#getCorePoolSize()
     */
    public int getCorePoolSize();

    /**
     * @return largestPoolSize
     * @see java.util.concurrent.ThreadPoolExecutor#getLargestPoolSize()
     */
    public int getLargestPoolSize();

    /**
     * @return maximumPoolSize
     * @see java.util.concurrent.ThreadPoolExecutor#getMaximumPoolSize()
     */
    public int getMaximumPoolSize();

    /**
     * @return poolSize
     * @see java.util.concurrent.ThreadPoolExecutor#getPoolSize()
     */
    public int getPoolSize();

    /**
     * @return taskCount
     * @see java.util.concurrent.ThreadPoolExecutor#getTaskCount()
     */
    public long getTaskCount();

}
