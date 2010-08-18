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

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;

public class NumberOfInnerPageletsReporter extends ReportDelegatingReporter {

    private final int threshold;

    public NumberOfInnerPageletsReporter(final Report report, final int threshold) {
        super(report);
        this.threshold = threshold;

    }

    public void addToReport(final ResultPage page) {
        if (page.getResponseCode() == 200) {
            final int num = page.getMarkers().size();
            if (num >= threshold) {
                report.addRow(Integer.toString(num)  ,  page.getUri().toString());
            }
        }
    }

    /* (non-Javadoc)
     * @see com.fatwire.dta.sscrawler.reporting.reporters.ReportDelegatingReporter#startCollecting()
     */
    @Override
    public void startCollecting() {
        super.startCollecting();
        report.addHeader("threshold: " +threshold);
        report.addHeader(getHeader());

    }

    @Override
    protected String[] getHeader() {
        return new String[]{"inner pagelets","uri"};
    }

}
