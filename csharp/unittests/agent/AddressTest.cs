/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Arien Malec     arien.malec@nhindirect.org
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;

using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Agent.Tests
{
    public class AddressTest
    {

        [Fact]
        public void TestBasicAddressCreate()
        {
            DirectAddress addr = new DirectAddress("eleanor@roosevelt.com");
            Assert.Null(addr.Certificates);
            Assert.False(addr.HasCertificates);
            Assert.Null(addr.TrustAnchors);
            Assert.False(addr.HasTrustAnchors);
            Assert.Equal<TrustEnforcementStatus>(TrustEnforcementStatus.Unknown, addr.Status);
            Assert.False(addr.IsTrusted(TrustEnforcementStatus.Success));
            Assert.Equal<string>("eleanor", addr.User);
            Assert.Equal<string>("roosevelt.com", addr.Host);
        }

        [Fact]
        public void TestAddressCertificates()
        {
            DirectAddress addr = new DirectAddress(new MailAddress("\"Eleanor Roosevelt\" <eleanor@roosevelt.org>"));
            addr.Certificates = new X509Certificate2Collection(new X509Certificate2());
            Assert.True(addr.HasCertificates);
        }

        [Fact]
        public void TestAddressTrustAnchors()
        {
            DirectAddress addr = new DirectAddress(new MailAddress("\"Eleanor Roosevelt\" <eleanor@roosevelt.org>"));
            addr.TrustAnchors = new X509Certificate2Collection(new X509Certificate2());
            Assert.True(addr.HasTrustAnchors);
        }

        [Fact]
        public void TestAddressTrustStatusSuccess()
        {
            DirectAddress addr = new DirectAddress("\"Eleanor Roosevelt\" <eleanor@roosevelt.org>");
            addr.Status = TrustEnforcementStatus.Success;
            Assert.True(addr.IsTrusted(TrustEnforcementStatus.Success));
        }

        [Fact]
        public void TestAddressTrustStatusFailure()
        {
            DirectAddress addr = new DirectAddress("\"Eleanor Roosevelt\" <eleanor@roosevelt.org>");
            addr.Status = TrustEnforcementStatus.Failed;
            Assert.False(addr.IsTrusted(TrustEnforcementStatus.Success));
            Assert.False(addr.IsTrusted(TrustEnforcementStatus.Unknown));
        }

        [Fact]
        public void TestBasicAdddressCollectionCreate()
        {
            DirectAddressCollection coll = new DirectAddressCollection();
            Assert.False(coll.IsTrusted());
            Assert.Equal<string>("", coll.ToString());
            Assert.False(coll.IsTrusted(TrustEnforcementStatus.Failed));
            Assert.True(coll.GetCertificates().Count == 0);
            Assert.True(coll.Certificates.Count() == 0);
            Assert.True(coll.GetUntrusted().Count() == 0);
            Assert.True(coll.GetTrusted().Count() == 0);
            Assert.DoesNotThrow(() => coll.RemoveUntrusted());
        }


        [Theory]
        [InlineData(null,0)]
        [InlineData("", 0)]
        [InlineData("eleanor@roosevelt.com, \"Franklin Roosevelt\" <frank@roosevelt.com>, sean+o'nolan@tinymollitude.net", 3)]
        public void TestParseAddressCollection(string addressList, int expectedCount)
        {
            DirectAddressCollection coll = DirectAddressCollection.Parse(addressList);
            Assert.Equal<int>(expectedCount, coll.Count);
        }

        [Fact]
        public void TestAddressCollectionIsTrustedMixedStatus()
        {
            //Mixed trusted and untrusted should be untrusted
            DirectAddressCollection coll = BasicCollection();
            Assert.False(coll.IsTrusted());
        }

        [Fact]
        public void TestAddressCollectionIsTrustedAllTrusted()
        {
            DirectAddressCollection coll = BasicCollection();
            foreach(DirectAddress addr in coll) { addr.Status = TrustEnforcementStatus.Success; }
            //All trusted addresses should be trusted
            Assert.True(coll.IsTrusted());
        }

        [Fact]
        public void TestAddressCollectionIsTrustedExplicitStatus()
        {
            DirectAddressCollection coll = BasicCollection();
            // should be able to define a custom floor for trust
            Assert.True(coll.IsTrusted(TrustEnforcementStatus.Failed));
        }


        [Fact]
        public void TestAddressCollectionGetTrusted()
        {
            DirectAddressCollection coll = BasicCollection();
            IEnumerable<DirectAddress> trusted = coll.GetTrusted();
            Assert.Equal(1, trusted.Count());
            Assert.Equal("tinymollitude.net", trusted.First().Host);
            Assert.Equal("sean+o'nolan", trusted.First().User);
        }

        [Fact]
        public void TestAddressCollectionGetUntrusted()
        {
            DirectAddressCollection coll = BasicCollection();
            Assert.Equal(2, coll.GetUntrusted().Count());
            Assert.Equal(2, coll.GetTrusted(TrustEnforcementStatus.Unknown).Count());
        }

        [Fact]
        public void TestAddressCollectionRemoveUntrusted()
        {
            DirectAddressCollection coll = BasicCollection();
            coll.RemoveUntrusted();
            Assert.Equal(1, coll.Count);
        }

        [Fact]
        public void TestAddressCollectionsCertificates()
        {
            DirectAddressCollection coll = BasicCollection();
            X509Certificate2 certa = new X509Certificate2();
            X509Certificate2 certb = new X509Certificate2();
            X509Certificate2 certc = new X509Certificate2();

            coll[0].Certificates = new X509Certificate2Collection(certa);
            coll[1].Certificates = new X509Certificate2Collection(certb);
            coll[2].Certificates = new X509Certificate2Collection(certc);
            IEnumerable<X509Certificate2> certs = coll.Certificates;
            Assert.Contains<X509Certificate2>(certa, certs);
            Assert.Contains<X509Certificate2>(certb, certs);
            Assert.Contains<X509Certificate2>(certc, certs);
        }

        DirectAddressCollection BasicCollection()
        {
            string[] addrStrings = new string[] {
                                                    "eleanor@roosevelt.com",
                                                    "\"Franklin Roosevelt\" <frank@roosevelt.com>",
                                                    "sean+o'nolan@tinymollitude.net"};
            IEnumerable<DirectAddress> addrs =  addrStrings.Select(a => new DirectAddress(a));
            DirectAddressCollection coll = new DirectAddressCollection();
            coll.Add(addrs);
            coll[0].Status = TrustEnforcementStatus.Failed;
            coll[1].Status = TrustEnforcementStatus.Unknown;
            coll[2].Status = TrustEnforcementStatus.Success;
            return coll;
        }

    }
}