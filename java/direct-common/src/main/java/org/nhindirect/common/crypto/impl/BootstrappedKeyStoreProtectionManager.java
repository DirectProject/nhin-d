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
import java.util.HashMap;
import java.util.Map;

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Key> getAllKeys() throws CryptoException 
	{
		final Map<String, Key> keys = new HashMap<String, Key>();
		
		keys.put("PrivKeyProtKey", getPrivateKeyProtectionKey());
		keys.put("KeyStoreProtKey", getKeyStoreProtectionKey());
		
		return keys;
	}	
}
