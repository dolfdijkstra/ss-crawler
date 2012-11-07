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

import java.util.regex.Pattern;

import com.fatwire.dta.sscrawler.util.SSUriHelper;

public class BodyMarkerHandler extends AbstractBodyMarkerHandler {

    private static final String tagName = "com\\.fatwire\\.satellite\\.page";
    private static final Pattern pattern = Pattern
            .compile("(<" + tagName + ")(\\s(\\w*=\".*?\")?)*(/" + tagName + ">)");

    public BodyMarkerHandler(final SSUriHelper uriHelper) {
        super(uriHelper);
    }

    @Override
    protected Pattern getPagePattern() {
        return pattern;
    }

}
