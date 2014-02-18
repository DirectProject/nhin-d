/* 
 Copyright (c) 2013, Direct Project
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
using Health.Direct.Agent;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;
using Health.Direct.Common.Extensions;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Agent.Tests
{
    public class MessageEnvelopeFacts
    {
        public static string TestMessage =
            @"From: <toby@redmond.hsgincubator.com>
To: <biff@nhind.hsgincubator.com>, <bob@nhind.hsgincubator.com>
Cc: <frank@nhind.hsgincubator.com>,
 <joe@nhind.hsgincubator.com>,
 <fenton@nhind.hsgincubator.com>,
 <laura@nhind.hsgincubator.com>
Bcc: <nancy@nhind.hsgincubator.com>
Subject: Simple Text Message
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?";

        [Fact]
        public void RemoveRecipients()
        {
            MessageEnvelope envelope = new MessageEnvelope(TestMessage);
            DirectAddressCollection toRemove = new DirectAddressCollection();
            toRemove.Add(new DirectAddress("<nancy@nhind.hsgincubator.com>"));
            toRemove.Add(new DirectAddress("<joe@nhind.hsgincubator.com>"));
            toRemove.Add(new DirectAddress("<laura@nhind.hsgincubator.com>"));
            toRemove.Add(new DirectAddress("<bob@nhind.hsgincubator.com>"));
            
            Assert.DoesNotThrow(() => envelope.RemoveFromRoutingHeaders(toRemove));
            
            string outputText = null;
            Assert.DoesNotThrow(() => outputText = envelope.SerializeMessage());
            
            Message outputMessage = Message.Load(outputText);
            Assert.True(outputMessage.Bcc == null);
            
            this.CheckHeaderRaw(outputMessage.Cc, 2);
            this.CheckCollection(outputMessage.Cc, 
                                new string[] {"frank@nhind.hsgincubator.com", "fenton@nhind.hsgincubator.com"},
                                new string[] {"joe@nhind.hsgincubator.com>", "laura@nhind.hsgincubator.com"});
            
            this.CheckHeaderRaw(outputMessage.To, 1);
            this.CheckCollection(outputMessage.To,
                                new string[] { "biff@nhind.hsgincubator.com"},
                                new string[] { "bob@nhind.hsgincubator.com"});
                                 
        }
        
        void CheckHeaderRaw(Header header, int addressCount)
        {
            Assert.True(header != null);
            
            string foldedText = header.SourceText.ToString();
            string[] foldedParts = foldedText.Split(new string[] { MailStandard.CRLF }, StringSplitOptions.None);
            Assert.True(foldedParts.Length == addressCount);
        }
                
        void CheckCollection(Header header, string[] contains, string[] doesNotContain)
        {
            Assert.True(header != null);
            
            DirectAddressCollection collection = null;
            Assert.DoesNotThrow(() => collection= DirectAddressCollection.Parse(header.Value));
            if (!contains.IsNullOrEmpty())
            {
                foreach(string addr in contains)
                {
                    Assert.True(collection.Contains(addr));
                }
            }
            if (!doesNotContain.IsNullOrEmpty())
            {
                foreach (string addr in doesNotContain)
                {
                    Assert.True(!collection.Contains(addr));
                }
            }
        }
    }
}
