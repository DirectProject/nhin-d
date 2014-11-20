/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Arien Malec   arien.malec@nhindirect.org

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.Linq;

using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;

using Xunit;

namespace Health.Direct.Common.Tests.Mail
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
        
        [Fact]
        public void CheckDate()
        {
            Message m = new Message();
            m.To = new Header("To", "drsmith@exchange.example.org");
            m.Subject = new Header("Subject", "Hello, world!");
            m.Body = new Body("This is a test.");
            
            m.Timestamp();
            Assert.True(m.HasHeader(MailStandard.Headers.Date));

            string messageText = MimeSerializer.Default.Serialize(m);
            Message m2 = MailParser.ParseMessage(messageText);
            Assert.True(m.HasHeader(MailStandard.Headers.Date));
        }
    }
}