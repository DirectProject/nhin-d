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

import java.security.cert.X509Certificate;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.behavior.ICompositeCacheAttributes;
import org.apache.jcs.engine.behavior.IElementAttributes;
import org.nhindirect.common.crypto.KeyStoreProtectionManager;
import org.nhindirect.stagent.cert.CacheableCertStore;
import org.nhindirect.stagent.cert.CertCacheFactory;
import org.nhindirect.stagent.cert.CertStoreCachePolicy;
import org.nhindirect.stagent.cert.CertificateStore;
import org.nhindirect.stagent.options.OptionsManager;
import org.nhindirect.stagent.options.OptionsParameter;

/**
 * Implementation of certificate store where certificate and keys are stored in a KeyStoreProtectionManager.
 * @author Greg Meyer
 * @since 2.1
 */
public class CacheableKeyStoreManagerCertificateStore extends AbstractKeyStoreManagerCertificateStore implements CacheableCertStore
{
	private static final String CACHE_NAME = "CACHEABLE_PKCS11_STORE_CERT_CACHE";
	
	protected static final int DEFAULT_MAX_CAHCE_ITEMS = 1000;
	protected static final int DEFAULT_CACHE_TTL = 3600; // 1 hour
	
	protected JCS cache;
	protected CertStoreCachePolicy cachePolicy;
	
	private static final Log LOGGER = LogFactory.getFactory().getInstance(CacheableKeyStoreManagerCertificateStore.class);
	
	/**
	 * Constructor
	 */
	public CacheableKeyStoreManagerCertificateStore()
	{
		this(null, null);
	}
	
	/**
	 * Constructor
	 * @param storeMgr Keystore manager that holds the private and public keys
	 */
	public CacheableKeyStoreManagerCertificateStore(KeyStoreProtectionManager storeMgr)
	{
		this(storeMgr, null);
	}
	
	/**
	 * Constructor
	 * @param storeMgr Keystore manager that holds the private and public keys
	 * @param cachePolicy The cache policy of the certificate store.
	 */
	public CacheableKeyStoreManagerCertificateStore(KeyStoreProtectionManager storeMgr, CertStoreCachePolicy cachePolicy)
	{
		super(storeMgr);
		this.cachePolicy = cachePolicy;	
		
		createCache();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCachePolicy(CertStoreCachePolicy policy) 
	{		
		this.cachePolicy = policy;
		applyCachePolicy(policy);
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
				// no-op
			}
		}
	}
	
	private synchronized JCS getCache()
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
			cache = CertCacheFactory.getInstance().getCertCache(CACHE_NAME, cachePolicy == null ? getDefaultPolicy() : cachePolicy);	
			if (cachePolicy == null)
				cachePolicy = getDefaultPolicy();
		}
		///CLOVER:OFF
		catch (CacheException e)
		{
			LOGGER.warn("CacheablePKCS11CertificateStore - Could not create certificate cache " + CACHE_NAME, e);
		}
		///CLOVER:ON
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	///CLOVER:OFF
	public void flush(boolean purgeBootStrap) 
	{
		
		if (cache != null)
		{
			try
			{
				cache.clear();
			}
			catch (CacheException e)
			{
				/**
				 * no-op
				 */
			}
		
		}
	}
	///CLOVER:ON
	
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
    	
    	final JCS cache = getCache();
    	
    	if (cache != null)
    	{
    		retVal = (Collection<X509Certificate>)cache.get(realSubjectName);
    		if (retVal == null || retVal.size() == 0)
    			retVal = super.getCertificates(subjectName);
    	}
    	else // cache miss
    		retVal = super.getCertificates(subjectName);
    	
		if (retVal == null || retVal.size() == 0)
		{
			LOGGER.info("getCertificates(String subjectName) - Could not find a PKCS11 certificate for subject " + subjectName);
		}
		else
		{
			try
			{
				if (cache != null)
					cache.put(realSubjectName, retVal);
			}
			catch (CacheException e)
			{
				/*
				 * no-opss
				 */
			}
		}
    	
    	return retVal;
    }  
	
    
    ///COVER:OFF
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBootStrap(CertificateStore bootstrapStore) 
	{
		/*
		 * PKCS11 private keys are generally not extractable, so it doesn't make sense to cache them offline
		 */
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadBootStrap() 
	{
		/*
		 * PKCS11 private keys are generally not extractable, so it doesn't make sense to cache them offline
		 */
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadBootStrap(CertificateStore bootstrapStore) 
	{
		/*
		 * PKCS11 private keys are generally not extractable, so it doesn't make sense to cache them offline
		 */
	}
    ///COVER:OFF
	
	private CertStoreCachePolicy getDefaultPolicy()
	{
		return new DefaultGenericPKCS11CachePolicy();
	}
	
	/**
	 * Default cache policy
	 * @author Greg Meyer
	 *
	 */
	private static class DefaultGenericPKCS11CachePolicy implements CertStoreCachePolicy
	{
		protected final int maxItems;
		protected final int subjectTTL;
		
		public DefaultGenericPKCS11CachePolicy()
		{
			OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.CACHABLE_PKCS11_CERT_RESOLVER_MAX_CACHE_SIZE);
			maxItems =  OptionsParameter.getParamValueAsInteger(param, DEFAULT_MAX_CAHCE_ITEMS); 
			
			param = OptionsManager.getInstance().getParameter(OptionsParameter.CACHABLE_PKCS11_CERT_RESOLVER_CACHE_TTL);
			subjectTTL =  OptionsParameter.getParamValueAsInteger(param, DEFAULT_CACHE_TTL); 
		}
		
		public int getMaxItems() 
		{
			return maxItems;
		}

		public int getSubjectTTL() 
		{
			return subjectTTL;
		}
		
	}
}
