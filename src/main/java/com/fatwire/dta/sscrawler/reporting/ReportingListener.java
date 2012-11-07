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

package com.fatwire.dta.sscrawler.reporting;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.events.PageletRenderedEvent;
import com.fatwire.dta.sscrawler.events.PageletRenderingListener;
import com.fatwire.dta.sscrawler.jobs.JobChangeListener;
import com.fatwire.dta.sscrawler.jobs.JobFinishedEvent;
import com.fatwire.dta.sscrawler.jobs.JobScheduledEvent;
import com.fatwire.dta.sscrawler.jobs.JobStartedEvent;

public class ReportingListener implements PageletRenderingListener, JobChangeListener {
    private final Log log = LogFactory.getLog(getClass());

    private final List<Reporter> reporters = new CopyOnWriteArrayList<Reporter>();

    public void addReporter(final Reporter reporter) {
        reporters.add(reporter);
    }

    public void renderPerformed(final PageletRenderedEvent event) {
        final ResultPage page = event.getPage();
        for (final Iterator<Reporter> i = reporters.iterator(); i.hasNext();) {
            final Reporter reporter = i.next();
            try {
                reporter.addToReport(page);
            } catch (final Throwable e) {
                log.error(e, e);
                // i.remove(); //remove reporter if we can't use it
            }
        }

    }

    public void jobFinished(final JobFinishedEvent event) {
        for (final Reporter reporter : reporters) {
            try {
                reporter.endCollecting();
            } catch (final Throwable e) {
                log.error(e, e);
            }
        }

    }

    public void jobScheduled(final JobScheduledEvent event) {

    }

    public void jobStarted(final JobStartedEvent event) {
        for (final Iterator<Reporter> i = reporters.iterator(); i.hasNext();) {
            final Reporter reporter = i.next();
            try {
                reporter.startCollecting();
            } catch (final Throwable e) {
                log.error(e, e);
                i.remove(); // remove reporter if we can't use it
            }
        }

    }

}
