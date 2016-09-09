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

package org.nhindirect.common.crypto.jceproviders.safenetprotect.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore.Entry;
import java.security.KeyStore.LoadStoreParameter;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;

/**
 * Wrapper for the key store.  Needed to key store type parameters.  All methods delegate to an underlying implementation.
 * @author Greg Meyer
 * @since 2.1
 */
public class KeyS extends KeyStoreSpi
{
	protected static final String CLAZZ_NAME = "au.com.safenet.crypto.provider.slot0.CryptokiKeyStore";
	
	protected Class<?> internalClazz;
	
	protected KeyStoreSpi internalKeyStore;
	
	/**
	 * Constructor
	 */
	public KeyS()
	{
		super();
		
		try
		{
			internalClazz = this.getClass().getClassLoader().loadClass(CLAZZ_NAME);
			
			internalKeyStore = KeyStoreSpi.class.cast(internalClazz.newInstance());
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Key engineGetKey(String alias, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException
	{
		return internalKeyStore.engineGetKey(alias, password);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Certificate[] engineGetCertificateChain(String alias)
	{
		return internalKeyStore.engineGetCertificateChain(alias);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Certificate engineGetCertificate(String alias)
	{
		return internalKeyStore.engineGetCertificate(alias);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date engineGetCreationDate(String alias)
	{
		return internalKeyStore.engineGetCreationDate(alias);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException
	{
		internalKeyStore.engineSetKeyEntry(alias, key, password, chain);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) throws KeyStoreException
	{
		internalKeyStore.engineSetKeyEntry(alias, key, chain);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineSetCertificateEntry(String alias, Certificate cert) throws KeyStoreException
	{
		internalKeyStore.engineSetCertificateEntry(alias, cert);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineDeleteEntry(String alias) throws KeyStoreException
	{
		internalKeyStore.engineDeleteEntry(alias);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Enumeration<String> engineAliases()
	{
		return internalKeyStore.engineAliases();	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean engineContainsAlias(String alias)
	{
		return internalKeyStore.engineContainsAlias(alias);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int engineSize()
	{
		return internalKeyStore.engineSize();	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean engineIsKeyEntry(String alias)
	{
		return internalKeyStore.engineIsKeyEntry(alias);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean engineIsCertificateEntry(String alias)
	{
		return internalKeyStore.engineIsKeyEntry(alias);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String engineGetCertificateAlias(Certificate cert)
	{
		return internalKeyStore.engineGetCertificateAlias(cert);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineStore(OutputStream stream, char[] password)
			throws IOException, NoSuchAlgorithmException, CertificateException
	{
		internalKeyStore.engineStore(stream, password);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineStore(LoadStoreParameter param) throws IOException, NoSuchAlgorithmException, CertificateException
	{
		internalKeyStore.engineStore(param);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineLoad(InputStream stream, char[] password)
			throws IOException, NoSuchAlgorithmException, CertificateException
	{
		internalKeyStore.engineLoad(stream, password);		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineLoad(LoadStoreParameter param) throws IOException, NoSuchAlgorithmException, CertificateException
	{
		internalKeyStore.engineLoad(param);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Entry engineGetEntry(String alias, ProtectionParameter protParam)
			throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException
	{
		return internalKeyStore.engineGetEntry(alias, protParam);		
	}

	@Override
	public void engineSetEntry(String alias, Entry entry, ProtectionParameter protParam) throws KeyStoreException
	{
		internalKeyStore.engineSetEntry(alias, entry, protParam);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean engineEntryInstanceOf(String alias, Class<? extends Entry> entryClass)
	{
		return internalKeyStore.engineEntryInstanceOf(alias, entryClass);	
	}
	
	
}
