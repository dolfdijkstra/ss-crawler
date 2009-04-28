package com.fatwire.dta.sscrawler.reporting.reporters;

import java.util.List;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;

public class InnerPageletReporter extends ReportDelegatingReporter {

    public InnerPageletReporter(final Report report) {
        super(report);
    }

    public void addToReport(final ResultPage page) {
        if (page.getResponseCode() == 200) {
            final List<QueryString> markers = page.getMarkers();
            if (!markers.isEmpty()) {
                synchronized (this) {
                    report.addRow(page.getUri().toString());
                    for (final QueryString qs : markers) {
                        report.addRow("#", qs.toString());

                    }
                }
            }
        }

    }

    @Override
    protected String[] getHeader() {
        return new String[]{"page", "marker"};
    }

}
