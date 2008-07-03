package com.fatwire.dta.sscrawler.events;

import com.fatwire.dta.sscrawler.ResultPage;

public class PageletRenderedEvent extends EventObject<ResultPage> {

    public PageletRenderedEvent(ResultPage page) {
        super(page);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 87405192630120962L;

    /**
     * @return the page
     */
    public ResultPage getPage() {
        return getSource();
    }

}
