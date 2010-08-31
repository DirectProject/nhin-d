/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico chris.lomonico@surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Threading;
using System.Web;
using System.Web.Caching;

namespace NHINDirect.Caching
{

    public delegate void SimpleStringEventDel(Object sender, String key);

    public abstract class CachingBase <V> where V : class
    {

        protected List<string> m_keys;
        protected ReaderWriterLockSlim m_lock;

        public event SimpleStringEventDel CacheItemExpired;

        protected bool m_ignoreExpiration = false;

        
        #region protected abstract TimeSpan Ttl
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.24.2010 11:07:01 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// implementation for this property should be provided by the extending class to set the class-level 
        /// TTL timepsane for items stored in the cache
        /// </summary>
        /// <value></value>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        protected abstract TimeSpan Ttl { get; }
        #endregion

        #region public Int32 CacheCount
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.24.2010 12:58:45 PM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// Gets the CacheCount of the CachingBase
        /// </summary>
        /// <value></value>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public Int32 CacheCount
        {
            get
            {
                return this.m_keys.Count;
            }
        }
        #endregion

        #region public CachingBase()
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.24.2010 11:08:38 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// Initializes a new instance of the <b>CachingBase[V];</b> class.
        /// </summary>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public CachingBase()
        {
            m_keys = new List<string>();
            this.m_lock = new ReaderWriterLockSlim();
        }
        #endregion

        #region protected virtual V Get(string key)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.24.2010 10:07:41 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// gets a value out of the cache denoted by the key suffix supplied
        /// </summary>
        /// <param name="key">string containing the suffix of the key for the desired item in the cache</param>
        /// <returns>class instance of type V if found in cache; otherwise null</returns>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        protected virtual V Get(string key)
        {
            key = key.ToLower().Trim();
            //----------------------------------------------------------------------------------------------------
            //---attempt to return the value from the cache;
            return HttpRuntime.Cache[key] as V;

        }
        #endregion

        #region protected virtual void Put(string key , V value)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.24.2010 10:58:21 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// puts an item into the cache using the key and value supplied in parameters
        /// </summary>
        /// <param name="key">string containing the unqiue key of the item</param>
        /// <param name="value">value to be stored in the cache</param>
        /// <remarks>update will occurr on item if item is put into the cache that already exists under the same key</remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        protected virtual void Put(string key
            , V value)
        {
            key = key.ToLower().Trim();
            this.Put(key
                , value
                , this.Ttl);

        }
        #endregion

        #region protected virtual void Put(string key , V value , TimeSpan ttl)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.24.2010 11:00:26 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// adds an items to the cache using the specified key and ttl
        /// </summary>
        /// <param name="key">string suffix for the key referencing the item in the cache</param>
        /// <param name="value">class instance of the type V to be added to the cache</param>
        /// <param name="ttl">timepsan used to denote the duration of an items existence in the cache</param>
        /// <remarks>going to assume that a put with an existing key will not throw an exception but should rather
        /// update the item in the cache</remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        protected virtual void Put(string key
            , V value
            , TimeSpan ttl)
        {
            key = key.ToLower().Trim();
            this.m_lock.EnterUpgradeableReadLock();
            try
            {

#if DEBUG
                Console.WriteLine("{0} - NHINDirect.Caching.CachingBase put item [{1}] in cache with TTL [{2}]"
                    , DateTime.UtcNow.ToString("mm:ss:ff")
                    , key
                    , ttl);
#endif 
                //----------------------------------------------------------------------------------------------------
                //---make use of insert to replace an existing item matching the key in the cache
                HttpRuntime.Cache.Insert(key
                    , value
                    , null
                    , DateTime.Now.Add(ttl)
                    , Cache.NoSlidingExpiration
                    , CacheItemPriority.High
                    , new CacheItemRemovedCallback(CachedItemRemovedCallBack));

                

                //----------------------------------------------------------------------------------------------------
                //---check to see if the item exists already in the list of keys, if not add it in
                if (!this.m_keys.Contains(key))
                {
                    //----------------------------------------------------------------------------------------------------
                    //---enter write lock mode
                    this.m_lock.EnterWriteLock();
                    try
                    {
                        this.m_keys.Add(key);
                    }
                    finally
                    {
                        this.m_lock.ExitWriteLock();
                    }
                }
               
                
            }
            finally
            {
                //----------------------------------------------------------------------------------------------------
                //---exit out of the upgradeable lock
                this.m_lock.ExitUpgradeableReadLock();
            }

        }
        #endregion

        #region protected void CachedItemRemovedCallBack(string key, object value, CacheItemRemovedReason reason)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.24.2010 11:15:23 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// provides callback funcationality when an item is removed from the cache
        /// </summary>
        /// <param name="key">string containing the key of the item that was removed</param>
        /// <param name="value">object that was removed from the cache</param>
        /// <param name="reason">readson as to why object was invalidated in the cache</param>
        /// <remarks>only when an item has expired in the cache should it be removed from the cache keys list</remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        protected void CachedItemRemovedCallBack(string key
            , object value
            , CacheItemRemovedReason reason)
        {
            //----------------------------------------------------------------------------------------------------
            //---if remove all has been called ignore expiration
            if (m_ignoreExpiration) { return; }
            switch (reason)
            {
                case CacheItemRemovedReason.Expired:
                    this.m_lock.EnterWriteLock();
                    try
                    {
                        this.m_keys.Remove(key);
                    }
                    finally
                    {
                        this.m_lock.ExitWriteLock();
                    }
                    this.OnCacheItemExpired(key);
                    break;
                default:
                    return;
            }
        }
        #endregion

        #region protected virtual void Remove(string k)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.24.2010 11:12:06 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// removes an item from the cache denoted by the key suffix
        /// </summary>
        /// <param name="key">string containing the suffis of the key for the item to be removed</param>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        protected virtual void Remove(string key)
        {
            key = key.ToLower().Trim();
            this.m_lock.EnterWriteLock();
            try
            {
                this.m_keys.Remove(key);
                HttpRuntime.Cache.Remove(key);
            }
            finally
            {
                this.m_lock.ExitWriteLock();
            }


        }
        #endregion

        #region public virtual void RemoveAll()
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.24.2010 11:17:02 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// removes all items from the cache
        /// </summary>
        /// <remarks>If the cache is being cleaned out, it should be safe to assume that a lock is not needed
        /// to stop values from being written to the cache/list of keys while they are being cleaned
        /// code utilizing this method should take this into consideration
        /// if need be locks can be added to stop any io on the storage objects</remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public virtual void RemoveAll()
        {
            m_ignoreExpiration = true;

            this.m_lock.EnterWriteLock();
            try
            {
                //----------------------------------------------------------------------------------------------------
                //---remove each matching item from the cache
                foreach (string key in m_keys)
                {
                    try
                    {
                        HttpRuntime.Cache.Remove(key);
                    }
                    catch { }
                }

                //----------------------------------------------------------------------------------------------------
                //---clear out the list of keys
                this.m_keys.Clear();
            }
            finally
            {
                this.m_lock.ExitWriteLock();
                m_ignoreExpiration = false;
            }
        }
        #endregion

        #region protected void OnCacheItemExpired(string key)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.31.2010 6:27:40 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// method to raise event if event is subscribed to
        /// </summary>
        /// <param name="key">string containing the key of the item in the cache that expired</param>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        protected void OnCacheItemExpired(string key)
        {
            if (this.CacheItemExpired != null)
            {
                this.CacheItemExpired(this, key);
            }
        }
        #endregion

    }

}
