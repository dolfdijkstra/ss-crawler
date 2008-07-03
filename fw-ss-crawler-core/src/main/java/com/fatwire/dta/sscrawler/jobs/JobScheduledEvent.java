package com.fatwire.dta.sscrawler.jobs;

import com.fatwire.dta.sscrawler.events.EventObject;

public class JobScheduledEvent extends EventObject<Job> {

    public JobScheduledEvent(Job source) {
        super(source);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 7949941069045531614L;

}
