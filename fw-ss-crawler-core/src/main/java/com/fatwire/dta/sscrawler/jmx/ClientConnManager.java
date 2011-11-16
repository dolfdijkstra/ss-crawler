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

package com.fatwire.dta.sscrawler.jmx;

import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

public class ClientConnManager implements ClientConnManagerMXBean {
    private final ThreadSafeClientConnManager connectionManager;

    public ClientConnManager(ThreadSafeClientConnManager connectionManager) {
        super();
        this.connectionManager = connectionManager;
    }

  
    public ConnManagerMetrics getConnManagerMetrics() {
        return new ConnManagerMetrics(connectionManager.getConnectionsInPool(), connectionManager.getMaxTotal(),connectionManager.getDefaultMaxPerRoute());
    }
    
    

    /* (non-Javadoc)
     * @see com.fatwire.dta.sscrawler.jmx.ClientConnManagerMXBean#getMaxTotal()
     */
    public int getMaxTotal() {
        return connectionManager.getMaxTotal();
    }

    /* (non-Javadoc)
     * @see com.fatwire.dta.sscrawler.jmx.ClientConnManagerMXBean#getDefaultMaxPerRoute()
     */
    public int getDefaultMaxPerRoute() {
        return connectionManager.getDefaultMaxPerRoute();
    }


    /**
     * @return
     * @see org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager#getConnectionsInPool()
     */
    public int getConnectionsInPool() {
        return connectionManager.getConnectionsInPool();
    }
}
