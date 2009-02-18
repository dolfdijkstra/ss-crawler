package com.fatwire.dta.sscrawler.reporting.reporters;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.reporting.Reporter;

public class SuspiciousContextParamReporter implements Reporter {

    private final Report report;

    /**
     * @param report
     */
    public SuspiciousContextParamReporter(final Report report) {
        super();
        this.report = report;
    }

    public void addToReport(final ResultPage page) {

        if (page.getResponseCode() == 200) {
            for (QueryString qs : page.getMarkers()) {
                if (qs.isOK()) {

                    String context = qs.getParameters().get("context");
                    if (context != null && context.indexOf(';') != -1) {
                        report.addRow(page.getPageName() + "\t" + page.getUri()
                                + "\t" + qs);
                    }

                }
            }
        }
    }

    public void endCollecting() {
        report.finishReport();
    }

    public void startCollecting() {
        report.startReport();
        report.addRow("pagename\tcalling pagelet\tcall to pagelet");
    }

}
