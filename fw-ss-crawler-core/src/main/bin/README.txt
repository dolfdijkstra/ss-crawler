README for version ${pom.version}

This program will crawl ContentServer for all the pagelets 
and links it finds, and report on them. Please see the 
reporting section on the reports provided.

USAGE:
minimal arguments to specify for windows
ss-crawler.bat crawler <full url to ContentServer with querystring containing parameters for staring page>

for instance: crawl FSII at localhost 
ss-crawler.bat crawler "http://localhost:8080/cs/ContentServer?pagename=FSIIWrapper&cid=1118867611403&c=Page&p=1118867611403&childpagename=FirstSiteII/FSIILayout"

minimal arguments to specify for UNIX (bash)
ss-crawler.sh crawler <full url to ContentServer with querystring containing parameters for staring page>

for instance: crawl FSII at localhost 
ss-crawler.sh crawler 'http://localhost:8080/cs/ContentServer?pagename=FSIIWrapper&cid=1118867611403&c=Page&p=1118867611403&childpagename=FirstSiteII/FSIILayout'

see http://www.nl.fatwire.com/dta/ss-crawler/ for more documentation.