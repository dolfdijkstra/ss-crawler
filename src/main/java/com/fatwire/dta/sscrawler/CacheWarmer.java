/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    protected List<Reporter> createReporters(final File outputDir, final SSUriHelper helper) {
        final List<Reporter> reporters = new ArrayList<Reporter>();
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
