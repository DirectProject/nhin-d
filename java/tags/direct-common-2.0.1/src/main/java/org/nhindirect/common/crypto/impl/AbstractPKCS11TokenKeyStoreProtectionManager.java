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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStore.Entry;
import java.security.Provider;
import java.security.Security;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.common.crypto.MutableKeyStoreProtectionManager;
import org.nhindirect.common.crypto.PKCS11Credential;
import org.nhindirect.common.crypto.WrappableKeyProtectionManager;
import org.nhindirect.common.crypto.exceptions.CryptoException;

/**
 * Abstract base class for accessing key store pass phrases from a PKCS11 token.  Concrete implementations 
 * define methods for logging into the token.
 * <p>
 * For key wrapping, this class utilizes the AES/CBC/PKCS5Padding algorithm with a static initialization vector.
 * @author Greg Meyer
 * @since 1.3
 */
public abstract class AbstractPKCS11TokenKeyStoreProtectionManager implements MutableKeyStoreProtectionManager, WrappableKeyProtectionManager
{

	private static final Log LOGGER = LogFactory.getFactory().getInstance(AbstractPKCS11TokenKeyStoreProtectionManager.class);
	
	public static final String SUNPKCS11_KEYSTORE_PROVIDER_NAME = "sun.security.pkcs11.SunPKCS11";
	public static final String DEFAULT_KESTORE_TYPE = "PKCS11";
	public static final String WRAP_ALGO = "AES/CBC/PKCS5Padding";
	
	public static final byte[] IV_BYTES = {0x10, 0x37, 0x65, 0x12, 0x73, 0x27, 0x41, 0x27, 0x14, 0x33, 0x52, 0x07, 0x20, 0x60, 0x49, 0x01};
	
	protected PKCS11Credential credential;
	protected String keyStorePassPhraseAlias;
	protected String privateKeyPassPhraseAlias;
	protected KeyStore ks;
	protected String keyStoreType;
	protected String keyStoreProviderName;
	protected String pcks11ConfigFile;
	protected InputStream keyStoreSource;
	
	
	
	/**
	 * Empty constructor.
	 * @throws CryptoException
	 */
	public AbstractPKCS11TokenKeyStoreProtectionManager() throws CryptoException
	{
		this.credential = null;
		this.keyStorePassPhraseAlias = "";
		this.privateKeyPassPhraseAlias = "";
		this.keyStoreType = DEFAULT_KESTORE_TYPE;
		this.keyStoreProviderName = "";
		this.pcks11ConfigFile = "";
		this.keyStoreSource = null;
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
		this.keyStoreType = DEFAULT_KESTORE_TYPE;
		this.keyStoreProviderName = "";
		this.pcks11ConfigFile = "";
		this.keyStoreSource = null;
		
		initTokenStore();
	}
	
	protected void loadProvider() throws CryptoException
	{
		try
		{
			// first see if we need to add a PKCS11 provider or custom provider
			
			if (!StringUtils.isEmpty(this.keyStoreProviderName))
			{
				if (this.keyStoreProviderName.equals(SUNPKCS11_KEYSTORE_PROVIDER_NAME))
				{
					// we want to add a SunPKCS11 provider...
					// this provider requires a config file
					if (StringUtils.isEmpty(this.pcks11ConfigFile))
						throw new IllegalStateException("SunPKCS11 providers require a configuration file.  There is not one set.");
					
					// check and see if this is one of the same providers that is already loaded
					final InputStream inStream = FileUtils.openInputStream(new File(this.pcks11ConfigFile));
					
					final Properties props = new Properties();
					props.load(inStream);
					IOUtils.closeQuietly(inStream);
					
					boolean providerFound = false;
					
					final String requestedName = props.getProperty("name");
					
					// check if this provider exists
					if (!StringUtils.isEmpty(requestedName) && Security.getProvider(requestedName) != null)
						providerFound = true;
					
					if (!providerFound)
					{
						// dynamic load... some class loaders may have issues, so use dynamic loading
						final Class<?> provider = this.getClass().getClassLoader().loadClass("sun.security.pkcs11.SunPKCS11");
						final Constructor<?> ctor = provider.getConstructor(String.class);
						Security.addProvider((Provider)ctor.newInstance(this.pcks11ConfigFile));
					}
				}
				else
				{
					// create the new provider
					final Class<?> provider = this.getClass().getClassLoader().loadClass(this.keyStoreProviderName);
					
					// check if the provider is already loaded
					boolean providerFound = false;
					for (Provider existingProv : Security.getProviders())
					{
						if (existingProv.getClass().equals(provider))
						{
							providerFound = true;
							break;
						}
					}
					
					if (!providerFound)
						Security.addProvider((Provider)provider.newInstance());
					
				}
			}
		}
		catch (Exception e)
		{
			throw new CryptoException("Error loading PKCS11 provder", e);
		}
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
	
	public void setKeyStoreType(String keyStoreType)
	{
		this.keyStoreType = keyStoreType;
	}
	
	public void setKeyStoreSource(InputStream keyStoreSource)
	{
		this.keyStoreSource = keyStoreSource;
	}
	
	public void setKeyStoreSourceAsString(String keyStoreSource)
	{
		try
		{
			this.keyStoreSource = new ByteArrayInputStream(keyStoreSource.getBytes("UTF-8"));
		}
		catch (Exception e)
		{
			/* do quietly, no-op */
		}
	}
	
	public void setKeyStoreProviderName(String keyStoreProviderName)
	{
		this.keyStoreProviderName = keyStoreProviderName;
	}
	
	public void setPcks11ConfigFile(String pcks11ConfigFile)
	{
		this.pcks11ConfigFile = pcks11ConfigFile;
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
	public synchronized Map<String, Key> getAllKeys() throws CryptoException
	{
		final Map<String, Key> keys = new HashMap<String, Key>();
		
		try 
		{
			final Enumeration<String> aliases = ks.aliases();
			while (aliases.hasMoreElements())
			{
				final String alias = aliases.nextElement();
				if (ks.isKeyEntry(alias))
				{
					try
					{
						final Key key = ks.getKey(alias, null);
						// make sure it's a secret key
						
						if (key instanceof SecretKey)
						{
							keys.put(alias, key);
						}
					}
					catch (Exception e)
					{
						// no-op, this might be a key that we don't care about
					}
				}
			}
		} 
		catch (Exception e) 
		{
			throw new CryptoException("Error extracting keys from PKCS11 token", e);
		}
		
		return keys;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Key getKey(String keyName) throws CryptoException
	{
		return safeGetKeyWithRetry(keyName);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Key getPrivateKeyProtectionKey() throws CryptoException
	{
		return safeGetKeyWithRetry(privateKeyPassPhraseAlias);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Key getKeyStoreProtectionKey() throws CryptoException
	{
		return safeGetKeyWithRetry(keyStorePassPhraseAlias);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Entry> getAllEntries() throws CryptoException 
	{
		final Map<String, Entry> entries = new HashMap<String, Entry>();
		
		try 
		{
			final Enumeration<String> aliases = ks.aliases();
			while (aliases.hasMoreElements())
			{
				final String alias = aliases.nextElement();
				if (ks.isKeyEntry(alias))
				{
					try
					{
						final Entry entry = ks.getEntry(alias, null);

						entries.put(alias, entry);
					}
					catch (Exception e)
					{
						// no-op, this might be a key that we don't care about
					}
				}
			}
		} 
		catch (Exception e) 
		{
			throw new CryptoException("Error extracting entries from PKCS11 token", e);
		}
		
		return entries;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Entry getEntry(String entryName) throws CryptoException 
	{
		return getSafeEntryWtihRetry(entryName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPrivateKeyProtectionKey(Key key) throws CryptoException 
	{
		safeSetKeyWithRetry(privateKeyPassPhraseAlias, key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPrivateKeyProtectionKeyAsBytes(byte[] key) throws CryptoException 
	{
		try 
		{
			final Key keySpec = new SecretKeySpec(key, "");
			safeSetKeyWithRetry(privateKeyPassPhraseAlias, keySpec);
		} 
		catch (CryptoException e)
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new CryptoException("Error storing key store protection into PKCS11 token", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPrivateKeyProtectionKeyAsString(String key) throws CryptoException 
	{
		try 
		{
			final Key keySpec = new SecretKeySpec(key.getBytes(), "");
			safeSetKeyWithRetry(privateKeyPassPhraseAlias, keySpec);
		} 
		catch (CryptoException e)
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new CryptoException("Error storing key store protection into PKCS11 token", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearPrivateKeyProtectionKey() throws CryptoException
	{
		safeDeleteKeyWithRetry(privateKeyPassPhraseAlias);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setKeyStoreProtectionKey(Key key) throws CryptoException 
	{
		safeSetKeyWithRetry(keyStorePassPhraseAlias, key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setKeyStoreProtectionKeyAsBytes(byte[] key) throws CryptoException 
	{
		try 
		{
			final Key keySpec = new SecretKeySpec(key, "");
			safeSetKeyWithRetry(keyStorePassPhraseAlias, keySpec);
		} 
		catch (CryptoException e)
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new CryptoException("Error storing key store protection into PKCS11 token", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setKeyStoreProtectionKeyAsString(String key) throws CryptoException 
	{
		try 
		{
			final Key keySpec = new SecretKeySpec(key.getBytes(), "");
			safeSetKeyWithRetry(keyStorePassPhraseAlias, keySpec);
		} 
		catch (CryptoException e)
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new CryptoException("Error storing key store protection into PKCS11 token", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearKeyStoreProtectionKey() throws CryptoException 
	{
		this.safeDeleteKeyWithRetry(keyStorePassPhraseAlias);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setKey(String alias, Key key) throws CryptoException
	{
		safeSetKeyWithRetry(alias, key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearKey(String alias) throws CryptoException
	{
		// make sure the key exists first
		if (this.getKey(alias) != null)
			safeDeleteKeyWithRetry(alias);		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEntry(String alias, Entry entry) throws CryptoException
	{
		safeSetEntryWithRetry(alias, entry);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearEntry(String alias) throws CryptoException
	{
		if (this.getEntry(alias) != null)
			safeDeleteKeyWithRetry(alias);	
	}
	
	protected synchronized void safeSetKeyWithRetry(String alias, Key key) throws CryptoException
	{
		boolean reloadAndRetry = false;
		
		try 
		{
			ks.setKeyEntry(alias, key, null, null);
		} 
		catch (Exception e) 
		{
			LOGGER.warn("Could not set key entry on first attemp.  Will attempt to reload the key store and try again");
			reloadAndRetry = true;
		}
		
		if (reloadAndRetry)
		{
			this.reloadKeyStore();
			
			try 
			{
				ks.setKeyEntry(alias, key, null, null);
			} 
			catch (Exception e) 
			{	
				throw new CryptoException("Error setting key in PKCS11 token", e);
			}
		}
	}
	
	protected synchronized void safeDeleteKeyWithRetry(String alias) throws CryptoException
	{
		boolean reloadAndRetry = false;
		
		try 
		{
			ks.deleteEntry(alias);
		} 
		catch (Exception e) 
		{
			LOGGER.warn("Could not delete key entry on first attemp.  Will attempt to reload the key store and try again");
			reloadAndRetry = true;
		}
		
		if (reloadAndRetry)
		{
			this.reloadKeyStore();
			
			try 
			{
				ks.deleteEntry(alias);
			} 
			catch (Exception e) 
			{	
				throw new CryptoException("Error deleting key from PKCS11 token", e);
			}
		}
	}
	
	protected synchronized void safeSetEntryWithRetry(String alias, Entry entry) throws CryptoException
	{
		boolean reloadAndRetry = false;
		
		try 
		{
			ks.setEntry(alias, entry, null);
		} 
		catch (Exception e) 
		{
			LOGGER.warn("Could not set entry on first attemp.  Will attempt to reload the key store and try again");
			reloadAndRetry = true;
		}
		
		if (reloadAndRetry)
		{
			this.reloadKeyStore();
			
			try 
			{
				ks.setEntry(alias, entry, null);
			} 
			catch (Exception e) 
			{	
				throw new CryptoException("Error setting entry in PKCS11 token", e);
			}
		}
	}
		
	protected synchronized Key safeGetKeyWithRetry(String alias) throws CryptoException
	{
		boolean reloadAndRetry = false;
		
		try 
		{
			return ks.getKey(alias, null);
		} 
		catch (Exception e) 
		{
			LOGGER.warn("Could not get key entry on first attemp.  Will attempt to reload the key store and try again");
			reloadAndRetry = true;
		}
		
		if (reloadAndRetry)
		{
			this.reloadKeyStore();
			
			try 
			{
				return ks.getKey(alias, null);
			} 
			catch (Exception e) 
			{	
				throw new CryptoException("Error getting key from PKCS11 token", e);
			}
		}
		
		return null;
	}
	
	protected synchronized Entry getSafeEntryWtihRetry(String alias) throws CryptoException
	{
		boolean reloadAndRetry = false;
		
		try 
		{
			return ks.getEntry(alias, null);
		} 
		catch (Exception e) 
		{
			LOGGER.warn("Could not get entry on first attemp.  Will attempt to reload the key store and try again");
			reloadAndRetry = true;
		}
		
		if (reloadAndRetry)
		{
			this.reloadKeyStore();
			
			try 
			{
				return ks.getEntry(alias, null);
			} 
			catch (Exception e) 
			{	
				throw new CryptoException("Error getting entry from PKCS11 token", e);
			}
		}
		
		return null;
	}
	
	/**
	 * In some cases, the connection to the underlying key store may become disconnected and the keystore needs to be reloaded 
	 */
	protected void reloadKeyStore() throws CryptoException
	{
		// close the key store and reload it
		ks = null;
		
		initTokenStore();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public KeyStore getKS()
	{
		return ks;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public byte[] wrapWithSecretKey(SecretKey kek, Key keyToWrap) throws CryptoException 
	{
		final IvParameterSpec iv = new IvParameterSpec(IV_BYTES);
		try
		{
			final Cipher wrapCipher = Cipher.getInstance(WRAP_ALGO, ks.getProvider().getName());
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
		final IvParameterSpec iv = new IvParameterSpec(IV_BYTES);
		try
		{
			final Cipher unwrapCipher = Cipher.getInstance(WRAP_ALGO, ks.getProvider().getName());
			unwrapCipher.init(Cipher.UNWRAP_MODE, kek, iv);
	
			return unwrapCipher.unwrap(wrappedData, keyAlg, keyType);
		}
		catch (Exception e)
		{
			throw new CryptoException("Failed to unwrap key: " + e.getMessage(), e);
		}
	}
}
