/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico chris.lomonico@surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Threading;
using System.Web;
using System.Web.Caching;

namespace NHINDirect.Caching
{

    /// <summary>
    /// Delegate used for simple event signature
    /// </summary>
    /// <param name="sender">Object that fired the event</param>
    /// <param name="key">String value intended to denote the key of the item</param>
    public delegate void SimplestringEventDel(Object sender, string key);

    /// <summary>
    /// Provides abstract, base implementation for typed cache class
    /// </summary>
    /// <typeparam name="T">Type of Class which will be stored in the cache</typeparam>
    public abstract class CachingBase <T> where T : class
    {
        /// <summary>
        /// Dictionary used for local resolution of keys as opposed to having to hit 
        /// the cache for related counts, contains etc
        /// </summary>
        protected Dictionary<string, string> m_keys;

        /// <summary>
        /// Locking mechanism used to enforce thread saftey
        /// </summary>
        protected ReaderWriterLockSlim m_lock;

        /// <summary>
        /// Event for notification of an item's expiration from the cache
        /// </summary>
        public event SimplestringEventDel CacheItemExpired;

        /// <summary>
        /// Flag for indicating whether or not to ignore expiration during cleansing of cache
        /// </summary>
        protected bool m_ignoreExpiration = false;
        
        /// <summary>
        /// Implementation for this property should be provided by the extending class to set the class-level 
        /// TTL timepsane for items stored in the cache
        /// </summary>
        protected abstract TimeSpan TimeToLive { get; }

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
        /// Initializes a new instance of the <b>CachingBase[V];</b> class.
        /// </summary>
        protected CachingBase()
        {
            m_keys = new Dictionary<string,string>();
            m_lock = new ReaderWriterLockSlim();
        }

        /// <summary>
        /// Gets a value out of the cache denoted by the key suffix supplied
        /// </summary>
        /// <param name="key">String containing the suffix of the key for the desired item in the cache</param>
        /// <returns>Instance of type <typeparamref name="T"/> if found in cache; otherwise <c>null</c></returns>
        protected virtual T Get(string key)
        {
            if (key == null)
            {
                throw new ArgumentNullException("key");
            }

            key = CanonicalKey(key);

            // attempt to return the value from the cache;
            return HttpRuntime.Cache[key] as T;
        }

        /// <summary>
        /// Puts an item into the cache using the key and value supplied in parameters
        /// </summary>
        /// <remarks>
        /// Update will occurr on item if item is put into the cache that already exists under the same key
        /// </remarks>
        /// <param name="key">string containing the unqiue key of the item</param>
        /// <param name="value">value to be stored in the cache</param>
        protected virtual void Put(string key
            , T value)
        {
            Put(key, value, TimeToLive);
        }

        /// <summary>
        /// Adds an items to the cache using the specified key and ttl
        /// </summary>
        /// <remarks>
        /// Going to assume that a put with an existing key will not throw an exception but should rather
        /// update the item in the cache
        /// </remarks>
        /// <param name="key">String suffix for the key referencing the item in the cache</param>
        /// <param name="value">Instance of the type <typeparamref name="T"/> to be added to the cache</param>
        /// <param name="ttl">Timepsan used to denote the duration of an items existence in the cache</param>
        protected virtual void Put(string key
            , T value
            , TimeSpan ttl)
        {
            if (key == null)
            {
                throw new ArgumentNullException("key");
            }
            if (value == null)
            {
                throw new ArgumentNullException("value");
            }

            key = CanonicalKey(key);
            m_lock.EnterUpgradeableReadLock();

            try
            {
                // make use of insert to replace an existing item matching the key in the cache
                HttpRuntime.Cache.Insert(key
                    , value
                    , null
                    , DateTime.Now.Add(ttl)
                    , Cache.NoSlidingExpiration
                    , CacheItemPriority.High
                    , new CacheItemRemovedCallback(CachedItemRemovedCallBack))
                    ;

                // check to see if the item exists already in the list of keys, if not add it in
                if (!m_keys.ContainsKey(key))
                {
                    // enter write lock mode
                    m_lock.EnterWriteLock();
                    try
                    {
                        m_keys.Add(key, key);
                    }
                    finally
                    {
                        m_lock.ExitWriteLock();
                    }
                }
            }
            finally
            {
                // exit out of the upgradeable lock
                m_lock.ExitUpgradeableReadLock();
            }
        }

        /// <summary>
        /// Provides callback funcationality when an item is removed from the cache
        /// </summary>
        /// <remarks>
        /// Only when an item has expired in the cache should it be removed from the cache keys list
        /// </remarks>
        /// <param name="key">String containing the key of the item that was removed</param>
        /// <param name="value">Object that was removed from the cache</param>
        /// <param name="reason">Reason as to why object was invalidated in the cache</param>
        protected void CachedItemRemovedCallBack(string key
            , object value
            , CacheItemRemovedReason reason)
        {
            if (key == null)
            {
                throw new ArgumentNullException("key");
            }

            // if remove all has been called ignore expiration
            if (m_ignoreExpiration || reason != CacheItemRemovedReason.Expired) { return; }

            m_lock.EnterWriteLock();

            try
            {
                m_keys.Remove(key);
            }
            finally
            {
                m_lock.ExitWriteLock();
            }

            OnCacheItemExpired(key);
        }

        /// <summary>
        /// Removes an item from the cache denoted by the key suffix
        /// </summary>
        /// <param name="key">String containing the suffis of the key for the item to be removed</param>
        protected virtual void Remove(string key)
        {
            if (key == null)
            {
                throw new ArgumentNullException("key");
            }

            key = CanonicalKey(key);

            m_lock.EnterWriteLock();

            try
            {
                m_keys.Remove(key);
                HttpRuntime.Cache.Remove(key);
            }
            finally
            {
                m_lock.ExitWriteLock();
            }
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
            m_lock.EnterWriteLock();
            m_ignoreExpiration = true;

            try
            {
                // remove each matching item from the cache
                foreach (string key in m_keys.Keys)
                {
                    try
                    {
                        HttpRuntime.Cache.Remove(key);
                    }
                    catch { }
                }

                // clear out the list of keys
                m_keys.Clear();
            }
            finally
            {
                m_ignoreExpiration = false;
                m_lock.ExitWriteLock();
            }
        }

        /// <summary>
        /// Method to raise event if event is subscribed to
        /// </summary>
        /// <param name="key">String containing the key of the item in the cache that expired</param>
        protected void OnCacheItemExpired(string key)
        {
            if (key == null)
            {
                throw new ArgumentNullException("key");
            }

            SimplestringEventDel cacheItemExpired = CacheItemExpired;
            if (cacheItemExpired != null)
            {
                cacheItemExpired(this, key);
            }
        }

        /// <summary>
        /// Returns the string key in lower case and extra whitespace has been trimmed.
        /// </summary>
        /// <param name="key">The key to change</param>
        /// <returns>The lowercase and trimmed version of <paramref name="key"/></returns>
        private static string CanonicalKey(string key)
        {
            return key.ToLower().Trim();
        }
    }
}
