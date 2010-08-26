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

package com.fatwire.dta.sscrawler.jobs;

public class NullProgressMonitor implements ProgressMonitor {

    public void beginTask(final String name, final int totalWork) {
        // TODO Auto-generated method stub

    }

    public void done() {
        // TODO Auto-generated method stub

    }

    public void internalWorked(final double work) {
        // TODO Auto-generated method stub

    }

    public boolean isCanceled() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setCanceled(final boolean value) {
        // TODO Auto-generated method stub

    }

    public void setTaskName(final String name) {
        // TODO Auto-generated method stub

    }

    public void subTask(final String name) {
        // TODO Auto-generated method stub

    }

    public void worked(final int work) {
        // TODO Auto-generated method stub

    }

}
