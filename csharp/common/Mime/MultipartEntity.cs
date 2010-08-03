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

namespace NHINDirect.Mime
{
    public abstract class MultipartEntity : IEnumerable<MimeEntity>
    {
        ContentType m_contentType;

        public MultipartEntity()
            : this(MimeStandard.MediaType_MultipartMixed)
        {
        }
        
        public MultipartEntity(string contentType)
            : this(new ContentType(contentType))
        {
        }
        
        public MultipartEntity(ContentType contentType)
        {
            this.ContentType = contentType;
        }
        
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
                    throw new ArgumentNullException();
                }
                
                m_contentType = value;
                this.EnsureBoundary();
            }     
        }
        
        public string Boundary
        {
            get
            {
                return m_contentType.Boundary;
            }
        }

        public MimeEntity ToEntity()
        {
            MimeEntity entity = new MimeEntity();
            entity.SetParts(this, m_contentType.ToString());
            return entity;
        }

        public abstract IEnumerator<MimeEntity> GetEnumerator();
        
        void EnsureBoundary()
        {
            if (string.IsNullOrEmpty(m_contentType.Boundary))
            {
                m_contentType.Boundary = Guid.NewGuid().ToString("N");
            }
        }
        
        #region IEnumerable Members

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return this.GetEnumerator();
        }

        #endregion
    }
}
