using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using NHINDirect.Mail;
using NHINDirect.Mime;

using Xunit;

namespace NHINDirect.Tests.Mail
{
    /// <summary>
    /// Basic tests of round-trip serialization/deserialization.
    /// </summary>
    /// <remarks>
    /// Borrow from Ruby Mail library: http://github.com/mikel/mail
    /// </remarks>
    public class RoundTripFacts
    {
        [Fact]
        public void ShouldDoBasicRoundTrip()
        {
            Message m = new Message();
            m.To = new Header("To", "drsmith@exchange.example.org");
            m.Body = new Body("This is a test.");
            MimeSerializer serializer = MimeSerializer.Default;
            string messageText = serializer.Serialize(m);

            Message m2 = MailParser.ParseMessage(messageText);
            // TODO: Perhaps body is inserting a CRLF?
            Assert.Equal("This is a test.", m2.Body.Text.Trim());
            Assert.Equal("drsmith@exchange.example.org", m2.To.Value);
        }

    }
}
