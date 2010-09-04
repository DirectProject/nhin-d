/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
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
        
        static TestSmtpAgent()
        {
            AgentTests.AgentTester.EnsureStandardMachineStores();        
        }
        
        public TestSmtpAgent()
        {
            //m_agent = new SmtpAgent(base.LoadTestSettings("TestSmtpAgentConfigService.xml"));
            //m_agent = new SmtpAgent(base.LoadTestSettings("TestSmtpAgentConfigServiceProd.xml"));
            m_agent = new SmtpAgent(base.LoadTestSettings("TestSmtpAgentConfig.xml"));
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
