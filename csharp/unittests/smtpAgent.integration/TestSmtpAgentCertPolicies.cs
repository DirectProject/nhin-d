using System.IO;
using Xunit;

namespace Health.Direct.SmtpAgent.Integration.Tests
{
    public class TestSmtpAgentCertPolicies : SmtpAgentTester
    {
        [Fact]
        public void TestFilterCertificateByPolicy_nullResolver_assertNoCertsFiltered()
        {
            SmtpAgent m_agent = SmtpAgentFactory.Create(GetSettingsPath("TestSmtpAgentConfigWithCertPolicy.xml"));
            m_agent.Settings.InternalMessage.EnableRelay = true;
            CleanMessages(m_agent.Settings);
            CleanMonitor();

            //
            // Process loopback messages.  Leaves un-encrypted mdns in pickup folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            var sendingMessage = LoadMessage(TestMessage);
            Assert.Null(Record.Exception(() => RunEndToEndTest(sendingMessage, m_agent)));

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
                    Assert.Null(Record.Exception(() => RunMdnOutBoundProcessingTest(LoadMessage(messageText), m_agent)));
                }
            }
            Assert.True(foundMdns);
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
            VerifyMdnIncomingMessage(message);  //Plain Text
            agent.ProcessMessage(message);      //Encrypts
            VerifyOutgoingMessage(message);     //Mdn looped back
        }
    }
}
