/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using Health.Direct.Common.Caching;

namespace Health.Direct.Common.Domains
{
    /// <summary>
    /// Handles caching of certificates.
    /// </summary>
    public class DomainCache
    {
        const int DefaultCacheTTLSeconds = 60; /* 1 minute */

        Cache<Dictionary<string, string>> m_cache;
        CacheSettings m_settings;

        /// <summary>
        /// Creates a new instance using the specified CacheSettings.
        /// </summary>        
        public DomainCache(CacheSettings settings)
        {
            if (settings == null)
            {
                throw new ArgumentNullException("settings");
            }
            m_settings = new CacheSettings(settings);

            InitializeCache();
        }

        private void InitializeCache()
        {
            if (m_settings.Cache)
            {
                if (m_settings.NegativeCache)
                {
                    throw new InvalidOperationException("NegativeCacheWithCacheDisabled");
                }

                if (!m_settings.CacheTTLSeconds.HasValue)
                {
                    m_settings.CacheTTLSeconds = DefaultCacheTTLSeconds;
                }

                if (m_settings.CacheTTLSeconds <= 0)
                {
                    throw new ArgumentOutOfRangeException("CacheTTLSeconds");
                }

                m_cache = new Cache<Dictionary<string, string>>(m_settings.Name);
            }
            
        }

        /// <summary>
        /// Gets an item with the specified subjectName from the certificate cache. 
        /// </summary>        
        /// <returns>Cert collection if found in cache, otherwise null.</returns>
        public Dictionary<string, string> Get(string groupName)
        {
            if (m_cache != null)
            {
                string key = groupName.ToLower();
                var value = m_cache.Get(key);
                return value;
            }
            return null;
        }

        /// <summary>
        /// Put a domain list in the cache
        /// </summary>        
        public void Put(string groupName, Dictionary<string, string> value)
        {
            if (m_cache != null)
            {
                string key = groupName.ToLower();
                m_cache.Put(key, value, new TimeSpan(0, 0, m_settings.CacheTTLSeconds.Value));
            }
        }
    }
}
