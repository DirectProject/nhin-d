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
using System.Collections.Generic;

using NHINDirect.Mime;

namespace NHINDirect.Mail
{
    /// <summary>
    /// Represents an RFC 5322 message.
    /// </summary>
    public class Message : MimeEntity
    {        

        /// <summary>
        /// Initializes an empty instance.
        /// </summary>
        public Message()
        {
        }
        
        /// <summary>
        /// Intializes an instance with the supplied <paramref name="headers"/>
        /// </summary>
        /// <param name="headers">The email headers for this message</param>
        public Message(IEnumerable<Header> headers)
        {
            if (headers == null)
            {
                throw new ArgumentNullException("headers");
            }
            
            this.Headers.Add(headers);
        }
        
        /// <summary>
        /// Initializes an instance with a To header value
        /// </summary>
        /// <param name="to">The <c>To</c> header value.</param>
        public Message(string to)
            : this(to, null)
        {
        }

        /// <summary>
        /// Initializes an instance with a To and From header value
        /// </summary>
        /// <param name="to">The <c>To</c> header value.</param>
        /// <param name="from">The <c>From</c> header value.</param>
        public Message(string to, string from)
        {
            this.AddToFromHeaders(to, from);
        }

        /// <summary>
        /// Initializes an instance with a To and From header value and body text.
        /// </summary>
        /// <param name="to">The <c>To</c> header value.</param>
        /// <param name="from">The <c>From</c> header value.</param>
        /// <param name="bodyText">Text for the body, added as <c>text/plain</c></param>
        public Message(string to, string from, string bodyText)
            : this(to, from, bodyText, MimeStandard.MediaType.Default)
        {
        }

        /// <summary>
        /// Initializes an instance with a To and From header value and body text with a specified content type
        /// </summary>
        /// <param name="to">The <c>To</c> header value.</param>
        /// <param name="from">The <c>From</c> header value.</param>
        /// <param name="bodyText">Text for the body, added as <c>text/plain</c></param>
        /// <param name="contentType">The <c>Content-Type</c> media type</param>
        public Message(string to, string from, string bodyText, string contentType)
            : base(bodyText, contentType)
        {
            this.AddToFromHeaders(to, from);
        }
        
        /// <summary>
        /// The <c>to</c> header
        /// </summary>      
        public Header To
        {
            get
            {
                return this.Headers[MailStandard.Headers.To];
            }
            set
            {
                this.Headers[MailStandard.Headers.To] = value;
            }
        }

        /// <summary>
        /// The <c>cc</c> header
        /// </summary>
        public Header Cc
        {
            get
            {
                return this.Headers[MailStandard.Headers.Cc];
            }
            set
            {
                this.Headers[MailStandard.Headers.Cc] = value;
            }
        }

        /// <summary>
        /// The <c>bcc</c> header
        /// </summary>
        public Header Bcc
        {
            get
            {
                return this.Headers[MailStandard.Headers.Bcc];
            }
            set
            {
                this.Headers[MailStandard.Headers.Bcc] = value;
            }
        }

        /// <summary>
        /// The <c>from</c> header
        /// </summary>
        public Header From
        {
            get
            {
                return this.Headers[MailStandard.Headers.From];
            }
            set
            {
                this.Headers[MailStandard.Headers.From] = value;
            }
        }

        /// <summary>
        /// The <c>subject</c> header
        /// </summary>
        public Header Subject
        {
            get
            {
                return this.Headers[MailStandard.Headers.Subject];
            }
            set
            {
                this.Headers[MailStandard.Headers.Subject] = value;
            }
        }

        /// <summary>
        /// The <c>message-id</c> header
        /// </summary>
        public Header ID
        {
            get
            {
                return this.Headers[MailStandard.Headers.MessageID];
            }
            set
            {
                this.Headers[MailStandard.Headers.MessageID] = value;
            }
        }
        
        /// <summary>
        /// The <c>date</c> header
        /// </summary>
        public Header Date
        {
            get
            {
                return this.Headers[MailStandard.Headers.Date];
            }
            set
            {
                this.Headers[MailStandard.Headers.Date] = value;
            }
        }

        /// <summary>
        /// The value of the To Header, if any
        /// </summary>                
        public string ToValue
        {
            get
            {
                return this.Headers.GetValue(MailStandard.Headers.To);
            }
            set
            {
                this.Headers.SetValue(MailStandard.Headers.To, value);
            }
        }

        /// <summary>
        /// The value of the Cc Header, if any
        /// </summary>                
        public string CcValue
        {
            get
            {
                return this.Headers.GetValue(MailStandard.Headers.Cc);
            }
            set
            {
                this.Headers.SetValue(MailStandard.Headers.Cc, value);
            }
        }

        /// <summary>
        /// The value of the Bcc Header, if any
        /// </summary>                
        public string BccValue
        {
            get
            {
                return this.Headers.GetValue(MailStandard.Headers.Bcc);
            }
            set
            {
                this.Headers.SetValue(MailStandard.Headers.Bcc, value);
            }
        }

        /// <summary>
        /// The value of the From Header, if any
        /// </summary>                
        public string FromValue
        {
            get
            {
                return this.Headers.GetValue(MailStandard.Headers.From);
            }
            set
            {
                this.Headers.SetValue(MailStandard.Headers.From,  value);
            }
        }

        /// <summary>
        /// The message's Subject header value, if any
        /// </summary>
        public string SubjectValue
        {
            get
            {
                return this.Headers.GetValue(MailStandard.Headers.Subject);
            }
            set
            {
                this.Headers.SetValue(MailStandard.Headers.Subject, value);
            }
        }

        /// <summary>
        /// Gets and sets the value for <c>Message-ID</c>
        /// </summary>
        public string IDValue
        {
            get
            {
                return this.Headers.GetValue(MailStandard.Headers.MessageID);
            }
            set
            {
                this.Headers.SetValue(MailStandard.Headers.MessageID, value);
            }
        }

        /// <summary>
        /// Gets and sets the <c>Date</c> header value.
        /// </summary>
        public string DateValue
        {
            get
            {
                return this.Headers.GetValue(MailStandard.Headers.Date);
            }
            set
            {
                this.Headers.SetValue(MailStandard.Headers.Date, value);
            }
        }

        /// <summary>Extracts the body and associated MIME <c>Content-*</c> headers as a <see cref="MimeEntity"/></summary>
        /// <remarks>
        /// The source message has MIME and non-MIME headers, and the body is not a complete MIME entity for signing and encryption.
        /// Takes the source and creates new Message that contains only items relevant to Mime
        /// </remarks>
        /// <returns>The extacted MIME headers and body as a <see cref="MimeEntity"/></returns>
        public MimeEntity ExtractMimeEntity()
        {
            MimeEntity entity = new MimeEntity();
            
            if (this.HasHeaders)
            {
                //
                // TODO: Optimize this.... 
                //
                entity.Headers = this.Headers.SelectMimeHeaders();
                if (!entity.HasHeaders)
                {
                    throw new MimeException(MimeError.InvalidMimeEntity);
                }
            }
            
            if (this.HasBody)
            {
                entity.Body = new Body(this.Body);
            }
            
            return entity;
        }
        
        /// <summary>
        /// Extracts the MIME entity for signing and encryption purposes.
        /// </summary>
        /// <remarks>The MIME entity for signing and encrytion consists of the <c>Content-*</c>
        /// MIME headers and the body as a complete MIME entity. Some clients omit the epilogue of a
        /// multipart message.</remarks>
        /// <param name="includeEpilogue">Should the epilogue be included if this the body of this message
        /// is multipart?</param>
        /// <returns>The complete MIME entity from this message for signing and encrytion.</returns>
        public MimeEntity ExtractEntityForSignature(bool includeEpilogue)
        {
            if (includeEpilogue || !this.IsMultiPart)
            {
                return this.ExtractMimeEntity();
            }
            
            MimeEntity signableEntity = new MimeEntity();
            signableEntity.Headers = this.Headers.SelectMimeHeaders();
            
            StringSegment content = StringSegment.Null;
            foreach(MimePart part in this.GetAllParts())
            {
                if (part.Type == MimePartType.BodyEpilogue)
                {
                    content = new StringSegment(content.Source, content.StartIndex, part.SourceText.StartIndex - 1);
                }
                else
                {                
                    content.Union(part.SourceText);
                }
            }            
            signableEntity.Body = new Body(content);
            
            return signableEntity;
        }                        
        
        void AddToFromHeaders(string to, string from)
        {
            if (!string.IsNullOrEmpty(to))
            {
                this.Headers.Add(MailStandard.Headers.To, to);
            }
            if (!string.IsNullOrEmpty(from))
            {    
                this.Headers.Add(MailStandard.Headers.From, from);
            }
        }

        /// <summary>
        /// Parses RFC 5322 message <paramref name="messageText"/> returning a <see cref="Message"/>
        /// </summary>
        /// <param name="messageText">The RFC 5322 message text to parse</param>
        /// <returns>A <see cref="Message"/> containing the parsed message</returns>        
        public static Message Load(string messageText)
        {
            return MailParser.ParseMessage(messageText);
        }
    }
}
