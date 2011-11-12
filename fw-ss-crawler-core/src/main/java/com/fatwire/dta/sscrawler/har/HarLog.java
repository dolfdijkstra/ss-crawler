package com.fatwire.dta.sscrawler.har;

import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class HarLog {

    List<HarPage> pages = new LinkedList<HarPage>();
    List<HarEntry> entries = new LinkedList<HarEntry>();

    DateTimeFormatter dateFormatter = ISODateTimeFormat.dateTime();
    private JsonGenerator g;

    public void addPage(HarPage page) {
        pages.add(page);
    }

    public void addEntry(HarEntry entry) {
        entries.add(entry);
    }

    void stream(Writer writer) throws IOException {
        JsonFactory f = new JsonFactory();

        g = f.createJsonGenerator(writer);

        g.writeStartObject();

        g.writeObjectFieldStart("log");
        g.writeStringField("version", "1.2");
        g.writeStringField("comment", "ss-crawler created");
        g.writeObjectFieldStart("creator");
        g.writeStringField("name", "ss-crawler");
        g.writeStringField("version", "1.3");
        g.writeStringField("comment", "Analyzer for the pagelet composition");
        g.writeEndObject();
        g.writeObjectFieldStart("browser");
        g.writeStringField("name", "ss-crawler/0,9");
        g.writeStringField("version", "1.3");
        g.writeStringField("comment", "ss-crawler browser");
        g.writeEndObject();

        streamPages();

        streamEntries();
        g.writeEndObject();
        g.writeEndObject();

        g.close();
    }

    private void streamEntries() throws JsonGenerationException, IOException {
        g.writeArrayFieldStart("entries");

        // URI uri = URI.create(url);
        for (HarEntry entry : entries) {
            g.writeStartObject();
            g.writeStringField("pageref", entry.getPageRef());
            g.writeStringField("startedDateTime", dateFormatter.print(entry.getStartTime()));
            g.writeNumberField("time", entry.getReadTime());
            g.writeObjectFieldStart("request");

            g.writeStringField("method", "GET");
            g.writeStringField("url", entry.getUri().toASCIIString());
            g.writeStringField("httpVersion", entry.getStatusLine().getHttpVersion());
            g.writeArrayFieldStart("cookies");
            g.writeEndArray();
            g.writeArrayFieldStart("headers");
            for (Header header : entry.getRequestHeaders()) {
                g.writeStartObject();

                g.writeStringField("name", header.getName());
                g.writeStringField("value", header.getValue());

                g.writeEndObject();
            }
            g.writeEndArray();
            g.writeArrayFieldStart("queryString");
            g.writeEndArray();
            // g.writeObjectFieldStart("postData");
            // g.writeEndObject();
            g.writeNumberField("headersSize", -1);
            g.writeNumberField("bodySize", 0);
            g.writeStringField("comment", "");
            g.writeEndObject();

            g.writeObjectFieldStart("response");
            g.writeNumberField("status", entry.getStatusLine().getStatusCode());
            g.writeStringField("statusText", entry.getStatusLine().getReasonPhrase());
            g.writeStringField("httpVersion", entry.getStatusLine().getHttpVersion());
            g.writeArrayFieldStart("cookies");
            g.writeEndArray();
            g.writeArrayFieldStart("headers");
            for (Header header : entry.getResponseHeaders()) {
                g.writeStartObject();

                g.writeStringField("name", header.getName());
                g.writeStringField("value", header.getValue());

                g.writeEndObject();
            }
            g.writeEndArray();
            g.writeObjectFieldStart("content");
            g.writeNumberField("size", entry.getPageLength());
            g.writeNumberField("compression", 0);
            g.writeStringField("mimeType", "text/html; charset=utf-8");
            g.writeStringField("text", entry.getBody());
            g.writeStringField("comment", "");
            g.writeEndObject();
            g.writeStringField("redirectURL", "");
            g.writeNumberField("headersSize", -1);
            g.writeNumberField("bodySize", entry.getPageLength());
            g.writeStringField("comment", "");
            g.writeEndObject();
            g.writeObjectFieldStart("cache");
            g.writeEndObject();
            g.writeObjectFieldStart("timings");
            g.writeNumberField("blocked", 0);
            // g.writeNumberField("dns", -1);
            g.writeNumberField("connect", entry.getConnectTime());
            g.writeNumberField("send", 0);
            g.writeNumberField("wait", entry.getTimeToFirstByte());
            g.writeNumberField("receive", entry.getReadTime() - entry.getTimeToFirstByte());
            // g.writeNumberField("ssl", -1);
            g.writeStringField("comment", "");
            g.writeEndObject();
            g.writeStringField("serverIPAddress",
                    InetAddress.getAllByName(entry.getUri().getHost())[0].getHostAddress());
            g.writeStringField("connection", "12345");
            g.writeStringField("comment", "");
            g.writeEndObject();
        }
        g.writeEndArray();

    }

    private void streamPages() throws JsonGenerationException, IOException {
        g.writeArrayFieldStart("pages");

        for (HarPage page : pages) {
            g.writeStartObject();
            g.writeStringField("startedDateTime", dateFormatter.print(page.getStartedDateTime()));// "2009-04-16T12:07:25.123+01:00");
            g.writeStringField("id", page.getId());
            g.writeStringField("title", page.getTitle());
            g.writeObjectFieldStart("pageTimings");

            g.writeNumberField("onContentLoad", page.getOnContentLoad());
            g.writeNumberField("onLoad", page.getOnLoad());
            g.writeStringField("comment", page.getComment());
            g.writeEndObject();
            g.writeEndObject();
        }
        g.writeEndArray();

    }

}
