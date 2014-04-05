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

package org.nhindirect.common.tx.model;

import java.io.Serializable;

import org.codehaus.enunciate.json.JsonRootType;

/**
 * Represents a message detail as a name/value pair.  A detail can either be named by a predefined enumeration of {@link TxDetailType} or a user
 * defined name can be used.
 * @author Greg Meyer
 * @since 1.1
 */
@JsonRootType
public class TxDetail implements Serializable
{
	static final long serialVersionUID = -3930645229029223953L;
	
	protected String detailName;
	
	protected String detailValue;
	
	/**
	 * Constructor
	 */
	public TxDetail()
	{
		detailName = TxDetailType.UNKNOWN.getType();
		detailValue = "";
	}
	
	/**
	 * Constructor
	 * @param detailName The detail type of the detail.
	 * @param detailValue The detail value
	 */
	public TxDetail(TxDetailType detailName, String detailValue)
	{
		this(detailName.getType(), detailValue);
	}
	
	/**
	 * Constructor
	 * @param detailName The detail type as a string name
	 * @param detailValue The detail value
	 */
	public TxDetail(String detailName, String detailValue)
	{
		if (detailName == null || detailName.isEmpty())
			throw new IllegalArgumentException("Detail name cannot be null or empty");
		
		if (detailValue == null)
			throw new IllegalArgumentException("Detail value cannot be null");
		
		this.detailName = detailName;
		this.detailValue = detailValue;
	}
	
	/**
	 * Sets the detail name as a string
	 * @param detailName The detail name
	 */
	public void setDetailName(String detailName)
	{
		if (detailName == null || detailName.isEmpty())
			throw new IllegalArgumentException("Detail name cannot be null or empty");
		
		this.detailName = detailName;
	}
	
	/**
	 * Gets the detail name
	 * @return The detail name
	 */
	public String getDetailName()
	{
		return this.detailName;
	}
	
	/**
	 * Sets the detail value
	 * @param detailValue The detail value
	 */
	public void setDetailValue(String detailValue)
	{
		if (detailValue == null)
			throw new IllegalArgumentException("Detail value cannot be null");
		
		this.detailValue = detailValue;
	}
	
	/**
	 * Gets the detail value
	 * @return The detail value
	 */
	public String getDetailValue()
	{
		return this.detailValue;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{	
		final StringBuilder builder = new StringBuilder(detailName).append("\r\n").append(detailValue);
		
		return builder.toString();
	}
}
