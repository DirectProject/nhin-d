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

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="address")
/**
 * The JPA Address class
 */
public class Address {
	
	private String emailAddress;
	
	private long id;
	
	private Domain domain;
	
	private String displayName;
	
	private Calendar createTime;

	private Calendar updateTime;

	private EntityStatus status;
	
	private String type;
	
    public Address()
    {
    }
     
    public Address(Domain aDomain, String anAddress)
    {
    	 setDomain(aDomain);
         setEmailAddress(anAddress);
         setDisplayName("");
         setCreateTime(Calendar.getInstance());
         setUpdateTime(Calendar.getInstance());
         setStatus(EntityStatus.NEW);
    }
    
    public Address(Domain aDomain, String anAddress, String aName)
    {
        setDomain(aDomain);
        setEmailAddress(anAddress);
        setDisplayName(aName);
        setCreateTime(Calendar.getInstance());
        setUpdateTime(Calendar.getInstance());
        setStatus(EntityStatus.NEW);
    }
    
    public Address(Address anAddress)
    {
    	if (anAddress != null)
    	{
	    	setDomain(anAddress.getDomain());
	    	setEmailAddress(anAddress.getEmailAddress());
	    	setDisplayName(anAddress.getDisplayName());
	    	setCreateTime(anAddress.getCreateTime());
	    	setUpdateTime(anAddress.getUpdateTime());
	    	setStatus(anAddress.getStatus());
	    	setType(anAddress.getType());
    	}
    }
    
    @Column(name="emailaddress",length=400)
	public String getEmailAddress() {
		return emailAddress; 
	}

	public void setEmailAddress(String anEmail) {
		emailAddress = anEmail;
	}

	@Column(name="id",nullable=false)
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn(name="domainId")
	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain anId) {
		domain = anId;
		
	}

	@Column(name="displayname",length=100)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String aName) {
		displayName = aName;	
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Calendar timestamp) {
		createTime = timestamp;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Calendar timestamp) {
		updateTime = timestamp;
	}

	@Column(name="status")
	@Enumerated
	public EntityStatus getStatus() {
		return status;
	}


	public void setStatus(EntityStatus aStatus) {
		status = aStatus;
	}

	@Column(name="type",length=64)
	public String getType() {
		return type;
	}

	public void setType(String aType) {
		type = aType;
	}
	
	@Override
	public String toString() {
		return "[ID: " + getId() +
			   " | Address: " + getEmailAddress() +
		       " | For: "    + getDisplayName() +
		       " | Domain: " + getDomain().getDomainName() + "]";
	}

}
