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

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;

/**
 * Reports all pages that return with a non-200 status code
 * 
 * @author Dolf Dijkstra
 *
 */

public class Non200ResponseCodeReporter extends ReportDelegatingReporter {
    private AtomicInteger count = new AtomicInteger();
    public Non200ResponseCodeReporter(final Report report) {
        super(report);
    }

    public void addToReport(final ResultPage page) {
        if (page.getResponseCode() != 200) {
            count.incrementAndGet();
            report.addRow(page.getUri().toString(), Integer.toString(page.getResponseCode()));
        }

    }
    public Verdict getVerdict() {
        return count.get() > 1 ? Verdict.RED : Verdict.GREEN;
    }
    @Override
    protected String[] getHeader() {
        return new String[]{"uri","responsecode"};
    }

}
