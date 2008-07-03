package com.fatwire.dta.sscrawler.reporting;

public interface Report {

    void startReport();

    void addRow(String line);

    void finishReport();

}
