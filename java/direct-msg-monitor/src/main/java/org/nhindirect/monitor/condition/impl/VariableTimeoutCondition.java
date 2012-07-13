package org.nhindirect.monitor.condition.impl;

import java.util.Collection;

import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.monitor.condition.TxTimeoutCondition;

public class VariableTimeoutCondition implements TxTimeoutCondition
{
	protected final TxTimeoutCondition timelyExpression;
	protected final TxTimeoutCondition generalExpression;
	
	public VariableTimeoutCondition(TxTimeoutCondition timelyExpression, TxTimeoutCondition generalExpression)
	{
		if (timelyExpression == null || generalExpression == null)
			throw new IllegalArgumentException("Expressions cannot be null.");
		
		this.timelyExpression = timelyExpression;
		this.generalExpression = generalExpression;
	}

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
	
	///CLOVER:OFF
	protected Tx getMessageToTrack(Collection<Tx> txs)
	{
		return AbstractCompletionCondition.getMessageToTrack(txs);
	}
	
	protected boolean isRelAndTimelyRequired(Tx messageToTrack)
	{
		return VariableCompletionCondition.isRelAndTimelyRequired(messageToTrack);
	}
	///CLOVER:ON
}
