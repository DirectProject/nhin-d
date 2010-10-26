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