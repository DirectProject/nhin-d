/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico chris.lomonico@surescripts.com
    Umesh Madan     umeshma@microsoft.com
    Ali Emami       aliemami@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Runtime.Caching;
//TODO: Revisit 
namespace Health.Direct.Common.Caching
{
    /// <summary>
    /// An LRU Cache with TTL support
    /// Cached objects have a string key that identifies them uniquely
    /// Items are removed from the cache:
    ///  - when they expire
    ///  - when there is memory pressure and the cache must be trimmed
    /// </summary>
    /// <typeparam name="T">Type of Class which will be stored in the cache</typeparam>
    public class CachingBase <T> where T : class
    {
        HashSet<string> m_keys;  // Set of keys added to the cache
        CacheEntryRemovedCallback m_removeCallback;        
        CacheItemPriority m_priority;
        private static ObjectCache _cache = MemoryCache.Default;
        private CacheItemPolicy policy = null;

        /// <summary>
        /// Initializes a new instance of CachingBase
        /// </summary>
        public CachingBase()
            : this(CacheItemPriority.Default)
        {
        }
                
        /// <summary>
        /// Initializes a new instance of the <b>CachingBase[V];</b> class.
        /// </summary>
        protected CachingBase(CacheItemPriority priority)
        {
            m_keys = new HashSet<string>();
            m_removeCallback = new CacheEntryRemovedCallback(this.OnCachedItemRemoved);
            m_priority = priority;
        }
        
        /// <summary>
        /// Gets the CacheCount of the CachingBase
        /// </summary>
        /// <value></value>
        public int CacheCount
        {
            get
            {
                return m_keys.Count;
            }
        }
        
        /// <summary>
        /// Event for notification of an item's expiration from the cache
        /// </summary>
        public event Action<CachingBase<T>, string> CacheItemExpired;
        
        /// <summary>
        /// Gets a value out of the cache denoted by the key suffix supplied
        /// </summary>
        /// <param name="key">key for the item to retrieve</param>
        /// <returns>Instance of type <typeparamref name="T"/> if found in cache; otherwise <c>null</c></returns>
        public T Get(string key)
        {
            // Delegate parameter checking to the Cache object
            return _cache[key] as T;
        }
        
        /// <summary>
        /// Return the keys in the system
        /// Takes a current snapshot of known keys. 
        /// Since this is an 
        /// </summary>
        /// <returns></returns>
        public string[] GetKeys()
        {
            lock(m_keys)
            {
                string[] keys = new string[m_keys.Count];
                m_keys.CopyTo(keys);
                return keys;
            }
        }
                
        /// <summary>
        /// Puts an item into the cache using the key and value supplied in parameters
        /// </summary>
        /// <remarks>
        /// Update will occurr on item if item is put into the cache that already exists under the same key
        /// </remarks>
        /// <param name="key">string containing the unqiue key of the item</param>
        /// <param name="value">value to be stored in the cache</param>
        public void Put(string key, T value)
        {
            Put(key, value, null);
        }

        /// <summary>
        /// Adds an items to the cache using the specified key and ttl
        /// </summary>
        /// <remarks>
        /// Going to assume that a put with an existing key will not throw an exception but should rather
        /// update the item in the cache
        /// </remarks>
        /// <param name="key">key referencing the item in the cache</param>
        /// <param name="value">Instance of the type <typeparamref name="T"/> to be added to the cache</param>
        /// <param name="ttl">Timepsan used to denote the duration of an items existence in the cache</param>
        public void Put(string key, T value, TimeSpan? ttl)
        {

            policy = new CacheItemPolicy();
            policy.Priority = m_priority;
            if (ttl != null)
            {
                policy.AbsoluteExpiration = DateTime.UtcNow.Add(ttl.Value);
            }
            else
            {
                policy.AbsoluteExpiration = DateTime.MaxValue;
            }
            policy.RemovedCallback = m_removeCallback;

            // Add inside cache 
            _cache.Set(key, value, policy);
            this.AddKey(key);
        }

        /// <summary>
        /// Provides callback functionality when an item is removed from the cache
        /// </summary>
        /// <remarks>
        /// Only when an item has expired in the cache should it be removed from the cache keys list
        /// </remarks>
       /// <param name="args"><seealso cref="CacheEntryRemovedArguments"/></param>
        protected void OnCachedItemRemoved(CacheEntryRemovedArguments args) 
            // string key, object value, CacheEntryRemovedReason reason)
        {
            if (args.RemovedReason == CacheEntryRemovedReason.Removed) 
            {
                return;
            }
            
            this.RemoveKey(args.CacheItem.Key);
            this.NotifyExpired(args.CacheItem.Key);
        }

        /// <summary>
        /// Removes an item from the cache denoted by the key suffix
        /// </summary>
        /// <param name="key">String containing the suffis of the key for the item to be removed</param>
        public bool Remove(string key)
        {
            try
            {
                // Delegate parameter checking to the Cache object
                // Runtime cache does its own thread safety
                _cache.Remove(key);
                return this.RemoveKey(key);
            }
            catch
            {
            }
            
            return false;
        }

        /// <summary>
        /// Removes all items from the cache
        /// </summary>
        /// <remarks>
        /// If the cache is being cleaned out, it should be safe to assume that a lock is not needed
        /// to stop values from being written to the cache/list of keys while they are being cleaned
        /// code utilizing this method should take this into consideration
        /// if need be locks can be added to stop any io on the storage objects
        /// </remarks>
        public virtual void RemoveAll()
        {
            string[] keys = this.GetKeys();
            // remove each matching item from the cache
            foreach (string key in keys)
            {
                RemoveKey(key);
            }
        }
        
        bool AddKey(string key)
        {
            try
            {
                lock(m_keys)
                {
                    return m_keys.Add(key);
                }
            }
            catch
            {
            }
            
            return false;
        }
        
        bool RemoveKey(string key)
        {
            try
            {
                lock(m_keys)
                {
                    return m_keys.Remove(key);
                }
            }
            catch
            {
            }
            return false;
        }
        
        /// <summary>
        /// Method to raise event if event is subscribed to
        /// </summary>
        /// <param name="key">String containing the key of the item in the cache that expired</param>
        void NotifyExpired(string key)
        {
            Action<CachingBase<T>, string> cacheItemExpired = CacheItemExpired;
            if (cacheItemExpired == null)
            {
                return;
            }
                        
            try
            {
                cacheItemExpired(this, key);
            }
            catch
            {
            }
        }
    }
}