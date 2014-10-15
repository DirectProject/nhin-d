/* 
 Copyright (c) 2014, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using System.Net.Mail;
using Health.Direct.Common.Caching;

namespace Health.Direct.Common.Policies
{
    /// <summary>
    /// Supports resolution of policies by domain.
    /// </summary>
    public class PolicyResolver : IPolicyResolver
    {
        readonly IPolicyIndex m_policyIndex;
        readonly PolicyCache m_policyCache;

        /// <summary>
        /// Creates a policy resolver that retrieves policies from the email address. 
        /// Policies are resolved against the host portion fo the<see cref="MailAddress"/>.
        /// </summary>
        /// <param name="index">
        /// An index instance providing <see cref="IPolicyIndex"/>
        /// </param>
        /// <param name="cacheSettings">
        /// The cache settings to use. Specify null for no caching.
        /// </param>
        public PolicyResolver(IPolicyIndex index, CacheSettings cacheSettings)
        {
            if (index == null)
            {
                throw new ArgumentNullException("index");
            }

            m_policyIndex = index;

            if (cacheSettings != null && cacheSettings.Cache)
            {
                m_policyCache = new PolicyCache(cacheSettings); 
            }
        }

        //TODO: Don't like this.  Caching is not efficient 
        /// <summary>
        /// Resolve outgoing cert policy 
        /// </summary>
        /// <param name="address"></param>
        /// <returns></returns>
        public IList<IPolicyExpression> GetOutgoingPolicy(MailAddress address)
        {
            return Resolve(address.Host); //TODO: decide if we resolve at email address also.
        }

        /// <summary>
        /// Resolve incoming cert policy
        /// </summary>
        /// <param name="address"></param>
        /// <returns></returns>
        public IList<IPolicyExpression> GetIncomingPolicy(MailAddress address)
        {
            return Resolve(address.Host);
        }

        /// <summary>
        /// Actually resolves a certificate for the given name. Override to customize. 
        /// </summary>
        /// <param name="domainName">Return policy expressions for this domain name</param>
        /// <returns>
        /// The list of <see cref="IPolicyExpression"/>s of for the requested domain name.
        /// </returns>
        protected virtual IList<IPolicyExpression> Resolve(string domainName)
        {
            IList<IPolicyExpression> matches = null;

            if (m_policyCache != null)
            {
                matches = m_policyCache.Get(domainName);

                if (matches != null)
                {
                    return matches;
                }
            }

            if (m_policyIndex != null)
            {
                // policy indexer will pull from service endpoint
                matches = m_policyIndex[domainName];
            }

            if (m_policyCache != null)
            {
                m_policyCache.Put(domainName, matches);
            }

            return matches;
        }
    }
}