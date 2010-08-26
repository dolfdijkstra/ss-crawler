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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import com.fatwire.dta.sscrawler.QueryString;

public class PageNameDecorator extends TreeMap<String, List<QueryString>> {

    /**
     * 
     */
    private static final long serialVersionUID = 846247321423661559L;

    /**
     * @param c
     */
    public PageNameDecorator(final Collection<? extends QueryString> c) {
        super();
        for (final QueryString s : c) {
            final String pageName = s.getParameters().get(HelperStrings.PAGENAME);
            if (pageName != null) {
                final String p = "pagename=" + pageName;
                List<QueryString> l = null;
                if (containsKey(p)) {
                    l = get(p);

                } else {
                    l = new ArrayList<QueryString>();
                    put(p, l);
                }
                l.add(s);
            }
        }

    }

}
