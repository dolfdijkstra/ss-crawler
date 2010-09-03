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
