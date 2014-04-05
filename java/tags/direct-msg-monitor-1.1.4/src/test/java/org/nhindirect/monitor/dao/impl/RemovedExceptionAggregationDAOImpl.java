package org.nhindirect.monitor.dao.impl;

import org.nhindirect.monitor.dao.AggregationDAOException;
import org.nhindirect.monitor.dao.AggregationVersionException;
import org.nhindirect.monitor.dao.entity.Aggregation;
import org.springframework.transaction.annotation.Transactional;


public class RemovedExceptionAggregationDAOImpl extends AbstractExceptionAggregationDAOImpl
{
	public RemovedExceptionAggregationDAOImpl()
	{
		
	}
	
	@Override
    @Transactional(readOnly = false, rollbackFor={AggregationDAOException.class})
	public void removeAggregation(Aggregation agg, String exchangeId) throws AggregationDAOException
	{
		if (intervalCounter.getAndIncrement() % addSuccessModulus == 0)
		{
			throw new AggregationVersionException("Exception generated from RemovedExceptionAggregationDAOImpl");
		}
		
		super.removeAggregation(agg, exchangeId);
	}
}
