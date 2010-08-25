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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.cli.AlreadySelectedException;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;

import com.fatwire.dta.sscrawler.domain.HostConfig;
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

    @SuppressWarnings("static-access")
    public static Options setUpCmd() {
        // create Options object
        Options options = new Options();

        // add t option
        options.addOption("h", "help", false, "print this message.");
        /*
         * Option startUri =
         * OptionBuilder.withArgName("uri").hasArg().withDescription(
         * "Starting uri in the form of 'http://localhost:8080/cs/ContentServer?pagename=...'"
         * ).withLongOpt("startUri").create("u"); startUri.setRequired(true);
         * 
         * options.addOption(startUri);
         */
        Option reportDir = OptionBuilder.withArgName("dir").hasArg().withDescription(
                "Directory where reports are stored").withLongOpt("reportDir").create("d");
        options.addOption(reportDir);

        Option max = OptionBuilder.withArgName("num").hasArg().withDescription(
                "Maximum number of pages, default is unlimited").withLongOpt("max").create("m");
        options.addOption(max);

        Option uriHelperFactory = OptionBuilder.withArgName("classname").hasArg().withDescription(
                "Class for constructing urls").withLongOpt("uriHelperFactory").create("f");
        uriHelperFactory.setType(UriHelperFactory.class);
        options.addOption(uriHelperFactory);

        Option threads = OptionBuilder.withArgName("num").hasArg().withDescription(
                "Number of concurrent threads that are reading from ContentServer").withLongOpt("threads").create("t");
        options.addOption(threads);

        Option proxyUsername = OptionBuilder.withArgName("username").hasArg().withDescription("Proxy Username")
                .withLongOpt("proxyUsername").create("pu");
        options.addOption(proxyUsername);

        Option proxyPassword = OptionBuilder.withArgName("password").hasArg().withDescription("Proxy Password")
                .withLongOpt("proxyPassword").create("pw");
        options.addOption(proxyPassword);

        Option proxyHost = OptionBuilder.withArgName("host").hasArg().withDescription("Proxy hostname").withLongOpt(
                "proxyHost").create("ph");
        options.addOption(proxyHost);

        Option proxyPort = OptionBuilder.withArgName("port").hasArg().withDescription("Proxy port number").withLongOpt(
                "proxyPort").create("pp");
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
        Options o = App.setUpCmd();
        CommandLineParser p = new BasicParser();
        // BasicParser, GnuParser, Parser, PosixParser
        try {
            CommandLine s = p.parse(o, args);
            System.out.println(s.hasOption("h"));
            System.out.println(s.hasOption("pu"));
            System.out.println(s.getOptionValue("pu"));
            System.out.println(s.getArgList());
            String cmd = "crawler";
            int startpos = 0;
            if (!args[0].startsWith("-")) {
                startpos = 1;
                cmd = args[0];

            }
            if (s.getArgList().contains("crawler")) {
                new App().doWork(s);
            } else if (s.getArgList().contains("warmer")) {
                new CacheWarmer().doWork(s);
            } else {
                System.err.println("no subcommand found");
                printUsage();
                System.exit(1);
            }

        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printUsage();
            System.exit(1);
        }

    }

    public static void printUsage() {
        Options options = App.setUpCmd();
        HelpFormatter formatter = new HelpFormatter();
        // formatter.setLongOptPrefix("--");
        // formatter.setOptPrefix("#");
        formatter
                .printHelp(
                        "java " + App.class.getName() + " <subcommand> [options] [argument]",
                        "Tool to retrieve page from ContentServer as-if SatelliteServer is rendering them.\n"
                                + "Argument a the start uri in the form of 'http://localhost:8080/cs/ContentServer?pagename=...'.\n"
                                + "Available subcommands:\n    crawler: extensive reporting on the discovered pagelets.\n    warmer:  warm the cache.\n\n",
                        options, "For more into see http://www.nl.fatwire.com/dta/ss-crawler/", true);

    }

    private HostConfig createHostConfig(final URI uri) {
        final HostConfig hostConfig = new HostConfig();

        hostConfig.setHostname(uri.getHost());

        hostConfig.setPort(uri.getPort() == -1 ? 80 : uri.getPort());
        hostConfig.setDomain(uri.getPath());
        hostConfig.setProtocol(uri.getScheme());

        return hostConfig;

    }

    protected void doWork(CommandLine cmd) throws Exception {
        Crawler crawler = new Crawler();

        URI startUri = null;

        startUri = URI.create(cmd.getArgs()[1]);
        if (cmd.hasOption('m')) {
            crawler.setMaxPages(Integer.parseInt(cmd.getOptionValue('m')));
        }

        int threads = Integer.parseInt(cmd.getOptionValue('t', "5"));

        if (startUri == null)
            throw new IllegalArgumentException("startUri is not set");
        int t = startUri.toASCIIString().indexOf("/ContentServer");
        if (t == -1) {
            throw new IllegalArgumentException("/ContentServer is not found on the startUri.");
        }

        crawler.setStartUri(new URI(null, null, null, -1, startUri.getRawPath(), startUri.getRawQuery(), startUri
                .getFragment()));
        final HostConfig hc = createHostConfig(URI.create(startUri.toASCIIString().substring(0, t)));

        String proxyUsername = cmd.getOptionValue("pu");
        String proxyPassword = cmd.getOptionValue("pw");
        String proxyHost = cmd.getOptionValue("ph");
        int proxyPort = Integer.parseInt(cmd.getOptionValue("", "8080"));
        
        
        if (StringUtils.isNotBlank(proxyUsername) && StringUtils.isNotBlank(proxyUsername)) {
            hc.setProxyCredentials(new UsernamePasswordCredentials(proxyUsername, proxyPassword));
        }

        if (StringUtils.isNotBlank(proxyHost)) {
            hc.setProxyHost(new ProxyHost(proxyHost, proxyPort));
        } else if (StringUtils.isNotBlank(System.getProperty("http.proxyhost"))
                && StringUtils.isNotBlank(System.getProperty("http.proxyport"))) {
            hc.setProxyHost(new ProxyHost(System.getProperty("http.proxyhost"), Integer.parseInt(System
                    .getProperty("http.proxyport"))));

        }
        crawler.setHostConfig(hc);

        SSUriHelper helper = null;

        if (cmd.hasOption('f')) {
            UriHelperFactory f = (UriHelperFactory) (Class.forName(cmd.getOptionValue('f')).newInstance());
            helper = f.create(crawler.getStartUri().getPath());
        } else {
            helper = new SSUriHelper(crawler.getStartUri().getPath());
        }
        final ThreadPoolExecutor readerPool = new RenderingThreadPool(threads);
        MBeanServer platform = java.lang.management.ManagementFactory.getPlatformMBeanServer();
        try {
            platform.registerMBean(readerPool, new ObjectName("com.fatwire.crawler:name=readerpool"));
        } catch (Throwable x) {
            LogFactory.getLog(App.class).error(x.getMessage(), x);
        }

        crawler.setExecutor(readerPool);
        File path = null;
        if (cmd.hasOption('d')) {
            path = new File(cmd.getOptionValue("d"));
        } else {
            path = getOutputDir();
        }
        if (path != null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmm");
            path = new File(path, df.format(new Date()));
            path.mkdirs();
        }
        crawler.setReporters(createReporters(path, helper));
        crawler.setUriHelper(helper);
        crawler.work();
        readerPool.shutdown();
        try {
            platform.unregisterMBean(new ObjectName("com.fatwire.crawler:name=readerpool"));
        } catch (Throwable x) {
            LogFactory.getLog(App.class).error(x.getMessage(), x);
        }
    }

    protected File getOutputDir() {

        final File outputDir = new File("./reports");
        outputDir.mkdirs();
        return outputDir;
    }

    protected List<Reporter> createReporters(File outputDir, SSUriHelper helper) {

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
