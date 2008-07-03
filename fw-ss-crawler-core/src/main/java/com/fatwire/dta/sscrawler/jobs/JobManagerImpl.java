package com.fatwire.dta.sscrawler.jobs;

public class JobManagerImpl implements JobManager {

    protected void run(Job job) {
        job.run(createMonitor());
    }

    ProgressMonitor createMonitor() {
        return new StdOutProgressMonitor();
    }

}
