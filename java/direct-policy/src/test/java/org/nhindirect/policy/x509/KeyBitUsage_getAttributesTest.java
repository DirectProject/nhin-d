package org.nhindirect.policy.x509;

import junit.framework.TestCase;

public class KeyBitUsage_getAttributesTest extends TestCase
{
	public void testGetAttributes_assertAttributes()
	{
		assertEquals((1 << 7), KeyUsageBit.DIGITAL_SIGNATURE.getUsageBit());
		assertEquals("digitalSignature", KeyUsageBit.DIGITAL_SIGNATURE.getName());
	}
}
