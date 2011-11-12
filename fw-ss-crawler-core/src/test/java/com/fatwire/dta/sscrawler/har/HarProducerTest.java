package com.fatwire.dta.sscrawler.har;

import java.io.StringWriter;
import java.net.URI;

import org.codehaus.jackson.JsonGenerationException;

import junit.framework.TestCase;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;

public class HarProducerTest extends TestCase {
    HarProducer har = new HarProducer();
    int level = 0;
    int count = 0;
    int total_http;
    HarLog log;
    int pageCounter = 0;

    public void testUrl() throws Exception {

        URI uri = URI
                .create("http://jsk-virtualbox:8180/cs/ContentServer?pagename=FSIIWrapper&cid=1118867611403&c=Page&p=1118867611403&childpagename=FirstSiteII/FSIILayout");
        har.init(uri);
        log = new HarLog();
        long t = System.nanoTime();
        long tStart = System.currentTimeMillis();
        ResultPage page = har.run(uri);
        HarPage hp = new HarPage("page-" + pageCounter, tStart);
        hp.setTitle(page.getPageName());
        log.addPage(hp);
        doPage(page);
        System.out.println("http time: " + total_http + " ms.");
        long e = System.nanoTime() - t;
        if (e > 1000000) {
            System.out.println("total time: " + (e / 1000000) + " ms.");
        } else {
            System.out.println("total time: " + (e / 1000) + " us.");

        }
        hp.setOnLoad((int) (System.currentTimeMillis()-tStart));
        har.shutdown();
        StringWriter w = new StringWriter();
        try {
            log.stream(w);
        } catch (JsonGenerationException e1) {
            System.out.println(w.toString());
            throw e1;
            
        }
        System.out.println(w.toString());
    }

    void doPage(ResultPage page) throws Exception {
        level++;
        count++;
        total_http += page.getReadTime();
       
        URI uri = har.toUrl(page.getUri());
        HarEntry entry = new HarEntry(uri, "page-" + pageCounter, page);
        log.addEntry(entry);

        System.out.println(level + " - " + count + " - " + page.getUri());
        // System.out.println(page.getBody());

        System.out.println(page.getTimeToFirstByte() + " - " + page.getReadTime() + " - " + page.getLinks().size());

        System.out.println();

        for (QueryString qs : page.getMarkers()) {
            // System.out.println(qs.toString());
            doPage(har.run(qs));
        }

        level--;

    }

}
