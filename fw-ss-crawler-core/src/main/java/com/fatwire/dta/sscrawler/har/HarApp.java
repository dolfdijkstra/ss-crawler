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

package com.fatwire.dta.sscrawler.har;

import java.io.StringWriter;
import java.net.URI;

import org.codehaus.jackson.JsonGenerationException;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;

public class HarApp {
    HarProducer har = new HarProducer();
    int level = 0;
    int count = 0;
    int total_http;
    HarLog harLog;
    int pageCounter = 0;

    public static void main(String[] a) throws Exception {

        URI uri = URI
                .create("http://jsk-virtualbox:8180/cs/ContentServer?pagename=FSIIWrapper&cid=1118867611403&c=Page&p=1118867611403&childpagename=FirstSiteII/FSIILayout");
        HarApp app = new HarApp();
        app.init(uri);
    }

    void init(URI uri) throws Exception {
        har.init(uri);
        harLog = new HarLog();
        long t = System.nanoTime();
        long tStart = System.currentTimeMillis();
        ResultPage page = har.run(uri);
        HarPage hp = new HarPage("page-" + pageCounter, tStart);
        hp.setTitle(page.getPageName());
        harLog.addPage(hp);
        doPage(page);
        System.out.println("http time: " + total_http + " ms.");
        long e = System.nanoTime() - t;
        if (e > 1000000) {
            System.out.println("total time: " + (e / 1000000) + " ms.");
        } else {
            System.out.println("total time: " + (e / 1000) + " us.");

        }
        hp.setOnLoad((int) (System.currentTimeMillis() - tStart));
        har.shutdown();
        StringWriter w = new StringWriter();
        try {
            harLog.stream(w);
        } catch (JsonGenerationException e1) {
            System.err.println(w.toString());
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
        harLog.addEntry(entry);

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
