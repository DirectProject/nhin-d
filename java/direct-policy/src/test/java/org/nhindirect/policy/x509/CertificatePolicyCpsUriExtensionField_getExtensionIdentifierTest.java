package org.nhindirect.policy.x509;

import junit.framework.TestCase;

public class CertificatePolicyCpsUriExtensionField_getExtensionIdentifierTest extends TestCase
{
	public void testGetExtenstionIdentifier()
	{
		final CertificatePolicyCpsUriExtensionField field = new CertificatePolicyCpsUriExtensionField(true);
		assertEquals(ExtentionIdentifier.CERTIFICATE_POLICIES, field.getExtentionIdentifier());
	}
}
