package org.nhindirect.common.crypto.impl;

import java.security.Key;
import java.security.KeyStore;

import javax.crypto.spec.SecretKeySpec;

import org.nhindirect.common.crypto.PKCS11Credential;
import org.nhindirect.common.crypto.exceptions.CryptoException;

/**
 * This implementations uses the same login process as the {@link StaticPKCS11TokenKeyStoreProtectionManager}, however the 
 * keystore and private key protection keys are loaded into the manager at init time and cached in the class.  The advantage
 * is that multiple trips do not have to be made to the HSM to get the secret keys per certificate operation.  They disadvantage
 * is that secret keys or cached in process memory.
 * <p>
 * This class is also a generic band aid for systems that get disconnected from their HSMs due to policy and technical reasons.  These
 * systems need access to the secret keys even after they are disconnected.
 * @author Greg Meyer
 * @since 1.4.1
 */
public class StaticCachedPKCS11TokenKeyStoreProtectionManager extends StaticPKCS11TokenKeyStoreProtectionManager
{
	private Key keystoreProtectionKey;
	private Key privateKeyProtectionKey;
	
	/**
	 * Empty constructor
	 * @throws CryptoException
	 */
	public StaticCachedPKCS11TokenKeyStoreProtectionManager() throws CryptoException
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
	public StaticCachedPKCS11TokenKeyStoreProtectionManager(PKCS11Credential credential, String keyStorePassPhraseAlias, String privateKeyPassPhraseAlias) throws CryptoException
	{
		super(credential, keyStorePassPhraseAlias, privateKeyPassPhraseAlias);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void initTokenStore() throws CryptoException
	{
		loadProvider();
		
		try
		{
			ks = KeyStore.getInstance(keyStoreType);
			ks.load(keyStoreSource, credential.getPIN()); 
			
			// preload the 2 secret keys
			keystoreProtectionKey = this.getKey(keyStorePassPhraseAlias);
			privateKeyProtectionKey = this.getKey(privateKeyPassPhraseAlias);
			
			// some HSMs only store references to the keys in these objects and 
			// and still have to go back to the HSM to pull the actual key data
			// create a key object from the encoded data
			keystoreProtectionKey = new SecretKeySpec(keystoreProtectionKey.getEncoded(), "");
			privateKeyProtectionKey = new SecretKeySpec(privateKeyProtectionKey.getEncoded(), "");
			
		}
		catch (Exception e)
		{
			throw new CryptoException("Error initializing PKCS11 token", e);
		}
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
		return keystoreProtectionKey;
	}
}
