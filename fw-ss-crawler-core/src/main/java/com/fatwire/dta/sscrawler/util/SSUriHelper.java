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

import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.dta.sscrawler.Link;
import com.fatwire.dta.sscrawler.QueryString;

public class SSUriHelper {
    protected final Log log = LogFactory.getLog(getClass());

    public static final String SSURI_PREFIX = "SSURI";

    public static final String SSURI_BLOBSERVER = SSURI_PREFIX + "apptype=BlobServer";

    private final String path;

    private static final String UTF8 = "utf-8";

    private final URLCodec urlCodec = new URLCodec();

    /**
     * @param path
     */
    public SSUriHelper(final String path) {
        super();
        this.path = path;
    }

    public String toLink(final QueryString uri) {
        if (uri.getParameters().isEmpty()) {
            return null;
        }
        try {
            final Map<String, String> map = new TreeMap<String, String>(uri.getParameters());
            map.remove(HelperStrings.CACHECONTROL);
            map.remove(HelperStrings.RENDERMODE);
            final StringBuilder qs = new StringBuilder();
            qs.append(path);
            // qs.append("ContentServer");
            qs.append("?");
            for (final Iterator<Map.Entry<String, String>> i = map.entrySet().iterator(); i.hasNext();) {
                final Map.Entry<String, String> entry = i.next();
                qs.append(urlCodec.encode(entry.getKey()));
                qs.append("=");
                final String v = entry.getValue();
                if (v != null && v.startsWith(HelperStrings.SSURI_START)) {

                    final QueryString inner = linkToMap(v);
                    qs.append(urlCodec.encode(toLink(inner)));
                } else {
                    qs.append(urlCodec.encode(v));
                }
                if (i.hasNext()) {
                    qs.append("&");
                }
            }
            return qs.toString();
        } catch (final EncoderException e) {
            log.warn(e);
            return null;
        } catch (final RuntimeException e) {
            log.warn(e);
            return null;
        }

    }

    public final Link linkToMap(final String link) {

        return uriToQueryString(URI.create(StringEscapeUtils.unescapeXml(link)));

    }

    public Link uriToQueryString(final URI uri) {
        final String qs = uri.getQuery();
        if (log.isDebugEnabled()) {
            log.debug(qs);
        }
        final Link map = new Link();
        if (qs == null) {
            return map;
        }
        final String[] val = uri.getQuery().split("&");

        for (final String v : val) {
            if (!v.startsWith(SSURI_PREFIX)) {
                final int t = v.indexOf('=');
                map.addParameter(v.substring(0, t), v.substring(t + 1, v.length()));
            } else {
                if (SSURI_BLOBSERVER.equals(v)) {
                    map.clear();
                    break;
                }
            }
        }
        return map;

    }

    protected String getCharSet() {
        return UTF8;
    }

}
