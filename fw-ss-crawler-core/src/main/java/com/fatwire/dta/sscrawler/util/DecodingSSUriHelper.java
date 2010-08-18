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

import com.fatwire.dta.sscrawler.Link;

public class DecodingSSUriHelper extends SSUriHelper {

    public DecodingSSUriHelper(final String domain) {
        super(domain);
    }

    /* (non-Javadoc)
     * @see com.fatwire.dta.sscrawler.util.SSUriHelper#uriToQueryString(java.net.URI)
     */
    @Override
    public Link uriToQueryString(final URI uri) {

        if (log.isDebugEnabled()) {
            log.debug(uri.getRawQuery());
        }
        final String[] val = uri.getQuery().split("&");
        final Link map = new Link();
        for (final String v : val) {
            if (!v.startsWith("SSURI")) {
                final int t = v.indexOf('=');
                try {
                    String key = URLDecoder.decode(v.substring(0, t), "UTF-8");
                    String value = URLDecoder.decode(v.substring(t + 1, v
                            .length()), "UTF-8");
                    map.addParameter(key, value);
                } catch (final UnsupportedEncodingException e) {
                    log.error(e, e);
                }
            } else {
                if ("SSURIapptype=BlobServer".equals(v)) {
                    map.clear();
                    break;
                }
            }
        }
        return map;
    }
}
