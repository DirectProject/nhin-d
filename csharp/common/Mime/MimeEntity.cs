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
using System.Linq;
using System.Text;
using System.Net.Mime;
using System.IO;

namespace NHINDirect.Mime
{
    public class MimeEntity
    {
        HeaderCollection m_headers;
        Body m_body;
        ContentType m_contentType;  // strongly typed, used internally for parsing
        
        public MimeEntity()
        {
        }
        
        public bool HasHeaders
        {
            get
            {
                return (m_headers != null && m_headers.Count > 0);
            }
        }

        public virtual HeaderCollection Headers
        {
            get
            {
                if (m_headers == null)
                {
                    m_headers = new HeaderCollection();
                }

                return m_headers;
            }
            set
            {
                m_headers = value;
            }
        }

        public virtual string ContentType
        {
            get
            {
                return this.Headers.GetValue(MimeStandard.ContentTypeHeader);
            }
            set
            {
                this.Headers.SetValue(MimeStandard.ContentTypeHeader, value);
                m_contentType = null;
            }
        }

        public virtual string ContentDisposition
        {
            get
            {
                return this.Headers.GetValue(MimeStandard.ContentDispositionHeader);
            }
            set
            {
                this.Headers.SetValue(MimeStandard.ContentDispositionHeader, value);
            }
        }

        public virtual string ContentTransferEncoding
        {
            get
            {
                return this.Headers.GetValue(MimeStandard.ContentTransferEncodingHeader);
            }
            set
            {
                this.Headers.SetValue(MimeStandard.ContentTransferEncodingHeader, value);
            }
        }

        public virtual ContentType ParsedContentType
        {
            get
            {
                if (m_contentType == null)
                {
                    string contentType = this.ContentType;
                    if (string.IsNullOrEmpty(contentType))
                    {
                        contentType = MimeStandard.MediaType_Default;
                    }
                    
                    m_contentType = new ContentType(contentType);
                }

                return m_contentType;
            }
        }

        public bool IsMultiPart
        {
            get
            {
                string contentType = this.ContentType;
                if (string.IsNullOrEmpty(contentType))
                {
                    return false;
                }
                return MimeStandard.Contains(contentType, MimeStandard.MediaType_Multipart);
            }
        }

        public virtual Body Body
        {
            get
            {
                return m_body;
            }
            set
            {
                m_body = value;
            }
        }

        public bool HasBody
        {
            get
            {
                return (m_body != null);
            }
        }
        
        public virtual void ApplyBody(MimeEntity entity)
        {
            if (entity == null)
            {
                throw new ArgumentNullException();
            }

            this.Headers.AddUpdate(entity.Headers);
            this.Body = entity.Body;            
        }
        
        public bool HasHeader(string name)
        {
            if (!this.HasHeaders)
            {
                return false;
            }
            
            return (this.m_headers[name] != null);
        }
        
        public bool HasHeader(string name, string value)
        {
            if (!this.HasHeaders)
            {
                return false;
            }

            Header header = this.m_headers[name];
            return (header != null && MimeStandard.Equals(header.Value, value));
        }
        
        public void ApplyBody(MultipartEntity multipartEntity)
        {
            this.SetParts(multipartEntity);
        }
        
        /// <summary>
        /// Skips/ignores Prologue and the Epilogue parts...
        /// </summary>
        /// <returns></returns>
        public IEnumerable<MimeEntity> GetParts()
        {
            if (!this.IsMultiPart)
            {
                throw new MimeException(MimeError.NotMultipart);
            }
            
            foreach(MimePart part in this.GetAllParts())
            {
                if (part.Type == MimePartType.BodyPart)
                {
                    yield return MimeSerializer.Default.Deserialize<MimeEntity>(part.SourceText);   
                }
            }
        }
        
        /// <summary>
        /// Gets all body parts of a multipart message, including prologue & epilogue
        /// </summary>
        /// <returns></returns>
        public virtual IEnumerable<MimePart> GetAllParts()
        {
            if (!this.IsMultiPart)
            {
                throw new MimeException(MimeError.NotMultipart);
            }
            
            return MimeParser.ReadBodyParts(m_body.SourceText, this.ParsedContentType.Boundary);
        }
        
        public void SetParts(MultipartEntity entities)
        {
            this.SetParts(entities, MimeSerializer.Default);
        }

        public void SetParts(MultipartEntity entities, MimeSerializer serializer)
        {
            this.SetParts(entities, entities.ContentType.ToString(), serializer);
        }
        
        public void SetParts(IEnumerable<MimeEntity> entities, string contentType)
        {
            this.SetParts(entities, contentType, MimeSerializer.Default);
        }

        public virtual void SetParts(IEnumerable<MimeEntity> entities, string contentType, MimeSerializer serializer)
        {
            if (entities == null)
            {
                throw new ArgumentNullException("entities");
            }
            if (serializer == null)
            {
                throw new ArgumentNullException("serializer");
            }

            if (string.IsNullOrEmpty(contentType))
            {
                contentType = MimeStandard.MediaType_MultipartMixed;
            }
            this.ContentType = contentType;
            this.Body = new Body(serializer.Serialize(entities, this.ParsedContentType.Boundary));
        }

        public override string ToString()
        {
            return MimeSerializer.Default.Serialize(this);
        }
        
        public virtual MimeEntityCollection ToParts()
        {
            if (!this.IsMultiPart)
            {
                throw new InvalidOperationException();
            }
            
            return new MimeEntityCollection(this.ContentType, this.GetParts());
        }
    }
}
