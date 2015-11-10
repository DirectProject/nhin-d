package org.nhindirect.stagent.cryptography.activekeyops;

import java.security.cert.X509Certificate;
import java.util.Arrays;

import junit.framework.TestCase;

import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.cryptography.SMIMECryptographerImpl;
import org.nhindirect.stagent.mail.MimeEntity;
import org.nhindirect.stagent.mail.MimeStandard;
import org.nhindirect.stagent.utils.TestUtils;

public class SplitDirectRecipientInformationFactory_createInstanceTest extends TestCase
{
	static
	{
		CryptoExtensions.registerJCEProviders();
	}
	
	protected SMIMEEnveloped createSMIMEEnv() throws Exception
	{
		// get the cert
		final X509Certificate cert = TestUtils.getExternalCert("user1");
		
		// create an encrypted message
		final MimeEntity entity = new MimeEntity();
		entity.setText("Hello world.");
		entity.setHeader(MimeStandard.ContentTypeHeader, "text/plain");
		entity.setHeader(MimeStandard.ContentTransferEncodingHeader, "7bit");
		
		final SMIMECryptographerImpl encryptor = new SMIMECryptographerImpl();
		return new SMIMEEnveloped(encryptor.encrypt(entity, Arrays.asList(cert)));
	}
	
	public void testInstanceTest_emptyProvider_assertDefaultProvider() throws Exception
	{
		final SMIMEEnveloped env = createSMIMEEnv();
		final RecipientInformation recipient = (RecipientInformation)env.getRecipientInfos().getRecipients().iterator().next();
		
		final SplitDirectRecipientInformationFactory factory = new SplitDirectRecipientInformationFactory();
			
		final SplitDirectRecipientInformation recInfo = (SplitDirectRecipientInformation) factory.createInstance(recipient, env);
		assertEquals(CryptoExtensions.getJCEProviderName(), recInfo.encProvider);
		assertEquals(CryptoExtensions.getJCESensitiveProviderName(), recInfo.keyEncProvider);
	}
	
	public void testInstanceTest_configedProvider_assertConfigedProvider() throws Exception
	{
		final SMIMEEnveloped env = createSMIMEEnv();
		final RecipientInformation recipient = (RecipientInformation)env.getRecipientInfos().getRecipients().iterator().next();
		
		final SplitDirectRecipientInformationFactory factory = new SplitDirectRecipientInformationFactory("SensitiveProv", "NonSenProv");
				
		final SplitDirectRecipientInformation recInfo = (SplitDirectRecipientInformation) factory.createInstance(recipient, env);
		assertEquals("NonSenProv", recInfo.encProvider);
		assertEquals("SensitiveProv", recInfo.keyEncProvider);
	}
}
