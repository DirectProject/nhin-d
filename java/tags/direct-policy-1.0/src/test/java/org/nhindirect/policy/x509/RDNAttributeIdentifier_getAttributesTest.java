package org.nhindirect.policy.x509;

import junit.framework.TestCase;

public class RDNAttributeIdentifier_getAttributesTest extends TestCase
{
	public void testGetAttributes_toString()
	{
		assertEquals("CN", RDNAttributeIdentifier.COMMON_NAME.toString());
		assertEquals("C", RDNAttributeIdentifier.COUNTRY.toString());
	}
	
	public void testGetAttributes_fromName()
	{
		assertEquals(RDNAttributeIdentifier.COMMON_NAME, RDNAttributeIdentifier.fromName("CN"));
		assertNull(RDNAttributeIdentifier.fromName("CN."));
		//assertEquals("C", RDNAttributeIdentifier.COUNTRY.toString());
	}
}
