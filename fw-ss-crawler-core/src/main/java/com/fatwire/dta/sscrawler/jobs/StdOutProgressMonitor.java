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

public class StdOutProgressMonitor implements ProgressMonitor {

    private int totalWork;

    private String taskName;

    private boolean cancelled;

    private String subTask;

    public void beginTask(String name, int totalWork) {
        this.taskName = name;
        this.totalWork = totalWork;
        System.out.println(name);

    }

    public void done() {
        System.out.println(taskName +" is finished");

    }

    public void internalWorked(double work) {
        System.out.print("#");

    }

    public boolean isCanceled() {
        return cancelled;
    }

    public void setCanceled(boolean value) {
        this.cancelled = value;

    }

    public void setTaskName(String name) {
        this.taskName = name;
        System.out.println(name);

    }

    public void subTask(String name) {
        this.subTask = name;
        System.out.println(name);

    }

    public void worked(int work) {
        System.out.print("#");

    }

}
