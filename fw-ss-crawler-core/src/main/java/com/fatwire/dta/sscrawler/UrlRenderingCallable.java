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

import java.net.URI;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * Callable that downloads the pagelet
 * 
 * @author Dolf Dijkstra
 * 
 */

public class UrlRenderingCallable implements Callable<ResultPage> {
    private static final Log log = LogFactory.getLog(UrlRenderingCallable.class);

    private final HttpClient client;
    private final HttpContextHolder contextHolder;
    private final URI uri;

    private final QueryString qs;

    /**
     * @param httpClientService
     * @param uri
     * @param qs
     */
    public UrlRenderingCallable(final HttpClient httpClient, HttpContextHolder contextHolder, final URI uri,
            final QueryString qs) {
        super();
        this.client = httpClient;
        this.uri = uri;
        this.qs = qs;
        this.contextHolder = contextHolder;
    }

    public ResultPage call() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("downloading " + uri);
        }
        final long startTime = System.currentTimeMillis();

        final ResultPage page = new ResultPage(qs);
        page.setStartTime(startTime);

        final HttpGet httpGet = new HttpGet(uri);
        HttpEntity entity = null;
        try {
            HttpContext ctx = contextHolder.getContext();

            final HttpResponse responseCode = client.execute(httpGet, ctx);
            page.setTimeToFirstByte(System.currentTimeMillis() - startTime);
            page.setResponseCode(responseCode.getStatusLine().getStatusCode());
            entity = responseCode.getEntity();
            HttpUriRequest req = (HttpUriRequest) ctx.getAttribute(ExecutionContext.HTTP_REQUEST);
//            HttpConnection con =(HttpConnection) ctx.getAttribute(ExecutionContext.HTTP_CONNECTION);//
//            HttpConnectionMetrics metrics=con.getMetrics();
//            metrics.getMetric(metricName)

            page.setRequestHeaders(req.getAllHeaders());
            page.setStatusLine(responseCode.getStatusLine());
            page.setResponseHeaders(responseCode.getAllHeaders());

            page.setMimetype(entity.getContentType().getValue());
            if (responseCode.getStatusLine().getStatusCode() == 200) {
                final String charSet = EntityUtils.getContentCharSet(entity);
                byte[] b = EntityUtils.toByteArray(entity);
                page.setReadTime(System.currentTimeMillis() - startTime);

                page.setPageLength(b.length);

                final String responseBody = new String(b, Charset.forName(charSet));

                if (responseBody != null) {
                    if (log.isTraceEnabled()) {
                        log.trace(responseBody);
                    }
                    page.setBody(responseBody);
                }

            } else {

                page.setReadTime(System.currentTimeMillis() - startTime);
                log.error("reponse code is " + responseCode + " for " + httpGet.getURI().toString());
            }
        } catch (final Exception e) {
            httpGet.abort();
            throw e;
        } finally {
            EntityUtils.consume(entity);
        }
        return page;
    }

    /**
     * @return the uri
     */
    public URI getUri() {
        return uri;
    }

}
