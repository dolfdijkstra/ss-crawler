package com.fatwire.dta.sscrawler.reporting;

import com.fatwire.dta.sscrawler.ResultPage;

public interface Reporter {

    void startCollecting();
    
    void endCollecting();
    
    void addToReport(ResultPage page);
}
