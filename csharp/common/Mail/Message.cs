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
                throw new ArgumentNullException();
            }
            
            this.Headers.Add(headers);
        }

        // TODO: an AddressHeader subclass would be nice here...
                          
        /// <summary>
        /// The <c>to</c> header
        /// </summary>      
        public Header To
        {
            get
            {
                return this.Headers[MailStandard.ToHeader];
            }
            set
            {
                this.Headers[MailStandard.ToHeader] = value;
            }
        }

        /// <summary>
        /// The <c>cc</c> header
        /// </summary>
        public Header Cc
        {
            get
            {
                return this.Headers[MailStandard.CcHeader];
            }
            set
            {
                this.Headers[MailStandard.CcHeader] = value;
            }
        }

        /// <summary>
        /// The <c>bcc</c> header
        /// </summary>
        public Header Bcc
        {
            get
            {
                return this.Headers[MailStandard.BccHeader];
            }
            set
            {
                this.Headers[MailStandard.BccHeader] = value;
            }
        }
                
        /// <summary>
        /// The <c>from</c> header
        /// </summary>
        public Header From
        {
            get
            {
                return this.Headers[MailStandard.FromHeader];
            }
            set
            {
                this.Headers[MailStandard.FromHeader] = value;
            }
        }
                
        /// <summary>
        /// The <c>subject</c> header
        /// </summary>
        public Header Subject
        {
            get
            {
                return this.Headers[MailStandard.SubjectHeader];
            }
            set
            {
                this.Headers[MailStandard.SubjectHeader] = value;
            }
        }

        /// <summary>
        /// The <c>message-id</c> header
        /// </summary>
        public Header ID
        {
            get
            {
                return this.Headers[MailStandard.MessageIDHeader];
            }
            set
            {
                this.Headers[MailStandard.MessageIDHeader] = value;
            }
        }

        /// <summary>
        /// The <c>date</c> header
        /// </summary>
        public Header Date
        {
            get
            {
                return this.Headers[MailStandard.DateHeader];
            }
            set
            {
                this.Headers[MailStandard.DateHeader] = value;
            }
        }
        

        /// <summary>Extracts the body and associated MIME <c>Content-*</c> headers as a <see cref="MimeEntity"/></summary>
        /// <remarks>
        /// The source message has MIME and non-MIME headers, and the body is not a complete MIME entity for signing and encryption.
        /// Takes the source and creates new Message that contains only items relevant to Mime
        /// </remarks>
        /// <returns></returns>
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
    }
}
