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

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import com.fatwire.dta.sscrawler.Configurator;
import com.fatwire.dta.sscrawler.CrawlerConfigurator;
import com.fatwire.dta.sscrawler.HttpContextHolder;
import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.UrlRenderingCallable;
import com.fatwire.dta.sscrawler.domain.HostConfig;
import com.fatwire.dta.sscrawler.handlers.BodyLinkHandler;
import com.fatwire.dta.sscrawler.handlers.BodyMarkerHandler;
import com.fatwire.dta.sscrawler.util.SSUriHelper;
import com.fatwire.dta.sscrawler.util.UriUtil;

public class HarProducer implements HttpContextHolder {
    Configurator conf = new CrawlerConfigurator();
    // private SingleClientConnManager connectionManager;
    private HttpHost target;
    private HttpClient httpClient;
    private boolean requestPageData = true;
    private SSUriHelper uriHelper;
    private BodyLinkHandler bodyLinkHandler;
    private BodyMarkerHandler bodyMarkerHandler;
    private HttpContext context;

    ResultPage run(URI uri) throws Exception {
        QueryString qs = uriHelper.createLink(uri);
        return run(qs);
    }

    ResultPage run(QueryString qs) throws Exception {
        // System.out.println("downloading " + qs);
        URI u = toUrl(checkUri(qs));
        System.out.println("downloading " + qs);
        System.out.println("downloading " + u);
        UrlRenderingCallable renderer = new UrlRenderingCallable(httpClient, this, u, qs);
        ResultPage page = renderer.call();
        page.setConnectTime(-1);
        long t = System.nanoTime();
      
      
        bodyMarkerHandler.visit(page);
        bodyLinkHandler.visit(page);
        System.out.println("Visiting page " + page.getUri() +" took " + ((System.nanoTime()-t)/1000) +"us.");
        return page;
    }

    protected URI toUrl(final QueryString ssuri) throws URISyntaxException {

        URI part = URI.create(uriHelper.toLink(ssuri));
        return new URI(target.getSchemeName(), null, target.getHostName(), target.getPort(), part.getPath(),
                part.getQuery(), null);
    }

    private QueryString checkUri(final QueryString ssuri) {
        return UriUtil.checkForFtss(uriHelper, ssuri, requestPageData);
    }

    void init(URI uri) throws NumberFormatException, URISyntaxException {

        uriHelper = new SSUriHelper(uri.getPath());

        HostConfig hostConfig = conf.createHostConfig(uri, null, null, null, -1);
        target = hostConfig.getTargetHost();
        httpClient = conf.initClient();
        bodyLinkHandler = new BodyLinkHandler(uriHelper);
        bodyMarkerHandler = new BodyMarkerHandler(uriHelper);
    }

    void shutdown() throws Exception {

        conf.shutdown();
    }

    public HttpContext getContext() {

        if (context == null) {

            context = conf.getContext(target);
        }
        return context;
    }

}
