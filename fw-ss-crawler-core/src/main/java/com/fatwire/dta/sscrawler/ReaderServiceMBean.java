package com.fatwire.dta.sscrawler;

public interface ReaderServiceMBean {

    int getCount();
    int getScheduledCount();
    int getCompleteCount();
    int getConnectionsInPool();
    
}
