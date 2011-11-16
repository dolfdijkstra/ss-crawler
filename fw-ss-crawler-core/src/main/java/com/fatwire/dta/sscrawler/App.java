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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;

import com.fatwire.dta.sscrawler.domain.HostConfig;
import com.fatwire.dta.sscrawler.handlers.BodyHandler;
import com.fatwire.dta.sscrawler.jobs.NullProgressMonitor;
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
import com.fatwire.dta.sscrawler.util.SSUriHelper;
import com.fatwire.dta.sscrawler.util.UriHelperFactory;

public class App {

    @SuppressWarnings("static-access")
    public static Options setUpCmd() {
        final Options options = new Options();

        options.addOption("h", "help", false, "print this message.");

        final Option reportDir = OptionBuilder.withArgName("dir").hasArg()
                .withDescription("Directory where reports are stored").withLongOpt("reportDir").create("d");
        options.addOption(reportDir);

        final Option max = OptionBuilder.withArgName("num").hasArg()
                .withDescription("Maximum number of pages, default is unlimited").withLongOpt("max").create("m");
        options.addOption(max);

        final Option uriHelperFactory = OptionBuilder.withArgName("classname").hasArg()
                .withDescription("Class for constructing urls").withLongOpt("uriHelperFactory").create("f");
        uriHelperFactory.setType(UriHelperFactory.class);
        options.addOption(uriHelperFactory);

        final Option threads = OptionBuilder.withArgName("num").hasArg()
                .withDescription("Number of concurrent threads that are reading from ContentServer")
                .withLongOpt("threads").create("t");
        options.addOption(threads);

        final Option proxyUsername = OptionBuilder.withArgName("username").hasArg().withDescription("Proxy Username")
                .withLongOpt("proxyUsername").create("pu");
        options.addOption(proxyUsername);

        final Option proxyPassword = OptionBuilder.withArgName("password").hasArg().withDescription("Proxy Password")
                .withLongOpt("proxyPassword").create("pw");
        options.addOption(proxyPassword);

        final Option proxyHost = OptionBuilder.withArgName("host").hasArg().withDescription("Proxy hostname")
                .withLongOpt("proxyHost").create("ph");
        options.addOption(proxyHost);

        final Option proxyPort = OptionBuilder.withArgName("port").hasArg().withDescription("Proxy port number")
                .withLongOpt("proxyPort").create("pp");
        options.addOption(proxyPort);
        return options;

    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {

        if (args.length < 1) {
            printUsage();
            System.exit(1);
        }
        DOMConfigurator.configure("conf/log4j.xml");
        final Options o = App.setUpCmd();
        final CommandLineParser p = new BasicParser();
        try {
            final CommandLine s = p.parse(o, args);
            if (s.hasOption('h')) {
                printUsage();
            } else if (s.getArgList().contains("crawler") && s.getArgList().size() > 1) {
                new App().doWork(s);
            } else if (s.getArgList().contains("warmer") && s.getArgList().size() > 1) {
                new CacheWarmer().doWork(s);
            } else {
                System.err.println("no subcommand and/or URI found on " + s.getArgList());
                printUsage();
                System.exit(1);
            }

        } catch (final ParseException e) {
            System.err.println(e.getMessage());
            printUsage();
            System.exit(1);
        }

    }

    public static void printUsage() {
        final Options options = App.setUpCmd();
        final HelpFormatter formatter = new HelpFormatter();
        formatter
                .printHelp(
                        "java " + App.class.getName() + " <subcommand> [options] [argument]\n",
                        "Tool to retrieve pages from ContentServer as-if SatelliteServer is rendering them.\nVarious reports on the implemented caching strategy are produced."
                                + "Argument: the start uri in the form of 'http://localhost:8080/cs/ContentServer?pagename=...'.\n"
                                + "Available subcommands:\n    crawler: extensive reporting on the discovered pagelets.\n    warmer:  warm the cache.\n\n",
                        options, "For more into see http://www.nl.fatwire.com/dta/ss-crawler/", true);

    }

    protected void doWork(final CommandLine cmd) throws Exception {
        if (cmd.getArgs()[1] == null) {
            throw new IllegalArgumentException("startUri is not set");
        }
        Configurator configurator = new Configurator();
        final HostConfig hc = createHostConfig(cmd, configurator);
        URI startUri = URI.create(cmd.getArgs()[1]);

        SSUriHelper helper = null;

        if (cmd.hasOption('f')) {
            final UriHelperFactory f = (UriHelperFactory) Class.forName(cmd.getOptionValue('f')).newInstance();
            helper = f.create(startUri.getPath());
        } else {
            helper = new SSUriHelper(startUri.getPath());
        }

        final int threads = Integer.parseInt(cmd.getOptionValue('t', "5"));
        final ThreadPoolExecutor readerPool = new RenderingThreadPool(threads);
        final MBeanServer platform = java.lang.management.ManagementFactory.getPlatformMBeanServer();
        try {
            platform.registerMBean(readerPool, new ObjectName("com.fatwire.crawler:name=readerpool"));
        } catch (final Throwable x) {
            LogFactory.getLog(App.class).error(x.getMessage(), x);
        }

        configurator.initConnectionManager(hc);
        DefaultHttpReaderTemplate template = new DefaultHttpReaderTemplate(hc.getTargetHost(), helper,
                configurator.initClient(hc), configurator.createContextHolder(hc.getTargetHost()));

        final URLReaderService reader = new URLReaderService(readerPool, template);

        reader.setHandler(new BodyHandler(helper));

        if (cmd.hasOption('m')) {
            reader.setMaxPages(Integer.parseInt(cmd.getOptionValue('m')));
        }

        final Crawler crawler = new Crawler(reader);

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
        crawler.addReporters(createReporters(path, helper));
        crawler.setStartUri(helper.createLink(startUri));
        try {
            crawler.execute(new NullProgressMonitor());
        } finally {
            configurator.shutdown();
            readerPool.shutdown();
            try {
                platform.unregisterMBean(new ObjectName("com.fatwire.crawler:name=readerpool"));
            } catch (final Throwable x) {
                LogFactory.getLog(App.class).error(x.getMessage(), x);
            }
        }
    }

    /**
     * @param cmd
     * @param startUri
     * @return
     * @throws IllegalArgumentException
     * @throws NumberFormatException
     * @throws URISyntaxException
     */
    protected HostConfig createHostConfig(final CommandLine cmd, Configurator conf) throws IllegalArgumentException,
            NumberFormatException, URISyntaxException {

        URI startUri = URI.create(cmd.getArgs()[1]);

        final int t = startUri.getPath().indexOf("/ContentServer");
        if (t == -1) {
            throw new IllegalArgumentException("/ContentServer is not found on the startUri.");
        }

        final String proxyUsername = cmd.getOptionValue("pu");
        final String proxyPassword = cmd.getOptionValue("pw");
        final String proxyHost = cmd.getOptionValue("ph");
        final int proxyPort = Integer.parseInt(cmd.getOptionValue("", "8080"));

        final HostConfig hc = conf.createHostConfig(startUri, proxyUsername, proxyPassword, proxyHost, proxyPort);
        return hc;
    }

    protected File getOutputDir() {

        final File outputDir = new File("./reports");
        outputDir.mkdirs();
        return outputDir;
    }

    protected List<Reporter> createReporters(final File outputDir, final SSUriHelper helper) {

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

}
