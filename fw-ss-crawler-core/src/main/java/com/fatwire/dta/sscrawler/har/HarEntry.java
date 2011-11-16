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

package com.fatwire.dta.sscrawler.har;

import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.StatusLine;

import com.fatwire.dta.sscrawler.ResultPage;

public class HarEntry {

    private final ResultPage delegate;
    private final URI uri;
    private final String pageRef;

    public HarEntry(URI uri, String pageRef, ResultPage page) {
        super();
        if (uri == null)
            throw new IllegalArgumentException("uri cannot be null.");
        if (page == null)
            throw new IllegalArgumentException("page cannot be null.");
        if (StringUtils.isBlank(uri.getHost()))
            throw new IllegalArgumentException("host cannot be blank: " + uri.toASCIIString());
        if (StringUtils.isBlank(pageRef))
            throw new IllegalArgumentException("pageRef cannot be blank.");
        this.uri = uri;
        this.delegate = page;
        this.pageRef = pageRef;
    }

    /**
     * @return
     * @see com.fatwire.dta.sscrawler.ResultPage#getBody()
     */
    public String getBody() {
        return delegate.getBody();
    }

    /**
     * @return
     * @see com.fatwire.dta.sscrawler.ResultPage#getReadTime()
     */
    public long getReadTime() {
        return delegate.getReadTime();
    }

    /**
     * @return
     * @see com.fatwire.dta.sscrawler.ResultPage#getResponseHeaders()
     */
    public Header[] getResponseHeaders() {
        return delegate.getResponseHeaders();
    }

    /**
     * @return
     * @see com.fatwire.dta.sscrawler.ResultPage#getPageName()
     */
    public String getPageName() {
        return delegate.getPageName();
    }

    /**
     * @return
     * @see com.fatwire.dta.sscrawler.ResultPage#getTimeToFirstByte()
     */
    public long getTimeToFirstByte() {
        return delegate.getTimeToFirstByte();
    }

    /**
     * @return
     * @see com.fatwire.dta.sscrawler.ResultPage#getStartTime()
     */
    public long getStartTime() {
        return delegate.getStartTime();
    }

    /**
     * @return
     * @see com.fatwire.dta.sscrawler.ResultPage#getRequestHeaders()
     */
    public Header[] getRequestHeaders() {
        return delegate.getRequestHeaders();
    }

    /**
     * @return
     * @see com.fatwire.dta.sscrawler.ResultPage#getStatusLine()
     */
    public StatusLine getStatusLine() {
        return delegate.getStatusLine();
    }

    /**
     * @return
     * @see com.fatwire.dta.sscrawler.ResultPage#getPageLength()
     */
    public int getPageLength() {
        return delegate.getPageLength();
    }

    /**
     * @return the uri
     */
    public URI getUri() {
        return uri;
    }

    public String getPageRef() {

        return pageRef;
    }

    /**
     * @return
     * @see com.fatwire.dta.sscrawler.ResultPage#getConnectTime()
     */
    public long getConnectTime() {
        return delegate.getConnectTime();
    }

}
