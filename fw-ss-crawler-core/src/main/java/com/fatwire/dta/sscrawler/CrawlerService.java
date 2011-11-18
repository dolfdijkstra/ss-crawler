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
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;

import com.fatwire.dta.sscrawler.events.PageletRenderedEvent;
import com.fatwire.dta.sscrawler.handlers.Visitor;
import com.fatwire.dta.sscrawler.jmx.ReaderService;

public class CrawlerService extends AbstractCrawlerService {
    private final Log log = LogFactory.getLog(getClass());
    private final Log log_time = LogFactory.getLog(getClass().getName() + ".time");
    private HttpReaderTemplate template;

    private final TaskTracker taskTracker;

    public CrawlerService(final Executor readerPool, HttpReaderTemplate template) {
        super();

        taskTracker = new TaskTracker(readerPool);
        this.template = template;

    }

    public void start(final List<Link> startUrls) {
        final Scheduler scheduler = new Scheduler();
        scheduler.maxPages = getMaxPages();

        final MBeanServer platform = java.lang.management.ManagementFactory.getPlatformMBeanServer();
        ObjectName jmx = null;
        try {
            jmx = new ObjectName("com.fatwire.crawler:type=scheduler");

            platform.registerMBean(new ReaderService(scheduler), jmx);
        } catch (final Throwable t) {
            log.error(t.getMessage(), t);
        }

        for (final QueryString uri : startUrls) {

            scheduler.schedulePage(uri);
        }
        scheduler.waitForlAllTasksToFinish();

        try {
            if (jmx != null)
                platform.unregisterMBean(jmx);
        } catch (final Throwable t) {
            log.error(t.getMessage(), t);
        }

    }

    <T> void execute(Callable<T> runnable, Priority priority, SchedulerCallback<T> callback) {
        if (runnable == null || priority == null || callback == null)
            throw new NullPointerException();

        taskTracker.schedule(new Task<T>(runnable, priority, callback));

    }

    private class Task<T> implements Runnable, Comparable<Task<?>> {
        private final Callable<T> delegate;
        private final Priority priority;
        private final SchedulerCallback<T> callback;

        private Task(Callable<T> delegate, Priority priority, SchedulerCallback<T> callback) {
            if (delegate == null || priority == null || callback == null)
                throw new NullPointerException();
            this.delegate = delegate;
            this.priority = priority;
            this.callback = callback;
        }

        public int compareTo(Task<?> o) {

            return priority.compareTo(o.priority);
        }

        public void run() {

            try {
                T t = delegate.call();
                callback.pageComplete(t);
            } catch (Exception e) {
                callback.handleFailure(e, delegate);
            } finally {
                taskTracker.finish();
            }

        }

    }

    static final class Priority implements Comparable<Priority> {
        private int major;
        private int minor;

        Priority(int major, int minor) {
            this.major = major;
            this.minor = minor;
        }

        public int compareTo(final Priority o) {
            // comparing on major and minor, with same major, the
            // lower minor comes first.
            if (major != o.major) {
                return major < o.major ? -1 : 1;
            } else if (minor == o.minor) {
                return 0;
            } else {
                return minor < o.minor ? -1 : 1;
            }

        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Priority [major=");
            builder.append(major);
            builder.append(", minor=");
            builder.append(minor);
            builder.append("]");
            return builder.toString();
        }
    }

    static final class TaskTracker {
        private final AtomicInteger scheduledCounter = new AtomicInteger();
        private final CountDownLatch complete = new CountDownLatch(1);
        private final Executor executor;
        private final Log log = LogFactory.getLog(getClass());

        public TaskTracker(Executor executor) {
            super();
            this.executor = executor;
        }

        void schedule(Task<?> task) {
            scheduledCounter.incrementAndGet();
            try {
                executor.execute(task);

            } catch (Throwable t) {
                finish();
                log.error(t);
            }

        }

        void finish() {
            if (scheduledCounter.decrementAndGet() == 0) {
                complete.countDown();
            }
        }

        void waitForlAllTasksToFinish() throws InterruptedException {
            complete.await();

        }

        public int getScheduledCount() {
            return scheduledCounter.get();
        }

    }

    public class Scheduler {

        private final Set<QueryString> urlsDiscovered = Collections
                .newSetFromMap(new ConcurrentHashMap<QueryString, Boolean>());

        private final AtomicInteger pageCount = new AtomicInteger();

        private final AtomicInteger completeCount = new AtomicInteger();

        private int maxPages = Integer.MAX_VALUE;

        private final SchedulerCallback<ResultPage> visitorCallback = new SchedulerCallback<ResultPage>() {
            public void pageComplete(final ResultPage page) {

                
                for (final QueryString ssUri : page.getMarkers()) {

                    schedulePage(ssUri);

                }
                for (final QueryString ssUri : page.getLinks()) {

                    schedulePage(ssUri);

                }
                long t = System.nanoTime();
                fireEvent(new PageletRenderedEvent(page));
                log_time.debug("fireEvent for page " + page.getUri() + " took " + ((System.nanoTime() - t) / 1000)
                        + "us.");
                completeCount.incrementAndGet();

            }

            public void handleFailure(Throwable t, Callable<ResultPage> context) {
                log.error(t + " for " + context, t);

            }
        };

        private final SchedulerCallback<ResultPage> downloadCallback = new SchedulerCallback<ResultPage>() {
            public void pageComplete(final ResultPage page) {
                Visitor<ResultPage> handler = getHandler();
                if (handler != null && page != null && page.getBody() != null) {
                    long t = System.nanoTime();
                    handler.visit(page);
                    log_time.debug("Visiting page " + page.getUri() + " took " + ((System.nanoTime() - t) / 1000)
                            + "us.");
                }
                visitorCallback.pageComplete(page);

            }

            public void handleFailure(Throwable t, Callable<ResultPage> context) {
                if (t instanceof ConnectException) {
                    log.error(t + " for " + context);
                } else if (t instanceof HttpException) {
                    log.error(t + " for " + context);
                } else if (t instanceof IOException) {
                    log.error(t + " for " + context);
                } else if (t instanceof Throwable) {
                    log.error(t + " for " + context, t);
                }

            }
        };

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
                if (pageCount.incrementAndGet() > maxPages) {
                    return; // do not schedule beyond max number of pages
                }
            }

            try {

                int priority = 0;
                if (qs instanceof Link) {
                    priority = 5;
                }
                Priority p = new Priority(priority, pageCount.get());

                execute(template.create(qs), p, downloadCallback);

            } catch (final Exception e) {

                log.error(e.getMessage(), e);

            }

        }

        void waitForlAllTasksToFinish() {
            try {
                taskTracker.waitForlAllTasksToFinish();
            } catch (final InterruptedException e) {
                log.warn(e, e);
            }

        }

        public int getCount() {
            return pageCount.get();
        }

        public int getScheduledCount() {
            return taskTracker.getScheduledCount();
        }

        public int getCompleteCount() {
            return completeCount.get();
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

    }

}
