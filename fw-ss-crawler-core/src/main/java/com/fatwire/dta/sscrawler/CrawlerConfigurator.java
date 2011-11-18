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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.cli.CommandLine;
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
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
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
import com.fatwire.dta.sscrawler.handlers.BodyHandler;
import com.fatwire.dta.sscrawler.handlers.Visitor;
import com.fatwire.dta.sscrawler.jmx.ClientConnManager;
import com.fatwire.dta.sscrawler.reporting.Reporter;
import com.fatwire.dta.sscrawler.reporting.reporters.DefaultArgumentsAsPageCriteriaReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.InnerLinkReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.InnerPageletPerOuterReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.NestingReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.Non200ResponseCodeReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.NotCachedReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.NumberOfInnerPageletsReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.OuterLinkCollectingReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.PageCollectingReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.PageCriteriaReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.PageRenderTimeReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.PageletOnlyReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.PageletReuseReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.PageletTimingsStatisticsReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.PageletUriCollectingReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.RootElementReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.SameContentPageletReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.SummaryReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.SuspiciousContextParamReporter;
import com.fatwire.dta.sscrawler.reporting.reports.FileReport;
import com.fatwire.dta.sscrawler.util.HelperStrings;
import com.fatwire.dta.sscrawler.util.SSUriHelper;
import com.fatwire.dta.sscrawler.util.UriHelperFactory;

public class CrawlerConfigurator {
    private static final String CONNECTIONMANAGER_NAME = "com.fatwire.crawler.http:name=connectionmanager";

    private ThreadSafeClientConnManager connectionManager;
    private Log log = LogFactory.getLog(getClass());
    final CommandLine cmd;
    private SSUriHelper helper;
    private HostConfig hostConfig;
    private URI startUri;
    Timer timer = new Timer();

    public CrawlerConfigurator(CommandLine cmd) {
        this.cmd = cmd;
        createHostConfig();
    }

    public HttpClient initClient() {
        initConnectionManager();

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

    /**
     * @param cmd
     * @param startUri
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    protected SSUriHelper createUriHelper(URI startUri) {
        if (helper == null) {
            if (cmd.hasOption('f')) {
                UriHelperFactory f;
                try {
                    f = (UriHelperFactory) Class.forName(cmd.getOptionValue('f')).newInstance();
                    helper = f.create(startUri.getPath());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                helper = new SSUriHelper(startUri.getPath());
            }
        }
        return helper;
    }

    public ThreadSafeClientConnManager initConnectionManager() {
        if (connectionManager != null)
            return connectionManager;
        connectionManager = new ThreadSafeClientConnManager();

        TrustStrategy trustStrategy = new TrustSelfSignedStrategy();
        SSLSocketFactory sslSocketFactory;
        try {
            sslSocketFactory = new SSLSocketFactory(trustStrategy);
            Scheme scheme;
            scheme = new Scheme("https", 443, sslSocketFactory);
            if (scheme != null) {
                connectionManager.getSchemeRegistry().register(scheme);
            }
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }

        connectionManager.setDefaultMaxPerRoute(1500);
        connectionManager.setMaxTotal(30000);

        final MBeanServer platform = java.lang.management.ManagementFactory.getPlatformMBeanServer();
        try {
            platform.registerMBean(new ClientConnManager(connectionManager), new ObjectName(CONNECTIONMANAGER_NAME));
        } catch (final Throwable t) {
            log.error(t.getMessage(), t);
        }
        timer.scheduleAtFixedRate(cleanUp, 0, 15000L);
        return connectionManager;
    }

    /**
     * @param startUri
     * @param proxyUsername
     * @param proxyPassword
     * @param proxyHost
     * @param proxyPort
     * @return
     */
    public HostConfig createHostConfig(URI startUri, final String proxyUsername, final String proxyPassword,
            final String proxyHost, final int proxyPort) {

        if (hostConfig == null) {
            HostConfig hc;
            try {
                hc = new HostConfig(new URI(startUri.getScheme(), null, startUri.getHost(), startUri.getPort(),
                        startUri.getPath(), null, null));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
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
            hostConfig = hc;
        }
        return hostConfig;
    }

    final TimerTask cleanUp = new TimerTask() {
        public void run() {

            connectionManager.closeExpiredConnections();
            // Optionally, close connections
            // that have been idle longer than 30 sec
            connectionManager.closeIdleConnections(30, TimeUnit.SECONDS);

        }
    };

    public void shutdown() {
        timer.cancel();

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

    /**
     * @param cmd
     * @return
     * @throws NumberFormatException
     */
    protected ThreadPoolExecutor createThreadPool() throws NumberFormatException {
        final int threads = Integer.parseInt(cmd.getOptionValue('t', "5"));

        return new RenderingThreadPool(threads);
    }

    /**
 
     */
    protected HostConfig createHostConfig() {
        if (hostConfig == null) {
            startUri = URI.create(cmd.getArgs()[1]);

            final int t = startUri.getPath().indexOf("/ContentServer");
            if (t == -1) {
                throw new IllegalArgumentException("/ContentServer is not found on the startUri.");
            }

            final String proxyUsername = cmd.getOptionValue("pu");
            final String proxyPassword = cmd.getOptionValue("pw");
            final String proxyHost = cmd.getOptionValue("ph");
            final int proxyPort = Integer.parseInt(cmd.getOptionValue("", "8080"));

            hostConfig = createHostConfig(startUri, proxyUsername, proxyPassword, proxyHost, proxyPort);
        }
        return hostConfig;
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

                    return CrawlerConfigurator.this.getContext(target);
                }

            };

            public HttpContext getContext() {

                return context.get();
            }
        };
    }

    public List<Reporter> createReporters() {
        File path = null;
        if (cmd.hasOption('d')) {
            path = new File(cmd.getOptionValue("d"));
        } else {
            path = getOutputDir();
        }
        if (path != null) {
            final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmm");
            path = new File(path, df.format(new Date()));
            path.mkdirs();
        }
        return createReporters(path);
    }

    protected File getOutputDir() {

        final File outputDir = new File("./reports");
        outputDir.mkdirs();
        return outputDir;
    }

    protected List<Reporter> createReporters(final File outputDir) {
        final SSUriHelper helper = createUriHelper(startUri);
        final List<Reporter> reporters = new ArrayList<Reporter>();
        reporters.add(new PageletUriCollectingReporter(new FileReport(outputDir, "pagelets.tsv", '\t')));
        reporters.add(new PageCollectingReporter(new File(outputDir, "pages")));
        reporters.add(new OuterLinkCollectingReporter(new FileReport(outputDir, "browsable-links.tsv", '\t'), helper));
        reporters.add(new InnerLinkReporter(new FileReport(outputDir, "inner-links.tsv", '\t')));

        reporters.add(new PageletTimingsStatisticsReporter(new FileReport(outputDir, "pagelet-stats.tsv", '\t')));
        reporters.add(new PageRenderTimeReporter(new FileReport(outputDir, "pagelet-timings.tsv", '\t')));

        reporters.add(new PageCriteriaReporter(new FileReport(outputDir, "pagecriteria.tsv", '\t')));

        reporters.add(new RootElementReporter(new FileReport(outputDir, "root-elements.tsv", '\t')));

        reporters.add(new Non200ResponseCodeReporter(new FileReport(outputDir, "non-200-repsonse.tsv", '\t')));

        reporters.add(new InnerPageletPerOuterReporter(new FileReport(outputDir, "inner-pagelets.tsv", '\t')));
        reporters.add(new NumberOfInnerPageletsReporter(new FileReport(outputDir, "num-inner-pagelets.tsv", '\t'), 8));
        reporters.add(new NestingReporter(new FileReport(outputDir, "nesting.tsv", '\t'), 8, 5));

        reporters.add(new PageletOnlyReporter(new FileReport(outputDir, "pagelet-only.tsv", '\t')));

        reporters.add(new NotCachedReporter(new FileReport(outputDir, "not-cached.tsv", '\t')));
        reporters.add(new DefaultArgumentsAsPageCriteriaReporter(new FileReport(outputDir,
                "defaultArguments-as-pagecriteria.tsv", '\t')));

        reporters.add(new SameContentPageletReporter(new FileReport(outputDir, "same-content-pagelet.tsv", '\t')));
        reporters.add(new PageletReuseReporter(new FileReport(outputDir, "pagelet-reuse.tsv", '\t'), 5));

        reporters.add(new SuspiciousContextParamReporter(new FileReport(outputDir, "suspicious-context-parameters.tsv",
                '\t')));

        reporters.add(new SummaryReporter(new FileReport(outputDir, "summary.txt", '\t'), reporters));

        /*
         * TODO - inner uncached pagelets, is inner uncached good? Too many
         */

        return reporters;
    }

    public CrawlerService createCrawlerService(ThreadPoolExecutor readerPool) {

        DefaultHttpReaderTemplate template = createDefaultHttpReaderTemplate();

        CrawlerService reader = new CrawlerService(readerPool, template);

        Visitor<ResultPage> handler = createBodyHandler();

        reader.setHandler(handler);

        if (cmd.hasOption('m')) {
            reader.setMaxPages(Integer.parseInt(cmd.getOptionValue('m')));
        }

        return reader;
    }

    protected Visitor<ResultPage> createBodyHandler() {
        helper = createUriHelper(getStartUri());
        return new BodyHandler(helper);
    }

    protected URI getStartUri() {
        return startUri;
    }

    protected DefaultHttpReaderTemplate createDefaultHttpReaderTemplate() {
        hostConfig = createHostConfig();
        return new DefaultHttpReaderTemplate(hostConfig.getTargetHost(), helper, initClient(),
                createContextHolder(hostConfig.getTargetHost()));
    }

    public Crawler createCrawler(CrawlerService reader) {
        // URI startUri = URI.create(cmd.getArgs()[1]);

        SSUriHelper helper = createUriHelper(startUri);
        Crawler crawler = new Crawler(reader);

        crawler.addReporters(createReporters());
        crawler.setStartUri(helper.createLink(startUri));
        return crawler;
    }
}
