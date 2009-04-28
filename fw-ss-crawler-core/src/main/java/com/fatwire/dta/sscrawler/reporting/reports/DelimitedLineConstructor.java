package com.fatwire.dta.sscrawler.reporting.reports;

public class DelimitedLineConstructor {

    private final char seperator;

    public DelimitedLineConstructor(char delimiter) {
        this.seperator = delimiter;
    }

    String construct(String[] columns) {
        if (columns == null || columns.length == 0)
            return null;
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                b.append(seperator);
            }
            b.append(columns[i]);
        }
        return b.toString();

    }
}
