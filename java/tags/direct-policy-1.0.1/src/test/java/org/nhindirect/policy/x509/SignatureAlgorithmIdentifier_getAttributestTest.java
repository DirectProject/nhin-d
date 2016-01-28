package org.nhindirect.policy.x509;

import junit.framework.TestCase;

public class SignatureAlgorithmIdentifier_getAttributestTest extends TestCase
{
	public void testGetAttributes()
	{
		assertEquals("1.2.840.113549.1.1.5", SignatureAlgorithmIdentifier.SHA1RSA.getId());
		assertEquals("sha1RSA", SignatureAlgorithmIdentifier.SHA1RSA.getName());
	}
}
