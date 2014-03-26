/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
   in the documentation and/or other materials provided with the distribution.  
3. Neither the name of the The NHIN Direct Project (nhindirect.org) nor the names of its contributors may be used to endorse or promote 
   products derived from this software without specific prior written permission.
   
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nhindirect.config.model;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.codehaus.enunciate.json.JsonRootType;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.nhindirect.config.model.exceptions.CertificateConversionException;


/**
 * A Direct trust bundle.
 * @author Greg Meyer
 * @since 1.0
 */
///CLOVER:OFF
@JsonRootType
public class TrustBundle
{
	
	private long id;
	private String bundleName;
	private String bundleURL;
    private byte[] signingCertificateData;
    private Collection<TrustBundleAnchor> trustBundleAnchors;
    private int refreshInterval;
    private Calendar lastRefreshAttempt;
    private BundleRefreshError lastRefreshError;
    private Calendar lastSuccessfulRefresh;    
    private Calendar createTime;  
    private String checkSum;
	
    /**
     * Empty constructor
     */
    public TrustBundle()
    {
    	
    }
    
    /**
     * Gets the internal system id of the trust bundle.
     * @return The internal system id of the trust bundle.
     */
    public long getId() 
    {
		return id;
	}
    
    /**
     * Sets the internal system id of the trust bundle.
     * @param the internal system id of the trust bundle.
     */
	public void setId(long id) 
	{
		this.id = id;
	}
	
	/**
	 * Gets the name of the bundle.
	 * @return The name of the bundle.
	 */
	public String getBundleName() 
	{
		return bundleName;
	}
	
	/**
	 * Sets the name of the bundle.
	 * @param bundleName The name of the bundle.
	 */
	public void setBundleName(String bundleName) 
	{
		this.bundleName = bundleName;
	}
	
	/**
	 * Gets the URL location of the bundle.
	 * @return The URL location of the bundle.
	 */
	public String getBundleURL() 
	{
		return bundleURL;
	}
	
	/**
	 * Sets the URL location of the bundle.
	 * @param bundleURL The URL location of the bundle.
	 */
	public void setBundleURL(String bundleURL) 
	{
		this.bundleURL = bundleURL;
	}
	
	/**
	 * Gets the DER encoded data of the X509 certificate that signed the bundle.
	 * @return The DER encoded data of the X509 certificate that signed the bundle.
	 */
	public byte[] getSigningCertificateData() 
	{
		return signingCertificateData;
	}
	
	/**
	 * Sets the DER encoded data of the X509 certificate that signed the bundle.
	 * @param signingCertificateData The DER encoded data of the X509 certificate that signed the bundle.
	 */
	public void setSigningCertificateData(byte[] signingCertificateData) 
	{
		this.signingCertificateData = signingCertificateData;
	}
	
	/**
	 * Gets the trust anchors in the bundle.
	 * @return The trust anchors in the bundle.
	 */
	public Collection<TrustBundleAnchor> getTrustBundleAnchors() 
	{
		if (trustBundleAnchors == null)
			trustBundleAnchors = Collections.emptyList();
		
		return Collections.unmodifiableCollection(trustBundleAnchors);
	}
	
	/**
	 * Sets the trust anchors in the bundle.
	 * @param trustBundleAnchors The trust anchors in the bundle.
	 */
	public void setTrustBundleAnchors(Collection<TrustBundleAnchor> trustBundleAnchors) 
	{
		this.trustBundleAnchors = new ArrayList<TrustBundleAnchor>(trustBundleAnchors);
	}
	
	/**
	 * Gets the refresh interval for the bundle.
	 * @return The refresh interval for the bundle.
	 */
	public int getRefreshInterval() 
	{
		return refreshInterval;
	}
	
	/**
	 * Sets the refresh interval for the bundle.
	 * @param refreshInterval The refresh interval for the bundle.
	 */
	public void setRefreshInterval(int refreshInterval) 
	{
		this.refreshInterval = refreshInterval;
	}
	
	/**
	 * Gets the date/time of the last time a refresh was attempted.
	 * @return The date/time of the last time a refresh was attempted.
	 */ 
	public Calendar getLastRefreshAttempt() 
	{
		return lastRefreshAttempt;
	}
	
	/**
	 * Sets the date/time of the last time a refresh was attempted.
	 * @param lastRefreshAttempt The date/time of the last time a refresh was attempted.
	 */
	public void setLastRefreshAttempt(Calendar lastRefreshAttempt) 
	{
		this.lastRefreshAttempt = lastRefreshAttempt;
	}
	
	/**
	 * Gets the status of the last refresh attempt.
	 * @return The status of the last refresh attempt.
	 */
	public BundleRefreshError getLastRefreshError() 
	{
		return lastRefreshError;
	}
	
	/**
	 * Sets the status of the last refresh attempt.
	 * @param lastRefreshError The status of the last refresh attempt.
	 */
	public void setLastRefreshError(BundleRefreshError lastRefreshError) 
	{
		this.lastRefreshError = lastRefreshError;
	}
	
	/**
	 * Gets the date/time of the last time the bundle was successfully refreshed.
	 * @return The date/time of the last time the bundle was successfully refreshed.
	 */
	public Calendar getLastSuccessfulRefresh() 
	{
		return lastSuccessfulRefresh;
	}
	
	/**
	 * Sets the date/time of the last time the bundle was successfully refreshed.
	 * @param lastSuccessfulRefresh The date/time of the last time the bundle was successfully refreshed.
	 */
	public void setLastSuccessfulRefresh(Calendar lastSuccessfulRefresh) 
	{
		this.lastSuccessfulRefresh = lastSuccessfulRefresh;
	}
	
	/**
	 * Gets the date/time that bundle was created in the system.
	 * @return The date/time that bundle was created in the system.
	 */
	public Calendar getCreateTime()
	{
		return createTime;
	}
	
	/**
	 * Sets the date/time that bundle was created in the system.
	 * @param createTime The date/time that bundle was created in the system.
	 */
	public void setCreateTime(Calendar createTime) 
	{
		this.createTime = createTime;
	}
	
	/**
	 * Gets the check sum of the bundle.  This consists of a an SHA-1 has of the bundle file.
	 * @return The check sum of the bundle.
	 */
	public String getCheckSum() 
	{
		return checkSum;
	}
	
	/**
	 * Sets the check sum of the bundle.
	 * @param checkSum The check sum of the bundle.
	 */
	public void setCheckSum(String checkSum) 
	{
		this.checkSum = checkSum;
	}
	
	@JsonIgnore
	/**
	 * The returned value is derived from the internal byte stream representation.  This attribute is suppressed during JSON conversion.
	 */
	public X509Certificate getSigningCertificateAsX509Certificate()
	{
		
		if (signingCertificateData == null || signingCertificateData.length == 0)
			return null;
		
		ByteArrayInputStream bais = null;
        try 
        {
            bais = new ByteArrayInputStream(signingCertificateData);
            return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);
        } 
        catch (Exception e) 
        {
            throw new CertificateConversionException("Data cannot be converted to a valid X.509 Certificate", e);
        }
        finally
        {
        	IOUtils.closeQuietly(bais);
        }
	}		
    
}
///CLOVER:ON
