package org.nhindirect.common.crypto.impl;

import static org.junit.Assert.assertTrue;

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
}
