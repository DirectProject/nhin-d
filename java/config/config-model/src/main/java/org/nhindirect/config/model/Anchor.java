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
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;

import org.codehaus.enunciate.json.JsonRootType;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.nhindirect.config.model.exceptions.CertificateConversionException;


/**
 * A trust anchor used for discerning trust of an end entity certificates.
 * @author Greg Meyer
 * @since 1.0
 */
///CLOVER:OFF
@JsonRootType
public class Anchor 
{
    private String owner;
    private String thumbprint;
    private long certificateId;
    private byte[] certificateData;
    private long id;
    private Calendar createTime;
    private Calendar validStartDate;
    private Calendar validEndDate;
    private EntityStatus status;
    private boolean incoming;
    private boolean outgoing;
    
    /**
     * Empty constructor.
     */
    public Anchor()
    {
    	
    }

    /**
     * Gets the owner of the anchor.  The owner is domain name that this anchor is associate with.
     * @return The owner of the anchor.
     */
	public String getOwner() 
	{
		return owner;
	}

	/**
	 * Sets the owner of the anchor.
	 * @param owner The owner of the anchor.
	 */
	public void setOwner(String owner) 
	{
		this.owner = owner;
	}

	/**
	 * Gets the thumbprint of the anchor.  A thumbprint is a SHA-1 has of the DER encoding of the anchor.
	 * @return The thumbprint of the anchor.
	 */
	public String getThumbprint() 
	{
		return thumbprint;
	}

	/**
	 * Sets the thumbprint of the anchor.
	 * @param thumbprint The thumbprint of the anchor.
	 */
	public void setThumbprint(String thumbprint) 
	{
		this.thumbprint = thumbprint;
	}

	/**
	 * Gets the internal id of the anchor.
	 * @return The interal id of the anchor.
	 */
	public long getCertificateId() 
	{
		return certificateId;
	}

	/**
	 * Sets the internal id of the anchor.
	 * @param certificateId The internal id of the anchor.
	 */
	public void setCertificateId(long certificateId) 
	{
		this.certificateId = certificateId;
	}

	/**
	 * Gets the DER encoded data of the anchor.
	 * @return The DER encoded data of the anchor.
	 */
	public byte[] getCertificateData() 
	{
		return certificateData;
	}

	/**
	 * Sets the DER encoded data of the anchor.
	 * @param certificateData The DER encoded data of the anchor.
	 */
	public void setCertificateData(byte[] certificateData) 
	{
		this.certificateData = certificateData;
	}

	/**
	 * Gets the internal id of the anchor.
	 * @return The interal id of the anchor.
	 */
	public long getId() 
	{
		return id;
	}

	/**
	 * Sets the internal id of the anchor.
	 * @param certificateId The internal id of the anchor.
	 */
	public void setId(long id) 
	{
		this.id = id;
	}

	/**
	 * Gets the date/time that the anchor was added to the system.
	 * @return The date/time that the anchor was added to the system.
	 */
	public Calendar getCreateTime() 
	{
		return createTime;
	}

	/**
	 * Sets the date/time that the anchor was added to the system.
	 * @param createTime The date/time that the anchor was added to the system.
	 */
	public void setCreateTime(Calendar createTime) 
	{
		this.createTime = createTime;
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

	/**
	 * Gets the status of the anchor.
	 * @return The status of the anchor.
	 */
	public EntityStatus getStatus() 
	{
		return status;
	}

	/**
	 * Sets the status of the anchor.
	 * @param status The status of the anchor.
	 */
	public void setStatus(EntityStatus status) 
	{
		this.status = status;
	}

	/**
	 * Determines if the anchor is valid for incoming messages.
	 * @return True if the anchor can be use for incoming messages; false otherwise.
	 */
	public boolean isIncoming() 
	{
		return incoming;
	}

	/**
	 * Sets if the anchor is valid for incoming messages.
	 * @param incoming True if the anchor can be use for incoming messages; false otherwise.
	 */
	public void setIncoming(boolean incoming) 
	{
		this.incoming = incoming;
	}

	/**
	 * Determines if the anchor is valid for outgoing messages.
	 * @return True if the anchor can be use for outgoing messages; false otherwise.
	 */
	public boolean isOutgoing() 
	{
		return outgoing;
	}

	/**
	 * Sets if the outgoing is valid for outgoing messages.
	 * @param outgoing True if the anchor can be use for outgoing messages; false otherwise.
	 */
	public void setOutgoing(boolean outgoing) 
	{
		this.outgoing = outgoing;
	}
    
	@JsonIgnore
	/**
	 * The returned value is derived from the internal byte stream representation.  This attribute is suppressed during JSON conversion.
	 */
	public X509Certificate getAnchorAsX509Certificate()
	{
        final InputStream bais = new ByteArrayInputStream(certificateData);

        try
        {
        	return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);
        } 
        catch (CertificateException e) 
        {
        	throw new CertificateConversionException(e);
		}
        finally
        {
        	try {bais.close();} catch (IOException ex) {}
        }
	}	
}
///CLOVER:ON
