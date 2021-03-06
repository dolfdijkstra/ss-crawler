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

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.dta.sscrawler.reporting.Report;

public class ReportCollection implements Report {
    private final Log log = LogFactory.getLog(getClass());

    private final List<Report> reports = new CopyOnWriteArrayList<Report>();

    public ReportCollection() {

    }

    public ReportCollection(final Report... reports) {
        for (final Report report : reports) {
            addReport(report);
        }
    }

    public void addReport(final Report reporter) {
        reports.add(reporter);
    }

    public void addHeader(final String... columns) {
        for (final Iterator<Report> i = reports.iterator(); i.hasNext();) {
            final Report report = i.next();
            try {
                report.addHeader(columns);
            } catch (final Throwable e) {
                log.error(e, e);
            }
        }

    }

    public void addRow(final String... values) {
        for (final Iterator<Report> i = reports.iterator(); i.hasNext();) {
            final Report report = i.next();
            try {
                report.addRow(values);
            } catch (final Throwable e) {
                log.error(e, e);
                i.remove(); // remove reporter if we can't use it
            }
        }

    }

    public void finishReport() {
        for (final Iterator<Report> i = reports.iterator(); i.hasNext();) {
            final Report report = i.next();
            try {
                report.finishReport();
            } catch (final Throwable e) {
                log.error(e, e);
            }
        }

    }

    public void startReport() {
        for (final Iterator<Report> i = reports.iterator(); i.hasNext();) {
            final Report report = i.next();
            try {
                report.startReport();
            } catch (final Throwable e) {
                log.error(e, e);
                i.remove(); // remove reporter if we can't use it
            }
        }

    }

}
