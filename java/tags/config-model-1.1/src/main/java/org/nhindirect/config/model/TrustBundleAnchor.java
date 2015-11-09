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
import java.util.Calendar;

import org.apache.commons.io.IOUtils;
import org.codehaus.enunciate.json.JsonRootType;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.nhindirect.config.model.exceptions.CertificateConversionException;

/**
 * An trust anchor within a trust bundle.
 * @author Greg Meyer
 * @since 1.0
 */
///CLOVER:OFF
@JsonRootType
public class TrustBundleAnchor 
{
    private long id;
    private byte[] anchorData;
    private String thumbprint;
    private Calendar validStartDate;
    private Calendar validEndDate;
    
    /**
     * Empty constructor.
     */
    public TrustBundleAnchor()
    {
    	
    }

    /**
     * Gets the internal system id of the trust bundle anchor.
     * @return The internal system id of the trust bundle anchor.
     */
	public long getId() 
	{
		return id;
	}

	/**
	 * Sets the internal system id of the trust bundle anchor.
	 * @param id The internal system id of the trust bundle anchor.
	 */
	public void setId(long id) 
	{
		this.id = id;
	}

	/**
	 * Gets the DER encoded data of the anchor.
	 * @return The DER encoded data of the anchor.
	 */
	public byte[] getAnchorData() 
	{
		return anchorData;
	}

	/**
	 * Sets the DER encoded data of the anchor.
	 * @param anchorData the DER encoded data of the anchor.
	 */
	public void setAnchorData(byte[] anchorData) 
	{
		this.anchorData = anchorData;
	}

	/**
	 * Gets the thumb print of the anchor.  The thumb print consists of a SHA-1 hash of the anchors DER encoding.
	 * @return The thumb print of the anchor. 
	 */
	public String getThumbprint() 
	{
		return thumbprint;
	}

	/**
	 * Sets the thumb print of the anchor. 
	 * @param thumbprint The thumb print of the anchor. 
	 */
	public void setThumbprint(String thumbprint) 
	{
		this.thumbprint = thumbprint;
	}

	/**
	 * Gets the valid from date/time of the anchor.
	 * @return The valid from date/time of the anchor.
	 */
	public Calendar getValidStartDate() 
	{
		return validStartDate;
	}

	/** 
	 * Sets the valid from date/time of the anchor.
	 * @param validStartDate The valid from date/time of the anchor.
	 */
	public void setValidStartDate(Calendar validStartDate) 
	{
		this.validStartDate = validStartDate;
	}

	/**
	 * Gets the valid until date/time of the anchor.
	 * @return The valid until date/time of the anchor.
	 */
	public Calendar getValidEndDate() 
	{
		return validEndDate;
	}

	/**
	 * Sets the valid until date/time of the anchor.
	 * @param validEndDate The valid until date/time of the anchor.
	 */
	public void setValidEndDate(Calendar validEndDate) 
	{
		this.validEndDate = validEndDate;
	}
    
	@JsonIgnore
	/**
	 * The returned value is derived from the internal byte stream representation.  This attribute is suppressed during JSON conversion.
	 */
	public X509Certificate getAsX509Certificate()
	{
		
		if (anchorData == null || anchorData.length == 0)
			return null;
		
		ByteArrayInputStream bais = null;
        try 
        {
            bais = new ByteArrayInputStream(anchorData);
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
