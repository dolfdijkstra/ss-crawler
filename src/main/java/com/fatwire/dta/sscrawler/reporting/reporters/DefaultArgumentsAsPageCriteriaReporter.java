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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.httpclient.Header;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.util.HelperStrings;

/**
 * List default arguments not defined as pagecriteria
 * 
 * @author Dolf.Dijkstra
 * 
 */

public class DefaultArgumentsAsPageCriteriaReporter extends ReportDelegatingReporter {

    private final Set<String> pagenamesDone = new CopyOnWriteArraySet<String>();

    public static final String DEFAULT_ARGUMENTS = HelperStrings.CS_TO_SS_RESPONSE_HEADER_PREFIX + "defaultarguments";
    private final AtomicInteger count = new AtomicInteger();

    public DefaultArgumentsAsPageCriteriaReporter(final Report report) {
        super(report);

    }

    public synchronized void addToReport(final ResultPage page) {
        if (page.getResponseCode() == 200) {
            if (pagenamesDone.contains(page.getPageName())) {
                return;
            }
            pagenamesDone.add(page.getPageName());
            final List<String> pageCriteria = extractPageCriteria(page.getResponseHeaders());
            for (final Header header : page.getResponseHeaders()) {
                if (header.getName().startsWith(DEFAULT_ARGUMENTS)) {
                    final String v = header.getValue().split("\\|", 2)[0];
                    if (!pageCriteria.contains(v)) {
                        count.incrementAndGet();
                        report.addRow(new String[] { page.getPageName(), v, pageCriteria.toString() });
                    }
                }

            }

        }

    }

    public Verdict getVerdict() {
        return count.get() > 1 ? Verdict.RED : Verdict.GREEN;
    }

    private List<String> extractPageCriteria(final Header[] headers) {

        for (final Header header : headers) {
            if (HelperStrings.PAGE_CRITERIA_HEADER.equals(header.getName())) {
                return Arrays.asList(header.getValue() == null ? new String[0] : header.getValue().split(","));

            }
        }
        return Collections.emptyList();
    }

    @Override
    public String[] getHeader() {
        return new String[] { "pagename", "default argument", "page criteria" };
    }

}
