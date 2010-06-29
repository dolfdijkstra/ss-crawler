package com.fatwire.dta.sscrawler;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;

import com.fatwire.dta.sscrawler.reporting.Reporter;
import com.fatwire.dta.sscrawler.reporting.reporters.DefaultArgumentsAsPageCriteriaReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.InnerLinkReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.InnerPageletReporter;
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
import com.fatwire.dta.sscrawler.reporting.reporters.SuspiciousContextParamReporter;
import com.fatwire.dta.sscrawler.reporting.reports.FileReport;
import com.fatwire.dta.sscrawler.util.SSUriHelper;
import com.fatwire.dta.sscrawler.util.UriHelperFactory;

public class App {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {

        if (args.length < 1) {
            throw new IllegalArgumentException(
                    "Usage: java "
                            + App.class.getName()
                            + " -startUri \"http://localhost:8080/cs/ContentServer?pagename=...\" -reportDir <report dir> -max <max pages>");
        }
        DOMConfigurator.configure("conf/log4j.xml");

        String cmd = "crawler";
        int startpos = 0;
        if (!args[0].startsWith("-")) {
            startpos = 1;
            cmd = args[0];

        }
        if ("crawler".equals(cmd)) {
            String[] a = new String[args.length - startpos];
            System.arraycopy(args, startpos, a, 0, a.length);
            crawlerMain(a);
        } else if ("warmer".equals(cmd)) {
            String[] a = new String[args.length - startpos];
            System.arraycopy(args, startpos, a, 0, a.length);
            CacheWarmer.main(a);

        }

    }

    private static void crawlerMain(final String[] args) throws NumberFormatException, IllegalArgumentException,
            URISyntaxException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Crawler crawler = new Crawler();
        File path = null;
        String factory = null;
        URI startUri = null;
        int threads = 5;

        for (int i = 0; i < args.length; i++) {
            if ("-startUri".equals(args[i])) {
                startUri = URI.create(args[++i]);
            } else if ("-reportDir".equals(args[i])) {
                path = new File(args[++i]);
            } else if ("-max".equals(args[i])) {
                crawler.setMaxPages(Integer.parseInt(args[++i]));
            } else if ("-uriHelperFactory".equals(args[i])) {
                factory = args[++i];
            } else if ("-threads".equals(args[i])) {
                threads = Integer.parseInt(args[++i]);

            }

        }
        if (startUri == null)
            throw new IllegalArgumentException("startUri is not set");
        int t = startUri.toASCIIString().indexOf("/ContentServer");
        if (t == -1) {
            throw new IllegalArgumentException("/ContentServer is not found on the startUri.");
        }
        crawler.setHost(startUri.toASCIIString().substring(0, t));
        crawler.setStartUri(new URI(null, null, null, -1, startUri.getRawPath(), startUri.getRawQuery(), startUri
                .getFragment()));

        /*
         * String proxyUsername=null; String proxyPassword=null; String
         * proxyHost=null; int proxyPort=-1;
         * 
         * HttpState state = new HttpState(); if
         * (!StringUtils.isBlank(proxyUsername) &&
         * !StringUtils.isBlank(proxyUsername)) { Credentials credentials = new
         * UsernamePasswordCredentials(proxyUsername, proxyPassword);
         * crawler.setProxyCredential(credentials);
         * 
         * state.setProxyCredentials(AuthScope.ANY, credentials); }
         * 
         * ProxyHost proxyHost1 = StringUtils.isNotBlank(proxyHost) ? new
         * ProxyHost(proxyHost, proxyPort) : null;
         * crawler.setProxyHost(proxyHost1);
         */
        if (path == null) {
            path = App.getOutputDir();
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmm");
        path = new File(path, df.format(new Date()));
        path.mkdirs();
        SSUriHelper helper = null;

        if (factory != null) {
            UriHelperFactory f = (UriHelperFactory) (Class.forName(factory).newInstance());
            helper = f.create(crawler.getStartUri().getPath());
        } else {
            helper = new SSUriHelper(crawler.getStartUri().getPath());
        }
        final ThreadPoolExecutor readerPool = new RenderingThreadPool(threads);

        crawler.setExecutor(readerPool);
        crawler.setReporters(createReporters(path, helper));
        crawler.setUriHelper(helper);
        crawler.work();
        readerPool.shutdown();
    }

    private static File getOutputDir() {

        final File outputDir = new File("./reports");// TempDir.getTempDir(App.class);
        outputDir.mkdirs();
        return outputDir;
    }

    private static List<Reporter> createReporters(File outputDir, SSUriHelper helper) {

        List<Reporter> reporters = new ArrayList<Reporter>();
        reporters.add(new PageletUriCollectingReporter(new FileReport(outputDir, "pagelets.tsv", '\t')));
        reporters.add(new OuterLinkCollectingReporter(new FileReport(outputDir, "browsable-links.tsv", '\t'), helper));

        reporters.add(new PageCollectingReporter(new File(outputDir, "pages")));

        reporters.add(new PageletTimingsStatisticsReporter(new FileReport(outputDir, "pagelet-stats.tsv", '\t')));

        reporters.add(new PageCriteriaReporter(new FileReport(outputDir, "pagecriteria.tsv", '\t')));
        reporters.add(new PageRenderTimeReporter(new FileReport(outputDir, "pagelet-timings.tsv", '\t')));

        reporters.add(new RootElementReporter(new FileReport(outputDir, "root-elements.tsv", '\t')));
        reporters.add(new NumberOfInnerPageletsReporter(new FileReport(outputDir, "num-inner-pagelets.tsv", '\t'), 12));
        reporters.add(new Non200ResponseCodeReporter(new FileReport(outputDir, "non-200-repsonse.tsv", '\t')));
        reporters.add(new InnerPageletReporter(new FileReport(outputDir, "inner-pagelets.tsv", '\t')));

        reporters.add(new InnerLinkReporter(new FileReport(outputDir, "inner-links.tsv", '\t')));

        reporters.add(new NestingReporter(new FileReport(outputDir, "nesting.tsv", '\t'), 10));
        reporters.add(new PageletOnlyReporter(new FileReport(outputDir, "pagelet-only.tsv", '\t')));
        reporters.add(new PageletReuseReporter(new FileReport(outputDir, "pagelet-reuse.tsv", '\t'), 0));

        reporters.add(new NotCachedReporter(new FileReport(outputDir, "not-cached.tsv", '\t')));
        reporters.add(new DefaultArgumentsAsPageCriteriaReporter(new FileReport(outputDir,
                "defaultArguments-as-pagecriteria.tsv", '\t')));
        reporters.add(new SameContentPageletReporter(new FileReport(outputDir, "same-content-pagelet.tsv", '\t')));
        reporters.add(new SuspiciousContextParamReporter(new FileReport(outputDir, "suspicious-context-parameters.tsv",
                '\t')));

        return reporters;
    }

}
