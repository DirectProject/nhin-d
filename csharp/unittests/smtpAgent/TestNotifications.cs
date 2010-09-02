using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using NHINDirect;
using NHINDirect.Agent;
using NHINDirect.SmtpAgent;
using NHINDirect.Mail;
using NHINDirect.Mail.Notifications;
using AgentTests;
using Xunit;
using Xunit.Extensions;

namespace SmtpAgentTests
{
    public class TestNotifications : SmtpAgentTester
    {
        SmtpAgent m_agent;
        NotificationProducer m_producer;

        static TestNotifications()
        {
            AgentTests.AgentTester.EnsureStandardMachineStores();
        }
        
        public TestNotifications()
        {
            m_agent = new SmtpAgent(SmtpAgentSettings.LoadSettings(MakeFilePath("SmtpAgentTestFiles\\TestSmtpAgentConfig.xml")));
            m_producer = new NotificationProducer(m_agent.Settings.Notifications);
        }
        
        [Fact]
        public void TestBasic()
        {
            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = true;
            
            Message msg = MailParser.ParseMessage(TestMessage);
            msg.RequestNotification();
            
            OutgoingMessage outgoing = null;
            IncomingMessage incoming = null;                        
            base.ProcessEndToEnd(m_agent, msg, out outgoing, out incoming);
             
            int i = 0;            
            foreach(NotificationMessage notification in m_producer.Produce(incoming))
            {
                NHINDAddress sender = incoming.DomainRecipients[i++];
                Assert.True(MailStandard.Equals(sender.Address, notification.FromValue));
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
            int count = 0;
            foreach (NotificationMessage notification in m_producer.Produce(incoming))
            {
                ++count;
            }
            return count;
        }
    }
}
