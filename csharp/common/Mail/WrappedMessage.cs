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
using System.Text;
using System.Net.Mime;
using Health.Direct.Common.Mime;

namespace Health.Direct.Common.Mail
{
    /// <summary>
    /// Encapsulates an RFC 5322 message wrapped as a separate RFC5322 message containing the wrapped message
    /// </summary>
    public static class WrappedMessage
    {        
        /// <summary>
        /// Creates a wrapped message, copying headers
        /// </summary>
        /// <param name="message">The message to wrap</param>
        /// <param name="headersToCopy">The headers that will be copied into the wrapped message</param>
        /// <returns></returns>
        public static Message Create(Message message, string[] headersToCopy)
        {
            if (message == null)
            {
                throw new ArgumentNullException("message");
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


        /// <summary>
        /// Creates a wrapped message, copying headers
        /// </summary>
        /// <param name="message">A string containing the RFC 5322 message to wrap</param>
        /// <param name="headersToCopy">The headers that will be copied into the wrapped message</param>
        /// <returns></returns>
        public static Message Create(string message, string[] headersToCopy)
        {
            if (string.IsNullOrEmpty(message))
            {
                throw new ArgumentException("value was null or empty", "message");
            }
            
            Message wrappedMessage = new Message();
            wrappedMessage.Headers.Add(MimeSerializer.Default.DeserializeHeaders(message), headersToCopy);
            wrappedMessage.Body = new Body(message);
            wrappedMessage.ContentType = MailStandard.MediaType.WrappedMessage;

            return wrappedMessage;
        }
        
        /// <summary>
        /// Tests if this message contains a wrapped RFC 5322 message
        /// </summary>
        /// <param name="message">The message to test</param>
        /// <returns><c>true</c> if the message contains a wrapped RFC 5322 message, <c>false</c> otherwise</returns>
        public static bool IsWrapped(Message message)
        {
            if (message == null)
            {
                return false;
            }
            
            return message.HasMediaType(MailStandard.MediaType.WrappedMessage);
        }
        
        /// <summary>
        /// Extracts the inner message of a wrapped message as an unwrapped message
        /// </summary>
        /// <param name="message">The wrapped message</param>
        /// <returns>The unwrapped message</returns>
        /// <exception cref="MimeException">If this is not a wrapped message</exception>
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

            StringSegment innerMessageText = DecodeBody(message);
            return MimeSerializer.Default.Deserialize<Message>(innerMessageText);
        }
        
        static StringSegment DecodeBody(Message message)
        {
            StringSegment innerMessageText = message.Body.SourceText;
            TransferEncoding encoding = message.GetTransferEncoding();
            switch (encoding)
            {
                default:
                    throw new MimeException(MimeError.TransferEncodingNotSupported);

                case TransferEncoding.SevenBit:
                    break; // Nothing to do

                case TransferEncoding.QuotedPrintable:
                    string decodedText = QuotedPrintableDecoder.Decode(innerMessageText);
                    innerMessageText = new StringSegment(decodedText);
                    break;
                
                case TransferEncoding.Base64:
                    byte[] bytes = Convert.FromBase64String(innerMessageText.ToString());
                    string textFromBytes = Encoding.ASCII.GetString(bytes);
                    innerMessageText = new StringSegment(textFromBytes);
                    break;                    
            }
            
            return innerMessageText;
        }
    }
}