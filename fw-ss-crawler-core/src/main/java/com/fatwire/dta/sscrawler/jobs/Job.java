package com.fatwire.dta.sscrawler.jobs;

public interface Job {

    void schedule();

    void run(ProgressMonitor monitor);
    
    
}
