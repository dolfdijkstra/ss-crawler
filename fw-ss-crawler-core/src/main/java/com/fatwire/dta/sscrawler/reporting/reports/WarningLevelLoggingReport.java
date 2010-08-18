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
