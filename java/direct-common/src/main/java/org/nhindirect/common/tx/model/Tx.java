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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.enunciate.json.JsonRootType;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Represents a message with a set of monitoring details and the type of message.
 * @author Greg Meyer
 * @since 1.1
 */
@JsonRootType
public class Tx implements Serializable
{
	static final long serialVersionUID = -6462360991748031100L;	
	
	protected TxMessageType msgType;
	
	protected Map<String, TxDetail> details;
	
	/**
	 * Constructor
	 */
	public Tx()
	{
		msgType = TxMessageType.UNKNOWN;
		details = new HashMap<String, TxDetail>();
	}
	
	/**
	 * Constructor
	 * @param msgType The message type
	 * @param details Map of message details
	 */
	public Tx(TxMessageType msgType, Map<String, TxDetail> details)
	{
		if (msgType == null)
			throw new IllegalArgumentException("Type cannot be null");
		
		if (details == null)
			throw new IllegalArgumentException("Details cannot be null");
		
		this.details = Collections.unmodifiableMap(details);
		this.msgType = msgType;
	}
	
	/**
	 * Gets the message type
	 * @return The message type
	 */
	public TxMessageType getMsgType()
	{
		return this.msgType;
	}
	
	/**
	 * Sets the message type
	 * @param type The message type
	 */
	public void setMsgType(TxMessageType type)
	{
		if (type == null)
			throw new IllegalArgumentException("Type cannot be null");
		
		this.msgType = type;
	}
	
	/**
	 * Sets the message details
	 * @param details The message details
	 */
	public void setDetails(Map<String, TxDetail> details)
	{
		if (details == null)
			throw new IllegalArgumentException("Details cannot be null");
		
		this.details = details;
	}
	
	/**
	 * Gets the map of message details
	 * @return
	 */
	public Map<String, TxDetail> getDetails()
	{
		return this.details;
	}
	
	/**
	 * Gets a specific message detail by type
	 * @param detailType The detail type to get
	 * @return The detail corresponding to the type
	 */
	@JsonIgnore
	public TxDetail getDetail(TxDetailType detailType)
	{
		if (detailType == null)
			return null;
		
		return getDetail(detailType.getType());
	}
	
	/**
	 * Gets a specific message detail by a string name
	 * @param detailName The detail type to get
	 * @return The detail corresponding to the name
	 */
	@JsonIgnore
	public TxDetail getDetail(String detailName)
	{
		if (detailName == null || detailName.isEmpty())
			return null;
		
		return details.get(detailName);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder("TxType: ").append(msgType.toString());
		
		if (details.isEmpty())
			builder.append("\r\nNo Details");
		else
		{
			for (TxDetail detail: details.values())
				builder.append("\r\n\r\n").append(detail.toString());
		}
		
		return builder.toString();
		
	}
}
