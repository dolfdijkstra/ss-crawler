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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import com.fatwire.dta.sscrawler.domain.HostConfig;
import com.fatwire.dta.sscrawler.events.PageletRenderedEvent;
import com.fatwire.dta.sscrawler.events.PageletRenderingListener;
import com.fatwire.dta.sscrawler.handlers.BodyHandler;
import com.fatwire.dta.sscrawler.jobs.Command;
import com.fatwire.dta.sscrawler.jobs.ProgressMonitor;
import com.fatwire.dta.sscrawler.util.SSUriHelper;

public class RenderCommand implements Command {

    private final List<QueryString> queue = new ArrayList<QueryString>();

    private int maxPages;

    private HostConfig hostConfig;

    private SSUriHelper uriHelper;

    private BodyHandler handler;

    private final Executor executor;

    private final Set<PageletRenderingListener> listeners = new CopyOnWriteArraySet<PageletRenderingListener>();

    /**
     * @param hostConfig
     * @param maxPages
     */
    public RenderCommand(final HostConfig hostConfig, final int maxPages, final Executor executor) {
        super();
        this.hostConfig = hostConfig;
        this.maxPages = maxPages;
        this.executor = executor;
    }

    public RenderCommand(final HostConfig hostConfig, final ThreadPoolExecutor readerPool) {
        this(hostConfig, Integer.MAX_VALUE, readerPool);
    }

    public void addStartUri(final QueryString uri) {
        queue.add(uri);
    }

    public void execute(final ProgressMonitor monitor) {
        if (executor == null) {
            throw new IllegalStateException("executor is null");
        }
        if (hostConfig == null) {
            throw new IllegalStateException("hostConfig is null");
        }
        if (handler == null) {
            throw new IllegalStateException("BodyHandler is null");
        }
        if (uriHelper == null) {
            throw new IllegalStateException("uriHelper is null");
        }
        if (maxPages < 1) {
            throw new IllegalStateException("Number of pages to crawl is less than 1");
        }
        if (queue.isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }

        final URLReaderService reader = new URLReaderService(executor);

        reader.setHostConfig(hostConfig);
        reader.setHandler(handler);
        reader.setUriHelper(uriHelper);
        reader.setMaxPages(maxPages);

        reader.addStartUris(queue);

        final PageletRenderingListener readerListener = new PageletRenderingListener() {

            public void renderPerformed(final PageletRenderedEvent event) {
                for (final PageletRenderingListener listener : listeners) {
                    listener.renderPerformed(event);
                }

            }

        };

        reader.addListener(readerListener);

        reader.start(monitor);
        reader.removeListener(readerListener);

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

    /**
     * @return the hostConfig
     */
    public HostConfig getHostConfig() {
        return hostConfig;
    }

    /**
     * @param hostConfig the hostConfig to set
     */
    public void setHostConfig(final HostConfig hostConfig) {
        this.hostConfig = hostConfig;
    }

    /**
     * @return the uriHelper
     */
    public SSUriHelper getUriHelper() {
        return uriHelper;
    }

    /**
     * @param uriHelper the uriHelper to set
     */
    public void setUriHelper(final SSUriHelper uriHelper) {
        this.uriHelper = uriHelper;
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
    public void setMaxPages(final int maxPages) {
        this.maxPages = maxPages;
    }

}
