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

import com.fatwire.dta.sscrawler.events.PageletRenderedEvent;
import com.fatwire.dta.sscrawler.events.PageletRenderingListener;
import com.fatwire.dta.sscrawler.handlers.Visitor;

public class AbstractCrawlerService {

    private volatile boolean stopped = false;
    private Visitor<ResultPage> handler;
    private final Set<PageletRenderingListener> listeners = new CopyOnWriteArraySet<PageletRenderingListener>();
    private int maxPages = Integer.MAX_VALUE;

    public AbstractCrawlerService() {
        super();
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

    public void addListener(final PageletRenderingListener listener) {
        listeners.add(listener);
    }

    public void removeListener(final PageletRenderingListener listener) {
        listeners.remove(listener);
    }

    /**
     * @return the handler
     */
    public Visitor<ResultPage> getHandler() {
        return handler;
    }

    /**
     * @param handler the handler to set
     */
    public void setHandler(final Visitor<ResultPage> handler) {
        this.handler = handler;
    }

    public void setMaxPages(int max) {
        if (max < 1) {
            throw new IllegalArgumentException("max should be greater then 0");
        }

        this.maxPages = max;

    }

    /**
     * @return the maxPages
     */
    public int getMaxPages() {
        return maxPages;
    }

    protected void fireEvent(PageletRenderedEvent event) {

        for (final PageletRenderingListener listener : listeners) {
            listener.renderPerformed(event);
        }

    }

}
