package com.fatwire.dta.sscrawler.handlers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fatwire.dta.sscrawler.Pagelet;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.util.SSUriHelper;

public abstract class AbstractBodyMarkerHandler extends AbstractBodyHandler {

    private static final Pattern tagPattern = Pattern.compile(" .*?=\".*?\"");

    protected Pattern getTagPattern() {
        return tagPattern;
    }

    protected abstract Pattern getPagePattern();

    public AbstractBodyMarkerHandler(final SSUriHelper uriHelper) {
        super(uriHelper);
    }

    public final void visit(ResultPage page) {
        final Matcher m = getPagePattern().matcher(page.getBody());
        //"<com.fatwire.satellite.page pagename="FirstSiteII/FSIILayout" cid="1118867611403" locale="1154551493541" rendermode="live" p="1118867611403" c="Page" /com.fatwire.satellite.page>
        while (m.find()) {
            if (log.isDebugEnabled()) {
                log.debug(m.group());
            }
            doTag(m.group(), page);
        }

    }

    private void doTag(final String tag, ResultPage page) {
        final Matcher m = getTagPattern().matcher(tag);
        final Pagelet map = new Pagelet();
        while (m.find()) {
            if (log.isTraceEnabled()) {
                log.trace(m.group());
            }
            final String x = m.group();
            final int t = x.indexOf('=');
            final String key = x.substring(0, t).trim();
            if (filter(key)) {
                String value = x.substring(t + 2, x.length() - 1);
                try {
                    String v = URLDecoder.decode(value, "UTF-8");
                    map.addParameter(key, v);
                } catch (UnsupportedEncodingException e) {
                    log.warn("can not urldecode '" + value + "'", e);
                }

            }

        }
        page.addMarker(map);

    }

    protected boolean filter(String key) {
        return !"cachecontrol".equalsIgnoreCase(key);
    }

}