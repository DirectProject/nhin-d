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
using System.IO;
using Health.Direct.Agent;
using Health.Direct.Agent.Tests;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Common.Mime;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.MonitorService;
using Health.Direct.Config.Store;
using Xunit;
using Message = CDO.Message;

namespace Health.Direct.SmtpAgent.Tests
{
    public class TestSmtpAgent : SmtpAgentTester
    {
        SmtpAgent m_agent;
        readonly NotificationProducer m_producer;

        static TestSmtpAgent()
        {
            AgentTester.EnsureStandardMachineStores();        
        }
        
        public TestSmtpAgent()
        {
            //m_agent = SmtpAgentFactory.Create(base.GetSettingsPath("TestSmtpAgentConfigService.xml"));
            //m_agent = SmtpAgentFactory.Create(base.GetSettingsPath("TestSmtpAgentConfigServiceProd.xml"));
            m_agent = SmtpAgentFactory.Create(base.GetSettingsPath("TestSmtpAgentConfig.xml"));

            m_producer = new NotificationProducer(m_agent.Settings.Notifications);
        }
        
        [Fact]
        public void Test()
        {
            Assert.DoesNotThrow(() => m_agent.ProcessMessage(this.LoadMessage(TestMessage)));
            Assert.Throws<AgentException>(() => m_agent.ProcessMessage(this.LoadMessage(BadMessage)));
        }
        
        [Fact]
        public void TestEndToEnd()
        {
            CleanMessages(m_agent.Settings);
            m_agent.Settings.InternalMessage.EnableRelay = true;
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(TestMessage)));
                        
            m_agent.Settings.InternalMessage.EnableRelay = false;
            Assert.Throws<SmtpAgentException>(() => RunEndToEndTest(this.LoadMessage(TestMessage)));
        }

        [Fact]
        public void TestEndToEndInternalMessage()
        {
            m_agent.Settings.InternalMessage.EnableRelay = true;
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(TestMessage)));
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(CrossDomainMessage)));
            m_agent.Settings.InternalMessage.EnableRelay = false;
        }

        //
        // Ensure Mdns start process and dispatch
        // Requesting delivery notification.
        // X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true
        // This will be test better in the MdnMonitor code.
        [Fact]
        public void TestEndToEndStartMdnMonitor()
        {
            CleanMessages(m_agent.Settings);
            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = true;
            m_agent.Settings.Notifications.AlwaysAck = true;
            m_agent.Settings.MdnMonitor = new ClientSettings();
            m_agent.Settings.MdnMonitor.Url = "http://localhost:6692/MonitorService.svc/Dispositions";
            
            //
            // Process loopback messages.  Leaves un-encrypted mdns in pickup folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            var sendingMessage = LoadMessage(TestMessage);
            Assert.DoesNotThrow(() => RunEndToEndTest(sendingMessage));
            
            //
            // grab the clear text mdns and delete others.
            //
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                if (messageText.Contains("disposition-notification"))
                {
                    Assert.DoesNotThrow(() => RunMdnOutBoundProcessingTest(this.LoadMessage(messageText)));
                }
            }

            //
            // Now the messages are encrypted and can be handled
            // Processed Mdn's will be recorded by the MdnMonitorService
            //
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                CDO.Message message = LoadMessage(messageText);
                Assert.DoesNotThrow(() => RunMdnInBoundProcessingTest(message));

                //
                // This is what we are realy testing...
                //
                TestMdnsInProcessedStatus(message, false);
            }

            m_agent.Settings.InternalMessage.EnableRelay = false;
        }


        //
        // Ensure Mdns start process and dispatch
        // Requesting delivery notification.
        // X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true
        // This will be test better in the MdnMonitor code.
        [Fact]
        public void TestEndToEndTimelyAndReliableStartMdnMonitor()
        {
            CleanMessages(m_agent.Settings);
            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = true;
            m_agent.Settings.Notifications.AlwaysAck = true;
            m_agent.Settings.MdnMonitor = new ClientSettings();
            m_agent.Settings.MdnMonitor.Url = "http://localhost:6692/MonitorService.svc/Dispositions";

            //
            // Process loopback messages.  Leaves un-encrypted mdns in pickup folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.

            string textMessage = string.Format(TestMessageTimelyAndReliable, Guid.NewGuid());
            var sendingMessage = LoadMessage(textMessage);
            Assert.DoesNotThrow(() => RunEndToEndTest(sendingMessage));

            //
            // grab the clear text mdns and delete others.
            //
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                if (messageText.Contains("disposition-notification"))
                {
                    Assert.DoesNotThrow(() => RunMdnOutBoundProcessingTest(this.LoadMessage(messageText)));
                }
            }

            //
            // Now the messages are encrypted and can be handled
            // Processed Mdn's will be recorded by the MdnMonitorService
            //
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                CDO.Message message = LoadMessage(messageText);
                Assert.DoesNotThrow(() => RunMdnInBoundProcessingTest(message));

                //
                // This is what we are realy testing...
                //
                TestMdnsInProcessedStatus(message, true);
            }

            //
            // Prepare a Dispatched MDN
            //
            var incoming = new IncomingMessage(textMessage);

            var notificationMessages = incoming.Message.CreateNotificationMessages(
                incoming.Recipients.AsMailAddresses(),
                sender => Notification.CreateAck
                    (
                        new ReportingUserAgent
                            (
                                sender.Host
                                , m_agent.Settings.Notifications.ProductName
                            )
                            , m_agent.Settings.Notifications.Text
                            , MDNStandard.NotificationType.Dispatched)
                 );

            //
            // Simulating a destination client sending a dispatched MDN
            //
            foreach (var notification in notificationMessages)
            {
                var dispatchText = MimeSerializer.Default.Serialize(notification);
                CDO.Message message = LoadMessage(dispatchText);
                message = RunEndToEndTest(message);
                TestMdnsInDispatchedStatus(message);
            }

            m_agent.Settings.InternalMessage.EnableRelay = false;
        }

        //
        // Ensure Mdns start process and dispatch
        // Requesting delivery notification.
        // X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true
        // Time outs witll be tested in the MdnMonitor code.
        // The GatewayIsDestination flag is set, thus the gateway
        // will send a dispatched notification rather than waiting for a 
        // destination client.
        [Fact]
        public void TestEndToEndTimelyAndReliableAtGatewayStartMdnMonitor()
        {
            CleanMessages(m_agent.Settings);
            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = true;
            m_agent.Settings.Notifications.AlwaysAck = true;
            m_agent.Settings.Notifications.GatewayIsDestination = true;
            m_agent.Settings.MdnMonitor = new ClientSettings();
            m_agent.Settings.MdnMonitor.Url = "http://localhost:6692/MonitorService.svc/Dispositions";

            //
            // Process loopback messages.  Leaves un-encrypted mdns in pickup folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            string textMessage = string.Format(TestMessageTimelyAndReliable, Guid.NewGuid());
            var sendingMessage = LoadMessage(textMessage);
            Assert.DoesNotThrow(() => RunEndToEndTest(sendingMessage));

            //
            // grab the clear text mdns and delete others.
            //
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                if (messageText.Contains("disposition-notification"))
                {
                    Assert.DoesNotThrow(() => RunMdnOutBoundProcessingTest(this.LoadMessage(messageText)));
                }
            }

            //
            // Now the messages are encrypted and can be handled
            // Processed Mdn's will be recorded by the MdnMonitorService
            //
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                CDO.Message message = LoadMessage(messageText);
                Assert.DoesNotThrow(() => RunMdnInBoundProcessingTest(message));
            }
             //Test Mdn data
            var messageEnvelope = new CDOSmtpMessage(sendingMessage).GetEnvelope();
            foreach (var recipient in messageEnvelope.Recipients)
            {
                var queryMdn = new Mdn(messageEnvelope.Message.IDValue
                        , recipient.Address
                        , messageEnvelope.Message.FromValue);

                var mdnManager = CreateConfigStore().Mdns;
                var mdn = mdnManager.Get(queryMdn.MdnIdentifier);
                Assert.NotNull(mdn);
                Assert.Equal("dispatched", mdn.Status, StringComparer.OrdinalIgnoreCase);
                Assert.Equal(true, mdn.NotifyDispatched);
                Assert.NotNull(mdn.MdnProcessedDate);
            } 

            m_agent.Settings.InternalMessage.EnableRelay = false;
        }

        [Fact(Skip="Need Config Service to run this")]
        //[Fact]
        public void TestEndToEndBad()
        {
            Assert.Throws<AgentException>(() => RunEndToEndTest(this.LoadMessage(UnknownUsersMessage)));
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

        void RunMdnOutBoundProcessingTest(CDO.Message message)
        {
            VerifyMdnIncomingMessage(message);      //Plain Text
            m_agent.ProcessMessage(message);        //Encrypts
            base.VerifyOutgoingMessage(message);    //Mdn looped back
        }

        void RunMdnInBoundProcessingTest(CDO.Message message)
        {
            VerifyOutgoingMessage(message);         //Encryted Message
            m_agent.ProcessMessage(message);        //Decrypts Message
            base.VerifyMdnIncomingMessage(message);    //Mdn looped back
        }

        static void TestMdnsInProcessedStatus(CDO.Message message, bool timelyAndReliable)
        {
            var queryMdn = BuildQueryMdn(message);

            var mdnManager = CreateConfigStore().Mdns;
            var mdn = mdnManager.Get(queryMdn.MdnIdentifier);
            Assert.NotNull(mdn);
            Assert.Equal("processed", mdn.Status, StringComparer.OrdinalIgnoreCase);
            Assert.NotNull(mdn.MdnProcessedDate);
            Assert.Equal(timelyAndReliable, mdn.NotifyDispatched);
        }

        static void TestMdnsInDispatchedStatus(CDO.Message message)
        {
            var queryMdn = BuildQueryMdn(message);

            var mdnManager = CreateConfigStore().Mdns;
            var mdn = mdnManager.Get(queryMdn.MdnIdentifier);
            Assert.NotNull(mdn);
            Assert.Equal("dispatched", mdn.Status, StringComparer.OrdinalIgnoreCase);
            Assert.Equal(true, mdn.NotifyDispatched);
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

            envelope = new MessageEnvelope(TestMessage,
                                           DirectAddressCollection.ParseSmtpServerEnvelope("xyz@untrusted.com"),
                                           new DirectAddress("toby@redmond.hsgincubator.com"));

            //
            // This SHOULD throw an exception
            //
            Assert.Throws<AgentException>(() => m_agent.SecurityAgent.ProcessOutgoing(envelope));
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
        @"To: toby@nhind.hsgincubator.com
From: toby@nhind.hsgincubator.com
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