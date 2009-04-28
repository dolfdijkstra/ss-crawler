/**
 * 
 */
package com.fatwire.dta.sscrawler.reporting.reporters;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;

public class PageletUriCollectingReporter extends ReportDelegatingReporter {
    public PageletUriCollectingReporter(final Report report) {
        super(report);
    }

    public void addToReport(final ResultPage page) {
        report.addRow(page.getPageName(), page.getUri().toString());
    }

    @Override
    protected String[] getHeader() {
        return new String[]{"pagename","uri"};
    }
}