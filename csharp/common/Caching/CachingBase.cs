/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico chris.lomonico@surescripts.com
    Umesh Madan     umeshma@microsoft.com
 
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
    /// Provides abstract, base implementation for typed cache class
    /// </summary>
    /// <typeparam name="T">Type of Class which will be stored in the cache</typeparam>
    public abstract class CachingBase <T> where T : class
    {
        /// <summary>
        /// Dictionary used for local resolution of keys as opposed to having to hit 
        /// the cache for related counts, contains etc
        /// </summary>
        HashSet<string> m_keys;
        /// <summary>
        /// Locking mechanism used to enforce thread saftey
        /// </summary>
        ReaderWriterLockSlim m_lock;
        CacheItemRemovedCallback m_removeCallback;        
        CacheItemPriority m_priority;
        
        /// <summary>
        /// Initializes a new instance of CachingBase
        /// </summary>
        protected CachingBase()
            : this(CacheItemPriority.Normal)
        {
        }
                
        /// <summary>
        /// Initializes a new instance of the <b>CachingBase[V];</b> class.
        /// </summary>
        protected CachingBase(CacheItemPriority priority)
        {
            m_keys = new HashSet<string>();
            m_lock = new ReaderWriterLockSlim();
            m_removeCallback = new CacheItemRemovedCallback(this.OnCachedItemRemoved);
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
        /// <param name="key">String containing the suffix of the key for the desired item in the cache</param>
        /// <returns>Instance of type <typeparamref name="T"/> if found in cache; otherwise <c>null</c></returns>
        public virtual T Get(string key)
        {
            if (key == null)
            {
                throw new ArgumentNullException("key");
            }
        
            m_lock.EnterReadLock();
            try
            {
                // attempt to return the value from the cache;
                return HttpRuntime.Cache[key] as T;
            }
            finally
            {
                m_lock.ExitReadLock();
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
        protected virtual void Put(string key, T value)
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
        /// <param name="key">String suffix for the key referencing the item in the cache</param>
        /// <param name="value">Instance of the type <typeparamref name="T"/> to be added to the cache</param>
        /// <param name="ttl">Timepsan used to denote the duration of an items existence in the cache</param>
        protected virtual void Put(string key, T value, TimeSpan? ttl)
        {
            if (key == null)
            {
                throw new ArgumentNullException("key");
            }
            if (value == null)
            {
                throw new ArgumentNullException("value");
            }

            m_lock.EnterWriteLock();
            try
            {
                // make use of insert to replace an existing item matching the key in the cache
                HttpRuntime.Cache.Insert(key
                                         , value
                                         , null
                                         , (ttl != null) ? DateTime.UtcNow.Add(ttl.Value) : Cache.NoAbsoluteExpiration
                                         , Cache.NoSlidingExpiration
                                         , m_priority
                                         , m_removeCallback)
                    ;

                // check to see if the item exists already in the list of keys, if not add it in
                if (!m_keys.Contains(key))
                {
                    m_keys.Add(key);
                }
            }
            finally
            {
                m_lock.ExitWriteLock();
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
        protected void OnCachedItemRemoved(string key
                                                 , object value
                                                 , CacheItemRemovedReason reason)
        {
            if (key == null)
            {
                throw new ArgumentNullException("key");
            }
            
            if (reason != CacheItemRemovedReason.Removed) 
            {
                m_lock.EnterWriteLock();
                try
                {
                    m_keys.Remove(key);
                }
                catch
                {
                }
                finally
                {
                    m_lock.ExitWriteLock();
                }
                
                NotifyExpired(key);
            }
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

            m_lock.EnterWriteLock();
            try
            {
                HttpRuntime.Cache.Remove(key);
                m_keys.Remove(key);
            }
            catch
            {
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
            try
            {
                // remove each matching item from the cache
                foreach (string key in m_keys)
                {
                    try
                    {
                        HttpRuntime.Cache.Remove(key);
                    }
                    catch 
                    { 
                    }
                }

                // clear out the list of keys
                m_keys.Clear();
            }
            finally
            {
                m_lock.ExitWriteLock();
            }
        }

        /// <summary>
        /// Method to raise event if event is subscribed to
        /// </summary>
        /// <param name="key">String containing the key of the item in the cache that expired</param>
        void NotifyExpired(string key)
        {
            if (key == null)
            {
                throw new ArgumentNullException("key");
            }
            
            Action<CachingBase<T>, string> cacheItemExpired = CacheItemExpired;
            if (cacheItemExpired != null)
            {
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
}