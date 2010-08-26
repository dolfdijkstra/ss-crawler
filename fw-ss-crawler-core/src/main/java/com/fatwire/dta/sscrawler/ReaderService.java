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
    public ReaderService(final Scheduler service, final MultiThreadedHttpConnectionManager connectionManager) {
        super();
        this.service = service;
        this.connectionManager = connectionManager;
    }

    public int getCount() {
        return service.getCount();
    }

    public int getConnectionsInPool() {
        return connectionManager.getConnectionsInPool();
    }

    public int getScheduledCount() {
        return service.getScheduledCount();
    }

    public int getCompleteCount() {
        return service.getCompleteCount();
    }

}
