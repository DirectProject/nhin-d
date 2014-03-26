package org.nhindirect.common.crypto;

import java.security.Key;

import org.nhindirect.common.crypto.exceptions.CryptoException;

/**
 * Interface definition for accessing key store pass phrases.  PKCS12 keystores generally have two layers of protection with each being options:
 * <br>
 * <ul>
 * <li>Pass phrase protection for the entire key store.</li>
 * <li>Pass phrase protection for private keys associated with a public key</li>
 * <br>
 * This interface assumes that all private keys are stores with the same pass phrase.  Pass phrases may stored in a multitude of mediums such as protected files,
 * databases, or PKCS11 tokens.
 * @author Greg Meyer
 * @since 1.3
 *
 */
public interface KeyStoreProtectionManager 
{
	/**
	 * Gets the key protecting the key store as a whole.
	 * @return The key protecting the key store as a whole.
	 * @throws CryptoException
	 */
	public Key getPrivateKeyProtectionKey() throws CryptoException;
	
	/**
	 * Gets the key protecting private keys in the key store.
	 * @return The key protecting private keys in the key store.
	 * @throws CryptoException
	 */
	public Key getKeyStoreProtectionKey() throws CryptoException;
}
