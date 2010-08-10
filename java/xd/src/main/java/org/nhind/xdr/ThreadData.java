/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nhind.xdr;

import java.util.HashMap;

/**
 *
 * @author Vince
 */
public class ThreadData {

    public final String MESSAGE = "message";
    public final String ACTION = "action";
    public final String REPLY = "reply";
    public final String TO = "to";
    public final String RELATESTO = "relatesto";
    public final String THISHOST = "thishost";
    public final String REMOTEHOST = "remotehost";
    public final String PID = "pid";
    public final String FROM = "from";
    private static HashMap<Long, HashMap> threadMap = new HashMap();
    private Long threadId = null;

    public ThreadData(Long id) {
        this.threadId = id;
    }

    public void clean(Long id) {
        threadMap.remove(id);
    }

    public void setMessageId(String value) {
        setValue(value, MESSAGE);
    }

     public void setFrom(String value) {
        setValue(value,FROM);
    }
    public void setThisHost(String value) {
        setValue(value, THISHOST);
    }
    public void setRemoteHost(String value) {
        setValue(value, REMOTEHOST);
    }

    public void setAction(String value) {
        setValue(value, ACTION);
    }

    public void setPid(String value) {
        setValue(value, PID);
    }

    public void setReplyAddress(String value) {
        setValue(value, REPLY);
    }

    public void setTo(String value) {
        setValue(value, TO);
    }

    public void setRelatesTo(String value) {
        setValue(value, RELATESTO);
    }

    public String getThisHost() {
       return getValue(THISHOST);
    }

     public String getRemoteHost() {
        return getValue(REMOTEHOST);
    }
    public String getMessageId() {
        return getValue(MESSAGE);
    }

    public String getAction() {
        return getValue(ACTION);
    }
      public String getPid() {
        return getValue(PID);
    }

    public String getReplyAddress() {
        return getValue(REPLY);
    }

    public String getTo() {
        return getValue(TO);
    }

    public String getFrom() {
        return getValue(FROM);
    }

    public String getRelatesTo() {
        return getValue(RELATESTO);
    }

    private void setValue(String value, String type) {
        HashMap data = null;
        if (threadMap.containsKey(threadId)) {
            data = threadMap.get(threadId);
        } else {
            data = new HashMap();
            threadMap.put(threadId, data);
        }
        data.put(type, value);
    }

    private String getValue(String type) {
        String ret = null;

        HashMap data = threadMap.get(threadId);
        if (data != null) {
            ret = (String) data.get(type);
        }
        return ret;
    }
}
