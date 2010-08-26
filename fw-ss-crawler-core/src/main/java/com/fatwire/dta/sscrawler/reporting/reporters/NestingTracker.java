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

package com.fatwire.dta.sscrawler.reporting.reporters;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;

public class NestingTracker {

    private final ConcurrentMap<QueryString, List<QueryString>> pages = new ConcurrentHashMap<QueryString, List<QueryString>>();

    public void add(ResultPage page) {
        pages.putIfAbsent(page.getUri(), page.getMarkers());
    }

    public Set<QueryString> getKeys() {
        return pages.keySet();
    }

    public int getNestingLevel(QueryString qs) {
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
    
    List<QueryString> getMarkers(QueryString qs){
        List<QueryString> markers = pages.get(qs);
        return markers !=null?markers: Collections.<QueryString>emptyList();
        
    }
}
