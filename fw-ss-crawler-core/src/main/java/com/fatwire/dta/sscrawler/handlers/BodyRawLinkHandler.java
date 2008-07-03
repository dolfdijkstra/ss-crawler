package com.fatwire.dta.sscrawler.handlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.util.SSUriHelper;

public class BodyRawLinkHandler extends AbstractBodyHandler {

    private final Pattern rawLinkPattern = Pattern
            .compile("Satellite\\?.*?[\"']");

    public BodyRawLinkHandler(final SSUriHelper uriHelper) {
        super(uriHelper);

    }

    public void visit(ResultPage page) {

        final Matcher m = rawLinkPattern.matcher(page.getBody());

        while (m.find()) {
            log.warn(page.getUri() + " has a raw link: " + m.group());
            //todo add as a link
        }

    }

}
