/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nhind.xdr;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
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
    
    private Long threadId = null;

    private static Map<Long, Map<String, String>> threadMap = new HashMap<Long, Map<String, String>>();
    
    /**
     * @param id
     */
    public ThreadData(Long id) {
        this.threadId = id;
    }

    /**
     * @param id
     */
    public void clean(Long id) {
        threadMap.remove(id);
    }

    /**
     * @param value
     */
    public void setMessageId(String value) {
        setValue(value, MESSAGE);
    }

     /**
     * @param value
     */
    public void setFrom(String value) {
        setValue(value,FROM);
    }
    /**
     * @param value
     */
    public void setThisHost(String value) {
        setValue(value, THISHOST);
    }
    /**
     * @param value
     */
    public void setRemoteHost(String value) {
        setValue(value, REMOTEHOST);
    }

    /**
     * @param value
     */
    public void setAction(String value) {
        setValue(value, ACTION);
    }

    /**
     * @param value
     */
    public void setPid(String value) {
        setValue(value, PID);
    }

    /**
     * @param value
     */
    public void setReplyAddress(String value) {
        setValue(value, REPLY);
    }

    /**
     * @param value
     */
    public void setTo(String value) {
        setValue(value, TO);
    }

    /**
     * @param value
     */
    public void setRelatesTo(String value) {
        setValue(value, RELATESTO);
    }

    /**
     * @return
     */
    public String getThisHost() {
       return getValue(THISHOST);
    }

     /**
     * @return
     */
    public String getRemoteHost() {
        return getValue(REMOTEHOST);
    }
    /**
     * @return
     */
    public String getMessageId() {
        return getValue(MESSAGE);
    }

    /**
     * @return
     */
    public String getAction() {
        return getValue(ACTION);
    }
    
    /**
     * @return
     */
    public String getPid() {
        return getValue(PID);
    }

    /**
     * @return
     */
    public String getReplyAddress() {
        return getValue(REPLY);
    }

    /**
     * @return
     */
    public String getTo() {
        return getValue(TO);
    }

    /**
     * @return
     */
    public String getFrom() {
        return getValue(FROM);
    }

    /**
     * @return
     */
    public String getRelatesTo() {
        return getValue(RELATESTO);
    }

    /**
     * @param value
     * @param type
     */
    private void setValue(String value, String type) {
        Map<String, String> data = null;
        if (threadMap.containsKey(threadId)) {
            data = threadMap.get(threadId);
        } else {
            data = new HashMap<String, String>();
            threadMap.put(threadId, data);
        }
        data.put(type, value);
    }

    /**
     * @param type
     * @return
     */
    private String getValue(String type) {
        String ret = null;

        Map<String, String> data = threadMap.get(threadId);
        if (data != null) {
            ret = (String) data.get(type);
        }
        return ret;
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
