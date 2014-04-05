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
using System.Linq;
using System.Net.Mail;
using System.IO;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Extensions;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.Mail
{
    public class MessageIDFacts
    {
        [Theory]
        [InlineData(null)]
        [InlineData("foo.com")]
        public void TestID(string hostName)
        {
            string messageID = null;
            
            Assert.DoesNotThrow(() => messageID = Message.GenerateMessageID(hostName));
            Assert.True(!string.IsNullOrEmpty(messageID));
            Assert.True(messageID[0] == '<');
            Assert.True(messageID[messageID.Length - 1] == '>');
            Assert.True(messageID.Contains('@'));
            
            if (string.IsNullOrEmpty(hostName))
            {
                Assert.True(messageID.Contains(System.Net.Dns.GetHostName()));    
            }
            else
            {
                Assert.True(messageID.Contains(hostName));
            }
        }
        
        [Fact]
        public void TestMessage()
        {
            Message message = new Message();
            message.FromValue = "toby@redmond.hsgincubator.com";
            Assert.DoesNotThrow(() => message.AssignMessageID());
            
            string messageID = message.IDValue;
            MailAddress address = new MailAddress(message.FromValue);
            Assert.True(messageID.Contains(address.Host));
        }
    }
}
