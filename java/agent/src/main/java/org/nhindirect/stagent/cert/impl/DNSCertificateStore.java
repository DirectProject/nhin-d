/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
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

import java.io.File;
import java.net.UnknownHostException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.behavior.ICompositeCacheAttributes;
import org.apache.jcs.engine.behavior.IElementAttributes;
import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.cert.CacheableCertStore;
import org.nhindirect.stagent.cert.CertStoreCachePolicy;
import org.nhindirect.stagent.cert.CertificateStore;
import org.nhindirect.stagent.cert.impl.annotation.DNSCertStoreBootstrap;
import org.nhindirect.stagent.cert.impl.annotation.DNSCertStoreCachePolicy;
import org.nhindirect.stagent.cert.impl.annotation.DNSCertStoreServers;
import org.xbill.DNS.CERTRecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.NSRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.ResolverConfig;
import org.xbill.DNS.Type;
import org.xbill.DNS.security.CERTConverter;

import com.google.inject.Inject;

/**
 * Certificate store backed by DNS CERT records (RFC 4398) for dynamic lookup and a configurable local cache of off line lookup. 
 * By default the service uses the local node's DNS server configuration for initial DNS queries and a key store implementation for 
 * off line lookups.  The default key store creates new file named NHINDKeyStore with a default file and private key password if the 
 * file does not already exist.
 * <br>
 * Depending the OS TCP implementation, lookups may be cached in native DNS resolvers resulting in optimized lookups.  
 * However this may not always in line with HIPS policies.  Refer to you OS DNS implementation for more details.
 * <br>
 * This service caches DNS entries independently of OS resolver.  Caching can be tuned using the {@link CacheableCertStore} interface.
 * By default, the time to live of a subjects DNS certs is one day and the maximum number of entries is 1000 before the cache
 * is pruned to make room for new entries.  Pruning by default uses a least recently used algorithm.
 * 
 * @author Greg Meyer
 *
 */
public class DNSCertificateStore extends CertificateStore implements CacheableCertStore
{
	private static final String CACHE_NAME = "DNS_REMOTE_CERT_CACHE";
	
	private CertificateStore localStoreDelegate;
	private List<String> servers = new ArrayList<String>();
	private JCS cache;
	private CertStoreCachePolicy cachePolicy;

	/**
	 * Constructs a service using the machines local DNS server configuration and a default key store implementation for
	 * local lookups.
	 */
	public DNSCertificateStore()
	{
		setServers(null);		
		localStoreDelegate = createDefaultLocalStore();
		loadBootStrap();
	}
	
	/**
	 * Constructs a service using the server list for DNS lookups and a default key store implementation for
	 * local lookups.
	 * @param servers The DNS users to use for initial certificate resolution.
	 */
	public DNSCertificateStore(Collection<String> servers)
	{
		setServers(servers);
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
	public DNSCertificateStore(@DNSCertStoreServers Collection<String> servers, 
			@DNSCertStoreBootstrap CertificateStore bootstrapStore, @DNSCertStoreCachePolicy CertStoreCachePolicy policy)
	{
		if (bootstrapStore == null)
			throw new IllegalArgumentException();
		
		setServers(servers);
		
		this.cachePolicy = policy;			
		this.localStoreDelegate = bootstrapStore;			
		loadBootStrap();
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
			cache = JCS.getInstance(CACHE_NAME);		
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
		return new DefaultDNSCachePolicy();
	}
	
	/*
	 * Create the default local key store service.
	 */
	private CertificateStore createDefaultLocalStore()
	{
		KeyStoreCertificateStore retVal = new KeyStoreCertificateStore(new File("DNSNHINKeyStore"), "nH!NdK3yStor3", "31visl!v3s");
		
		return retVal;
	}
	
	/**
	 * Sets the DNS servers used for initial certificate lookups.  This replaces all currently set DNS server configuration.  This method is thread safe and
	 * may block if a current lookup is currently in progress.
	 * @param servers The DNS servers used for initial certificate lookups.
	 */
	public void setServers(Collection<String> servers)
	{
		if (servers == null || servers.size() == 0)
		{
			String[] configedServers = ResolverConfig.getCurrentConfig().servers();
			
			if (configedServers != null)
			{
				this.servers.addAll(Arrays.asList(configedServers));
			}		
		}		
		else
		{
			this.servers.clear();
			this.servers.addAll(servers);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
    public boolean contains(X509Certificate cert)
    {
    	return localStoreDelegate == null ? false : localStoreDelegate.contains(cert);
    }	
    
	/**
	 * {@inheritDoc}
	 */
    public void add(X509Certificate cert)
    {
    	if (localStoreDelegate != null)
    		localStoreDelegate.add(cert);
    }    
	
	/**
	 * {@inheritDoc}
	 */
    public void remove(X509Certificate cert)
    {
    	if (localStoreDelegate != null)
    		localStoreDelegate.remove(cert);
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
    		retVal = (Collection<X509Certificate>)cache.get(realSubjectName);//localStoreDelegate.getCertificates(subjectName);
    		if (retVal == null || retVal.size() == 0)
    			retVal = this.lookupDNS(realSubjectName);
    	}
    	else // cache miss
    	{
    		retVal = this.lookupDNS(realSubjectName);
    		if (retVal.size() == 0 && localStoreDelegate != null)
    			retVal = localStoreDelegate.getCertificates(realSubjectName); // last ditch effort is to go to the bootstrap cache
    	}
    	
    	return retVal;
    }     
    
	/**
	 * {@inheritDoc}
	 */
    @Override
    public Collection<X509Certificate> getAllCertificates()
    {
    	return (localStoreDelegate == null) ? null : localStoreDelegate.getAllCertificates(); 
    }    
    
	private Collection<X509Certificate> lookupDNS(String name)
	{
		String domain;
		String lookupName = name.replace('@', '.');
		Collection<X509Certificate> retVal = new ArrayList<X509Certificate>();
				
		// get the domain of the address
		int index;
		if ((index = name.indexOf("@")) > -1)
			domain = name.substring(index + 1);
		else
			domain = name;
		
		try
		{
			// try the configured servers first
			Lookup lu = new Lookup(new Name(lookupName), Type.CERT);
			lu.setResolver(createExResolver(servers.toArray(new String[servers.size()]),2, 3)); // default retries is 3, limite to 2
			
			Record[] retRecords = lu.run();
			
			if (retRecords == null || retRecords.length == 0)
			{
				Name tempDomain;
				
				// try to find the resource's name server records
				// the address may be an alias so check if there is a CNAME record
				lu = new Lookup(new Name(lookupName), Type.CNAME);
				lu.setResolver(createExResolver(servers.toArray(new String[servers.size()]),2,3));
				
				retRecords = lu.run();	
				if (retRecords != null && retRecords.length > 0)
				{
					CNAMERecord cnameRect = (CNAMERecord)retRecords[0];
					tempDomain = cnameRect.getTarget();
				}
				else
					tempDomain = new Name(domain);  // not a CNAME						
					
				// look for a name server records
				while (tempDomain.labels() > 1)
				{
					lu = new Lookup(tempDomain, Type.NS);
					lu.setResolver(createExResolver(servers.toArray(new String[servers.size()]), 2, 3));
					retRecords = lu.run();
					
					if (retRecords != null && retRecords.length > 0)
						break;
					
					tempDomain = new Name(tempDomain.toString().substring((tempDomain.toString().indexOf(".") + 1)));
				}
				
				if (retRecords == null || retRecords.length == 0)
					return retVal; // can't find a name server... bail
				
				String[] remoteServers = new String[retRecords.length];
				for (int i = 0; i < remoteServers.length - 0; ++i)
				{
					remoteServers[i] = ((NSRecord)retRecords[i]).getTarget().toString();
				}
				
				// search the name servers for the cert
				lu = new Lookup(new Name(lookupName), Type.CERT);
				lu.setResolver(createExResolver(remoteServers, 2, 3));
				
				retRecords = lu.run();
			}
						
			if (retRecords != null)
			{
				retVal = new ArrayList<X509Certificate>();
				for (Record rec : retRecords)
				{
					if (rec instanceof CERTRecord) 
					{
						Certificate certToAdd = CERTConverter.parseRecord((CERTRecord)rec);
						if (certToAdd instanceof X509Certificate) // may not be an X509Cert
							retVal.add((X509Certificate)certToAdd);
					}
				}			
			}
			else if (domain.length() < name.length())  // if this is an email address, do the search again and the host level
				retVal = lookupDNS(domain);
		}
		catch (Exception e)
		{
			throw new NHINDException(e);
		}
		
		// add or update the local cert store
		if (retVal != null && retVal.size() > 0 && localStoreDelegate != null)
		{
			for (X509Certificate cert : retVal)
			{

				
				if (localStoreDelegate.contains(cert)) 
					localStoreDelegate.update(cert);
				else
					localStoreDelegate.add(cert);
			}			
			try
			{
				if (cache != null)
					cache.put(name, retVal);
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

	public void loadBootStrap() 
	{
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
	}

	public void loadBootStrap(CertificateStore bootstrapStore) 
	{
		if (localStoreDelegate == null)
		{
			throw new IllegalArgumentException();
		}
		this.localStoreDelegate = bootstrapStore;
		loadBootStrap();
	}

	public void setBootStrap(CertificateStore bootstrapStore) 
	{
		if (localStoreDelegate == null)
		{
			throw new IllegalArgumentException();
		}
		this.localStoreDelegate = bootstrapStore;		
	}

	public void setCachePolicy(CertStoreCachePolicy policy) 
	{		
		this.cachePolicy = policy;
		applyCachePolicy(policy);
	}
	
	private static class DefaultDNSCachePolicy implements CertStoreCachePolicy
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
	
	private ExtendedResolver createExResolver(String[] servers, int retries, int timeout)
	{
		ExtendedResolver retVal = null;
		try
		{
			retVal = new ExtendedResolver(servers);
			retVal.setRetries(retries);
			retVal.setTimeout(timeout);
		}
		catch (UnknownHostException e) {/* no-op */}
		return retVal;
	}
}
