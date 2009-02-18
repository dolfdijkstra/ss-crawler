package com.fatwire.dta.sscrawler.reporting.reporters;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.httpclient.Header;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.util.HelperStrings;

public class RootElementReporter extends ReportDelegatingReporter {

    public static final String HEADER_NAME = HelperStrings.CS_TO_SS_RESPONSE_HEADER_PREFIX +"rootelement";

    private final Set<String> elements = new CopyOnWriteArraySet<String>();

    public RootElementReporter(final Report report) {
        super(report);

    }

    public synchronized void addToReport(final ResultPage page) {
        if (page.getResponseCode() == 200) {
            for (final Header header : page.getResponseHeaders()) {
                if (RootElementReporter.HEADER_NAME.equals(header.getName())) {
                    String element = header.getValue();

                    if (!elements.contains(element)) {
                        elements.add(element);
                        report.addRow(element);
                    }
                }
            }

        }

    }

}
