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

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.math.stat.descriptive.SynchronizedSummaryStatistics;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.Link;
import com.fatwire.dta.sscrawler.reporting.Report;

public class NestingReporter extends ReportDelegatingReporter {

    private final int threshold;
    private final int avg;

    private AtomicInteger count = new AtomicInteger();
    private final SynchronizedSummaryStatistics total = new SynchronizedSummaryStatistics();
    private final NestingTracker tracker = new NestingTracker();

    /**
     * @param report to send the data to 
     * @param threshold max number of nested pagelets allowed
     * @param avg maximum average number of pagelets allowed
     */
    public NestingReporter(Report report, final int threshold, final int avg) {
        super(report);
        this.threshold = threshold;
        this.avg = avg;
        if (threshold <= avg) throw new IllegalStateException("threshold cannot be equal or smaller than avg.");

    }

    public void addToReport(ResultPage page) {
        if (page.getResponseCode() == 200) {
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
    public void endCollecting() {
        report.startReport();
        report.addHeader("threshold", Integer.toString(threshold));
        report.addHeader("uri", "nesting");

        for (QueryString qs : tracker.getKeys()) {
            if (qs instanceof Link) {
                int level = tracker.getNestingLevel(qs);
                total.addValue(level);
                if (level >= threshold) {
                    count.incrementAndGet();

                    report.addRow(qs.toString(), Integer.toString(level));
                } else if (level == 0) {
                    report.addRow(qs.toString(), Integer.toString(level));
                }
            }
        }
        final DecimalFormat df = new DecimalFormat("###0.00");
        report.addRow("Average Nesting level", df.format(total.getMean()));
        report.addRow("Maximum Nesting level", Long.toString(Math.round(total.getMax())));
        report.addRow("Minimum Nesting level", Long.toString(Math.round(total.getMin())));
        super.endCollecting();
    }

    /**
     * If on average more than averageThreshold nested pagelets are found, turn red 
     * if one pagelet is found over threshold, turn orange
     * 
     */
    public Verdict getVerdict() {
        return  total.getMean() > avg ? Verdict.RED : count.get() > 1 ? Verdict.AMBER : Verdict.GREEN;
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
        return new String[0];
    }

}
