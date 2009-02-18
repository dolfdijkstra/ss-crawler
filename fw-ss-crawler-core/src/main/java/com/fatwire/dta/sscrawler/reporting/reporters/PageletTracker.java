package com.fatwire.dta.sscrawler.reporting.reporters;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;

public class PageletTracker {
    private final Map<QueryString, AtomicInteger> pages = new HashMap<QueryString, AtomicInteger>();

    public void add(ResultPage page) {
        for (QueryString qs : page.getMarkers()) {
            AtomicInteger i = pages.get(qs);
            if (i ==null){
                i = new AtomicInteger();
                pages.put(qs, i);
            }
            
            i.incrementAndGet();
        }
    }

    public Set<Entry<QueryString, AtomicInteger>> getEntries() {
        return pages.entrySet();
    }


}
