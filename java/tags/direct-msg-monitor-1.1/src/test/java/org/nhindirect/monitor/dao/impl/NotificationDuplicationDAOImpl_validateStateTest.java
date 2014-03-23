package org.nhindirect.monitor.dao.impl;

import static org.mockito.Mockito.mock;

import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;

import org.junit.Test;


public class NotificationDuplicationDAOImpl_validateStateTest 
{
	
	@Test
	public void testValidateState_stateOK() throws Exception
	{
		NotificationDuplicationDAOImpl impl = new NotificationDuplicationDAOImpl();
		impl.setEntityManager(mock(EntityManager.class));

		impl.validateState();
	}
	
	@Test
	public void testValidateState_nullManager_assertIllegalStateException() throws Exception
	{
		NotificationDuplicationDAOImpl impl = new NotificationDuplicationDAOImpl();
		
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
