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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.fatwire.dta.sscrawler.events.PageletRenderedEvent;
import com.fatwire.dta.sscrawler.events.PageletRenderingListener;
import com.fatwire.dta.sscrawler.jobs.Command;
import com.fatwire.dta.sscrawler.jobs.ProgressMonitor;

public class RenderCommand implements Command {
    final URLReaderService reader;

    private final List<Link> queue = new ArrayList<Link>();

    private final Set<PageletRenderingListener> listeners = new CopyOnWriteArraySet<PageletRenderingListener>();

    /**
     * @param hostConfig
     * @param maxPages
     */
    public RenderCommand(final URLReaderService reader) {
        super();
        this.reader = reader;
    }

    public void addStartUri(final Link uri) {
        queue.add(uri);
    }

    public void execute(final ProgressMonitor monitor) {

        if (queue.isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }

        final PageletRenderingListener readerListener = new PageletRenderingListener() {

            public void renderPerformed(final PageletRenderedEvent event) {
                for (final PageletRenderingListener listener : listeners) {
                    listener.renderPerformed(event);
                }

            }

        };

        reader.addListener(readerListener);

        reader.start(queue);
        reader.removeListener(readerListener);

    }

    public void addListener(final PageletRenderingListener listener) {
        listeners.add(listener);
    }

    public void removeListener(final PageletRenderingListener listener) {
        listeners.remove(listener);
    }

}
