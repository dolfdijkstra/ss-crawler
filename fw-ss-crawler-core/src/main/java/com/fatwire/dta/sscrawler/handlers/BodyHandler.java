package com.fatwire.dta.sscrawler.handlers;

import java.util.ArrayList;
import java.util.List;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.util.SSUriHelper;

public class BodyHandler implements Visitor<ResultPage> {

    private final List<Visitor<ResultPage>> visitors = new ArrayList<Visitor<ResultPage>>();

    /**
     * @param page
     */
    public BodyHandler(final SSUriHelper uriHelper) {
        super();
        visitors.add(new BodyMarkerHandler(uriHelper));
        visitors.add(new ShortBodyMarkerHandler(uriHelper));
        visitors.add(new BodyLinkHandler(uriHelper));
        visitors.add(new BodyRawLinkHandler(uriHelper));
        visitors.add(new SSUnqualifiedBodyLinkHandler(uriHelper));

    }

    public void visit(ResultPage page) {
        if (page.getResponseCode() != 200)
            return; //bail out
        for (Visitor<ResultPage> visitor : visitors) {
            visitor.visit(page);
        }

    }

}
