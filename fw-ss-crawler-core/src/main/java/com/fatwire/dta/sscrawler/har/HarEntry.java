package com.fatwire.dta.sscrawler.har;

import java.net.URI;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.lang.StringUtils;

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
