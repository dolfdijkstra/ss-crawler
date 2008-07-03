package com.fatwire.dta.sscrawler.jobs;

import com.fatwire.dta.sscrawler.events.EventObject;

public class JobStartedEvent extends EventObject<Job> {

    public JobStartedEvent(Job source) {
        super(source);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -4482542445018440882L;

}
