package org.nhindirect.policy.x509;

import junit.framework.TestCase;

public class SubjectPublicKeyAlgorithmField_getFieldNameTest extends TestCase
{
	public void testGetFieldName()
	{
		final SubjectPublicKeyAlgorithmField field = new SubjectPublicKeyAlgorithmField();
		assertEquals(X509FieldType.TBS, field.getX509FieldType());
		assertEquals(TBSFieldName.SUBJECT_PUBLIC_KEY_INFO, field.getFieldName());
	}
}
