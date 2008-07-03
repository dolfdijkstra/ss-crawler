package com.fatwire.dta.sscrawler.reporting.reporters;

import java.util.List;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.util.HelperStrings;

public class InnerPageletReporter extends ReportDelegatingReporter {

    public InnerPageletReporter(final Report report) {
        super(report);
    }

    public void addToReport(final ResultPage page) {
        if (page.getResponseCode() == 200) {
            final List<QueryString> markers = page.getMarkers();
            if (!markers.isEmpty()) {

                final StringBuilder b = new StringBuilder("inner pagelets on ");
                b.append(page.getUri().toString());
                for (final QueryString qs : markers) {
                    b.append(HelperStrings.CRLF);
                    b.append("# ");
                    b.append(qs.toString());

                }
                report.addRow(b.toString());
            }
        }

    }

}
