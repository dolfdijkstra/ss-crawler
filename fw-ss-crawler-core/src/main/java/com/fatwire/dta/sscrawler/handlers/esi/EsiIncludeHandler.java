package com.fatwire.dta.sscrawler.handlers.esi;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fatwire.dta.sscrawler.Pagelet;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.handlers.AbstractBodyHandler;
import com.fatwire.dta.sscrawler.util.SSUriHelper;

public class EsiIncludeHandler extends AbstractBodyHandler {

    private static final Pattern tagPattern = Pattern.compile(" .*?=\".*?\"");
    
    private static final Pattern pattern = Pattern.compile("(<esi:include"
            + ")(\\s(\\w*=\".*?\")?)*(/" + "/>)");

    public EsiIncludeHandler(final SSUriHelper uriHelper) {
        super(uriHelper);
    }

    protected Pattern getTagPattern() {
        return tagPattern;
    }

    protected Pattern getPagePattern() {
        return pattern;
    }

    public final void visit(ResultPage page) {
        final Matcher m = getPagePattern().matcher(page.getBody());
        //<esi:include src=“object” attr1="val1" attr2="val2" etc./>
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
