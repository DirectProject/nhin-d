using System.IO;

using NHINDirect.Agent;
using NHINDirect.SmtpAgent;

using Xunit;

namespace SmtpAgentTests
{    
    public class TestSmtpAgent
    {
        const string TestMessage = 
@"From: <toby@redmond.hsgincubator.com>
To: <biff@nhind.hsgincubator.com>
Subject: Simple Text Message
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?";

        const string BadMessage =
@"From: <toby@redmond.hsgincubator.com>
To: <xyz@untrusted.com>
Subject: Simple Text Message
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Bad message?";
        
        MessageArrivalEventHandler m_handler;
        
        public TestSmtpAgent()
        {
            AgentTests.AgentTester.EnsureStandardMachineStores();
            m_handler = new MessageArrivalEventHandler();
            m_handler.InitFromConfigFile(Path.Combine(Directory.GetCurrentDirectory(), "TestSmtpAgentConfig.xml"));
        }
        
        [Fact]
        public void Test()
        {
            Assert.DoesNotThrow(() => m_handler.ProcessCDOMessage(this.LoadMessage(TestMessage)));
            Assert.Throws<AgentException>(() => m_handler.ProcessCDOMessage(this.LoadMessage(BadMessage)));
        }
        
        [Fact]
        public void TestEndToEnd()
        {
            CDO.Message message = this.LoadMessage(TestMessage);
            //
            // Outgoing
            //
            Assert.DoesNotThrow(() => m_handler.ProcessCDOMessage(message));
            //
            // Incoming
            //
            Assert.DoesNotThrow(() => m_handler.ProcessCDOMessage(message));   
        }
        
        [Fact]
        public void TestUntrusted()
        {
            SmtpAgentSettings settings = SmtpAgentSettings.LoadSettings(Path.Combine(Directory.GetCurrentDirectory(), "TestSmtpAgentConfig.xml"));      
            SmtpAgent agent = new SmtpAgent(settings);
            //
            // This should be accepted because the envelope is what we look at
            //
            MessageEnvelope envelope = new MessageEnvelope(BadMessage, 
                                                        NHINDAddressCollection.ParseSmtpServerEnvelope("biff@nhind.hsgincubator.com"),
                                                        new NHINDAddress("toby@redmond.hsgincubator.com")
                                                        );
           
            Assert.DoesNotThrow(() => agent.Agent.ProcessOutgoing(envelope));  

            envelope = new MessageEnvelope(TestMessage,
                                    NHINDAddressCollection.ParseSmtpServerEnvelope("xyz@untrusted.com"),
                                    new NHINDAddress("toby@redmond.hsgincubator.com"));

            //
            // This SHOULD throw an exception
            //
            Assert.Throws<AgentException>(() => agent.Agent.ProcessOutgoing(envelope));
        }
        
        CDO.Message LoadMessage(string text)
        {
            return NHINDirect.SmtpAgent.Extensions.LoadCDOMessageFromText(text);
        }
    }
}
