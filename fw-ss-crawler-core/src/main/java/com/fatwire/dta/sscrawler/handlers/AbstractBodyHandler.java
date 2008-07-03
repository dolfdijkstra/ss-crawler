package com.fatwire.dta.sscrawler.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.util.SSUriHelper;

public abstract class AbstractBodyHandler implements Visitor<ResultPage> {
    protected final Log log = LogFactory.getLog(getClass());

    final protected SSUriHelper uriHelper;

    /**
     * 
     * @param uriHelper
     */
    public AbstractBodyHandler(final SSUriHelper uriHelper) {
        super();
        this.uriHelper = uriHelper;
    }

}
