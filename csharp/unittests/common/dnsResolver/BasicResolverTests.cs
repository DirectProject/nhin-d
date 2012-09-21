/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;

using Health.Direct.Common.DnsResolver;

using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.DnsResolver
{
    public class BasicResolverTests : IDisposable
    {
        private readonly DnsClient m_client;

        const string PublicDns = "8.8.8.8";         // Google
        //const string PublicDns = "151.197.0.38";  // Verizon
        //const string PublicDns = "4.2.2.1";       // Level 3
        //const string PublicDns = "64.81.111.2";   // Speakeasy
        //const string SubnetDns = "192.168.0.1";
        //const string LocalDns = "127.0.0.1";

        public BasicResolverTests()
        {
            m_client = new DnsClient(PublicDns) { Timeout = TimeSpan.FromSeconds(10) };
        }

        public void Dispose()
        {
            m_client.Dispose();
        }

        [Theory]
        [InlineData("www.microsoft.com")]
        [InlineData("www.yahoo.com")]
        [InlineData("www.google.com")]
        [InlineData("www.apple.com")]
        [InlineData("www.bing.com")]
        //[InlineData("nhind.hsgincubator.com")]
        //[InlineData("dns.hsgincubator.com")]
        [InlineData("www.nhindirect.org")]
        [InlineData("www.relayhealth.com")]
        [InlineData("www.epic.com")]
        [InlineData("www.cerner.com")]
        [InlineData("www.ibm.com")]
        public void TestA(string domain)
        {
            Resolve(DnsRequest.CreateA(domain));
        }

        // we're able to resuse these names in TestCert and ResolveCert
        public static IEnumerable<object[]> CertDomainNames
        {
            get
            {
                yield return new[] { "nhind.hsgincubator.com" };
                yield return new[] { "redmond.hsgincubator.com" };
                //yield return new[] { "gm2552.securehealthemail.com.hsgincubator.com" };
                //yield return new[] { "ses.testaccount.yahoo.com.hsgincubator.com" };
                // these two throw a DnsProtocolException
                //yield return new[] { "nhin1.rwmn.org.hsgincubator.com" };
                //yield return new[] { "nhin.whinit.org.hsgincubator.com" };
            }
        }

        [Theory(Skip = "Requires remote DNS call on port 53.")]
        [PropertyData("CertDomainNames")]
        public void TestCert(string domain)
        {
            Resolve(DnsRequest.CreateCERT(domain));
        }

        [Theory(Skip = "Requires remote DNS call on port 53.")]
        [PropertyData("CertDomainNames")]
        public void ResolveCert(string domain)
        {
            IEnumerable<CertRecord> certs = m_client.ResolveCERTFromNameServer(domain);
            Assert.True(certs != null, domain);
            Assert.NotNull(certs.FirstOrDefault());
        }

        [Theory]
        //[InlineData("nhind.hsgincubator.com")]
        //[InlineData("redmond.hsgincubator.com")]
        [InlineData("www.microsoft.com")]
        [InlineData("www.hotmail.com")]
        [InlineData("www.gmail.com")]
        [InlineData("www.relayhealth.com")]
        [InlineData("www.aol.com")]
        [InlineData("gmail.com")]
        public void TestMX(string domain)
        {
            Resolve(DnsRequest.CreateMX(domain));
        }

        private void Resolve(DnsRequest request)
        {
            DnsResponse matches = m_client.Resolve(request);

            Assert.NotNull(matches);
            Assert.True(matches.HasAnswerRecords, string.Format("{0}:{1}", request.Question.Type, request.Question.Domain));
        }
    }
}