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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.fatwire.dta.sscrawler.reporting.Report;

public class FileReport implements Report {

    private PrintWriter writer;

    private final File file;

    private final DelimitedLineConstructor dlc;

    /**
     * @param file
     */
    public FileReport(final File file, final char delimiter) {
        super();
        this.file = file;
        dlc = new DelimitedLineConstructor(delimiter);

    }

    public FileReport(final File parentDir, final String name, final char delimiter) {
        this(new File(parentDir, name), delimiter);

    }

    public synchronized void addHeader(final String... columns) {
        if (columns != null && columns.length > 0) {
            writer.println(dlc.construct(columns));
        }
    }

    public synchronized void addRow(final String... values) {
        writer.println(dlc.construct(values));
    }

    public void finishReport() {
        writer.close();
        writer = null;
    }

    public void startReport() {
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new RuntimeException("Can't create parent folder for " + file);
            }
        } else {
            if (!file.getParentFile().isDirectory()) {
                throw new RuntimeException("parent folder for " + file + " is not a directory");
            }

        }
        try {
            writer = new PrintWriter(file);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
