package com.fatwire.dta.sscrawler.reporting.reporters;

import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;

public class PageletReuseReporter extends ReportDelegatingReporter {

    private final int threshold;

    public PageletReuseReporter(Report report, final int threshold) {
        super(report);
        this.threshold = threshold;

    }

    private final PageletTracker tracker = new PageletTracker();

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
        super.startCollecting();
        report.addRow("threshold\t" + threshold);
        report.addRow("uri\treuse");

        for ( Entry<QueryString, AtomicInteger> e: tracker.getEntries()) {
            QueryString qs = e.getKey();
            
            int level = e.getValue().get();
            if (level >= threshold) {
                report.addRow(qs.toString() + "\t"
                        + level);
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

}
