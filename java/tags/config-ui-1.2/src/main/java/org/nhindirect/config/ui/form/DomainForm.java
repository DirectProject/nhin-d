package org.nhindirect.config.ui.form;
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
import java.util.Collection;

import org.hibernate.validator.constraints.NotEmpty;
import org.nhindirect.config.store.Address;
import org.nhindirect.config.store.Domain;
import org.nhindirect.config.store.EntityStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class DomainForm {

	private Domain domain;
	
	public Collection<Address> getAddresses() {
		return domain.getAddresses();
	}
	
	public void setAddresses(Collection<Address> addresses) {
		domain.setAddresses(addresses);
	}
	
	public DomainForm() {
		domain = new Domain();
	}

	public String getPostmasterEmail() {
		return domain.getPostMasterEmail();
	}

	public void setPostmasterEmail(String postmasterEmail) {
		domain.setPostMasterEmail(postmasterEmail);
	}
	
	public Long getPostmasterEmailAddressId() {
		return domain.getPostmasterAddressId();
	}
	
	public void setPostmasterEmailAddressId(Long anId) {
		domain.setPostmasterAddressId(anId);
	}
	
	public void populate(Domain domain) {
		this.domain = domain;
	}
	
	public Domain getDomainFromForm() {
		return domain;
	}

	public Long getId() {
		return domain.getId();
	}

	public void setId(Long anId) {
		domain.setId(anId);
	}

	public String getDomainName() {
		return domain.getDomainName();
	}

	
	public Calendar getCreateTime() {
		return domain.getCreateTime();
	}

	public Calendar getUpdateTime() {
		return domain.getUpdateTime();
	}

	public EntityStatus getStatus() {
		return domain.getStatus();
	}

	public void setDomainName(String aName) {
		domain.setDomainName(aName);
	}

	public void setCreateTime(Calendar timestamp) {
		domain.setCreateTime(timestamp);
	}

	public void setUpdateTime(Calendar timestamp) {
		domain.setUpdateTime(timestamp);
	}

	public void setStatus(EntityStatus aStatus) {
		domain.setStatus(aStatus);
	}

	public boolean isValid() {
		return domain.isValid();
	}
	
}
