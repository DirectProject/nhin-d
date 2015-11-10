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

package org.nhindirect.gateway.smtp.config.cert.impl;

import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.behavior.ICompositeCacheAttributes;
import org.apache.jcs.engine.behavior.IElementAttributes;
import org.nhind.config.rest.CertificateService;
import org.nhindirect.common.crypto.KeyStoreProtectionManager;
import org.nhindirect.config.model.utils.CertUtils;
import org.nhindirect.config.model.utils.CertUtils.CertContainer;
import org.nhindirect.stagent.AgentError;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.cert.CacheableCertStore;
import org.nhindirect.stagent.cert.CertCacheFactory;
import org.nhindirect.stagent.cert.CertStoreCachePolicy;
import org.nhindirect.stagent.cert.CertificateStore;
import org.nhindirect.stagent.cert.WrappedOnDemandX509CertificateEx;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.options.OptionsManager;
import org.nhindirect.stagent.options.OptionsParameter;

public class ConfigServiceRESTCertificateStore extends CertificateStore implements CacheableCertStore
{
 	/**
 	 * Integer value specifies the maximum number of certificates that can be held in the web service certificate cache.
 	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cert.wsresolver.MaxCacheSize
 	 */
    public final static String WS_CERT_RESOLVER_MAX_CACHE_SIZE = "WS_CERT_RESOLVER_MAX_CACHE_SIZE";     
    
 	/**
 	 * Integer value specifies the time to live in seconds that a certificate can be held in the web service certificate cache.
 	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cert.wsresolver.CacheTTL
 	 */
    public final static String WS_CERT_RESOLVER_CACHE_TTL = "WS_CERT_RESOLVER_CACHE_TTL"; 
    
	protected static final int DEFAULT_WS_MAX_CAHCE_ITEMS = 1000;
	protected static final int DEFAULT_WS_TTL = 3600; // 1 hour
    
	private static final Log LOGGER = LogFactory.getFactory().getInstance(ConfigServiceCertificateStore.class);
	
	private static final String CACHE_NAME = "CONFIG_SERVICE_CERT_CACHE";
	
	protected JCS cache;
	protected CertStoreCachePolicy cachePolicy;
	protected CertificateService certService;
	protected KeyStoreProtectionManager mgr;
	
	static
	{
		initJVMParams();
		
		CryptoExtensions.registerJCEProviders();
	}
	
	public synchronized static void initJVMParams()
	{
		/*
		 * Web service resolver parameters
		 */
		final Map<String, String> JVM_PARAMS = new HashMap<String, String>();
		JVM_PARAMS.put(WS_CERT_RESOLVER_MAX_CACHE_SIZE, "org.nhindirect.stagent.cert.wsresolver.MaxCacheSize");	
		JVM_PARAMS.put(WS_CERT_RESOLVER_CACHE_TTL, "org.nhindirect.stagent.cert.wsresolver.CacheTTL");		
		
		OptionsManager.addInitParameters(JVM_PARAMS);
	}
	
	/**
	 * Constructs a service using the configuration service proxy and a default key store implementation for
	 * local lookups.
	 * @param proxy The configuration service proxy;
	 */
	public ConfigServiceRESTCertificateStore(CertificateService certService)
	{
		setCertificateService(certService);		
		createCache();
		// no longer create 
		// the local store by default
	}	


	
	/**
	 * Constructs a service using the configuration service proxy and a key store implementation for
	 * local lookups.
	 * @param proxy The configuration service proxy;
	 * @param bootstrapStore The certificate store used for local lookups.  This store is also the boot strap store.
	 * @param policy The certificate cache policy
	 */
	public ConfigServiceRESTCertificateStore(CertificateService certService, 
			CertificateStore bootstrapStore, CertStoreCachePolicy policy)
	{
		this(certService, bootstrapStore, policy, null);
					
	}	
	
	public ConfigServiceRESTCertificateStore(CertificateService certService, 
			CertificateStore bootstrapStore, CertStoreCachePolicy policy,
			KeyStoreProtectionManager mgr)
	{
		this.cachePolicy = policy;	
		createCache();

		setCertificateService(certService);		
		setKeyStoreProectionManager(mgr);		
	}	
	
	public void setCertificateService(CertificateService certService)
	{
		this.certService = certService;
	}	
	
	public void setKeyStoreProectionManager(KeyStoreProtectionManager mgr)
	{
		this.mgr = mgr;
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
			cache = CertCacheFactory.getInstance().getCertCache(CACHE_NAME, cachePolicy == null ? getDefaultPolicy() : cachePolicy);
			if (cachePolicy == null)
				cachePolicy = getDefaultPolicy();		
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
		return new DefaultConfigStoreCachePolicy();
	}
	
	/**
	 * {@inheritDoc}
	 * Not supported in this certificate store implementation.
	 */
    public boolean contains(X509Certificate cert)
    {
    	throw new UnsupportedOperationException("Contains is not supported.");
    }	
    
	/**
	 * {@inheritDoc}
	 * Not supported in this certificate store implementation.
	 */
    public void add(X509Certificate cert)
    {
    	throw new UnsupportedOperationException("Add is not supported.");
    }    
	
	/**
	 * {@inheritDoc}
	 * Not supported in this certificate store implementation.
	 */
    public void remove(X509Certificate cert)
    {
    	throw new UnsupportedOperationException("Remove is not supported.");
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
    		retVal = (Collection<X509Certificate>)cache.get(realSubjectName);
    		if (retVal == null || retVal.size() == 0)
    		{
    			retVal = this.lookupFromConfigStore(realSubjectName);
    			if (retVal == null || retVal.size() == 0)
    			{
    				LOGGER.info("getCertificates(String subjectName) - Could not find a ConfigService certificate for subject " + subjectName);
    			}
    		}
    	}
    	else // cache miss
    	{
    		retVal = this.lookupFromConfigStore(realSubjectName);
    		if (retVal.size() == 0)
    			
    		{

    			LOGGER.info("getCertificates(String subjectName) - Could not find a ConfigService certificate for subject " + subjectName);
    		}
    	}
    	
    	return retVal;
    }  
    
    private Collection<X509Certificate> lookupFromConfigStore(String subjectName)
    {    	
    	String domain;
    	
    	Collection<org.nhindirect.config.model.Certificate> certificates;
    	try
    	{
    		certificates = certService.getCertificatesByOwner(subjectName);
    	}
    	catch (Exception e)
    	{
    		throw new NHINDException("WebService error getting certificates by subject: " + e.getMessage(), e);
    	}
    	
    	if (certificates == null || certificates.isEmpty())
    	{
    		// try again with the domain name
    		int index;
    		if ((index = subjectName.indexOf("@")) > -1)
    			domain = subjectName.substring(index + 1);
    		else
    			domain = subjectName;
    		
        	try
        	{
        		certificates = certService.getCertificatesByOwner(domain);
        	}
        	catch (Exception e)
        	{
        		throw new NHINDException("WebService error getting certificates by domain: " + e.getMessage(), e);
        	}
    	}
    	
    	if (certificates == null || certificates.isEmpty())
    		return Collections.emptyList();
    	
    	Collection<X509Certificate> retVal = new ArrayList<X509Certificate>();
    	for (org.nhindirect.config.model.Certificate cert : certificates)
    	{
    		X509Certificate storeCert = certFromData(cert.getData());
    		retVal.add(storeCert);
    	}
    	
		// add to JCS and cache
		try
		{
			if (cache != null)
				cache.put(subjectName, retVal);
		}
		catch (CacheException e)
		{
			/*
			 * TODO: handle exception
			 */
		}    	
    	
    	return retVal;
    }
    
	/**
	 * {@inheritDoc}
	 */
    @Override
    public Collection<X509Certificate> getAllCertificates()
    {
    	// get everything from the configuration service.... no caching here
    	Collection<org.nhindirect.config.model.Certificate> certificates;
    	try
    	{
    		certificates = certService.getAllCertificates();
    	}
    	catch (Exception e)
    	{
    		throw new NHINDException("WebService error getting all certificates: " + e.getMessage(), e);
    	}
    	 
    	// purge everything
    	this.flush(true);
    	
    	if (certificates == null || certificates.isEmpty())
    		return Collections.emptyList();
    	
    	// convert to X509Certificates and store
    	Collection<X509Certificate> retVal = new ArrayList<X509Certificate>();
    	for (org.nhindirect.config.model.Certificate cert : certificates)
    	{
    		X509Certificate storeCert = certFromData(cert.getData());
    		retVal.add(storeCert);
    		
    		// add to JCS and cache
				try
				{
					if (cache != null)
						cache.put(cert.getOwner(), retVal);
				}
				catch (CacheException e)
				{
					/*
					 * TODO: handle exception
					 */
				}
    	}
    	
    	return retVal;
    }    
    
    private X509Certificate certFromData(byte[] data)
    {
    	X509Certificate retVal = null;
        try 
        {
        	// first check for wrapped data
        	final CertContainer container = CertUtils.toCertContainer(data);
        	if (container.getWrappedKeyData() != null)
        	{
        		// this is a wrapped key
        		// make sure we have a KeyStoreManager configured
        		if (this.mgr == null)
        		{
        			throw new NHINDException(AgentError.Unexpected,  
        					"Resolved certifiate has wrapped data, but resolver has not been configured to unwrap it.");
        		}
        		
        		// create a new wrapped certificate object
        		retVal = WrappedOnDemandX509CertificateEx.fromX509Certificate(mgr, container.getCert(), container.getWrappedKeyData());
        		return retVal;
        	}
        	
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            
            // lets try this a as a PKCS12 data stream first
            try
            {
            	KeyStore localKeyStore = KeyStore.getInstance("PKCS12", CryptoExtensions.getJCEProviderName());
            	
            	localKeyStore.load(bais, "".toCharArray());
            	Enumeration<String> aliases = localKeyStore.aliases();


        		// we are really expecting only one alias 
        		if (aliases.hasMoreElements())        			
        		{
        			String alias = aliases.nextElement();
        			X509Certificate cert = (X509Certificate)localKeyStore.getCertificate(alias);
        			
    				// check if there is private key
    				Key key = localKeyStore.getKey(alias, "".toCharArray());
    				if (key != null && key instanceof PrivateKey) 
    				{
    					retVal = X509CertificateEx.fromX509Certificate(cert, (PrivateKey)key);
    				}
    				else
    					retVal = cert;
    					
        		}
            }
            catch (Exception e)
            {
            	// must not be a PKCS12 stream, go on to next step
            }
   
            if (retVal == null)            	
            {
            	//try X509 certificate factory next       
                bais.reset();
                bais = new ByteArrayInputStream(data);

                retVal = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);            	
            }
            bais.close();
        } 
        catch (Exception e) 
        {
            throw new NHINDException("Data cannot be converted to a valid X.509 Certificate", e);
        }
        
        return retVal;
    }
    
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
				 * TODO: handle exception
				 */
			}
		}
	}

	public void loadBootStrap() 
	{
		/* no -op */
	}
	
	public void loadBootStrap(CertificateStore bootstrapStore) 
	{
		// do nothing now
		// local bootstraps were not a really a good 
		// idea.. getting rid of the concept in this implementation
	}

	public void setBootStrap(CertificateStore bootstrapStore) 
	{
	}

	public void setCachePolicy(CertStoreCachePolicy policy) 
	{		
		this.cachePolicy = policy;
		applyCachePolicy(policy);
	}	
	
	public static class DefaultConfigStoreCachePolicy implements CertStoreCachePolicy
	{
		protected final int maxItems;
		protected final int subjectTTL;
		
		public DefaultConfigStoreCachePolicy()
		{
			OptionsParameter param = OptionsManager.getInstance().getParameter(WS_CERT_RESOLVER_MAX_CACHE_SIZE);
			maxItems =  OptionsParameter.getParamValueAsInteger(param, DEFAULT_WS_MAX_CAHCE_ITEMS); 
			
			param = OptionsManager.getInstance().getParameter(WS_CERT_RESOLVER_CACHE_TTL);
			subjectTTL =  OptionsParameter.getParamValueAsInteger(param, DEFAULT_WS_TTL); 
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
