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
        private Message m_roundTripMultipart;

        [Fact]
        public void ShouldDoBasicRoundTrip()
        {
            Message m = new Message();
            m.To = new Header("To", "drsmith@exchange.example.org");
            m.Subject = new Header("Subject", "Hello, world!");
            m.Body = new Body("This is a test.");

            string messageText = MimeSerializer.Default.Serialize(m);
            Message m2 = MailParser.ParseMessage(messageText);
            // TODO: Perhaps body is inserting a CRLF?
            Assert.Equal("This is a test.", m2.Body.Text.Trim());
            Assert.Equal("drsmith@exchange.example.org", m2.To.Value);
            Assert.Equal("Hello, world!", m2.Subject.Value);
        }
        
        /// <summary>
        /// Basic round trip mutipart message - treat as a value object in tests -- no modifying...
        /// </summary>
        Message RoundTripMultipartMessage
        {
            get
            {
                if (m_roundTripMultipart == null)
                {
                    Message m = new Message();
                    m.To = new Header("To", "drsmith@exchange.example.org");
                    m.Subject = new Header("Subject", "Hello, world!");

                    MimeEntityCollection c = new MimeEntityCollection("multipart/mixed");
                    c.Entities.Add(new MimeEntity("Text part", "text/plain"));
                    c.Entities.Add(new MimeEntity("<html><body><p>Hello, World!</p></body></html>", "text/html"));
                    m.UpdateBody(c);

                    string messageText = MimeSerializer.Default.Serialize(m);
                    m_roundTripMultipart = MailParser.ParseMessage(messageText);
                }
                return m_roundTripMultipart;
            }
        }



        [Fact]
        public void RoundTripMultipartEmailShouldBeMultipart()
        {
            Message m = RoundTripMultipartMessage;
            Assert.True(m.IsMultiPart);    
        }

        [Fact]

        public void RoundTripMultipartShouldHaveTwoParts()
        {
            Message m = RoundTripMultipartMessage;
            Assert.Equal(2,
                m.GetParts().Count());
        }

        [Fact]
        public void RoundTripMultipartShouldHaveTextPart()
        {
            Message m = RoundTripMultipartMessage;
            Assert.Equal("text/plain",
                m.GetParts()
                    .ElementAt(0)
                    .ContentType);
        }

        [Fact]
        public void RoundTripMultipartShouldMatchSecondPartContent()
        {
            Message m = RoundTripMultipartMessage;
            Assert.Equal("<html><body><p>Hello, World!</p></body></html>",
                m.GetParts()
                    .ElementAt(1)
                    .Body.Text);
        }
    }
}
