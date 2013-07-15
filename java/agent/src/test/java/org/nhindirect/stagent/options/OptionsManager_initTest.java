package org.nhindirect.stagent.options;

import junit.framework.TestCase;

public class OptionsManager_initTest extends TestCase
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
	
	public void testInit_noParamsSet() throws Exception
	{
		OptionsManager manager = OptionsManager.getInstance();
		assertEquals(0, manager.options.size());
	}
	
	public void testInit_noUnknownParam() throws Exception
	{
		OptionsManager manager = OptionsManager.getInstance();
		manager.initParam("Bogus Param Name");
		assertEquals(0, manager.options.size());
	}
	
	public void testInit_emptyParamValue() throws Exception
	{
		System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", "");
		
		OptionsManager manager = OptionsManager.getInstance();
		assertEquals(0, manager.options.size());

	}
	
	public void testInit_populateParamValue() throws Exception
	{
		System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", "BC");
		
		OptionsManager manager = OptionsManager.getInstance();
		assertEquals(1, manager.options.size());

		System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", "");
	}
}
