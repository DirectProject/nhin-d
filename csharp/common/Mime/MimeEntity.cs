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
using System.IO;
using System.Net.Mime;

namespace Health.Direct.Common.Mime
{
    /// <summary>
    /// Represents a MIME entity -- the collection of MIME headers and the associated body.
    /// </summary>
    /// <example>
    /// The following constructs a multipart entity with two subparts:
    /// <code>
    /// MimeEntity e = new MimeEntity();
    /// MimeEntityCollection c = new MimeEntityCollection("multipart/mixed");
    /// c.Entities.Add(new MimeEntity("Text part", "text/plain"));
    /// c.Entities.Add(new MimeEntity("<html><body><p>Hello, World!</p></body></html>", "text/html"));
    /// e.UpdateBody(c);
    /// </code>
    /// 
    /// </example>
    public class MimeEntity
    {
        HeaderCollection m_headers;
        Body m_body;
        ContentType m_contentType;  // strongly typed, used internally for parsing
        
        /// <summary>
        /// Initializes an empty instances
        /// </summary>
        public MimeEntity()
        {
        }
        
        /// <summary>
        /// Initializes an instance with a text body part.
        /// </summary>
        /// <param name="bodyText">The text body part</param>
        public MimeEntity(string bodyText)   
            : this(bodyText, MimeStandard.MediaType.Default)         
        {
        }

        /// <summary>
        /// Initializes an instance with a string bodypart, and associated <c>Content-Type</c>
        /// </summary>
        /// <param name="bodyText">The body part</param>
        /// <param name="contentType">The content type string</param>
        public MimeEntity(string bodyText, string contentType)
            : this(new Body(bodyText), contentType)
        {
        }

        /// <summary>
        /// Initializes an instance with a <see cref="Body"/>, and associated <c>Content-Type</c>
        /// </summary>
        /// <param name="body">The body of this entity</param>
        /// <param name="contentType">The content type string</param>
        public MimeEntity(Body body, string contentType)
        {
            if (body == null)
            {
                throw new ArgumentNullException("body");
            }
            if (contentType == null)
            {
                throw new ArgumentNullException("contentType");
            }
            
            this.ContentType = contentType;
            this.Body = body;
        }
        
        /// <summary>
        /// Tests if this entity has headers
        /// </summary>
        public bool HasHeaders
        {
            get
            {
                return (m_headers != null && m_headers.Count > 0);
            }
        }

        /// <summary>
        /// Gets and sets the headers for this entity
        /// </summary>
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
                this.ClearParsedHeaders();
                m_headers = value;
            }
        }

        /// <summary>
        /// Gets and sets the value of <c>Content-Type</c>
        /// </summary>
        /// <remarks>Note that this includes the entire header value, not just the media type string</remarks>
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

        /// <summary>
        /// Gets and sets the value of the <c>Content-Disposition</c> header
        /// </summary>
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


        /// <summary>
        /// Gets and sets the <c>Content-Transfer-Encoding</c> header value.
        /// </summary>
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

        /// <summary>
        /// Gets the <see cref="ContentType"/> for this entity
        /// </summary>
        public virtual ContentType ParsedContentType
        {
            get
            {
                if (m_contentType == null)
                {
                    string contentType = this.ContentType;
                    if (string.IsNullOrEmpty(contentType))
                    {
                        contentType = MimeStandard.MediaType.Default;
                    }
                    
                    m_contentType = new ContentType(contentType);
                }

                return m_contentType;
            }
        }

        /// <summary>
        /// Tests if the <c>Content-Type</c> header for this entity indicates multipart content.
        /// </summary>
        public bool IsMultiPart
        {
            get
            {
                string contentType = this.ContentType;
                if (string.IsNullOrEmpty(contentType))
                {
                    return false;
                }
                return MimeStandard.Contains(contentType, MimeStandard.MediaType.Multipart);
            }
        }


        /// <summary>
        /// Gets and sets the <see cref="Body"/> for this entity.
        /// </summary>
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

        /// <summary>
        /// Tests if this entity has a body.
        /// </summary>
        public bool HasBody
        {
            get
            {
                return (m_body != null);
            }
        }

        /// <summary>
        /// Compares the given mediaType to the media type of the message
        /// </summary>
        /// <param name="mediaType"></param>
        /// <returns><c>true</c> if the media type is the provided string by MIME string comparison rules,
        /// <c>false</c> otherwise.</returns>
        public bool HasMediaType(string mediaType)
        {
            return this.ParsedContentType.IsMediaType(mediaType);
        }
        
        /// <summary>
        /// Updates this entity with a new entity, updating headers and body as appropriate.
        /// </summary>
        /// <param name="entity">The entity to update.</param>
        public virtual void UpdateBody(MimeEntity entity)
        {
            if (entity == null)
            {
                throw new ArgumentNullException("entity");
            }
            
            this.ClearParsedHeaders();
            this.Headers.AddUpdate(entity.Headers);
            this.Body = entity.Body;            
        }
        
        /// <summary>
        /// Tests if this entity has the named header, using MIME-appropriate string comparison.
        /// </summary>
        /// <param name="name">The header name to test for.</param>
        /// <returns><c>true</c> if the entity has the named header, <c>false</c> otherwise</returns>
        public bool HasHeader(string name)
        {
            if (!this.HasHeaders)
            {
                return false;
            }
            
            return (m_headers[name] != null);
        }

        /// <summary>
        /// Tests if this entity has the named header with a value, using MIME-appropriate string comparison.
        /// </summary>
        /// <param name="name">The header name to test for.</param>
        /// <param name="value">The value to test</param>
        /// <returns><c>true</c> if the entity has the named header and the header has the appropriate value, <c>false</c> otherwise</returns>
        public bool HasHeader(string name, string value)
        {
            if (!this.HasHeaders)
            {
                return false;
            }

            Header header = m_headers[name];
            return (header != null && MimeStandard.Equals(header.Value, value));
        }
        
        /// <summary>
        /// Parses the ContentTransferEncoding header, if any.
        /// If no header specified, returns SevenBit, the default
        /// If transfer encoding not recognized, returns TransferEncoding.Unknown
        /// </summary>
        /// <returns>The transfer encoding for this Mime Entity</returns>
        public TransferEncoding GetTransferEncoding()
        {
            TransferEncoding encoding = TransferEncoding.SevenBit;
            string transferEncodingHeader = this.ContentTransferEncoding;
            if (!string.IsNullOrEmpty(transferEncodingHeader))
            {
                encoding = MimeStandard.ToTransferEncoding(transferEncodingHeader);
            }
            return encoding;
        }
                        
        /// <summary>
        /// Updates this entity with the multipart entity, updating headers and body as appropriate.
        /// </summary>
        /// <param name="multipartEntity">The mulitpart entity to update.</param>
        public virtual void UpdateBody(MultipartEntity multipartEntity)
        {
            this.SetParts(multipartEntity);
        }
        
        /// <summary>
        /// Gets the parts of a multipart body
        /// </summary>
        /// <remarks>
        /// Skips/ignores Prologue and the Epilogue parts...
        /// </remarks>
        /// <exception cref="MimeException">If the body is not multipart</exception>
        /// <returns>An enumeration of MIME body parts for the multipart body.</returns>
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
        /// Gets all body parts of a multipart message, including prologue and epilogue
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

        /// <summary>
        /// Updates this entity with the multipart entity, updating headers and body as appropriate.
        /// </summary>
        /// <param name="entities">The mulitpart entity to update.</param>
        public void SetParts(MultipartEntity entities)
        {
            this.SetParts(entities, MimeSerializer.Default);
        }

        /// <summary>
        /// Updates this entity with the multipart entity, updating headers and body as appropriate,
        /// using a custom serializer
        /// </summary>
        /// <param name="entities">The mulitpart entity to update.</param>
        /// <param name="serializer">The serializer to use</param>
        public void SetParts(MultipartEntity entities, MimeSerializer serializer)
        {
            this.SetParts(entities, entities.ContentType.ToString(), serializer);
        }

        /// <summary>
        /// Updates this entity with the multipart body parts and content type provided.
        /// </summary>
        /// <param name="entities">The mulitpart bodyparts to update.</param>
        /// <param name="contentType">The main body part to use</param>
        public void SetParts(IEnumerable<MimeEntity> entities, string contentType)
        {
            this.SetParts(entities, contentType, MimeSerializer.Default);
        }


        /// <summary>
        /// Updates this entity with the multipart body parts and content type provided.
        /// using a custom serializer
        /// </summary>
        /// <param name="entities">The mulitpart bodyparts to update.</param>
        /// <param name="contentType">The main body part to use</param>
        /// <param name="serializer">The custom serializer to use.</param>
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
                contentType = MimeStandard.MediaType.MultipartMixed;
            }
            
            this.ContentType = contentType;
            this.Body = new Body(serializer.Serialize(entities, this.ParsedContentType.Boundary));
        }

        /// <summary>
        /// Returns a string representation of the entity.
        /// </summary>
        /// <returns>A string representation of the entity.</returns>
        public override string ToString()
        {
            return MimeSerializer.Default.Serialize(this);
        }
        
        /// <summary>
        /// Serializes the entity to text and saves to the provided file.
        /// </summary>
        /// <param name="filePath">The file name to save to.</param>
        public void Save(string filePath)
        {
            MimeSerializer.Default.Serialize(this, filePath);
        }

        /// <summary>
        /// Serializes the entity to text and saves to the provided stream.
        /// </summary>
        /// <param name="stream">The stream to save to.</param>
        public void Save(Stream stream)
        {
            MimeSerializer.Default.Serialize(this, stream);
        }
                
        void ClearParsedHeaders()
        {
            m_contentType = null;
        }
    }
}