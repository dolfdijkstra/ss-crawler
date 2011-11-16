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

import java.io.IOException;
import java.net.ConnectException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;

import com.fatwire.dta.sscrawler.events.PageletRenderedEvent;
import com.fatwire.dta.sscrawler.events.PageletRenderingListener;
import com.fatwire.dta.sscrawler.handlers.BodyHandler;
import com.fatwire.dta.sscrawler.jmx.ReaderService;

public class URLReaderService {
    private final Log log = LogFactory.getLog(getClass());
    private static final Log log_time = LogFactory.getLog(URLReaderService.class.getName() + ".time");
    private volatile boolean stopped = false;

    private BodyHandler handler;

    private int maxPages = Integer.MAX_VALUE;

    private final Set<PageletRenderingListener> listeners = new CopyOnWriteArraySet<PageletRenderingListener>();

    private HttpReaderTemplate template;
    private final Executor executor;

    public URLReaderService(final Executor readerPool, HttpReaderTemplate template) {
        super();

        this.executor = readerPool;
        this.template = template;

    }

    public void start(final List<Link> startUrls) {
        final Scheduler scheduler = new Scheduler();

        final MBeanServer platform = java.lang.management.ManagementFactory.getPlatformMBeanServer();
        ObjectName jmx = null;
        try {
            jmx = new ObjectName("com.fatwire.crawler:type=scheduler");

            platform.registerMBean(new ReaderService(scheduler), jmx);
        } catch (final Throwable t) {
            log.error(t.getMessage(), t);
        }

        for (final QueryString thingToDo : startUrls) {

            scheduler.schedulePage(thingToDo);
        }
        scheduler.waitForlAllTasksToFinish();

        try {
            if (jmx != null)
                platform.unregisterMBean(jmx);
        } catch (final Throwable t) {
            log.error(t.getMessage(), t);
        }

    }

    public class Scheduler implements SchedulerCallback {

        private final Set<QueryString> urlsDiscovered = Collections
                .newSetFromMap(new ConcurrentHashMap<QueryString, Boolean>());

        private final CountDownLatch complete = new CountDownLatch(1);

        private final AtomicInteger scheduledCounter = new AtomicInteger();

        private final AtomicInteger count = new AtomicInteger();

        private final AtomicInteger completeCount = new AtomicInteger();

        /**
         * @param executor
         */
        public Scheduler() {
            super();

        }

        void schedulePage(final QueryString qs) {

            if (!urlsDiscovered.add(qs)) {
                return;

            }
            if (log.isDebugEnabled()) {
                log.debug("adding " + qs);
            }
            if (qs instanceof Link) {
                if (count.incrementAndGet() > maxPages) {
                    return; // do not schedule beyond max number of pages
                }
            }

            scheduledCounter.incrementAndGet();
            try {

                int priority = 0;
                if (qs instanceof Link) {
                    priority = 5;
                }

                executor.execute(new Harvester(template.create(qs), this, handler, priority, count.get()));

            } catch (final Exception e) {
                scheduledCounter.decrementAndGet();
                log.error(e.getMessage(), e);

            }

        }

        public void pageComplete(final ResultPage page) {

            completeCount.incrementAndGet();
            for (final QueryString ssUri : page.getMarkers()) {

                schedulePage(ssUri);

            }
            for (final QueryString ssUri : page.getLinks()) {

                schedulePage(ssUri);

            }

            final PageletRenderedEvent event = new PageletRenderedEvent(page);
            for (final PageletRenderingListener listener : listeners) {
                listener.renderPerformed(event);
            }
        }

        public void taskFinished() {
            log.debug("Active workers: " + scheduledCounter.get());
            if (scheduledCounter.decrementAndGet() == 0) {
                complete.countDown();
            }

        }

        void waitForlAllTasksToFinish() {
            try {
                complete.await();
            } catch (final InterruptedException e) {
                log.warn(e, e);
            }

        }

        public int getCount() {
            return count.get();
        }

        public int getScheduledCount() {
            return scheduledCounter.get();
        }

        public int getCompleteCount() {
            return completeCount.get();
        }

    }

    static class Harvester implements Runnable, Comparable<Harvester> {
        private static final Log log = LogFactory.getLog(Harvester.class);
        private final UrlRenderingCallable downloader;

        private final int priority;

        private final int orderNumber;
        private SchedulerCallback callback;
        private BodyHandler handler;

        /**
         * @param downloader
         */
        public Harvester(final UrlRenderingCallable downloader, SchedulerCallback callback, BodyHandler handler,
                final int priority, final int orderNumber) {
            super();
            this.downloader = downloader;

            this.priority = priority;
            this.orderNumber = orderNumber;
            this.callback = callback;
            this.handler = handler;
        }

        public void run() {
            try {
                long t = System.nanoTime();
                final ResultPage page = downloader.call();

                log_time.debug("Downloading page " + page.getUri() + " took " + ((System.nanoTime() - t) / 1000)
                        + "us.");

                if (handler != null && page != null && page.getBody() != null) {
                    t = System.nanoTime();
                    handler.visit(page);
                    log_time.debug("Visiting page " + page.getUri() + " took " + ((System.nanoTime() - t) / 1000)
                            + "us.");
                }
                callback.pageComplete(page);
            } catch (final ConnectException e) {
                log.error(e + " for " + downloader.getUri());
            } catch (final HttpException e) {
                log.error(e + " for " + downloader.getUri());
            } catch (final IOException e) {
                log.error(e + " for " + downloader.getUri());
            } catch (final Exception e) {
                log.error(e + " for " + downloader.getUri(), e);
            } finally {
                callback.taskFinished();
            }

        }

        public int compareTo(final Harvester o) {
            // comparing on priority and orderNumber, with same priority, the
            // lower order number comes first.
            if (priority != o.priority) {
                return priority < o.priority ? -1 : 1;
            } else if (orderNumber == o.orderNumber) {
                return 0;
            } else {
                return orderNumber < o.orderNumber ? -1 : 1;
            }

        }

    }

    /**
     * @return true if the service is stopped
     */
    public boolean isStopped() {
        return stopped;
    }

    /**
     * stop the service
     */
    public void stop() {
        stopped = true;
    }

    /**
     * @return the maxPages
     */
    public int getMaxPages() {
        return maxPages;
    }

    /**
     * @param maxPages the maxPages to set
     */
    public void setMaxPages(final int max) {
        if (max < 1) {
            throw new IllegalArgumentException("max should be greater then 0");
        }

        this.maxPages = max;
    }

    public void addListener(final PageletRenderingListener listener) {
        listeners.add(listener);
    }

    public void removeListener(final PageletRenderingListener listener) {
        listeners.remove(listener);
    }

    /**
     * @return the handler
     */
    public BodyHandler getHandler() {
        return handler;
    }

    /**
     * @param handler the handler to set
     */
    public void setHandler(final BodyHandler handler) {
        this.handler = handler;
    }

}
