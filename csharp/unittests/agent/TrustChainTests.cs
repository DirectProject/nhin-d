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

using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;

using Health.Direct.Common.Certificates;

using Xunit;

namespace Health.Direct.Agent.Tests
{
    public class TrustChainTests
    {
        MemoryX509Store m_store;
        ICertificateResolver m_resolver;
        TrustChainValidator m_validator;
        X509Certificate2Collection m_endCerts;
        X509Certificate2Collection m_trustedAnchors;
        
        public TrustChainTests()
        {
            m_store = TestCertificates.ChainCertsStore.Clone();
            m_resolver = m_store.CreateResolver();
            m_validator = this.CreateValidator();
            //
            // Find the endcert and the root cert
            // We'll trust the root cert, but the intermediaries are not trusted
            //            
            m_endCerts = m_resolver.GetCertificates(new MailAddress("foo@end.xyz"));
            m_trustedAnchors = m_resolver.GetCertificatesForDomain("root.xyz");
        }
        
        [Fact]
        public void TestValidTrustChain()
        {
            Assert.True(!m_endCerts.IsNullOrEmpty());
            Assert.True(!m_trustedAnchors.IsNullOrEmpty());
            
            //
            // Ok, verify certs..
            //            
            foreach(X509Certificate2 cert in m_endCerts)
            {
                X509Certificate2Collection issuers = m_validator.ResolveIntermediateIssuers(cert);
                Assert.True(!issuers.IsNullOrEmpty() && issuers.Count == 3);
                Assert.True(m_validator.IsTrustedCertificate(cert, m_trustedAnchors));
            }            
        }
        
        [Fact]
        public void TestFailMaxLength()
        {
            m_validator.MaxIssuerChainLength = 1;
            foreach (X509Certificate2 cert in m_endCerts)
            {
                Assert.False(m_validator.IsTrustedCertificate(cert, m_trustedAnchors));
            }
        }

        [Fact]
        public void TestInvalidTrustChain()
        {
            //
            // We'll remove one of the intermediate certs so it will not resolve
            // Then we should get validation failures
            //
            m_store.Remove("inter11.xyz");
            m_validator.IssuerResolver = m_store.CreateResolver();
            foreach (X509Certificate2 cert in m_endCerts)
            {
                Assert.False(m_validator.IsTrustedCertificate(cert, m_trustedAnchors));
            }
        }
        
        [Fact]
        public void TestDnsAltName()
        {
            MemoryX509Store dnsCerts = TestCertificates.LoadCertificateFolder("Dns");
            foreach(X509Certificate2 dnsCert in dnsCerts)
            {
                string dnsName = dnsCert.GetNameInfo(X509NameType.DnsName, false);
                Assert.True(dnsCert.MatchDnsName(dnsName));
                Assert.True(dnsCert.MatchDnsOrEmailOrName(dnsName));
            }
        }
        
        TrustChainValidator CreateValidator()
        {
            TrustChainValidator validator = new TrustChainValidator();
            validator.IssuerResolver = m_resolver;
            validator.ProblemFlags =
                X509ChainStatusFlags.NotTimeValid |
                X509ChainStatusFlags.Revoked |
                X509ChainStatusFlags.NotSignatureValid |
                X509ChainStatusFlags.CtlNotTimeValid |
                X509ChainStatusFlags.CtlNotSignatureValid;
            
            return validator;
        }
    }
}