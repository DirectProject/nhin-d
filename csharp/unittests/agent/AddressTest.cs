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
        public void TestAddressTrustStatus()
        {
            NHINDAddress addr = new NHINDAddress(new MailAddress("\"Eleanor Roosevelt\" <eleanor@roosevelt.org>"));
            addr.Certificates = new X509Certificate2Collection(new X509Certificate2());
            addr.TrustAnchors = new X509Certificate2Collection(new X509Certificate2());
            Assert.True(addr.HasCertificates);
            Assert.True(addr.HasTrustAnchors);
            addr.Status = TrustEnforcementStatus.Success;
            Assert.True(addr.IsTrusted(TrustEnforcementStatus.Success));
            addr.Status = TrustEnforcementStatus.Failed;
            Assert.False(addr.IsTrusted(TrustEnforcementStatus.Success));
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

        [Fact]
        public void TestAddressCollectionTrust()
        {
            NHINDAddressCollection coll = BasicCollection();
            Assert.False(coll.IsTrusted());
            Assert.True(coll.IsTrusted(TrustEnforcementStatus.Unknown));
            foreach (NHINDAddress addr in coll)
            {
                addr.Status = TrustEnforcementStatus.Success;
            }
            Assert.True(coll.IsTrusted());
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
        public void TestAddressCollectionTrustedUntrusted()
        {
            NHINDAddressCollection coll = BasicCollection();
            coll[0].Status = TrustEnforcementStatus.Failed;
            coll[1].Status = TrustEnforcementStatus.Unknown;
            coll[2].Status = TrustEnforcementStatus.Success;

            Assert.False(coll.IsTrusted());
            Assert.Equal(1, coll.GetTrusted().Count());
            Assert.Equal(2, coll.GetUntrusted().Count());
            Assert.Equal(2, coll.GetTrusted(TrustEnforcementStatus.Unknown).Count());

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
            return coll;
        }

    }
}
