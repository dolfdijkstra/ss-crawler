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

import com.fatwire.dta.sscrawler.Link;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 
 * Special form of a SSHelper that unpacks <tt>packedargs</tt> if the parameter <tt>childpagename</tt> is present.
 * <p/>
 * render:gettemplateurl sets all the non c/cid/p/rendermode args into one packedargs argument. This code reverts this if the customer
 *  urlassembler does the same. 
 * 
 * @author Dolf Dijkstra
 * @since Sep 3, 2010
 */
public class PackedArgsSSUriHelper extends SSUriHelper {
    protected final Log log = LogFactory.getLog(getClass());


    /**
     * @param path
     */
    public PackedArgsSSUriHelper(final String path) {
        super(path);
    }

    public Link createLink(final URI uri) {
        Link link = super.createLink(uri);
        if (link == null)
            return link;

        if (link.has("childpagename")) {
            final String packedargs = link.remove("packedargs");
            // special case if there is a childpagename, in that case packedargs
            // contain all the non c/cid/p/rendermode args.
            // decode them correctly

            if (packedargs != null) {
                log.trace("packedargs with childpagename: " + packedargs);
                String[] pv;
                try {
                    pv = decode(packedargs).split("&");
                    for (String v : pv) {
                        String[] nv = v.split("=");
                        if (nv.length == 2) {
                            link.addParameter(nv[0], nv[1]);
                        } else if (nv.length == 1) {
                            link.addParameter(nv[0], "");
                        }

                    }

                } catch (DecoderException e) {
                    log.warn(e + " for packedargs: " + packedargs + " on " + uri.getRawQuery(), e);
                    return null;

                }
            }

        }

        return link;

    }

}
