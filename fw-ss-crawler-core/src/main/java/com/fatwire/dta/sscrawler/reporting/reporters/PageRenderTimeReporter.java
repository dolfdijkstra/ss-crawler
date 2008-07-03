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
        report.addRow(page.getPageName() + "\t"
                + page.getReadTime() + "\t"
                + page.getResponseCode() + "\t" + page.getUri());

    }

    /* (non-Javadoc)
     * @see com.fatwire.dta.sscrawler.reporting.reporters.ReportDelegatingReporter#startCollecting()
     */
    @Override
    public void startCollecting() {
        super.startCollecting();
        report.addRow("pagename\tdownload time\tstatuscode\targuments");
                
        
    }

}