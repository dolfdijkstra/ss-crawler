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

import java.util.concurrent.atomic.AtomicInteger;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.reporting.Reporter;

public class SuspiciousContextParamReporter implements Reporter {

    private final Report report;
    private AtomicInteger count = new AtomicInteger();

    /**
     * @param report
     */
    public SuspiciousContextParamReporter(final Report report) {
        super();
        this.report = report;
    }

    public void addToReport(final ResultPage page) {

        if (page.getResponseCode() == 200) {
            for (QueryString qs : page.getMarkers()) {
                if (qs.isOK()) {

                    String context = qs.getParameters().get("context");
                    if (context != null && context.indexOf(';') != -1) {
                        count.incrementAndGet();
                        report.addRow(page.getPageName(), context, page.getUri().toString(), qs.toString());
                    }

                }
            }
        }
    }

    public void endCollecting() {
        report.finishReport();
    }

    public void startCollecting() {
        report.startReport();
        report.addHeader("pagename", "context", "calling pagelet", "call to pagelet");
    }

    public String getTitle() {
        return this.getClass().getSimpleName();
    }

    public Verdict getVerdict() {
        return count.get() > 10 ? Verdict.RED : count.get() == 0 ? Verdict.GREEN : Verdict.AMBER;
    }
}
