/* 
 * Copyright (c) 2010, NHIN Direct Project
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution.  
 * 3. Neither the name of the the NHIN Direct Project (nhindirect.org)
 *    nor the names of its contributors may be used to endorse or promote products 
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY 
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nhindirect.xd.soap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * ThreadData bean. Contains information about the running threads.
 * 
 * @author Vince
 */
public class ThreadData {

    public static final String MESSAGE = "message";
    public static final String ACTION = "action";
    public static final String REPLY = "reply";
    public static final String TO = "to";
    public static final String RELATESTO = "relatesto";
    public static final String THISHOST = "thishost";
    public static final String REMOTEHOST = "remotehost";
    public static final String PID = "pid";
    public static final String FROM = "from";
    public static final String DIRECT_TO = "directTo";
    public static final String DIRECT_FROM = "directFrom";
    public static final String DIRECT_METADATA_LEVEL = "directMetadataLevel";
    
    private Long threadId = null;

    /**
     * Object to contain and relate all ThreadData objects.
     */
    private static Map<Long, Map<String, String>> threadMap = new HashMap<Long, Map<String, String>>();
    
    /**
     * Constructor.
     * 
     * @param id
     *            The value to set as the ID for the ThreadData object.
     */
    public ThreadData(Long id) {
        this.threadId = id;
    }

    /**
     * Remove an element from the threadMap object specified by the given id.
     * 
     * @param id
     *            The id of the element to remove from the threadMap object.
     */
    public static void clean(Long id) {
        threadMap.remove(id);
    }

    /**
     * Set the value for the key MESSAGE.
     * 
     * @param value
     *            the value for the key MESSAGE.
     */
    public void setMessageId(String value) {
        setValue(value, MESSAGE);
    }

    /**
     * Set the value for the key FROM.
     * 
     * @param value
     *            the value for the key FROM.
     */
    public void setFrom(String value) {
        setValue(value,FROM);
    }

    /**
     * Set the value for the key THISHOST.
     * 
     * @param value
     *            the value for the key THISHOST.
     */
    public void setThisHost(String value) {
        setValue(value, THISHOST);
    }

    /**
     * Set the value for the key REMOTEHOST.
     * 
     * @param value
     *            the value for the key REMOTEHOST.
     */
    public void setRemoteHost(String value) {
        setValue(value, REMOTEHOST);
    }

    /**
     * Set the value for the key ACTION.
     * 
     * @param value
     *            the value for the key ACTION.
     */
    public void setAction(String value) {
        setValue(value, ACTION);
    }

    /**
     * Set the value for the key PID.
     * 
     * @param value
     *            the value for the key PID.
     */
    public void setPid(String value) {
        setValue(value, PID);
    }

    /**
     * Set the value for the key REPLY.
     * 
     * @param value
     *            the value for the key REPLY.
     */
    public void setReplyAddress(String value) {
        setValue(value, REPLY);
    }

    /**
     * Set the value for the key TO.
     * 
     * @param value
     *            the value for the key TO.
     */
    public void setTo(String value) {
        setValue(value, TO);
    }

    /**
     * Set the value for the key RELATESTO.
     * 
     * @param value
     *            the value for the key RELATESTO.
     */
    public void setRelatesTo(String value) {
        setValue(value, RELATESTO);
    }
    
    /**
     * Set the value for the key DIRECT_TO.
     * 
     * @param value
     *            the value for the key DIRECT_TO.
     */
    public void setDirectTo(String value) {
        setValue(value, DIRECT_TO);
    }

    /**
     * Set the value for the key DIRECT_FROM.
     * 
     * @param value
     *            the value for the key DIRECT_FROM.
     */
    public void setDirectFrom(String value) {
        setValue(value, DIRECT_FROM);
    }
    
    /**
     * Set the value for the key DIRECT_METADATA_LEVEL.
     * 
     * @param value
     *            the value for the key DIRECT_METADATA_LEVEL.
     */
    public void setDirectMetadataLevel(String value) {
        setValue(value, DIRECT_METADATA_LEVEL);
    }
    
    /**
     * Return the value for the key THISHOST.
     * 
     * @return the value for the key THISHOST.
     */
    public String getThisHost() {
       return getValue(THISHOST);
    }

    /**
     * Return the value for the key REMOTEHOST.
     * 
     * @return the value for the key REMOTEHOST.
     */
    public String getRemoteHost() {
        return getValue(REMOTEHOST);
    }

    /**
     * Return the value for the key MESSAGE.
     * 
     * @return the value for the key MESSAGE.
     */
    public String getMessageId() {
        return getValue(MESSAGE);
    }

    /**
     * Return the value for the key ACTION.
     * 
     * @return the value for the key ACTION.
     */
    public String getAction() {
        return getValue(ACTION);
    }
    
    /**
     * Return the value for the key PID.
     * 
     * @return the value for the key PID.
     */
    public String getPid() {
        return getValue(PID);
    }

    /**
     * Return the value for the key REPLY.
     * 
     * @return the value for the key REPLY.
     */
    public String getReplyAddress() {
        return getValue(REPLY);
    }

    /**
     * Return the value for the key TO.
     * 
     * @return the value for the key TO.
     */
    public String getTo() {
        return getValue(TO);
    }

    /**
     * Return the value for the key FROM.
     * 
     * @return the value for the key FROM.
     */
    public String getFrom() {
        return getValue(FROM);
    }

    /**
     * Return the value for the key RELATESTO.
     * 
     * @return the value for the key RELATESTO.
     */
    public String getRelatesTo() {
        return getValue(RELATESTO);
    }
    
    /**
     * Return the value for the key DIRECT_TO.
     * 
     * @return the value for the key DIRECT_TO.
     */
    public String getDirectTo() {
        return getValue(DIRECT_TO);
    }
    
    /**
     * Return the value for the key DIRECT_FROM.
     * 
     * @return the value for the key DIRECT_FROM.
     */
    public String getDirectFrom() {
        return getValue(DIRECT_FROM);
    }

    /**
     * Return the value for the key DIRECT_METADATA_LEVEL.
     * 
     * @return the value for the key DIRECT_METADATA_LEVEL.
     */
    public String getDirectMetadataLevel() {
        return getValue(DIRECT_METADATA_LEVEL);
    }
    
    /**
     * Set a key,value pair for the current threadId.
     * 
     * @param value
     *            The value to set.
     * @param key
     *            The key to set.
     */
    private void setValue(String value, String key) {
        Map<String, String> data = null;
        if (threadMap.containsKey(threadId)) {
            data = threadMap.get(threadId);
        } else {
            data = new HashMap<String, String>();
            threadMap.put(threadId, data);
        }
        data.put(key, value);
    }

    /**
     * Get the value relating to the specified key and current threadId.
     * 
     * @param key
     *            The key for which to retrieve the value.
     * @return the value associated with the given key and current threadId.
     */
    private String getValue(String key) {
        String ret = null;

        Map<String, String> data = threadMap.get(threadId);
        if (data != null) {
            ret = data.get(key);
        }
        return ret;
    }

    /**
     * Return a read-only copy of the threadMap object.
     * 
     * @return a read-only copy of the threadmap object
     */
    protected static Map<Long, Map<String, String>> getThreadMapView() {
        return Collections.unmodifiableMap(threadMap);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        Map<String, String> m = threadMap.get(this.threadId);

        if (m != null) {
            StringBuffer sb = new StringBuffer("ThreadData (threadId: " + this.threadId + ")" + "\n");

            for (Map.Entry<String, String> e : m.entrySet()) {
                sb.append(" > " + e.getKey() + ": " + e.getValue() + "\n");
            }

            return sb.toString();
        }

        return "No map found for threadId: " + this.threadId;
    }
}
