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

public class HelperStrings {
    public static final String SS_CLIENT_INDICATOR = "ft_ss"; // true if client
                                                              // is ss,
                                                              // false/null if
                                                              // not

    public static final String SS_PAGEDATA_REQUEST = "please_send_pagedata_to_ft_ss"; // true
                                                                                      // /
                                                                                      // false

    public static final String PAGENAME = "pagename";

    public static final String RENDERMODE = "rendermode";

    public static final String CS_TO_SS_RESPONSE_HEADER_PREFIX = "com.futuretense.contentserver.pagedata.field.";
    public static final String SS_CACHEINFO_HEADER = CS_TO_SS_RESPONSE_HEADER_PREFIX + "sscacheinfocache-disabled";
    public static final String CS_CACHEINFO_HEADER = CS_TO_SS_RESPONSE_HEADER_PREFIX + "cscacheinfocache-disabled";

    public static final String PAGE_CRITERIA_HEADER = CS_TO_SS_RESPONSE_HEADER_PREFIX + "pagecriteria";

    public static final char[] CRLF = "\r\n".toCharArray();

    public static final String CONTENTSERVER = "ContentServer";

    public static final String SSURI_START = "satellitescheme://SSURI";

    public static final String CACHECONTROL = "cachecontrol";

    public static final String STATUS_NOTCACHED = "<!--FTCACHE-0-->";
    public static final String STATUS_CACHED = "<!--FTCACHE-1-->";

}
