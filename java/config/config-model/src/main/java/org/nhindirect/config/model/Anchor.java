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
    
    public Anchor()
    {
    	
    }

	public String getOwner() 
	{
		return owner;
	}

	public void setOwner(String owner) 
	{
		this.owner = owner;
	}

	public String getThumbprint() 
	{
		return thumbprint;
	}

	public void setThumbprint(String thumbprint) 
	{
		this.thumbprint = thumbprint;
	}

	public long getCertificateId() 
	{
		return certificateId;
	}

	public void setCertificateId(long certificateId) 
	{
		this.certificateId = certificateId;
	}

	public byte[] getCertificateData() 
	{
		return certificateData;
	}

	public void setCertificateData(byte[] certificateData) 
	{
		this.certificateData = certificateData;
	}

	public long getId() 
	{
		return id;
	}

	public void setId(long id) 
	{
		this.id = id;
	}

	public Calendar getCreateTime() 
	{
		return createTime;
	}

	public void setCreateTime(Calendar createTime) 
	{
		this.createTime = createTime;
	}

	public Calendar getValidStartDate() 
	{
		return validStartDate;
	}

	public void setValidStartDate(Calendar validStartDate) 
	{
		this.validStartDate = validStartDate;
	}

	public Calendar getValidEndDate() 
	{
		return validEndDate;
	}

	public void setValidEndDate(Calendar validEndDate) 
	{
		this.validEndDate = validEndDate;
	}

	public EntityStatus getStatus() 
	{
		return status;
	}

	public void setStatus(EntityStatus status) 
	{
		this.status = status;
	}

	public boolean isIncoming() 
	{
		return incoming;
	}

	public void setIncoming(boolean incoming) 
	{
		this.incoming = incoming;
	}

	public boolean isOutgoing() 
	{
		return outgoing;
	}

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
