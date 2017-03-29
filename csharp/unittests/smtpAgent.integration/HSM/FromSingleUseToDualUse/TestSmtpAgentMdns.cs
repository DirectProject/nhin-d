using System;
using System.IO;
using Health.Direct.Agent.Tests;
using Health.Direct.Common.Mail.Notifications;
using Xunit;

namespace Health.Direct.SmtpAgent.Integration.Tests.HSM.FromSingleUseToDualUse
{
    public class TestSmtpAgentMdns : SmtpAgentTester
    {
        SmtpAgent m_agent;


        static TestSmtpAgentMdns()
        {
            AgentTester.EnsureStandardMachineStores();
        }

        public TestSmtpAgentMdns()
        {
            m_agent = SmtpAgentFactory.Create(GetSettingsPath("TestSmtpAgentConfigService_HsmCryptographer.xml"));
        }

        [Fact]
        public void TestEndToEnd_GatewayIsDestination_Is_True_And_TimelyAndReliable_Not_Requestd()
        {
            CleanMessages(m_agent.Settings);
            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Outgoing.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = true;
            m_agent.Settings.Notifications.AlwaysAck = true;
            m_agent.Settings.Notifications.GatewayIsDestination = true;

            //
            // Process loopback messages.  Leaves un-encrypted mdns in pickup folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            string textMessage = string.Format(string.Format(TestMessageHsmToSoft, Guid.NewGuid()), Guid.NewGuid());
            var sendingMessage = LoadMessage(textMessage);
            RunEndToEndTest(sendingMessage, m_agent);

            //
            // grab the clear text mdns and delete others.
            //
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                if (messageText.Contains("disposition-notification"))
                {
                    RunMdnOutBoundProcessingTest(LoadMessage(messageText), m_agent);
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

                RunMdnInBoundProcessingTest(message, m_agent);
                var envelope = new CDOSmtpMessage(message).GetEnvelope();
                var mdn = MDNParser.Parse(envelope.Message);

                //
                // Only expect processed MDNs
                //
                Assert.Equal(MDNStandard.NotificationType.Processed, mdn.Disposition.Notification);
                TestMdnTimelyAndReliableExtensionField(mdn, false);
            }

            m_agent.Settings.InternalMessage.EnableRelay = false;
        }
    }
}
