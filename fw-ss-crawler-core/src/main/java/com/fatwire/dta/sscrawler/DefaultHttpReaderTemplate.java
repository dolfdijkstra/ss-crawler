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
import java.net.URISyntaxException;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIUtils;

import com.fatwire.dta.sscrawler.util.SSUriHelper;
import com.fatwire.dta.sscrawler.util.UriUtil;

public class DefaultHttpReaderTemplate implements HttpReaderTemplate {

    private final HttpHost hostConfig;
    private final SSUriHelper uriHelper;

    private final HttpClient client;

    private final HttpContextHolder contextHolder;

    private boolean requestPageData = true;

    public DefaultHttpReaderTemplate(HttpHost hostConfig, SSUriHelper uriHelper, HttpClient client,
            HttpContextHolder contextHolder) {
        super();
        this.hostConfig = hostConfig;
        this.uriHelper = uriHelper;
        this.client = client;
        this.contextHolder = contextHolder;
    }

    public UrlRenderingCallable create(QueryString qs) {
        final URI uri = toUri(checkUri(qs));

        final UrlRenderingCallable downloader = new UrlRenderingCallable(client, contextHolder, uri, qs);
        return downloader;
    }

    private QueryString checkUri(final QueryString ssuri) {
        return UriUtil.checkForFtss(uriHelper, ssuri, requestPageData);

    }

    protected URI toUri(final QueryString ssuri) {
        try {
            return URIUtils.rewriteURI(URI.create(uriHelper.toLink(ssuri)), hostConfig, true);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }
}
