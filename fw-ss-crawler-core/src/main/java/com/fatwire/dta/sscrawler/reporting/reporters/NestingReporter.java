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

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;

public class NestingReporter extends ReportDelegatingReporter {

    private final int threshold;

    public NestingReporter(Report report, final int threshold) {
        super(report);
        this.threshold = threshold;

    }

    private final NestingTracker tracker = new NestingTracker();

    public void addToReport(ResultPage page) {
        if (page.getResponseCode() == 200) {
            tracker.add(page);
        }
    }

    /* (non-Javadoc)
     * @see com.fatwire.dta.sscrawler.reporting.reporters.ReportDelegatingReporter#endCollecting()
     */
    @Override
    public void endCollecting() {
        super.startCollecting();
        report.addHeader("threshold", Integer.toString(threshold));
        report.addHeader("uri", "nesting");

        for (QueryString qs : tracker.getKeys()) {
            int level = tracker.getNestingLevel(qs);
            if (level >= threshold) {
                report.addRow(qs.toString(), Integer.toString(level));
            }
        }

        super.endCollecting();
    }

    /* (non-Javadoc)
     * @see com.fatwire.dta.sscrawler.reporting.reporters.ReportDelegatingReporter#startCollecting()
     */
    @Override
    public void startCollecting() {
        //do nothing, all is handled in the endCollecting call
    }

    @Override
    protected String[] getHeader() {
        return new String[0];
    }

}
