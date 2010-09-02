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

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

import com.fatwire.dta.sscrawler.domain.HostConfig;
import com.fatwire.dta.sscrawler.handlers.BodyHandler;
import com.fatwire.dta.sscrawler.jobs.NullProgressMonitor;
import com.fatwire.dta.sscrawler.jobs.ProgressMonitor;
import com.fatwire.dta.sscrawler.reporting.Reporter;
import com.fatwire.dta.sscrawler.reporting.ReportingListener;
import com.fatwire.dta.sscrawler.util.SSUriHelper;

/**
 * 
 * 
 * @author Dolf Dijkstra
 * 
 */
public class Crawler {

    private HostConfig hostConfig;

    private URI startUri;

    private int maxPages = Integer.MAX_VALUE;

    private List<Reporter> reporters = new LinkedList<Reporter>();;

    private SSUriHelper uriHelper;

    private Executor executor;

    private ProgressMonitor progressMonitor;

    public void work() {

        final RenderCommand command = new RenderCommand(hostConfig, maxPages, executor);
        command.addStartUri(uriHelper.createLink(startUri));
        command.setUriHelper(uriHelper);
        command.setHandler(new BodyHandler(uriHelper));

        final ReportingListener reportingListener = new ReportingListener();

        for (final Reporter reporter : reporters) {
            reportingListener.addReporter(reporter);
        }

        command.addListener(reportingListener);

        for (final Reporter reporter : reporters) {
            reporter.startCollecting();
        }
        if (progressMonitor == null) {
            progressMonitor = new NullProgressMonitor();
        }
        command.execute(progressMonitor);

        for (final Reporter reporter : reporters) {
            reporter.endCollecting();
        }

    }

    /**
     * @return the maxPages
     */
    public int getMaxPages() {
        return maxPages;
    }

    /**
     * @param max the maxPages to set
     */
    public void setMaxPages(final int max) {
        if (max < 1) {
            throw new IllegalArgumentException("max should be greater then 0");
        }
        maxPages = max;
    }

    /**
     * @return the startUri
     */
    public URI getStartUri() {
        return startUri;
    }

    /**
     * @param startUri the startUri to set
     */
    public void setStartUri(final URI startUri) {
        this.startUri = startUri;
    }

    /**
     * @return the reporters
     */
    public List<Reporter> getReporters() {
        return reporters;
    }

    /**
     * @param reporters the reporters to set
     */
    public void setReporters(final List<Reporter> reporters) {
        this.reporters = reporters;
    }

    /**
     * @return the executor
     */
    public Executor getExecutor() {
        return executor;
    }

    /**
     * @param executor the executor to set
     */
    public void setExecutor(final Executor executor) {
        this.executor = executor;
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
     * @return the progressMonitor
     */
    public ProgressMonitor getProgressMonitor() {
        return progressMonitor;
    }

    /**
     * @param progressMonitor the progressMonitor to set
     */
    public void setProgressMonitor(final ProgressMonitor progressMonitor) {
        this.progressMonitor = progressMonitor;
    }

    public void setHostConfig(final HostConfig hostConfig) {
        this.hostConfig = hostConfig;
    }

    public HostConfig getHostConfig() {
        return hostConfig;
    }

}
