package org.nhindirect.monitor.dao.impl;

import org.nhindirect.monitor.dao.AggregationDAOException;
import org.nhindirect.monitor.dao.AggregationVersionException;
import org.nhindirect.monitor.dao.entity.Aggregation;
import org.springframework.transaction.annotation.Transactional;


public class AddUpdateExceptionAggregationDAOImpl extends AbstractExceptionAggregationDAOImpl
{	
	public AddUpdateExceptionAggregationDAOImpl()
	{
		super();
	}
		
	@Override
    @Transactional(readOnly = false, rollbackFor={AggregationDAOException.class})	
	public void addUpdateAggregation(Aggregation aggr) throws AggregationDAOException
	{		
		if (intervalCounter.getAndIncrement() % addSuccessModulus == 0)
		{
			throw new AggregationVersionException("Exception generated from ExceptionGenerationAggregationDAOImpl");
		}
		
		super.addUpdateAggregation(aggr);
	}
}
