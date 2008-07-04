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