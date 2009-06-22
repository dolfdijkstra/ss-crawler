package com.fatwire.dta.sscrawler;

public interface RenderingThreadPoolMBean {

    /**
     * @return
     * @see java.util.concurrent.ThreadPoolExecutor#getActiveCount()
     */
    public int getActiveCount();

    /**
     * @return
     * @see java.util.concurrent.ThreadPoolExecutor#getCompletedTaskCount()
     */
    public long getCompletedTaskCount();

    /**
     * @return
     * @see java.util.concurrent.ThreadPoolExecutor#getCorePoolSize()
     */
    public int getCorePoolSize();

    /**
     * @return
     * @see java.util.concurrent.ThreadPoolExecutor#getLargestPoolSize()
     */
    public int getLargestPoolSize();

    /**
     * @return
     * @see java.util.concurrent.ThreadPoolExecutor#getMaximumPoolSize()
     */
    public int getMaximumPoolSize();

    /**
     * @return
     * @see java.util.concurrent.ThreadPoolExecutor#getPoolSize()
     */
    public int getPoolSize();

    /**
     * @return
     * @see java.util.concurrent.ThreadPoolExecutor#getTaskCount()
     */
    public long getTaskCount();

}
