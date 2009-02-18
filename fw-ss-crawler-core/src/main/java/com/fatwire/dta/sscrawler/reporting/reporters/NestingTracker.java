package com.fatwire.dta.sscrawler.reporting.reporters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;

public class NestingTracker {

    private final Map<QueryString, List<QueryString>> pages = new HashMap<QueryString, List<QueryString>>();

    public synchronized void add(ResultPage page) {
        if (!pages.containsKey(page.getUri())) {
            pages.put(page.getUri(), page.getMarkers());
        }
    }

    public synchronized Set<QueryString> getKeys() {
        return pages.keySet();
    }

    public synchronized int getNestingLevel(QueryString qs) {
        int level = 0;
        if (qs == null)
            return 0;
        if (pages.containsKey(qs)) {
            for (QueryString inner : pages.get(qs)) {
                level++;
                level = level + getNestingLevel(inner);
            }
        }
        return level;

    }
}
