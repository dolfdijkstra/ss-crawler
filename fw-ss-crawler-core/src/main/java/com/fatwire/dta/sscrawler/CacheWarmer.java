package com.fatwire.dta.sscrawler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fatwire.dta.sscrawler.reporting.Reporter;
import com.fatwire.dta.sscrawler.reporting.reporters.PageletTimingsStatisticsReporter;
import com.fatwire.dta.sscrawler.reporting.reports.StdOutReport;
import com.fatwire.dta.sscrawler.util.SSUriHelper;

public class CacheWarmer extends App {

    /*
     * (non-Javadoc)
     * 
     * @see com.fatwire.dta.sscrawler.App#createReporters(java.io.File,
     * com.fatwire.dta.sscrawler.util.SSUriHelper)
     */
    @Override
    protected List<Reporter> createReporters(File outputDir, SSUriHelper helper) {
        List<Reporter> reporters = new ArrayList<Reporter>();
        reporters.add(new PageletTimingsStatisticsReporter(new StdOutReport()));

        return reporters;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fatwire.dta.sscrawler.App#getOutputDir()
     */
    @Override
    protected File getOutputDir() {
        return null;
    }

}
