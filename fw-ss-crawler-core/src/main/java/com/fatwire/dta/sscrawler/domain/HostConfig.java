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

import java.net.URI;

import org.apache.http.HttpHost;
import org.apache.http.auth.Credentials;

/**
 * Describing the host we connect to
 * 
 * @author Dolf.Dijkstra
 * @since Nov 1, 2007
 */

public class HostConfig {

    private long id;

    private String domain;

    private Credentials proxyCredentials;
    private HttpHost proxyHost;

    private HttpHost target;

    public HostConfig(URI uri) {
        target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        domain = uri.getHost();

    }

    /**
     * @return the domain
     */
    public String getDomain() {
        return domain;
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return target.getHostName() + ":" + target.getPort();
    }

    public void setProxyCredentials(Credentials credentials) {
        this.proxyCredentials = credentials;
    }

    public Credentials getProxyCredentials() {
        return proxyCredentials;
    }

    /**
     * @return the proxyHost
     */
    public HttpHost getProxyHost() {
        return proxyHost;
    }

    /**
     * @param proxyHost the proxyHost to set
     */
    public void setProxyHost(HttpHost proxyHost) {
        this.proxyHost = proxyHost;
    }

    public HttpHost getTargetHost() {

        return target;
    }

}
