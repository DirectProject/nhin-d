/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Ali Emami   aliemami@microsoft.com
  
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
using Xunit.Extensions;
using Health.Direct.Common.DnsResolver;
using System.IO;
using Xunit;
using System.Threading;

namespace Health.Direct.DnsResponder.Tests
{
    public class AuthoritativeRecordResolverTest : Tester
    {
        static DnsRecordTable s_recordTable; 
        static TestServer s_rootDnsServer; 
        static TestServer s_authoritativeResolverServer;        

        static AuthoritativeRecordResolverTest()
        {
            InitAuthoritativeResolver(); 

            InitRootDnsServer(); 
        }

        private static void InitAuthoritativeResolver()
        {
            // Setup the authoritative resolver.
            // Primary name server is on 127.0.0.1:5400.  
            AuthoritativeResolutionSettings settings = new AuthoritativeResolutionSettings()
            {
                Cache = true,
                DnsResolutionPort = 5400, // perform DNS resolutions on 5400
                PrimaryNameServer = new DnsIPEndpointSettings[] { new DnsIPEndpointSettings("127.0.0.1", 5400) },
                TimeoutMilliseconds = 30000
            };

            // Create the authoritative resolver. 
            AuthoritativeRecordResolver resolver = new AuthoritativeRecordResolver(settings);

            // Setup DNS server to host the authoritative resolver at 127.0.0.1:5401
            DnsServerSettings dnsServerSettings = new DnsServerSettings();
            dnsServerSettings.Address = "127.0.0.1";
            dnsServerSettings.Port = 5401;

            s_authoritativeResolverServer = new TestServer(resolver, dnsServerSettings);            
            s_authoritativeResolverServer.Server.Start();
        }

        private static void InitRootDnsServer()
        {   
            DnsServerSettings settings = new DnsServerSettings();
            settings.Address = "127.0.0.1";
            settings.Port = 5400;
            settings.TcpServerSettings.MaxOutstandingAccepts = 4;
            settings.TcpServerSettings.MaxActiveRequests = 16;
            settings.TcpServerSettings.ReceiveTimeout = 60 * 1000;
            settings.TcpServerSettings.SendTimeout = 60 * 1000;

            settings.UdpServerSettings.MaxOutstandingAccepts = 4;
            settings.UdpServerSettings.MaxActiveRequests = 16;
            settings.UdpServerSettings.ReceiveTimeout = 60 * 1000;
            settings.UdpServerSettings.SendTimeout = 60 * 1000;

            MemoryStore memoryDnsStore = new MemoryStore();

            DnsRecordTable table = new DnsRecordTable();
            
            // Address only
            table.Add(new NSRecord("abc.com", "127.0.0.1")); 
            table.Add(new AddressRecord("abc.com", "192.200.0.1"));
            table.Add(new AddressRecord("abc.com", "192.200.0.2"));
            
            // Cert + Address
            table.Add(new NSRecord("redmond.hsgincubator.com", "127.0.0.1"));
            table.Add(new AddressRecord("redmond.hsgincubator.com", "192.210.0.1"));
            table.Add(new CertRecord(new DnsX509Cert(File.ReadAllBytes("metadata\\certificates\\redmond.cer"))));

            // Cert MX + Address
            table.Add(new NSRecord("direct.hisp.com", "127.0.0.1"));
            table.Add(new AddressRecord("direct.hisp.com", "192.220.0.1"));
            table.Add(new MXRecord("direct.hisp.com", "gateway.direct.hisp.com"));
            

            s_recordTable = table;
            
            foreach (DnsResourceRecord record in s_recordTable.Records)
            {
                memoryDnsStore.Records.Add(record); 
            }            

            s_rootDnsServer = new TestServer(memoryDnsStore, settings);             
            s_rootDnsServer.Server.Start();            
        }

        public static IEnumerable<object[]> Domains
        {
            get
            {   
                yield return new object[] { "abc.com", DnsStandard.RecordType.ANAME};
                
                yield return new object[] { "redmond.hsgincubator.com", DnsStandard.RecordType.ANAME };
                yield return new object[] { "redmond.hsgincubator.com", DnsStandard.RecordType.CERT };
                                
                yield return new object[] { "direct.hisp.com", DnsStandard.RecordType.ANAME };
                yield return new object[] { "direct.hisp.com", DnsStandard.RecordType.MX };                
            }
        }

        public static IEnumerable<object[]> UnknownDomains
        {
            get
            {
                yield return new object[] { "unknowndomain.com", DnsStandard.RecordType.ANAME };
                                
                yield return new object[] { "abc.com", DnsStandard.RecordType.MX};
                yield return new object[] { "abc.com", DnsStandard.RecordType.CERT };                

                yield return new object[] { "test@redmond.hsgincubator.com", DnsStandard.RecordType.ANAME};
                yield return new object[] { "redmond.hsgincubator.com", DnsStandard.RecordType.MX };                
            }
        }

        [Theory]
        [PropertyData("Domains")]
        public void ResolveSuccess(string domain, DnsStandard.RecordType type)
        {   
            using (DnsClient client = s_authoritativeResolverServer.CreateClient())
            {   
                switch (type)
                {
                    case DnsStandard.RecordType.ANAME:
                        ResolveA(client, domain);
                        break; 
                    case DnsStandard.RecordType.MX:
                        ResolveMX(client, domain); 
                        break; 
                    case DnsStandard.RecordType.CERT:
                        ResolveCert(client, domain);                         
                        break; 
                    default:
                        throw new NotSupportedException();
                }               
            }
        }

        [Theory]
        [PropertyData("UnknownDomains")]
        public void ResolveWithNameErrors(string domain, DnsStandard.RecordType type)
        {
            using (DnsClient client = s_authoritativeResolverServer.CreateClient())
            {
                DnsResponse response = client.Resolve(new DnsRequest(new DnsQuestion(domain, type)));

                Assert.False(response.HasAdditionalRecords);
                Assert.False(response.HasAnswerRecords);
                Assert.False(response.HasAnyRecords);
                Assert.True(response.IsNameError); 
            }
        }

        [Fact]        
        public void ResolveVerifyTTLUpdate()
        {
            using (DnsClient client = s_authoritativeResolverServer.CreateClient())
            {
                DnsResponse response = client.Resolve(new DnsRequest(new DnsQuestion("direct.hisp.com", DnsStandard.RecordType.ANAME)));

                Thread.Sleep(2500);

                DnsResponse response2 = client.Resolve(new DnsRequest(new DnsQuestion("direct.hisp.com", DnsStandard.RecordType.ANAME)));

                for (int i = 0; i < response.AnswerRecords.Count; i++)
                {
                    Assert.True(response.AnswerRecords[i].TTL > response2.AnswerRecords[i].TTL);
                }

                for (int i = 0; i < response.AdditionalRecords.Count; i++)
                {
                    Assert.True(response.AdditionalRecords[i].TTL > response2.AdditionalRecords[i].TTL);
                }
                
                for (int i = 0; i < response.NameServerRecords.Count; i++)
                {
                    Assert.True(response.NameServerRecords[i].TTL > response2.NameServerRecords[i].TTL);
                }
            }
        }

        private void ResolveA(DnsClient client, string domain)
        {
            IEnumerable<DnsResourceRecord> expectedMatches = s_recordTable[domain, DnsStandard.RecordType.ANAME];
            IEnumerable<AddressRecord> matches = client.ResolveA(domain);
            Assert.True(Equals(matches, expectedMatches));
        }

        private void ResolveMX(DnsClient client, string domain)
        {
            IEnumerable<DnsResourceRecord> expectedMatches = s_recordTable[domain, DnsStandard.RecordType.MX];
            IEnumerable<MXRecord> matches = client.ResolveMX(domain);
            Assert.True(Equals(matches, expectedMatches));
        }

        private void ResolveCert(DnsClient client, string domain)
        {
            IEnumerable<DnsResourceRecord> expectedMatches = s_recordTable[domain, DnsStandard.RecordType.CERT];
            client.UseUDPFirst = false;
            IEnumerable<CertRecord> matches = client.ResolveCERT(domain);
            Assert.True(Equals(matches, expectedMatches));
        }
    }
}
