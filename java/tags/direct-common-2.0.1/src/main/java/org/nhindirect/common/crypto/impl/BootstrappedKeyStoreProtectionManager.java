/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.nhindirect.common.crypto.impl;

import java.security.Key;
import java.security.KeyStore.Entry;
import java.security.KeyStore.SecretKeyEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.nhindirect.common.crypto.KeyStoreProtectionManager;
import org.nhindirect.common.crypto.WrappableKeyProtectionManager;
import org.nhindirect.common.crypto.exceptions.CryptoException;

/**
 * Implementation of a key store manager where the protection keys are provided as injected parameters.  This class is useful if the 
 * pass phrases or keys are stored in configuration files and can be provided as declarative config statements.
 * @author Greg Meyer
 * @since 1.3
 */
public class BootstrappedKeyStoreProtectionManager implements KeyStoreProtectionManager, WrappableKeyProtectionManager
{
	public final static String PrivKeyProtKey = "PrivKeyProtKey";
	public final static String KeyStoreProtKey = "KeyStoreProtKey";
	
	protected Key keyStoreProtectionKey;
	protected Key privateKeyProtectionKey;
	protected Map<String, Entry> keyEntries;
	
	/**
	 * Empty constructore
	 */
	public BootstrappedKeyStoreProtectionManager ()
	{
		setKeyEntries(null);
	}
	
	/**
	 * Constructs a manager by providing the protection keys as strings.
	 * @param keyStoreProtectionKey The pass phrase that protects the key store as a whole.
	 * @param privateKeyProtectionKey The pass phrase that protects the private keys in the key store.
	 */
	public BootstrappedKeyStoreProtectionManager (String keyStoreProtectionKey, String privateKeyProtectionKey)
	{
		setKeyEntries(null);
		setKeyStoreProtectionKey(keyStoreProtectionKey);
		setPrivateKeyProtectionKey(privateKeyProtectionKey);
	}
	
	/**
	 * Constructs a manager by providing the protection keys as strings and a collection of key entries
	 * @param keyStoreProtectionKey The pass phrase that protects the key store as a whole.
	 * @param privateKeyProtectionKey The pass phrase that protects the private keys in the key store.
	 * @param entries Key entries
	 */
	public BootstrappedKeyStoreProtectionManager (String keyStoreProtectionKey, String privateKeyProtectionKey,
			Map<String, Entry> entries)
	{
		setKeyEntries(entries);
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
		keyEntries.put(KeyStoreProtKey, new SecretKeyEntry((SecretKey)this.keyStoreProtectionKey));
	}
	
	/**
	 * Sets the pass phrase that protects the key store as a whole as a String.
	 * @param keyStoreProtectionKey The pass phrase that protects the key store as a whole as a String.
	 */
	public void setKeyStoreProtectionKey(String keyStoreProtectionKey)
	{
		this.keyStoreProtectionKey = new SecretKeySpec(keyStoreProtectionKey.getBytes(), "");
		keyEntries.put(KeyStoreProtKey, new SecretKeyEntry((SecretKey)this.keyStoreProtectionKey));
	}
	
	/**
	 * Sets the pass phrase that protects the private keys in the key store as a byte array.
	 * @param privateKeyProtectionKey The pass phrase that protects the private keys in the key store as a byte array.
	 */
	public void setPrivateKeyProtectionKey(byte[] privateKeyProtectionKey)
	{
		this.privateKeyProtectionKey = new SecretKeySpec(privateKeyProtectionKey, "");
		keyEntries.put(PrivKeyProtKey, new SecretKeyEntry((SecretKey)this.privateKeyProtectionKey));
	}

	/**
	 * Sets the pass phrase that protects the private keys in the key store as a String.
	 * @param privateKeyProtectionKey The pass phrase that protects the private keys in the key store as a String.
	 */
	public void setPrivateKeyProtectionKey(String privateKeyProtectionKey)
	{
		this.privateKeyProtectionKey = new SecretKeySpec(privateKeyProtectionKey.getBytes(), "");
		keyEntries.put(PrivKeyProtKey, new SecretKeyEntry((SecretKey)this.privateKeyProtectionKey));
	}
	
	/**
	 * Sets the key entries.
	 * @param entries The key entries
	 */
	public void setKeyEntries(Map<String, Entry> entries) 
	{
		this.keyEntries = (entries == null) ? new HashMap<String, Entry>() : new HashMap<String, Entry>(entries);
		// add the static entries
		if (this.keyStoreProtectionKey != null)
			keyEntries.put(PrivKeyProtKey, new SecretKeyEntry((SecretKey)this.keyStoreProtectionKey));
		if (this.keyStoreProtectionKey != null)
			keyEntries.put(KeyStoreProtKey, new SecretKeyEntry((SecretKey)this.privateKeyProtectionKey));
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Key> getAllKeys() throws CryptoException 
	{
		final Map<String, Key> keys = new HashMap<String, Key>();
		for (Map.Entry<String, Entry> keyEntry : this.keyEntries.entrySet())
			if (keyEntry.getValue() instanceof SecretKeyEntry)
				keys.put(keyEntry.getKey(), ((SecretKeyEntry)keyEntry.getValue()).getSecretKey());
	
		return keys;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Key getKey(String keyName) throws CryptoException
	{
		final Entry keyEntry = getEntry(keyName);
		if (keyEntry != null && keyEntry instanceof SecretKeyEntry)
			return ((SecretKeyEntry)keyEntry).getSecretKey();

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Entry> getAllEntries() throws CryptoException 
	{
		return Collections.unmodifiableMap(keyEntries);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Entry getEntry(String entryName) throws CryptoException 
	{
		return this.keyEntries.get(entryName);
	}
	
	/**
	 * {@inheritDoc}}
	 */
	@Override
	public byte[] wrapWithSecretKey(SecretKey kek, Key keyToWrap) throws CryptoException 
	{
		final IvParameterSpec iv = new IvParameterSpec(AbstractPKCS11TokenKeyStoreProtectionManager.IV_BYTES);
		try
		{
			final Cipher wrapCipher = Cipher.getInstance(AbstractPKCS11TokenKeyStoreProtectionManager.WRAP_ALGO);
			wrapCipher.init(Cipher.WRAP_MODE, kek, iv);

			return wrapCipher.wrap(keyToWrap);
		}
		catch (Exception e)
		{
			throw new CryptoException("Failed to wrap key: " + e.getMessage(), e);
		}

	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Key unwrapWithSecretKey(SecretKey kek, byte[] wrappedData, String keyAlg, int keyType) throws CryptoException 
	{
		final IvParameterSpec iv = new IvParameterSpec(AbstractPKCS11TokenKeyStoreProtectionManager.IV_BYTES);
		try
		{
			final Cipher unwrapCipher = Cipher.getInstance(AbstractPKCS11TokenKeyStoreProtectionManager.WRAP_ALGO);
			unwrapCipher.init(Cipher.UNWRAP_MODE, kek, iv);
	
			return unwrapCipher.unwrap(wrappedData, keyAlg, keyType);
		}
		catch (Exception e)
		{
			throw new CryptoException("Failed to unwrap key: " + e.getMessage(), e);
		}
	}
}
