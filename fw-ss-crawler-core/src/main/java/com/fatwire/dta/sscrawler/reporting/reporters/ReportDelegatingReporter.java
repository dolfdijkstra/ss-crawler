package com.fatwire.dta.sscrawler.reporting.reporters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.reporting.Reporter;

public abstract class ReportDelegatingReporter implements Reporter {

    protected final Report report;

    protected final Log log = LogFactory.getLog(getClass());

    public ReportDelegatingReporter(final Report report) {
        super();
        this.report = report;
    }

    public void endCollecting() {
        report.finishReport();
    }

    public void startCollecting() {
        report.startReport();
    }

}