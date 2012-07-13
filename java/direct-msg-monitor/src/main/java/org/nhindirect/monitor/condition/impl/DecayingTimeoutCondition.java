package org.nhindirect.monitor.condition.impl;

import java.util.Collection;

import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.monitor.condition.TxTimeoutCondition;

public class DecayingTimeoutCondition implements TxTimeoutCondition
{
	protected final long completionTimeout;
	
	public DecayingTimeoutCondition(long completionTimeout)
	{
		this.completionTimeout = completionTimeout;
	}

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
	
	protected long getCurrentTime()
	{
		return System.currentTimeMillis();
	}
}
