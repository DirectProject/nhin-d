/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joe Shook	    jshook@kryptiq.com

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using Health.Direct.Agent;
using Health.Direct.Agent.Config;
using Health.Direct.Agent.Tests;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Policies;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Client.MonitorService;
using Health.Direct.Config.Store;
using Health.Direct.SmtpAgent.Config;
using Health.Direct.SmtpAgent.Policy;
using Moq;
using Xunit;

namespace Health.Direct.SmtpAgent.Tests
{
    public class SmtpAgentMocks : SmtpAgentTester
    {
        protected List<Mdn> MdnMemoryStore = new List<Mdn>();
        protected List<Address> AddressMemoryStore = new List<Address>();

        protected Mock<ClientSettings> MockMdnClientSettings()
        {
            Mock<ClientSettings> mockClientSettings = new Mock<ClientSettings>();
            mockClientSettings.SetupAllProperties();
            Mock<IMdnMonitor> mockMdnMonitorClient = new Mock<IMdnMonitor>();
            mockMdnMonitorClient.SetupAllProperties();


            //
            // Ensure MdnMonitorClient calls start with Mdn[] type.
            // Also track the mdns created
            //
            mockMdnMonitorClient.Setup(mc => mc.Start(It.IsAny<Mdn[]>()))
                                .Callback<Mdn[]>(m => MdnMemoryStore = m.ToList());

            //
            // Ensure we create a MdnMonitorClient
            //
            mockClientSettings.Setup(c => c.CreateMdnMonitorClient())
                              .Returns(mockMdnMonitorClient.Object);
            return mockClientSettings;
        }

        protected Mock<ClientSettings> MockAddressClientSettings()
        {
            Mock<ClientSettings> mockClientSettings = new Mock<ClientSettings>();
            mockClientSettings.SetupAllProperties();
            Mock<IAddressManager> mockAddressManagerClient = new Mock<IAddressManager>();
            mockAddressManagerClient.SetupAllProperties();


            //
            // Ensure AddressManagerClient calls to GetAddress uses the internal AddressMemoryStore
            //
            mockAddressManagerClient.Setup(
                mc => mc.GetAddresses(It.IsAny<string[]>(), It.Is<EntityStatus>(e => e == EntityStatus.Enabled)))
                                    .Returns<string[], EntityStatus>(
                                    (x, s) =>
                                        {
                                            var addresses =
                                                AddressMemoryStore.Where(a => x.Contains(a.EmailAddress, StringComparer.OrdinalIgnoreCase )  && a.Status == s)
                                                                  .ToArray();
                                            if (addresses.IsNullOrEmpty())
                                            {
                                                return null;
                                            }
                                            return addresses;
                                        });
                   
          
            //
            // Ensure we create a AddressManagerClient
            //
            mockClientSettings.Setup(c => c.CreateAddressManagerClient())
                              .Returns(mockAddressManagerClient.Object);
            return mockClientSettings;
        }

        [Fact]
        public void TestMockAddressClientSettings()
        {
            Mock<ClientSettings> mockClientSettings = MockAddressClientSettings();
            var addressClientSettings = mockClientSettings.Object;
            var addressManager = addressClientSettings.CreateAddressManagerClient();
            var addresses = addressManager.GetAddresses(new string[] {"joe@hobo.lab"}, EntityStatus.Enabled);
            Assert.Null(addresses);

            AddressMemoryStore.AddRange(new Address[]
                {
                    new Address(){EmailAddress = "joe@hobo.lab", Status = EntityStatus.Enabled},
                    new Address(){EmailAddress = "bob@hobo.lab", Status = EntityStatus.Disabled},
                    new Address(){EmailAddress = "tim@hobo.lab", Status = EntityStatus.New},
                });

            addresses = addressManager.GetAddresses(new string[] { "joe@hobo.lab" }, EntityStatus.Enabled);
            Assert.NotNull(addresses);
            Assert.True(addresses[0].EmailAddress.Equals("joe@hobo.lab"));
            Assert.True(addresses[0].Status.Equals(EntityStatus.Enabled));

            addresses = addressManager.GetAddresses(new string[] { "bob@hobo.lab" }, EntityStatus.Enabled);
            Assert.Null(addresses);


            addresses = addressManager.GetAddresses(new string[] { "tim@hobo.lab" }, EntityStatus.Enabled);
            Assert.Null(addresses);
        }

        protected static void MockPublicCerts(DirectAddressCollection recipients, Mock<ICertificateResolver> mockDnsResolver)
        {
            foreach (var recipient in recipients)
            {
                string address = recipient.Address;
                string host = new MailAddress(address).Host;
                mockDnsResolver.Setup(
                    reslover => reslover.GetCertificates(It.Is<MailAddress>(adrs =>
                                                                            adrs.Address.Equals(
                                                                                new MailAddress(address).Address
                                                                                , StringComparison.OrdinalIgnoreCase))))
                               .Returns(TestCertificates.AllPublicCerts.Where((x) =>
                               {
                                   return
                                       x.MatchEmailNameOrName(address) ||
                                       x.MatchDnsName(host);
                               }
                                            ));
            }
        }

        //
        // In the resolver collection (mockPublicCertResolvers) the Mock<ICertificateResolver> is setup to return 
        // test certificates for a recipient.  But other Mock<[certresolvers]) are not mapped.
        // So including a resolver like the DnsTimeoutResolver is a great way to force a specific network behavior
        // into your testing harness.
        //
        protected static void MockPublicCerts(DirectAddressCollection recipients, List<object> mockPublicCertResolvers)
        {
            foreach (var recipient in recipients)
            {
                string address = recipient.Address;
                string host = new MailAddress(address).Host;

                foreach (object resolver in mockPublicCertResolvers)
                {
                    if (resolver.GetType() == typeof(Mock<ICertificateResolver>)) //Normal
                    {
                        ((Mock<ICertificateResolver>)resolver).Setup(
                        reslover => reslover.GetCertificates(It.Is<MailAddress>(adrs =>
                                                                            adrs.Address.Equals(
                                                                                new MailAddress(address).Address
                                                                                , StringComparison.OrdinalIgnoreCase))))
                               .Returns(TestCertificates.AllPublicCerts.Where((x) =>
                               {
                                   return
                                       x.MatchEmailNameOrName(address) ||
                                       x.MatchDnsName(host);
                               }
                                            ));
                    }
                    //
                    // Else is type DnsTimeoutResolver
                    // Do nothing
                    //
                }
            }
        }
    }
}
