package com.fatwire.dta.sscrawler.har;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.URLReaderService.HttpClientService;
import com.fatwire.dta.sscrawler.UrlRenderingCallable;
import com.fatwire.dta.sscrawler.domain.HostConfig;
import com.fatwire.dta.sscrawler.handlers.BodyLinkHandler;
import com.fatwire.dta.sscrawler.handlers.BodyMarkerHandler;
import com.fatwire.dta.sscrawler.util.HelperStrings;
import com.fatwire.dta.sscrawler.util.SSUriHelper;

public class HarProducer implements HttpClientService {
    public static class OneConnectionManager implements HttpConnectionManager {
        private HttpConnection connection;
        long lastOpen = -1;
        private HttpConnectionManagerParams params = new HttpConnectionManagerParams();

        /**
         * @param idleTimeout
         * @see org.apache.commons.httpclient.SimpleHttpConnectionManager#closeIdleConnections(long)
         */
        public void closeIdleConnections(long idleTimeout) {
            connection.close();
        }

        /**
         * @param hostConfiguration
         * @param timeout
         * @return
         * @deprecated
         * @see org.apache.commons.httpclient.SimpleHttpConnectionManager#getConnection(org.apache.commons.httpclient.HostConfiguration,
         *      long)
         */
        public HttpConnection getConnection(HostConfiguration hostConfiguration, long timeout) {
            System.out.println("getConnection(HostConfiguration hostConfiguration, long timeout) ");
            checkConnection(hostConfiguration);
            return connection;
        }

        /**
         * @param hostConfiguration
         */
        protected void checkConnection(HostConfiguration hostConfiguration) {
            if (connection == null) {
                connection = new HttpConnection(hostConfiguration) {

                    /* (non-Javadoc)
                     * @see org.apache.commons.httpclient.HttpConnection#open()
                     */
                    @Override
                    public void open() throws IOException {
                        long t = System.currentTimeMillis();

                        super.open();
                        lastOpen = System.currentTimeMillis() - t;
                        // System.out.println("open took: " + (lastOpen) +
                        // "ms.");

                    }

                };
                connection.setHttpConnectionManager(this);
                connection.setParams(params);
            } else {
                lastOpen = -1;
            }
        }

        /**
         * @param hostConfiguration
         * @return
         * @see org.apache.commons.httpclient.SimpleHttpConnectionManager#getConnection(org.apache.commons.httpclient.HostConfiguration)
         */
        public HttpConnection getConnection(HostConfiguration hostConfiguration) {
            System.out.println("getConnection(HostConfiguration hostConfiguration)");
            checkConnection(hostConfiguration);
            return connection;

        }

        /**
         * @param hostConfiguration
         * @param timeout
         * @return
         * @see org.apache.commons.httpclient.SimpleHttpConnectionManager#getConnectionWithTimeout(org.apache.commons.httpclient.HostConfiguration,
         *      long)
         */
        public HttpConnection getConnectionWithTimeout(HostConfiguration hostConfiguration, long timeout) {
            System.out.println("getConnectionWithTimeout(HostConfiguration hostConfiguration," + timeout + ")");
            checkConnection(hostConfiguration);
            return connection;
        }

        /**
         * @return
         * @see org.apache.commons.httpclient.SimpleHttpConnectionManager#getParams()
         */
        public HttpConnectionManagerParams getParams() {
            return params;
        }

        /**
         * @return
         * @deprecated
         * @see org.apache.commons.httpclient.SimpleHttpConnectionManager#isConnectionStaleCheckingEnabled()
         */
        public boolean isConnectionStaleCheckingEnabled() {
            return params.isStaleCheckingEnabled();
        }

        /**
         * @param conn
         * @see org.apache.commons.httpclient.SimpleHttpConnectionManager#releaseConnection(org.apache.commons.httpclient.HttpConnection)
         */
        public void releaseConnection(HttpConnection conn) {

        }

        /**
         * @param connectionStaleCheckingEnabled
         * @deprecated
         * @see org.apache.commons.httpclient.SimpleHttpConnectionManager#setConnectionStaleCheckingEnabled(boolean)
         */
        public void setConnectionStaleCheckingEnabled(boolean connectionStaleCheckingEnabled) {

        }

        /**
         * @param params
         * @see org.apache.commons.httpclient.SimpleHttpConnectionManager#setParams(org.apache.commons.httpclient.params.HttpConnectionManagerParams)
         */
        public void setParams(HttpConnectionManagerParams params) {
            this.params = params;
        }

        public void shutdown() {
            connection.close();
        }
    }

    private OneConnectionManager connectionManager;
    private HostConfig hostConfig;
    private HttpClient httpClient;
    private boolean requestPageData = true;
    private SSUriHelper uriHelper;
    private BodyLinkHandler bodyLinkHandler;
    private BodyMarkerHandler bodyMarkerHandler;

    ResultPage run(URI uri) throws Exception {
        QueryString qs = uriHelper.createLink(uri);
        return run(qs);
    }

    ResultPage run(QueryString qs) throws Exception {
        // System.out.println("downloading " + qs);
        UrlRenderingCallable renderer = new UrlRenderingCallable(this, checkUri(qs), qs);
        ResultPage page = renderer.call();
        page.setConnectTime(connectionManager.lastOpen == -1 ? 0L : connectionManager.lastOpen);
        bodyMarkerHandler.visit(page);
        bodyLinkHandler.visit(page);
        return page;
    }

    public HttpClient get() {

        return httpClient;
    }

    protected URI toUrl(final QueryString ssuri) throws URISyntaxException {
        URI part = URI.create(uriHelper.toLink(ssuri));
        return new URI(hostConfig.getProtocol().getScheme(), null, hostConfig.getHostname(), hostConfig.getPort(),
                part.getPath(), part.getQuery(), null);
    }

    private String checkUri(final QueryString ssuri) {
        final String uri = uriHelper.toLink(ssuri);
        if (requestPageData) {
            if (ssuri.has(HelperStrings.SS_PAGEDATA_REQUEST) == false) {
                return uri + "&" + HelperStrings.SS_PAGEDATA_REQUEST + "=true";
            }
        } else {
            if (ssuri.has(HelperStrings.SS_CLIENT_INDICATOR) == false) {
                return uri + "&" + HelperStrings.SS_CLIENT_INDICATOR + "=true";
            }

        }
        return uri;
    }

    void init(URI uri) {

        uriHelper = new SSUriHelper(uri.getPath());
        hostConfig = new HostConfig();

        hostConfig.setHostname(uri.getHost());

        hostConfig.setPort(uri.getPort() == -1 ? 80 : uri.getPort());
        hostConfig.setDomain(uri.getPath());
        hostConfig.setProtocol(uri.getScheme());
        initConnectionManager();
        httpClient = initClient();
        bodyLinkHandler = new BodyLinkHandler(uriHelper);
        bodyMarkerHandler = new BodyMarkerHandler(uriHelper);
    }

    void shutdown() throws Exception {
        connectionManager.getClass().getMethod("shutdown", new Class[0]).invoke(connectionManager, new Object[0]);
    }

    protected void initConnectionManager() {
        connectionManager = new OneConnectionManager();

        connectionManager.getParams().setConnectionTimeout(30000);
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(1500);
        connectionManager.getParams().setMaxTotalConnections(30000);
    }

    protected HttpClient initClient() {
        final HttpClient client = new HttpClient(connectionManager);
        client.getHostConfiguration().setHost(hostConfig.getHostname(), hostConfig.getPort(), hostConfig.getProtocol());

        if (hostConfig.getProxyHost() != null) {
            client.getHostConfiguration().setProxyHost(hostConfig.getProxyHost());
            if (hostConfig.getProxyCredentials() != null) {
                client.getState().setProxyCredentials(AuthScope.ANY, hostConfig.getProxyCredentials());
            }
        }

        client.getParams().setParameter(HttpMethodParams.USER_AGENT, "ss-crawler-0.9");

        // RFC 2101 cookie management spec is used per default
        // to parse, validate, format & match cookies
        // client.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
        // client.getParams().setCookiePolicy(CookiePolicy.DEFAULT);
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

        // client.getParams().makeStrict();
        client.getParams().getDefaults().setBooleanParameter(HttpMethodParams.SINGLE_COOKIE_HEADER, true);

        client.getState().addCookie(
                new Cookie(hostConfig.getHostname(), HelperStrings.SS_CLIENT_INDICATOR, Boolean.TRUE.toString(),
                        hostConfig.getDomain(), -1, false));
        return client;
    }

}
