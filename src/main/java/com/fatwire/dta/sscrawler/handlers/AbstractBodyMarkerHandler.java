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

package com.fatwire.dta.sscrawler.handlers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fatwire.dta.sscrawler.Pagelet;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.util.SSUriHelper;

public abstract class AbstractBodyMarkerHandler extends AbstractBodyHandler {

    private static final Pattern tagPattern = Pattern.compile(" .*?=\".*?\"");

    protected Pattern getTagPattern() {
        return tagPattern;
    }

    protected abstract Pattern getPagePattern();

    public AbstractBodyMarkerHandler(final SSUriHelper uriHelper) {
        super(uriHelper);
    }

    public final void visit(final ResultPage page) {
        final Matcher m = getPagePattern().matcher(page.getBody());
        // "<com.fatwire.satellite.page pagename="FirstSiteII/FSIILayout" cid="1118867611403" locale="1154551493541" rendermode="live" p="1118867611403" c="Page"
        // /com.fatwire.satellite.page>
        while (m.find()) {
            if (log.isDebugEnabled()) {
                log.debug(m.group());
            }
            doTag(m.group(), page);
        }

    }

    private void doTag(final String tag, final ResultPage page) {
        final Matcher m = getTagPattern().matcher(tag);
        final Pagelet map = new Pagelet();
        while (m.find()) {
            if (log.isTraceEnabled()) {
                log.trace(m.group());
            }
            final String x = m.group();
            final int t = x.indexOf('=');
            final String key = x.substring(0, t).trim();
            if (filter(key)) {
                final String value = x.substring(t + 2, x.length() - 1);
                try {
                    final String v = URLDecoder.decode(value, "UTF-8");
                    map.addParameter(key, v);
                } catch (final UnsupportedEncodingException e) {
                    log.warn("can not urldecode '" + value + "' from '" + x +"'", e);
                }

            }

        }
        page.addMarker(map);

    }

    protected boolean filter(final String key) {
        return !"cachecontrol".equalsIgnoreCase(key);
    }

}
