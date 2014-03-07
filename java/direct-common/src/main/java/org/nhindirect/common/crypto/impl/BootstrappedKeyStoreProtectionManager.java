package org.nhindirect.common.crypto.impl;

import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import org.nhindirect.common.crypto.KeyStoreProtectionManager;
import org.nhindirect.common.crypto.exceptions.CryptoException;

/**
 * Implementation of a key store manager where the protection keys are provided as injected parameters.  This class is useful if the 
 * pass phrases or keys are stored in configuration files and can be provided as declarative config statements.
 * @author Greg Meyer
 * @since 1.3
 */
public class BootstrappedKeyStoreProtectionManager implements KeyStoreProtectionManager
{
	protected Key keyStoreProtectionKey;
	protected Key privateKeyProtectionKey;
	
	/**
	 * Empty constructore
	 */
	public BootstrappedKeyStoreProtectionManager ()
	{
		
	}
	
	/**
	 * Constructs a manager by providing the protection keys as strings.
	 * @param keyStoreProtectionKey The pass phrase that protects the key store as a whole.
	 * @param privateKeyProtectionKey The pass phrase that protects the private keys in the key store.
	 */
	public BootstrappedKeyStoreProtectionManager (String keyStoreProtectionKey, String privateKeyProtectionKey)
	{
		setKeyStoreProtectionKey(keyStoreProtectionKey);
		setPrivateKeyProtectionKey(privateKeyProtectionKey);
	}
	
	/**
	 * Sets the pass phrase that protects the key store as a whole as a byte array.
	 * @param keyStoreProtectionKey The pass phrase that protects the key store as a whole as a byte array.
	 */
	public void setKeyStoreProtectionKey(byte[] keyStoreProtectionKey)
	{
		this.keyStoreProtectionKey = new SecretKeySpec(keyStoreProtectionKey, "");
	}
	
	/**
	 * Sets the pass phrase that protects the key store as a whole as a String.
	 * @param keyStoreProtectionKey The pass phrase that protects the key store as a whole as a String.
	 */
	public void setKeyStoreProtectionKey(String keyStoreProtectionKey)
	{
		this.keyStoreProtectionKey = new SecretKeySpec(keyStoreProtectionKey.getBytes(), "");
	}
	
	/**
	 * Sets the pass phrase that protects the private keys in the key store as a byte array.
	 * @param privateKeyProtectionKey The pass phrase that protects the private keys in the key store as a byte array.
	 */
	public void setPrivateKeyProtectionKey(byte[] privateKeyProtectionKey)
	{
		this.privateKeyProtectionKey = new SecretKeySpec(privateKeyProtectionKey, "");
	}

	/**
	 * Sets the pass phrase that protects the private keys in the key store as a String.
	 * @param privateKeyProtectionKey The pass phrase that protects the private keys in the key store as a String.
	 */
	public void setPrivateKeyProtectionKey(String privateKeyProtectionKey)
	{
		this.privateKeyProtectionKey = new SecretKeySpec(privateKeyProtectionKey.getBytes(), "");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Key getPrivateKeyProtectionKey() throws CryptoException 
	{
		return privateKeyProtectionKey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Key getKeyStoreProtectionKey() throws CryptoException 
	{
		return keyStoreProtectionKey;
	}	
	
}
