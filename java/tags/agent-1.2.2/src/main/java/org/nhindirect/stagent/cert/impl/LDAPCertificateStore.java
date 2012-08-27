/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Manjiri Namjoshi      NM019057@cerner.com
 
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

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.behavior.ICompositeCacheAttributes;
import org.apache.jcs.engine.behavior.IElementAttributes;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.cert.CacheableCertStore;
import org.nhindirect.stagent.cert.CertStoreCachePolicy;
import org.nhindirect.stagent.cert.CertificateStore;
import org.nhindirect.stagent.cert.Thumbprint;

import com.google.inject.Inject;

/**
 * Certificate store backed by LDAP-based provider directories (RFC 4398) for dynamic lookup and a configurable local cache of off line lookup. 
 * By default the service uses the local node's DNS server configuration for initial DNS queries and a key store implementation for 
 * off line lookups.  The default key store creates new file named NHINDKeyStore with a default file and private key password if the 
 * file does not already exist.
 * <br>
 * 
 * <br>
 * This service caches LDAP entries independently of OS resolver.  Caching can be tuned using the {@link CacheableCertStore} interface.
 * By default, the time to live of a subjects LDAP certs is one day and the maximum number of entries is 1000 before the cache
 * is pruned to make room for new entries.  Pruning by default uses a least recently used algorithm.
 * 
 * @author NM019057
 *
 */
public class LDAPCertificateStore extends CertificateStore implements
		CacheableCertStore {
	
	private static final Log LOGGER = LogFactory.getFactory().getInstance(LDAPCertificateStore.class);
	private static final String CACHE_NAME = "LDAP_REMOTE_CERT_CACHE";
	
	private CertificateStore localStoreDelegate;
	private JCS cache;
	private CertStoreCachePolicy cachePolicy;
	private LdapCertUtil ldapCertUtil;

	/**
	 * Constructs a service using the machines local DNS server configuration and a default key store implementation for
	 * local lookups.
	 */
	public LDAPCertificateStore()
	{
		localStoreDelegate = createDefaultLocalStore();
		loadBootStrap();
	}
	
	/**
	 * Constructs a service using the server list for DNS lookups and a key store implementation for
	 * local lookups.
	 * @param servers The DNS users to use for initial certificate resolution.
	 * @param localStoreDelegate The certificate store used for local lookups.  This store is also the boot strap store.
	 */
	@Inject 
	public LDAPCertificateStore(LdapCertUtilImpl ldapCertUtil, 
			CertificateStore bootstrapStore, CertStoreCachePolicy policy)
	{

		this((LdapCertUtil)ldapCertUtil, bootstrapStore, policy);
	}	
	
	public LDAPCertificateStore(LdapCertUtil ldapCertUtil, 
			CertificateStore bootstrapStore, CertStoreCachePolicy policy)
	{
		
		this.ldapCertUtil = ldapCertUtil;
		
		this.cachePolicy = policy;
		if (bootstrapStore == null) {
			this.localStoreDelegate = createDefaultLocalStore();
		}
		else {
			this.localStoreDelegate = bootstrapStore;
		}
		loadBootStrap();
	}	
	
	protected synchronized JCS getCache()
	{
		if (cache == null)
			createCache();
		
		return cache;
	}
	
	private void createCache()
	{
		try
		{
			// create instance
			// create cache with a different region everytime a new cache is created
			cache = JCS.getInstance(CACHE_NAME+UUID.randomUUID());		
			applyCachePolicy(cachePolicy == null ? getDefaultPolicy() : cachePolicy);
					
		}
		catch (CacheException e)
		{
			// TODO: log error
		}
	}
	
	private void applyCachePolicy(CertStoreCachePolicy policy)
	{
		if (getCache() != null)
		{
			try
			{
				ICompositeCacheAttributes attributes = cache.getCacheAttributes();
				attributes.setMaxObjects(policy.getMaxItems());
				attributes.setUseLateral(false);
				attributes.setUseRemote(false);
				cache.setCacheAttributes(attributes);
				
				IElementAttributes eattributes = cache.getDefaultElementAttributes();
				eattributes.setMaxLifeSeconds(policy.getSubjectTTL());
				eattributes.setIsEternal(false);
				eattributes.setIsLateral(false);
				eattributes.setIsRemote(false);		
				
				cache.setDefaultElementAttributes(eattributes);
			}
			catch (CacheException e)
			{
				// TODO: Handle exception
			}
		}
	}
	
	private CertStoreCachePolicy getDefaultPolicy()
	{
		return new DefaultLDAPCachePolicy();
	}
	
	/*
	 * Create the default local key store service.
	 */
	protected CertificateStore createDefaultLocalStore()
	{
		KeyStoreCertificateStore retVal = new KeyStoreCertificateStore(new File("NHINKeyStore"), "nH!NdK3yStor3", "31visl!v3s");
		
		return retVal;
	}

	@Override
	public void add(X509Certificate cert) 
	{		
    	if (contains(cert))
    		throw new IllegalArgumentException("Cert already contained in store.  Use update() to update a certificate");
    	
	}

	@Override
	public boolean contains(X509Certificate cert) 
	{
		Collection<X509Certificate> foundCerts;
		
		String subject = CryptoExtensions.getSubjectAddress(cert);
		if (subject == null || subject.isEmpty())
			// this should not happen, but in case need to get the entire cert list
			foundCerts = getAllCertificates();
		else
			foundCerts = getCertificates(subject);
			
		if (foundCerts != null)
		{
			Thumbprint searchCertTP = Thumbprint.toThumbprint(cert);
			
			for (X509Certificate foundCert : foundCerts)
				if (Thumbprint.toThumbprint(foundCert).equals(searchCertTP))
					return true;
		}
		
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */  
    @SuppressWarnings("unchecked")
	@Override
    public Collection<X509Certificate> getCertificates(String subjectName)
    {
      	String realSubjectName;
    	int index;
		if ((index = subjectName.indexOf("EMAILADDRESS=")) > -1)
			realSubjectName = subjectName.substring(index + "EMAILADDRESS=".length());
		else
			realSubjectName = subjectName;    	    	
    	
    	Collection<X509Certificate> retVal;
    	
    	JCS cache = getCache();
    	
    	if (cache != null)
    	{
    		// try to get it from the cache first
    		retVal = (Collection<X509Certificate>)cache.get(realSubjectName);
    		
    		// the certificate is not in the cache, so now hit the real server
    		if (retVal == null || retVal.size() == 0) {
    			retVal = ldapCertUtil.ldapSearch(realSubjectName);
    			
    			// add or update the cache and the local cert store
    			if (retVal != null && retVal.size() > 0 ) {
    				
        			// don't cache wildcard searches
    				if (!subjectName.contains("*"))
    				{
	    				try
	    				{
	    					// first add the certificates to the cache 
	    					cache.putSafe(realSubjectName, retVal);
	    				}
	    				catch (CacheException e)
	    				{
	    					// TODO: handle exception
	    					LOGGER.error("Error adding certificates to the cache: " + e.getMessage(), e);
	    				}
	    				
	    				// now add or update the local cert store
	    				if(localStoreDelegate != null) {
	    					addOrUpdateLocalStoreDelegate(retVal);
	    				}
    				}
    			}
    			// couldn't retrieve the certificate from the real server, so have to go to the bootstrap
    			else {
    				if(localStoreDelegate!=null) {
    					retVal = localStoreDelegate.getCertificates(realSubjectName); // last ditch effort is to go to the bootstrap cache
    				}
    			}
    		}
    	}
    	else // cache miss
    	{
    		retVal = ldapCertUtil.ldapSearch(realSubjectName);
    		if(localStoreDelegate!=null) {
    			if (retVal == null ||  retVal.size() == 0) {
    				retVal = localStoreDelegate.getCertificates(realSubjectName); // last ditch effort is to go to the bootstrap cache
    			}
    			else if (!subjectName.contains("*"))
    			{
    				// now add or update the local cert store
    				addOrUpdateLocalStoreDelegate(retVal);
    			}
    		}
    	}
    	return retVal;
    }     
    
    protected void addOrUpdateLocalStoreDelegate(Collection<X509Certificate> retVal) {
    	if(retVal!=null && localStoreDelegate!=null) {
    		for (X509Certificate cert : retVal)
    		{	
    			if (localStoreDelegate.contains(cert)) 
    				localStoreDelegate.update(cert);
    			else
    				localStoreDelegate.add(cert);
    		}
    	}
    }

	@Override
	public Collection<X509Certificate> getAllCertificates() 
	{
		/*
		 * don't hit the cache for this... need to go back
		 * to the orignal source
		 */
		return getCertificates("*");
	}

	@Override
	public void remove(X509Certificate cert) 
	{
		// TODO Auto-generated method stub

	}

	public void flush(boolean purgeBootStrap) 
	{
		// TODO Auto-generated method stub

	}

	public void loadBootStrap() 
	{
		// TODO Auto-generated method stub

	}

	public void loadBootStrap(CertificateStore bootstrapStore) 
	{
		// TODO Auto-generated method stub

	}

	public void setBootStrap(CertificateStore bootstrapStore) 
	{
		// TODO Auto-generated method stub

	}

	public void setCachePolicy(CertStoreCachePolicy policy) 
	{
		
	}
	
	private static class DefaultLDAPCachePolicy implements CertStoreCachePolicy
	{

		public int getMaxItems() 
		{
			return 1000; 
		}

		public int getSubjectTTL() 
		{
			return 3600 * 24; // 1 day
		}
		
	}

}
