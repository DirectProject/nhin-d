using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Net.Mime;
using NHINDirect.Agent;
using NHINDirect.SmtpAgent;
using NHINDirect.Mime;
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

        public static IEnumerable<object[]> EndToEndParams
        {
            get
            {
                yield return new[] { TestMessage };
            }
        }

        [Fact]
        public void Test()
        {
            Assert.DoesNotThrow(() => m_handler.ProcessCDOMessage(this.LoadMessage(SmtpAgentTester.TestMessage)));
            Assert.Throws<AgentException>(() => m_handler.ProcessCDOMessage(this.LoadMessage(SmtpAgentTester.BadMessage)));
        }
        
        /// <summary>
        /// TODO: Make this into a theory, and use the same messages used by the NHIND Agent tests
        /// </summary>
        [Theory]
        [PropertyData("EndToEndParams")]     
        public void TestEndToEnd(string messageText)
        {
            CDO.Message message = this.LoadMessage(messageText);
            
            string originalSubject = message.Subject;
            string originalContentType = message.GetContentType();
            
            //
            // Outgoing
            //
            Assert.DoesNotThrow(() => m_handler.ProcessCDOMessage(message));            
            
            message = this.LoadMessage(message.GetMessageText()); // re-load the message
            Assert.True(string.IsNullOrEmpty(message.Subject));                        
            ContentType contentType = new ContentType(message.GetContentType());
            Assert.True(NHINDirect.Cryptography.SMIMEStandard.IsContentEncrypted(contentType));
            //
            // Incoming
            //
            Assert.DoesNotThrow(() => m_handler.ProcessCDOMessage(message));
            
            message = this.LoadMessage(message.GetMessageText()); // re-load the message            
            Assert.True(message.Subject.Equals(originalSubject));
            Assert.True(MimeStandard.Equals(message.GetContentType(), originalContentType));             
        }
    }
}
