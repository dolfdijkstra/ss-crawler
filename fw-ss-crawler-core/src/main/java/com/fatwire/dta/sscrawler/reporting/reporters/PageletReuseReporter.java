package com.fatwire.dta.sscrawler.reporting.reporters;

import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;

public class PageletReuseReporter extends ReportDelegatingReporter {

    private final int threshold;
    private final PageletTracker tracker = new PageletTracker();
    
    public PageletReuseReporter(Report report, final int threshold) {
        super(report);
        this.threshold = threshold;

    }



    public synchronized void addToReport(ResultPage page) {
        if (page.getResponseCode() == 200) {
            tracker.add(page);
        }
    }

    /* (non-Javadoc)
     * @see com.fatwire.dta.sscrawler.reporting.reporters.ReportDelegatingReporter#endCollecting()
     */
    @Override
    public synchronized void endCollecting() {
        report.startReport();
        report.addHeader("threshold: " + threshold);
        report.addHeader(getHeader());

        for (Entry<QueryString, AtomicInteger> e : tracker.getEntries()) {
            QueryString qs = e.getKey();

            int level = e.getValue().get();
            if (level >= threshold) {
                report.addRow(qs.toString(), Integer.toString(level));
            }
        }

        super.endCollecting();
    }

    /* (non-Javadoc)
     * @see com.fatwire.dta.sscrawler.reporting.reporters.ReportDelegatingReporter#startCollecting()
     */
    @Override
    public void startCollecting() {
        //do nothing, all is handled in the endCollecting call
    }
    @Override
    protected String[] getHeader() {
        return new String[] { "uri", "reuse" };
    }

}
