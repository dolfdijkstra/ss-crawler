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

package com.fatwire.dta.sscrawler;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.dta.sscrawler.URLReaderService.HttpClientService;

/**
 * Callable that downloads the pagelet
 * 
 * @author Dolf Dijkstra
 * 
 */

public class UrlRenderingCallable implements Callable<ResultPage> {
    private static final Log log = LogFactory.getLog(UrlRenderingCallable.class);

    private final HttpClientService httpClientService;

    private final String uri;

    private final QueryString qs;

    /**
     * @param httpClientService
     * @param uri
     * @param qs
     */
    public UrlRenderingCallable(final HttpClientService httpClientService, final String uri, final QueryString qs) {
        super();
        this.httpClientService = httpClientService;
        this.uri = uri;
        this.qs = qs;
    }

    public ResultPage call() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("downloading " + uri);
        }
        final long startTime = System.currentTimeMillis();
        
        final ResultPage page = new ResultPage(qs);
        page.setStartTime(startTime);
        final GetMethod httpGet = new GetMethod(uri);
        httpGet.setFollowRedirects(true);

        try {
            final int responseCode = httpClientService.get().executeMethod(httpGet);
            page.setTimeToFirstByte(System.currentTimeMillis() - startTime);
            page.setResponseCode(responseCode);

            page.setRequestHeaders(httpGet.getRequestHeaders());
            page.setStatusLine(httpGet.getStatusLine());
            page.setResponseHeaders(httpGet.getResponseHeaders());

            if (responseCode == 200) {
                final String charSet = httpGet.getResponseCharSet();
                final InputStream in = httpGet.getResponseBodyAsStream();
                if (in != null) {
                    byte[] b = IOUtils.toByteArray(in);
                    page.setReadTime(System.currentTimeMillis() - startTime);
                    IOUtils.closeQuietly(in);

                    page.setPageLength(b.length);

                    final String responseBody = new String(b, Charset.forName(charSet));

                    if (responseBody != null) {
                        if (log.isTraceEnabled()) {
                            log.trace(responseBody);
                        }
                        page.setBody(responseBody);
                    }

                }
            } else {

                page.setReadTime(System.currentTimeMillis() - startTime);
                log.error("reponse code is " + responseCode + " for " + httpGet.getURI().toString());
            }
        } catch (final Exception e) {
            httpGet.abort();
            throw e;
        } finally {
            httpGet.releaseConnection();
        }
        return page;
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

}
