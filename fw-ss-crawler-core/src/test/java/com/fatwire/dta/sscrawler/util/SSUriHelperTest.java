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

import junit.framework.Assert;
import junit.framework.TestCase;

import com.fatwire.dta.sscrawler.Link;

public class SSUriHelperTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLinkToMap() {
        SSUriHelper helper = new SSUriHelper("/cs/ContentServer");
        String uri = "satellitescheme://SSURI/?SSURIapptype=ContentServer&packedargs=section%3Dexample&SSURIcontainer=Default&c=Page&childpagename=IS%2FPage%2FThreeColumnPage&SSURIsession=false&pagename=IS%2FWrapper%2FWrapper&cid=1268294297498&SSURIsscontext=Satellite+Server&SSURIassembler=is#satellitefragment";
        Link link = helper.createLink(uri);
        Assert
                .assertEquals(
                        "c=Page&childpagename=IS/Page/ThreeColumnPage&cid=1268294297498&packedargs=section=example&pagename=IS/Wrapper/Wrapper",
                        link.toString());

    }

}
