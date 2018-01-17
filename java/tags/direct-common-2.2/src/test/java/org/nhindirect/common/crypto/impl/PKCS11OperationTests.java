package org.nhindirect.common.crypto.impl;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.nhindirect.common.crypto.PKCS11Credential;
import org.nhindirect.common.crypto.exceptions.CryptoException;
import org.nhindirect.common.util.TestUtils;

public class PKCS11OperationTests 
{
	/*
	 * Enable the tests below when testing with a real PKCS token
	 */
	
	static
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	
	@Test 
	public void testGenerateSecureAESKeyOnToken() throws Exception
	{
		final String pkcs11ProvName = TestUtils.setupSafeNetToken();
		
		if (!StringUtils.isEmpty(pkcs11ProvName))
		{
			try
			{
				final KeyGenerator keyGen = KeyGenerator.getInstance("AES", pkcs11ProvName);
				final SecureRandom random = SecureRandom.getInstance("PKCS11", pkcs11ProvName); // cryptograph. secure random 
				keyGen.init(random); 
				final SecretKey key = keyGen.generateKey();
				
				assertNotNull(key);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw e;
			}
		}
	}
	
	@Test
	public void testSignDataOnToken() throws Exception
	{
		final String pkcs11ProvName = TestUtils.setupSafeNetToken();
		
		if (!StringUtils.isEmpty(pkcs11ProvName))
		{
			final KeyStore ks = KeyStore.getInstance("PKCS11");
			
			ks.load(null, "1Kingpuff".toCharArray());
			
			final Enumeration<String> aliases = ks.aliases();
			
			while (aliases.hasMoreElements())
			{
				final String alias = aliases.nextElement();
				
				System.out.println("\r\nAlias Name: " + alias);
				
				final KeyStore.Entry entry = ks.getEntry(alias, null);
				System.out.println("Key Type: " + entry.getClass());
				if (entry instanceof KeyStore.PrivateKeyEntry)
				{
					final KeyStore.PrivateKeyEntry pEntry = (KeyStore.PrivateKeyEntry)entry;
					
					final Signature sig = Signature.getInstance("SHA256withRSA", pkcs11ProvName);
					sig.initSign(pEntry.getPrivateKey());
					
					// sign the data
			        String starttext = "Some Text to Encrypt and Sign as an Example";
			        final byte[] bytes = starttext.getBytes();
			        sig.update(bytes);
			        final byte[] theSignature = sig.sign();
			        assertNotNull(theSignature);
			        
			        System.out.println("Data Signed");
			        break;
				}
				
			}
		}
	}

	
	/**
	 * This test will most likely kick out when executed, but can serve as sample code 
	 * for wrapping and unwrapping sensitive key material on a PKCS11 token.
	 * @throws Exception
	 */
	@Test
	public void testImportEncryptedPrivateKeyWithWrapping() throws Exception
	{
		/*
		 * The point of this test is to ensure encrypted private keys can be loaded 
		 * into the token without ever exposing any secret material in process memory.
		 */
		
		final String pkcs11ProvName = TestUtils.setupSafeNetToken();
		
		if (!StringUtils.isEmpty(pkcs11ProvName))
		{
			final PKCS11Credential cred = new BootstrappedPKCS11Credential("1Kingpuff");
			final StaticPKCS11TokenKeyStoreProtectionManager mgr = 
					new StaticPKCS11TokenKeyStoreProtectionManager(cred, "KeyStoreProtKey", "PrivKeyProtKey");
			
			/*
			 * 1. Create an AES128 secret key on the HSM that will be used to 
			 * encrypt and decrypt private key data.  Use the PrivKeyProtKey entry to store it
			 */
			final KeyGenerator keyGen = KeyGenerator.getInstance("AES", pkcs11ProvName);
			keyGen.init(128); 
			final SecretKey keyStoreSecretKey = keyGen.generateKey();
			
			/*
			 * 2. Get an existing private key that was generated and is stored in a p12 file.  
			 * For real operations, the private key may be generated on an HSM and exported in wrapped format for
			 * storage in a database.  For this test, we'll just use an existing private key in a p12 file and 
			 * wrap it on the HSM.
			 */
			final KeyStore store = KeyStore.getInstance("pkcs12");
			store.load(FileUtils.openInputStream(new File("./src/test/resources/certs/gm2552encrypted.p12")), "1kingpuff".toCharArray());
			// there should only be on entry
			final String alias = store.aliases().nextElement();
			final PrivateKey entry = (PrivateKey)store.getKey(alias, "1kingpuff".toCharArray());
			
			/*
			 * 3. "Wrap" the private using secret key and AES128 encryption and write it to a file.  The encryption is done
			 * on the HSM so the secret key never leaves the HSM token.  We aren't actually "wrapping" the private key because
			 * it's not on the HSM.  Using "encrypt" instead.
			 */
			byte[] wrappedKey = null;
			try
			{
				wrappedKey = mgr.wrapWithSecretKey(keyStoreSecretKey, entry);
			}
			catch (CryptoException e)
			{
				// this HSM token does not support wrapping.... kick out
				return;
			}
			FileUtils.writeByteArrayToFile(new File("wrappedPrivateKey.der"), wrappedKey);
			
			/*
			 * 4. Now we have a wrap key in a file.  Let's install it into the token using the 
			 * secret key on the HSM.  This should return us with a private key object, but we should
			 * not be able to get access to the actual unencrypted key data.
			 */
			byte[] encryptedKey = FileUtils.readFileToByteArray(new File("wrappedPrivateKey.der"));
			final PrivateKey securedPrivateKey = (PrivateKey)mgr.unwrapWithSecretKey(keyStoreSecretKey, encryptedKey, "RSA", Cipher.PRIVATE_KEY);
			assertNotNull(securedPrivateKey);
		}
		
		
	}
}
