/**
 * 
 */
package com.fatwire.dta.sscrawler.reporting.reporters;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;

public class PageRenderTimeReporter extends ReportDelegatingReporter {

    /**
     * @param report
     */
    public PageRenderTimeReporter(final Report report) {
        super(report);

    }

    public void addToReport(final ResultPage page) {
        report.addRow(page.getPageName(), Long.toString(page.getReadTime()),
                Integer.toString(page.getResponseCode()), page.getUri()
                        .toString());

    }

    @Override
    protected String[] getHeader() {
        return new String[] { "pagename", "download time", "statuscode",
                "arguments" };
    }

}