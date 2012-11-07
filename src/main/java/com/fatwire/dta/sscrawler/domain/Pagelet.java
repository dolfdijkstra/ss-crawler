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

package com.fatwire.dta.sscrawler.domain;

import java.util.Collection;

public class Pagelet {

    private long id;

    private String params;

    private Collection<Pagelet> nestedPagelets;

    private Collection<Link> listedLinks;

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the listedLinks
     */
    public Collection<Link> getListedLinks() {
        return listedLinks;
    }

    /**
     * @param listedLinks the listedLinks to set
     */
    public void setListedLinks(Collection<Link> listedLinks) {
        this.listedLinks = listedLinks;
    }

    /**
     * @return the nestedPagelets
     */
    public Collection<Pagelet> getNestedPagelets() {
        return nestedPagelets;
    }

    /**
     * @param nestedPagelets the nestedPagelets to set
     */
    public void setNestedPagelets(Collection<Pagelet> nestedPagelets) {
        this.nestedPagelets = nestedPagelets;
    }

    /**
     * @return the params
     */
    public String getParams() {
        return params;
    }

    /**
     * @param params the params to set
     */
    public void setParams(String params) {
        this.params = params;
    }
}
