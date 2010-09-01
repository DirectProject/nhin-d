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
using System.Linq;
using System.Text;
using System.Configuration;
using DnsResolver;

namespace NHINDirect.Caching
{
    /// <summary>
    /// extends the base 
    /// </summary>
    public class DnsResponseCache : CachingBase<DnsResponse>
    {
        
        /// <summary>
        /// provides implementation for base class property for setting a default ttl value
        /// </summary>
        protected override TimeSpan TimeToLive
        {
            get
            {
                // default to 20 seconds for global expiration
                return TimeSpan.FromSeconds(20);
            }
        }

        /// <summary>
        /// builds a common key format given the dnsquestion passed in
        /// </summary>
        /// <param name="question">instance of a dns question from which qtype and qname are used to build the key</param>
        /// <returns>string containing the key built from properties in the question</returns>
        public virtual string BuildKey(DnsQuestion question)
        {
            if (question == null)
            {
                throw new Exception("Empty DnsQuestion used as key");
            }
            return string.Format("{0}.{1}"
                    , question.QType.ToString()
                    , question.QName ?? "unknown").ToLower();
        }
        
        /// <summary>
        /// puts an object into the cache after building the key by calling the base method Put
        /// </summary>
        /// <param name="response">DnsResponse instace to be stored in the cache; DnsQuestion from this instance is used for the key</param>
        /// <param name="ttl">timespan specifying the ttl for the DnsResponse object that is to be stored in the cache</param>
        public void Put(DnsResponse response
            , TimeSpan ttl)
        {
            base.Put(BuildKey(response.Question)
                , response
                , ttl);
        }

        /// <summary>
        /// puts an object into the cache after building the key by calling the base method Put
        /// </summary>
        /// <param name="response">DnsResponse instace to be stored in the cache; DnsQuestion from this instance is used for the key</param>
        public void Put(DnsResponse response)
        {
            // no sense in storing nothing
            if (response == null || response.AnswerRecords.Count() == 0)
            {
                return;
            }

            // get the minimum ttl from the records (int for the total seconds)
            var val = response.AnswerRecords.Select(r => r.TTL).Min();
            TimeSpan ts = TimeSpan.FromSeconds(val);

            // store the record in the cache
            Put(response, ts);
        }

        /// <summary>
        /// gets an item from the cache using the DnsRequest to find the item
        /// </summary>
        /// <param name="request">DnsRequest instance used to build the key to find the item in the cache</param>
        /// <returns>DnsResponse instance that was found in the cache; if not found, null is returned</returns>
        public DnsResponse Get(DnsRequest request)
        {
            return base.Get(BuildKey(request.Question));
        }

        /// <summary>
        /// gets an item from the cache using the DnsResponse to find the item
        /// </summary>
        /// <param name="response">DnsResponse instance used to build the key to find the item in the cache</param>
        /// <returns>DnsResponse instance that was found in the cache; if not found, null is returned</returns>
        public DnsResponse Get(DnsResponse response)
        {
            return base.Get(BuildKey(response.Question));
        }

        /// <summary>
        /// gets an item from the cache using the DnsResponse to find the item
        /// </summary>
        /// <param name="question">DnsQuestion instance used to build the key to find the item in the cache</param>
        /// <returns>DnsResponse instance that was found in the cache; if not found, null is returned</returns>
        public DnsResponse Get(DnsQuestion question)
        {
            return base.Get(BuildKey(question));
        }

        /// <summary>
        /// removes an item from the cache
        /// </summary>
        /// <param name="request">DnsRequest used to build the key for the corresponding item</param>
        public void Remove(DnsRequest request)
        {
            Remove(request.Question);
        }

        /// <summary>
        /// removes an item from the cache
        /// </summary>
        /// <param name="response">DnsResponse used to build the key for the corresponding item</param>
        public void Remove(DnsResponse response)
        {
            Remove(response.Question);
        }

        /// <summary>
        /// removes an item from the cache
        /// </summary>
        /// <param name="question">DnsQuestion used to build the key for the corresponding item</param>
        public void Remove(DnsQuestion question)
        {
            base.Remove(BuildKey(question));
        }
    }
}
