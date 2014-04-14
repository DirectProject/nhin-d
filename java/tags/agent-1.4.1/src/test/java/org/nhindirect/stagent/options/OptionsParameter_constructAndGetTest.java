package org.nhindirect.stagent.options;

import junit.framework.TestCase;

public class OptionsParameter_constructAndGetTest extends TestCase
{	
	public void testContructParamter() throws Exception
	{
		OptionsParameter param = new OptionsParameter(OptionsParameter.JCE_PROVIDER, "Test Value");
		
		assertEquals(OptionsParameter.JCE_PROVIDER, param.getParamName());
		assertEquals("Test Value", param.getParamValue());
	}
	
	public void testContructParamter_emptyValue() throws Exception
	{
		OptionsParameter param = new OptionsParameter(OptionsParameter.JCE_PROVIDER, "");
		
		assertEquals(OptionsParameter.JCE_PROVIDER, param.getParamName());
		assertEquals("", param.getParamValue());
	}
	
	public void testContructParamter_nullValue() throws Exception
	{
		OptionsParameter param = new OptionsParameter(OptionsParameter.JCE_PROVIDER, null);
		
		assertEquals(OptionsParameter.JCE_PROVIDER, param.getParamName());
		assertNull(param.getParamValue());
	}
	
	public void testContructParamter_emptyName_assertException() throws Exception
	{
		boolean exceptionOccured = false;
		
		try
		{
			new OptionsParameter("", "Test");
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}

		assertTrue(exceptionOccured);
	}
	
	
	public void testContructParamter_nullName_assertException() throws Exception
	{
		boolean exceptionOccured = false;
		
		try
		{
			new OptionsParameter(null, "Test");
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}

		assertTrue(exceptionOccured);
	}
}
