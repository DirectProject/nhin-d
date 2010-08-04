using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using NUnit.Framework;
using NHINDirect.Mail;
using NHINDirect.Agent;
using NHINDirect.SmtpAgent;

namespace SmtpAgentTests
{    
    [TestFixture]
    public class TestSmtpAgent
    {
        public TestSmtpAgent()
        {
        }
        
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
        
        [SetUp]
        public void Init()
        {
            m_handler = new MessageArrivalEventHandler();
            m_handler.Init(Path.Combine(Directory.GetCurrentDirectory(), "TestSmtpAgentConfig.xml"));
        }
        
        [Test]
        public void Test()
        {
            m_handler.ProcessCDOMessage(this.LoadMessage(TestMessage));
            m_handler.ProcessCDOMessage(this.LoadMessage(BadMessage));
        }
        
        [Test]
        public void TestUntrusted()
        {
            
            SmtpAgent agent = new SmtpAgent(Path.Combine(Directory.GetCurrentDirectory(), "TestSmtpAgentConfig.xml"));
            //
            // This should be accepted because the envelope is what we look at
            //
            MessageEnvelope envelope = new MessageEnvelope(BadMessage, 
                                                        NHINDAddressCollection.ParseSmtpServerEnvelope("biff@nhind.hsgincubator.com"),
                                                        new NHINDAddress("toby@redmond.hsgincubator.com")
                                                        );
           
            bool isIncoming = false;
            try
            {
                agent.Agent.Process(envelope, ref isIncoming);  
            }
            catch
            {
                Assert.Fail();
            }
            //
            // This SHOULD throw an exception
            //
            envelope = new MessageEnvelope(TestMessage,
                                    NHINDAddressCollection.ParseSmtpServerEnvelope("xyz@untrusted.com"),
                                    new NHINDAddress("toby@redmond.hsgincubator.com"));

            isIncoming = false;
            try
            {
                agent.Agent.Process(envelope, ref isIncoming);
                Assert.Fail();
            }
            catch
            {
            }
        }
        
        CDO.Message LoadMessage(string text)
        {
            return NHINDirect.SmtpAgent.Extensions.LoadCDOMessageFromText(TestMessage);
        }
    }
}
