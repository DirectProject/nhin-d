using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;

using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Agent.Tests
{
    public class TestEnvelope
    {
        string m_messageFolder;
        
        public TestEnvelope()
        {
            m_messageFolder = Path.Combine(Directory.GetCurrentDirectory(), "TestMessages");
        }
        
        [Fact]
        public void TestIncomingBasic()
        {
            Message message = LoadMessage("simple.eml");
            DirectAddressCollection rcpto = new DirectAddressCollection(
                new DirectAddress[] {
                    new DirectAddress("foo@bar.com"),
                    new DirectAddress("biff@nhind.hsgincubator.com")
                    }
                );
            
            IncomingMessage incoming = new IncomingMessage(message, rcpto, new DirectAddress("toby@redmond.hsgincubator.com"));
            Assert.True(incoming.Recipients.Count == 2);
            
            DirectAddressCollection routingRecipients = incoming.GetRecipientsInRoutingHeaders();
            Assert.True(routingRecipients.Count == 1);
            
            Assert.False(incoming.AreAddressesInRoutingHeaders(rcpto));
            incoming.Recipients.Remove(x => MimeStandard.Equals(x.Address, "foo@bar.com"));
            Assert.True(incoming.AreAddressesInRoutingHeaders(rcpto));
            
            message.ToValue = "toby@redmond.hsgincubator.com";
            incoming = new IncomingMessage(message, rcpto, new DirectAddress("toby@redmond.hsgincubator.com"));
            Assert.False(incoming.AreAddressesInRoutingHeaders(rcpto));

            message.ToValue = "TOBY@REDMOND.HSGINCUBATOR.COM";
            incoming = new IncomingMessage(message, rcpto, new DirectAddress("toby@redmond.hsgincubator.com"));
            Assert.False(incoming.AreAddressesInRoutingHeaders(rcpto));
        }
        
        Message LoadMessage(string messageFile)
        {
            return MailParser.ParseMessage(ReadMessageText(messageFile));
        }
        
        public string ReadMessageText(string messageFilePath)
        {
            if (!Path.IsPathRooted(messageFilePath))
            {
                messageFilePath = Path.Combine(m_messageFolder, messageFilePath);
            }

            return File.ReadAllText(messageFilePath);
        }
    }
}
