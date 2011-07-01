/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico chris.lomonico@surescripts.com
    Umesh Madan umeshma@microsoft.com
    Ali Emami   aliemami@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Linq;

using Health.Direct.Common.DnsResolver;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Common.Caching
{
    /// <summary>
    /// Dns Response Cache - a single, app-domain wide Singleton
    /// </summary>
    public class DnsResponseCache : CachingBase<DnsResponse>
    {
        static DnsResponseCache s_cache;
        
        static DnsResponseCache()
        {
            s_cache = new DnsResponseCache();
        }
        
        string m_uniqueName = null;
        
        /// <summary>
        /// The Singleton App Domain Wide Dns Response Cache
        /// </summary>
        public static DnsResponseCache Current
        {
            get
            {
                return s_cache;
            }
        }
        
        private DnsResponseCache()
        {
        }
        
        /// <summary>
        /// Caches use a common memory store where each object in the store has a key.
        /// To keep your custom cache from stomping on other Dns Caches, use a unique name.
        /// </summary>
        /// <param name="uniqueName">unique name for this Dns Cache</param>
        public DnsResponseCache(string uniqueName)
        {
            if (string.IsNullOrEmpty(uniqueName))
            {
                throw new ArgumentException("uniqueName");
            }
            m_uniqueName = uniqueName;
        }
           
        /// <summary>
        /// Builds a common key format given the dnsquestion passed in
        /// </summary>
        /// <param name="question">Instance of a dns question from which qtype and qname are used to build the key</param>
        /// <returns>String containing the key built from properties in the question</returns>
        public virtual string BuildKey(DnsQuestion question)
        {
            if (question == null)
            {
                throw new ArgumentNullException("question");
            }
            
            string domain = question.Domain.ToLower();

            return string.Format("{0}.DnsCache.{1}.{2}", m_uniqueName, question.Type, domain);
        }
        
        /// <summary>
        /// Puts an object into the cache after building the key by calling the base method Put
        /// </summary>
        /// <param name="response">DnsResponse instace to be stored in the cache; DnsQuestion from this instance is used for the key</param>
        /// <param name="ttl">Timespan specifying the ttl for the DnsResponse object that is to be stored in the cache</param>
        public void Put(DnsResponse response, TimeSpan ttl)
        {
            if (response == null || !response.HasAnyRecords)
            {
                return;
            }

            response.TTLOrigin = DateTime.UtcNow; 

            base.Put(BuildKey(response.Question), response, ttl);
        }

        /// <summary>
        /// Puts an object into the cache after building the key by calling the base method Put
        /// </summary>
        /// <param name="response">DnsResponse instace to be stored in the cache; DnsQuestion from this instance is used for the key</param>
        public void Put(DnsResponse response)
        {
            // no sense in storing nothing
            if (response == null || !response.HasAnyRecords)
            {
                return;
            }
            
            int ttl = response.GetMinTTL(response.Question.Type);
            if (ttl <= 0)
            {
                //
                // Don't cache
                //
                return;
            }
            
            TimeSpan ts = TimeSpan.FromSeconds(ttl);

            // store the record in the cache
            Put(response, ts);
        }
        
        /// <summary>
        /// Find a cached response based on the given Dns question
        /// </summary>
        /// <param name="question">dns question</param>
        /// <returns>cached response, or null</returns>
        public DnsResponse Get(DnsQuestion question)
        {
            try
            {
                return base.Get(this.BuildKey(question));
            }
            catch
            {
            }
            
            return null;
        }
        
        /// <summary>
        /// Gets an item from the cache using the DnsRequest to find the item
        /// </summary>
        /// <param name="request">DnsRequest instance used to build the key to find the item in the cache</param>
        /// <returns>DnsResponse instance that was found in the cache; if not found, <c>null</c> is returned</returns>
        public DnsResponse Get(DnsRequest request)
        {
            if (request == null)
            {
                throw new ArgumentNullException("request");
            }
            
            return this.Get(request.Question);
        }

        /// <summary>
        /// Gets an item from the cache using the DnsResponse to find the item
        /// </summary>
        /// <param name="response">DnsResponse instance used to build the key to find the item in the cache</param>
        /// <returns>DnsResponse instance that was found in the cache; if not found, <c>null</c> is returned</returns>
        public DnsResponse Get(DnsResponse response)
        {
            if (response == null)
            {
                throw new ArgumentNullException("response");
            }
            
            return this.Get(response.Question);
        }

        /// <summary>
        /// Removes an item from the cache
        /// </summary>
        /// <param name="request">DnsRequest used to build the key for the corresponding item</param>
        public void Remove(DnsRequest request)
        {
            if (request == null)
            {
                throw new ArgumentNullException("request");
            }
            Remove(request.Question);
        }

        /// <summary>
        /// Removes an item from the cache
        /// </summary>
        /// <param name="response">DnsResponse used to build the key for the corresponding item</param>
        public void Remove(DnsResponse response)
        {
            if (response == null)
            {
                throw new ArgumentNullException("response");
            }
            Remove(response.Question);
        }

        /// <summary>
        /// Removes an item from the cache
        /// </summary>
        /// <param name="question">DnsQuestion used to build the key for the corresponding item</param>
        public void Remove(DnsQuestion question)
        {
            if (question == null)
            {
                throw new ArgumentNullException("question");
            }
            base.Remove(BuildKey(question));
        }
    }
}