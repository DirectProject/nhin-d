package org.nhindirect.common.crypto.impl;

import java.security.KeyStore;

import org.nhindirect.common.crypto.PKCS11Credential;
import org.nhindirect.common.crypto.exceptions.CryptoException;

/**
 * Implementation of PKCS11 token store that generally does not get detached from the system.  Credentials are accessed via a PKCS11Credential implemenation.
 * @author Greg Meyer
 * @since 1.3
 */
public class StaticPKCS11TokenKeyStoreProtectionManager extends AbstractPKCS11TokenKeyStoreProtectionManager
{
	
	/**
	 * Empty constructor
	 * @throws CryptoException
	 */
	public StaticPKCS11TokenKeyStoreProtectionManager() throws CryptoException
	{
		super();
	}
	
	/**
	 * Constructs the store with a credential manager and aliases.
	 * @param credential The credentials to log into the store.
	 * @param keyStorePassPhraseAlias The alias name of the key store key in the PKCS11 token.
	 * @param privateKeyPassPhraseAlias The alias name of the private key protection key in the PKCS11 token.
	 * @throws CryptoException
	 */
	public StaticPKCS11TokenKeyStoreProtectionManager(PKCS11Credential credential, String keyStorePassPhraseAlias, String privateKeyPassPhraseAlias) throws CryptoException
	{
		super(credential, keyStorePassPhraseAlias, privateKeyPassPhraseAlias);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void initTokenStore() throws CryptoException
	{
		try
		{
			ks = KeyStore.getInstance("PKCS11");
			ks.load(null, credential.getPIN()); 
		}
		catch (Exception e)
		{
			throw new CryptoException("Error initializing PKCS11 token", e);
		}
	}
}
