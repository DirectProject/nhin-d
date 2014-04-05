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
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.behavior.ICompositeCacheAttributes;
import org.apache.jcs.engine.behavior.IElementAttributes;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.cert.CacheableCertStore;
import org.nhindirect.stagent.cert.CertCacheFactory;
import org.nhindirect.stagent.cert.CertStoreCachePolicy;
import org.nhindirect.stagent.cert.CertificateStore;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.options.OptionsManager;
import org.nhindirect.stagent.options.OptionsParameter;


/**
 * Certificate store backed by the configuration service.
 * @author Greg Meyer
 *
 */
public class ConfigServiceCertificateStore extends CertificateStore implements CacheableCertStore
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
    
	/**
	 * Integer value that specifies the socket timeout in seconds for a web service record.
	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cert.wsresolver.SOTimeout
	 */
    public final static String WS_CERT_RESOLVER_SO_TIMEOUT = "WS_CERT_RESOLVER_SO_TIMEOUT";

	/**
	 * Integer value that specifies the connection timeout in seconds for a web service record.
	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cert.wsresolver.ConnectionTimeout
	 */
    public final static String WS_CERT_RESOLVER_CONNECTION_TIMEOUT = "WS_CERT_RESOLVER_CONNECTION_TIMEOUT";
	
    
    /**
     * Default connection timeout to the configuration service in milliseconds
     */
    public static final int DEFAULT_WS_CONNECTION_TIMEOUT = 30000; // 30 seconds
    
    /**
     * Default transport timeout to the configuration service in milliseconds
     */
    public static final int DEFAULT_WS_SO_TIMEOUT = 10000; // 10 seconds
    
	protected static final int DEFAULT_WS_MAX_CAHCE_ITEMS = 1000;
	protected static final int DEFAULT_WS_TTL = 3600; // 1 hour
    
	private static final Log LOGGER = LogFactory.getFactory().getInstance(ConfigServiceCertificateStore.class);
	
	private static final String CACHE_NAME = "CONFIG_SERVICE_CERT_CACHE";
	
	protected CertificateStore localStoreDelegate;
	protected JCS cache;
	protected CertStoreCachePolicy cachePolicy;
	protected ConfigurationServiceProxy proxy;
	
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
		JVM_PARAMS.put(WS_CERT_RESOLVER_SO_TIMEOUT, "org.nhindirect.stagent.cert.wsresolver.SOTimeout");		
		JVM_PARAMS.put(WS_CERT_RESOLVER_CACHE_TTL, "org.nhindirect.stagent.cert.wsresolver.CacheTTL");	
		JVM_PARAMS.put(WS_CERT_RESOLVER_CONNECTION_TIMEOUT, "org.nhindirect.stagent.cert.wsresolver.ConnectionTimeout");	
		
		OptionsManager.addInitParameters(JVM_PARAMS);
	}
	
	/**
	 * Constructs a service using the configuration service proxy and a default key store implementation for
	 * local lookups.
	 * @param proxy The configuration service proxy;
	 */
	public ConfigServiceCertificateStore(ConfigurationServiceProxy proxy)
	{
		setConfigurationServiceProxy(proxy);		
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
	public ConfigServiceCertificateStore(ConfigurationServiceProxy proxy, 
			CertificateStore bootstrapStore, CertStoreCachePolicy policy)
	{
		this.cachePolicy = policy;	
		createCache();
		
		// no longer create 
		// the local store by default
		if (bootstrapStore != null)
		{
			this.localStoreDelegate = bootstrapStore;
			loadBootStrap();
		}

		setConfigurationServiceProxy(proxy);		
					
	}	
	
	public void setConfigurationServiceProxy(ConfigurationServiceProxy proxy)
	{
		this.proxy = proxy;
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
    			if (localStoreDelegate != null)
    			{
	    			retVal = localStoreDelegate.getCertificates(realSubjectName); // last ditch effort is to go to the bootstrap cache
	    			if (retVal == null || retVal.size() == 0)
	    			{
	    				LOGGER.info("getCertificates(String subjectName) - Could not find a ConfigService certificate for subject " + subjectName);
	    			}
    			}
    			else 
    				LOGGER.info("getCertificates(String subjectName) - Could not find a ConfigService certificate for subject " + subjectName);
    		}
    	}
    	
    	return retVal;
    }  
    
    private Collection<X509Certificate> lookupFromConfigStore(String subjectName)
    {    	
    	String domain;
    	
    	org.nhind.config.Certificate[] certificates;
    	try
    	{
    		certificates = proxy.getCertificatesForOwner(subjectName, null);
    	}
    	catch (Exception e)
    	{
    		throw new NHINDException("WebService error getting certificates by subject: " + e.getMessage(), e);
    	}
    	
    	if (certificates == null || certificates.length == 0)
    	{
    		// try again with the domain name
    		int index;
    		if ((index = subjectName.indexOf("@")) > -1)
    			domain = subjectName.substring(index + 1);
    		else
    			domain = subjectName;
    		
        	try
        	{
        		certificates = proxy.getCertificatesForOwner(domain, null);
        	}
        	catch (Exception e)
        	{
        		throw new NHINDException("WebService error getting certificates by domain: " + e.getMessage(), e);
        	}
    	}
    	
    	if (certificates == null || certificates.length == 0)
    		return Collections.emptyList();
    	
    	Collection<X509Certificate> retVal = new ArrayList<X509Certificate>();
    	for (org.nhind.config.Certificate cert : certificates)
    	{
    		X509Certificate storeCert = certFromData(cert.getData());
    		retVal.add(storeCert);
    		
			
			if(localStoreDelegate != null)
			{
				if (localStoreDelegate.contains(storeCert)) 
					localStoreDelegate.update(storeCert);
				else
					localStoreDelegate.add(storeCert);
			}
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
    	org.nhind.config.Certificate[] certificates;
    	try
    	{
    		certificates = proxy.listCertificates(0L, 0x8FFF, null);  // hard code to get everything
    	}
    	catch (Exception e)
    	{
    		throw new NHINDException("WebService error getting all certificates: " + e.getMessage(), e);
    	}
    	 
    	// purge everything
    	this.flush(true);
    	
    	if (certificates == null || certificates.length == 0)
    		return Collections.emptyList();
    	
    	// convert to X509Certificates and store
    	Collection<X509Certificate> retVal = new ArrayList<X509Certificate>();
    	for (org.nhind.config.Certificate cert : certificates)
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
				
				if(localStoreDelegate != null)
				{
					if (localStoreDelegate.contains(storeCert)) 
						localStoreDelegate.update(storeCert);
					else
						localStoreDelegate.add(storeCert);
				}
    	}
    	
    	return retVal;
    }    
    
    private X509Certificate certFromData(byte[] data)
    {
    	X509Certificate retVal = null;
        try 
        {
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
		
			if (purgeBootStrap && this.localStoreDelegate != null)
			{
				localStoreDelegate.remove(localStoreDelegate.getAllCertificates());
			}
		}
	}
	
	@SuppressWarnings("unused")
	public void loadBootStrap() 
	{
		///CLOVER:OFF
		if (localStoreDelegate == null)
			throw new IllegalStateException("The boot strap store has not been set.");
		

		JCS cache = null;
		if ((cache = getCache()) != null)
		{
			Map<String, Collection<X509Certificate>> cacheBuilderMap = new HashMap<String, Collection<X509Certificate>>();
			for (X509Certificate cert : localStoreDelegate.getAllCertificates())
			{
				/*
				 * TODO: need to decide how the entries/subjects will be indexed and named
				 */
			}
			
			for (Entry<String, Collection<X509Certificate>> entry : cacheBuilderMap.entrySet())
			{
				try
				{
					cache.put(entry.getKey(), entry.getValue());
				}
				catch (CacheException e)
				{
					/*
					 * TODO: handle exception
					 */
				}
			}
		}
		///CLOVER:ON
	}

	public void loadBootStrap(CertificateStore bootstrapStore) 
	{
		if (bootstrapStore == null)
		{
			throw new IllegalArgumentException();
		}
		this.localStoreDelegate = bootstrapStore;
		loadBootStrap();
	}

	public void setBootStrap(CertificateStore bootstrapStore) 
	{
		loadBootStrap(bootstrapStore);
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
