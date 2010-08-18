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

import java.util.ArrayList;
import java.util.List;

import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.util.SSUriHelper;

public class BodyHandler implements Visitor<ResultPage> {

    private final List<Visitor<ResultPage>> visitors = new ArrayList<Visitor<ResultPage>>();

    /**
     * @param page
     */
    public BodyHandler(final SSUriHelper uriHelper) {
        super();
        visitors.add(new BodyMarkerHandler(uriHelper));
        visitors.add(new ShortBodyMarkerHandler(uriHelper));
        visitors.add(new BodyLinkHandler(uriHelper));
        visitors.add(new BodyRawLinkHandler(uriHelper));
        visitors.add(new SSUnqualifiedBodyLinkHandler(uriHelper));

    }

    public void visit(ResultPage page) {
        if (page.getResponseCode() != 200)
            return; //bail out
        for (Visitor<ResultPage> visitor : visitors) {
            visitor.visit(page);
        }

    }

}
