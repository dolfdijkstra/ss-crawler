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

package com.fatwire.dta.sscrawler.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.dta.sscrawler.Link;
import com.fatwire.dta.sscrawler.Pagelet;
import com.fatwire.dta.sscrawler.QueryString;
import com.fatwire.dta.sscrawler.handlers.BodyLinkHandler;

public class UriUtil {
    private static final Log log = LogFactory.getLog(BodyLinkHandler.class);

    private static final String PAGENAME_KEY = "pagename=";

    private static final String UTF8 = "utf-8";

    public static String extractPageName(final String uri) {
        final String qs = URI.create(uri).getRawQuery();
        if (qs != null) {
            for (final String p : qs.split("&")) {
                if (p.startsWith(UriUtil.PAGENAME_KEY)) {
                    try {
                        return URLDecoder.decode(p.substring(UriUtil.PAGENAME_KEY.length()), UriUtil.UTF8);
                    } catch (final UnsupportedEncodingException e) {
                        UriUtil.log.error(e, e);
                    }
                }
            }

        }
        return null;

    }

    public static Map<String, String> extractParams(final String uri) {
        final String qs = URI.create(uri).getRawQuery();
        final Map<String, String> map = new TreeMap<String, String>();
        if (qs != null) {
            for (final String p : qs.split("&")) {
                final String[] nvp = p.split("=");
                try {
                    final String key = URLDecoder.decode(nvp[0], UriUtil.UTF8);
                    if (nvp.length > 0) {
                        map.put(key, URLDecoder.decode(nvp[1], UriUtil.UTF8));

                    } else {
                        map.put(key, null);
                    }
                } catch (final UnsupportedEncodingException e) {
                    UriUtil.log.error(e, e);
                }
            }

        }
        return map;
    }

    public static QueryString checkForFtss(SSUriHelper uriHelper, QueryString ssuri, boolean requestPageData) {
        final QueryString uri = ssuri instanceof Pagelet? new Pagelet((Pagelet) ssuri): new Link((Link) ssuri);
        if (requestPageData) {
            if (ssuri.has(HelperStrings.SS_PAGEDATA_REQUEST) == false) {
                uri.addParameter(HelperStrings.SS_PAGEDATA_REQUEST, Boolean.TRUE.toString());
            }
        } else {
            if (ssuri.has(HelperStrings.SS_CLIENT_INDICATOR) == false) {
                uri.addParameter(HelperStrings.SS_CLIENT_INDICATOR, Boolean.TRUE.toString());
            }

        }
        return uri;
    }
    
    public static URI normalize(URI uri){
        return uri.normalize();
    }
}
