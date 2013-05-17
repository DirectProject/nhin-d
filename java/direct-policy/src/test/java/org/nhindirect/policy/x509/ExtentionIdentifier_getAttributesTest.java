package org.nhindirect.policy.x509;

import junit.framework.TestCase;

public class ExtentionIdentifier_getAttributesTest extends TestCase
{
	public void testGetAttributes()
	{
		assertEquals("2.5.29.15",  ExtentionIdentifier.KEY_USAGE.getId());
		assertEquals("Subject Alternative Name",  ExtentionIdentifier.SUBJECT_ALT_NAME.getDisplay());
		assertEquals("SubjectAltName",  ExtentionIdentifier.SUBJECT_ALT_NAME.getRfcName());
	}
}
