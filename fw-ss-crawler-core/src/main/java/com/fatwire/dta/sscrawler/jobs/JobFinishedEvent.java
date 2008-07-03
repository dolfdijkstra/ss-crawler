package com.fatwire.dta.sscrawler.jobs;

import com.fatwire.dta.sscrawler.events.EventObject;



public class JobFinishedEvent extends EventObject<Job> {

    public JobFinishedEvent(Job source) {
        super(source);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 5574306919853993009L;

}
