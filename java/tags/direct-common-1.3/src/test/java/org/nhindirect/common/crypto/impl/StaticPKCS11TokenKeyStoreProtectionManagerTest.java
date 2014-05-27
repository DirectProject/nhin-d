package org.nhindirect.common.crypto.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.Test;
import org.nhindirect.common.crypto.PKCS11Credential;

public class StaticPKCS11TokenKeyStoreProtectionManagerTest 
{
	
	@Test
	public void testDummy()
	{
		
	}
	
	/*
	 * Enable the tests below when testing with a real PKCS token
	 */
	

	static
	{
		//final String configName = "./src/test/resources/pkcs11Config/pkcs11.cfg";
		//final Provider p = new sun.security.pkcs11.SunPKCS11(configName);
		//Security.addProvider(p);
	}
	/*
	@Test
	public void testSetKeysAsKeyAndGetFromToken() throws Exception
	{
		
		PKCS11Credential cred = new BootstrappedPKCS11Credential("1Kingpuff");
		
		final StaticPKCS11TokenKeyStoreProtectionManager mgr = new StaticPKCS11TokenKeyStoreProtectionManager(cred, "KeyStoreProtKey", "PrivKeyProtKey");
		
		// create the keys on the token
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		SecureRandom random = new SecureRandom(); // cryptograph. secure random 
		keyGen.init(random); 
		final SecretKey keyStoreSecretKey = keyGen.generateKey();
		
		mgr.clearKeyStoreProtectionKey();
		mgr.setKeyStoreProtectionKey(keyStoreSecretKey);

		keyGen = KeyGenerator.getInstance("AES");
		random = new SecureRandom(); // cryptograph. secure random 
		keyGen.init(random); 
		final SecretKey privKeySecretKey = keyGen.generateKey();
		
		mgr.clearPrivateKeyProtectionKey();
		mgr.setPrivateKeyProtectionKey(privKeySecretKey);
		
		assertTrue(Arrays.equals(keyStoreSecretKey.getEncoded(), mgr.getKeyStoreProtectionKey().getEncoded()));
		assertTrue(Arrays.equals(privKeySecretKey.getEncoded(), mgr.getPrivateKeyProtectionKey().getEncoded()));
	}
	
	@Test
	public void testSetKeysAsByteArrayAndGetFromToken() throws Exception
	{
		
		PKCS11Credential cred = new BootstrappedPKCS11Credential("1Kingpuff");
		
		final StaticPKCS11TokenKeyStoreProtectionManager mgr = new StaticPKCS11TokenKeyStoreProtectionManager(cred, "KeyStoreProtKey", "PrivKeyProtKey");
		
		// create the keys on the token
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		SecureRandom random = new SecureRandom(); // cryptograph. secure random 
		keyGen.init(random); 
		final SecretKey keyStoreSecretKey = keyGen.generateKey();
		
		mgr.clearKeyStoreProtectionKey();
		mgr.setKeyStoreProtectionKeyAsBytes(keyStoreSecretKey.getEncoded());

		keyGen = KeyGenerator.getInstance("AES");
		random = new SecureRandom(); // cryptograph. secure random 
		keyGen.init(random); 
		final SecretKey privKeySecretKey = keyGen.generateKey();
		
		mgr.clearPrivateKeyProtectionKey();
		mgr.setPrivateKeyProtectionKeyAsBytes(privKeySecretKey.getEncoded());
		
		assertTrue(Arrays.equals(keyStoreSecretKey.getEncoded(), mgr.getKeyStoreProtectionKey().getEncoded()));
		assertTrue(Arrays.equals(privKeySecretKey.getEncoded(), mgr.getPrivateKeyProtectionKey().getEncoded()));
	}	
	
	@Test
	public void testSetKeysAsStringAndGetFromToken() throws Exception
	{
		
		PKCS11Credential cred = new BootstrappedPKCS11Credential("1Kingpuff");
		
		final StaticPKCS11TokenKeyStoreProtectionManager mgr = new StaticPKCS11TokenKeyStoreProtectionManager(cred, "KeyStoreProtKey", "PrivKeyProtKey");
		
		// create the keys on the token
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		SecureRandom random = new SecureRandom(); // cryptograph. secure random 
		keyGen.init(random); 
		
		mgr.clearKeyStoreProtectionKey();
		mgr.setKeyStoreProtectionKeyAsString("12345");
		
		mgr.clearPrivateKeyProtectionKey();
		mgr.setPrivateKeyProtectionKeyAsString("67890");
		
		assertTrue(Arrays.equals("12345".getBytes(), mgr.getKeyStoreProtectionKey().getEncoded()));
		assertTrue(Arrays.equals("67890".getBytes(), mgr.getPrivateKeyProtectionKey().getEncoded()));
	}	
	
	@Test
	public void testGetKeys_noKeyExists_assertNull() throws Exception
	{
		
		PKCS11Credential cred = new BootstrappedPKCS11Credential("1Kingpuff");
		
		final StaticPKCS11TokenKeyStoreProtectionManager mgr = new StaticPKCS11TokenKeyStoreProtectionManager(cred, "KeyStoreProtKey", "PrivKeyProtKey");
		
		
		mgr.clearKeyStoreProtectionKey();
		
		mgr.clearPrivateKeyProtectionKey();
		
		
		assertNull(mgr.getKeyStoreProtectionKey());
		assertNull(mgr.getPrivateKeyProtectionKey());
	}
	
	@Test
	public void testGetAllKeys_secureRandomGenKeys() throws Exception
	{
		PKCS11Credential cred = new BootstrappedPKCS11Credential("1Kingpuff");
		
		final StaticPKCS11TokenKeyStoreProtectionManager mgr = new StaticPKCS11TokenKeyStoreProtectionManager(cred, "KeyStoreProtKey", "PrivKeyProtKey");
		
		// create the keys on the token
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		SecureRandom random = new SecureRandom(); // cryptograph. secure random 
		keyGen.init(random); 
		final SecretKey keyStoreSecretKey = keyGen.generateKey();
		
		mgr.clearKeyStoreProtectionKey();
		mgr.setKeyStoreProtectionKey(keyStoreSecretKey);

		keyGen = KeyGenerator.getInstance("AES");
		random = new SecureRandom(); // cryptograph. secure random 
		keyGen.init(random); 
		final SecretKey privKeySecretKey = keyGen.generateKey();
		
		mgr.clearPrivateKeyProtectionKey();
		mgr.setPrivateKeyProtectionKey(privKeySecretKey);
		
		final Map<String, Key> keys = mgr.getAllKeys();
		
		assertEquals(2, keys.size());
		
		Iterator<Entry<String, Key>> entryIter = keys.entrySet().iterator();
				
		Key key = entryIter.next().getValue();		
		assertTrue(Arrays.equals(privKeySecretKey.getEncoded(), key.getEncoded()));
		key = entryIter.next().getValue();	
		assertTrue(Arrays.equals(keyStoreSecretKey.getEncoded(), key.getEncoded()));
	}
	
	@Test
	public void testGetAllKeys_StringKeys() throws Exception
	{
		PKCS11Credential cred = new BootstrappedPKCS11Credential("1Kingpuff");
		
		final StaticPKCS11TokenKeyStoreProtectionManager mgr = new StaticPKCS11TokenKeyStoreProtectionManager(cred, "KeyStoreProtKey", "PrivKeyProtKey");
		
		// create the keys on the token
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		SecureRandom random = new SecureRandom(); // cryptograph. secure random 
		keyGen.init(random); 
		
		mgr.clearKeyStoreProtectionKey();
		mgr.setKeyStoreProtectionKeyAsString("12345");
		
		mgr.clearPrivateKeyProtectionKey();
		mgr.setPrivateKeyProtectionKeyAsString("67890");
		
		final Map<String, Key> keys = mgr.getAllKeys();
		
		assertEquals(2, keys.size());
		
		Iterator<Entry<String, Key>> entryIter = keys.entrySet().iterator();
				
		Key key = entryIter.next().getValue();		
		assertTrue(Arrays.equals("67890".getBytes(), key.getEncoded()));
		key = entryIter.next().getValue();	
		assertTrue(Arrays.equals("12345".getBytes(), key.getEncoded()));
	}	
*/
}
