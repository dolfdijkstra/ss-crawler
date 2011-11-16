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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.fatwire.dta.sscrawler.events.PageletRenderedEvent;
import com.fatwire.dta.sscrawler.events.PageletRenderingListener;
import com.fatwire.dta.sscrawler.jobs.Command;
import com.fatwire.dta.sscrawler.jobs.ProgressMonitor;
import com.fatwire.dta.sscrawler.reporting.Reporter;
import com.fatwire.dta.sscrawler.reporting.ReportingListener;

/**
 * 
 * 
 * @author Dolf Dijkstra
 * 
 */
public class Crawler implements Command {

    final URLReaderService reader;

    private Link startUri;

    final private List<Reporter> reporters = new LinkedList<Reporter>();
    final ReportingListener reportingListener = new ReportingListener();

    public Crawler(URLReaderService reader) {
        super();
        this.reader = reader;
    }

    public void execute(final ProgressMonitor monitor) {
        if (monitor == null)
            throw new IllegalArgumentException("monitor cannot be null.");
        final PageletRenderingListener readerListener = new PageletRenderingListener() {

            public void renderPerformed(final PageletRenderedEvent event) {
                reportingListener.renderPerformed(event);
                monitor.worked(1);
            }

        };

        reader.addListener(readerListener);

        for (final Reporter reporter : reporters) {
            reporter.startCollecting();
        }
        try {
            reader.start(Collections.singletonList(startUri));
        } finally {
            reader.removeListener(readerListener);

            for (final Reporter reporter : reporters) {
                reporter.endCollecting();
            }

        }
    }

    /**
     * @return the startUri
     */
    public Link getStartUri() {
        return startUri;
    }

    /**
     * @param startUri the startUri to set
     */
    public void setStartUri(final Link startUri) {
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
    public void addReporters(final List<Reporter> reporters) {
        this.reporters.addAll(reporters);

        for (final Reporter reporter : reporters) {
            reportingListener.addReporter(reporter);
        }
    }

}
