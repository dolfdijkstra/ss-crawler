package com.fatwire.dta.sscrawler;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;

import com.fatwire.dta.sscrawler.URLReaderService.Scheduler;

public class ReaderService implements ReaderServiceMBean {

    private final Scheduler service;
    private final MultiThreadedHttpConnectionManager connectionManager;
    
    /**
     * @param service
     * @param connectionManager
     */
    public ReaderService(Scheduler service,
            MultiThreadedHttpConnectionManager connectionManager) {
        super();
        this.service = service;
        this.connectionManager = connectionManager;
    }


    public int getCount() {
        return service.getCount();
    }


    public int getConnectionsInPool() {
        return this.connectionManager.getConnectionsInPool();
    }


    public int getScheduledCount() {
        return this.service.getScheduledCount();
    }


    public int getCompleteCount() {
        return service.getCompleteCount();
    }

    
}
