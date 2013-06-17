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

package org.nhindirect.monitor.expression;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.tx.model.TxMessageType;

/**
 * A correlation expression used by an aggregation EIP that groups {@link Tx} messages together based on certain properties.
 * <br>
 * This correlator groups {@link Tx} objects together by either the {@link org.nhindirect.common.tx.model.TxDetailType#MSG_ID} property
 * or the {@link org.nhindirect.common.tx.model.TxDetailType#PARENT_MSG_ID} property.  If the {@link Tx} object has a type of 
 * {{@link org.nhindirect.common.tx.model.TxMessageType#IMF}, then then {@link org.nhindirect.common.tx.model.TxDetailType#PARENT_MSG_ID} property
 * is used.  If the {@link Tx} object has a type of {@link org.nhindirect.common.tx.model.TxMessageType#DSN} or {@link org.nhindirect.common.tx.model.TxMessageType#MDN}, 
 * then the {@link org.nhindirect.common.tx.model.TxDetailType#PARENT_MSG_ID} is used.
 * @author Greg Meyer
 * @since 1.0
 */
public class MessageIdCorrelationExpression implements Expression
{

	/**
	 * {@inheritDoc}}
	 * This class specifically returns the message id or the parent message id based on the {@link Tx} type.
	 */
	@SuppressWarnings({ "hiding", "unchecked" })
	@Override
	public <String> String evaluate(Exchange exchange, Class<String> type) 
	{
		String retVal = null;
		
		final Tx tx = (Tx)exchange.getIn().getBody();
		
		final TxMessageType msgType = tx.getMsgType();
		
		final Map<java.lang.String, TxDetail> details = tx.getDetails();
		if (!details.isEmpty())
		{
			// first check the type of message
			switch (msgType)
			{
				case IMF:
				{
					final TxDetail msgIdDetail = details.get(TxDetailType.MSG_ID.getType());
					if (msgIdDetail != null)
						retVal = (String) msgIdDetail.getDetailValue().toString();
					break;
				}
				case DSN:
				case MDN:
				{
					final TxDetail msgIdDetail = details.get(TxDetailType.PARENT_MSG_ID.getType());
					if (msgIdDetail != null)
						retVal = (String) msgIdDetail.getDetailValue().toString();
					break;
				}
				
			}
		}
		return retVal;
	}

}
