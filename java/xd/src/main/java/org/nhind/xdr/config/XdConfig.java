package org.nhind.xdr.config;

import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.Setting;

public class XdConfig
{
    private enum Keys
    {
        MAIL_HOST("xd.MailHost"), 
        MAIL_USER("xd.MailUser"), 
        MAIL_PASS("xd.MailPass"), 
        AUDIT_METHOD("xd.AuditMethod"), 
        AUDIT_HOST("xd.AuditHost"), 
        AUDIT_PORT("xd.AuditPort"), 
        AUDIT_FILE("xd.AuditFile");

        private String key;

        private Keys(String key)
        {
            this.key = key;
        }

        public String getKey()
        {
            return key;
        }
    }

    private String mailHost;
    private String mailUser;
    private String mailPass;
    private String auditMethod;
    private String auditHost;
    private String auditPort;
    private String auditFile;

    private ConfigurationServiceProxy proxy;

    public XdConfig()
    {
        this.init();
    }
    
    public XdConfig(String endpoint)
    {
        this.proxy = new ConfigurationServiceProxy(endpoint);
        this.init();
    }

    public XdConfig(ConfigurationServiceProxy proxy)
    {
        this.proxy = proxy;
        this.init();
    }

    public void init()
    {
        if (proxy == null)
            return;

        Setting tmp;

        try
        {
            tmp = proxy.getSettingByName(Keys.MAIL_HOST.getKey());
            if (tmp != null)
                mailHost = tmp.getValue();

            tmp = proxy.getSettingByName(Keys.MAIL_USER.getKey());
            if (tmp != null)
                mailUser = tmp.getValue();

            tmp = proxy.getSettingByName(Keys.MAIL_PASS.getKey());
            if (tmp != null)
                mailPass = tmp.getValue();

            tmp = proxy.getSettingByName(Keys.AUDIT_METHOD.getKey());
            if (tmp != null)
                auditMethod = tmp.getValue();

            tmp = proxy.getSettingByName(Keys.AUDIT_HOST.getKey());
            if (tmp != null)
                auditHost = tmp.getValue();

            tmp = proxy.getSettingByName(Keys.AUDIT_PORT.getKey());
            if (tmp != null)
                auditPort = tmp.getValue();

            tmp = proxy.getSettingByName(Keys.AUDIT_FILE.getKey());
            if (tmp != null)
                auditFile = tmp.getValue();
        }
        catch (Exception e)
        {

        }
    }

    /**
     * @return the mailHost
     */
    public String getMailHost()
    {
        return mailHost;
    }

    /**
     * @param mailHost
     *            the mailHost to set
     */
    public void setMailHost(String mailHost)
    {
        this.mailHost = mailHost;
    }

    /**
     * @return the mailUser
     */
    public String getMailUser()
    {
        return mailUser;
    }

    /**
     * @param mailUser
     *            the mailUser to set
     */
    public void setMailUser(String mailUser)
    {
        this.mailUser = mailUser;
    }

    /**
     * @return the mailPass
     */
    public String getMailPass()
    {
        return mailPass;
    }

    /**
     * @param mailPass
     *            the mailPass to set
     */
    public void setMailPass(String mailPass)
    {
        this.mailPass = mailPass;
    }

    /**
     * @return the auditMethod
     */
    public String getAuditMethod()
    {
        return auditMethod;
    }

    /**
     * @param auditMethod
     *            the auditMethod to set
     */
    public void setAuditMethod(String auditMethod)
    {
        this.auditMethod = auditMethod;
    }

    /**
     * @return the auditHost
     */
    public String getAuditHost()
    {
        return auditHost;
    }

    /**
     * @param auditHost
     *            the auditHost to set
     */
    public void setAuditHost(String auditHost)
    {
        this.auditHost = auditHost;
    }

    /**
     * @return the auditPort
     */
    public String getAuditPort()
    {
        return auditPort;
    }

    /**
     * @param auditPort
     *            the auditPort to set
     */
    public void setAuditPort(String auditPort)
    {
        this.auditPort = auditPort;
    }

    /**
     * @return the auditFile
     */
    public String getAuditFile()
    {
        return auditFile;
    }

    /**
     * @param auditFile
     *            the auditFile to set
     */
    public void setAuditFile(String auditFile)
    {
        this.auditFile = auditFile;
    }

    /**
     * @return the proxy
     */
    public ConfigurationServiceProxy getProxy()
    {
        return proxy;
    }

    /**
     * @param proxy
     *            the proxy to set
     */
    public void setProxy(ConfigurationServiceProxy proxy)
    {
        this.proxy = proxy;
    }
}
