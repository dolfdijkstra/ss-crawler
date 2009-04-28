/**
 * 
 */
package com.fatwire.dta.sscrawler.reporting.reporters;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.math.stat.descriptive.SynchronizedSummaryStatistics;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;

public class PageletTimingsStatisticsReporter extends ReportDelegatingReporter {

    private final Map<String, SynchronizedSummaryStatistics> stats = new ConcurrentHashMap<String, SynchronizedSummaryStatistics>();

    private final SynchronizedSummaryStatistics total = new SynchronizedSummaryStatistics();

    private final AtomicInteger pagesDone = new AtomicInteger();

    /**
     * @param file
     */
    public PageletTimingsStatisticsReporter(final Report report) {
        super(report);

    }

    public synchronized void addToReport(final ResultPage page) {
        pagesDone.incrementAndGet();
        total.addValue(page.getReadTime());
        final String pagename = page.getPageName();
        if (pagename != null) {
            SynchronizedSummaryStatistics ss = stats.get(pagename);
            if (ss == null) {
                ss = new SynchronizedSummaryStatistics();
                stats.put(pagename, ss);
            }
            ss.addValue(page.getReadTime());
        }

    }

    @Override
    public synchronized void endCollecting() {
        report.startReport();
        //report.addRow("reporting on " + pagesDone.get() + " pages");
        final DecimalFormat df = new DecimalFormat("###0.00");
        final DecimalFormat lf = new DecimalFormat("##0");
        //     final DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
        int l = 0;
        for (final String s : stats.keySet()) {
            l = Math.max(s.length(), l);
        }
        final char[] blank = new char[l];
        Arrays.fill(blank, ' ');
        report.addHeader("pagename" + new String(blank, 0, l - 8),
                "invocations", "average", "min", "max", "standard-deviation");

        for (final Map.Entry<String, SynchronizedSummaryStatistics> e : stats
                .entrySet()) {
            SynchronizedSummaryStatistics s = e.getValue();

            final String n = e.getKey()
                    + new String(blank, 0, l - e.getKey().length());
            report.addRow(n, Long.toString(s.getN()), df.format(s.getMean()),
                    lf.format(s.getMin()), lf.format(s.getMax()), df.format(s
                            .getStandardDeviation()));

        }
        final String n = "total" + new String(blank, 0, l - "total".length());
        report.addRow(n, Long.toString(total.getN()), df
                .format(total.getMean()), lf.format(total.getMin()), lf
                .format(total.getMax()), df
                .format(total.getStandardDeviation()));

        report.finishReport();

    }
    @Override
    protected String[] getHeader() {
        return new String[0];
    }


    @Override
    public synchronized void startCollecting() {
        stats.clear();
        pagesDone.set(0);
        total.clear();

    }

}