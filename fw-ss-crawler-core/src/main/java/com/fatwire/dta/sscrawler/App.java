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

import com.fatwire.dta.sscrawler.jobs.NullProgressMonitor;
import com.fatwire.dta.sscrawler.util.UriHelperFactory;

public class App {

    private static final String THREADPOOL_MBEAN = "com.fatwire.crawler:name=threadpool";

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

                new App().doWork(new CrawlerConfigurator(s));
            } else if (s.getArgList().contains("warmer") && s.getArgList().size() > 1) {
                new App().doWork(new CacheWarmerConfigurator(s));
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

    protected void doWork(Configurator configurator) throws Exception {
        // if (cmd.getArgs()[1] == null) {
        // throw new IllegalArgumentException("startUri is not set");
        // }
        //

        final ThreadPoolExecutor readerPool = configurator.createThreadPool();
        final MBeanServer platform = java.lang.management.ManagementFactory.getPlatformMBeanServer();
        try {
            platform.registerMBean(readerPool, new ObjectName(THREADPOOL_MBEAN));
        } catch (final Throwable x) {
            LogFactory.getLog(App.class).error(x.getMessage(), x);
        }

        final CrawlerService reader = configurator.createCrawlerService(readerPool);
        final Crawler crawler = configurator.createCrawler(reader);

        try {
            crawler.execute(new NullProgressMonitor());

        } finally {
            configurator.shutdown();
            readerPool.shutdown();
            try {
                platform.unregisterMBean(new ObjectName(THREADPOOL_MBEAN));
            } catch (final Throwable x) {
                LogFactory.getLog(getClass()).error(x.getMessage(), x);
            }
        }
    }

}
