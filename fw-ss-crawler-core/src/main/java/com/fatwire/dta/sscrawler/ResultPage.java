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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.StatusLine;

import com.fatwire.dta.sscrawler.util.HelperStrings;

public class ResultPage {

    
    private long startTime;
    private final QueryString uri;

    private String body;

    private Header[] responseHeaders;

    private final List<QueryString> links;

    private final List<QueryString> markers;

    private long readTime = -1;
    private long timeToFirstByte = -1;

    private final String pageName;

    private int responseCode = -1;
    private Header[] requestHeaders;
    private StatusLine statusLine;
    private int pageLength;
    private long connectTime=-1;

    /**
     * @param uri
     */
    public ResultPage(final QueryString uri) {
        super();
        this.uri = uri;
        pageName = uri.get(HelperStrings.PAGENAME);
        links = new ArrayList<QueryString>();
        markers = new ArrayList<QueryString>();
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(final String body) {
        this.body = body;
    }

    /**
     * @return the links
     */
    public List<QueryString> getLinks() {
        return links;
    }

    /**
     * @return the uri
     */
    public QueryString getUri() {
        return uri;
    }

    public void addLink(final Link uri) {
        links.add(uri);

    }

    public void addLinks(final Collection<Link> uris) {
        links.addAll(uris);

    }

    public void addMarker(final Pagelet uri) {
        markers.add(uri);

    }

    public void addMarkers(final Collection<Pagelet> uris) {
        markers.addAll(uris);

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (uri == null ? 0 : uri.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResultPage other = (ResultPage) obj;
        if (uri == null) {
            if (other.uri != null) {
                return false;
            }
        } else if (!uri.equals(other.uri)) {
            return false;
        }
        return true;
    }

    /**
     * @return the markers
     */
    public List<QueryString> getMarkers() {
        return markers;
    }

    /**
     * @return the readTime
     */
    public long getReadTime() {
        return readTime;
    }

    /**
     * @param readTime the readTime to set
     */
    public void setReadTime(final long readTime) {
        this.readTime = readTime;
    }

    /**
     * @return the responseHeaders
     */
    public Header[] getResponseHeaders() {
        return responseHeaders != null ? responseHeaders : new Header[0];
    }

    /**
     * @param responseHeaders the responseHeaders to set
     */
    public void setResponseHeaders(final Header[] responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    /**
     * @return the pageName
     */
    public String getPageName() {
        return pageName;
    }

    /**
     * @return the responseCode
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * @param responseCode the responseCode to set
     */
    public void setResponseCode(final int responseCode) {
        this.responseCode = responseCode;
    }

    public void setTimeToFirstByte(long l) {
        timeToFirstByte=l;
        
    }

    /**
     * @return the timeToFirstByte
     */
    public long getTimeToFirstByte() {
        return timeToFirstByte;
    }

    /**
     * @return the startTime
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setRequestHeaders(Header[] requestHeaders) {
        this.requestHeaders=requestHeaders;
        
    }

    /**
     * @return the requestHeaders
     */
    public Header[] getRequestHeaders() {
        return requestHeaders;
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine=statusLine;
        
    }

    /**
     * @return the statusLine
     */
    public StatusLine getStatusLine() {
        return statusLine;
    }

    public void setPageLength(int length) {
        pageLength=length;
        
    }

    /**
     * @return the pageLength
     */
    public int getPageLength() {
        return pageLength;
    }

    public void setConnectTime(long l) {
        connectTime=l;
        
    }

    /**
     * @return the connectTime
     */
    public long getConnectTime() {
        return connectTime;
    }

}
