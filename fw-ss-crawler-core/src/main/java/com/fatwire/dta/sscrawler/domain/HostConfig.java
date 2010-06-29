package com.fatwire.dta.sscrawler.domain;

import com.fatwire.dta.sscrawler.EasySSLProtocolSocketFactory;

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
    public void setProtocol(String protocol) {
        if (protocol.equalsIgnoreCase("https")) {
            this.protocol = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
        } else {
            this.protocol = Protocol.getProtocol(protocol);
        }
    }

}
