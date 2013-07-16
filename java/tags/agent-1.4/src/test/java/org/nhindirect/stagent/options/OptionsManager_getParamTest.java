package org.nhindirect.stagent.options;

import junit.framework.TestCase;

public class OptionsManager_getParamTest  extends TestCase
{
	@Override
	public void setUp()
	{
		OptionsManager.INSTANCE = null;
	}
	
	@Override
	public void tearDown()
	{
		OptionsManager.getInstance().options.clear();
	}
	
	public void testGetParams_emptyParams() throws Exception
	{
		OptionsManager mgr = OptionsManager.getInstance();
		assertEquals(0, mgr.getParameters().size());
		

	}
	
	public void testGetParams_overrideExistantParam() throws Exception
	{		
		OptionsManager mgr = OptionsManager.getInstance();
		assertNull(mgr.getParameter("Test Param"));
		
		OptionsParameter param = new OptionsParameter("Test Param", "Test Value");
		
		mgr.setOptionsParameter(param);
		assertEquals(1, mgr.getParameters().size());
		
		
		OptionsParameter retParam = mgr.getParameter("Test Param");
		assertNotNull(retParam);
		assertEquals(param.getParamName(), retParam.getParamName());
		assertEquals(param.getParamValue(), retParam.getParamValue());
		
		param = new OptionsParameter("Test Param", "Test Value2");
		
		mgr.setOptionsParameter(param);
		assertEquals(1, mgr.getParameters().size());
		
		retParam = mgr.getParameter("Test Param");
		assertNotNull(retParam);
		assertEquals(param.getParamName(), retParam.getParamName());
		assertEquals(param.getParamValue(), retParam.getParamValue());
	}
	
	public void testGetParams_overrideJVMParam() throws Exception
	{		
		System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", "BC");
		OptionsManager mgr = OptionsManager.getInstance();
		assertNotNull(OptionsParameter.JCE_PROVIDER);
		
		OptionsParameter param = new OptionsParameter(OptionsParameter.JCE_PROVIDER, "NSS");
		
		mgr.setOptionsParameter(param);
		assertEquals(1, mgr.getParameters().size());
		
		
		OptionsParameter retParam = mgr.getParameter(OptionsParameter.JCE_PROVIDER);
		assertNotNull(retParam);
		assertEquals(param.getParamName(), retParam.getParamName());
		assertEquals(param.getParamValue(), retParam.getParamValue());
		
		System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", "");
	}
	
	public void testGetParams_nullParameter_assertException() throws Exception
	{		
		OptionsManager mgr = OptionsManager.getInstance();
		
		boolean exceptionOccured = false;
		try
		{
			mgr.getParameter(null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}

		assertTrue(exceptionOccured);
	}
	
	public void testGetParams_emptyParameter_assertException() throws Exception
	{		
		OptionsManager mgr = OptionsManager.getInstance();
		
		boolean exceptionOccured = false;
		try
		{
			mgr.getParameter("");
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}

		assertTrue(exceptionOccured);
	}
}

