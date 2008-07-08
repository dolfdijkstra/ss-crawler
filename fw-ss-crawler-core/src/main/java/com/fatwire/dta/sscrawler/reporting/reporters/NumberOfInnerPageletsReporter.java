package com.fatwire.dta.sscrawler.reporting.reporters;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;

public class NumberOfInnerPageletsReporter extends ReportDelegatingReporter {

    private final int threshold;

    public NumberOfInnerPageletsReporter(final Report report, final int threshold) {
        super(report);
        this.threshold = threshold;

    }

    public void addToReport(final ResultPage page) {
        if (page.getResponseCode() == 200) {
            final int num = page.getMarkers().size();
            if (num >= threshold) {
                report.addRow(num + "\t" + page.getUri());
            }
        }
    }

    /* (non-Javadoc)
     * @see com.fatwire.dta.sscrawler.reporting.reporters.ReportDelegatingReporter#startCollecting()
     */
    @Override
    public void startCollecting() {
        super.startCollecting();
        report.addRow("threshold\t" + threshold);
        report.addRow("inner pagelets\turi");

    }

}
