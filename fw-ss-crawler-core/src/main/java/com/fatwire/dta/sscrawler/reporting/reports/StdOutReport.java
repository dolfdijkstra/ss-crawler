package com.fatwire.dta.sscrawler.reporting.reports;

import com.fatwire.dta.sscrawler.reporting.Report;

public class StdOutReport implements Report {

    public void addRow(String line) {
        System.out.println(line);

    }

    public void finishReport() {
        // TODO Auto-generated method stub

    }

    public void startReport() {
        // TODO Auto-generated method stub

    }

}
