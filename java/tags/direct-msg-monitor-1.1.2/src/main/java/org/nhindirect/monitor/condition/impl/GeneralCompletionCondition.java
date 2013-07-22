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

package org.nhindirect.monitor.condition.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;

/**
 * A general case completion condition implementation.  This completion condition only checks for the existence
 * of the original message, and either an MDN or DNS message corresponding to the original message.  The condition takes
 * into consideration that an MDN or DNS message must be present for all recipients of the original message for the condition to be
 * considered complete.
 * @author Greg Meyuer
 * @since 1.0
 */
public class GeneralCompletionCondition extends AbstractCompletionCondition
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<String> getIncompleteRecipients(Collection<Tx> txs)
	{
		if (txs == null || txs.size() == 0)
			return Collections.emptyList();
		
		final Tx originalMessage = getMessageToTrack(txs);
		if (originalMessage == null)
			return Collections.emptyList();
		
		final TxDetail originalRecipDetail = originalMessage.getDetail(TxDetailType.RECIPIENTS.getType());
		if (originalRecipDetail == null)
			return Collections.emptyList();
		
		// add the original recipient list to a map of recipients to status
		final Map<String, RecipientResponseStatus> recipStatuses = new HashMap<String, RecipientResponseStatus>();
		for (String recip : originalRecipDetail.getDetailValue().split(","))
			recipStatuses.put(recip, new RecipientResponseStatus(recip.trim()));
		
		for (Tx tx : txs)
		{
			final TxDetail finalRecipDetail = tx.getDetail(TxDetailType.FINAL_RECIPIENTS.getType());
			if (finalRecipDetail != null)
			{
				switch (tx.getMsgType())
				{
				   case MDN:
				   {
					   // an MDN is sent per original message recipient, so we should only be able
					   // to extract one original recipient from this message
					   final RecipientResponseStatus recipStatus = recipStatuses.get(normalizeFinalRecip(finalRecipDetail.getDetailValue()));
					   if (recipStatus != null)
						   recipStatus.addReceivedStatus(RecipientResponseStatus.MDNReceived);

					   break;
				   }
				   case DSN:
				   {
					   // DSN messages may contain multiple final recipients
					   // need to split the recipients out
					   for (String finalRecip : finalRecipDetail.getDetailValue().split(","))
					   {
						   final RecipientResponseStatus recipStatus = recipStatuses.get(normalizeFinalRecip(finalRecip));
						   if (recipStatus != null)
							   recipStatus.addReceivedStatus(RecipientResponseStatus.DSNReceived);
					   }
					   break;
				   }
				}
		 	}
		}
		
		
		final Collection<String> retVal = new ArrayList<String>();
		// only mark as complete if all of the original recipients 
		// have received some type of notification
		for (RecipientResponseStatus status : recipStatuses.values())
			if (status.getReceivedStatus() == 0)
			{
				retVal.add(status.getRecipient());
			}	
		
		return retVal;
	}
	
	private static class RecipientResponseStatus
	{
		public static final short MDNReceived = 0x0001;
		public static final short DSNReceived = 0x0002;
		
		protected int statusesReceived = 0;
		protected final String recipient;
		
		RecipientResponseStatus(String recipient)
		{
			this.recipient = recipient;
		}
		
		public void addReceivedStatus(short status)
		{
			statusesReceived |= status;
		}
		
		public int getReceivedStatus()
		{
			return statusesReceived;
		}
		
		public String getRecipient()
		{
			return recipient;
		}
	}

}
