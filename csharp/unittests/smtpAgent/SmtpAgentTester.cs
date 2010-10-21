/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.IO;
using System.Net.Mime;
using System.Net.Mail;
using System.Linq;
using NHINDirect.Mail;
using NHINDirect.Agent;
using NHINDirect.SmtpAgent;
using Xunit;

namespace SmtpAgentTests
{
    public class SmtpAgentTester
    {
        public const string TestMessage =
@"From: <toby@redmond.hsgincubator.com>
To: <biff@nhind.hsgincubator.com>, <bob@nhind.hsgincubator.com>
Subject: Simple Text Message
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?";

        public const string CrossDomainMessage =
@"From: <toby@redmond.hsgincubator.com>
To: <biff@nhind.hsgincubator.com>, <bob@nhind.hsgincubator.com>, <gm2552@securehealthemail.com>
Subject: Simple Text Message
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?";

        public const string BadMessage =
@"From: <toby@redmond.hsgincubator.com>
To: <xyz@untrusted.com>
Subject: Bad Text Message
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Bad message?";

        public const string UnknownUsersMessage =
@"From: <toby@redmond.hsgincubator.com>
To: <frank@nhind.hsgincubator.com>, <joe@nhind.hsgincubator.com>
Subject: Unknown Users Text Message
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?";

        public const string MultiToMessage =
@"From: <toby@redmond.hsgincubator.com>
To: <biff@nhind.hsgincubator.com>, <bob@nhind.hsgincubator.com>, <gm2552@securehealthemail.com>, <nimbu@redmond.hsgincubator.com>, <pongo@redmond.hsgincubator.com>, <ryan@securehealthemail.com>, <frank@nhind.hsgincubator.com>
Subject: Simple Text Message
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?";

        
        internal string MakeFilePath(string subPath)
        {
            return Path.Combine(Directory.GetCurrentDirectory(), subPath);
        }
        
        internal CDO.Message LoadMessage(string text)
        {
            return NHINDirect.SmtpAgent.Extensions.LoadCDOMessageFromText(text);
        }
        
        internal CDO.Message LoadMessage(CDO.Message source)
        {
            return this.LoadMessage(source.GetMessageText());
        }
        
        internal void VerifyOutgoingMessage(CDO.Message message)
        {
            Assert.True(string.IsNullOrEmpty(message.Subject));
         
            ContentType contentType = new ContentType(message.GetContentType());
            Assert.True(NHINDirect.Cryptography.SMIMEStandard.IsContentEncrypted(contentType));
        }
        
        internal void VerifyIncomingMessage(CDO.Message message)
        {
            ContentType contentType = new ContentType(message.GetContentType());
            Assert.False(NHINDirect.Cryptography.SMIMEStandard.IsContentEncrypted(contentType));            
        }

        internal void ProcessEndToEnd(SmtpAgent agent, Message msg, out OutgoingMessage outgoing, out IncomingMessage incoming)
        {
            outgoing = agent.SecurityAgent.ProcessOutgoing(new MessageEnvelope(msg));
            incoming = agent.SecurityAgent.ProcessIncoming(new MessageEnvelope(outgoing.SerializeMessage()));            
        }
        
        protected string GetSettingsPath(string fileName)
        {
            string relativePath = Path.Combine("SmtpAgentTestFiles", fileName);
            relativePath = MakeFilePath(relativePath);
            return relativePath;
        }
    }
    
    public class DummySmtpMessage : ISmtpMessage
    {
        public bool HasEnvelope
        {
            get { throw new NotImplementedException(); }
        }

        public MessageEnvelope GetEnvelope()
        {
            throw new NotImplementedException();
        }

        public string GetMailFrom()
        {
            throw new NotImplementedException();
        }

        public string GetRcptTo()
        {
            throw new NotImplementedException();
        }

        public void SetRcptTo(NHINDAddressCollection recipients)
        {
            throw new NotImplementedException();
        }

        public void Update(string messageText)
        {
            throw new NotImplementedException();
        }

        public void Accept()
        {
            throw new NotImplementedException();
        }

        public void Reject()
        {
            throw new NotImplementedException();
        }

        public void Abort()
        {
            throw new NotImplementedException();
        }

        public void SaveToFile(string filePath)
        {
            throw new NotImplementedException();
        }
    }
}
