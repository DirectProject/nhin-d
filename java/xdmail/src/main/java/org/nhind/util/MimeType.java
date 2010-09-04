package org.nhind.util;

import org.apache.commons.lang.StringUtils;

public enum MimeType {

    TEXT_PLAIN("text/plain"),
    TEXT_XML("text/xml"),
    TEXT_CDA_XML("text/cda+xml"),
    APPLICATION_CCR("application/ccr"),
    APPLICATION_XML("application/xml"),
    APPLICATION_PDF("application/pdf");

    private String s1;
    
    private MimeType(String s1) {
        this.s1 = s1;

    }
    
    public boolean matches(String s1) {
        return StringUtils.startsWith(s1, this.s1);
    }

    /**
     * @return the s1
     */
    public String getS1() {
        return s1;
    }

    /**
     * @param s1
     *            the s1 to set
     */
    public void setS1(String s1) {
        this.s1 = s1;
    }

}
