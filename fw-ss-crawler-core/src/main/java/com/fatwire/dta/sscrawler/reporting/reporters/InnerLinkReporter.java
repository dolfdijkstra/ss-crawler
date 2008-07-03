package com.fatwire.dta.sscrawler.reporting.reporters;

import java.util.List;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.util.HelperStrings;

public class InnerLinkReporter extends ReportDelegatingReporter {

    public InnerLinkReporter(final Report report) {
        super(report);
    }

    public void addToReport(final ResultPage page) {
        if (page.getResponseCode() == 200) {
            final List<QueryString> links = page.getLinks();
            if (!links.isEmpty()) {

                final StringBuilder b = new StringBuilder("links on ");
                b.append(page.getUri().toString());
                for (final QueryString qs : links) {
                    b.append(HelperStrings.CRLF);
                    b.append("# ");
                    b.append(qs.toString());

                }
                report.addRow(b.toString());
            }
        }

    }

}
