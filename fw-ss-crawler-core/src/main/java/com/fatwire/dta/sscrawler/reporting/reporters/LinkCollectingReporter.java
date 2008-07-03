/**
 * 
 */
package com.fatwire.dta.sscrawler.reporting.reporters;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;

public class LinkCollectingReporter extends ReportDelegatingReporter {
    public LinkCollectingReporter(final Report report) {
        super(report);
    }

    public void addToReport(final ResultPage page) {
        final StringBuilder msg = new StringBuilder();
        msg.append(page.getPageName());
        msg.append("\t");
        msg.append(page.getUri());
        report.addRow(msg.toString());
    }

}