package org.nhindirect.stagent.cryptography.activekeyops;

import junit.framework.TestCase;

import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.cryptography.activekeyops.SplitProviderDirectSignedDataGenerator;
import org.nhindirect.stagent.cryptography.activekeyops.SplitProviderDirectSignedDataGeneratorFactory;

public class SplitProviderDirectSignedDataGeneratorFactory_createGenTest extends TestCase
{
	public void testCreateGenerator_emptyProvider_assertDefaultProvider() throws Exception
	{
		final SplitProviderDirectSignedDataGeneratorFactory factory = new SplitProviderDirectSignedDataGeneratorFactory();
		final SplitProviderDirectSignedDataGenerator gen = (SplitProviderDirectSignedDataGenerator) factory.createInstance();
		assertEquals(CryptoExtensions.getJCESensitiveProviderName(), gen.sigProvider);
		assertEquals(CryptoExtensions.getJCEProviderName(), gen.digestProvider);
	}
	
	public void testCreateGenerator_customeProvider_assertProviders() throws Exception
	{
		final SplitProviderDirectSignedDataGeneratorFactory factory = new SplitProviderDirectSignedDataGeneratorFactory("TestProvider1", "TestProvider2");
		final SplitProviderDirectSignedDataGenerator gen = (SplitProviderDirectSignedDataGenerator) factory.createInstance();
		assertEquals("TestProvider1", gen.sigProvider);
		assertEquals("TestProvider2", gen.digestProvider);
	}
}
