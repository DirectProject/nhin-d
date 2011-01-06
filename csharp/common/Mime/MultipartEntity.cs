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
using System.Net.Mime;

namespace Health.Direct.Common.Mime
{
    /// <summary>
    /// Represents a MIME multipart entity, with body parts.
    /// </summary>
    public abstract class MultipartEntity : IEnumerable<MimeEntity>
    {
        ContentType m_contentType;

        /// <summary>
        /// Initializes a default empty multipart entity with default content type.
        /// </summary>
        public MultipartEntity()
            : this(MimeStandard.MediaType.MultipartMixed)
        {
        }
        
        /// <summary>
        /// Initializes an empty multipart entity with the supplied content type.
        /// </summary>
        /// <param name="contentType">The valid content type string</param>
        public MultipartEntity(string contentType)
            : this(new ContentType(contentType))
        {
        }

        /// <summary>
        /// Initializes an empty multipart entity with the supplied content type.
        /// </summary>
        /// <param name="contentType">The content type for this instance.</param>
        public MultipartEntity(ContentType contentType)
        {
            this.ContentType = contentType;
        }
        
        /// <summary>
        /// Gets and sets the content type for this entity. Will create a new boundary string if
        /// one does not already exist.
        /// </summary>
        public ContentType ContentType
        {
            get
            {
                return m_contentType;
            }       
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException("value");
                }
                
                m_contentType = value;
                this.EnsureBoundary();
            }     
        }
        
        /// <summary>
        /// Gets the boundary string for this instance.
        /// </summary>
        public string Boundary
        {
            get
            {
                return m_contentType.Boundary;
            }
        }

        /// <summary>
        /// Returns the entity corresponding to this instance.
        /// </summary>
        /// <returns>The associated entity.</returns>
        public MimeEntity ToEntity()
        {
            MimeEntity entity = new MimeEntity();
            entity.SetParts(this, m_contentType.ToString());
            return entity;
        }

        /// <summary>
        /// Gets an enumerator over the body parts for this entity.
        /// </summary>
        /// <returns>An enumerator of body parts for this entity.</returns>
        public abstract IEnumerator<MimeEntity> GetEnumerator();
        
        void EnsureBoundary()
        {
            if (string.IsNullOrEmpty(m_contentType.Boundary))
            {
                m_contentType.Boundary = Guid.NewGuid().ToString("N");
            }
        }
        
        #region IEnumerable Members

        /// <summary>
        /// Gets the non-generic enumerator for this instance.
        /// </summary>
        /// <remarks>Use the typesafe generic enumerator by preference.</remarks>
        /// <returns>An enumerator of <see cref="MimeEntity"/> body parts.</returns>
        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return this.GetEnumerator();
        }

        #endregion
    }
}