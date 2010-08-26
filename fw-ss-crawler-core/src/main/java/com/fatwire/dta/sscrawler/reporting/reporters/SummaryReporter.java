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
