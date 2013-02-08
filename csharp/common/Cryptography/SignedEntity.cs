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

using Health.Direct.Common.Mime;

namespace Health.Direct.Common.Cryptography
{
    /// <summary>
    /// Represents a <c>multipart/signed</c> MIME entity.
    /// </summary>
    public class SignedEntity : MultipartEntity
    {
        MimeEntity m_content;
        MimeEntity m_signature;
        
        /// <summary>
        /// Creates an entity consisting of the content and signature.
        /// </summary>
        /// <param name="algorithm">The digest algorithm used in the signature, used for the <c>micalg</c> parameter</param>
        /// <param name="content">The content entity that was signed.</param>
        /// <param name="signature">The signature entity</param>
        public SignedEntity(DigestAlgorithm algorithm, MimeEntity content, MimeEntity signature)
            : base(CreateContentType(algorithm))
        {
            if (content == null)
            {
                throw new ArgumentNullException("content");
            }
            
            Content = content;
            Signature = signature;
        }

        /// <summary>
        /// Creates an entity from the <paramref name="parts"/> of which the first part must be the content and the second
        /// part the signature..
        /// </summary>
        /// <param name="contentType">The content type header for the new entity.</param>
        /// <param name="parts">The body parts, which must consist of two parts, of which the first must be the content and the second part the signature</param>
        public SignedEntity(ContentType contentType, IEnumerable<MimeEntity> parts)
            : base(contentType)
        {
            if (parts == null)
            {
                throw new ArgumentNullException("parts");
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
        
        /// <summary>
        /// Gets and sets the content body part
        /// </summary>
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
                    throw new ArgumentNullException("value");
                }
                
                m_content = value;
            }
        }
        
        /// <summary>
        /// Gets and sets the signature body part.
        /// </summary>
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
                    throw new ArgumentNullException("value");
                }
                
                if (!SMIMEStandard.IsDetachedSignature(value))
                {
                    throw new SignatureException(SignatureError.NotDetachedSignature);
                }
                m_signature = value;
            }
        }
                
        /// <summary>
        /// Gets an enumeration of the content and signature body parts
        /// </summary>
        /// <returns>An enumeration of the content and signature body parts</returns>
        public override IEnumerator<MimeEntity> GetEnumerator()
        {
            yield return m_content;
            yield return m_signature;
        }
        
        /// <summary>
        /// Creates a signed entity from a <see cref="MimeEntity"/>, which must be multipart and have a content and signed part.
        /// </summary>
        /// <param name="source">The source entity.</param>
        /// <returns>The newly initialized signed entity.</returns>
        public static SignedEntity Load(MimeEntity source)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }

            if (!source.IsMultiPart)
            {
                throw new SignatureException(SignatureError.InvalidMultipartSigned);
            }
            
            return new SignedEntity(source.ParsedContentType, source.GetParts());
        }
        
        /// <summary>
        /// Create the ContentType MIME header for a Signed MIME entity
        /// </summary>
        /// <param name="digestAlgorithm">Digest algorithm being used, such as SHA1</param>
        /// <returns>ContentType header</returns>
        public static ContentType CreateContentType(DigestAlgorithm digestAlgorithm)
        {
            ContentType contentType = new ContentType(SMIMEStandard.MultiPartTypeSigned);
            contentType.Parameters.Add(SMIMEStandard.ProtocolParameterKey, SMIMEStandard.SignatureProtocol);
            contentType.Parameters.Add(SMIMEStandard.MICAlgorithmKey, SMIMEStandard.ToString(digestAlgorithm));
            return contentType;
        }
    }
}