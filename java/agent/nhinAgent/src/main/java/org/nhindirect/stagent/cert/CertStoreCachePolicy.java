package org.nhindirect.stagent.cert;

/**
 * Cache policy setting for a cacheable cert store.
 * @author Greg Meyer
 *
 */
public interface CertStoreCachePolicy 
{
	/**
	 * The maximum amount of time a subject's certificates can remain in the cache before getting purged.  To maintain 
	 * coherency, this setting is independent of the number of times a cache hit occurs per subject.
	 * @return The maxiumum amount of time in seconds that a subject's certificates will remain in the cache before being purged.
	 */
	public int getSubjectTTL();
	
	/**
	 * The maximum number of items that can be held in the cache.  Items will be trimmed according to cache policy.  By default
	 * the policy will purged based on least recently used.
	 * @param The maximum number of items that can be held in the cache. 
	 */
	public int getMaxItems();
	
	
}
