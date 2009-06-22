package com.fatwire.dta.sscrawler;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;

import com.fatwire.dta.sscrawler.jobs.NullProgressMonitor;
import com.fatwire.dta.sscrawler.reporting.Reporter;
import com.fatwire.dta.sscrawler.reporting.reporters.PageletTimingsStatisticsReporter;
import com.fatwire.dta.sscrawler.reporting.reports.StdOutReport;
import com.fatwire.dta.sscrawler.util.SSUriHelper;
import com.fatwire.dta.sscrawler.util.UriHelperFactory;


public class CacheWarmer {
    public static void main(final String[] args) throws Exception {

        if (args.length < 1) {
            throw new IllegalArgumentException(
                    "Usage: java "
                            + CacheWarmer.class.getName()
                            + " -startUri \"http://localhost:8080/cs/ContentServer?pagename=...\" -reportDir <report dir> -max <max pages> -threads <number of worker threads>");
        }
        DOMConfigurator.configure("conf/log4j.xml");
        Crawler crawler = new Crawler();
        //File path = null;
        String factory = null;
        URI startUri = null;
        int threads=5;
        
        for (int i = 0; i < args.length; i++) {
            if ("-startUri".equals(args[i])) {
                startUri = URI.create(args[++i]);
            } else if ("-reportDir".equals(args[i])) {
                //path = new File(args[++i]);
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
        if (t ==-1){
            throw new IllegalArgumentException("/ContentServer is not found on the startUri.");
        }
        crawler.setHost(startUri.toASCIIString().substring(0, t));
        crawler.setStartUri(new URI(null, null, null, -1,
                startUri.getRawPath(), startUri.getRawQuery(), startUri
                        .getFragment()));

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmm");
        //path = new File(path, df.format(new Date()));
        //path.mkdirs();
        SSUriHelper helper = null;

        if (factory != null) {
            UriHelperFactory f = (UriHelperFactory) (Class.forName(factory)
                    .newInstance());
            helper = f.create(crawler.getStartUri().getPath());
        } else {
            helper = new SSUriHelper(crawler.getStartUri().getPath());
        }

        final RenderingThreadPool readerPool = new RenderingThreadPool(threads);
        MBeanServer platform = java.lang.management.ManagementFactory.getPlatformMBeanServer();
        try {
            platform.registerMBean(readerPool, new ObjectName("com.fatwire.monitoring:name=readerpool"));
        } catch (Throwable x) {
            LogFactory.getLog(CacheWarmer.class).error(x.getMessage(), x);
       }

        crawler.setExecutor(readerPool);
        List<Reporter> reporters = new ArrayList<Reporter>();
        reporters.add(new PageletTimingsStatisticsReporter(new StdOutReport()));

        crawler.setReporters(reporters);
        crawler.setUriHelper(helper);
        crawler.setProgressMonitor(new NullProgressMonitor());
        crawler.work();
        readerPool.shutdown();
        try {
            platform.unregisterMBean(new ObjectName("com.fatwire.monitoring:name=readerpool"));
        } catch (Throwable x) {
            LogFactory.getLog(CacheWarmer.class).error(x.getMessage(), x);
       }

    }

}
