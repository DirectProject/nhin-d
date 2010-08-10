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
using System.Net.Mime;

using NHINDirect.Mime;

namespace NHINDirect.Cryptography
{
    public class SignedEntity : MultipartEntity
    {
        MimeEntity m_content;
        MimeEntity m_signature;
        
        public SignedEntity(DigestAlgorithm algorithm, MimeEntity content, MimeEntity signature)
            : base(CreateContentType(algorithm))
        {
            if (content == null)
            {
                throw new ArgumentNullException();
            }
            
            Content = content;
            Signature = signature;
        }
        
        public SignedEntity(ContentType contentType, IEnumerable<MimeEntity> parts)
            : base(contentType)
        {
            if (parts == null)
            {
                throw new ArgumentNullException();
            }
            
            int count = 0;
            foreach(MimeEntity part in parts)
            {
                switch(count)
                {
                    default:
                        throw new SignatureException(SignatureError.InvalidSignedEntity);
                    
                    case 0:
                        Content = part;
                        break;
                    
                    case 1:
                        Signature = part;
                        break;
                }                
                ++count;
            }
        }
        
        public MimeEntity Content
        {
            get
            {
                return m_content;
            }
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException();
                }
                
                m_content = value;
            }
        }
        
        public MimeEntity Signature
        {
            get
            {
                return m_signature;
            }
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException();
                }
                
                if (!SMIMEStandard.IsDetachedSignature(value))
                {
                    throw new SignatureException(SignatureError.NotDetachedSignature);
                }
                m_signature = value;
            }
        }
                
        public override IEnumerator<MimeEntity> GetEnumerator()
        {
            yield return m_content;
            yield return m_signature;
        }
        
        public static SignedEntity Load(MimeEntity source)
        {
            if (source == null)
            {
                throw new ArgumentNullException();
            }

            if (!source.IsMultiPart)
            {
                throw new SignatureException(SignatureError.InvalidMultipartSigned);
            }
            
            return new SignedEntity(source.ParsedContentType, source.GetParts());
        }
        
        static ContentType CreateContentType(DigestAlgorithm digestAlgorithm)
        {
            var contentType = new ContentType(SMIMEStandard.MultiPartTypeSigned);
            contentType.Parameters.Add(SMIMEStandard.ProtocolParameterKey, SMIMEStandard.SignatureProtocol);
			contentType.Parameters.Add(SMIMEStandard.MICAlgorithmKey, digestAlgorithm.AsString());
            return contentType;
        }
    }
}
