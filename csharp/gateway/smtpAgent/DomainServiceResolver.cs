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
using System.Linq;
using Health.Direct.Common.Caching;
using Health.Direct.Common.Domains;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Store;

namespace Health.Direct.SmtpAgent
{
    public class DomainServiceResolver : IDomainResolver
    {
        string m_agentName;
        ClientSettings m_domainClientSettings;
        DomainCache m_domainCache;

        public DomainServiceResolver(string agentName, ClientSettings domainClientSettings, CacheSettings cacheSettings)
        {
            if (agentName == null)
            {
                throw new ArgumentNullException(nameof(agentName));
            }

            if (domainClientSettings == null)
            {
                throw new ArgumentNullException(nameof(domainClientSettings));
            }
            m_agentName = agentName;
            m_domainClientSettings = domainClientSettings;
            if (cacheSettings != null && cacheSettings.Cache)
            {
                CacheSettings agentDomainCacheSettings = new CacheSettings(cacheSettings) { Name = "AgentDomainCache" };
                m_domainCache = new DomainCache(agentDomainCacheSettings);
            }
        }

        /// <summary>
        /// This resolver's domain cache.
        /// </summary>
        public DomainCache Cache
        {
            get
            {
                return m_domainCache;
            }
        }

        public IEnumerable<string> Domains
        {
            get
            {
                return GetDomains().Values.Select(d => d.Name);
            }
        }


        public Dictionary<string, Domain> GetDomains()
        {
            Dictionary<string, Domain> managedDomains;

            if (m_domainCache != null)
            {
                managedDomains = m_domainCache.Get(m_agentName);
                if (managedDomains != null)
                {
                    return managedDomains;
                }
            }

            using (DomainManagerClient client = CreateClient())
            {
                Domain[] domains = client.GetAgentDomains(m_agentName, EntityStatus.Enabled);
                managedDomains = new Dictionary<string, Domain>(MimeStandard.Comparer); // Case-IN-sensitive
                for (int i = 0; i < domains.Length; ++i)
                {
                    managedDomains[domains[i].Name] = domains[i];
                }
            }

            if (m_domainCache != null)
            {
                m_domainCache.Put(m_agentName, managedDomains);
            }

            return managedDomains;
        }


        public bool IsManaged(string domain)
        {
            if (string.IsNullOrEmpty(domain))
            {
                throw new ArgumentException("value was null or empty", "domain");
            }

            return GetDomains().ContainsKey(domain);
        }

        public bool HsmEnabled(string address)
        {
            if (string.IsNullOrEmpty(address))
            {
                throw new ArgumentException("value was null or empty", nameof(address));
            }

            return GetDomains().Any(d =>
                address.ContainsIgnoreCase(d.Key) &&
                d.Value.SecurityStandard == SecurityStandard.Fips1402);
        }


        public bool Validate(string[] domains)
        {
            if (domains == null || domains.Length == 0)
            {
                return false;
            }

            for (int i = 0; i < domains.Length; ++i)
            {
                string domain = domains[i];
                if (string.IsNullOrEmpty(domain))
                {
                    return false;
                }
            }

            return true;
        }

        private DomainManagerClient CreateClient()
        {
            return new DomainManagerClient(m_domainClientSettings.Binding, m_domainClientSettings.Endpoint);
        }
    }
}
