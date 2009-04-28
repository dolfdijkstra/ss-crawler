package com.fatwire.dta.sscrawler.reporting.reporters;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.httpclient.Header;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;

public class PageletOnlyReporter extends ReportDelegatingReporter {
    private static final String PAGELET_ONLY_HEADER = "com.futuretense.contentserver.pagedata.field.pageletonly";

    private final Set<String> pages = new CopyOnWriteArraySet<String>(); 
    
    public PageletOnlyReporter(Report report) {
        super(report);
    }

    public void addToReport(ResultPage page) {
        if (page.getResponseCode() == 200) {
            for (final Header header : page.getResponseHeaders()) {
                if (PAGELET_ONLY_HEADER.equals(header.getName())) {
                    if ("F".equals(header.getValue())) {
                        if (pages.add(page.getPageName())){
                            report.addRow(header.getValue(), page.getPageName());
                        }
                    }
                    break;
                }
            }
        }

    }

    @Override
    protected String[] getHeader() {
        return new String[] { "value", "pagename" };
    }
}
