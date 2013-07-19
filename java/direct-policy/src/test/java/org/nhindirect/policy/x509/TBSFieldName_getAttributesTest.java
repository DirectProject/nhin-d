package org.nhindirect.policy.x509;

import junit.framework.TestCase;

public class TBSFieldName_getAttributesTest extends TestCase
{
	public void testGetAttributes_getRFCName()
	{
		assertEquals("Version", TBSFieldName.VERSION.getRfcName());
		assertEquals("SerialNumber", TBSFieldName.SERIAL_NUMBER.getRfcName());
	}
	
	public void testGetAttributes_getDisplay()
	{
		assertEquals("Version", TBSFieldName.VERSION.getDisplay());
		assertEquals("Serial Number", TBSFieldName.SERIAL_NUMBER.getDisplay());
	}
	
	public void testGetAttributes_toString()
	{
		assertEquals("Version", TBSFieldName.VERSION.toString());
		assertEquals("SerialNumber", TBSFieldName.SERIAL_NUMBER.toString());
	}
	
}
