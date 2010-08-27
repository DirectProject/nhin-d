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

using NHINDirect.Mime;

namespace NHINDirect.Mail
{
    public static class WrappedMessage
    {        
        public static Message Create(Message message, string[] headersToCopy)
        {
            if (message == null)
            {
                throw new ArgumentNullException();
            }
            
            Message wrappedMessage = new Message();
            if (message.HasHeaders)
            {
                wrappedMessage.Headers.Add(message.Headers, headersToCopy);
            }            
            wrappedMessage.Body = new Body(MimeSerializer.Default.Serialize(message));
            wrappedMessage.ContentType = MailStandard.MediaType.WrappedMessage;
            
            return wrappedMessage;
        }
        
        public static Message Create(string message, string[] headersToCopy)
        {
            if (string.IsNullOrEmpty(message))
            {
                throw new ArgumentException();
            }
            
            Message wrappedMessage = new Message();
            wrappedMessage.Headers.Add(MimeSerializer.Default.DeserializeHeaders(message), headersToCopy);
            wrappedMessage.Body = new Body(message);
            wrappedMessage.ContentType = MailStandard.MediaType.WrappedMessage;

            return wrappedMessage;
        }
        
        public static bool IsWrapped(Message message)
        {
            if (message == null)
            {
                return false;
            }
            
            string contentType = message.ContentType;
            return (!string.IsNullOrEmpty(contentType) && MimeStandard.Equals(contentType, MailStandard.MediaType.WrappedMessage));            
        }
        
        public static Message ExtractInner(Message message)
        {
            if (!IsWrapped(message))
            {
                throw new MimeException(MimeError.ContentTypeMismatch);
            }
            
            if (!message.HasBody)
            {
                throw new MimeException(MimeError.MissingBody);
            }
            
            return MimeSerializer.Default.Deserialize<Message>(message.Body.SourceText);
        }
    }
}
