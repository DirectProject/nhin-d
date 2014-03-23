package org.nhindirect.policy.x509;

import junit.framework.TestCase;

public class AuthorityInfoAccessMethodIdentifier_getAttributesTest extends TestCase
{
	public void testGetAttributes()
	{
		assertEquals("1.3.6.1.5.5.7.48.1", AuthorityInfoAccessMethodIdentifier.OCSP.getId());
		assertEquals("OCSP", AuthorityInfoAccessMethodIdentifier.OCSP.getName());
		
		assertEquals("1.3.6.1.5.5.7.48.2", AuthorityInfoAccessMethodIdentifier.CA_ISSUERS.getId());
		assertEquals("caIssuers", AuthorityInfoAccessMethodIdentifier.CA_ISSUERS.getName());		
	}
}
