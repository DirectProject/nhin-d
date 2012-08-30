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

import java.util.Collection;

import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.condition.TxCompletionCondition;

/**
 * Abstract completion condition that implements the logic to determine the initial message that is being tracked.  The initial
 * message should be the only message with a {@link TxMessageType} of IMF.  All other messages should be of type DSN, MDN, or unknown.
 * @author Greg Meyer
 * @since 1.0
 */
public abstract class AbstractCompletionCondition implements TxCompletionCondition
{
	/**
	 * Determines the orginial message that is being tracked.
	 * @param txs Collection of aggregated messages that have been correlated together.
	 * @return The {@link Tx} object corresponding to the original message that is being tracked.  Returns
	 * null if the original message has not yet been added to the correlated collection.
	 */
	public static Tx getMessageToTrack(final Collection<Tx> txs)
	{        
        if (txs == null || txs.size() == 0)
        	return null;
        
        for (Tx tx : txs)
        	if (tx.getMsgType() == TxMessageType.IMF)
        		return tx;
        
        return null;
	}
	
	/**
	 * Determines the orginial message that is being tracked.
	 * @param txs Collection of aggregated messages that have been correlated together.
	 * @return The {@link Tx} object corresponding to the original message that is being tracked.  Returns
	 * null if the original message has not yet been added to the correlated collection.
	 */
	protected Tx getMessageToTrackInternal(final Collection<Tx> txs)
	{        
		return getMessageToTrack(txs);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isComplete(Collection<Tx> txs) 
	{
		if (txs == null || txs.size() == 0)
			return false;
		
		final Tx originalMessage = getMessageToTrackInternal(txs);
		if (originalMessage == null)
			return false;
		
		final TxDetail originalRecipDetail = originalMessage.getDetail(TxDetailType.RECIPIENTS.getType());
		if (originalRecipDetail == null)
			return false;	
		
		final Collection<String> incompleteRecips = getIncompleteRecipients(txs);
			
		return incompleteRecips.isEmpty();
	}
	
	/**
	 * Final recipients may begin with something like rfc822;.  This removes the prefix and just returns the final
	 * recipient as an address.
	 * @param recip  The final recipient
	 * @return Normalized version of the final recipient that only contains the email address.
	 */
	public static String normalizeFinalRecip(String recip)
	{
		String normalizedString = recip;
		
		final int index = recip.indexOf(";");
		if (index > -1)
		{
			normalizedString = recip.substring(index + 1).trim();
		}
			
		return normalizedString;
	}
}
