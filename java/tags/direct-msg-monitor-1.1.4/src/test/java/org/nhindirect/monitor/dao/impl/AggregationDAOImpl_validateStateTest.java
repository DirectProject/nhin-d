package org.nhindirect.monitor.dao.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.nhindirect.monitor.dao.impl.AggregationDAOImpl;

public class AggregationDAOImpl_validateStateTest 
{
	@Test
	public void testValidateState_stateOK() throws Exception
	{
		AggregationDAOImpl impl = new AggregationDAOImpl();
		impl.setEntityManager(mock(EntityManager.class));

		impl.validateState();
	}
	
	@Test
	public void testValidateState_nullManager_assertIllegalStateException() throws Exception
	{
		AggregationDAOImpl impl = new AggregationDAOImpl();
		
		boolean exceptionOccured = false;
		try
		{
			impl.validateState();
		}
		catch (IllegalStateException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
}
