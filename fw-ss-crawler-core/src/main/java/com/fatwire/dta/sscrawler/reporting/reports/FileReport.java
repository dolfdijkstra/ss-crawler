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
    public FileReport(final File file, char delimiter) {
        super();
        this.file = file;
        this.dlc = new DelimitedLineConstructor(delimiter);

    }

    public FileReport(final File parentDir, String name, char delimiter) {
        this(new File(parentDir, name), delimiter);

    }

    public synchronized void addHeader(String... columns) {
        if (columns != null && columns.length > 0) {
            writer.println(dlc.construct(columns));
        }
    }

    public synchronized void addRow(String... values) {
        writer.println(dlc.construct(values));
    }

    public void finishReport() {
        writer.close();
        writer = null;
    }

    public void startReport() {
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new RuntimeException("Can't create parent folder for "
                        + file);
            }
        } else {
            if (!file.getParentFile().isDirectory()) {
                throw new RuntimeException("parent folder for " + file
                        + " is not a directory");
            }

        }
        try {
            this.writer = new PrintWriter(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
