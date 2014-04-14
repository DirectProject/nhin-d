package org.nhindirect.policy.x509;

import junit.framework.TestCase;

public class SubjectPublicKeySizeField_getFieldNameTest extends TestCase
{
	public void testGetFielName()
	{
		final SubjectPublicKeySizeField field = new SubjectPublicKeySizeField();
		
		assertEquals(TBSFieldName.SUBJECT_PUBLIC_KEY_INFO, field.getFieldName());
	}
}
