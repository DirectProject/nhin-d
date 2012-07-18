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

import org.nhindirect.common.tx.TxUtil;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.monitor.condition.TxTimeoutCondition;

/**
 * A concrete timeout condition container that holds two specific timeout condition implementations.  Specifically these conditions
 * are for a general completion use case, and the other that implements the timely and reliable messaging for direct specification.  This class
 * determines which completion condition to use based on the existence of the X-DIRECT-FINAL-DESTINATION-DELIVERY message disposition option.
 * <p>
 * Because HISP implementations may vary, the implementations of the completion conditions are injected to support implementation flexibility.
 * @author Greg Meyer
 * @since 1.0
 */
public class VariableTimeoutCondition implements TxTimeoutCondition
{
	protected final TxTimeoutCondition timelyExpression;
	protected final TxTimeoutCondition generalExpression;
	
	/**
	 * Constructor
	 * @param timelyExpression Reliable and timely timeout condition
	 * @param generalExpression General timeout condition
	 */
	public VariableTimeoutCondition(TxTimeoutCondition timelyExpression, TxTimeoutCondition generalExpression)
	{
		if (timelyExpression == null || generalExpression == null)
			throw new IllegalArgumentException("Expressions cannot be null.");
		
		this.timelyExpression = timelyExpression;
		this.generalExpression = generalExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTimeout(Collection<Tx> txs, long exchangeStartTime) 
	{
		TxTimeoutCondition conditionToUse = null;
		
		final Tx messageToTrack = getMessageToTrack(txs);
		
		// if there is not a message to track, then we will just assume
		// the general timeout condition
		if (messageToTrack == null)
			conditionToUse = generalExpression;
		else 
			conditionToUse = isRelAndTimelyRequired(messageToTrack) ? timelyExpression : generalExpression;
		
		return conditionToUse.getTimeout(txs, exchangeStartTime);
	}
	
	/**
	 * Determines the orginial message that is being tracked.
	 * @param txs Collection of aggregated messages that have been correlated together.
	 * @return The {@link Tx} object corresponding to the original message that is being tracked.  Returns
	 * null if the original message has not yet been added to the correlated collection.
	 *
	 */
	///CLOVER:OFF
	protected Tx getMessageToTrack(Collection<Tx> txs)
	{
		return AbstractCompletionCondition.getMessageToTrack(txs);
	}
	
	/**
	 * Determines if the timely and reliable completion condition should be used for a message.  This is determined by 
     * the existence of the X-DIRECT-FINAL-DESTINATION-DELIVERY message disposition option on the original message.
	 * @param imfMessage The original IMF message that is being tracked.
	 * @return true if the original message indicates that it requires timely and reliable tracking; false otherwise
	 */
	protected boolean isRelAndTimelyRequired(Tx messageToTrack)
	{
		return TxUtil.isReliableAndTimelyRequested(messageToTrack);
	}
	///CLOVER:ON
}
