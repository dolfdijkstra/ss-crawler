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

/**
 * 
 */
package com.fatwire.dta.sscrawler.reporting.reporters;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.Header;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.util.CacheHelper;
import com.fatwire.dta.sscrawler.util.HelperStrings;

/**
 * Reports passed in arguments that are not part of the pageCriteria
 * 
 * 
 * @author Dolf.Dijkstra
 * @since Apr 3, 2009
 */
public class PageCriteriaReporter extends ReportDelegatingReporter {
    private final AtomicInteger count = new AtomicInteger();

    public PageCriteriaReporter(final Report report) {
        super(report);

    }

    public Verdict getVerdict() {
        return count.get() > 1 ? Verdict.RED : Verdict.GREEN;
    }

    public void addToReport(final ResultPage page) {
        if (page.getResponseCode() != 200) {
            return; // bail out
        }

        // check if this pagelet should be cached (is cacheable)
        if (page.getBody().endsWith(HelperStrings.STATUS_NOTCACHED)) {
            return;
        } else {
            if (!CacheHelper.shouldCache(page.getResponseHeaders())) {
                return; // page should not be cached based on SiteCatalog info
            }
        }
        final Header[] headers = page.getResponseHeaders();
        for (final Header header : headers) {
            if (HelperStrings.PAGE_CRITERIA_HEADER.equals(header.getName())) {
                final List<String> pageCriteria = Arrays.asList(header.getValue() == null ? new String[0] : header
                        .getValue().split(","));
                final Map<String, String> params = new TreeMap<String, String>(page.getUri().getParameters());
                // remove params that should not be part of PageCriteria
                params.remove(HelperStrings.PAGENAME);
                params.remove(HelperStrings.RENDERMODE);
                params.remove(HelperStrings.SS_CLIENT_INDICATOR);
                params.remove(HelperStrings.SS_PAGEDATA_REQUEST);
                for (final String param : params.keySet()) {
                    if (!pageCriteria.contains(param)) {
                        count.incrementAndGet();
                        report.addRow(page.getPageName(), page.getUri().toString(), param, header.getValue());

                    }
                }
                break;
            }
        }
    }

    @Override
    protected String[] getHeader() {
        return new String[] { "pagename", "uri", "illegal parameter", "page criteria" };
    }

}
