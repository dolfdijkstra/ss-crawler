package com.fatwire.dta.sscrawler.reporting.reports;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.dta.sscrawler.reporting.Report;

public class WarningLevelLoggingReport implements Report {

    protected final Log log;

    public WarningLevelLoggingReport(String loggerName) {
        log = LogFactory.getLog(loggerName);
    }

    public void addRow(String... line) {
        for (String l : line) {
            log.warn(l);
        }
    }

    public void finishReport() {
        // TODO Auto-generated method stub

    }

    public void startReport() {
        // TODO Auto-generated method stub

    }

    public void addHeader(String... columns) {
        // TODO Auto-generated method stub

    }

}
