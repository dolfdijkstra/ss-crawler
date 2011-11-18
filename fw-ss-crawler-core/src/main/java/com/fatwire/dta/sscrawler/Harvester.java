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

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;

public class Harvester implements Callable<ResultPage> {
    private static final Log log = LogFactory.getLog(Harvester.class);
    private static final Log log_time = LogFactory.getLog(Harvester.class.getName() + ".time");
    private final UrlRenderingCallable downloader;

    /**
     * @param downloader
     */
    public Harvester(final UrlRenderingCallable downloader) {
        super();
        this.downloader = downloader;

    }

    public ResultPage call() {
        try {
            long t = System.nanoTime();
            final ResultPage page = downloader.call();

            log_time.debug("Downloading page " + page.getUri() + " took " + ((System.nanoTime() - t) / 1000) + "us.");
            return page;
        } catch (final ConnectException e) {
            log.error(e + " for " + downloader.getUri());
        } catch (final HttpException e) {
            log.error(e + " for " + downloader.getUri());
        } catch (final IOException e) {
            log.error(e + " for " + downloader.getUri());
        } catch (final Exception e) {
            log.error(e + " for " + downloader.getUri(), e);
        }
        return null;

    }

}
