package org.nhindirect.monitor.condition;

import java.util.Collection;

import org.nhindirect.common.tx.model.Tx;

public interface TxTimeoutCondition 
{
	public long getTimeout(Collection<Tx> txs, long exchangeStartTime);
}
