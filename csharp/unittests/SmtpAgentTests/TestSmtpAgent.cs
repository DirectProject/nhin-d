using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using NUnit.Framework;
using NHINDirect.Mail;
using NHINDirect.SmtpAgent;

namespace SmtpAgentTests
{    
    [TestFixture]
    public class TestSmtpAgent
    {
        SmtpServiceAgent m_agent;
        
        public TestSmtpAgent()
        {
        }
        
        const string TestMessage = @"
            From: <toby@redmond.hsgincubator.com>
            To: <biff@nhind.hsgincubator.com>
            Subject: Simple Text Message
            Date: Mon, 10 May 2010 14:53:27 -0700
            MIME-Version: 1.0
            Content-Type: text/plain

            Yo. Wassup?";
        
        [Test]
        public void Test()
        {
            Message message = MailParser.ParseMessage<  
            SmtpServiceAgent agent = new SmtpServiceAgent("testService", Path.Combine(Directory.GetCurrentDirectory(), "AgentConfig.xml"));
            agent.ProcessMessage(
        }
    }
}
