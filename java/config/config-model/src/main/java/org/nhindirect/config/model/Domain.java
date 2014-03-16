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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

import org.codehaus.enunciate.json.JsonRootType;

/**
 * A Direct email domain.
 * @author Greg Meyer
 * @since 1.0
 */
///CLOVER:OFF
@JsonRootType
public class Domain 
{
    private String domainName;

    private Calendar createTime;

    private Calendar updateTime;

    private Address postmasterAddress;

    private Collection<Address> addresses;

    private long id;

    private EntityStatus status;
    
    /**
     * Empty constructor
     */
    public Domain()
    {
    	
    }

    /**
     * Gets the name of the domain.
     * @return The name of the domain.
     */
	public String getDomainName() 
	{
		return domainName;
	}

	/**
	 * Sets the name of the domain.
	 * @param domainName The name of the domain.
	 */
	public void setDomainName(String domainName) 
	{
		this.domainName = domainName;
	}

	/**
	 * Sets the date/time the domain was created in the system.
	 * @return The date/time the domain was created in the system.
	 */
	public Calendar getCreateTime() 
	{
		return createTime;
	}

	/**
	 * Sets the date/time the domain was created in the system.
	 * @param createTime The date/time the domain was created in the system.
	 */ 
	public void setCreateTime(Calendar createTime) 
	{
		this.createTime = createTime;
	}

	/**
	 * Gets the date/time that the domain was last updated.
	 * @return The date/time that the domain was last updated.
	 */
	public Calendar getUpdateTime() 
	{
		return updateTime;
	}

	/**
	 * Sets the date/time that the domain was last updated.
	 * @param updateTime The date/time that the domain was last updated.
	 */
	public void setUpdateTime(Calendar updateTime) 
	{
		this.updateTime = updateTime;
	}

	/**
	 * Gets the email address of the domain's postmaster.
	 * @return The email address of the domain's postmaster.
	 */
	public Address getPostmasterAddress() 
	{
		return postmasterAddress;
	}

	/**
	 * Sets the email address of the domain's postmaster.
	 * @param postmasterAddress The email address of the domain's postmaster.
	 */
	public void setPostmasterAddress(Address postmasterAddress) 
	{
		this.postmasterAddress = postmasterAddress;
	}

	/**
	 * Gets all addresses that are configured for a domain.
	 * @return All addresses that are configured for a domain.
	 */
	public Collection<Address> getAddresses() 
	{
		if (addresses == null)
			addresses = Collections.emptyList();
		
		return Collections.unmodifiableCollection(addresses);
	}

	/**
	 * Sets all addresses that are configured for a domain.
	 * @param addresses All addresses that are configured for a domain.
	 */
	public void setAddresses(Collection<Address> addresses) 
	{
		this.addresses = new ArrayList<Address>(addresses);
	}

	/**
	 * Gets the internal system id of the domain.
	 * @return The internal system id of the domain.
	 */
	public long getId() 
	{
		return id;
	}

	/**
	 * Sets the internal system id of the domain.
	 * @param id The internal system id of the domain.
	 */
	public void setId(long id) 
	{
		this.id = id;
	}

	/**
	 * Gets the status of the domain.
	 * @return The status of the domain.
	 */
	public EntityStatus getStatus() 
	{
		return status;
	}

	/**
	 * Sets the status of the domain.
	 * @param status The status of the domain.
	 */
	public void setStatus(EntityStatus status) 
	{
		this.status = status;
	}
    
    
}
///CLOVER:ON
