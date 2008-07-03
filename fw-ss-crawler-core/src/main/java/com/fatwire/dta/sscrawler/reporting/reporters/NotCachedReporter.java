/**
 * 
 */
package com.fatwire.dta.sscrawler.reporting.reporters;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.util.CacheHelper;
import com.fatwire.dta.sscrawler.util.HelperStrings;

public class NotCachedReporter extends ReportDelegatingReporter {
    public NotCachedReporter(final Report report) {
        super(report);

    }


    public void addToReport(final ResultPage page) {
        if (page.getResponseCode() != 200) {
            return; //bail out
        }

        if (CacheHelper.shouldCache(page.getResponseHeaders())) {
            if (page.getBody().endsWith(HelperStrings.STATUS_NOTCACHED)) {
                report.addRow("not caching while we should\t" + page.getUri());
            } else {
                //report.addRow("not caching as expected\t" + page.getUri());
            }
        } else {
            report.addRow("not caching as per SiteCatalog\t" + page.getUri());

        }
    }


}