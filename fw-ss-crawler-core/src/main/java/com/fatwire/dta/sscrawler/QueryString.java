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

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public abstract class QueryString {

    private final Map<String, String> map = new TreeMap<String, String>();

    
    
    public void addParameter(final String key, final String value) {
        map.put(key, value);
    }

    public Map<String, String> getParameters() {
        return map;
    }
    public String get(String key) {
        return map.get(key);
    }

    public boolean has(String key) {
        return map.containsKey(key);
    }
    public String remove(String key) {
        return map.remove(key);
    }

    public void clear() {
        map.clear();
    }

    public boolean isOK() {
        return !map.isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (map == null ? 0 : map.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QueryString other = (QueryString) obj;
        if (map == null) {
            if (other.map != null) {
                return false;
            }
        } else if (!map.equals(other.map)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder qs = new StringBuilder();
        for (final Iterator<Map.Entry<String, String>> i = map.entrySet().iterator(); i.hasNext();) {
            final Map.Entry<String, String> entry = i.next();
            qs.append(entry.getKey());
            qs.append("=");
            qs.append(entry.getValue());
            if (i.hasNext()) {
                qs.append("&");
            }
        }
        return qs.toString();

    }

}
