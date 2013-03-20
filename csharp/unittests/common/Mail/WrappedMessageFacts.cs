/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Mail;
using System.Net.Mime;
using System.IO;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.Mail
{
    public class WrappedMessageFacts
    {
        static readonly string[] DirectHeaders = new[]
                  {
                      MailStandard.VersionHeader,
                      MailStandard.Headers.From,
                      MailStandard.Headers.To,
                      MailStandard.Headers.Cc,
                      MailStandard.Headers.Bcc,
                      MailStandard.Headers.OrigDate,
                      MailStandard.Headers.MessageID,
                      MailStandard.Headers.InReplyTo,
                      MailStandard.Headers.References,
                      MailStandard.Headers.Date
                  };
        
        [Theory]
        [InlineData(3, 2, 250)]
        [InlineData(4, 2, 1000)]
        [InlineData(1, 5, 15000)]
        [InlineData(3, 0, 2343)]
        public void TestRoundTrip(int toCount, int ccCount, int bodyLength)
        {
            // Manually wrap the mail...
            MailMessage mail = this.GenerateRandomMail(toCount, ccCount, bodyLength);
            MailMessage wrappedMessage = WrappedMailMessage(mail); 
            string wrappedMessageText = wrappedMessage.Serialize();
            
            Message parsedMessage = null;
            Assert.DoesNotThrow(() => parsedMessage = Message.Load(wrappedMessageText));
            Message extracted = null;
            Assert.DoesNotThrow(() => extracted = WrappedMessage.ExtractInner(parsedMessage));
            
            string extractedBody = extracted.Body.Text;
            if (extracted.GetTransferEncoding() == TransferEncoding.QuotedPrintable)
            {
                extractedBody = QuotedPrintableDecoder.Decode(new StringSegment(extractedBody));
                extractedBody = extractedBody.TrimEnd();
            }
            Assert.True(extractedBody == mail.Body);
        }
        
        MailMessage WrappedMailMessage(MailMessage inner)
        {
            MailMessage wrapped = new MailMessage();
            wrapped.From = inner.From;
            if (inner.To.Count > 0)
            {
                wrapped.To.Add(inner.To.ToString());
            }
            if (inner.CC.Count > 0)
            {
                wrapped.CC.Add(inner.CC.ToString());
            }
            wrapped.Headers.Add(MailStandard.ContentTypeHeader, MailStandard.MediaType.WrappedMessage);

            string innerText = inner.Serialize();
            wrapped.Body = innerText;
            
            return wrapped;
        }
        
        MailMessage GenerateRandomMail(int toCount, int ccCount, int bodyLength)
        {
            MailMessage mail = new MailMessage();
            this.AddRandomAddresses(mail, toCount, ccCount);
            mail.Subject = string.Format("Random long subject text with a timestamp in it  {0} and some ====", DateTime.Now);
            mail.Body = this.GenerateRandomBody(bodyLength);
            return mail;
        }
                        
        void AddRandomAddresses(MailMessage mail, int toCount, int ccCount)
        {
            mail.From = new MailAddress(this.GenerateEmailAddress(50, 35, 40));
            for (int i = 0; i < toCount; ++i)
            {
                string addr = this.GenerateEmailAddress(50, 35, 30);
                mail.To.Add(new MailAddress(addr));
            }

            for (int i = 0; i < ccCount; ++i)
            {
                string addr = this.GenerateEmailAddress(40, 40, 30);
                mail.CC.Add(new MailAddress(addr));
            }
        }
        
        string GenerateEmailAddress(int nameLength, int userNameLength, int hostLength)
        {
            StringBuilder builder = new StringBuilder(nameLength + userNameLength + hostLength + 10);
            builder.Append('"');
            builder.Append('N', nameLength);
            builder.Append('"');
            builder.Append(" <");
            builder.Append('u', userNameLength);
            builder.Append('@');
            builder.Append('H', hostLength);
            builder.Append(".foo");
            builder.Append('>');
            
            return builder.ToString();
        }
        
        string GenerateRandomBody(int length)
        {
            Random rand = new Random();
            StringBuilder builder = new StringBuilder(length);
            for (int i = 0; i < length; ++i)
            {
                char ch = (char) rand.Next(1, 127);
                if (ch == MimeStandard.CR || ch == MimeStandard.LF)
                {
                    builder.Append(MimeStandard.CRLF);
                }
                else
                {
                    builder.Append(ch);
                }
            }
            
            return builder.ToString();
        }
    }
}
