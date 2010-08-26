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

package com.fatwire.dta.sscrawler.reporting.reporters;

import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.util.CacheHelper;

public class PageletReuseReporter extends ReportDelegatingReporter {

    private final int threshold;
    private final PageletTracker tracker = new PageletTracker();
    private final AtomicInteger count = new AtomicInteger();
    private final AtomicInteger total = new AtomicInteger();

    public PageletReuseReporter(final Report report, final int threshold) {
        super(report);
        this.threshold = threshold;

    }

    public void addToReport(final ResultPage page) {
        if (page.getResponseCode() == 200 && CacheHelper.shouldCache(page.getResponseHeaders())) {
            // track pages that should be cached
            tracker.add(page);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.dta.sscrawler.reporting.reporters.ReportDelegatingReporter
     * #endCollecting()
     */
    @Override
    public synchronized void endCollecting() {
        report.startReport();
        report.addHeader("threshold: " + threshold);
        report.addHeader(getHeader());

        for (final Entry<QueryString, AtomicInteger> e : tracker.getEntries()) {
            final QueryString qs = e.getKey();
            total.incrementAndGet();
            final int level = e.getValue().get();
            if (level >= threshold) {
                count.incrementAndGet();
                report.addRow(qs.toString(), Integer.toString(level));
            }
        }

        super.endCollecting();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.dta.sscrawler.reporting.reporters.ReportDelegatingReporter
     * #startCollecting()
     */
    @Override
    public void startCollecting() {
        // do nothing, all is handled in the endCollecting call
    }

    @Override
    protected String[] getHeader() {
        return new String[] { "uri", "reuse" };
    }

    public Verdict getVerdict() {
        return count.get() == 0 ? Verdict.RED : Verdict.GREEN;
    }

}
