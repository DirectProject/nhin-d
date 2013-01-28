/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:    
    Ali Emami       aliemami@microsoft.com
  
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
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading;

using Health.Direct.Common.Certificates;
using Health.Direct.Common.Caching;
using Xunit;

namespace Health.Direct.Common.Tests.Certificates
{
    public class CertificateResolverFacts : IDisposable
    {
        const string TestCacheName = "Cert.testcache"; 
        const string DomainIncubator = "nhind.hsgincubator.com";
        const string UserAtDomainIncubator = "user@nhind.hsgincubator.com";
        const string DomainNotExists = "blahblahabcd.com";       

        string[] m_domains = new string[] { DomainIncubator, UserAtDomainIncubator, DomainNotExists };

        SystemX509Store m_certStore; 
        CertificateResolver m_resolver;
        CertificateCache m_cache;
        bool m_negativeCache;

        public CertificateResolverFacts()
        {
            m_resolver = CreateResolver(true, 60, false); // enable caching, ttl=60secs, disable negative caching 

            m_cache = m_resolver.Cache;

            ClearCache();            
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
            string domain = DomainIncubator;
            X509Certificate2Collection source = m_resolver.GetCertificatesForDomain(domain);
            X509Certificate2Collection cached = m_cache.Get(domain);

            VerifyValidCert(source, cached);            

            // now get it served from the cache. 
            source = m_resolver.GetCertificatesForDomain(domain);
            cached = m_cache.Get(domain);

            VerifyValidCert(source, cached);            
        }

        [Fact]
        public void CachingCertNotFoundNoNegativeCache()
        {
            CachingCertNotFound(); 
        }

        [Fact]
        public void CachingCertNotFoundNegativeCache()
        {
            m_resolver = CreateResolver(true, 60, true);
            CachingCertNotFound();
        }

        public void CachingCertNotFound()
        {
            string domain = DomainNotExists;
            X509Certificate2Collection source = m_resolver.GetCertificatesForDomain(domain);
            X509Certificate2Collection cached = m_cache.Get(domain);

            VerifyCertNotFound(source, cached);

            // now get it served from the cache. 
            source = m_resolver.GetCertificatesForDomain(domain);
            cached = m_cache.Get(domain);

            VerifyCertNotFound(source, cached);
        }

        [Fact]
        public void CachingVerifyTTL()
        {
            m_resolver = CreateResolver(
                true,   /* caching enabled */
                3,      /* TTL */
                false); /* negative cache */
                
            string domain = DomainIncubator;
            X509Certificate2Collection source = m_resolver.GetCertificatesForDomain(domain);
            X509Certificate2Collection cached = m_cache.Get(domain);

            VerifyValidCert(source, cached);

            Thread.Sleep(4000);

            // verify its evicted from cache.
            cached = m_cache.Get(domain);
            Assert.Null(cached);

            // now get it served from the cache. 
            source = m_resolver.GetCertificatesForDomain(domain);
            cached = m_cache.Get(domain);

            VerifyValidCert(source, cached);
        }

        [Fact]
        public void CachingVerifyDisabled()
        {
            m_resolver = CreateResolver(
                false,  /* caching disabled */
                0,      /* TTL */
                false); /* negative caching */

            string domain = DomainIncubator;
            X509Certificate2Collection source = m_resolver.GetCertificatesForDomain(domain);
            X509Certificate2Collection cached = m_cache.Get(domain);

            Assert.Null(cached);
            Assert.True(source.Count > 0); 
        }

        [Fact]
        public void CachingVerifyDisabledNullCacheSettings()
        {
            using (SystemX509Store store = SystemX509Store.OpenExternal())
            {
                m_resolver = new CertificateResolver(store, null);                
                m_cache = m_resolver.Cache;
                string domain = DomainIncubator;
                X509Certificate2Collection source = m_resolver.GetCertificatesForDomain(domain);

                Assert.Null(m_resolver.Cache);
                Assert.True(source.Count > 0);
            }
        }

        [Fact]
        public void CachingVerifyKeyCaseInsensitive()
        {
            string domain = DomainIncubator;
            X509Certificate2Collection source = m_resolver.GetCertificatesForDomain(domain);
            X509Certificate2Collection cached = m_cache.Get(domain);

            VerifyValidCert(source, cached);
                         
            domain = DomainIncubator.ToUpper(); 
            source = m_resolver.GetCertificatesForDomain(domain);
            cached = m_cache.Get(domain);

            VerifyValidCert(source, cached);            
        }

        [Fact]
        public void CachingVerifyFallbackAddressesNoNegativeCache()
        {   
            CachingVerifyFallbackAddresses();
        }

        [Fact]
        public void CachingVerifyFallbackAddressesNegativeCache()
        {   
            m_resolver = CreateResolver(true, 60, true);
            CachingVerifyFallbackAddresses();
        }

        private void CachingVerifyFallbackAddresses()
        {
            MailAddress address = new MailAddress(UserAtDomainIncubator);

            X509Certificate2Collection source = m_resolver.GetCertificates(address);
            X509Certificate2Collection cachedUserAnchors = m_cache.Get(UserAtDomainIncubator);
            X509Certificate2Collection cachedDomainAnchors = m_cache.Get(address.Host);

            if (m_negativeCache)
            {
                Assert.NotNull(cachedUserAnchors);
                Assert.True(cachedUserAnchors.Count == 0);
            }
            else
            {
                Assert.Null(cachedUserAnchors);
            }

            VerifyValidCert(source, cachedDomainAnchors);
        }

        private CertificateResolver CreateResolver(bool cachingEnabled, int ttlSeconds, bool negativeCache)
        {
            if (m_certStore != null)
            {
                m_certStore.Dispose(); 
            }

            m_certStore = SystemX509Store.OpenExternal();
            m_negativeCache = negativeCache;

            return new CertificateResolver(
                m_certStore,
                new CacheSettings() { 
                    Name = TestCacheName, 
                    Cache = cachingEnabled, 
                    NegativeCache = negativeCache,
                    CacheTTLSeconds = ttlSeconds});
        }

        public void Dispose()
        {
            if (m_certStore != null)
            {
                m_certStore.Dispose();
            }
        }

        private void ClearCache()
        {
            // clear the cache store. 
            Cache<X509Certificate2Collection> cache = new Cache<X509Certificate2Collection>(TestCacheName);             
            foreach (string domain in m_domains)
            {
                cache.Remove(domain);                
            }
        }

        private void VerifyValidCert(X509Certificate2Collection source, X509Certificate2Collection cached)
        {
            Assert.NotNull(source);
            Assert.NotNull(cached);
            Assert.True(cached.Count > 0);
            Assert.Equal(source.Count, cached.Count);

            source.Enumerate().All((x) => cached.FindByThumbprint(x.Thumbprint) != null);
            cached.Enumerate().All((x) => source.FindByThumbprint(x.Thumbprint) != null);
        }

        private void VerifyCertNotFound(X509Certificate2Collection source, X509Certificate2Collection cached)
        {
            Assert.Null(source);

            if (m_negativeCache)
            {
                Assert.NotNull(cached);
                Assert.True(cached.Count == 0); /* anchor not found -> cache empty collection */
            }
            else
            {
                Assert.Null(cached); 
            }
        }
    }
}
