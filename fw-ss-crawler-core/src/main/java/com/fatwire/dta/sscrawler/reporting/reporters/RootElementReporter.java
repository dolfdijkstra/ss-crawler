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

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.httpclient.Header;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.util.HelperStrings;

public class RootElementReporter extends ReportDelegatingReporter {

    public static final String HEADER_NAME = HelperStrings.CS_TO_SS_RESPONSE_HEADER_PREFIX + "rootelement";

    private final Set<String> elements = new CopyOnWriteArraySet<String>();

    public RootElementReporter(final Report report) {
        super(report);

    }

    public void addToReport(final ResultPage page) {
        if (page.getResponseCode() == 200) {
            for (final Header header : page.getResponseHeaders()) {
                if (RootElementReporter.HEADER_NAME.equals(header.getName())) {
                    String element = header.getValue();

                    if (!elements.contains(element)) {
                        elements.add(element);
                        report.addRow(element);
                    }
                }
            }

        }

    }

    @Override
    protected String[] getHeader() {
        return new String[] { "elementname" };
    }

    public Verdict getVerdict() {
        return Verdict.NONE;
    }
}
