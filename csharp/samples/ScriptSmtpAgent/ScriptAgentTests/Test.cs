using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using NHINDirect.ScriptAgent;
using System.IO;

namespace ScriptAgentTests
{
    [TestFixture]
    public class Test
    {
        const string TestMessage = @"
            From: <toby@redmond.hsgincubator.com>
            To: <biff@nhind.hsgincubator.com>
            Subject: Simple Text Message
            Date: Mon, 10 May 2010 14:53:27 -0700
            MIME-Version: 1.0
            Content-Type: text/plain

            Yo. Wassup?";
        
        SmtpAgentEventHandler m_handler;
        
        [SetUp]
        public void Init()
        {
            m_handler = new SmtpAgentEventHandler();
            m_handler.Init("testing", Path.Combine(Directory.GetCurrentDirectory(), "ScriptAgentTestConfig.xml"));
            //m_handler.Init("testing", Path.Combine(Directory.GetCurrentDirectory(), "AgentConfig.xml"));
        }        
        
        [Test]
        public void TestEndToEnd()
        {
            this.TestEndToEnd(TestMessage.Trim(), 
                                @";SMTP:bob@nhind.hsgincubator.com;SMTP:biff@nhind.hsgincubator.com",
                                @"toby@redmond.hsgincubator.com");
            
        }

        [Test]
        public void TestCDO()
        {
            bool isIncoming = true;
            string text = m_handler.ProcessCDOMessageFile(@"C:\inetpub\mailroot\simple.eml", ref isIncoming);
            Assert.IsFalse(isIncoming);
            Assert.IsNotEmpty(text);
        }
        
        void TestEndToEnd(string messageText, string recipients, string from)
        {
            messageText = this.ProcessOutgoing(messageText, recipients, from);            
            messageText = this.ProcessIncoming(messageText, recipients, from);
            messageText = this.ProcessOutgoing(messageText, recipients, from);
        }   
        
        string ProcessOutgoing(string messageText, string recipients, string from)
        {
            bool isIncoming = false;
            messageText = m_handler.ProcessMessage(messageText, recipients, from, ref isIncoming);
            
            Assert.IsFalse(isIncoming);
            Assert.IsNotEmpty(messageText);
            
            return messageText;
        }

        string ProcessIncoming(string messageText, string recipients, string from)
        {
            bool isIncoming = false;
            messageText = m_handler.ProcessMessage(messageText, recipients, from, ref isIncoming);
            Assert.IsTrue(isIncoming);
            Assert.IsNotEmpty(messageText);

            return messageText;
        }
    }
}
