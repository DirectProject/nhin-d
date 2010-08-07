using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;
using NUnit.Framework;
using NHINDirect.Certificates;

namespace AgentTests
{
    //
    // Commented out since this currently requires BIND running on localhost
    //
    [TestFixture]
	[Category("Integration")]
    public class DnsResolverTest
    {
        public const string ServerIP = "127.0.0.1";
        
        static string[] GoodAddresses = new string[]
        {
            "bob@nhind.hsgincubator.com",
            "biff@nhind.hsgincubator.com",
            "gatewaytest@hotmail.com",
        };
        
        [Test]  
        public void Test()
        {
            DnsCertResolver resolver = new DnsCertResolver(IPAddress.Parse(DnsResolverTest.ServerIP), 5000, "hsgincubator.com");
            resolver.AssumeWildcardSupport = false;
            
            foreach(string address in GoodAddresses)
            {
                MailAddress mailAddress = new MailAddress(address);
                X509Certificate2Collection certs = resolver.GetCertificates(mailAddress);
                Assert.True(certs.Count > 0);
            }
        }
    }
}
