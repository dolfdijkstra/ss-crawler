README for version ${pom.version}

This program will crawl ContentServer for all the pagelets 
and links it finds, and report on them. Please see the 
reporting section on the reports provided.

USAGE:
minimal arguments to specify for windows
ss-crawler.bat -startUri <full url to ContentServer with querystring containing parameters for staring page>

for instance: crawl FSII at localhost 
ss-crawler.bat -startUri "http://localhost:8080/cs/ContentServer?pagename=FSIIWrapper&cid=1118867611403&c=Page&p=1118867611403&childpagename=FirstSiteII/FSIILayout"

minimal arguments to specify for UNIX (bash)
ss-crawler.sh -startUri <full url to ContentServer with querystring containing parameters for staring page>

for instance: crawl FSII at localhost 
ss-crawler -startUri 'http://localhost:8080/cs/ContentServer?pagename=FSIIWrapper&cid=1118867611403&c=Page&p=1118867611403&childpagename=FirstSiteII/FSIILayout'

COMMAND LINE ARGUMENTS
command line arguments are space seperated (-a val1 -b val2)
-startUri (REQUIRED> <full url to ContentServer with querystring containing parameters for staring page>
-reportDir <OPTIONAL> <directory where the reports are written>. If not specified ./reports will be used. The directory with be appended with a subdir for the timestamp when the program was started. 
-max (OPTIONAL) <maximum number of full pages to be crawled>. If not specified 
-uriHelperFactory (OPTIONAL) <a classname extending com.fatwire.dta.sscrawler.util.UriHelperFactory if you need a special implementation for uri parsing>. Normally no need to set this. For CS6.2 you need to set it tocom.fatwire.dta.sscrawler.util.DecodingUriHelperFactory.

PROXY SUPPORT
Add to the start script, with the correct values for your environment. At this moment there is no support for proxu authentication. 
set JAVA_OPTS=-Dhttp.proxyhost=myproxy -Dhttp.proxyport=8080


REPORTS:
Several reports will be created when this program is running.

* LinkCollectingReporter: pagelets.txt
  A list of pagenames and the various other query parameters 
  
* OuterLinkCollectingReporter: browsable-links.txt
  A list of links as that could be used by a browser. In technical terms: the link to the outer wrapper pagelet.
  
* PageCollectingReporter: pages-list.txt
  Writes the full set of http headers and response body into a subdir called pages, 
  and procudes a overview file (pages-list.txt) with the mapping between the pagelet uri and the file.

* PageletTimingsStatisticsReporter: pagelet-stats.xls
  Writers various statistical parameters per pagename of the pagelets called.
  The statistics produced are number of invocations, average, min, max and standard-deviation for the download time.

* PageCriteriaReporter: pagecriteria.txt
  If this pagelet is configured to be cached, this report will report on arguments used to call the pagelet that are not part of the pagelet cache criteria.
  
* PageRenderTimeReporter: pagelet-timings.txt
  Reports for each pagelet call done  what the pagename, download time, http response code and the uri was.

* RootElementReporter: root-elements.txt
  Reports the root elements for all the pagelets invoked.
  
* NumberOfInnerPageletsReporter: num-inner-pagelets.txt 
  Writers a list of (direct) inner pagelets per invoked pagelet if the number of inner pagelets is higher then 12.
                
* Non200ResponseCodeReporter: non-200-repsonse.txt
  Which pagelets produce a non 200 HTTP status code response. A non-200 response is considered an error.

* InnerPageletReporter: inner-pagelets.txt
  Reports per pagelet what the direct innner pagelets are.

* InnerLinkReporter: inner-links.txt
  Reports per pagelet what the links are (from render:getpageurl, render:getbloburl etc) are.


* NestingReporter: nesting.txt
  Reports per pagelet the number of direct and nested pagelets if this number is higher then 10.
  
* PageletOnlyReporter: pagelet-only.txt
  Reports a list of pagelets that can not be invoked externally. (Have site catalog pageletonly flag set to T).

* NotCachedReporter: not-cached.txt
  Reports a list of pagelets that are not cached, either by configuration or by runtime.

* DefaultArgumentsAsPageCriteriaReporter: defaultArgumentsAsPageCriteriaReporter.txt
  Reports if the pagelet default arguments (as per SiteCatalog) are not also cache criteria.

