package org.nhindirect.stagent.cryptography.activekeyops;

import java.security.cert.X509Certificate;
import java.util.Arrays;

import junit.framework.TestCase;

import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.cryptography.SMIMECryptographerImpl;
import org.nhindirect.stagent.mail.MimeEntity;
import org.nhindirect.stagent.mail.MimeStandard;
import org.nhindirect.stagent.utils.TestUtils;

public class DefaultDirectRecipientInformation_getDecryptedContentTest extends TestCase
{
	protected X509CertificateEx encCert;
	
	
	static
	{
		CryptoExtensions.registerJCEProviders();
	}
	
	protected SMIMEEnveloped createSMIMEEnv() throws Exception
	{
		// get the cert
		encCert = (X509CertificateEx)TestUtils.getInternalCert("user1");
		
		// create an encrypted message
		final MimeEntity entity = new MimeEntity();
		entity.setText("Hello world.");
		entity.setHeader(MimeStandard.ContentTypeHeader, "text/plain");
		entity.setHeader(MimeStandard.ContentTransferEncodingHeader, "7bit");
		
		final SMIMECryptographerImpl encryptor = new SMIMECryptographerImpl();
		return new SMIMEEnveloped(encryptor.encrypt(entity, Arrays.asList((X509Certificate)encCert)));
	}
	
	public void testDecryptedContent_defaultConfig_assertDecrypted() throws Exception
	{
		final SMIMEEnveloped env = createSMIMEEnv();
		final RecipientInformation recipient = (RecipientInformation)env.getRecipientInfos().getRecipients().iterator().next();
		
		final SplitDirectRecipientInformationFactory factory = new SplitDirectRecipientInformationFactory();
			
		final SplitDirectRecipientInformation recInfo = (SplitDirectRecipientInformation) factory.createInstance(recipient, env);
		
		// this won't work unless the data is successfully decrypted
		assertNotNull(recInfo.getDecryptedContent(encCert.getPrivateKey()));
	}
}
