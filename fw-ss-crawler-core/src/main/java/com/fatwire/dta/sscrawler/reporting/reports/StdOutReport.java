package com.fatwire.dta.sscrawler.reporting.reports;

import com.fatwire.dta.sscrawler.reporting.Report;

public class StdOutReport implements Report {

    private final DelimitedLineConstructor dlc = new DelimitedLineConstructor(',');

    public synchronized void addHeader(String... columns) {
        System.out.println(dlc.construct(columns));
    }

    public synchronized void addRow(String... values) {
        System.out.println(dlc.construct(values));
    }

    public void finishReport() {
        // TODO Auto-generated method stub

    }

    public void startReport() {
        // TODO Auto-generated method stub

    }

}
