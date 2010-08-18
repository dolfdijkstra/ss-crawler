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

/**
 * 
 */
package com.fatwire.dta.sscrawler.reporting.reporters;

import com.fatwire.dta.sscrawler.Link;
import com.fatwire.dta.sscrawler.ResultPage;
import com.fatwire.dta.sscrawler.reporting.Report;
import com.fatwire.dta.sscrawler.util.SSUriHelper;

public class OuterLinkCollectingReporter extends ReportDelegatingReporter {
    private final SSUriHelper uriHelper;

    public OuterLinkCollectingReporter(final Report report,
            SSUriHelper uriHelper) {
        super(report);
        this.uriHelper = uriHelper;
    }

    public void addToReport(final ResultPage page) {
        if (page.getUri() instanceof Link) {
            report.addRow(uriHelper.toLink(page.getUri()));
        }
    }

    @Override
    protected String[] getHeader() {
        return new String[0];
    }

}