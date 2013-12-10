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
import org.nhindirect.monitor.condition.TxTimeoutCondition;

/**
 * Timeout condition that decrements the timeout based on the original correlation start time and the maximum amount of
 * time that correlation should be allowed stay in the aggregator.
 * @author Greg Meyer
 * @since 1.0
 */
public class DecayingTimeoutCondition implements TxTimeoutCondition
{
	protected final long completionTimeout;
	
	/**
	 * Constructor
	 * @param completionTimeout The maximum amount of
     * time in milliseconds that correlation should be allowed stay in the aggregator.
	 */
	public DecayingTimeoutCondition(long completionTimeout)
	{
		this.completionTimeout = completionTimeout;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTimeout(Collection<Tx> txs, long exchangeStartTime) 
	{
	    // get the difference between the exchange start time and the current time
		final long timeSinceStart = getCurrentTime() - exchangeStartTime;
		
		// subtract the elapsed time since the exchange started form the completion timeout
		long newTimeout = completionTimeout - timeSinceStart;
		
		// there may be condition where the new timeout may be <= 0 due to delays
		// in the timeout thread... we don't want 0 or negative timeouts (not sure how
		// Camel handles these), so set the timeout to 1ms as a mitigating stradegy
		if (newTimeout <= 0)
			newTimeout = 1;
		
		return newTimeout;
	}
	
	/**
	 * Gets the current date time
	 * @return The current date time
	 */
	protected long getCurrentTime()
	{
		return System.currentTimeMillis();
	}
}
