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
using Health.Direct.Config.Client;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.SmtpAgent.Tests
{
    public class TestDomainServiceResolver
    {
        const string TestCacheName = "Domain.testcache";
        const string AgentName = "Agent1";
        const string MissingAgentName = "MissingAgent";


        [Fact(Skip = "Requires Config Service to be installed")]
        public void CachingDomainKnown()
        {
            DomainServiceResolver m_resolver = CreateResolver(AgentName, true, 60, false); // enable caching, ttl=60secs, dissable negative caching
            
            Dictionary<string, string> domains = m_resolver.GetDomains();
            Dictionary<string, string> cached = m_resolver.Cache.Get(AgentName);

            VerifyValidDomain(domains, cached);

            //// now get it served from the cache. 
            //// Would be nice to use mocking here to prove we did not call the client proxy
            domains = m_resolver.GetDomains();
            cached = m_resolver.Cache.Get(AgentName);

            VerifyValidDomain(domains, cached);
        }

        [Fact(Skip = "Requires Config Service to be installed")]
        public void CachingDomainNotKnown()
        {
            DomainServiceResolver m_resolver = CreateResolver(MissingAgentName, true, 60, false); // enable caching, ttl=60secs, dissable negative caching

            Dictionary<string, string> domains = m_resolver.GetDomains();
            Dictionary<string, string> cached = m_resolver.Cache.Get(MissingAgentName);

            VerifyDomainNotFound(domains, cached);

            //// now get it served from the cache. 
            //// Would be nice to use mocking here to prove we did not call the client proxy
            domains = m_resolver.GetDomains();
            cached = m_resolver.Cache.Get(MissingAgentName);

            VerifyDomainNotFound(domains, cached);
        }


        [Fact]
        public void NegativeCachingDomainNotAllowed()
        {
            Assert.Throws<InvalidOperationException>(() => CreateResolver(AgentName, true, 60, true)); // enable caching, ttl=60secs, enable negative caching
        }

        private void VerifyValidDomain(Dictionary<string, string> domains, Dictionary<string, string> cached)
        {
            Assert.NotNull(domains);
            Assert.NotNull(cached);
            Assert.True(cached.Count > 0);
            Assert.Equal(domains.Count, cached.Count);

            domains.Keys.All(x => cached.ContainsKey(x));
            cached.Keys.All(x => domains.ContainsKey(x));
        }


        //
        // DomainServiceResolver does not support negative cache.  It is a simple cache of only one tenant
        //
        private void VerifyDomainNotFound(Dictionary<string, string> domains, Dictionary<string, string> cached)
        {
            Assert.Empty(domains);
            Assert.Empty(cached);
        }


        private DomainServiceResolver CreateResolver(string agentName, bool cachingEnabled, int ttlSeconds, bool negativeCache)
        {

            return new DomainServiceResolver(
                agentName,
                new ClientSettings()
                    {
                        Url = "http://localhost:6692/DomainManagerService.svc/Domains"
                    },
                new CacheSettings()
                {
                    Name = TestCacheName,
                    Cache = cachingEnabled,
                    NegativeCache = negativeCache,
                    CacheTTLSeconds = ttlSeconds
                });
        }

    }
}
