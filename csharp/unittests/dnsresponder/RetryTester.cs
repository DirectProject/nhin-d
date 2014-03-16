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
using System.Text;
using Health.Direct.Common.DnsResolver;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.DnsResponder.Tests
{
    public class RetryTester : Tester
    {
        static TestServer s_failureServer;
        static FailureStore s_failureStore;
                
        static RetryTester()
        {
            s_failureStore = new FailureStore(TestStore.Default.Store);
            DnsServerSettings settings = new DnsServerSettings();
            settings.Address = "127.0.0.1";
            settings.Port = 5373;
            
            s_failureServer = new TestServer(s_failureStore, settings);
            s_failureServer.Server.Start();
        }

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
        
        [Theory]
        [PropertyData("Domains")]
        public void TestClientRetrySuccess(string domain, bool useUDP)
        {
            SetTimeout(5000, useUDP);
            s_failureStore.SuccessInterval = 2;
            s_failureStore.ThrowDnsException = true;
            
            IEnumerable<AddressRecord> matches = ResolveA(s_failureServer, domain, useUDP);
            IEnumerable<DnsResourceRecord> expectedMatches = TestStore.Default.Store.Records[domain, DnsStandard.RecordType.ANAME];
            Assert.True(Equals(matches, expectedMatches));

            s_failureStore.ThrowDnsException = false;
            matches = ResolveA(s_failureServer, domain, useUDP);
            expectedMatches = TestStore.Default.Store.Records[domain, DnsStandard.RecordType.ANAME];
            Assert.True(Equals(matches, expectedMatches));
        }

        [Theory]
        [PropertyData("Domains")]
        public void TestClientRetryFail(string domain, bool useUDP)
        {
            SetTimeout(5000, useUDP);
            s_failureStore.SuccessInterval = 0; // Force universal failures
            Assert.Throws<DnsProtocolException>(() => ResolveA(s_failureServer, domain, useUDP));
        }

        [Fact]
        public void ForceServerTCPTimeoutSlowClient()
        {
            string[] domains = TestStore.Default.Domains.ToArray();
            
            s_failureServer.Counters.InitWait(domains.Length);

            s_failureStore.SuccessInterval = 1; // Always succeed
            SetTimeout(100, false);
            
            foreach(string domain in domains)
            {
                using(BadTcpClient client = s_failureServer.CreateBadTcpClient())
                {
                    try
                    {
                        client.Connect();
                        client.SendLength(100);
                        client.SendSlowGarbage(100, 10);
                    }
                    catch
                    {
                    }
                }
            }

            Assert.DoesNotThrow(() => s_failureServer.Counters.Wait.WaitOne(20 * 1000));
            Assert.True(s_failureServer.Counters.IsConnectionBalanced);
        }

        [Fact]
        public void ForceServerTCPTimeout()
        {
            string[] domains = TestStore.Default.Domains.ToArray();

            s_failureServer.Counters.InitWait(domains.Length);

            s_failureStore.SuccessInterval = 1; // Always succeed
            SetTimeout(100, false);

            foreach (string domain in domains)
            {
                using(BadTcpClient client = s_failureServer.CreateBadTcpClient())
                {
                    try
                    {
                        client.ConnectAndPause(1000);
                    }
                    catch
                    {
                    }
                }
            }

            Assert.DoesNotThrow(() => s_failureServer.Counters.Wait.WaitOne(20 * 1000));
            Assert.True(s_failureServer.Counters.IsConnectionBalanced);
        }
        
        void SetTimeout(int ms, bool useUDP)
        {
            if (useUDP)
            {
                SetUdpTimeout(ms);
            }
            else
            {
                SetTcpTimeout(ms);
            }
        }
        
        void SetTcpTimeout(int ms)
        {
            s_failureServer.Server.Settings.TcpServerSettings.ReceiveTimeout = ms;
            s_failureServer.Server.Settings.TcpServerSettings.SendTimeout = ms;            
        }

        void SetUdpTimeout(int ms)
        {
            s_failureServer.Server.Settings.UdpServerSettings.ReceiveTimeout = ms;
            s_failureServer.Server.Settings.UdpServerSettings.SendTimeout = ms;
        }
    }
}
