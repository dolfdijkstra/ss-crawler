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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;

public class RawLinkReporter extends ReportDelegatingReporter {

    private final Pattern rawLinkPattern = Pattern.compile("Satellite\\?.*?[\"']");

    public RawLinkReporter(final Report report) {
        super(report);
    }

    public void addToReport(final ResultPage page) {
        if (page.getResponseCode() != 200) {
            final String body = page.getBody();
            if (body != null) {

                final Matcher m = rawLinkPattern.matcher(body);
                while (m.find()) {
                    report.addRow(page.getUri().toString(), m.group());
                }
            }
        }

    }

    @Override
    protected String[] getHeader() {
        return new String[] { "uri", "link" };
    }

    public Verdict getVerdict() {
        return Verdict.NONE;
    }
}
