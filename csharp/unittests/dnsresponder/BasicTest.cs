using System;
using System.Collections.Generic;
using System.Linq;

using Health.Direct.Common.Resolver;

using Xunit;
using Xunit.Extensions;

namespace Health.Direct.DnsResponder.Tests
{
    public class BasicTest : Tester
    {        
        public static IEnumerable<object[]> Domains
        {
            get
            {
                foreach(string domain in TestStore.Default.Domains)
                {
                    yield return new[] {domain};
                }
            }
        }
        
        public static IEnumerable<object[]> UnknownDomains
        {
            get
            {
                yield return new[] {"adljflakd"};
                yield return new[] {"lasdkjfal"};
            }
        }
        
        [Theory]
        [PropertyData("Domains")]
        public void TestUdpLookupA(string domain)
        {
            TestSuccess(domain, true);
        }

        [Theory]
        [PropertyData("Domains")]
        public void TestTcpLookupA(string domain)
        {
            TestSuccess(domain, false);
        }
        
        //[Theory]
        [PropertyData("UnknownDomains")]
        public void TestFailureUdp(string domain)
        {
            IEnumerable<AddressRecord> matches = ResolveA(TestServer.Default, domain, true);
            Assert.True(matches == null || matches.Count() == 0);
        }

        //[Theory]
        [PropertyData("UnknownDomains")]
        public void TestFailureTcp(string domain)
        {
            IEnumerable<AddressRecord> matches = ResolveA(TestServer.Default, domain, false);
            Assert.True(matches == null || matches.Count() == 0);
        }
        
        const int MultithreadThreadCount = 4;
        const int MultithreadRepeat = 250;
        [Fact]
        public void TestMultithreadUdp()
        {
            TestThreads(MultithreadThreadCount, true, TimeSpan.FromMilliseconds(5000));
        }

        [Fact]
        public void TestMultithreadTcp()
        {
            TestThreads(MultithreadThreadCount, false, TimeSpan.FromMilliseconds(5000));
        }
        
        void TestThreads(int threadCount, bool udp, TimeSpan timeout)
        {
            TestThreads(threadCount, udp, Repeater(TestStore.Default.Domains, MultithreadRepeat), TestStore.Default.Count * MultithreadRepeat, timeout);
        }
                
        void TestThreads(int threadCount, bool udp, IEnumerable<string> domains, int expectedSuccess, TimeSpan timeout)
        {
            TestThread[] threads = new TestThread[threadCount];
            for (int i = 0; i < threads.Length; ++i)
            {
                threads[i] = new TestThread(DnsStandard.RecordType.ANAME, udp, TestServer.Default, timeout);
            }
            for (int i = 0; i < threads.Length; ++i)
            {
                threads[i].Start(domains);
            }
            for (int i = 0; i < threads.Length; ++i)
            {
                threads[i].Stop();
            }
            for (int i = 0; i < threads.Length; ++i)
            {
                Assert.True(threads[i].Failure == 0);
                Assert.True(threads[i].Success == expectedSuccess);
            }            
        }
        
        void TestSuccess(string domain, bool useUDP)
        {
            TestSuccess(TestServer.Default, TestStore.Default, domain, false);
        }
        
        void TestSuccess(TestServer server, TestStore store, string domain, bool useUDP)
        {
            IEnumerable<AddressRecord> matches = Tester.ResolveA(server, domain, useUDP);
            IEnumerable<DnsResourceRecord> expectedMatches = store.Store.Records[domain, DnsStandard.RecordType.ANAME];

            Assert.True(Tester.Equals<AddressRecord>(matches, expectedMatches));
        }
    }
}