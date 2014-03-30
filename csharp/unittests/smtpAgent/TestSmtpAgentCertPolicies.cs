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
using ADODB;
using CDO;
using Health.Direct.Agent;
using Health.Direct.Agent.Config;
using Health.Direct.Agent.Tests;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Policies;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Policy;
using Health.Direct.SmtpAgent.Config;
using Health.Direct.SmtpAgent.Policy;
using Moq;
using Org.BouncyCastle.Asn1.X509;
using Xunit;
using Xunit.Sdk;

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

            SetPolicyTestSettings(m_agent.Settings);
            
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

            SetPolicyTestSettings(settings);

            //
            // Mock all policy resolvers
            //
            Mock<PrivatePolicyResolver> mockPrivatePolicyResolver;
            Mock<PublicPolicyResolver> mockPublicPolicyResolver;
            Mock<TrustPolicyResolver> mockTrustPolicyResolver;
            Mock<IPolicyFilter> mockPolicyFilter;
            MockPrivatePolicyResolver(settings, out mockPrivatePolicyResolver, out mockPublicPolicyResolver, out mockTrustPolicyResolver, out mockPolicyFilter);

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

            mockPrivatePolicyResolver.Verify(r => r.GetIncomingPolicy(new MailAddress("biff@nhind.hsgincubator.com"))
                , Times.Exactly(1)); 
            mockPrivatePolicyResolver.Verify(r => r.GetIncomingPolicy(new MailAddress("bob@nhind.hsgincubator.com"))
                , Times.Exactly(1));
            mockPolicyFilter.Verify(p => p.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()), Times.Exactly(0));
            //
            // These two are never called.  These code paths do not exist.
            //
            mockPublicPolicyResolver.Verify(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()), Times.Never());
            mockTrustPolicyResolver.Verify(r => r.GetOutgoingPolicy(It.IsAny<MailAddress>()), Times.Never());
        }
        
        [Fact]
        public void TestFilterCertificateByPolicy_noOutgoingExpressions_assertNoCertsFiltered()
        {
            string configPath = GetSettingsPath("TestSmtpAgentConfig.xml");
            SmtpAgentSettings settings = SmtpAgentSettings.LoadSettings(configPath);

            CleanMessages(settings);
            CleanMonitor();

            SetPolicyTestSettings(settings);

            //
            // Mock all policy resolvers
            //
            Mock<PrivatePolicyResolver> mockPrivatePolicyResolver;
            Mock<PublicPolicyResolver> mockPublicPolicyResolver;
            Mock<TrustPolicyResolver> mockTrustPolicyResolver;
            Mock<IPolicyFilter> mockPolicyFilter;
            MockPrivatePolicyResolver(settings, out mockPrivatePolicyResolver, out mockPublicPolicyResolver, out mockTrustPolicyResolver, out mockPolicyFilter);

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

            mockPublicPolicyResolver.Verify(r => r.GetOutgoingPolicy(new MailAddress("biff@nhind.hsgincubator.com"))
                , Times.Exactly(1));
            mockPublicPolicyResolver.Verify(r => r.GetOutgoingPolicy(new MailAddress("bob@nhind.hsgincubator.com"))
                , Times.Exactly(1));
            mockPolicyFilter.Verify(p => p.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()), Times.Exactly(0));
            //
            // These two are never called.  These code paths do not exist.
            //
            mockPublicPolicyResolver.Verify(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()), Times.Never());
            mockTrustPolicyResolver.Verify(r => r.GetOutgoingPolicy(It.IsAny<MailAddress>()), Times.Never());
        }

        [Fact]
        public void TestFilterCertificateByPolicy_incomingPolicyCompliant_assertNoCertsFiltered()
        {
            string configPath = GetSettingsPath("TestSmtpAgentConfig.xml");
            SmtpAgentSettings settings = SmtpAgentSettings.LoadSettings(configPath);

            CleanMessages(settings);
            CleanMonitor();

            SetPolicyTestSettings(settings);

            //
            // Mock all policy resolvers
            //
            Mock<PrivatePolicyResolver> mockPrivatePolicyResolver;
            Mock<PublicPolicyResolver> mockPublicPolicyResolver;
            Mock<TrustPolicyResolver> mockTrustPolicyResolver;
            Mock<IPolicyFilter> mockPolicyFilter;
            MockPrivatePolicyResolver(settings, out mockPrivatePolicyResolver, out mockPublicPolicyResolver, out mockTrustPolicyResolver, out mockPolicyFilter);
            
            Mock<IPolicyExpression> policyExpression = new Mock<IPolicyExpression>();

            mockPublicPolicyResolver.Setup(r => r.GetOutgoingPolicy(It.IsAny<MailAddress>()))
               .Returns(new List<IPolicyExpression>() { policyExpression.Object });

            mockPrivatePolicyResolver.Setup(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()))
                .Returns(new List<IPolicyExpression>() { policyExpression.Object });
            mockPolicyFilter.Setup(r => r.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()))
                .Returns(true);

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

            mockPublicPolicyResolver.Verify(r => r.GetOutgoingPolicy(new MailAddress("biff@nhind.hsgincubator.com"))
                , Times.Exactly(1));
            mockPublicPolicyResolver.Verify(r => r.GetOutgoingPolicy(new MailAddress("bob@nhind.hsgincubator.com"))
                , Times.Exactly(1));

            mockPrivatePolicyResolver.Verify(r => r.GetIncomingPolicy(new MailAddress("biff@nhind.hsgincubator.com"))
                , Times.Exactly(1));
            mockPrivatePolicyResolver.Verify(r => r.GetIncomingPolicy(new MailAddress("bob@nhind.hsgincubator.com"))
                , Times.Exactly(1));

           mockPrivatePolicyResolver.Verify(r => r.GetOutgoingPolicy(new MailAddress("toby@redmond.hsgincubator.com"))
               , Times.Exactly(1));

           mockTrustPolicyResolver.Verify(r => r.GetIncomingPolicy(new MailAddress("biff@nhind.hsgincubator.com"))
              , Times.Exactly(1));

           mockTrustPolicyResolver.Verify(r => r.GetIncomingPolicy(new MailAddress("bob@nhind.hsgincubator.com"))
               , Times.Exactly(1));

            mockPolicyFilter.Verify(p => p.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()), Times.Exactly(6));
            //
            // These two are never called.  These code paths do not exist.
            //
            mockPublicPolicyResolver.Verify(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()), Times.Never());
            mockTrustPolicyResolver.Verify(r => r.GetOutgoingPolicy(It.IsAny<MailAddress>()), Times.Never());
        }

        [Fact]
        public void TestFilterCertificateByPolicy_outgoingPolicyCompliant_assertNoCertsFiltered()
        {
            string configPath = GetSettingsPath("TestSmtpAgentConfig.xml");
            SmtpAgentSettings settings = SmtpAgentSettings.LoadSettings(configPath);

            CleanMessages(settings);
            CleanMonitor();

            SetPolicyTestSettings(settings);

            //
            // Mock all policy resolvers
            //
            Mock<PrivatePolicyResolver> mockPrivatePolicyResolver;
            Mock<PublicPolicyResolver> mockPublicPolicyResolver;
            Mock<TrustPolicyResolver> mockTrustPolicyResolver;
            Mock<IPolicyFilter> mockPolicyFilter;
            MockPrivatePolicyResolver(settings, out mockPrivatePolicyResolver, out mockPublicPolicyResolver, out mockTrustPolicyResolver, out mockPolicyFilter);

            Mock<IPolicyExpression> policyExpression = new Mock<IPolicyExpression>();
            mockPrivatePolicyResolver.Setup(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()))
                .Returns(new List<IPolicyExpression>() { policyExpression.Object });
            mockPolicyFilter.Setup(r => r.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()))
                .Returns(true);

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

            mockPublicPolicyResolver.Verify(r => r.GetOutgoingPolicy(new MailAddress("biff@nhind.hsgincubator.com"))
                , Times.Exactly(1));
            mockPublicPolicyResolver.Verify(r => r.GetOutgoingPolicy(new MailAddress("bob@nhind.hsgincubator.com"))
                , Times.Exactly(1));
            mockPolicyFilter.Verify(p => p.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()), Times.Exactly(2));
            //
            // These two are never called.  These code paths do not exist.
            //
            mockPublicPolicyResolver.Verify(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()), Times.Never());
            mockTrustPolicyResolver.Verify(r => r.GetOutgoingPolicy(It.IsAny<MailAddress>()), Times.Never());
        }


        [Fact]
        public void TestFilterCertificateByPolicy_notCompliant_assertNoCertsFiltered() 
        {
            string configPath = GetSettingsPath("TestSmtpAgentConfig.xml");
            SmtpAgentSettings settings = SmtpAgentSettings.LoadSettings(configPath);

            CleanMessages(settings);
            CleanMonitor();

            SetPolicyTestSettings(settings);

            //
            // Mock all policy resolvers
            //
            Mock<PrivatePolicyResolver> mockPrivatePolicyResolver;
            Mock<PublicPolicyResolver> mockPublicPolicyResolver;
            Mock<TrustPolicyResolver> mockTrustPolicyResolver;
            Mock<IPolicyFilter> mockPolicyFilter;
            MockPrivatePolicyResolver(settings, out mockPrivatePolicyResolver, out mockPublicPolicyResolver, out mockTrustPolicyResolver, out mockPolicyFilter);

            Mock<IPolicyExpression> policyExpression = new Mock<IPolicyExpression>();
            mockPrivatePolicyResolver.Setup(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()))
                .Returns(new List<IPolicyExpression>() { policyExpression.Object });
            mockPolicyFilter.Setup(r => r.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()))
                .Returns(false);

            SmtpAgent smtpAgent = SmtpAgentFactory.Create(settings);


            //
            // Process loopback messages.  Leaves message in bad message folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            var sendingMessage = LoadMessage(TestMessage);
            Assert.Throws<AgentException>(() => RunEndToEndTest(sendingMessage, smtpAgent));
            
            Assert.Equal(1, BadMessages().Count());


            mockPrivatePolicyResolver.Verify(r => r.GetOutgoingPolicy(new MailAddress("toby@redmond.hsgincubator.com"))
                , Times.Exactly(1));
            mockPolicyFilter.Verify(p => p.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()), Times.Exactly(1));
            //
            // These two are never called.  These code paths do not exist.
            //
            mockPublicPolicyResolver.Verify(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()), Times.Never());
            mockTrustPolicyResolver.Verify(r => r.GetOutgoingPolicy(It.IsAny<MailAddress>()), Times.Never());
        }


        [Fact]
        public void TestFilterCertificateByPolicy_requiredFieldMissing_assertNoCertsFiltered()
        {
            string configPath = GetSettingsPath("TestSmtpAgentConfig.xml");
            SmtpAgentSettings settings = SmtpAgentSettings.LoadSettings(configPath);

            CleanMessages(settings);
            CleanMonitor();

            SetPolicyTestSettings(settings);

            //
            // Mock all policy resolvers
            //
            Mock<PrivatePolicyResolver> mockPrivatePolicyResolver;
            Mock<PublicPolicyResolver> mockPublicPolicyResolver;
            Mock<TrustPolicyResolver> mockTrustPolicyResolver;
            Mock<IPolicyFilter> mockPolicyFilter;
            MockPrivatePolicyResolver(settings, out mockPrivatePolicyResolver, out mockPublicPolicyResolver, out mockTrustPolicyResolver, out mockPolicyFilter);

            Mock<IPolicyExpression> policyExpression = new Mock<IPolicyExpression>();
            mockPrivatePolicyResolver.Setup(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()))
                .Returns(new List<IPolicyExpression>() { policyExpression.Object });
            mockPolicyFilter.Setup(r => r.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()))
                .Throws<PolicyRequiredException>().Verifiable("Policy Required");
           ;

            SmtpAgent smtpAgent = SmtpAgentFactory.Create(settings);


            //
            // Process loopback messages.  Leaves message in bad message folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            var sendingMessage = LoadMessage(TestMessage);
            Assert.Throws<AgentException>(() => RunEndToEndTest(sendingMessage, smtpAgent));

            Assert.Equal(1, BadMessages().Count());


            mockPrivatePolicyResolver.Verify(r => r.GetOutgoingPolicy(new MailAddress("toby@redmond.hsgincubator.com"))
                , Times.Exactly(1));
            
            mockPolicyFilter.Verify(c => c.IsCompliant(It.IsAny<X509Certificate2>(),It.IsAny<IPolicyExpression>()), Times.Once);

            //
            // These two are never called.  These code paths do not exist.
            //
            mockPublicPolicyResolver.Verify(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()), Times.Never());
            mockTrustPolicyResolver.Verify(r => r.GetOutgoingPolicy(It.IsAny<MailAddress>()), Times.Never());
        }


        [Fact]
        public void TestFilterCertificateByPolicy_badPoolicyExpression_assertNoCertsFiltered()
        {
            string configPath = GetSettingsPath("TestSmtpAgentConfig.xml");
            SmtpAgentSettings settings = SmtpAgentSettings.LoadSettings(configPath);

            CleanMessages(settings);
            CleanMonitor();

            SetPolicyTestSettings(settings);

            //
            // Mock all policy resolvers
            //
            Mock<PrivatePolicyResolver> mockPrivatePolicyResolver;
            Mock<PublicPolicyResolver> mockPublicPolicyResolver;
            Mock<TrustPolicyResolver> mockTrustPolicyResolver;
            Mock<IPolicyFilter> mockPolicyFilter;
            MockPrivatePolicyResolver(settings, out mockPrivatePolicyResolver, out mockPublicPolicyResolver, out mockTrustPolicyResolver, out mockPolicyFilter);

            Mock<IPolicyExpression> policyExpression = new Mock<IPolicyExpression>();
            mockPrivatePolicyResolver.Setup(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()))
                .Returns(new List<IPolicyExpression>() { policyExpression.Object });
            mockPolicyFilter.Setup(r => r.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()))
                .Throws<PolicyProcessException>().Verifiable("Policy Required");
            ;

            SmtpAgent smtpAgent = SmtpAgentFactory.Create(settings);


            //
            // Process loopback messages.  Leaves message in bad message folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            var sendingMessage = LoadMessage(TestMessage);
            Assert.Throws<AgentException>(() => RunEndToEndTest(sendingMessage, smtpAgent));

            Assert.Equal(1, BadMessages().Count());


            mockPrivatePolicyResolver.Verify(r => r.GetOutgoingPolicy(new MailAddress("toby@redmond.hsgincubator.com"))
                , Times.Exactly(1));

            mockPolicyFilter.Verify(c => c.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()), Times.Once);

            //
            // These two are never called.  These code paths do not exist.
            //
            mockPublicPolicyResolver.Verify(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()), Times.Never());
            mockTrustPolicyResolver.Verify(r => r.GetOutgoingPolicy(It.IsAny<MailAddress>()), Times.Never());
        }


        [Fact]
        public void TestFilterCertificateByPolicy_trust_requiredPolicyThrow_assertNoCertsFiltered()
        {
            string configPath = GetSettingsPath("TestSmtpAgentConfig.xml");
            SmtpAgentSettings settings = SmtpAgentSettings.LoadSettings(configPath);

            CleanMessages(settings);
            CleanMonitor();

            SetPolicyTestSettings(settings);

            //
            // Mock all policy resolvers
            //
            Mock<PrivatePolicyResolver> mockPrivatePolicyResolver;
            Mock<PublicPolicyResolver> mockPublicPolicyResolver;
            Mock<TrustPolicyResolver> mockTrustPolicyResolver;
            Mock<IPolicyFilter> mockPolicyFilter;
            MockPrivatePolicyResolver(settings, out mockPrivatePolicyResolver, out mockPublicPolicyResolver, out mockTrustPolicyResolver, out mockPolicyFilter);

            Mock<IPolicyExpression> policyExpression = new Mock<IPolicyExpression>();
            mockTrustPolicyResolver.Setup(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()))
                .Returns(new List<IPolicyExpression>() { policyExpression.Object });
            mockPolicyFilter.Setup(r => r.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()))
                 .Throws<PolicyRequiredException>();
            ;

            SmtpAgent smtpAgent = SmtpAgentFactory.Create(settings);


            //
            // Process loopback messages.  Leaves message in bad message folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            var sendingMessage = LoadMessage(TestMessage);
            Assert.Throws<AgentException>(() => RunEndToEndTest(sendingMessage, smtpAgent));

            Assert.Equal(1, BadMessages().Count());


            mockPrivatePolicyResolver.Verify(r => r.GetOutgoingPolicy(new MailAddress("toby@redmond.hsgincubator.com"))
                , Times.Exactly(1));

            mockPolicyFilter.Verify(c => c.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()),
                Times.Exactly(2));

            //
            // These two are never called.  These code paths do not exist.
            //
            mockPublicPolicyResolver.Verify(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()), Times.Never());
            mockTrustPolicyResolver.Verify(r => r.GetOutgoingPolicy(It.IsAny<MailAddress>()), Times.Never());
        }



        [Fact]
        public void TestFilterCertificateByPolicy_trust_badPolicyThrow_assertNoCertsFiltered()
        {
            string configPath = GetSettingsPath("TestSmtpAgentConfig.xml");
            SmtpAgentSettings settings = SmtpAgentSettings.LoadSettings(configPath);

            CleanMessages(settings);
            CleanMonitor();

            SetPolicyTestSettings(settings);

            //
            // Mock all policy resolvers
            //
            Mock<PrivatePolicyResolver> mockPrivatePolicyResolver;
            Mock<PublicPolicyResolver> mockPublicPolicyResolver;
            Mock<TrustPolicyResolver> mockTrustPolicyResolver;
            Mock<IPolicyFilter> mockPolicyFilter;
            MockPrivatePolicyResolver(settings, out mockPrivatePolicyResolver, out mockPublicPolicyResolver, out mockTrustPolicyResolver, out mockPolicyFilter);

            Mock<IPolicyExpression> policyExpression = new Mock<IPolicyExpression>();
            mockTrustPolicyResolver.Setup(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()))
                .Returns(new List<IPolicyExpression>() { policyExpression.Object });
            mockPolicyFilter.Setup(r => r.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()))
                 .Throws<PolicyRequiredException>();
            ;

            SmtpAgent smtpAgent = SmtpAgentFactory.Create(settings);


            //
            // Process loopback messages.  Leaves message in bad message folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            var sendingMessage = LoadMessage(TestMessage);
            Assert.Throws<AgentException>(() => RunEndToEndTest(sendingMessage, smtpAgent));

            Assert.Equal(1, BadMessages().Count());


            mockPrivatePolicyResolver.Verify(r => r.GetOutgoingPolicy(new MailAddress("toby@redmond.hsgincubator.com"))
                , Times.Exactly(1));

            mockPolicyFilter.Verify(c => c.IsCompliant(It.IsAny<X509Certificate2>(), It.IsAny<IPolicyExpression>()),
                Times.Exactly(2));

            //
            // These two are never called.  These code paths do not exist.
            //
            mockPublicPolicyResolver.Verify(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()), Times.Never());
            mockTrustPolicyResolver.Verify(r => r.GetOutgoingPolicy(It.IsAny<MailAddress>()), Times.Never());
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

        private void SetPolicyTestSettings(SmtpAgentSettings settings)
        {
            settings.InternalMessage.EnableRelay = true;
            settings.Notifications.AutoResponse = true;
            settings.Notifications.AlwaysAck = true;
            settings.Notifications.AutoDsnFailureCreation =
                NotificationSettings.AutoDsnOption.Always.ToString();

            MdnMemoryStore.Clear();
            Mock<ClientSettings> mockMdnClientSettings = MockMdnClientSettings();
            settings.MdnMonitor = mockMdnClientSettings.Object;
        }

        //
        // Mock all policy resolver
        //
        protected static void MockPrivatePolicyResolver(SmtpAgentSettings settings, out Mock<PrivatePolicyResolver> mockPrivatePolicyResolver,
            out Mock<PublicPolicyResolver> mockPublicPolicyResolver, out Mock<TrustPolicyResolver> mockTrustPolicyResolver, out Mock<IPolicyFilter> mockPolicyFilter)
        {
            
            PolicyResolverSettings privatePolicySettings =
                settings.CertPolicies.Resolvers.First(r => r.Name == CertPolicyResolvers.PrivatePolicyName);
            Assert.NotNull(privatePolicySettings);
            settings.CertPolicies.Resolvers.Remove(privatePolicySettings);
            PolicyResolverSettings publicPolicySettings =
                settings.CertPolicies.Resolvers.First(r => r.Name == CertPolicyResolvers.PublicPolicyName);
            Assert.NotNull(publicPolicySettings);
            settings.CertPolicies.Resolvers.Remove(publicPolicySettings);
            PolicyResolverSettings trustPolicySettings =
                settings.CertPolicies.Resolvers.First(r => r.Name == CertPolicyResolvers.TrustPolicyName);
            Assert.NotNull(privatePolicySettings);
            settings.CertPolicies.Resolvers.Remove(trustPolicySettings);

            mockPrivatePolicyResolver = new Mock<PrivatePolicyResolver>();
            Mock<PolicyResolverSettings> mockPrivateResolverSettings = new Mock<PolicyResolverSettings>().SetupAllProperties();
            mockPrivateResolverSettings.Setup(s => s.CreateResolver()).Returns(mockPrivatePolicyResolver.Object);
            mockPrivateResolverSettings.Setup(s => s.Name).Returns(CertPolicyResolvers.PrivatePolicyName);

            mockPublicPolicyResolver = new Mock<PublicPolicyResolver>();
            Mock<PolicyResolverSettings> mockPublicResolverSettings = new Mock<PolicyResolverSettings>().SetupAllProperties();
            mockPublicResolverSettings.Setup(s => s.CreateResolver()).Returns(mockPublicPolicyResolver.Object);
            mockPublicResolverSettings.Setup(s => s.Name).Returns(CertPolicyResolvers.PublicPolicyName);

            mockTrustPolicyResolver = new Mock<TrustPolicyResolver>();
            Mock<PolicyResolverSettings> mockTrustResolverSettings = new Mock<PolicyResolverSettings>().SetupAllProperties();
            mockTrustResolverSettings.Setup(s => s.CreateResolver()).Returns(mockTrustPolicyResolver.Object);
            mockTrustResolverSettings.Setup(s => s.Name).Returns(CertPolicyResolvers.TrustPolicyName);

            settings.CertPolicies.Resolvers.Add(mockPrivateResolverSettings.Object);
            settings.CertPolicies.Resolvers.Add(mockPublicResolverSettings.Object);
            settings.CertPolicies.Resolvers.Add(mockTrustResolverSettings.Object);

            mockPrivatePolicyResolver.Setup(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()))
                .Returns(new List<IPolicyExpression>());
            mockPrivatePolicyResolver.Setup(r => r.GetOutgoingPolicy(It.IsAny<MailAddress>()))
                .Returns(new List<IPolicyExpression>());

            mockPublicPolicyResolver.Setup(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()))
                .Returns(new List<IPolicyExpression>());
            mockPublicPolicyResolver.Setup(r => r.GetOutgoingPolicy(It.IsAny<MailAddress>()))
                .Returns(new List<IPolicyExpression>());

            mockTrustPolicyResolver.Setup(r => r.GetIncomingPolicy(It.IsAny<MailAddress>()))
                .Returns(new List<IPolicyExpression>());
            mockTrustPolicyResolver.Setup(r => r.GetOutgoingPolicy(It.IsAny<MailAddress>()))
                .Returns(new List<IPolicyExpression>());

            mockPolicyFilter = new Mock<IPolicyFilter>();
            mockPolicyFilter.SetupAllProperties();
            PolicyFilter.Default = mockPolicyFilter.Object;
        }
    }
}
