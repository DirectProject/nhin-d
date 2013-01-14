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
using System.Linq;
using System.IO;

using Health.Direct.Agent;
using Health.Direct.Agent.Tests;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Config.Client;
using Xunit;

namespace Health.Direct.SmtpAgent.Tests
{
    public class TestNotifications : SmtpAgentTester
    {
        readonly SmtpAgent m_agent;
        readonly NotificationProducer m_producer;

        static TestNotifications()
        {
            AgentTester.EnsureStandardMachineStores();
        }
        
        public TestNotifications()
        {
            m_agent = SmtpAgentFactory.Create(MakeFilePath("SmtpAgentTestFiles\\TestSmtpAgentConfig.xml"));
            m_producer = new NotificationProducer(m_agent.Settings.Notifications);
        }
        
        [Fact]
        public void TestBasic()
        {
            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = true;

            Message msg = Message.Load(string.Format(TestMessage, Guid.NewGuid()));
            msg.RequestNotification();
            
            OutgoingMessage outgoing = null;
            IncomingMessage incoming = null;                        
            base.ProcessEndToEnd(m_agent, msg, out outgoing, out incoming);
             
            int i = 0;            
            foreach(NotificationMessage notification in m_producer.Produce(incoming))
            {
                DirectAddress sender = incoming.DomainRecipients[i++];
                Assert.Equal(sender.Address, notification.FromValue, MailStandard.Comparer);
            }            
            
            m_agent.Settings.InternalMessage.PickupFolder = Path.GetTempPath();
            Assert.DoesNotThrow(() => m_agent.ProcessMessage(this.LoadMessage(outgoing.SerializeMessage())));
        }

        [Fact]
        public void TestNotifyAlways()
        {
            //
            // Here, the sender does NOT explicitly request an MDN, but we send it anyway
            //
            m_agent.Settings.Notifications.AutoResponse = true;

            Message msg = Message.Load(string.Format(TestMessage, Guid.NewGuid()));
            OutgoingMessage outgoing = null;
            IncomingMessage incoming = null;

            base.ProcessEndToEnd(m_agent, msg, out outgoing, out incoming);

            m_agent.Settings.Notifications.AlwaysAck = true;
            Assert.True(CountNotificationsToBeSent(incoming) > 0);

            base.ProcessEndToEnd(m_agent, msg, out outgoing, out incoming);
            m_agent.Settings.Notifications.AlwaysAck = false;
            Assert.True(CountNotificationsToBeSent(incoming) == 0);
        }
        
        //
        // No MDNS should be produced in these cases
        //
        [Fact]        
        public void TestNoMDNSent()
        {
            Message msg = MailParser.ParseMessage(string.Format(TestMessage, Guid.NewGuid()));
            msg.RequestNotification();

            OutgoingMessage outgoing = null;
            IncomingMessage incoming = null;
            
            base.ProcessEndToEnd(m_agent, msg, out outgoing, out incoming);
            //
            // We have a valid MDN request, but the gateway is configured not to send them
            //
            m_agent.Settings.Notifications.AutoResponse = false;
            Assert.True(CountNotificationsToBeSent(incoming) == 0);           
            //
            // Renable Acks on the gateway
            // Then generate an MDN Ack & pass it through the gateway            
            // After end to end processing, we should receive a valid MDN 
            // The receiving gateway should NOT generate an Ack
            //
            m_agent.Settings.Notifications.AutoResponse = true; // Gateway now enabled to send acks            
            NotificationMessage notificationMessage = m_producer.Produce(incoming).First();                        
            this.ProcessEndToEnd(m_agent, notificationMessage, out outgoing, out incoming);
                        
            Assert.True(incoming.Message.IsMDN());  // Verify that the receiver got a valid MDN
            //
            // The message is itself an MDN Response! Should not be able to send a response
            //            
            Assert.True(CountNotificationsToBeSent(incoming) == 0);
        }
                        
        int CountNotificationsToBeSent(IncomingMessage incoming)
        {
            return m_producer.Produce(incoming).Count();
        }

        [Fact]
        public void TestStartMdnMonitor()
        {
            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = true;
            m_agent.Settings.Notifications.AlwaysAck = true;
            m_agent.Settings.MdnMonitor = new ClientSettings();
            m_agent.Settings.MdnMonitor.Url = "http://localhost:6692/MonitorService.svc/Dispositions";

            Message msg = Message.Load(string.Format(TestMessage, Guid.NewGuid()));
            OutgoingMessage outgoing = null;
            IncomingMessage incoming = null;

            base.ProcessEndToEnd(m_agent, msg, out outgoing, out incoming);
            
            Assert.True(CountNotificationsToBeSent(incoming) > 0);

            //Now send an MDN from receipients.
            foreach (var recipient in incoming.Recipients)
            {
                
            }
        }
    }
}