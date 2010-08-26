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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fatwire.dta.sscrawler.Link;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.util.SSUriHelper;

public class BodyLinkHandler extends AbstractBodyHandler {

    private final Pattern linkPattern = Pattern
            .compile("satellitescheme://SSURI/.*?#satellitefragment");

    /**
     * @param uriHelper
     */
    public BodyLinkHandler(final SSUriHelper uriHelper) {
        super(uriHelper);
    }

    /* (non-Javadoc)
     * @see com.fatwire.dta.sscrawler.handlers.Visitor#visit(java.lang.Object)
     */
    public void visit(ResultPage page) {
        final Matcher m = linkPattern.matcher(page.getBody());
        while (m.find()) {
            log.debug(m.group());
            Link map = uriHelper.linkToMap(m.group());
            if (map.isOK()) {
                page.addLink(map);
            }
        }
    }

}
