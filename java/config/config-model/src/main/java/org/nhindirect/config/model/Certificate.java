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

import java.util.Calendar;

import org.codehaus.enunciate.json.JsonRootType;

/**
 * An end entity X509 certificate.
 * @author Greg Meyer
 * @since 1.0
 */
///CLOVER:OFF
@JsonRootType
public class Certificate 
{
    private String owner;
    private String thumbprint;
    private long id;
    private byte[] data;
    private Calendar createTime;
    private Calendar validStartDate;
    private Calendar validEndDate;
    private EntityStatus status;
    private boolean privateKey;
    
    /**
     * Empty contstructor.
     */
    public Certificate()
    {
    	
    }

    /**
     * Gets the owner of the certificate.  The owner is determined by the email address or domain that this certificate is bound to.
     * @return The owner of the certificate.
     */
	public String getOwner() 
	{
		return owner;
	}

	/**
	 * Sets the owner of the certificate.
	 * @param owner The owner of the certificate
	 */
	public void setOwner(String owner) 
	{
		this.owner = owner;
	}

	/**
	 * Gets the thumb print of the certificate.  The thumb print is a SHA-1 hash of the certificates DER encoded data.
	 * @return The thumb print of the certificate.
	 */
	public String getThumbprint() 
	{
		return thumbprint;
	}

	/**
	 * Sets the thumb print of the certificate. 
	 * @param thumbprint The thumb print of the certificate.
	 */
	public void setThumbprint(String thumbprint) 
	{
		this.thumbprint = thumbprint;
	}

	/**
	 * Gets the internal id of the certificate.
	 * @return The internal id of the certificate.
	 */
	public long getId() 
	{
		return id;
	}

	/**
	 * Sets the internal id of the certificate.
	 * @param id The internal id of the certificate.
	 */
	public void setId(long id) 
	{
		this.id = id;
	}

	/**
	 * Get the DER encoded data of the certificate.
	 * @return The DER encoded data of the certificate.
	 */
	public byte[] getData() 
	{
		return data;
	}

	/**
	 * Sets the DER encoded data of the certificate.
	 * @param data The DER encoded data of the certificate.
	 */
	public void setData(byte[] data) 
	{
		this.data = data;
	}

	/**
	 * Gets the date/time that the certificate was added to the system.
	 * @return The date/time that the certificate was added to the system.
	 */
	public Calendar getCreateTime() 
	{
		return createTime;
	}

	/**
	 * Sets the date/time that the certificate was added to the system.
	 * @param createTime The date/time that the certificate was added to the system.
	 */
	public void setCreateTime(Calendar createTime) 
	{
		this.createTime = createTime;
	}

	/**
	 * Gets the valid from date/time of the certificate.
	 * @return The valid from date/time of the certificate.
	 */
	public Calendar getValidStartDate() 
	{
		return validStartDate;
	}

	/** 
	 * Sets the valid from date/time of the certificate.
	 * @param validStartDate The valid from date/time of the certificate.
	 */
	public void setValidStartDate(Calendar validStartDate) 
	{
		this.validStartDate = validStartDate;
	}

	/**
	 * Gets the valid until date/time of the certificate.
	 * @return The valid until date/time of the certificate.
	 */
	public Calendar getValidEndDate() 
	{
		return validEndDate;
	}

	/**
	 * Sets the valid until date/time of the certificate.
	 * @param validEndDate The valid until date/time of the certificate.
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
	 * Indicates if the certificate's private key is present.
	 * @return True if the certificate's private key is present.  False otherwise.
	 */
	public boolean isPrivateKey() 
	{
		return privateKey;
	}

	/**
	 * Indicates if the certificate's private key is present.
	 * @param privateKey True if the certificate's private key is present.  False otherwise.
	 */
	public void setPrivateKey(boolean privateKey) 
	{
		this.privateKey = privateKey;
	}
}
///CLOVER:ON
