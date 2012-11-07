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

import java.util.Arrays;
import java.util.List;

import com.fatwire.dta.sscrawler.Link;
import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;

public class InnerPageletPerOuterReporter extends ReportDelegatingReporter {
    private final NestingTracker tracker = new NestingTracker();

    public InnerPageletPerOuterReporter(final Report report) {
        super(report);
    }

    public void addToReport(final ResultPage page) {
        if (page.getResponseCode() == 200) {
            tracker.add(page);
        }

    }

    @Override
    public synchronized void startCollecting() {

    }

    @Override
    public void endCollecting() {
        report.startReport();

        for (final QueryString qs : tracker.getKeys()) {
            if (qs instanceof Link) {
                printLevel(qs, 0);
                report.addRow("");

            }
        }

        super.endCollecting();
    }

    /**
     * @param qs
     * @param level
     */
    private void printLevel(final QueryString qs, final int level) {
        if (level > 0) {
            final char[] pre = new char[level * 4];
            Arrays.fill(pre, ' ');
            final char[] ll = Integer.toString(level).toCharArray();
            System.arraycopy(ll, 0, pre, level, ll.length);
            report.addRow(new String(pre).concat(qs.toString()));
        } else {
            report.addRow(qs.toString());
        }
        final List<QueryString> markers = tracker.getMarkers(qs);

        for (final QueryString q : markers) {
            printLevel(q, level + 1);
        }
    }

    public Verdict getVerdict() {
        return Verdict.NONE;
    }

    @Override
    protected String[] getHeader() {
        return new String[] { "page" };
    }

}
