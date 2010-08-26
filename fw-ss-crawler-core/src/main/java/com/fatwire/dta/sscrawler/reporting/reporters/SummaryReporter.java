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

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.reporting.Reporter;

public class SummaryReporter implements Reporter {

    final Report report;
    List<Reporter> reporters;

    public SummaryReporter(final Report report, final List<Reporter> reporters) {
        this.report = report;
        this.reporters = reporters;
    }

    protected String[] getHeader() {
        return new String[] { "name", "Verdict" };
    }

    public void addToReport(final ResultPage page) {

    }

    public void endCollecting() {
        report.startReport();
        final Set<Reporter> r = new TreeSet<Reporter>(new Comparator<Reporter>() {

            public int compare(final Reporter r1, final Reporter r2) {

                return r1.getTitle().compareTo(r2.getTitle());
            }

        });
        r.addAll(reporters);
        for (final Reporter reporter : r) {
            if (reporter.getVerdict() != Verdict.NONE) {
                report.addRow(reporter.getTitle(), String.valueOf(reporter.getVerdict()));
            }

        }

        report.finishReport();
    }

    public Verdict getVerdict() {
        return Verdict.NONE;
    }

    public String getTitle() {
        return this.getClass().getSimpleName();
    }

    public void startCollecting() {

    }

}
