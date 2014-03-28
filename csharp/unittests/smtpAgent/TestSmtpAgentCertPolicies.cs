/* 
 Copyright (c) 2014, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using Health.Direct.Agent;
using Health.Direct.Agent.Config;
using Health.Direct.Agent.Tests;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Policies;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.SmtpAgent.Config;
using Health.Direct.SmtpAgent.Policy;
using Moq;
using Xunit;

namespace Health.Direct.SmtpAgent.Tests
{
    public class TestSmtpAgentCertPolicies : SmtpAgentMocks
    {
        

        static TestSmtpAgentCertPolicies()
        {
            AgentTester.EnsureStandardMachineStores();        
        }

        

        [Fact]
        public void TestFilterCertificateByPolicy_nullResolver_assertNoCertsFiltered()
        {
            SmtpAgent m_agent = SmtpAgentFactory.Create(GetSettingsPath("TestSmtpAgentConfig.xml"));

            CleanMessages(m_agent.Settings);
            CleanMonitor();

            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = true;
            m_agent.Settings.Notifications.AlwaysAck = true;
            m_agent.Settings.Notifications.AutoDsnFailureCreation =
                NotificationSettings.AutoDsnOption.Always.ToString();

            MdnMemoryStore.Clear();
            Mock<ClientSettings> mockMdnClientSettings = MockMdnClientSettings();
            m_agent.Settings.MdnMonitor = mockMdnClientSettings.Object;

            m_agent.Settings.CertPolicies.Resolvers = null;
            
            //
            // Process loopback messages.  Leaves un-encrypted mdns in pickup folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            var sendingMessage = LoadMessage(TestMessage);
            Assert.DoesNotThrow(() => RunEndToEndTest(sendingMessage, m_agent));

            //
            // grab the clear text mdns and delete others.
            //
            bool foundMdns = false;
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                if (messageText.Contains("disposition-notification"))
                {
                    foundMdns = true;
                    Assert.DoesNotThrow(() => RunMdnOutBoundProcessingTest(LoadMessage(messageText), m_agent));
                }
            }
            Assert.True(foundMdns);
        }

        
        [Fact]
        public void TestFilterCertificateByPolicy_noIncomingExpressions_assertNoCertsFiltered()
        {
            string configPath = GetSettingsPath("TestSmtpAgentConfig.xml");
            SmtpAgentSettings settings = SmtpAgentSettings.LoadSettings(configPath);

            CleanMessages(settings);
            CleanMonitor();

            settings.InternalMessage.EnableRelay = true;
            settings.Notifications.AutoResponse = true;
            settings.Notifications.AlwaysAck = true;
            settings.Notifications.AutoDsnFailureCreation =
                NotificationSettings.AutoDsnOption.Always.ToString();

            MdnMemoryStore.Clear();
            Mock<ClientSettings> mockMdnClientSettings = MockMdnClientSettings();
            settings.MdnMonitor = mockMdnClientSettings.Object;


            //
            // Mock the trust policy resolver
            //
            PolicyResolverSettings trustPolicySettings =
                settings.CertPolicies.Resolvers.First(r => r.Name == CertPolicyResolvers.TrustPolicyName);
            Assert.NotNull(trustPolicySettings);
            settings.CertPolicies.Resolvers.Remove(trustPolicySettings);

            Mock<IPolicyResolver> mockTrustPolicyResolver = new Mock<IPolicyResolver>();
            Mock<PolicyResolverSettings> mockPolicyResolverSettings = new Mock<PolicyResolverSettings>().SetupAllProperties();
            mockPolicyResolverSettings.Setup(s => s.CreateResolver()).Returns(mockTrustPolicyResolver.Object);
            mockPolicyResolverSettings.Setup(s => s.Name).Returns( CertPolicyResolvers.TrustPolicyName);
            settings.CertPolicies.Resolvers.Add(mockPolicyResolverSettings.Object);
            
            mockTrustPolicyResolver.Setup(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()))
                .Returns(new List<IPolicyExpression>());

            SmtpAgent smtpAgent = SmtpAgentFactory.Create(settings);

            //
            // Process loopback messages.  Leaves un-encrypted mdns in pickup folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            var sendingMessage = LoadMessage(TestMessage);
            Assert.DoesNotThrow(() => RunEndToEndTest(sendingMessage, smtpAgent));

            //
            // grab the clear text mdns and delete others.
            //
            bool foundMdns = false;
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                if (messageText.Contains("disposition-notification"))
                {
                    foundMdns = true;
                    Assert.DoesNotThrow(() => RunMdnOutBoundProcessingTest(LoadMessage(messageText), smtpAgent));
                }
            }
            Assert.True(foundMdns);

            mockTrustPolicyResolver.Verify(r => r.GetIncomingPolicy(It.IsAny<MailAddress>())
                , Times.Exactly(2)); //To: <biff@nhind.hsgincubator.com>, <bob@nhind.hsgincubator.com>
        }


        //
        // Processing message like smtp gateway would
        //
        CDO.Message RunEndToEndTest(CDO.Message message, SmtpAgent agent)
        {
            agent.ProcessMessage(message);
            message = LoadMessage(message);
            VerifyOutgoingMessage(message);

            agent.ProcessMessage(message);
            message = LoadMessage(message);

            if (agent.Settings.InternalMessage.EnableRelay)
            {
                VerifyIncomingMessage(message);
            }
            else
            {
                VerifyOutgoingMessage(message);
            }
            return message;
        }

        void RunMdnOutBoundProcessingTest(CDO.Message message, SmtpAgent agent)
        {
            VerifyMdnIncomingMessage(message);      //Plain Text
            agent.ProcessMessage(message);        //Encrypts
            VerifyOutgoingMessage(message);    //Mdn looped back
        }
    }
}
