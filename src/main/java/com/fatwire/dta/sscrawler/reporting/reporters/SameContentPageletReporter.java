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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.codec.digest.DigestUtils;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.reporting.Reporter;
import com.fatwire.dta.sscrawler.util.CacheHelper;
import com.fatwire.dta.sscrawler.util.HelperStrings;

public class SameContentPageletReporter implements Reporter {

    private final Report report;

    private final Map<String, List<QueryString>> map = new HashMap<String, List<QueryString>>();
    private int group = 0;

    /**
     * @param report
     */
    public SameContentPageletReporter(final Report report) {
        super();
        this.report = report;
    }

    public synchronized void addToReport(final ResultPage page) {
        if (page.getResponseCode() == 200 && CacheHelper.shouldCache(page.getResponseHeaders())) {
            final String hex = DigestUtils.md5Hex(page.getBody());
            List<QueryString> x = map.get(hex);
            if (x == null) {
                x = new ArrayList<QueryString>();
                map.put(hex, x);
            }
            x.add(page.getUri());
        }
    }

    public synchronized void endCollecting() {
        report.startReport();
        report.addHeader("group", "pagename", "suspected parameters", "uri");

        for (final Entry<String, List<QueryString>> e : map.entrySet()) {
            final List<QueryString> l = e.getValue();
            if (l.size() > 1) {
                group++;
                final Collection<String> suspects = getMultiValuedKeys(l);
                for (final QueryString qs : l) {
                    report.addRow(Integer.toString(group), qs.getParameters().get(HelperStrings.PAGENAME), suspects
                            .toString(), qs.toString());
                }
            }
        }
        report.finishReport();
    }

    public void startCollecting() {

    }

    Collection<String> getMultiValuedKeys(final List<QueryString> l) {
        final DupeCounter nvp = new DupeCounter();
        for (final QueryString qs : l) {
            final Map<String, String> m = qs.getParameters();
            for (final Map.Entry<String, String> e : m.entrySet()) {
                final String pair = e.getKey() + '=' + e.getValue();
                nvp.add(pair);
            }

        }
        return nvp.getSingleValues();
    }

    static class DupeCounter {
        Map<String, AtomicInteger> values;

        DupeCounter() {
            values = new HashMap<String, AtomicInteger>();
        }

        void add(final String value) {
            AtomicInteger i;
            i = values.get(value);
            if (i == null) {
                i = new AtomicInteger(0);
                values.put(value, i);
            }
            i.incrementAndGet();

        }

        Collection<String> getSingleValues() {
            final Set<String> s = new HashSet<String>();
            for (final Map.Entry<String, AtomicInteger> e : values.entrySet()) {
                if (e.getValue().get() == 1) {
                    s.add(e.getKey());
                }
            }
            return s;
        }

    }

    public String getTitle() {
        return this.getClass().getSimpleName();
    }

    public Verdict getVerdict() {
        return group > 10 ? Verdict.RED : group == 0 ? Verdict.GREEN : Verdict.AMBER;
    }
}
