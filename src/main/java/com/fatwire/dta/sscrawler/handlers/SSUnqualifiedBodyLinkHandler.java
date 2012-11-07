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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import com.fatwire.dta.sscrawler.Link;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.util.SSUriHelper;

public class SSUnqualifiedBodyLinkHandler extends AbstractBodyHandler {

    // ssUnqualifiedLink ended by quote, double quote or whitespace
    private final Pattern linkPattern = Pattern.compile("ssUnqualifiedLink\\?.*?['\\\" ]");

    /**
     * @param uriHelper
     */
    public SSUnqualifiedBodyLinkHandler(final SSUriHelper uriHelper) {
        super(uriHelper);
    }

    public void visit(final ResultPage page) {
        final Matcher m = linkPattern.matcher(page.getBody());

        while (m.find()) {
            log.debug(m.group());
            final String link = m.group();
            if (link.length() > 1) {
                doLink(link.substring(0, link.length() - 1), page);
            }
        }

    }

    void doLink(final String link, final ResultPage page) {
        log.trace(link);
        try {
            // <a
            // href='ssUnqualifiedLink?op=CM_Actualidad_FA&c=Page&op2=1142352029508&paginaActual=0&pagename=ComunidadMadrid%2FEstructura&subMenuP=subMenuPresidenta&language=es&cid=1109266752498'
            final URI uri = new URI(StringEscapeUtils.unescapeXml(link));
            // final URI uri = new URI(link);
            log.trace(uri.getQuery());
            final String[] val = uri.getQuery().split("&");
            final Link map = new Link();
            for (final String v : val) {
                if (v.startsWith("blobcol=")) {
                    map.clear();
                    break;
                } else {
                    final int t = v.indexOf('=');
                    map.addParameter(v.substring(0, t), v.substring(t + 1, v.length()));

                }
            }
            if (!map.isOK()) {
                page.addLink(map);
            }
        } catch (final URISyntaxException e) {
            log.error(e);
        }
    }

}
