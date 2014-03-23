package org.nhindirect.common.crypto.impl;

import java.security.Key;
import java.security.KeyStore;

import org.nhindirect.common.crypto.KeyStoreProtectionManager;
import org.nhindirect.common.crypto.PKCS11Credential;
import org.nhindirect.common.crypto.exceptions.CryptoException;

/**
 * Abstract base class for accessing key store pass phrases from a PKCS11 token.  Concrete implementations 
 * define methods for logging into the token.
 * @author Greg Meyer
 * @since 1.3
 */
public abstract class AbstractPKCS11TokenKeyStoreProtectionManager implements KeyStoreProtectionManager
{
	protected PKCS11Credential credential;
	protected String keyStorePassPhraseAlias;
	protected String privateKeyPassPhraseAlias;
	protected KeyStore ks;
	
	/**
	 * Empty constructor.
	 * @throws CryptoException
	 */
	public AbstractPKCS11TokenKeyStoreProtectionManager() throws CryptoException
	{
		this.credential = null;
		this.keyStorePassPhraseAlias = "";
		this.privateKeyPassPhraseAlias = "";
	}
	
	/**
	 * Constructor that takes a credential interface for logging into the token.
	 * @param credential The credential used to log into the token.
	 * @param keyStorePassPhraseAlias The alias name of the key store key in the PKCS11 token.
	 * @param privateKeyPassPhraseAlias  The alias name of the private key protection key in the PKCS11 token.
	 * @throws CryptoException
	 */
	public AbstractPKCS11TokenKeyStoreProtectionManager(PKCS11Credential credential, String keyStorePassPhraseAlias, String privateKeyPassPhraseAlias) throws CryptoException
	{
		this.credential = credential;
		this.keyStorePassPhraseAlias = keyStorePassPhraseAlias;
		this.privateKeyPassPhraseAlias = privateKeyPassPhraseAlias;
		
		initTokenStore();
	}
	
	/**
	 * Sets the credential used to log into the token.
	 * @param credential The credential used to log into the token.
	 */
	public void setCredential(PKCS11Credential credential)
	{
		this.credential = credential;
	}
	
	/**
	 * Sets the alias name of the key store key in the PKCS11 token.
	 * @param keyStorePassPhraseAlias The alias name of the key store key in the PKCS11 token.
	 */
	public void setKeyStorePassPhraseAlias(String keyStorePassPhraseAlias)
	{
		this.keyStorePassPhraseAlias = keyStorePassPhraseAlias;
	}
	
	/**
	 * Sets the alias name of the private key protection key in the PKCS11 token.
	 * @param privateKeyPassPhraseAlias the alias name of the private key protection key in the PKCS11 token.
	 */
	public void setPrivateKeyPassPhraseAlias(String privateKeyPassPhraseAlias)
	{
		this.privateKeyPassPhraseAlias = privateKeyPassPhraseAlias;
	}
	
	/**
	 * Initializes access to the token.  This is implementation specific and may require user interaction.
	 * @throws CryptoException
	 */
	public abstract void initTokenStore() throws CryptoException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Key getPrivateKeyProtectionKey() throws CryptoException
	{
		try 
		{
			return ks.getKey(privateKeyPassPhraseAlias, null);
		} 
		catch (Exception e) 
		{
			throw new CryptoException("Error extracting private key protection from PKCS11 token", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Key getKeyStoreProtectionKey() throws CryptoException
	{
		try 
		{
			return ks.getKey(keyStorePassPhraseAlias, null);
		} 
		catch (Exception e) 
		{
			throw new CryptoException("Error extracting key store protection from PKCS11 token", e);
		}
	}
}
