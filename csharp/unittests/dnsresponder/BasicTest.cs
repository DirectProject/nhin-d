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
using System;
using System.Collections.Generic;
using System.Linq;

using Health.Direct.Common.DnsResolver;

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
                foreach (string domain in TestStore.Default.Domains)
                {
                    yield return new object[] { domain, UseUdp };
                    yield return new object[] { domain, UseTcp };
                }
            }
        }

        public static IEnumerable<object[]> UnknownDomains
        {
            get
            {
                yield return new object[] { "adljflakd", UseUdp };
                yield return new object[] { "lasdkjfal", UseUdp };
                yield return new object[] { "adljflakd", UseTcp };
                yield return new object[] { "lasdkjfal", UseTcp };
            }
        }

        public static IEnumerable<object[]> NotSupported
        {
            get
            {
                foreach (string domain in TestStore.Default.Domains)
                {
                    foreach(DnsStandard.RecordType type in TestServer.NotSupported)
                    {
                        yield return new object[] { domain, type, UseUdp };
                        yield return new object[] { domain, type, UseTcp };
                    }
                }
            }
        }

        [Theory]
        [PropertyData("Domains")]
        public void TestLookupA(string domain, bool useUDP)
        {
            IEnumerable<AddressRecord> matches = ResolveA(TestServer.Default, domain, useUDP);
            IEnumerable<DnsResourceRecord> expectedMatches = TestStore.Default.Store.Records[domain, DnsStandard.RecordType.ANAME];
            Assert.True(Equals(matches, expectedMatches));
        }

        [Theory]
        [PropertyData("UnknownDomains")]
        public void TestFailure(string domain, bool useUDP)
        {
            IEnumerable<AddressRecord> matches = ResolveA(TestServer.Default, domain, useUDP);
            Assert.True(matches == null || matches.Count() == 0);
        }
                
        const int MultithreadThreadCount = 16;  
        const int MultithreadRepeat = 500;
        
        [Theory]
        [PropertyData("NotSupported")]
        public void TestNotSupported(string domain, DnsStandard.RecordType type, bool useUDP)
        {
            DnsClient client = TestServer.Default.CreateClient();
            client.UseUDPFirst = !useUDP;
            DnsResponse response = client.Resolve(new DnsRequest(type, domain));
            Assert.True(!response.IsSuccess);
        }
        
        [Theory]
        [InlineData(UseUdp)]
        [InlineData(UseTcp)]
        public void TestMultithread(bool useUDP)
        {
            TestThreads(MultithreadThreadCount, useUDP, TimeSpan.FromMilliseconds(5000));
        }

        static void TestThreads(int threadCount, bool udp, TimeSpan timeout)
        {
            TestThreads(threadCount, udp, Repeater(TestStore.Default.Domains, MultithreadRepeat), TestStore.Default.Count * MultithreadRepeat, timeout);
        }

        static void TestThreads(int threadCount, bool udp, IEnumerable<string> domains, int expectedSuccess, TimeSpan timeout)
        {
            TestThread[] threads = new TestThread[threadCount];
            for (int i = 0; i < threads.Length; ++i)
            {
                threads[i] = new TestThread(DnsStandard.RecordType.ANAME, udp, TestServer.Default, timeout);
            }
            for (int i = 0; i < threads.Length; ++i)
            {
                if (!udp && i == threads.Length - 1)
                {
                    threads[i].Start(domains, threads[i].TcpSocketDropper);
                }
                else
                {
                    threads[i].Start(domains);
                }
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
            
            if (udp)
            {
                TestServer.Default.AreMaxUdpAcceptsOutstanding();
            }            
            else
            {
                TestServer.Default.AreMaxTcpAcceptsOutstanding();
            }
        }
    }
}