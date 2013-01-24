package com.gsihealth.auditclient.type;

/**
 * Enumeration of valid audit methods.
 * 
 * @author beau
 */
public enum AuditMethodEnum
{
    FILE("file"), 
    SYSLOG("syslog");

    private String method;

    private AuditMethodEnum(String method)
    {
        this.method = method;
    }

    /**
     * Return the value of method.
     * 
     * @return the value of method.
     */
    public String getMethod()
    {
        return method;
    }
}
