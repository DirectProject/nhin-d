package org.nhindirect.policy.x509;

import junit.framework.TestCase;

public class AbstractExtensionField_getFieldNameTest extends TestCase
{
	public void testGetFieldName()
	{
		// use a concrete instance
		final CertificatePolicyIndentifierExtensionField field = new CertificatePolicyIndentifierExtensionField(true);
	
		assertEquals(TBSFieldName.EXTENSIONS, field.getFieldName());
	}
	
}
