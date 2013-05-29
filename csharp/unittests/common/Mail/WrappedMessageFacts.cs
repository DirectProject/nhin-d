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
using Health.Direct.Common.Collections;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;
using Health.Direct.Common.Cryptography;
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
            MailMessage mail = MailMessageGenerator.GenerateRandomMail(toCount, ccCount, bodyLength);
            MailMessage wrappedMessage = MailMessageGenerator.WrappedMailMessage(mail); 
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
        
        [Theory]
        [InlineData("Wrapped_Quoted.eml")]
        [InlineData("Wrapped_Quoted2.eml")]
        [InlineData("Wrapped_Quoted3.eml")]
        [InlineData("Wrapped_Quoted4.eml")]
        [InlineData("Wrapped_Base64.eml")]
        [InlineData("Wrapped_Base642.eml")]
        public void TestFiles(string fileName)
        {
            string filePath = Path.Combine("Mail\\TestFiles", fileName);
            string mailtext = File.ReadAllText(filePath);
            
            Message message = null;
            Assert.DoesNotThrow(() => message = Message.Load(mailtext));
            
            if (SMIMEStandard.IsContentMultipartSignature(message.ParsedContentType))
            {
                SignedEntity signedEntity = null;
                
                Assert.DoesNotThrow(() => signedEntity = SignedEntity.Load(message));
                message.Headers = message.Headers.SelectNonMimeHeaders();
                message.UpdateBody(signedEntity.Content); // this will merge in content + content specific mime headers
            }

            Message extracted = null;
            Assert.DoesNotThrow(() => extracted = WrappedMessage.ExtractInner(message));
            
            Header to = null;
            Assert.DoesNotThrow(() => to = extracted.To);
            
            MailAddressCollection addresses = null;
            Assert.DoesNotThrow(() => addresses = MailParser.ParseAddressCollection(to));
            Assert.True(addresses.Count > 0);

            Assert.DoesNotThrow(() => MailParser.ParseMailAddress(extracted.From));
        }        
    }
}
