using System;
using System.IO;
using System.Linq;
using Xunit;

namespace Health.Direct.SmtpAgent.Integration.Tests.HSM
{
    public class FailureTests : SmtpAgentTester
    {
        /// <summary>
        /// This is the Direct Project in a failed configuration.  The HSM cryptographer cannot get it's configuration data.
        /// Bad Url
        /// Outgoing message.
        /// </summary>
        [Fact]
        public void TestUnavailableTokenSettingsOutgoing()
        {
            var agent = SmtpAgentFactory.Create(GetSettingsPath("TestSmtpAgentConfigService_HsmCryptographerBadUrl.xml"));
            var messageId = Guid.NewGuid();
            var message = LoadMessage(string.Format(TestMessageDualToHsm, messageId));
            Assert.Throws<SmtpAgentSettingsException>(() => agent.ProcessMessage(message));

            Assert.True(FileMessages(agent.Settings.BadMessage.CopyFolder)
                .Select(File.ReadAllText)
                .Any(m => m.Contains(messageId.ToString())));
        }


        /// <summary>
        /// This is the Direct Project in a failed configuration.  The HSM cryptographer cannot get it's configuration data.
        /// Bad Url
        /// Incoming message.
        /// </summary>
        [Fact]
        public void TestUnavailableTokenSettingsIncoming()
        {
            var agent = SmtpAgentFactory.Create(GetSettingsPath("TestSmtpAgentConfigService_HsmCryptographer.xml"));
            var messageId = Guid.NewGuid();
            var message = LoadMessage(string.Format(TestMessageDualToHsm, messageId));
            agent.ProcessMessage(message);

            //nothing in bad folder
            Assert.False(FileMessages(agent.Settings.BadMessage.CopyFolder)
                .Select(File.ReadAllText)
                .Any(m => m.Contains(messageId.ToString())));

            agent = SmtpAgentFactory.Create(GetSettingsPath("TestSmtpAgentConfigService_HsmCryptographerBadUrl.xml"));
            message = LoadMessage(message);
            VerifyOutgoingMessage(message);
            Assert.Throws<SmtpAgentSettingsException>(() => agent.ProcessMessage(message));

            //message in bad folder
            Assert.True(FileMessages(agent.Settings.BadMessage.CopyFolder)
                .Select(File.ReadAllText)
                .Any(m => m.Contains(messageId.ToString())));
        }
    }
}
