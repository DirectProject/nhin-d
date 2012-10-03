package org.nhindirect.stagent.options;

import junit.framework.TestCase;

public class OptionsParameter_getParamValueAsBooleanTest extends TestCase 
{
	public void testGetParamValueAsBoolean_nullParam_returnDefaultVal()
	{
		boolean retVal = OptionsParameter.getParamValueAsBoolean(null, true);
		
		assertTrue(retVal);
	}
	
	public void testGetParamValueAsBoolean_nullParamValue_returnDefaultVal()
	{
		boolean retVal = OptionsParameter.getParamValueAsBoolean(new OptionsParameter("test", null), true);
		
		assertTrue(retVal);
	}
	
	public void testGetParamValueAsBoolean_emptyParamValue_returnDefaultVal()
	{
		boolean retVal = OptionsParameter.getParamValueAsBoolean(new OptionsParameter("test", ""), true);
		
		assertEquals(true, retVal);
	}
	
	public void testGetParamValueAsBoolean_invalidBooleanFormat_returnFalse()
	{
		boolean retVal = OptionsParameter.getParamValueAsBoolean(new OptionsParameter("test", "i-/ekux"), true);
		
		assertFalse(retVal);
	}
	
	public void testGetParamValueAsBoolean_validTrueValue_returnValue()
	{
		boolean retVal = OptionsParameter.getParamValueAsBoolean(new OptionsParameter("test", "true"), true);
		
		assertTrue(retVal);
	}
	
	public void testGetParamValueAsBoolean_validFalseValue_returnValue()
	{
		boolean retVal = OptionsParameter.getParamValueAsBoolean(new OptionsParameter("test", "false"), true);
		
		assertFalse(retVal);
	}
}
