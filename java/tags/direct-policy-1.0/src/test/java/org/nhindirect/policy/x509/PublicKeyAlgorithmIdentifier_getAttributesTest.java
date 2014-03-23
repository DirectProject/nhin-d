package org.nhindirect.policy.x509;

import junit.framework.TestCase;

public class PublicKeyAlgorithmIdentifier_getAttributesTest extends TestCase
{
	public void testGetAttributes()
	{
		assertEquals("1.2.840.113549.1.1.1", PublicKeyAlgorithmIdentifier.RSA.algId);
		assertEquals("RSA", PublicKeyAlgorithmIdentifier.RSA.getName());
		
		assertEquals("1.2.840.10040.4.1", PublicKeyAlgorithmIdentifier.DSA.algId);
		assertEquals("DSA", PublicKeyAlgorithmIdentifier.DSA.getName());
	}
}
