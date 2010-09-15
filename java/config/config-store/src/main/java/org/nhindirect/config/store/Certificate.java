package org.nhindirect.config.store;
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

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Entity
@Table(name="certificate")
/**
 * The JPA Certificate class
 */

public class Certificate {
	
	private static final Log log = LogFactory.getLog(Certificate.class);
	
	public static final byte[] NULL_CERT = new byte[] {};
	
	private String owner;
	private String thumbprint;
	private long   id = 0L;
	private byte[] data;	
	private Calendar createTime;
	private Calendar validStartDate;
	private Calendar validEndDate;
	private EntityStatus status;
	
	@Column(name="owner")
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Column(name="certificateData")
	@Lob
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) throws CertificateException {
		this.data = data;
		if (data == NULL_CERT) {
			setThumbprint("");
		}
		else {
			loadCertFromData();
		}
	}

	private void setThumbprint(String aThumbprint) {
		thumbprint = aThumbprint;
		
	}

	@Column(name="thumbprint")
	public String getThumbprint() {
		return thumbprint;
	}

	@Id
	@Column(name="id",nullable=false)
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name="createTime")
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Calendar timestamp) {
		createTime = timestamp;
	}
	
	@Column(name="status")
	@Enumerated
	public EntityStatus getStatus() {
		return status;
	}

	public void setStatus(EntityStatus status) {
		this.status = status;
	}

	@Column(name="validStartDate")
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getValidStartDate() {
		return validStartDate;
	}

	public void setValidStartDate(Calendar validStartDatee) {
		this.validStartDate = validStartDatee;
	}

	@Column(name="validEndDate")
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getValidEndDate() {
		return validEndDate;
	}

	public void setValidEndDate(Calendar validEndDate) {
		this.validEndDate = validEndDate;
	}

	
	public void validate() throws CertificateException {
		if (!hasData()) {
			throw new CertificateException("Invalid Certificate: no certificate data exists");
		}
	}
	                             
    private boolean hasData() {
    	return ((data != null) && (data.equals(NULL_CERT))) ? true : false;
    }
    
	public void clearData() {
		try {
			setData(NULL_CERT);
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	private void loadCertFromData() throws CertificateException {
		X509Certificate cert = null;
		try {
			validate();
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			cert = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(bais);
			setThumbprint(Thumbprint.toThumbprint(cert).toString());
			bais.close();
		}
		catch (Exception e) {
			setData(NULL_CERT);
			throw new CertificateException("Data cannot be converted to a valid X.509 Certificate", e);
		}
	}
}
