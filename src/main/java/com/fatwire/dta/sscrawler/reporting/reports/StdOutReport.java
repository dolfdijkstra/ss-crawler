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

import com.fatwire.dta.sscrawler.reporting.Report;

public class StdOutReport implements Report {

    private final DelimitedLineConstructor dlc = new DelimitedLineConstructor('\t');

    public synchronized void addHeader(final String... columns) {
        System.out.println(dlc.construct(columns));
    }

    public synchronized void addRow(final String... values) {
        System.out.println(dlc.construct(values));
    }

    public void finishReport() {
        // TODO Auto-generated method stub

    }

    public void startReport() {
        // TODO Auto-generated method stub

    }

}
