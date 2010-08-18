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

import org.apache.commons.httpclient.Header;

public class CacheHelper {
    //com.futuretense.contentserver.pagedata.field.sscacheinfocache-disabled: false
    //com.futuretense.contentserver.pagedata.field.cscacheinfocache-disabled: false
    public static final String SS_CACHE_INFO = HelperStrings.CS_TO_SS_RESPONSE_HEADER_PREFIX
            + "sscacheinfostring";

    public static final String CS_CACHE_INFO = HelperStrings.CS_TO_SS_RESPONSE_HEADER_PREFIX
            + "cscacheinfostring";

    //com.futuretense.contentserver.pagedata.field.sscacheinfostring: true
    //com.futuretense.contentserver.pagedata.field.cscacheinfostring: true

    public static boolean shouldCache(Header[] headers) {
        int i = 0;
        int j = 0;
        for (Header h : headers) {
            if (SS_CACHE_INFO.equals(h.getName())
                    || CS_CACHE_INFO.equals(h.getName())) {
                j++;
                if ("false".equals(h.getValue())) {
                    i++;
                }
            }
        }
        return j == 2 && i == 0;
    }

}
