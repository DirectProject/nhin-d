package org.nhindirect.monitor.dao.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.nhindirect.monitor.dao.impl.AggregationDAOImpl;

public abstract class AbstractExceptionAggregationDAOImpl extends AggregationDAOImpl
{
	protected int addSuccessModulus = 2; // ever other add/update will succeed
	
	protected AtomicInteger intervalCounter = new AtomicInteger(0);
	
	public void setSuccessModulus(int addSuccessModulus)
	{
		this.addSuccessModulus = addSuccessModulus;
	}
	
	public int getSuccessModulus()
	{
		return addSuccessModulus;
	}
}
