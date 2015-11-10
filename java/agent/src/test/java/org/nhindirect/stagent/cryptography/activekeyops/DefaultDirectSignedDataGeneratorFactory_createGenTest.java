package org.nhindirect.stagent.cryptography.activekeyops;

import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.cryptography.activekeyops.DefaultDirectSignedDataGenerator;
import org.nhindirect.stagent.cryptography.activekeyops.DefaultDirectSignedDataGeneratorFactory;

import junit.framework.TestCase;

public class DefaultDirectSignedDataGeneratorFactory_createGenTest extends TestCase
{
	public void testCreateGenerator_emptyProvider_assertDefaultProvider() throws Exception
	{
		final DefaultDirectSignedDataGeneratorFactory factory = new DefaultDirectSignedDataGeneratorFactory();
		final DefaultDirectSignedDataGenerator gen = (DefaultDirectSignedDataGenerator) factory.createInstance();
		assertEquals(CryptoExtensions.getJCEProviderName(), gen.sigProvider);
	}
	
	public void testCreateGenerator_customeProvider_assertProvider() throws Exception
	{
		final DefaultDirectSignedDataGeneratorFactory factory = new DefaultDirectSignedDataGeneratorFactory("TestProvider");
		final DefaultDirectSignedDataGenerator gen = (DefaultDirectSignedDataGenerator) factory.createInstance();
		assertEquals("TestProvider", gen.sigProvider);
	}
}
