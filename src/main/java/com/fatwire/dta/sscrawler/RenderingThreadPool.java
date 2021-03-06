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

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RenderingThreadPool extends ThreadPoolExecutor implements RenderingThreadPoolMBean {
    private final Log log = LogFactory.getLog(getClass());

    private final Set<FinishedListener> listeners = new CopyOnWriteArraySet<FinishedListener>();

    public RenderingThreadPool(final int threadSize) {
        super(threadSize, threadSize, 60, TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>(5000));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable,
     * java.lang.Throwable)
     */
    @Override
    protected void afterExecute(final Runnable r, final Throwable t) {
        if (log.isDebugEnabled()) {
            log.debug("afterExecute: " + getActiveCount() + " " + getQueue().size());
        }
        if (getActiveCount() == 1 && getQueue().size() == 0) {
            for (final FinishedListener listener : listeners) {
                listener.finished();
            }
        }
        super.afterExecute(r, t);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.util.concurrent.ThreadPoolExecutor#beforeExecute(java.lang.Thread,
     * java.lang.Runnable)
     */
    @Override
    protected void beforeExecute(final Thread t, final Runnable r) {
        super.beforeExecute(t, r);
    }

    interface FinishedListener {

        void finished();
    }

    public void addListener(final FinishedListener listener) {
        listeners.add(listener);
    }

    public void removeListener(final FinishedListener listener) {
        listeners.remove(listener);
    }

}
