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

	public String getEmailAddress() 
	{
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) 
	{
		this.emailAddress = emailAddress;
	}

	public long getId() 
	{
		return id;
	}

	public void setId(long id) 
	{
		this.id = id;
	}

	public String getDisplayName() 
	{
		return displayName;
	}

	public void setDisplayName(String displayName) 
	{
		this.displayName = displayName;
	}

	public String getEndpoint() 
	{
		return endpoint;
	}

	public void setEndpoint(String endpoint) 
	{
		this.endpoint = endpoint;
	}

	public Calendar getCreateTime() 
	{
	
		return createTime;
	}

	public void setCreateTime(Calendar createTime) 
	{
		this.createTime = createTime;
	}

	public Calendar getUpdateTime() 
	{
		return updateTime;
	}

	public void setUpdateTime(Calendar updateTime) 
	{
		this.updateTime = updateTime;
	}

	public EntityStatus getStatus()
	{
		return status;
	}

	public void setStatus(EntityStatus status) 
	{
		this.status = status;
	}

	public String getType() 
	{
		return type;
	}

	public void setType(String type) 
	{
		this.type = type;
	}
	
	public String getDomainName()
	{
		return domainName;
	}
	
	public void setDomainName(String domainName)
	{
		this.domainName = domainName;
	}
}
///CLOVER:ON
