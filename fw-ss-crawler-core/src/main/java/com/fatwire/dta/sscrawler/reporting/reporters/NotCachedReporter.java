/**
 * 
 */
package com.fatwire.dta.sscrawler.reporting.reporters;

import java.util.Set;
import java.util.TreeSet;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.util.CacheHelper;
import com.fatwire.dta.sscrawler.util.HelperStrings;

public class NotCachedReporter extends ReportDelegatingReporter {
    private Set<String> pages = new TreeSet<String>();
    
    public NotCachedReporter(final Report report) {
        super(report);

    }


    public synchronized void addToReport(final ResultPage page) {
        if (page.getResponseCode() != 200) {
            return; //bail out
        }

        if (CacheHelper.shouldCache(page.getResponseHeaders())) {
            if (page.getBody().endsWith(HelperStrings.STATUS_NOTCACHED)) {
                report.addRow("not caching while we should" , page.getUri().toString());
            } else {
                //report.addRow("caching as expected\t" + page.getUri());
            }
        } else {
            pages.add(page.getPageName());
        }
        
    }


    /* (non-Javadoc)
     * @see com.fatwire.dta.sscrawler.reporting.reporters.ReportDelegatingReporter#endCollecting()
     */
    @Override
    public synchronized void endCollecting() {
        for (String p : pages){
            report.addRow("not caching as per SiteCatalog",p);
        }
        super.endCollecting();
    }


    @Override
    protected String[] getHeader() {
        return new String[]{"reason","uri"};
    }


}