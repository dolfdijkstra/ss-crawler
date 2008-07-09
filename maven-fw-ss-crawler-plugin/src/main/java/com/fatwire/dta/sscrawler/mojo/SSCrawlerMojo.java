package com.fatwire.dta.sscrawler.mojo;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import com.fatwire.dta.sscrawler.Crawler;
import com.fatwire.dta.sscrawler.RenderingThreadPool;
import com.fatwire.dta.sscrawler.reporting.Reporter;
import com.fatwire.dta.sscrawler.reporting.reporters.DefaultArgumentsAsPageCriteriaReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.InnerLinkReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.InnerPageletReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.LinkCollectingReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.NestingReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.Non200ResponseCodeReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.NotCachedReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.NumberOfInnerPageletsReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.OuterLinkCollectingReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.PageCollectingReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.PageCriteriaReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.PageRenderTimeReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.PageletOnlyReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.PageletTimingsStatisticsReporter;
import com.fatwire.dta.sscrawler.reporting.reporters.RootElementReporter;
import com.fatwire.dta.sscrawler.reporting.reports.FileReport;
import com.fatwire.dta.sscrawler.util.SSUriHelper;
import com.fatwire.dta.sscrawler.util.UriHelperFactory;

/**
 * Goal crawls ContentServer mimicing Satellite Server.
 *
 * @goal crawl
 * 
 * @phase test
 */
public class SSCrawlerMojo extends AbstractMojo {
    /**
     * 
     * The output location of the reports
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /**
     * The starting url for the crawler. This should be a url that ContentServer can understand, not a assembled url and not pointing towards Satellite.
     * 
     * @parameter
     * 
     * @required
     * 
     */
    private URL startUrl;

    /**
     * @parameter
     */

    private boolean appendReportDirWithTimestamp = true;

    /**
     * @parameter
     */

    private String reportDirDateFormat = "yyyyMMdd_HHmm";

    /**
     * 
     * Maximum number of pages to crawl.
     * @parameter
     */

    private int max = Integer.MAX_VALUE;

    /**
     * 
     * @parameter
     */

    private String uriHelperFactory;

    /**
     * The concurrency for simultaneuous download requests
     * 
     * @parameter
     */

    private int concurrency = 5;

    /**
     * @parameter
     */

    public void execute() throws MojoExecutionException {

        if (startUrl == null)
            throw new MojoExecutionException("startUrl is not set");

        URI startUri;
        try {
            startUri = startUrl.toURI();
        } catch (URISyntaxException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

        int t = startUri.toASCIIString().indexOf("/ContentServer");
        if (t == -1) {
            throw new MojoExecutionException(
                    "/ContentServer is not found on the startUrl.");
        }
        Crawler crawler = new Crawler();

        crawler.setHost(startUri.toASCIIString().substring(0, t));
        try {
            crawler.setStartUri(new URI(null, null, null, -1, startUri
                    .getRawPath(), startUri.getRawQuery(), startUri
                    .getFragment()));
        } catch (URISyntaxException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

        File path = outputDirectory;

        if (appendReportDirWithTimestamp) {
            SimpleDateFormat df = new SimpleDateFormat(this.reportDirDateFormat);
            path = new File(path, df.format(new Date()));
        }
        path.mkdirs();
        SSUriHelper helper = null;

        if (uriHelperFactory != null) {
            UriHelperFactory f;
            try {
                f = (UriHelperFactory) (Class.forName(uriHelperFactory)
                        .newInstance());
            } catch (InstantiationException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
            helper = f.create(crawler.getStartUri().getPath());
        } else {
            helper = new SSUriHelper(crawler.getStartUri().getPath());
        }

        final ThreadPoolExecutor readerPool = new RenderingThreadPool(
                concurrency);

        crawler.setExecutor(readerPool);
        crawler.setReporters(createReporters(path, helper));
        crawler.setUriHelper(helper);
        crawler.setMaxPages(max);
        try {
            crawler.work();
        } finally {
            readerPool.shutdown();
        }
    }

    private List<Reporter> createReporters(File outputDir, SSUriHelper helper) {

        List<Reporter> reporters = new ArrayList<Reporter>();
        reporters.add(new LinkCollectingReporter(new FileReport(outputDir,
                "pagelets.txt")));
        reporters.add(new OuterLinkCollectingReporter(new FileReport(outputDir,
                "browsable-links.txt"), helper));

        reporters.add(new PageCollectingReporter(new File(outputDir, "pages")));

        reporters.add(new PageletTimingsStatisticsReporter(new FileReport(
                outputDir, "pagelet-stats.xls")));

        reporters.add(new PageCriteriaReporter(new FileReport(outputDir,
                "pagecriteria.txt")));
        reporters.add(new PageRenderTimeReporter(new FileReport(outputDir,
                "pagelet-timings.txt")));

        reporters.add(new RootElementReporter(new FileReport(outputDir,
                "root-elements.txt")));
        reporters.add(new NumberOfInnerPageletsReporter(new FileReport(
                outputDir, "num-inner-pagelets.txt"), 12));
        reporters.add(new Non200ResponseCodeReporter(new FileReport(outputDir,
                "non-200-repsonse.txt")));
        reporters.add(new InnerPageletReporter(new FileReport(outputDir,
                "inner-pagelets.txt")));

        reporters.add(new InnerLinkReporter(new FileReport(outputDir,
                "inner-links.txt")));

        reporters.add(new NestingReporter(new FileReport(outputDir,
                "nesting.txt"), 10));
        reporters.add(new PageletOnlyReporter(new FileReport(outputDir,
                "pagelet-only.txt")));
        reporters.add(new NotCachedReporter(new FileReport(outputDir,
                "not-cached.txt")));
        reporters.add(new DefaultArgumentsAsPageCriteriaReporter(
                new FileReport(outputDir,
                        "defaultArgumentsAsPageCriteriaReporter.txt")));
        return reporters;
    }

}
