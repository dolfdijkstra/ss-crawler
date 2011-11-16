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
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.fatwire.dta.sscrawler.domain.HostConfig;
import com.fatwire.dta.sscrawler.jmx.ClientConnManager;
import com.fatwire.dta.sscrawler.util.HelperStrings;

public class Configurator {
    private static final String CONNECTIONMANAGER_NAME = "com.fatwire.crawler.http:name=connectionmanager";
    // private HostConfig hostConfig;
    private ThreadSafeClientConnManager connectionManager;
    private Log log = LogFactory.getLog(getClass());

    public HttpClient initClient(HostConfig hostConfig) {
        initConnectionManager(hostConfig);

        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);

        HttpConnectionParams.setSoTimeout(httpParams, 5000);

        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

        httpParams.setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);

        httpParams.setParameter("http.protocol.wait-for-continue", 10000L);
        final DefaultHttpClient client = new DefaultHttpClient(connectionManager, httpParams);
        if (hostConfig.getProxyHost() != null) {

            client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, hostConfig.getProxyHost());
            if (hostConfig.getProxyCredentials() != null) {
                client.getCredentialsProvider().setCredentials(AuthScope.ANY, hostConfig.getProxyCredentials());
            }
        }

        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "ss-crawler/0.92");

        return client;
    }

    public ThreadSafeClientConnManager initConnectionManager(HostConfig hostConfig) {
        if (connectionManager != null)
            return connectionManager;
        connectionManager = new ThreadSafeClientConnManager();

        Scheme scheme = hostConfig.getScheme();
        if (scheme != null) {
            connectionManager.getSchemeRegistry().register(scheme);
        }

        connectionManager.setDefaultMaxPerRoute(1500);
        connectionManager.setMaxTotal(30000);
        // TODO start connection cleanup thread

        final MBeanServer platform = java.lang.management.ManagementFactory.getPlatformMBeanServer();
        try {
            platform.registerMBean(new ClientConnManager(connectionManager), new ObjectName(CONNECTIONMANAGER_NAME));
        } catch (final Throwable t) {
            log.error(t.getMessage(), t);
        }
        return connectionManager;
    }

    /**
     * @param startUri
     * @param proxyUsername
     * @param proxyPassword
     * @param proxyHost
     * @param proxyPort
     * @return
     * @throws URISyntaxException
     * @throws NumberFormatException
     */
    public HostConfig createHostConfig(URI startUri, final String proxyUsername, final String proxyPassword,
            final String proxyHost, final int proxyPort) throws URISyntaxException, NumberFormatException {

        final HostConfig hc = new HostConfig(new URI(startUri.getScheme(), null, startUri.getHost(),
                startUri.getPort(), startUri.getPath(), null, null));
        if (StringUtils.isNotBlank(proxyUsername) && StringUtils.isNotBlank(proxyUsername)) {
            hc.setProxyCredentials(new UsernamePasswordCredentials(proxyUsername, proxyPassword));
        }

        if (StringUtils.isNotBlank(proxyHost)) {
            hc.setProxyHost(new HttpHost(proxyHost, proxyPort));
        } else if (StringUtils.isNotBlank(System.getProperty("http.proxyhost"))
                && StringUtils.isNotBlank(System.getProperty("http.proxyport"))) {
            hc.setProxyHost(new HttpHost(System.getProperty("http.proxyhost"), Integer.parseInt(System
                    .getProperty("http.proxyport"))));

        }
        return hc;
    }

    final Runnable cleanUp = new Runnable() {
        public void run() {

            connectionManager.closeExpiredConnections();
            // Optionally, close connections
            // that have been idle longer than 30 sec
            connectionManager.closeIdleConnections(30, TimeUnit.SECONDS);

        }
    };

    public void shutdown() {
        try {
            connectionManager.shutdown();
        } catch (final Throwable t) {
            log.error(t.getMessage(), t);
        }
        final MBeanServer platform = java.lang.management.ManagementFactory.getPlatformMBeanServer();
        try {
            platform.unregisterMBean(new ObjectName(CONNECTIONMANAGER_NAME));
        } catch (Throwable e) {
            LogFactory.getLog(getClass()).error(e.getMessage(), e);
        }
    }

    public HttpContext getContext(HttpHost target) {

        BasicCookieStore cookieStore = new BasicCookieStore();

        BasicClientCookie cookie = new BasicClientCookie(HelperStrings.SS_CLIENT_INDICATOR, Boolean.TRUE.toString());
        cookie.setDomain(target.getHostName());
        cookie.setVersion(1);
        cookie.setPath("/");

        cookieStore.addCookie(cookie);

        // Create local HTTP context
        BasicHttpContext localContext = new BasicHttpContext();
        // Bind custom cookie store to the local context
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        localContext.setAttribute(ClientContext.COOKIE_SPEC, new BrowserCompatSpec());

        return localContext;

    }

    public HttpContextHolder createContextHolder(final HttpHost target) {
        return new HttpContextHolder() {

            private final ThreadLocal<HttpContext> context = new ThreadLocal<HttpContext>() {

                /*
                 * (non-Javadoc)
                 * 
                 * @see java.lang.ThreadLocal#initialValue()
                 */
                @Override
                protected HttpContext initialValue() {

                    return Configurator.this.getContext(target);
                }

            };

            public HttpContext getContext() {

                return context.get();
            }
        };
    }
}
