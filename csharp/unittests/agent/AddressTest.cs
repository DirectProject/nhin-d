using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;

using Xunit;
using Xunit.Extensions;

using NHINDirect.Agent;

namespace AgentTests
{
    public class AddressTest
    {

        [Fact]
        public void TestBasicAddressCreate()
        {
            NHINDAddress addr = new NHINDAddress("eleanor@roosevelt.com");
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
            NHINDAddress addr = new NHINDAddress(new MailAddress("\"Eleanor Roosevelt\" <eleanor@roosevelt.org>"));
            addr.Certificates = new X509Certificate2Collection(new X509Certificate2());
            Assert.True(addr.HasCertificates);
        }

        [Fact]
        public void TestAddressTrustAnchors()
        {
            NHINDAddress addr = new NHINDAddress(new MailAddress("\"Eleanor Roosevelt\" <eleanor@roosevelt.org>"));
            addr.TrustAnchors = new X509Certificate2Collection(new X509Certificate2());
            Assert.True(addr.HasTrustAnchors);
        }

        [Fact]
        public void TestAddressTrustStatusSuccess()
        {
            NHINDAddress addr = new NHINDAddress("\"Eleanor Roosevelt\" <eleanor@roosevelt.org>");
            addr.Status = TrustEnforcementStatus.Success;
            Assert.True(addr.IsTrusted(TrustEnforcementStatus.Success));
        }

        [Fact]
        public void TestAddressTrustStatusFailure()
        {
            NHINDAddress addr = new NHINDAddress("\"Eleanor Roosevelt\" <eleanor@roosevelt.org>");
            addr.Status = TrustEnforcementStatus.Failed;
            Assert.False(addr.IsTrusted(TrustEnforcementStatus.Success));
            Assert.False(addr.IsTrusted(TrustEnforcementStatus.Unknown));
        }

        [Fact]
        public void TestBasicAdddressCollectionCreate()
        {
            NHINDAddressCollection coll = new NHINDAddressCollection();
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
            NHINDAddressCollection coll = NHINDAddressCollection.Parse(addressList);
            Assert.Equal<int>(expectedCount, coll.Count);
        }

        [Fact]
        public void TestAddressCollectionIsTrustedMixedStatus()
        {
            //Mixed trusted and untrusted should be untrusted
            NHINDAddressCollection coll = BasicCollection();
            Assert.False(coll.IsTrusted());
        }

        [Fact]
        public void TestAddressCollectionIsTrustedAllTrusted()
        {
            NHINDAddressCollection coll = BasicCollection();
            foreach(NHINDAddress addr in coll) { addr.Status = TrustEnforcementStatus.Success; }
            //All trusted addresses should be trusted
            Assert.True(coll.IsTrusted());
        }

        [Fact]
        public void TestAddressCollectionIsTrustedExplicitStatus()
        {
            NHINDAddressCollection coll = BasicCollection();
            // should be able to define a custom floor for trust
            Assert.True(coll.IsTrusted(TrustEnforcementStatus.Failed));
        }


        [Fact]
        public void TestAddressCollectionGetTrusted()
        {
            NHINDAddressCollection coll = BasicCollection();
            IEnumerable<NHINDAddress> trusted = coll.GetTrusted();
            Assert.Equal(1, trusted.Count());
            Assert.Equal("tinymollitude.net", trusted.First().Host);
            Assert.Equal("sean+o'nolan", trusted.First().User);
        }

        [Fact]
        public void TestAddressCollectionGetUntrusted()
        {
            NHINDAddressCollection coll = BasicCollection();
            Assert.Equal(2, coll.GetUntrusted().Count());
            Assert.Equal(2, coll.GetTrusted(TrustEnforcementStatus.Unknown).Count());
        }

        [Fact]
        public void TestAddressCollectionRemoveUntrusted()
        {
            NHINDAddressCollection coll = BasicCollection();
            coll.RemoveUntrusted();
            Assert.Equal(1, coll.Count);
        }

        NHINDAddressCollection BasicCollection()
        {
            string[] addrStrings = new string[] {
                "eleanor@roosevelt.com",
                "\"Franklin Roosevelt\" <frank@roosevelt.com>",
                "sean+o'nolan@tinymollitude.net"};
            IEnumerable<NHINDAddress> addrs =  addrStrings.Select(a => new NHINDAddress(a));
            NHINDAddressCollection coll = new NHINDAddressCollection();
            coll.Add(addrs);
            coll[0].Status = TrustEnforcementStatus.Failed;
            coll[1].Status = TrustEnforcementStatus.Unknown;
            coll[2].Status = TrustEnforcementStatus.Success;
            return coll;
        }

    }
}
