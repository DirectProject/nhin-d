/* 
 Copyright (c) 2011, Direct Project
 All rights reserved.

 Authors:
    Ali Emami       aliemami@microsoft.com    
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Threading;
using System.Web;
using System.Web.Caching;

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
    public class Cache<T> where T : class
    {
        CachingBase<T> m_cacheBase; 
        string m_uniqueName = null;
        
        /// <summary>
        /// Caches use a common memory store where each object in the store has a key.
        /// To keep your custom cache from stomping on other caches, use a unique name.
        /// </summary>
        /// <param name="uniqueName">unique name for this Cache</param>
        public Cache(string uniqueName)            
        {
            if (string.IsNullOrEmpty(uniqueName))
            {
                throw new ArgumentException("uniqueName");
            }
            m_uniqueName = uniqueName;

            m_cacheBase = new CachingBase<T>(); 
        }

        /// <summary>
        /// Returns the underlying cache used by this Cache{T} instance. 
        /// </summary>
        public CachingBase<T> BaseCache
        {
            get
            {
                return m_cacheBase; 
            }
        }

        /// <summary>
        /// Builds a key for item in the common memory store given the item's key. 
        /// </summary>
        /// <param name="key">The key of the item to cache.</param>
        /// <returns>String containing the key to use to insert in the common memory store</returns>
        public virtual string BuildKey(string key)
        {
            if (key == null)
            {
                throw new ArgumentNullException("key");
            }

            return string.Format("{0}.{1}", m_uniqueName, key);
        }

        /// <summary>
        /// Gets a value out of the cache using the specified key.
        /// </summary>
        /// <param name="key">key for the item to retrieve</param>
        /// <returns>Instance of type <typeparamref name="T"/> if found in cache; otherwise <c>null</c></returns>
        public virtual T Get(string key)
        {
            return m_cacheBase.Get(BuildKey(key));
        }        

        /// <summary>
        /// Puts an item into the cache using the key and value supplied in parameters
        /// </summary>
        /// <remarks>
        /// Update will occurr on item if item is put into the cache that already exists under the same key
        /// </remarks>
        /// <param name="key">string containing the unqiue key of the item</param>
        /// <param name="value">value to be stored in the cache</param>
        public virtual void Put(string key, T value)
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
        public virtual void Put(string key, T value, TimeSpan? ttl)
        {
            m_cacheBase.Put(BuildKey(key), value, ttl);     
        }

        /// <summary>
        /// Removes an item from the cache denoted by the key suffix
        /// </summary>
        /// <param name="key">String containing the suffis of the key for the item to be removed</param>
        public virtual bool Remove(string key)
        {
            return m_cacheBase.Remove(BuildKey(key));
        }
    }
}