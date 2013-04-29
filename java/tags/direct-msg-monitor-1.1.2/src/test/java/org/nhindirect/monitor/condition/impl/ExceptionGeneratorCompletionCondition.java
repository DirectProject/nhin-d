package org.nhindirect.monitor.condition.impl;

import java.util.Collection;

import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.monitor.condition.impl.AbstractCompletionCondition;

public class ExceptionGeneratorCompletionCondition extends AbstractCompletionCondition
{

	@Override
	public Collection<String> getIncompleteRecipients(Collection<Tx> txs) 
	{
		throw new RuntimeException("ExceptionGeneratorCompletionCondition genererated exception");
	}

}
