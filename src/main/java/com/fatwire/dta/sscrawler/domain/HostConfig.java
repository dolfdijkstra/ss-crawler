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

import com.fatwire.dta.sscrawler.EasySSLProtocolSocketFactory;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.protocol.Protocol;

/**
 * Describing the host we connect to
 * 
 * @author Dolf.Dijkstra
 * @since Nov 1, 2007
 */

public class HostConfig {

    private long id;

    private String hostname;

    private int port;

    private String domain;

    private Protocol protocol;
    private Credentials proxyCredentials;
    private ProxyHost proxyHost;

    /**
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @param hostname the hostname to set
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
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
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return hostname + ":" + port + domain;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * @param protocol the protocol to set
     */
    @SuppressWarnings("deprecation")
    public void setProtocol(String protocol) {
        if (protocol.equalsIgnoreCase("https")) {
            this.protocol = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
        } else {
            this.protocol = Protocol.getProtocol(protocol);
        }
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
    public ProxyHost getProxyHost() {
        return proxyHost;
    }

    /**
     * @param proxyHost the proxyHost to set
     */
    public void setProxyHost(ProxyHost proxyHost) {
        this.proxyHost = proxyHost;
    }

}
