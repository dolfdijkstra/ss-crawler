package com.fatwire.dta.sscrawler.reporting.reporters;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;

public class NestingReporter extends ReportDelegatingReporter {

    private final int threshold;

    public NestingReporter(Report report, final int threshold) {
        super(report);
        this.threshold = threshold;

    }

    private final NestingTracker tracker = new NestingTracker();

    public void addToReport(ResultPage page) {
        if (page.getResponseCode() == 200) {
            tracker.add(page);
        }
    }

    /* (non-Javadoc)
     * @see com.fatwire.dta.sscrawler.reporting.reporters.ReportDelegatingReporter#endCollecting()
     */
    @Override
    public void endCollecting() {
        super.startCollecting();
        report.addHeader("threshold", Integer.toString(threshold));
        report.addHeader("uri", "nesting");

        for (QueryString qs : tracker.getKeys()) {
            int level = tracker.getNestingLevel(qs);
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
        return new String[0];
    }

}
