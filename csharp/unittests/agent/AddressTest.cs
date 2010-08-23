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
            Assert.Null(coll.GetCertificates());
            Assert.Null(coll.Certificates);
            Assert.Null(coll.GetUntrusted());
            Assert.Null(coll.GetTrusted());
        }



    }
}
