package com.fatwire.dta.sscrawler.reporting.reporters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.digest.DigestUtils;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.reporting.Reporter;
import com.fatwire.dta.sscrawler.util.CacheHelper;
import com.fatwire.dta.sscrawler.util.HelperStrings;

public class SameContentPageletReporter implements Reporter {

    private final Report report;

    private final Map<String, List<QueryString>> map = new HashMap<String, List<QueryString>>();

    /**
     * @param report
     */
    public SameContentPageletReporter(final Report report) {
        super();
        this.report = report;
    }

    public synchronized void addToReport(final ResultPage page) {
        if (page.getResponseCode() == 200
                && CacheHelper.shouldCache(page.getResponseHeaders())) {
            final String hex = DigestUtils.md5Hex(page.getBody());
            List<QueryString> x = map.get(hex);
            if (x == null) {
                x = new ArrayList<QueryString>();
                map.put(hex, x);
            }
            x.add(page.getUri());
        }
    }

    public synchronized void endCollecting() {
        report.startReport();
        for (final Entry<String, List<QueryString>> e : map.entrySet()) {
            final List<QueryString> l = e.getValue();
            if (l.size() > 1) {
                report.addRow("duplicates");
                for (final QueryString qs : l) {
                    report.addRow("\t"
                            + qs.getParameters().get(HelperStrings.PAGENAME)
                            + "\t" + qs.toString());
                }
            }
        }
        report.finishReport();
    }

    public void startCollecting() {

    }

}
