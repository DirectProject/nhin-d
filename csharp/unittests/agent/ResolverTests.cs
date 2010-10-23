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
using System.Collections.Generic;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;

using Health.Direct.Common.Certificates;

using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Agent.Tests
{
    public class UniformResolverTests
    {
        UniformCertificateResolver m_resolver;
        
        public UniformResolverTests()
        {
            m_resolver = new UniformCertificateResolver(TestCertificates.ChainCertsStore);
        }
        
        public static IEnumerable<object[]> Domains
        {
            get
            {
                yield return new[] {"foo"};
                yield return new[] {"bar"};
            }
        }
        
        public static IEnumerable<object[]> Addresses
        {
            get
            {
                yield return new[] {new MailAddress("foo@bar.com")};
                yield return new[] { new MailAddress("shoo@goo.com") };
            }
        }
        
        [Theory]
        [PropertyData("Domains")]
        public void TestDomains(string domain)
        {
            X509Certificate2Collection matches = m_resolver.GetCertificatesForDomain(domain);
            Assert.True(matches.Count == m_resolver.Certificates.Count);
        }

        [Theory]
        [PropertyData("Addresses")]
        public void TestAddresses(MailAddress address)
        {
            X509Certificate2Collection matches = m_resolver.GetCertificates(address);
            Assert.True(matches.Count == m_resolver.Certificates.Count);
        }
    }

    public class CertificateResolverTests
    {
        ICertificateResolver m_resolver;
        
        public CertificateResolverTests()
        {
            m_resolver = TestCertificates.PublicCertsStore.CreateResolver();
        }

        public static IEnumerable<object[]> GoodAddresses
        {
            get
            {
                yield return new[] { "bob@nhind.hsgincubator.com" };
                yield return new[] { "biff@nhind.hsgincubator.com" };
                yield return new[] { "toto@nhind.hsgincubator.com" };
                yield return new[] { "toby@redmond.hsgincubator.com" };
                yield return new[] { "snoopdog@redmond.hsgincubator.com" };
            }
        }

        public static IEnumerable<object[]> BadAddresses
        {
            get
            {
                yield return new[] { "bob@hsgincubator.com" };
                yield return new[] { "biff@nhind.com" };
                yield return new[] { "toto@com" };
            }
        }

        public static IEnumerable<object[]> GoodDomains
        {
            get
            {
                yield return new[] { "nhind.hsgincubator.com" };
                yield return new[] { "redmond.hsgincubator.com" };
            }
        }

        public static IEnumerable<object[]> BadDomains
        {
            get
            {
                yield return new[] { "foo.bar" };
                yield return new[] { "hsgincubator.com" };
            }
        }
        
        [Theory]
        [PropertyData("GoodAddresses")]
        public void GetCertificateWithGoodAddress(string address)
        {
            MailAddress mailAddress = new MailAddress(address);
            X509Certificate2Collection certs = m_resolver.GetCertificates(mailAddress);
            Assert.True(!certs.IsNullOrEmpty());
            foreach (X509Certificate2 cert in certs)
            {
                Assert.True(cert.MatchEmailNameOrName(mailAddress.Address) || cert.MatchEmailNameOrName(mailAddress.Host));
            }
        }

        [Theory]
        [PropertyData("GoodDomains")]
        public void GetCertificateWithGoodDomains(string domain)
        {
            X509Certificate2Collection certs = m_resolver.GetCertificatesForDomain(domain);
            Assert.True(!certs.IsNullOrEmpty());
            foreach (X509Certificate2 cert in certs)
            {
                Assert.True(cert.MatchEmailNameOrName(domain));
            }
        }

        [Theory]
        [PropertyData("BadDomains")]
        public void GetCertificateWithBadDomains(string domain)
        {
            X509Certificate2Collection certs = m_resolver.GetCertificatesForDomain(domain);
            Assert.True(certs.IsNullOrEmpty());
        }

        [Theory]
        [PropertyData("BadAddresses")]
        public void GetCertificateWithBadAddresses(string address)
        {
            X509Certificate2Collection certs = m_resolver.GetCertificates(new MailAddress(address));
            Assert.True(certs.IsNullOrEmpty());
        }
    }
}