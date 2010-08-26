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
    public class TestSmtpAgent : SmtpAgentTester
    {
        SmtpAgent m_agent;
        
        public TestSmtpAgent()
        {
            AgentTests.AgentTester.EnsureStandardMachineStores();

            //m_agent = new SmtpAgent(SmtpAgentSettings.LoadSettings(MakeFilePath("SmtpAgentTestFiles\\TestSmtpAgentConfigService.xml")));
            m_agent = new SmtpAgent(SmtpAgentSettings.LoadSettings(MakeFilePath("SmtpAgentTestFiles\\TestSmtpAgentConfig.xml")));
        }
        
        [Fact]
        public void Test()
        {
            Assert.DoesNotThrow(() => m_agent.ProcessMessage(this.LoadMessage(TestMessage)));
            Assert.Throws<AgentException>(() => m_agent.ProcessMessage(this.LoadMessage(BadMessage)));
        }
        
        [Fact]
        public void TestEndToEnd()
        {
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(TestMessage)));   
        }

        [Fact(Skip="Need Config Service to run  this")]
        //[Fact]
        public void TestEndToEndBad()
        {
            Assert.Throws<AgentException>(() => RunEndToEndTest(this.LoadMessage(UnknownUsersMessage)));
        }
        
        void RunEndToEndTest(CDO.Message message)
        {
            string text = message.GetMessageText();
            
            CDOSmtpMessage smtpMessage = new CDOSmtpMessage(message);
            
            MessageEnvelope envelope = m_agent.ProcessOutgoing(smtpMessage);
            smtpMessage.Update(envelope.SerializeMessage());
            
            smtpMessage = new CDOSmtpMessage(message);
            envelope = m_agent.ProcessIncoming(smtpMessage);
            smtpMessage.Update(envelope.SerializeMessage());
        }
        
        [Fact]
        public void TestUntrusted()
        {
            //
            // This should be accepted because the envelope is what we look at
            //
            MessageEnvelope envelope = new MessageEnvelope(BadMessage, 
                                                        NHINDAddressCollection.ParseSmtpServerEnvelope("biff@nhind.hsgincubator.com"),
                                                        new NHINDAddress("toby@redmond.hsgincubator.com")
                                                        );
           
            Assert.DoesNotThrow(() => m_agent.SecurityAgent.ProcessOutgoing(envelope));  

            envelope = new MessageEnvelope(TestMessage,
                                    NHINDAddressCollection.ParseSmtpServerEnvelope("xyz@untrusted.com"),
                                    new NHINDAddress("toby@redmond.hsgincubator.com"));

            //
            // This SHOULD throw an exception
            //
            Assert.Throws<AgentException>(() => m_agent.SecurityAgent.ProcessOutgoing(envelope));
        }        
    }
}
