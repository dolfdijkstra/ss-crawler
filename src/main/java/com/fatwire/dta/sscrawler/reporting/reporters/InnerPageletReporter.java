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

import java.util.List;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;

public class InnerPageletReporter extends ReportDelegatingReporter {

    public InnerPageletReporter(final Report report) {
        super(report);
    }

    public void addToReport(final ResultPage page) {
        if (page.getResponseCode() == 200) {
            final List<QueryString> markers = page.getMarkers();
            if (!markers.isEmpty()) {
                synchronized (this) {
                    report.addRow(page.getUri().toString());
                    for (final QueryString qs : markers) {
                        report.addRow("#", qs.toString());

                    }
                }
            }
        }

    }

    public Verdict getVerdict() {
        return Verdict.NONE;
    }

    @Override
    protected String[] getHeader() {
        return new String[] { "page", "marker" };
    }

}
