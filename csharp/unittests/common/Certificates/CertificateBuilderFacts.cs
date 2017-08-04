/* 
 Copyright (c) 2017, Direct Project
 All rights reserved.

 Authors:
    Dávid Koronthály    koronthaly@hotmail.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

using System;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Certificates;
using Org.BouncyCastle.Asn1.X509;
using Xunit;

namespace Health.Direct.Common.Tests.Certificates
{
    public class CertificateBuilderFacts
    {
        private const string Domain = "nhind.hsgincubator.com";
        private X509Certificate2 _rootCA;


        [Fact]
        public void IssueSimpleCertificate()
        {
            var emailAddress = $"test@{Domain}";

            var issuer = CreateCertificateAuthority(Domain);
            var certificate = CreateAddressBoundCertificate(issuer, emailAddress);

            Assert.False(certificate.IsCertificateAuthority());
            Assert.True(certificate.HasValidDateRange());
            Assert.True(certificate.HasPrivateKey);
            Assert.Equal(certificate.IssuerName.ToString(), issuer.SubjectName.ToString());
            Assert.True(certificate.MatchName(emailAddress));
        }

        [Fact]
        public void IssueCertificateAuthority()
        {
            var certificate = CreateCertificateAuthority(Domain);

            Assert.True(certificate.IsCertificateAuthority());
            Assert.True(certificate.HasValidDateRange());
            Assert.True(certificate.HasPrivateKey);
            Assert.Equal(certificate.IssuerName.ToString(), certificate.SubjectName.ToString());
            Assert.True(certificate.MatchName(Domain));
        }

        private X509Certificate2 CreateCertificateAuthority(string domain)
        {
            if (_rootCA == null)
            {
                var builder = new CertificateBuilder(1)
                {
                    SubjectDN = new X509Name($"CN={domain}")
                };

                builder.SetSubjectAlternativeNameToDomain(domain);
                builder.Policies.Add(DirectTrustCertificatePolicies.DTorgCPVersions);
                _rootCA = builder.Generate();
            }

            return _rootCA;
        }

        private X509Certificate2 CreateAddressBoundCertificate(X509Certificate2 issuer, string emailAddress)
        {
            var domain = issuer.GetNameInfo(X509NameType.DnsName, false);

            var builder = new CertificateBuilder(issuer)
            {
                AuthorityInformationAccessUri = new Uri($"http://{domain}/pki/{domain}.cer"),
                CrlDistributionPointUri = new Uri($"http://{domain}/pki/{domain}.crl"),
                SubjectDN = new X509Name($"CN={emailAddress}")
            };

            builder.SetSubjectAlternativeNameToEmail(emailAddress);
            builder.Policies.Add(DirectTrustCertificatePolicies.DTorgCPVersions);
            return builder.Generate();
        }
    }
}
