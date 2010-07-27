using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using DnsResolver;
using System.Net;

namespace DnsResolverTests
{
    [TestFixture]
    public class BasicResolverTests
    {
        const string PublicDns = "8.8.8.8";
        //const string PublicDns = "4.2.2.1";
        const string LocalDns = "127.0.0.1";
        
        public BasicResolverTests()
        {
        }
        
        [SetUp]
        public void Init()
        {
        }
        
        [Test]
        public void TestA()
        {
            this.Resolve(PublicDns, 
                        DnsRequest.CreateA,
                        "www.microsoft.com",
                        "www.yahoo.com",
                        "www.google.com",
                        "www.apple.com",
                        "nhind.hsgincubator.com",
                        "hvnhind.hsgincubator.com",
                        "dns.hsgincubator.com"
                        );
        }

        //[Test]
        public void TestCert()
        {
            this.Resolve(PublicDns,
                    DnsRequest.CreateCERT,
                    "nhind.hsgincubator.com",
                    "redmond.hsgincubator.com",
                    "gm2552.securehealthemail.com.hsgincubator.com",
                    "ses.testaccount.yahoo.com.hsgincubator.com",
                    "nhin1.rwmn.org.hsgincubator.com",
                    "nhin.whinit.org.hsgincubator.com"
            );
            
        }

        [Test]
        public void TestMX()
        {
            this.Resolve(PublicDns,
                        DnsRequest.CreateMX,
                        "nhind.hsgincubator.com",
                        "redmond.hsgincubator.com",
                        "www.microsoft.com"
                        );
        }


        IEnumerable<DnsRequest> CreateRequests(Func<string, DnsRequest> constructor, params string[] domains)
        {
            foreach (string domain in domains)
            {
                yield return constructor(domain);
            }
        }

        void Resolve(string server, Func<string, DnsRequest> constructor, params string[] domains)
        {
            this.Resolve(server, this.CreateRequests(constructor, domains));
        }

        void Resolve(string server, IEnumerable<DnsRequest> requests)
        {
            DnsClient client = this.CreateClient(server);
            foreach (DnsRequest request in requests)
            {
                try
                {
                    DnsResponse matches = client.Resolve(request);
                    if (matches == null || !matches.HasAnswerRecords)
                    {
                        this.DoAssert(request);
                    }
                }
                catch (DnsException)
                {
                    this.DoAssert(request);
                }
            }
        }

        DnsClient CreateClient(string server)
        {
            DnsClient client = new DnsClient(server);
            client.Timeout = 10000;
            return client;
        }
        
        void DoAssert(DnsRequest request)
        {
            Assert.Fail("{0}:{1}", request.Question.QType, request.Question.QName);
        }
    }
}
