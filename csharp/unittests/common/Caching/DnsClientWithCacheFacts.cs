/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico chris.lomonico@surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 */
using System;
using System.Collections.Generic;
using Health.Direct.Common.Caching;
using Health.Direct.Common.Dns;
using Health.Direct.Common.DnsResolver;

using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.Caching
{
    public class DnsClientWithCacheFacts : TestingBase, IDisposable
    {
        // set this to true if dump statements are needed for debugging purposes
        private const bool DumpIsEnabled = true;
        DnsResponseCache m_cache;
        private readonly DnsClientWithCache m_client;
        private readonly DnsClient m_clientNoCache;

        const string PublicDns = "8.8.8.8";
        //const string SubnetDns = "192.168.0.1";
        //const string PublicDns = "4.2.2.1";
        //const string LocalDns = "127.0.0.1";

        /// <summary>
        /// Gets the CertDomainNames of the DnsClientWithCacheFacts
        /// Remarks:
        /// we're able to resuse these names in TestCert and ResolveCert
        /// </summary>
        public static IEnumerable<object[]> CertDomainNames
        {
            get
            {
                yield return new[] { "direct.healthvault-ppe.com" };
                //yield return new[] { "nhind.hsgincubator.com" };
                //yield return new[] { "gm2552.securehealthemail.com.hsgincubator.com" };
                //yield return new[] { "ses.testaccount.yahoo.com.hsgincubator.com" };
                // these two throw a DnsProtocolException
                //yield return new[] { "nhin1.rwmn.org.hsgincubator.com" };
                //yield return new[] { "nhin.whinit.org.hsgincubator.com" };
            }
        }

        /// <summary>
        /// Initializes a new instance of the <b>DnsClientWithCacheFacts</b> class.
        /// </summary>
        public DnsClientWithCacheFacts()
            : base(DumpIsEnabled)
        {
            m_cache = new DnsResponseCache(Guid.NewGuid().ToString("D"));
            m_client = new DnsClientWithCache(PublicDns) { Timeout = TimeSpan.FromSeconds(5), Cache = m_cache};
            m_client.MaxRetries = 1;
            m_clientNoCache = new DnsClient(PublicDns) { Timeout = TimeSpan.FromSeconds(5) };
            m_clientNoCache.MaxRetries = 1;
        }

        /// <summary>
        /// Releases the resources used by the <b>DnsClientWithCacheFacts</b>.
        /// </summary>
        public void Dispose()
        {
            m_cache.RemoveAll();
            m_client.Dispose();
        }

        /// <summary>
        /// confirms ANAME records that are resolved with the client w/cache are stored in cache
        /// </summary>
        /// <param name="domain">domain name to be resolved</param>
        [Theory]
        [InlineData("www.microsoft.com")]
        [InlineData("www.yahoo.com")]
        [InlineData("www.google.com")]
        [InlineData("www.apple.com")]
        //[InlineData("nhind.hsgincubator.com")]
        //[InlineData("hvnhind.hsgincubator.com")]
        //[InlineData("dns.hsgincubator.com")]
        public void ResolveAEnsureInCache(string domain)
        {
            // try with ResolveCert
            Dump("Attempting to resolve A records for [{0}]", domain);

            IEnumerable<AddressRecord> results = m_client.ResolveA(domain);
            Assert.True(results != null, domain);
            DnsResponse res = m_client.Cache.Get(new DnsQuestion(domain
                                                                 , DnsStandard.RecordType.ANAME));

            Dump("ensuring item is stored in cache");
            Assert.NotNull(res);
        }
        
        /// <summary>
        /// Confirms ability of code to resolve certs using the dns caching client method ResolveCERT, ensures items are in cache
        /// </summary>
        /// <param name="domain">domain name to be resolved</param>
        [Theory(Skip = "Requires remote DNS call on port 53.")]
        [PropertyData("CertDomainNames")]
        public void ResolveCertEnsureInCache(string domain)
        {
            // try with ResolveCert
            Dump("Attempting to resolve CERT records for [{0}]", domain);

            IEnumerable<CertRecord> results = m_client.ResolveCERT(domain);
            Assert.True(results != null, domain);

            Dump("ensuring item is stored in cache");
            DnsResponse res = m_client.Cache.Get(new DnsQuestion(domain
                                                                 , DnsStandard.RecordType.CERT));
            Assert.NotNull(res);
        }

        /// <summary>
        /// confirms ability of code to resolve certs using the dns caching client method ResolveCERT
        /// </summary>
        /// <param name="domain">domain name to be resolved</param>
        [Theory(Skip="Requires remote DNS call on port 53.")]
        [PropertyData("CertDomainNames")]
        public void ResolveCERTFromNameServerEnsureInCache(string domain)
        {
            // try with ResolveCert
            Dump("Attempting to resolve CERT records for [{0}]", domain);

            IEnumerable<CertRecord> results = m_client.ResolveCERTFromNameServer(domain);
            Assert.True(results != null, domain);
            
            Dump("ensuring item is stored in cache");
            DnsResponse res = m_client.Cache.Get(new DnsQuestion(domain
                                                                 , DnsStandard.RecordType.CERT));
            Assert.NotNull(res);
        }

        /// <summary>
        /// confirms ability to resolve and cache mx records
        /// </summary>
        /// <param name="domain">domain name to be resolved</param>
        [Theory]
        //[InlineData("nhind.hsgincubator.com")]
        [InlineData("direct.healthvault-ppe.com")]
        [InlineData("www.microsoft.com")]
        public void ResolveMXEnsureInCache(string domain)
        {
            // try with ResolveCert
            Dump("Attempting to resolve MX records for [{0}]", domain);

            IEnumerable<MXRecord> results = m_client.ResolveMX(domain);
            Dump("ensuring that results were returned");
            Assert.True(results != null, domain);
            DnsResponse res = m_client.Cache.Get(new DnsQuestion(domain
                                                                 , DnsStandard.RecordType.MX));
            Dump("ensuring item is stored in cache");
            Assert.NotNull(res);
        }
        
        /// <summary>
        /// confirms ability to resolve and cache TXT records
        /// </summary>
        /// <param name="domain">domain name to be resolved</param>
        [Theory]
        [InlineData("www.microsoft.com")]
        [InlineData("www.yahoo.com")]
        //[InlineData("www.google.com")]
        [InlineData("www.apple.com")]
        public void ResolveTXTEnsureInCache(string domain)
        {
            // try with ResolveCert
            Dump("Attempting to resolve TXT records for [{0}]", domain);

            IEnumerable<TextRecord> results = m_client.ResolveTXT(domain);
            Dump("ensuring that results were returned");
            Assert.True(results != null, domain);
            DnsResponse res = m_client.Cache.Get(new DnsQuestion(domain
                                                                 , DnsStandard.RecordType.TXT));
            Dump("ensuring item is stored in cache");
            Assert.NotNull(res);
        }

        /// <summary>
        /// Confirms ability to resolve and cache PTR records
        /// </summary>
        /// <param name="domain">domain name to be resolved</param>
        [Theory]
        [InlineData("www.microsoft.com")]
        [InlineData("www.yahoo.com")]
        //[InlineData("www.google.com")]
        [InlineData("www.apple.com")]
        public void ResolvePTREnsureInCache(string domain)
        {
            // try with ResolveCert
            Dump("Attempting to resolve PTR records for [{0}]", domain);

            IEnumerable<PtrRecord> results = m_client.ResolvePTR(domain);
            Dump("ensuring that results were returned");
            Assert.True(results != null, domain);
            DnsResponse res = m_client.Cache.Get(new DnsQuestion(domain
                                                                 , DnsStandard.RecordType.PTR));
            Dump("ensuring item is stored in cache");
            Assert.NotNull(res);
        }

        /// <summary>
        /// confirms ability to resolve and cache NS records
        /// </summary>
        /// <param name="domain">domain name to be resolved</param>
        [Theory]
        [InlineData("www.microsoft.com")]
        [InlineData("www.yahoo.com")]
        [InlineData("www.google.com")]
        [InlineData("www.apple.com")]
        public void ResolveNSEnsureInCache(string domain)
        {
            // try with ResolveCert
            Dump("Attempting to resolve NS records for [{0}]", domain);

            IEnumerable<NSRecord> results = m_client.ResolveNS(domain);
            Dump("ensuring that results were returned");
            Assert.True(results != null, domain);
            DnsResponse res = m_client.Cache.Get(new DnsQuestion(domain
                                                                 , DnsStandard.RecordType.NS));
            Dump("ensuring item is stored in cache");
            Assert.NotNull(res);
        }

        /// <summary>
        /// confirms ability to resolve and cache SOA records
        /// </summary>
        /// <param name="domain">domain name to be resolved</param>
        [Theory]
        [InlineData("www.microsoft.com")]
        [InlineData("www.yahoo.com")]
        [InlineData("www.google.com")]
        [InlineData("www.apple.com")]
        public void ResolveSOAEnsureInCache(string domain)
        {
            // try with ResolveCert
            Dump("Attempting to resolve SOA records for [{0}]", domain);
            IEnumerable<SOARecord> results = m_client.ResolveSOA(domain);
            Dump("ensuring that results were returned");
            Assert.True(results != null, domain);
            DnsResponse res = m_client.Cache.Get(new DnsQuestion(domain
                                                                 , DnsStandard.RecordType.SOA));
            Dump("ensuring item is stored in cache");
            Assert.NotNull(res);
        }
    }
}