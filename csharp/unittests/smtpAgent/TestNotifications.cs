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

        public TestNotifications()
        {
            AgentTests.AgentTester.EnsureStandardMachineStores();

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
            
            OutgoingMessage outgoing = (OutgoingMessage) m_agent.SecurityAgent.ProcessOutgoing(new MessageEnvelope(msg));            
            IncomingMessage incoming = (IncomingMessage) m_agent.SecurityAgent.ProcessIncoming(new MessageEnvelope(outgoing.SerializeMessage()));
             
            int i = 0;            
            foreach(NotificationMessage notification in m_producer.Produce(incoming))
            {
                NHINDAddress sender = incoming.DomainRecipients[i++];
                Assert.True(MailStandard.Equals(sender.Address, notification.FromValue));
            }            
            
            m_agent.Settings.InternalMessage.PickupFolder = Path.GetTempPath();
            Assert.DoesNotThrow(() => m_agent.ProcessMessage(this.LoadMessage(outgoing.SerializeMessage())));
            //
            // Nothing should fire in these cases
            //
            m_agent.Settings.Notifications.AutoResponse = false;
            Assert.True(CountNotifications(incoming) == 0);

            m_agent.Settings.Notifications.AutoResponse = true;
            incoming.Message.Headers.RemoveAt(incoming.Message.Headers.IndexOf(MDNStandard.Headers.DispositionNotificationTo));
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
