/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Joe Shook	    jshook@kryptiq.com
   
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
using System.Xml;
using System.Xml.Serialization;
using Health.Direct.Agent;
using Health.Direct.Agent.Tests;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Common.Mime;
using Health.Direct.Config.Client;
using Health.Direct.Config.Store;
using Xunit;

namespace Health.Direct.SmtpAgent.Tests
{
    public class TestSmtpAgent : SmtpAgentTester
    {
        SmtpAgent m_agent;
        

        static TestSmtpAgent()
        {
            AgentTester.EnsureStandardMachineStores();        
        }

        public TestSmtpAgent()
        {
            //m_agent = SmtpAgentFactory.Create(base.GetSettingsPath("TestSmtpAgentConfigService.xml"));
            //m_agent = SmtpAgentFactory.Create(base.GetSettingsPath("TestSmtpAgentConfigServiceProd.xml"));
            m_agent = SmtpAgentFactory.Create(base.GetSettingsPath("TestSmtpAgentConfig.xml"));
        }

        [Fact]
        public void Test()
        {
            Assert.DoesNotThrow(() => m_agent.ProcessMessage(this.LoadMessage(string.Format(TestMessage, Guid.NewGuid()))));
            Assert.Throws<OutgoingAgentException>(() => m_agent.ProcessMessage(this.LoadMessage(BadMessage)));
        }
        
        [Fact]
        public void TestAddressDomainEnabled_Settings()
        {
            SmtpAgent agent = SmtpAgentFactory.Create(GetSettingsPath("TestSmtpAgentConfigService.xml"));
            Assert.True(agent.Settings.AddressManager.HasSettings);
            using (XmlNodeReader reader = new XmlNodeReader(agent.Settings.AddressManager.Settings))
            {
                XmlSerializer serializer = new XmlSerializer(typeof(AddressManagerSettings), new XmlRootAttribute(agent.Settings.AddressManager.Settings.LocalName));
                AddressManagerSettings addressManagerSettings = (AddressManagerSettings)serializer.Deserialize(reader);
                Assert.NotNull(addressManagerSettings);
                Assert.True(addressManagerSettings.EnableDomainSearch);
            }

        }

        [Fact]
        public void TestEndToEnd()
        {
            CleanMessages(m_agent.Settings);
            m_agent.Settings.InternalMessage.EnableRelay = true;
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(string.Format(TestMessage, Guid.NewGuid()))));
                        
            m_agent.Settings.InternalMessage.EnableRelay = false;
            Assert.Throws<SmtpAgentException>(() => RunEndToEndTest(this.LoadMessage(string.Format(TestMessage, Guid.NewGuid()))));
        }

        [Fact]
        public void TestEndToEndInternalMessage()
        {
            m_agent.Settings.InternalMessage.EnableRelay = true;
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(string.Format(TestMessage, Guid.NewGuid()))));
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(CrossDomainMessage)));
            m_agent.Settings.InternalMessage.EnableRelay = false;
        }

        
        [Fact (Skip="Need Config Service to run this")]
        public void TestEndToEndBad()
        {
            m_agent.Settings.InternalMessage.EnableRelay = true;
            Assert.Throws<AgentException>(() => RunEndToEndTest(this.LoadMessage(UnknownUsersMessage)));
            m_agent.Settings.InternalMessage.EnableRelay = false;
        }
        
        [Fact]
        public void TestEndToEndCrossDomain()
        {
            m_agent.Settings.InternalMessage.EnableRelay = true;
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(CrossDomainMessage)));            
        }

        CDO.Message RunEndToEndTest(CDO.Message message)
        {
            m_agent.ProcessMessage(message);            
            message = this.LoadMessage(message);
            base.VerifyOutgoingMessage(message);
            
            m_agent.ProcessMessage(message);
            message = this.LoadMessage(message);
            
            if (m_agent.Settings.InternalMessage.EnableRelay)
            {
                base.VerifyIncomingMessage(message);
            }
            else
            {
                base.VerifyOutgoingMessage(message);
            }
            return message;
        }
               
       

        [Fact]
        public void TestUntrusted()
        {
            //
            // This should be accepted because the envelope is what we look at
            //
            MessageEnvelope envelope = new MessageEnvelope(BadMessage, 
                                                           DirectAddressCollection.ParseSmtpServerEnvelope("biff@nhind.hsgincubator.com"),
                                                           new DirectAddress("toby@redmond.hsgincubator.com")
                );
           
            Assert.DoesNotThrow(() => m_agent.SecurityAgent.ProcessOutgoing(envelope));

            envelope = new MessageEnvelope(string.Format(TestMessage, Guid.NewGuid()),
                                           DirectAddressCollection.ParseSmtpServerEnvelope("xyz@untrusted.com"),
                                           new DirectAddress("toby@redmond.hsgincubator.com"));

            //
            // This SHOULD throw an exception
            //
            Assert.Throws<OutgoingAgentException>(() => m_agent.SecurityAgent.ProcessOutgoing(envelope));
        }

        public const string MaxRecipientsTestMessage =
            @"From: <toby@redmond.hsgincubator.com>
To: <biff@nhind.hsgincubator.com>, <bob@nhind.hsgincubator.com>, <a@nhind.hsgincubator.com>, <b@nhind.hsgincubator.com>
CC: <jim@nhind.hsgincubator.com>
Subject: Simple Text Message
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
X-something:
Content-Type: text/plain

Yo. Wassup?";
        [Fact]
        public void TestMaxRecipients()
        {
            OutgoingMessage outgoing = m_agent.SecurityAgent.ProcessOutgoing(MaxRecipientsTestMessage);

            m_agent.Settings.MaxIncomingDomainRecipients = 3;
            Assert.Throws<AgentException>(() => m_agent.SecurityAgent.ProcessIncoming(outgoing));

            m_agent.Settings.MaxIncomingDomainRecipients = 4;
            Assert.Throws<AgentException>(() => m_agent.SecurityAgent.ProcessIncoming(outgoing));

            m_agent.Settings.MaxIncomingDomainRecipients = 5;
            Assert.DoesNotThrow(() => m_agent.SecurityAgent.ProcessIncoming(outgoing));
        }
        
        public const string InternalRelayMessage =
        @"To: toby@redmond.hsgincubator.com
From: toby@redmond.hsgincubator.com
MIME-Version: 1.0
Subject: Simple Text Message
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?";
        
        [Fact]
        public void InternalRelayFail()
        {
            m_agent.Settings.InternalMessage.EnableRelay = false;
            SmtpAgentError error = SmtpAgentError.Unknown;
            try
            {
                this.RunEndToEndTest(this.LoadMessage(InternalRelayMessage));
            }
            catch(SmtpAgentException ex)
            {
                error = ex.Error;
            }
            
            Assert.Equal(SmtpAgentError.InternalRelayDisabled, error);
        }

        [Fact]
        public void InternalRelaySuccess()
        {
            m_agent.Settings.InternalMessage.EnableRelay = true;
            Assert.DoesNotThrow(() => this.RunEndToEndTest(this.LoadMessage(InternalRelayMessage)));
        }
    }
}