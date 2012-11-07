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

import java.util.Set;

import junit.framework.TestCase;

import com.fatwire.dta.sscrawler.Link;
import com.fatwire.dta.sscrawler.Pagelet;
import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.reporters.NestingTracker;

public class NestingTrackerTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAdd() {
        NestingTracker tracker = new NestingTracker();
        Link qs = new Link();
        qs.addParameter("pagename", "XXX");

        Pagelet inner = new Pagelet();
        inner.addParameter("pagename", "YYY");
        inner.addParameter("c", "Page");

        ResultPage page = new ResultPage(qs);
        page.addMarker(inner);

        tracker.add(page);

        Set s = tracker.getKeys();
        assertEquals(1, s.size());

    }

    public void testGetKeys() {
        NestingTracker tracker = new NestingTracker();
        QueryString qs = new Link();
        qs.addParameter("pagename", "XXX");

        Pagelet inner = new Pagelet();
        inner.addParameter("pagename", "YYY");
        inner.addParameter("c", "Page");

        ResultPage page = new ResultPage(qs);
        page.addMarker(inner);

        tracker.add(page);

        Set<QueryString> s = tracker.getKeys();
        for (QueryString qs2 : s) {
            assertEquals(qs2, qs);
        }
    }

    public void testGetNestingLevel() {
        NestingTracker tracker = new NestingTracker();
        QueryString qs = new Link();
        qs.addParameter("pagename", "XXX");

        Pagelet inner = new Pagelet();
        inner.addParameter("pagename", "YYY");
        inner.addParameter("c", "Page");

        ResultPage page = new ResultPage(qs);
        page.addMarker(inner);

        tracker.add(page);

        Set<QueryString> s = tracker.getKeys();
        for (QueryString qs2 : s) {
            System.out.println(qs2);
            assertEquals(1, tracker.getNestingLevel(qs2));
        }
        
    }

}
