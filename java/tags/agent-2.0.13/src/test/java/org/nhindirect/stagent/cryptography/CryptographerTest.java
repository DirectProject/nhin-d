package org.nhindirect.stagent.cryptography;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.SecretKey;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMultipart;


import junit.framework.TestCase;

import org.nhindirect.common.crypto.PKCS11Credential;
import org.nhindirect.common.crypto.impl.BootstrappedPKCS11Credential;
import org.nhindirect.common.crypto.impl.StaticPKCS11TokenKeyStoreProtectionManager;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.cryptography.DigestAlgorithm;
import org.nhindirect.stagent.cryptography.EncryptionAlgorithm;
import org.nhindirect.stagent.cryptography.SMIMECryptographerImpl;
import org.nhindirect.stagent.cryptography.SignedEntity;
import org.nhindirect.stagent.mail.MimeEntity;
import org.nhindirect.stagent.mail.MimeError;
import org.nhindirect.stagent.mail.MimeStandard;
import org.nhindirect.stagent.options.OptionsManager;
import org.nhindirect.stagent.parser.EntitySerializer;
import org.nhindirect.stagent.utils.TestUtils;

public class CryptographerTest extends TestCase
{	
	@Override
	public void setUp()
	{
    	CryptoExtensions.registerJCEProviders();
	}
	
	public void testEncryptAndDecryptMimeEntityAES128() throws Exception
	{
		testEncryptAndDecryptMimeEntity(EncryptionAlgorithm.AES128);
	}
	
	public void testEncryptAndDecryptMimeEntityAES256() throws Exception
	{
		testEncryptAndDecryptMimeEntity(EncryptionAlgorithm.AES256);
	}	
	
	public void testEncryptAndDecryptMimeEntityRSA_3DES() throws Exception
	{
		testEncryptAndDecryptMimeEntity(EncryptionAlgorithm.RSA_3DES);
	}		
	
	
	public void testEncryptAndDecryptMimeEntityAES192() throws Exception
	{
		testEncryptAndDecryptMimeEntity(EncryptionAlgorithm.AES192);
	}	
	
	
	private void testEncryptAndDecryptMimeEntity(EncryptionAlgorithm encAlg) throws Exception
	{
		X509Certificate cert = TestUtils.getExternalCert("user1");
		
		SMIMECryptographerImpl cryptographer = new SMIMECryptographerImpl();
		cryptographer.setEncryptionAlgorithm(encAlg);
		
		MimeEntity entity = new MimeEntity();
		entity.setText("Hello world.");
		entity.setHeader(MimeStandard.ContentTypeHeader, "text/plain");
		entity.setHeader(MimeStandard.ContentTransferEncodingHeader, "7bit");
		
		
		MimeEntity encEntity = cryptographer.encrypt(entity, cert);
		
		assertNotNull(encEntity);
		
		X509CertificateEx certex = TestUtils.getInternalCert("user1");
		
		MimeEntity decryEntity = cryptographer.decrypt(encEntity, certex);
		
		assertNotNull(decryEntity);
		
		byte[] decryEntityBytes = EntitySerializer.Default.serializeToBytes(decryEntity);
		byte[] entityBytes = EntitySerializer.Default.serializeToBytes(entity);
		
		assertTrue(Arrays.equals(decryEntityBytes, entityBytes));
		
	}
	
	public void testEncryptAndDecryptMimeEntity_sensitiveDataInPKCS11AES128() throws Exception
	{
		//testEncryptAndDecryptMimeEntity_sensitiveDataInPKCS11(EncryptionAlgorithm.AES128);
	}	
	
	private void testEncryptAndDecryptMimeEntity_sensitiveDataInPKCS11(EncryptionAlgorithm encAlg) throws Exception
	{
		
		OptionsManager.destroyInstance();
		
		System.setProperty("org.nhindirect.stagent.cryptography.JCESensitiveProviderName", "SunPKCS11-SafeNeteTokenPro");
		System.setProperty("org.nhindirect.stagent.cryptography.JCESensitiveProviderClassNames", "sun.security.pkcs11.SunPKCS11;./src/test/resources/pkcs11Config/pkcs11.cfg");
		
		CryptoExtensions.registerJCEProviders();
		
		try
		{
			X509Certificate cert = TestUtils.getExternalCert("user1");
			
			SMIMECryptographerImpl cryptographer = new SMIMECryptographerImpl();
			cryptographer.setEncryptionAlgorithm(encAlg);
			
			MimeEntity entity = new MimeEntity();
			entity.setText("Hello world.");
			entity.setHeader(MimeStandard.ContentTypeHeader, "text/plain");
			entity.setHeader(MimeStandard.ContentTransferEncodingHeader, "7bit");
			
			MimeEntity encEntity = cryptographer.encrypt(entity, cert);
			
			assertNotNull(encEntity);
			
			X509CertificateEx certex = TestUtils.getInternalCert("user1");
			
			// open up the pkcs11 store and find the private key
			KeyStore ks = KeyStore.getInstance("PKCS11");
			ks.load(null, "1Kingpuff".toCharArray()); 
			

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
			
			MimeEntity decryEntity = cryptographer.decrypt(encEntity, decryptCert);
			
			assertNotNull(decryEntity);
			
			byte[] decryEntityBytes = EntitySerializer.Default.serializeToBytes(decryEntity);
			byte[] entityBytes = EntitySerializer.Default.serializeToBytes(entity);
			
			assertTrue(Arrays.equals(decryEntityBytes, entityBytes));
		}		
		finally
		{
			System.setProperty("org.nhindirect.stagent.cryptography.JCESensitiveProviderName", "Hello");
			System.setProperty("org.nhindirect.stagent.cryptography.JCESensitiveProviderClassNames", "sun.security.pkcs11.SunPKCS11");
			
			OptionsManager.destroyInstance();
		}
	}
	
	public void testEncryptAndDecryptMultipartEntityAES128() throws Exception
	{
		testEncryptAndDecryptMultipartEntity(EncryptionAlgorithm.AES128);
	}
	
	public void testEncryptAndDecryptMultipartEntityAES192() throws Exception
	{
		testEncryptAndDecryptMultipartEntity(EncryptionAlgorithm.AES192);
	}
	
	public void testEncryptAndDecryptMultipartEntityAES256() throws Exception
	{
		testEncryptAndDecryptMultipartEntity(EncryptionAlgorithm.AES256);
	}	
	
	public void testEncryptAndDecryptMultipartEntityRSA_3DES() throws Exception
	{
		testEncryptAndDecryptMultipartEntity(EncryptionAlgorithm.RSA_3DES);
	}
	
	private void testEncryptAndDecryptMultipartEntity(EncryptionAlgorithm encAlgo) throws Exception
	{		
		X509Certificate cert = TestUtils.getExternalCert("user1");
		
		SMIMECryptographerImpl cryptographer = new SMIMECryptographerImpl();
		cryptographer.setEncryptionAlgorithm(encAlgo);
		
		MimeEntity entityText = new MimeEntity();
		entityText.setText("Hello world.");
		entityText.setHeader(MimeStandard.ContentTypeHeader, "text/plain");
		entityText.setHeader(MimeStandard.ContentTransferEncodingHeader, "7bit");
		
		MimeEntity entityXML = new MimeEntity();
		entityXML.setText("<Test></Test>");
		entityXML.setHeader(MimeStandard.ContentTypeHeader, "text/xml");		
		
		MimeMultipart mpEntity = new MimeMultipart();
		
		mpEntity.addBodyPart(entityText);
		mpEntity.addBodyPart(entityXML);
		
		MimeEntity encEntity = cryptographer.encrypt(mpEntity, cert);
		
		assertNotNull(encEntity);
		
		X509CertificateEx certex = TestUtils.getInternalCert("user1");
		
		MimeEntity decryEntity = cryptographer.decrypt(encEntity, certex);
		
		assertNotNull(decryEntity);
		
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		mpEntity.writeTo(oStream);
		InternetHeaders hdrs = new InternetHeaders();
		hdrs.addHeader(MimeStandard.ContentTypeHeader, mpEntity.getContentType());
		MimeEntity orgEntity = new MimeEntity(hdrs, oStream.toByteArray());
		
		byte[] decryEntityBytes = EntitySerializer.Default.serializeToBytes(decryEntity);
		byte[] entityBytes = EntitySerializer.Default.serializeToBytes(orgEntity);

		System.out.println("Original:\r\n" + new String(entityBytes));
		System.out.println("\r\n\r\n\r\nNew:\r\n" + new String(decryEntityBytes));		
		
		
		assertTrue(Arrays.equals(decryEntityBytes, entityBytes));		
		
	
	}	
	
	public void testSignMimeEntitySHA1() throws Exception
	{
		testSignMimeEntity(DigestAlgorithm.SHA1);
	}
	
	public void testSignMimeEntitySHA256() throws Exception
	{
		testSignMimeEntity(DigestAlgorithm.SHA256);
	}	
	
	public void testSignMimeEntitySHA384() throws Exception
	{
		testSignMimeEntity(DigestAlgorithm.SHA384);
	}	
	
	public void testSignMimeEntitySHA512() throws Exception
	{
		testSignMimeEntity(DigestAlgorithm.SHA512);
	}		
	
	private void testSignMimeEntity(DigestAlgorithm digAlg) throws Exception
	{	
		X509CertificateEx certex = TestUtils.getInternalCert("user1");
		
		SMIMECryptographerImpl cryptographer = new SMIMECryptographerImpl();
		cryptographer.setDigestAlgorithm(digAlg);
		
		MimeEntity entity = new MimeEntity();
		entity.setText("Hello world.");
		entity.setHeader(MimeStandard.ContentTypeHeader, "text/plain");
		entity.setHeader(MimeStandard.ContentTransferEncodingHeader, "7bit");
		
		SignedEntity signedEnt = cryptographer.sign(entity, certex);
		
		assertNotNull(signedEnt);
		
		byte[] signedEntityBytes = EntitySerializer.Default.serializeToBytes(signedEnt.getContent());
		byte[] entityBytes = EntitySerializer.Default.serializeToBytes(entity);		
		
		assertTrue(Arrays.equals(signedEntityBytes, entityBytes));
		assertNotNull(signedEnt.getSignature());
		
		X509Certificate cert = TestUtils.getExternalCert("user1");
			
		
		cryptographer.checkSignature(signedEnt, cert, new ArrayList<X509Certificate>());
	}

	public void testEncryptAndSignMimeEntity() throws Exception
	{	
		X509Certificate cert = TestUtils.getExternalCert("user1");
		
		SMIMECryptographerImpl cryptographer = new SMIMECryptographerImpl();
		
		MimeEntity entity = new MimeEntity();
		entity.setText("Hello world.");
		entity.setHeader(MimeStandard.ContentTypeHeader, "text/plain");
		entity.setHeader(MimeStandard.ContentTransferEncodingHeader, "7bit");

		MimeEntity encEntity = cryptographer.encrypt(entity, cert);
		
		assertNotNull(encEntity);
		
		X509CertificateEx certex = TestUtils.getInternalCert("user1");

		SignedEntity signedEnt = cryptographer.sign(entity, certex);
		
		assertNotNull(signedEnt);

		cryptographer.checkSignature(signedEnt, cert, new ArrayList<X509Certificate>());

	}

	public void testEncryptWithSingleCert_wrongDecryptCert_assertFailDecrypt() throws Exception
	{
		X509Certificate cert = TestUtils.getExternalCert("user1");
		
		SMIMECryptographerImpl cryptographer = new SMIMECryptographerImpl();
		
		MimeEntity entity = new MimeEntity();
		entity.setText("Hello world.");
		entity.setHeader(MimeStandard.ContentTypeHeader, "text/plain");
		entity.setHeader(MimeStandard.ContentTransferEncodingHeader, "7bit");
		
		
		MimeEntity encEntity = cryptographer.encrypt(entity, cert);
		
		assertNotNull(encEntity);
		
		X509CertificateEx certex = TestUtils.getInternalCert("altnameonly");
		
		boolean exceptionOccured = false;
		try
		{
			cryptographer.decrypt(encEntity, certex);
		}
		catch (NHINDException e)
		{
			if (e.getError().equals(MimeError.Unexpected));
				exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}
	
	public void testEncryptWithSingleCert_decryptWithMutlipeCerts_onlyOneCertCorrect_assertDecrypted() throws Exception
	{
		X509Certificate cert = TestUtils.getExternalCert("user1");
		
		SMIMECryptographerImpl cryptographer = new SMIMECryptographerImpl();
		
		MimeEntity entity = new MimeEntity();
		entity.setText("Hello world.");
		entity.setHeader(MimeStandard.ContentTypeHeader, "text/plain");
		entity.setHeader(MimeStandard.ContentTransferEncodingHeader, "7bit");
		
		
		MimeEntity encEntity = cryptographer.encrypt(entity, cert);
		
		assertNotNull(encEntity);
		
		X509CertificateEx certex1 = TestUtils.getInternalCert("altnameonly");
		X509CertificateEx certex2 = TestUtils.getInternalCert("user1");

		MimeEntity decryEntity = cryptographer.decrypt(encEntity, Arrays.asList(certex1, certex2));

		assertNotNull(decryEntity);
		
		byte[] decryEntityBytes = EntitySerializer.Default.serializeToBytes(decryEntity);
		byte[] entityBytes = EntitySerializer.Default.serializeToBytes(entity);
		
		assertTrue(Arrays.equals(decryEntityBytes, entityBytes));
	}	
	
}
