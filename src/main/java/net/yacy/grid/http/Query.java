/**
 *  Query
 *  Copyright 19.05.2016 by Michael Peter Christen, @0rb1t3r
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program in the file lgpl21.txt
 *  If not, see <http://www.gnu.org/licenses/>.
 */

package net.yacy.grid.http;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import net.yacy.grid.tools.DateParser;

public class Query {

    private HttpServletRequest request;
    private Map<String, byte[]> qm;
    
    public Query(final HttpServletRequest request) {
        this.qm = new HashMap<>();
        if (request != null) for (Map.Entry<String, String[]> entry: request.getParameterMap().entrySet()) {
            this.qm.put(entry.getKey(), entry.getValue()[0].getBytes(StandardCharsets.UTF_8));
        }
        this.request = request;
    }
    public Query initGET(final Map<String, String> q) {
        q.forEach((k, v) -> this.qm.put(k, v.getBytes(StandardCharsets.UTF_8)));
        return this;
    }
    public Query initGET(final JSONObject json) {
        json.keySet().forEach(k -> this.qm.put(k, json.getString(k).getBytes(StandardCharsets.UTF_8)));
        return this;
    }
    public Query initPOST(final Map<String, byte[]> qm) {
        this.qm = qm;
        return this;
    }
    public String get(String key) {
        String val = this.request == null ? null : this.request.getParameter(key);
        if (val == null && this.qm.containsKey(key)) return new String(this.qm.get(key), StandardCharsets.UTF_8);
        return val;
    }
    public String get(String key, String dflt) {
        String val = this.request == null ? null : this.request.getParameter(key);
        if (val == null && this.qm.containsKey(key)) return new String(this.qm.get(key), StandardCharsets.UTF_8);
        return val == null ? dflt : val;
    }
    public byte[] get(String key, byte[] dflt) {
        String val = this.request == null ? null : this.request.getParameter(key);
        if (val == null && this.qm.containsKey(key)) return this.qm.get(key);
        return val == null ? dflt : val.getBytes(StandardCharsets.UTF_8);
    }
    public String[] get(String key, String[] dflt, String delim) {
        String val = get(key);
        return val == null || val.length() == 0 ? dflt : val.split(delim);
    }
    public int get(String key, int dflt) {
        String val = get(key);
        return val == null || val.length() == 0 ? dflt : Integer.parseInt(val.trim());
    }
    public long get(String key, long dflt) {
        String val = get(key);
        return val == null || val.length() == 0 ? dflt : Long.parseLong(val.trim());
    }
    public double get(String key, double dflt) {
        String val = get(key);
        return val == null || val.length() == 0 ? dflt : Double.parseDouble(val.trim());
    }
    public boolean get(String key, boolean dflt) {
        String val = get(key);
        return val == null ? dflt : "true".equals(val = val.trim()) || "1".equals(val);
    }
    public Date get(String key, Date dflt, int timezoneOffset) {
        String val = get(key);
        try {
            return val == null || val.length() == 0 ? dflt : DateParser.parse(val.trim(), timezoneOffset).getTime();
        } catch (ParseException e) {
            return dflt;
        }
    }
    public Set<String> getKeys() {
        if (this.request == null || this.request.getParameterMap().size() == 0) return this.qm.keySet();
        return this.request.getParameterMap().keySet();
    }
    public void setResponse(final HttpServletResponse response, final String mime) {
        long access_time = System.currentTimeMillis();
        response.setDateHeader("Last-Modified", access_time);
        response.setDateHeader("Expires", access_time + 1000);
        response.setContentType(mime);
        response.setHeader("X-Robots-Tag",  "noindex,noarchive,nofollow,nosnippet");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
    }
    public int hashCode() {
        return qm.hashCode();
    }
    public HttpServletRequest getRequest() {
        return this.request;
    }
}