/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fatwire.dta.sscrawler.domain;

public class PageletPerformance {
    
    private long id;
    
    private CrawlSession session;
    
    private long downloadTime;

    /**
     * @return the downloadTime
     */
    public long getDownloadTime() {
        return downloadTime;
    }

    /**
     * @param downloadTime the downloadTime to set
     */
    public void setDownloadTime(long downloadTime) {
        this.downloadTime = downloadTime;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the session
     */
    public CrawlSession getSession() {
        return session;
    }

    /**
     * @param session the session to set
     */
    public void setSession(CrawlSession session) {
        this.session = session;
    }

}
