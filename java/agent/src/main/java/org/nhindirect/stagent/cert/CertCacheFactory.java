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

package org.nhindirect.stagent.cert;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.behavior.ICompositeCacheAttributes;
import org.apache.jcs.engine.behavior.IElementAttributes;

/**
 * Factory class for creating instances of JCS based certificate caches.  Caches are keyed by name (case sensitive).
 * <br>
 * The factory implements a singleton pattern for both the factory itself and named caches.
 * @author Greg Meyer
 * @since 1.3
 */
public class CertCacheFactory 
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(CertCacheFactory.class);
	
	protected static CertCacheFactory INSTANCE;
	
	protected final Map<String, JCS> certCacheMap;
	
	/**
	 * Gets the instance of the cache factory.
	 * @return The cache factory.
	 */
	public static synchronized CertCacheFactory getInstance()
	{
		if (INSTANCE == null)
			INSTANCE = new CertCacheFactory();
		
		return INSTANCE;
	}
	
	/*
	 * private contructor
	 */
	private CertCacheFactory()
	{
		certCacheMap = new HashMap<String, JCS>();
	}
	
	/**
	 * Retrieves a cert cache by name.  Caches are created using a singleton pattern meaning one and only once instance of a cache for a given name
	 * is ever created.
	 * @param cacheName The name of the cache to retrieve.
	 * @param cachePolicy Policy to apply to the cache
	 * @return The certificate cache for the given cache name.
	 * @throws CacheException Thrown if the cache cannot be created.
	 */
	public synchronized JCS getCertCache(String cacheName, CertStoreCachePolicy cachePolicy) throws CacheException
	{
		JCS retVal = certCacheMap.get(cacheName);
		
		if (retVal == null)
		{
			try
			{
				// create instance
				retVal = JCS.getInstance(cacheName);
				if (cachePolicy != null)
					applyCachePolicy(retVal, cachePolicy);
				
				certCacheMap.put(cacheName, retVal);
			}
			catch (CacheException e)
			{
				LOGGER.warn("Failed to create JCS cache " + cacheName, e);
				throw e;
			}
		}
		
		return retVal;
	}
	
	public synchronized void flushAll()
	{
		for (Entry<String, JCS> entry : certCacheMap.entrySet())
		{
			try
			{
				LOGGER.info("Flushing cache " + entry.getKey());
				entry.getValue().clear();
			}
			catch (CacheException e) {/* no-op */}
		}
	}
	
	/*
	 * Apply a policy to the cache
	 */
	private void applyCachePolicy(JCS cache, CertStoreCachePolicy policy) throws CacheException
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
	
}
