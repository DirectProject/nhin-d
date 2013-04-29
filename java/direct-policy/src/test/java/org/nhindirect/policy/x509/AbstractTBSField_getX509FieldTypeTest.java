package org.nhindirect.policy.x509;

import junit.framework.TestCase;

public class AbstractTBSField_getX509FieldTypeTest extends TestCase
{
	public void testGetFieldType()
	{
		// use a concrete instance
		final SubjectPublicKeySizeField field = new SubjectPublicKeySizeField();
		assertEquals(X509FieldType.TBS, field.getX509FieldType());
	}
}
