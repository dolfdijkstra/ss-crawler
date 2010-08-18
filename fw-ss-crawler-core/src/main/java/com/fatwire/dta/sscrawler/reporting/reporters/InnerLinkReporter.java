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
import com.fatwire.dta.sscrawler.util.HelperStrings;

public class InnerLinkReporter extends ReportDelegatingReporter {

    public InnerLinkReporter(final Report report) {
        super(report);
    }

    public void addToReport(final ResultPage page) {
        if (page.getResponseCode() == 200) {
            final List<QueryString> links = page.getLinks();
            if (!links.isEmpty()) {
                final StringBuilder b = new StringBuilder();
                b.append(page.getUri().toString());
                for (final QueryString qs : links) {
                    b.append(HelperStrings.CRLF);
                    b.append("# ");
                    b.append(qs.toString());

                }
                report.addRow(b.toString());
            }
        }

    }

    @Override
    protected String[] getHeader() {
        return new String[0];
    }

}
