package com.fatwire.dta.sscrawler.handlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fatwire.dta.sscrawler.Link;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.util.SSUriHelper;

public class BodyLinkHandler extends AbstractBodyHandler {

    private final Pattern linkPattern = Pattern
            .compile("satellitescheme://SSURI/.*?#satellitefragment");

    /**
     * @param body
     * @param uriHelper
     */
    public BodyLinkHandler(final SSUriHelper uriHelper) {
        super(uriHelper);
    }

    public void visit(ResultPage page) {
        final Matcher m = linkPattern.matcher(page.getBody());
        while (m.find()) {
            log.debug(m.group());
            Link map = uriHelper.linkToMap(m.group());
            if (map.isOK()) {
                page.addLink(map);
            }
        }
    }

}
