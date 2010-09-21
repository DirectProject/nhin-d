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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.CascadeType;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


@Entity
@Table(name="domain")
/**
 * The JPA Domain class
 */
public class Domain {
	
	private String   domainName;
	
	private Calendar createTime;

	private Calendar updateTime;

	private Long  postmasterAddressId;
	
	private String postmasterAddress;
	
	private Collection<Address> addresses;
	
	private long id;	
	
	private EntityStatus status = EntityStatus.NEW;
	
	public Domain()
    {
    }
    
    public Domain(String aName)
    {
        setDomainName(aName);
        setCreateTime(Calendar.getInstance());
        setUpdateTime(Calendar.getInstance());
        setStatus(EntityStatus.NEW);
    }
    
	@Column(name="id",nullable=false)
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}
	
	public void setId(long anId) {
		id = anId;
	}

	@Column(name="domainName",unique=true)
	public String getDomainName() {
		return domainName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getCreateTime() {
		return createTime;
	}
	
	@Column(name="postmasterAddressId")
	public Long getPostmasterAddressId() {
		return postmasterAddressId;
	}
	
	public void setPostmasterAddressId(Long anId) {
		postmasterAddressId = anId;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getUpdateTime() {
		return updateTime;
	}

	@Column(name="status")
	@Enumerated
	public EntityStatus getStatus() {
		return status;
	}
	

	
	public void setDomainName(String aName) {
		domainName = aName;
	}


	public void setCreateTime(Calendar timestamp) {
			createTime = timestamp;
	}


	public void setUpdateTime(Calendar timestamp) {

			updateTime = timestamp;
	}

	public void setStatus(EntityStatus aStatus) {
		status = aStatus;
	}
	
	/**
	 * If we have an email address id, then search through the collection of addresses to
	 * find an id match and return it.
	 * @return
	 */
	@Transient
	public String getPostMasterEmail() {
		return postmasterAddress;
	}
	
	/**
	 * Add the address unless it already exists, in which case, just set the postmasterEmailAddressId 
	 * appropriately
	 * @param email
	 */
	public void setPostMasterEmail(String email) {
		if (email != null) {
			boolean matched = false;
			
			// Check to see if we've already got the address
			for (Address address : getAddresses()) {
				if (address.getEmailAddress().equals(email)) {
					setPostmasterAddressId(address.getId());
					matched = true;
					break;
				}
			}
			// It's a new address so add it
			if (!matched) {
				Address postmaster = new Address(this, email);
				postmaster.setDisplayName("Post Master");
				postmaster.setStatus(EntityStatus.NEW);
				getAddresses().add(postmaster);
				setPostmasterAddressId(postmaster.getId());
			}
		}
		return;
	}
	
	
	@OneToMany(orphanRemoval=true, fetch=FetchType.EAGER, mappedBy="domain")
	public Collection<Address> getAddresses() {
		if (addresses == null) addresses = new ArrayList<Address>() ;
		return addresses;
	}

	public void setAddresses(Collection<Address> addresses) {
		this.addresses = addresses;
	}

	@Transient
	public boolean isValid() {
		boolean result = false;
		if ((getDomainName() != null) &&
		    (getDomainName().length() > 0) &&
		    ((getStatus().equals(EntityStatus.ENABLED)) ||
		     (getStatus().equals(EntityStatus.DISABLED)) ||
		     ((getStatus().equals(EntityStatus.NEW)) &&
		      (getId() == 0L)))) {
			result = true;
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return "[ID: " + getId() +
			   " | Domain: " + getDomainName() +
			   " | Status: " + getStatus().toString() + "]";
	}
	
	

}
