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
 * DNS Records
 * @author Greg Meyer
 * @since 1.0
 */
///CLOVER:OFF
@JsonRootType
public class DNSRecord 
{
    private long id;
	private String name;
	private int type;
	private int dclass;
	private long ttl;
	private byte[] data;
    private Calendar createTime;
    
    /**
     * Empty constructor
     */
    public DNSRecord()
    {
    	
    }
    
    /**
     * Gets the internal id of the record.
     * @return The internal id of the record.
     */
	public long getId() 
	{
		return id;
	}
	
	/**
	 * Sets the internal id of the record.
	 * @param id The internal id of the record.
	 */
	public void setId(long id)
	{
		this.id = id;
	}
	
	/**
	 * Gets the DNS name associated with the record.
	 * @return The DNS name associated with the record.
	 */
	public String getName() 
	{
		return name;
	}
	
	/**
	 * Sets the DNS name associated with the record.
	 * @param name The DNS name associated with the record.
	 */
	public void setName(String name) 
	{
		this.name = name;
	}
	
	/**
	 * Gets the DNS record type.
	 * @return The DNS record type.
	 */
	public int getType() 
	{
		return type;
	}
	
	/**
	 * Sets the DNS record type.
	 * @param type The DNS record type.
	 */
	public void setType(int type) 
	{
		this.type = type;
	}
	
	/**
	 * Gets the DNS record Dclass.
	 * @return The DNS record Dclass.
	 */
	public int getDclass() 
	{
		return dclass;
	}
	
	/**
	 * Sets the DNS record Dclass.
	 * @param dclass The DNS record Dclass.
	 */
	public void setDclass(int dclass) 
	{
		this.dclass = dclass;
	}
	
	/**
	 * Gets the DNS record's time to live.
	 * @return The DNS record's time to live.
	 */
	public long getTtl() 
	{
		return ttl;
	}
	
	/**
	 * Sets the DNS record's time to live.
	 * @param ttl The DNS record's time to live.
	 */
	public void setTtl(long ttl) 
	{
		this.ttl = ttl;
	}
	
	/**
	 * Sets the raw data of the DNS record.
	 * @return The raw data of the DNS record.
	 */
	public byte[] getData() 
	{
		return data;
	}
	
	/**
	 * Sets the raw data of the DNS record.
	 * @param data The raw data of the DNS record.
	 */
	public void setData(byte[] data) 
	{
		this.data = data;
	}
	
	/**
	 * Gets the date/time that the DNS record was created in the system.
	 * @return The date/time that the DNS record was created in the system.
	 */
	public Calendar getCreateTime() 
	{
		return createTime;
	}
	
	/**
	 * Sets the date/time that the DNS record was created in the system.
	 * @param createTime The date/time that the DNS record was created in the system.
	 */
	public void setCreateTime(Calendar createTime) 
	{
		this.createTime = createTime;
	}
}
///CLOVER:ON