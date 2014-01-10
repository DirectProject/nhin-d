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
using System.Net.Mime;

using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;

namespace Health.Direct.Common.Cryptography
{
    /// <summary>
    /// Utility methods testing conformance to RFC 5751 and 1847.
    /// </summary>
    public class SMIMEStandard : MailStandard
    {
        //
        // MIME Types
        //
        /// <summary>
        /// Multipart signed content. RFC 1847
        /// </summary>
        public const string MultiPartTypeSigned = "multipart/signed";
        /// <summary>
        /// <c>multipart/signed</c> parameter for protocol
        /// </summary>
        public const string ProtocolParameterKey = "protocol";
        /// <summary>
        /// S/MIME detached signature <c>Content-Type</c>
        /// </summary>
        public const string SignatureProtocol = "application/pkcs7-signature";
        /// <summary>
        /// <c>multipart/signed</c> parameter for hash/digest (message integrity check) algorithm
        /// </summary>
        public const string MICAlgorithmKey = "micalg"; // Message Integrity Check Protocol        
        //
        // Cryptography
        //
        /// <summary>
        /// S/MIME encrypted (enveloped) data <c>Content-Type</c> (see RFC 5751)
        /// </summary>
        public const string CmsEnvelopeMediaType = "application/pkcs7-mime";
        /// <summary>
        /// Alternative S/MIME encrypted (enveloped) data <c>Content-Type</c> (see RFC 5751)
        /// </summary>
        /// <remarks>
        /// Some S/MIME system (e.g., OpenSSL) use this content type by default.
        /// </remarks>
        public const string CmsEnvelopeMediaTypeAlt = "application/x-pkcs7-mime";   // we are forgiving when we receive messages
        
        /// <summary>
        /// S/MIME encrypted (enveloped) data full <c>Content-Type</c> with parameters
        /// </summary>
        public const string EncryptedEnvelopeContentTypeHeaderValue = "application/pkcs7-mime; smime-type=enveloped-data; name=\"smime.p7m\"";
        /// <summary>
        /// S/MIME encrypted (enveloped) data <c>Content-Disposition</c> header value.
        /// </summary>
        public const string EncryptedEnvelopeDisposition = "attachment; filename=\"smime.p7m\"";
        /// <summary>
        /// S/MIME non-detached signature full <c>Content-Type</c> with parameters
        /// </summary>
        public const string SignatureEnvelopeContentTypeHeaderValue = "application/pkcs7-mime; smime-type=signed-data; name=\"smime.p7\"";
        
        /// <summary>
        /// S/MIME detached signature full <c>Content-Type</c> with parameters
        /// </summary>
        public const string SignatureContentTypeHeaderValue = "application/pkcs7-signature; name=\"smime.p7s\"";
        /// <summary>
        /// S/MIME detached signature <c>Content-Type</c>
        /// </summary>
        public const string SignatureContentMediaType = "application/pkcs7-signature";
        /// <summary>
        /// S/MIME detached signature alternative <c>Content-Type</c>
        /// </summary>
        /// <remarks>
        /// Some S/MIME systems use this by defualt.
        /// </remarks>
        public const string SignatureContentMediaTypeAlternative = "application/x-pkcs7-signature"; // we are forgiving when we receive messages
        /// <summary>
        /// S/MIME detached signature <c>Content-Disposition</c> header value.
        /// </summary>
        public const string SignatureDisposition = "attachment; filename=\"smime.p7s\"";
        
        /// <summary>
        /// S/MIME <c>Content-Type</c> <c>smime-type</c> parameter
        /// </summary>
        public const string SmimeTypeParameterKey = "smime-type";
        /// <summary>
        /// S/MIME <c>smime-type</c> parameter value for encrypted (enveloped) data
        /// </summary>
        public const string EnvelopedDataSmimeType = "enveloped-data";
        /// <summary>
        /// S/MIME <c>smime-type</c> parameter value for signed data
        /// </summary>
        public const string SignedDataSmimeType = "signed-data";
        /// <summary>
        /// Default filename for enveloped encrypted and detached signature data
        /// </summary>
        public const string DefaultFileName = "smime.p7m";
        
        /// <summary>
        /// Tests content type to determine if it indicates enveloped data
        /// </summary>
        /// <param name="contentType">The content-type to examine</param>
        /// <returns><c>true</c> if this is enveloped content, <c>false</c> if not</returns>
        public static bool IsContentCms(ContentType contentType)
        {
            if (contentType == null)
            {
                throw new ArgumentNullException("contentType");
            }

            return (   contentType.IsMediaType(CmsEnvelopeMediaType) 
                       || contentType.IsMediaType(CmsEnvelopeMediaTypeAlt));
        }

        /// <summary>
        /// Tests content type to determine if it indicates encrypted data
        /// </summary>
        /// <param name="contentType">The content-type to examine</param>
        /// <returns><c>true</c> if this is encrypted content, <c>false</c> if not</returns>
        public static bool IsContentEncrypted(ContentType contentType)
        {
            if (contentType == null)
            {
                throw new ArgumentNullException("contentType");
            }
            
            return (IsContentCms(contentType)
                    &&  contentType.HasParameter(SmimeTypeParameterKey, EnvelopedDataSmimeType));
        }

        /// <summary>
        /// Tests content type to determine if it indicates enveloped (non-detached) signature data
        /// </summary>
        /// <param name="contentType">The content-type to examine</param>
        /// <returns><c>true</c> if this is eveloped signature content, <c>false</c> if not</returns>
        public static bool IsContentEnvelopedSignature(ContentType contentType)
        {
            if (contentType == null)
            {
                throw new ArgumentNullException("contentType");
            }

            return (IsContentCms(contentType)
                    &&  contentType.HasParameter(SmimeTypeParameterKey, SignedDataSmimeType));
        }
        
        /// <summary>
        /// Tests content type to determine if it indicates a multipart message with detached signature
        /// </summary>
        /// <param name="contentType">The content-type to examine</param>
        /// <returns><c>true</c> if this is multipart signature content, <c>false</c> if not</returns>
        public static bool IsContentMultipartSignature(ContentType contentType)
        {
            if (contentType == null)
            {
                throw new ArgumentNullException("contentType");
            }

            return (contentType.IsMediaType(MultiPartTypeSigned));
        }

        /// <summary>
        /// Tests content type to determine if it indicates a detached signature
        /// </summary>
        /// <param name="contentType">The content-type to examine</param>
        /// <returns><c>true</c> if this is a detached signature, <c>false</c> if not</returns>
        public static bool IsContentDetachedSignature(ContentType contentType)
        {
            return (    contentType.IsMediaType(SignatureContentMediaType) 
                        ||  contentType.IsMediaType(SignatureContentMediaTypeAlternative));
        }
        
        /// <summary>
        /// Tests the <paramref name="entity"/> to determine if the content type and encoding
        /// indicates it contains encrypted data.
        /// </summary>
        /// <param name="entity">The <see cref="MimeEntity"/> to test</param>
        /// <returns><c>true</c> if encrypted data, <c>false</c> otherwise</returns>
        public static bool IsEncrypted(MimeEntity entity)
        {
            return (IsContentEncrypted(entity.ParsedContentType) && VerifyEncoding(entity));
        }

        /// <summary>
        /// Tests the <paramref name="entity"/> to determine if the content type and encoding
        /// indicates it contains enveloped signed (non-detached) data.
        /// </summary>
        /// <param name="entity">The <see cref="MimeEntity"/> to test</param>
        /// <returns><c>true</c> if enveloped signed data, <c>false</c> otherwise</returns>
        public static bool IsSignedEnvelope(MimeEntity entity)
        {
            return (IsContentEnvelopedSignature(entity.ParsedContentType) && VerifyEncoding(entity));
        }

        /// <summary>
        /// Tests the <paramref name="entity"/> to determine if the content type and encoding
        /// indicates it contains detached signature data.
        /// </summary>
        /// <param name="entity">The <see cref="MimeEntity"/> to test</param>
        /// <returns><c>true</c> if detached signature data, <c>false</c> otherwise</returns>
        public static bool IsDetachedSignature(MimeEntity entity)
        {
            return (IsContentDetachedSignature(entity.ParsedContentType) && VerifyEncoding(entity));
        }
                
        static bool VerifyEncoding(MimeEntity entity)
        {
            return entity.HasHeader(ContentTransferEncodingHeader, TransferEncodingBase64);
        }

        /// <summary>
        /// Transforms the <paramref name="algorithm"/> to a string suitable for the <c>micalg</c> parameter value
        /// </summary>
        /// <param name="algorithm">The digest algorithm to transform</param>
        /// <returns>A string suitable for the value of the <c>micalg</c> parameter</returns>
        public static string ToString(DigestAlgorithm algorithm)
        {
            switch (algorithm)
            {
                default:
                    throw new NotSupportedException();

                case DigestAlgorithm.SHA1:
                    return "sha1";

                case DigestAlgorithm.SHA256:
                    return "sha256";

                case DigestAlgorithm.SHA384:
                    return "sha384";

                case DigestAlgorithm.SHA512:
                    return "sha512";
            }
        }
    }
}