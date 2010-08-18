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
