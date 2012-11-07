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

package com.fatwire.dta.sscrawler.events;

import com.fatwire.dta.sscrawler.ResultPage;

public class PageletRenderedEvent extends EventObject<ResultPage> {

    public PageletRenderedEvent(final ResultPage page) {
        super(page);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 87405192630120962L;

    /**
     * @return the page
     */
    public ResultPage getPage() {
        return getSource();
    }

}
