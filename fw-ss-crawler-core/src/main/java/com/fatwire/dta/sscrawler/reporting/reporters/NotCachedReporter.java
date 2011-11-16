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

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.util.CacheHelper;
import com.fatwire.dta.sscrawler.util.HelperStrings;

public class NotCachedReporter extends ReportDelegatingReporter {
    private final Set<String> pages = new TreeSet<String>();
    private final AtomicInteger count = new AtomicInteger();

    private boolean checkedGlobalStatus = false;

    public NotCachedReporter(final Report report) {
        super(report);

    }

    public Verdict getVerdict() {
        return count.get() > 1 ? Verdict.RED : Verdict.GREEN;
    }

    public void addToReport(final ResultPage page) {
        if (page.getResponseCode() != 200) {
            return; // bail out
        }
        if (checkedGlobalStatus == false) {
            checkedGlobalStatus = true;
            if (CacheHelper.isCacheDefaultEnabled(page.getResponseHeaders()) == false) {
                report.addRow("Caching globally disabled.");
            }
        }
        if (CacheHelper.shouldCache(page.getResponseHeaders())) {
            if (page.getBody().endsWith(HelperStrings.STATUS_NOTCACHED)) {
                count.incrementAndGet();
                report.addRow("not caching while we should", page.getUri().toString());
            }
        } else {
            synchronized (pages) {
                pages.add(page.getPageName());
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.dta.sscrawler.reporting.reporters.ReportDelegatingReporter
     * #endCollecting()
     */
    @Override
    public void endCollecting() {
        synchronized (pages) {
            for (final String p : pages) {
                report.addRow("not caching as per SiteCatalog", p);
            }
        }
        super.endCollecting();
    }

    @Override
    protected String[] getHeader() {
        return new String[] { "reason", "uri" };
    }

}
