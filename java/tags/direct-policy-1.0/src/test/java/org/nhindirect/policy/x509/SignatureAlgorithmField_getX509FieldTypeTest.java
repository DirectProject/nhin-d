package org.nhindirect.policy.x509;

import junit.framework.TestCase;

public class SignatureAlgorithmField_getX509FieldTypeTest extends TestCase
{
	public void testGetX509FieldType()
	{
		final SignatureAlgorithmField field = new SignatureAlgorithmField();
		assertEquals(X509FieldType.SIGNATURE_ALGORITHM, field.getX509FieldType());
	}
}
