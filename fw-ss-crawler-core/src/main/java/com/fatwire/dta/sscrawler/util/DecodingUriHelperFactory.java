package com.fatwire.dta.sscrawler.util;

public class DecodingUriHelperFactory extends UriHelperFactory {

    public SSUriHelper create(String path) {
        return new DecodingSSUriHelper(path);
    }

}
