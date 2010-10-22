/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System.Linq;
using System.IO;

using Health.Direct.Agent.Tests;

using NHINDirect.Agent;
using NHINDirect.Mail;
using NHINDirect.Mail.Notifications;

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
            
            Message msg = Message.Load(TestMessage);
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

        //
        // No MDNS should be produced in these cases
        //
        [Fact]        
        public void TestNoMDNSent()
        {
            Message msg = MailParser.ParseMessage(TestMessage);
            msg.RequestNotification();

            OutgoingMessage outgoing = null;
            IncomingMessage incoming = null;
            
            base.ProcessEndToEnd(m_agent, msg, out outgoing, out incoming);
            //
            // We have a valid MDN request, but the gateway is configured not to send them
            //
            m_agent.Settings.Notifications.AutoResponse = false;
            Assert.True(CountNotifications(incoming) == 0);

            m_agent.Settings.Notifications.AutoResponse = true; // Gateway now enabled to send acks
            //
            // The gateway is enabled to send MDNs but one was not requested
            //
            incoming.Message.Headers.RemoveAt(incoming.Message.Headers.IndexOf(MDNStandard.Headers.DispositionNotificationTo));
            Assert.True(CountNotifications(incoming) == 0);
            
            incoming.Message.RequestNotification();
            NotificationMessage notificationMessage = m_producer.Produce(incoming).First();
                        
            this.ProcessEndToEnd(m_agent, notificationMessage, out outgoing, out incoming);
            
            Assert.True(incoming.Message.IsMDN());            
            //
            // The message is itself an MDN Response! Should not be able to send a response
            //            
            Assert.True(CountNotifications(incoming) == 0);
        }
                        
        int CountNotifications(IncomingMessage incoming)
        {
            return m_producer.Produce(incoming).Count();
        }
    }
}