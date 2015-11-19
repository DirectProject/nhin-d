package org.nhindirect.common.crypto.impl;

import static org.junit.Assert.assertTrue;

import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.Test;


public class DynamicPKCS11TokenKeyStoreProtectionManagerTest 
{

	
	@Test
	public void testDummy()
	{
		
	}
	
	/*
	 * Enable the tests below when testing with a real PKCS token
	 */
	
	/*
	static
	{
		final String configName = "./src/test/resources/pkcs11Config/pkcs11.cfg";
		final Provider p = new sun.security.pkcs11.SunPKCS11(configName);
		Security.addProvider(p);
	}
	
	@Test
	public void testSetKeysAsKeyAndGetFromToken() throws Exception
	{
		BootstrapTestCallbackHandler handler = new BootstrapTestCallbackHandler("1Kingpuff");
		
		final DynamicPKCS11TokenKeyStoreProtectionManager mgr = new DynamicPKCS11TokenKeyStoreProtectionManager("KeyStoreProtKey", "PrivKeyProtKey", handler);
		
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
		
		BootstrapTestCallbackHandler handler = new BootstrapTestCallbackHandler("1Kingpuff");
		
		final DynamicPKCS11TokenKeyStoreProtectionManager mgr = new DynamicPKCS11TokenKeyStoreProtectionManager("KeyStoreProtKey", "PrivKeyProtKey", handler);
		
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
		
		BootstrapTestCallbackHandler handler = new BootstrapTestCallbackHandler("1Kingpuff");
		
		final DynamicPKCS11TokenKeyStoreProtectionManager mgr = new DynamicPKCS11TokenKeyStoreProtectionManager("KeyStoreProtKey", "PrivKeyProtKey", handler);
		
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
	*/
}
