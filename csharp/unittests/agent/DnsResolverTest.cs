using System.Collections.Generic;
using System.Net;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;

using NHINDirect.Certificates;

using Xunit;
using Xunit.Extensions;

namespace AgentTests
{
    public class DnsResolverTest
    {
        public const string ServerIP = "127.0.0.1";
        
        public static IEnumerable<object[]> GoodAddresses
        {
			get
			{
				yield return new[] {"bob@nhind.hsgincubator.com"};
				yield return new[] {"biff@nhind.hsgincubator.com"};
				yield return new[] {"gatewaytest@hotmail.com"};
			}
        }
        
        [Theory(Skip = "Requires Bind Server to be running on the local server")]
		[PropertyData("GoodAddresses")]
        public void GetCertificateWithGoodAddress(string address)
        {
        	var resolver
        		= new DnsCertResolver(IPAddress.Parse(ServerIP), 5000, "hsgincubator.com")
        		  	{
        		  		AssumeWildcardSupport = false
        		  	};

        	X509Certificate2Collection certs = resolver.GetCertificates(new MailAddress(address));
        	Assert.True(certs.Count > 0);
        }
    }
}
