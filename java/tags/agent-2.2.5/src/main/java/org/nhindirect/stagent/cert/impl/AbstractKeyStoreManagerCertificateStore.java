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

package org.nhindirect.stagent.cert.impl;

import java.security.KeyStore.Entry;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.nhindirect.common.crypto.KeyStoreProtectionManager;
import org.nhindirect.common.crypto.MutableKeyStoreProtectionManager;
import org.nhindirect.stagent.AgentError;
import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.cert.CertificateStore;
import org.nhindirect.stagent.cert.Thumbprint;
import org.nhindirect.stagent.cert.X509CertificateEx;

public abstract class AbstractKeyStoreManagerCertificateStore extends CertificateStore
{
	protected KeyStoreProtectionManager storeMgr;
	
	///CLOVER:OFF
	public AbstractKeyStoreManagerCertificateStore()
	{
		
	}
	///CLOVER:ON
	
	public AbstractKeyStoreManagerCertificateStore(KeyStoreProtectionManager storeMgr)
	{
		this.storeMgr = storeMgr;
	}
	
	public void setKeyStoreManager(KeyStoreProtectionManager storeMgr)
	{
		this.storeMgr = storeMgr;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
    @Override
    public Collection<X509Certificate> getAllCertificates()
    {
    	final Collection<X509Certificate> retVal = new ArrayList<X509Certificate>();
    	try
    	{
	    	for (Map.Entry<String, Entry> entry :  storeMgr.getAllEntries().entrySet())
	    	{
	    		if (entry.getValue() instanceof PrivateKeyEntry)
	    		{
	    			final PrivateKeyEntry privEntry = (PrivateKeyEntry)entry.getValue();
	    			retVal.add(X509CertificateEx.fromX509Certificate(
	    					(X509Certificate)privEntry.getCertificate(), privEntry.getPrivateKey()));
	    		}
	    	}
	    	
	    	return retVal;
    	}
    	///CLOVER:OFF
    	catch (Exception e)
    	{
    		throw new NHINDException(AgentError.Unexpected, "Failed to get key entries from PKCS11 store.", e);
    	}
    	///CLOVER:ON
    }   
    
	@Override
	public boolean contains(X509Certificate cert) 
	{
		return getAllCertificates().contains(cert);
	}
	
	@Override
	public void add(X509Certificate cert) 
	{
		if (!(storeMgr instanceof MutableKeyStoreProtectionManager))
			throw new IllegalStateException("The store manager is a MutableKeyStoreProtectionManager instance");
		
		if (!(cert instanceof X509CertificateEx) || !((X509CertificateEx)cert).hasPrivateKey())
			throw new IllegalArgumentException("PKCS11 certificates require a private key");
		
		
		final X509CertificateEx exCert = (X509CertificateEx)cert;
		// keys stores require aliases, and a given subject may include multiple certificates
		// to avoid possible collisions, this will use the certificate thumbprint
		final String alias = Thumbprint.toThumbprint(cert).toString();
		
		final PrivateKeyEntry entry = new PrivateKeyEntry(exCert.getPrivateKey(), new Certificate[] {cert});
		try
		{
			((MutableKeyStoreProtectionManager)storeMgr).setEntry(alias, entry);
		}
		///CLOVER:OFF
		catch (Exception e)
		{
			throw new NHINDException(AgentError.Unexpected, "Failed to add key entry into PKCS11 store.", e);
		}
		///CLOVER:ON
	}

	@Override
	public void remove(X509Certificate cert) 
	{
		if (!(storeMgr instanceof MutableKeyStoreProtectionManager))
			throw new IllegalStateException("The store manager is a MutableKeyStoreProtectionManager instance");
		
    	try
    	{
    		String aliasToRemove = null;
	    	for (String alias : storeMgr.getAllEntries().keySet())
	    	{
	    		final Entry entry = storeMgr.getEntry(alias);
	    		if (entry instanceof PrivateKeyEntry)
	    		{
	    			final PrivateKeyEntry privEntry = (PrivateKeyEntry)entry;
	    			if (cert.equals(privEntry.getCertificate()))
	    			{
	    				aliasToRemove = alias;
	    				break;
	    			}
	    		}
	    	}
	    	if (aliasToRemove != null)
	    	{
	    		final MutableKeyStoreProtectionManager mutMgr = (MutableKeyStoreProtectionManager)storeMgr;
	    		mutMgr.clearEntry(aliasToRemove);
	    	}
    	}
    	///CLOVER:OFF
    	catch (Exception e)
    	{
    		throw new NHINDException(AgentError.Unexpected, "Failed to remove key entry from PKCS11 store.", e);
    	}
    	///CLOVER:ON
		
	}
}
