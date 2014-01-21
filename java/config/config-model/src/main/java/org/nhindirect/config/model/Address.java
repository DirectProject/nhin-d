/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
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


///CLOVER:OFF
/**
 * A configured address or endpoint in the system.
 * @author Greg Meyer
 * @since 1.0
 */
@JsonRootType
public class Address 
{
    private String emailAddress;

    private long id;

    private String displayName;
    
    private String endpoint;

    private Calendar createTime;

    private Calendar updateTime;

    private EntityStatus status;

    private String type;
    
    private String domainName;

    /**
     * Construct an Address.
     */
    public Address() 
    {
    }

    /**
     * Gets the email address associated to the end point.
     * @return The email address associated to the end point.
     */
	public String getEmailAddress() 
	{
		return emailAddress;
	}

	/**
	 * Sets the email address associated to the end point.
	 * @param emailAddress The email address associated to the end point.
	 */
	public void setEmailAddress(String emailAddress) 
	{
		this.emailAddress = emailAddress;
	}

	/**
	 * Gets the internal id of the address.
	 * @return The internal id of the address.
	 */
	public long getId() 
	{
		return id;
	}

	/**
	 * Sets the internal id of the address.
	 * @param id The interal id of the address.
	 */
	public void setId(long id) 
	{
		this.id = id;
	}

	/**
	 * Gets the human readable display of the address.
	 * @return the human readable display of the address.
	 */
	public String getDisplayName() 
	{
		return displayName;
	}

	/**
	 * Sets the human readable display of the address.
	 * @param displayName The human readable display of the address.
	 */
	public void setDisplayName(String displayName) 
	{
		this.displayName = displayName;
	}

    /**
     * Gets the end point; generally an email address, but may also represent other types of end points such as an XD URL.
     * @return The end point name.
     */
	public String getEndpoint() 
	{
		return endpoint;
	}

	/**
	 * Sets the end point.
	 * @param endpoint The end point name.
	 */
	public void setEndpoint(String endpoint) 
	{
		this.endpoint = endpoint;
	}

	/**
	 * Gets the date/time the address was created.
	 * @return The date/time the address was created.
	 */
	public Calendar getCreateTime() 
	{
	
		return createTime;
	}

	/**
	 * Sets the date/time the address was created.
	 * @param createTime The date/time the address was created.
	 */
	public void setCreateTime(Calendar createTime) 
	{
		this.createTime = createTime;
	}

	/**
	 * Gets the date/time of the last update of the address.
	 * @return The date/time of the last update of the address.
	 */
	public Calendar getUpdateTime() 
	{
		return updateTime;
	}

	/**
	 * Sets the date/time of the last update of the address.
	 * @param updateTime The date/time of the last update of the address.
	 */
	public void setUpdateTime(Calendar updateTime) 
	{
		this.updateTime = updateTime;
	}

	/**
	 * Gets the status of the address.
	 * @return The status of the address.
	 */
	public EntityStatus getStatus()
	{
		return status;
	}

	/**
	 * Sets the status of the address.
	 * @param status The status of the address.
	 */
	public void setStatus(EntityStatus status) 
	{
		this.status = status;
	}

	/**
	 * Gets the type of the address.  This is an open free text field, but is generally associated to names such as XD or email.
	 * @return The type of the address
	 */
	public String getType() 
	{
		return type;
	}

	/**
	 * Sets the type of the address.
	 * @param type The type of the address.
	 */
	public void setType(String type) 
	{
		this.type = type;
	}
	
	/**
	 * Gets the email domain that the address is associated with.
	 * @return The email domain that the address is associated with.
	 */
	public String getDomainName()
	{
		return domainName;
	}
	
	/**
	 * Sets the email domain that the address is associated with.
	 * @param domainName The email domain that the address is associated with.
	 */
	public void setDomainName(String domainName)
	{
		this.domainName = domainName;
	}
}
///CLOVER:ON
