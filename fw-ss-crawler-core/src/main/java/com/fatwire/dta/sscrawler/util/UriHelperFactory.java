package com.fatwire.dta.sscrawler.util;

public class UriHelperFactory {

    public SSUriHelper create(String path) {
        return new SSUriHelper(path);
    }

}
