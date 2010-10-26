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

package org.nhindirect.nhindclient.config;

/**
 * Configuration for the NHINDClient.
 * 
 * @author beau
 */
public class NHINDClientConfig
{
    private String smtpHostName;
    private String smtpAuthUser;
    private String smtpAuthPassword;

    private String configServireUrl;
    
    /**
     * Construct a new NHINDClientConfig object.
     * 
     * @param smtpHostName
     *            The SMTP host name.
     * @param smtpAuthUser
     *            The SMTP auth user.
     * @param smtpAuthPassword
     *            The SMTP auth password.
     */
    public NHINDClientConfig(String smtpHostName, String smtpAuthUser, String smtpAuthPassword)
    {
        this.smtpHostName = smtpHostName;
        this.smtpAuthUser = smtpAuthUser;
        this.smtpAuthPassword = smtpAuthPassword;
    }
    
    /**
     * Construct a new NHINDClientConfig object.
     * 
     * @param smtpHostName
     *            The SMTP host name.
     * @param smtpAuthUser
     *            The SMTP auth user.
     * @param smtpAuthPassword
     *            The SMTP auth password.
     * @param configServiceUrl
     *            The configuration service URL
     */
    public NHINDClientConfig(String smtpHostName, String smtpAuthUser, String smtpAuthPassword, String configServiceUrl)
    {
        this(smtpHostName, smtpAuthUser, smtpAuthPassword);
        
        this.configServireUrl = configServiceUrl;
    }

    /**
     * Get the value of smtpHostName.
     * 
     * @return the smtpHostName.
     */
    public String getSmtpHostName()
    {
        return smtpHostName;
    }

    /**
     * Set the value of smtpHostName.
     * 
     * @param smtpHostName
     *            The smtpHostName to set.
     */
    public void setSmtpHostName(String smtpHostName)
    {
        this.smtpHostName = smtpHostName;
    }

    /**
     * Get the value of smtpAuthUser.
     * 
     * @return the smtpAuthUser.
     */
    public String getSmtpAuthUser()
    {
        return smtpAuthUser;
    }

    /**
     * Set the value of smtpAuthUser.
     * 
     * @param smtpAuthUser
     *            The smtpAuthUser to set.
     */
    public void setSmtpAuthUser(String smtpAuthUser)
    {
        this.smtpAuthUser = smtpAuthUser;
    }

    /**
     * Get the value of smtpAuthPassword.
     * 
     * @return the smtpAuthPassword.
     */
    public String getSmtpAuthPassword()
    {
        return smtpAuthPassword;
    }

    /**
     * Set the value of smtpAuthPassword.
     * 
     * @param smtpAuthPassword
     *            The smtpAuthPassword to set.
     */
    public void setSmtpAuthPassword(String smtpAuthPassword)
    {
        this.smtpAuthPassword = smtpAuthPassword;
    }

    /**
     * Get the value of configServiceUrl.
     * 
     * @return the configServireUrl.
     */
    public String getConfigServireUrl()
    {
        return configServireUrl;
    }

    /**
     * Set the value of configServiceUrl.
     * 
     * @param configServireUrl
     *            The configServireUrl to set.
     */
    public void setConfigServireUrl(String configServireUrl)
    {
        this.configServireUrl = configServireUrl;
    }

}
