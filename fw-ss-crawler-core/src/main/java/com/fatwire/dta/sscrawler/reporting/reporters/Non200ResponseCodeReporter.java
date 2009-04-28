package com.fatwire.dta.sscrawler.reporting.reporters;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;

public class Non200ResponseCodeReporter extends ReportDelegatingReporter {

    public Non200ResponseCodeReporter(final Report report) {
        super(report);
    }

    public void addToReport(final ResultPage page) {
        if (page.getResponseCode() != 200) {
            report.addRow(page.getUri().toString(), Integer.toString(page.getResponseCode()));
        }

    }

    @Override
    protected String[] getHeader() {
        return new String[]{"uri","responsecode"};
    }

}
