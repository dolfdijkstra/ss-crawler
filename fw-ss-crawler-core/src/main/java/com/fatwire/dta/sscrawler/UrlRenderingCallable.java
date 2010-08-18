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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

import org.apache.commons.httpclient.methods.GetMethod;
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
     * @param client
     * @param uri
     */
    public UrlRenderingCallable(final HttpClientService httpClientService, final String uri,final QueryString qs) {
        super();
        this.httpClientService=httpClientService;
        this.uri = uri;
        this.qs=qs;
    }

    public ResultPage call() throws Exception {
        if (log.isDebugEnabled())
            log.debug("downloading " + uri);
        final long startTime = System.currentTimeMillis();
        final ResultPage page = new ResultPage(qs);
        final GetMethod httpGet = new GetMethod(uri);
        httpGet.setFollowRedirects(true);

        try {
            final int responseCode = this.httpClientService.get().executeMethod(httpGet);
            page.setResponseCode(responseCode);
            //log.info(iGetResultCode);
            page.setResponseHeaders(httpGet.getResponseHeaders());
            if (responseCode == 200) {
                final String charSet = httpGet.getResponseCharSet();
                //log.info(charSet);

                final InputStream in = httpGet.getResponseBodyAsStream();
                if (in != null) {
                    final Reader reader = new InputStreamReader(in, Charset
                            .forName(charSet));
                    final String responseBody = copy(reader);
                    in.close();
                    page.setReadTime(System.currentTimeMillis() - startTime);
                    if (responseBody != null) {
                        if (log.isTraceEnabled()) {
                            log.trace(responseBody);
                        }
                        page.setBody(responseBody);
                    }

                }
            } else {
                
                page.setReadTime(System.currentTimeMillis() - startTime);
                log.error("reponse code is " + responseCode + " for "
                        + httpGet.getURI().toString());
            }
        } catch(Exception e){
            httpGet.abort();
            throw e;
        } finally {
            httpGet.releaseConnection();
        }
        return page;
    }

    /**
     * @param builder
     * @param reader
     * @throws IOException
     */
    private String copy(final Reader reader) throws IOException {
        final StringBuilder builder = new StringBuilder();
        final char[] c = new char[1024];
        int s;
        while ((s = reader.read(c)) != -1) {
            builder.append(c, 0, s);

        }
        return builder.toString();
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

}
