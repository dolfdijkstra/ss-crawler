package com.fatwire.dta.sscrawler.reporting;

public interface Report {

    void startReport();

    void addRow(String... values);
    
    void addHeader(String... columns);

    void finishReport();

}
