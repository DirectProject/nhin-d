package org.nhindirect.gateway.testutils;

public abstract class BaseTestPlan 
{
	public void perform() throws Exception 
	{
		try 
		{
			setupMocks();
			Exception exception = null;
			try 
			{
				performInner();
			} 
			catch (Exception e) 
			{
				exception = e;
			}
			assertException(exception);
		} 
		finally 
		{
			tearDownMocks();
		}
	}

	protected abstract void performInner() throws Exception;

	protected void setupMocks() 
	{
	}

	protected void tearDownMocks() 
	{
	}

	protected void assertException(Exception exception) throws Exception 
	{
		if (exception != null) 
		{
			throw exception;
		}
	}
}