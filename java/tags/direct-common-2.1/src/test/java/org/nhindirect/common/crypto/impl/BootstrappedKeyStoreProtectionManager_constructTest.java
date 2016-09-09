package org.nhindirect.common.crypto.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.security.KeyStore.Entry;
import java.security.KeyStore.SecretKeyEntry;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

public class BootstrappedKeyStoreProtectionManager_constructTest 
{
	@Test
	public void testConstructBootstrappedKeyStoreProtectionManager_defaultContstructor() throws Exception
	{
		final BootstrappedKeyStoreProtectionManager mgr = new BootstrappedKeyStoreProtectionManager();
		assertNotNull(mgr.keyEntries);
		assertTrue(mgr.keyEntries.isEmpty());
		assertNull(mgr.keyStoreProtectionKey);
		assertNull(mgr.privateKeyProtectionKey);
	}
	
	@Test
	public void testConstructBootstrappedKeyStoreProtectionManager_keysAsString() throws Exception
	{
		final BootstrappedKeyStoreProtectionManager mgr = new BootstrappedKeyStoreProtectionManager("Hello", "There");
		assertNotNull(mgr.keyEntries);
		assertFalse(mgr.keyEntries.isEmpty());
		assertEquals(2, mgr.keyEntries.size());
		assertNotNull(mgr.keyStoreProtectionKey);
		assertNotNull(mgr.privateKeyProtectionKey);
	}
	
	@Test
	public void testConstructBootstrappedKeyStoreProtectionManager_keysAsStringAndEntries() throws Exception
	{
		
		final Map<String, Entry> keyEntries = new HashMap<String, Entry>();
		final SecretKey key = new SecretKeySpec("Something".getBytes(), "");
		keyEntries.put("ThisEntry", new SecretKeyEntry((SecretKey)key));
		
		final BootstrappedKeyStoreProtectionManager mgr = new BootstrappedKeyStoreProtectionManager("Hello", "There", keyEntries);
		assertNotNull(mgr.keyEntries);
		assertEquals(3, mgr.keyEntries.size());
		assertNotNull(mgr.keyStoreProtectionKey);
		assertNotNull(mgr.privateKeyProtectionKey);
	}	
}
