package org.nhindirect.stagent.options;

import java.security.SecureRandom;

import junit.framework.TestCase;

public class OptionsParameter_getParamValueAsIntegerTest extends TestCase 
{
	public void testGetParamValueAsInteger_nullParam_returnDefaultVal()
	{
		SecureRandom random = new SecureRandom();
		random.setSeed(System.currentTimeMillis());
		
		int defaultVal = random.nextInt();
		int retVal = OptionsParameter.getParamValueAsInteger(null, defaultVal);
		
		assertEquals(defaultVal, retVal);
	}
	
	public void testGetParamValueAsInteger_nullParamValue_returnDefaultVal()
	{
		SecureRandom random = new SecureRandom();
		random.setSeed(System.currentTimeMillis());
		
		int defaultVal = random.nextInt();
		int retVal = OptionsParameter.getParamValueAsInteger(new OptionsParameter("test", null), defaultVal);
		
		assertEquals(defaultVal, retVal);
	}
	
	public void testGetParamValueAsInteger_emptyParamValue_returnDefaultVal()
	{
		SecureRandom random = new SecureRandom();
		random.setSeed(System.currentTimeMillis());
		
		int defaultVal = random.nextInt();
		int retVal = OptionsParameter.getParamValueAsInteger(new OptionsParameter("test", ""), defaultVal);
		
		assertEquals(defaultVal, retVal);
	}
	
	public void testGetParamValueAsInteger_invalidIntFormat_returnDefaultVal()
	{
		SecureRandom random = new SecureRandom();
		random.setSeed(System.currentTimeMillis());
		
		int defaultVal = random.nextInt();
		int retVal = OptionsParameter.getParamValueAsInteger(new OptionsParameter("test", "i-/ekux"), defaultVal);
		
		assertEquals(defaultVal, retVal);
	}
	
	public void testGetParamValueAsInteger_validValue_returnValue()
	{
		SecureRandom random = new SecureRandom();
		random.setSeed(System.currentTimeMillis());
		
		int defaultVal = random.nextInt();
		int retVal = OptionsParameter.getParamValueAsInteger(new OptionsParameter("test", "5"), defaultVal);
		
		assertEquals(5, retVal);
	}
}
