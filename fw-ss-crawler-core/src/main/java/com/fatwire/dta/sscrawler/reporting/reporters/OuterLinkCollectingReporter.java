/**
 * 
 */
package com.fatwire.dta.sscrawler.reporting.reporters;

import com.fatwire.dta.sscrawler.Link;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.util.SSUriHelper;

public class OuterLinkCollectingReporter extends ReportDelegatingReporter {
    private final SSUriHelper uriHelper;

    public OuterLinkCollectingReporter(final Report report,
            SSUriHelper uriHelper) {
        super(report);
        this.uriHelper = uriHelper;
    }

    public void addToReport(final ResultPage page) {
        if (page.getUri() instanceof Link) {
            report.addRow(uriHelper.toLink(page.getUri()));
        }
    }

    @Override
    protected String[] getHeader() {
        return new String[0];
    }

}