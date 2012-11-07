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

package com.fatwire.dta.sscrawler;

import com.fatwire.dta.sscrawler.util.HelperStrings;

public class SimpleUriValidator implements UriValidator {

    private String domain;

    private String domainWithCS;

    public boolean validate(final String uri) {
        return validateForDomainStart(uri) && validateForPageDataRequest(uri);
    }

    protected boolean validateForDomainStart(final String uri) {
        return !uri.startsWith(domainWithCS);
    }

    protected boolean validateForPageDataRequest(final String uri) {
        return uri.indexOf(HelperStrings.SS_PAGEDATA_REQUEST) < 1;
    }

    /**
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomain(final String domain) {
        this.domain = domain;
        domainWithCS = domain + HelperStrings.CONTENTSERVER;
    }

}
