using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using NHINDirect.Agent;
using NHINDirect.SmtpAgent;
using AgentTests;
using Xunit;
using Xunit.Extensions;

namespace SmtpAgentTests
{
    public class TestHandler : SmtpAgentTester
    {
        MessageArrivalEventHandler m_handler;

        public TestHandler()
        {
            AgentTests.AgentTester.EnsureStandardMachineStores();
            m_handler = new MessageArrivalEventHandler();
            m_handler.InitFromConfigFile(MakeFilePath("TestSmtpAgentConfig.xml"));
        }

        [Fact]
        public void Test()
        {
            Assert.DoesNotThrow(() => m_handler.ProcessCDOMessage(this.LoadMessage(SmtpAgentTester.TestMessage)));
            Assert.Throws<AgentException>(() => m_handler.ProcessCDOMessage(this.LoadMessage(SmtpAgentTester.BadMessage)));
        }
    }
}
