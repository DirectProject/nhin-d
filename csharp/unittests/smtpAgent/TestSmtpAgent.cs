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
    public class TestSmtpAgent : SmtpAgentTester
    {
        SmtpAgent m_agent;
        
        public TestSmtpAgent()
        {
            AgentTests.AgentTester.EnsureStandardMachineStores();

            //m_agent = new SmtpAgent(SmtpAgentSettings.LoadSettings(MakeFilePath("SmtpAgentTestFiles\\TestSmtpAgentConfigService.xml")));
            //m_agent = new SmtpAgent(SmtpAgentSettings.LoadSettings(MakeFilePath("SmtpAgentTestFiles\\TestSmtpAgentConfigServiceProd.xml")));
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
            m_agent.Settings.InternalMessage.EnableRelay = true;
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(TestMessage)));
                        
            m_agent.Settings.InternalMessage.EnableRelay = false;
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(TestMessage)));
        }

        [Fact]
        public void TestEndToEndInternalMessage()
        {
            m_agent.Settings.InternalMessage.EnableRelay = true;
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(TestMessage)));
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(CrossDomainMessage)));
            m_agent.Settings.InternalMessage.EnableRelay = false;
        }

        [Fact(Skip="Need Config Service to run  this")]
        //[Fact]
        public void TestEndToEndBad()
        {
            Assert.Throws<AgentException>(() => RunEndToEndTest(this.LoadMessage(UnknownUsersMessage)));
        }
        
        [Fact]
        public void TestEndToEndCrossDomain()
        {
            m_agent.Settings.InternalMessage.EnableRelay = true;
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(CrossDomainMessage)));            
        }
                
        void RunEndToEndTest(CDO.Message message)
        {
            m_agent.ProcessMessage(message);            
            message = this.LoadMessage(message);
            base.VerifyOutgoingMessage(message);
            
            m_agent.ProcessMessage(message);
            message = this.LoadMessage(message);
            
            if (m_agent.Settings.InternalMessage.EnableRelay)
            {
                base.VerifyIncomingMessage(message);
            }
            else
            {
                base.VerifyOutgoingMessage(message);
            }
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
