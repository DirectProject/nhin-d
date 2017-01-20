package org.nhindirect.stagent.cryptography.activekeyops;

import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.nhindirect.common.crypto.MutableKeyStoreProtectionManager;
import org.nhindirect.common.crypto.PKCS11Credential;
import org.nhindirect.common.crypto.impl.BootstrappedPKCS11Credential;
import org.nhindirect.common.crypto.impl.StaticPKCS11TokenKeyStoreProtectionManager;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.cert.impl.CacheableKeyStoreManagerCertificateStore;
import org.nhindirect.stagent.cryptography.SMIMECryptographerImpl;
import org.nhindirect.stagent.mail.MimeEntity;
import org.nhindirect.stagent.mail.MimeStandard;
import org.nhindirect.stagent.utils.TestUtils;

import junit.framework.TestCase;

public class SplitDirectRecipientInformation_getDecryptedContentTest extends TestCase
{
	protected X509Certificate encCert;
	protected String pkcs11ProvName;
	
	static
	{
		CryptoExtensions.registerJCEProviders();
	}
	
	protected SMIMEEnveloped createSMIMEEnv() throws Exception
	{
		return createSMIMEEnv(null);
	}
	
	protected SMIMEEnveloped createSMIMEEnv(X509Certificate cert) throws Exception
	{
		// get the cert
		if (cert == null)
			encCert = TestUtils.getInternalCert("user1");
		else
			encCert = cert;
		
		// create an encrypted message
		final MimeEntity entity = new MimeEntity();
		entity.setText("Hello world.");
		entity.setHeader(MimeStandard.ContentTypeHeader, "text/plain");
		entity.setHeader(MimeStandard.ContentTransferEncodingHeader, "7bit");
		
		final SMIMECryptographerImpl encryptor = new SMIMECryptographerImpl();
		return new SMIMEEnveloped(encryptor.encrypt(entity, Arrays.asList(encCert)));
	}
	
	public void testGetDecryptedContent_sameDefaultEncAndKeyEncProvider_assertDecrypted() throws Exception
	{
		final SMIMEEnveloped env = createSMIMEEnv();
		final RecipientInformation recipient = (RecipientInformation)env.getRecipientInfos().getRecipients().iterator().next();
		
		final SplitDirectRecipientInformationFactory factory = new SplitDirectRecipientInformationFactory();
			
		final SplitDirectRecipientInformation recInfo = (SplitDirectRecipientInformation) factory.createInstance(recipient, env);
		
		// this will be non-null if it works correctly
		assertNotNull(recInfo.getDecryptedContent(((X509CertificateEx)encCert).getPrivateKey()));
	}
	
	public void testGetDecryptedContent_differentEncAndKeyEncProvider_assertDecrypted() throws Exception
	{
		final SMIMEEnveloped env = createSMIMEEnv();
		final RecipientInformation recipient = (RecipientInformation)env.getRecipientInfos().getRecipients().iterator().next();
		
		final SplitDirectRecipientInformationFactory factory = new SplitDirectRecipientInformationFactory("SunJCE", "BC");
			
		final SplitDirectRecipientInformation recInfo = (SplitDirectRecipientInformation) factory.createInstance(recipient, env);
		
		// this will be non-null if it works correctly
		assertNotNull(recInfo.getDecryptedContent(((X509CertificateEx)encCert).getPrivateKey()));
	}
	
	public void testGetDecryptedContent_safeNetHSMKeyEncProvider_assertDecrypted() throws Exception
	{
        /**
         * This test is only run if a specific SafeNet eToken Pro HSM is connected to the testing 
         * system.  This can be modified for another specific machine and/or token.
         */
		pkcs11ProvName = TestUtils.setupSafeNetToken();
		if (!StringUtils.isEmpty(pkcs11ProvName))
		{
			final PKCS11Credential cred = new BootstrappedPKCS11Credential("1Kingpuff");
			final MutableKeyStoreProtectionManager mgr = new StaticPKCS11TokenKeyStoreProtectionManager(cred, "", "");
			final CacheableKeyStoreManagerCertificateStore store = new CacheableKeyStoreManagerCertificateStore(mgr);
			store.add(TestUtils.getInternalCert("user1"));
			
			// get a certificate from the key store
			final KeyStore ks = KeyStore.getInstance("PKCS11");
			
			ks.load(null, "1Kingpuff".toCharArray());
			
			// get the decryption cert
			X509CertificateEx decryptCert = null;
			
			final Enumeration<String> aliases = ks.aliases();
			while (aliases.hasMoreElements())
			{
				String alias = aliases.nextElement();
				
				Certificate pkcs11Cert = ks.getCertificate(alias);
				if (pkcs11Cert != null &&pkcs11Cert instanceof X509Certificate)
				{
					
					// check if there is private key
					Key key = ks.getKey(alias, null);
					if (key != null && key instanceof PrivateKey && CryptoExtensions.certSubjectContainsName((X509Certificate)pkcs11Cert, "user1@cerner.com"))
					{
						decryptCert = X509CertificateEx.fromX509Certificate((X509Certificate)pkcs11Cert, (PrivateKey)key);
						break;
					}
				}
			}	
			
			final SMIMEEnveloped env = createSMIMEEnv();
			final RecipientInformation recipient = (RecipientInformation)env.getRecipientInfos().getRecipients().iterator().next();
			
			final SplitDirectRecipientInformationFactory factory = new SplitDirectRecipientInformationFactory(pkcs11ProvName, "BC");
				
			final SplitDirectRecipientInformation recInfo = (SplitDirectRecipientInformation) factory.createInstance(recipient, env);
			
			// this will be non-null if it works correctly
			assertNotNull(recInfo.getDecryptedContent(decryptCert.getPrivateKey()));
		}
	}
	
	public void testGetDecryptedContent_safeNetHSMKeyEncProvider_differntEncCert_assertNotDecrypted() throws Exception
	{
        /**
         * This test is only run if a specific SafeNet eToken Pro HSM is connected to the testing 
         * system.  This can be modified for another specific machine and/or token.
         */
		pkcs11ProvName = TestUtils.setupSafeNetToken();
		if (!StringUtils.isEmpty(pkcs11ProvName))
		{
			// get a certificate from the key store
			final KeyStore ks = KeyStore.getInstance("PKCS11");
			
			ks.load(null, "1Kingpuff".toCharArray());
			
			// get the decryption cert
			X509CertificateEx decryptCert = null;
			
			final Enumeration<String> aliases = ks.aliases();
			while (aliases.hasMoreElements())
			{
				String alias = aliases.nextElement();
				
				Certificate pkcs11Cert = ks.getCertificate(alias);
				if (pkcs11Cert != null &&pkcs11Cert instanceof X509Certificate)
				{
					
					// check if there is private key
					Key key = ks.getKey(alias, null);
					if (key != null && key instanceof PrivateKey && CryptoExtensions.certSubjectContainsName((X509Certificate)pkcs11Cert, "user1@cerner.com"))
					{
						decryptCert = X509CertificateEx.fromX509Certificate((X509Certificate)pkcs11Cert, (PrivateKey)key);
						break;
					}
				}
			}	
			
			encCert = TestUtils.getInternalCert("gm2552");
			final SMIMEEnveloped env = createSMIMEEnv(encCert);
			final RecipientInformation recipient = (RecipientInformation)env.getRecipientInfos().getRecipients().iterator().next();
			
			final SplitDirectRecipientInformationFactory factory = new SplitDirectRecipientInformationFactory(pkcs11ProvName, "BC");
				
			final SplitDirectRecipientInformation recInfo = (SplitDirectRecipientInformation) factory.createInstance(recipient, env);
			
		
			boolean exceptionOccured = false;
			try
			{
				recInfo.getDecryptedContent(decryptCert.getPrivateKey());
			}
			catch (Exception e)
			{
				exceptionOccured = true;
			}
			assertTrue(exceptionOccured);
		}
	}
	

}
