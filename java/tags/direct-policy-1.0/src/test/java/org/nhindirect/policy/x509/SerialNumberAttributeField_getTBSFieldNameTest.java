package org.nhindirect.policy.x509;

import junit.framework.TestCase;

public class SerialNumberAttributeField_getTBSFieldNameTest extends TestCase
{
	public void testGetFieldName_assertName()
	{
		final SerialNumberAttributeField field = new SerialNumberAttributeField();
		assertEquals(TBSFieldName.SERIAL_NUMBER, field.getFieldName());
	}
}
