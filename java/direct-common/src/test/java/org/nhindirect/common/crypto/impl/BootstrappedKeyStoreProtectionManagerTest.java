package org.nhindirect.common.crypto.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.security.Key;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Arrays;

public class BootstrappedKeyStoreProtectionManagerTest 
{
	@Test
	public void testGetSetKeysFromByteArray() throws Exception
	{
		BootstrappedKeyStoreProtectionManager mgr = new BootstrappedKeyStoreProtectionManager();
		mgr.setKeyStoreProtectionKey("1234".getBytes());
		mgr.setPrivateKeyProtectionKey("5678".getBytes());
		
		assertTrue(Arrays.equals("1234".getBytes(), mgr.getKeyStoreProtectionKey().getEncoded()));
		assertTrue(Arrays.equals("5678".getBytes(), mgr.getPrivateKeyProtectionKey().getEncoded()));
	}
	
	@Test
	public void testGetSetKeysFromString() throws Exception
	{
		BootstrappedKeyStoreProtectionManager mgr = new BootstrappedKeyStoreProtectionManager();
		mgr.setKeyStoreProtectionKey("1234");
		mgr.setPrivateKeyProtectionKey("5678");
		
		assertTrue(Arrays.equals("1234".getBytes(), mgr.getKeyStoreProtectionKey().getEncoded()));
		assertTrue(Arrays.equals("5678".getBytes(), mgr.getPrivateKeyProtectionKey().getEncoded()));
	}	
	
	@Test
	public void testGetAllKeys() throws Exception
	{
		BootstrappedKeyStoreProtectionManager mgr = new BootstrappedKeyStoreProtectionManager();
		mgr.setKeyStoreProtectionKey("1234");
		mgr.setPrivateKeyProtectionKey("5678");
		
		final Map<String, Key> keys = mgr.getAllKeys();
		
		assertEquals(2, keys.size());
		
		Iterator<Entry<String, Key>> entryIter = keys.entrySet().iterator();
				
		Key key = entryIter.next().getValue();		
		assertTrue(Arrays.equals("5678".getBytes(), key.getEncoded()));
		key = entryIter.next().getValue();	
		assertTrue(Arrays.equals("1234".getBytes(), key.getEncoded()));
	}
}
