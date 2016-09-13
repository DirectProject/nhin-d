using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xunit;

namespace Health.Direct.SmtpAgent.Tests
{
    public class TestSmtpAgentFailures : SmtpAgentTester
    {
        /// <summary>
        /// This is the Direct Project in a failed configuration.  It is not loading an agent settings file because the file is bad.
        /// Rather it is loading an internally disabled configuration so we do not accedentally send un-encrypted messages 
        /// </summary>
        [Fact]
        public void TestBadlyFormedSmtpAgentSettings()
        {
            var agent = SmtpAgentFactory.Create(GetSettingsPath("TestSmtpAgentConfigBadlyFormed.xml"));
            var messageId = Guid.NewGuid();


            //Assert.DoesNotThrow(() => agent.ProcessMessage(LoadMessage(string.Format(TestMessage, messageId))));
            Assert.Throws<SmtpAgentSettingsException>(() => agent.ProcessMessage(LoadMessage(string.Format(TestMessage, messageId))));

            Assert.True(FileMessages(SettingsInitializer.BadmailFolder)
                .Select(File.ReadAllText)
                .Any(m => m.Contains(messageId.ToString())));
        }
    }
}
