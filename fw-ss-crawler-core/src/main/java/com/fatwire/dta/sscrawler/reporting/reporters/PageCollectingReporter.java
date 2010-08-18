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

/**
 * 
 */
package com.fatwire.dta.sscrawler.reporting.reporters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Reporter;

public class PageCollectingReporter implements Reporter {
    private final Log log = LogFactory.getLog(getClass());

    private static final char[] CRLF = "\r\n".toCharArray();

    private final File dir;

    private String path;

    private final AtomicLong idGen = new AtomicLong(System.currentTimeMillis());

    private PrintWriter pwriter;

    public PageCollectingReporter(final File dir) {
        this.dir = dir;
        dir.getParentFile().mkdirs();
        path = dir.getName();
    }

    public void addToReport(final ResultPage page) {
        if (page.getResponseCode() != 200) {
            return; //bail out
        }
        final long id = idGen.incrementAndGet();
        FileWriter writer = null;

        try {
            final String p = (page.getPageName() != null ? page.getPageName()
                    : "")
                    + "-" + id + ".txt";
            final File pFile = new File(dir, p);
            pwriter.println(page.getUri() + "\t" + path + File.separator
                    + p.replace('/', File.separatorChar));
            pwriter.flush();
            pFile.getAbsoluteFile().getParentFile().mkdirs();
            writer = new FileWriter(pFile);
            writer.write(page.getUri().toString());
            writer.write(PageCollectingReporter.CRLF);
            writer.write(PageCollectingReporter.CRLF);
            final Header[] headers = page.getResponseHeaders();
            for (final Header header : headers) {
                writer.write(header.toExternalForm());
            }
            writer.write(PageCollectingReporter.CRLF);
            writer.write(page.getBody());
        } catch (final IOException e) {
            log.error(e, e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (final IOException ignored) {
                    //ignored
                }
            }
        }

    }

    public void endCollecting() {
        pwriter.close();

    }

    public void startCollecting() {
        try {
            pwriter = new PrintWriter(new File(this.dir.getParentFile(),
                    "pages-list.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}