package com.fatwire.dta.sscrawler.reporting.reporters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.httpclient.Header;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.util.HelperStrings;

/**
 * List default arguments not defined as pagecriteria
 * @author Dolf.Dijkstra
 * 
 */

public class DefaultArgumentsAsPageCriteriaReporter extends
        ReportDelegatingReporter {

    private Set<String> pagenamesDone = new CopyOnWriteArraySet<String>();

    public static final String DEFAULT_ARGUMENTS = HelperStrings.CS_TO_SS_RESPONSE_HEADER_PREFIX
            + "defaultarguments";

    public DefaultArgumentsAsPageCriteriaReporter(Report report) {
        super(report);

    }

    public synchronized void addToReport(ResultPage page) {
        if (page.getResponseCode() == 200) {
            if (pagenamesDone.contains(page.getPageName())) {
                return;
            }
            pagenamesDone.add(page.getPageName());
            List<String> pageCriteria = extractPageCriteria(page
                    .getResponseHeaders());
            for (final Header header : page.getResponseHeaders()) {
                if (header.getName().startsWith(DEFAULT_ARGUMENTS)) {
                    String v = header.getValue().split("\\|", 2)[0];
                    if (!pageCriteria.contains(v)) {
                        report.addRow(new String[] { page.getPageName(), v,
                                pageCriteria.toString() });
                    }
                }

            }

        }

    }

    private List<String> extractPageCriteria(final Header[] headers) {

        for (final Header header : headers) {
            if (HelperStrings.PAGE_CRITERIA_HEADER.equals(header.getName())) {
                return Arrays.asList(header.getValue() == null ? new String[0]
                        : header.getValue().split(","));

            }
        }
        return Collections.emptyList();
    }

    @Override
    public String[] getHeader() {
        return new String[] { "pagename", "default argument", "page criteria" };
    }

    /*
     * 
     * 
     com.futuretense.contentserver.pagedata.field.defaultargumentssite: site|FirstSiteII
     com.futuretense.contentserver.pagedata.field.defaultargumentsseid: seid|1121304725667
     com.futuretense.contentserver.pagedata.field.cscacheinfottl-default: 21600
     com.futuretense.contentserver.pagedata.field.sscacheinfostring: false
     com.futuretense.contentserver.pagedata.field.sscacheinfocache-by-default: false
     com.futuretense.contentserver.pagedata.field.cscacheinfocache-by-default: false
     com.futuretense.contentserver.pagedata.field.defaultargumentsrendermode: rendermode|live
     com.futuretense.contentserver.pagedata.field.defaultargumentssitepfx: sitepfx|FSII
     com.futuretense.contentserver.pagedata.field.cscacheinfostring: false
     com.futuretense.contentserver.pagedata.field.sscacheinfottl-default: 21600
     com.futuretense.contentserver.pagedata.field.pageletonly: F
     com.futuretense.contentserver.pagedata.field.cscacheinfopopulated: true
     com.futuretense.contentserver.pagedata.field.sscacheinfopopulated: true
     com.futuretense.contentserver.pagedata.field.pagename: FSIIWrapper
     com.futuretense.contentserver.pagedata.field.rootelement: FSIIWrapper
     com.futuretense.contentserver.pagedata.field.pagecriteria: locale,rendermode,seid,site,sitepfx,ft_ss

     * 
     */

}
