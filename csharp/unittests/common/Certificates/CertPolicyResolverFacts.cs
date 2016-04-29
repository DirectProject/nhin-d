/* 
 Copyright (c) 2016, Direct Project
 All rights reserved.

 Authors:    
    Joseph Shook      Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using System.Threading;
using Health.Direct.Common.Caching;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Policies;
using Health.Direct.Policy.X509;
using Xunit;

namespace Health.Direct.Common.Tests.Certificates
{
    public class CertPolicyResolverFacts
    {
        const string TestCacheName = "Cert.testcache";
        const string Email = "bob@nhind.hsgincubator.com";
        const string EmailDoesNotExist = "knowone@notrust.lab";

        private PolicyResolver CreateResolver(CacheSettings cacheSettings)
        {
            var policyIndex = new CertPolicyIndexStub();

            var policyExpression = new KeyUsageExtensionField(false);
            var emailAddress = new MailAddress(Email);
            policyIndex.Add(emailAddress.Host, policyExpression);

            return new PolicyResolver(
                policyIndex,
                new CacheSettings()
                {
                    Name = cacheSettings.Name ?? TestCacheName,
                    Cache = cacheSettings.Cache,
                    NegativeCache = cacheSettings.NegativeCache,
                    CacheTTLSeconds = cacheSettings.CacheTTLSeconds
                });
        }

        [Fact]
        public void CachingDisabledNegativeCacheEnabled()
        {
            CacheSettings settings = new CacheSettings()
            {
                Cache = false,
                NegativeCache = true,
                CacheTTLSeconds = 60,
                Name = "ABC"
            };

            Assert.Throws(typeof(InvalidOperationException), () => settings.Validate());
            Assert.Throws(typeof(InvalidOperationException), () => new CertificateCache(settings));
        }

        [Fact]
        public void CachingCertKnown()
        {
            var cacheSettings = new CacheSettings
            {
                Cache = true,           /* caching disabled */
                CacheTTLSeconds = 60,   /* TTL */
                NegativeCache = false   /* negative caching */
            };

            var resolver = CreateResolver(cacheSettings);
            var emailAddress = new MailAddress(Email);
            var source = resolver.GetIncomingPolicy(emailAddress);
            var cached = resolver.Cache.Get(emailAddress.Host);

            VerifyValidPolicy(source, cached);

            // now get it served from the cache. 
            source = resolver.GetIncomingPolicy(emailAddress);
            cached = resolver.Cache.Get(emailAddress.Host);

            VerifyValidPolicy(source, cached);
        }

        [Fact]
        public void CachingPolicyNotFoundNoNegativeCache()
        {
            var cacheSettings = new CacheSettings
            {
                Cache = true,           /* caching disabled */
                CacheTTLSeconds = 60,   /* TTL */
                NegativeCache = false   /* negative caching */
            };

            var resolver = CreateResolver(cacheSettings);
            CachingPolicyNotFound(resolver, cacheSettings);
        }

        [Fact]
        public void CachingPolicyNotFoundNegativeCache()
        {
            var cacheSettings = new CacheSettings
            {
                Cache = true,           /* caching disabled */
                CacheTTLSeconds = 60,   /* TTL */
                NegativeCache = true   /* negative caching */
            };

            var resolver = CreateResolver(cacheSettings);
            CachingPolicyNotFound(resolver, cacheSettings);
        }

        [Fact]
        public void CachingVerifyTTL()
        {
            var cacheSettings = new CacheSettings
            {
                Cache = true,           /* caching disabled */
                CacheTTLSeconds = 3,    /* TTL */
                NegativeCache = false,  /* negative caching */
                Name = Guid.NewGuid().ToString("N")
            };

            var resolver = CreateResolver(cacheSettings);
            var emailAddress = new MailAddress(Email);
            var source = resolver.GetIncomingPolicy(emailAddress);
            var cached = resolver.Cache.Get(emailAddress.Host);

            VerifyValidPolicy(source, cached);

            Thread.Sleep(4000);

            // verify its evicted from cache.
            cached = resolver.Cache.Get(emailAddress.Host);
            Assert.False(cached.Any());

            // now get it served from the cache. 
            source = resolver.GetIncomingPolicy(emailAddress);
            cached = resolver.Cache.Get(emailAddress.Host);

            VerifyValidPolicy(source, cached);
        }

        [Fact]
        public void CachingVerifyDisabled()
        {
            var cacheSettings = new CacheSettings
            {
                Cache = false,          /* caching disabled */
                CacheTTLSeconds = 0,    /* TTL */
                NegativeCache = false,  /* negative caching */
                Name = Guid.NewGuid().ToString("N")
            };

            var resolver = CreateResolver(cacheSettings);
            var emailAddress = new MailAddress(Email);
            var source = resolver.GetIncomingPolicy(emailAddress);

            Assert.Null(resolver.Cache);
            Assert.True(source.Count > 0);
        }

        [Fact]
        public void CachingVerifyDisabledNullCacheSettings()
        {
            var policyIndex = new CertPolicyIndexStub();
            var policyExpression = new KeyUsageExtensionField(false);
            var emailAddress = new MailAddress(Email);
            policyIndex.Add(emailAddress.Host, policyExpression);

            var resolver = new PolicyResolver(policyIndex, null);
            var source = resolver.GetIncomingPolicy(emailAddress);
            Assert.Null(resolver.Cache);
            Assert.True(source.Count > 0);
        }

        [Fact]
        public void CachingVerifyKeyCaseInsensitive()
        {
            var cacheSettings = new CacheSettings
            {
                Cache = true,           /* caching disabled */
                CacheTTLSeconds = 60,   /* TTL */
                NegativeCache = false   /* negative caching */
            };

            var resolver = CreateResolver(cacheSettings);
            var emailAddress = new MailAddress(Email);
            var source = resolver.GetIncomingPolicy(emailAddress);
            var cached = resolver.Cache.Get(emailAddress.Host);

            VerifyValidPolicy(source, cached);

            var emailAddressToUpper = new MailAddress(Email.ToUpper());
            source = resolver.GetIncomingPolicy(emailAddressToUpper);
            cached = resolver.Cache.Get(emailAddress.Host);
            Assert.False(emailAddressToUpper.Host == emailAddress.Host);

            VerifyValidPolicy(source, cached);
        }

        private void VerifyValidPolicy(IList<IPolicyExpression> source, IList<IPolicyExpression> cached)
        {
            Assert.NotNull(source);
            Assert.NotNull(cached);
            Assert.True(cached.Count > 0);
            Assert.Equal(source.Count, cached.Count);


            Assert.True(source.All(x => cached.Select(c => c.ToString()).Contains(x.ToString())));
            Assert.True(cached.All(x => source.Select(s => s.ToString()).Contains(x.ToString())));
        }


        private void VerifyCertNotFound(IList<IPolicyExpression> source, IList<IPolicyExpression> cached, bool negativeCache)
        {
            Assert.False(source.Any());

            if (negativeCache)
            {
                Assert.NotNull(cached);
                Assert.True(cached.Count == 0); /* policy not found -> cache empty collection */
            }
            else
            {
                Assert.False(cached.Any());
            }
        }
        private void CachingPolicyNotFound(PolicyResolver resolver, CacheSettings cacheSettings)
        {
            var emailAddress = new MailAddress(EmailDoesNotExist);
            var source = resolver.GetIncomingPolicy(emailAddress);
            var cached = resolver.Cache.Get(emailAddress.Host);

            VerifyCertNotFound(source, cached, cacheSettings.NegativeCache);

            // now get it served from the cache. 
            source = resolver.GetIncomingPolicy(emailAddress);
            cached = resolver.Cache.Get(emailAddress.Host);

            VerifyCertNotFound(source, cached, cacheSettings.NegativeCache);
        }

    }
}