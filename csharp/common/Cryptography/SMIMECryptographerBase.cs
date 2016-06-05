/* 
 Copyright (c) 2016, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Security.Cryptography;
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Mime;

namespace Health.Direct.Common.Cryptography
{
    /// <summary>
    /// Base SMimeCryptographer
    /// 
    /// <remarks>
    /// OIDS
    /// Hash Algorithms
    /// </remarks>
    /// </summary>
    public abstract class SMIMECryptographerBase
    {
        /// <summary>
        /// Gets and sets the <see cref="EncryptionAlgorithm"/> used by this crytographer
        /// </summary>
        public EncryptionAlgorithm EncryptionAlgorithm { get; set; }

        /// <summary>
        /// Gets and sets the <see cref="DigestAlgorithm"/> used by this cryptographer
        /// </summary>
        public DigestAlgorithm DigestAlgorithm { get; set; }
        /// <summary>
        /// Gets and sets whether this cryptograher includes the epilogue to the multipart message in the signature
        /// </summary>
        /// <remarks>
        /// When signing multipart messages, some mail clients do not include the multipart epilogue
        /// </remarks>
        public bool IncludeMultipartEpilogueInSignature { get; set; }

        /// <summary>
        /// Gets and sets if this cryptogrpaher should include the entire certificate chain in the signature.
        /// </summary>
        /// <remarks>
        /// Generally, on the leaf user certificate is included, but including the entire chain can help
        /// recievers validate trust.
        /// </remarks>
        public X509IncludeOption IncludeCertChainInSignature { get; set; }

        /// <summary>
        /// Given a MimeEntity:
        /// - Checks that the MimeEntity is encrypted
        /// - Converts the entity body to bytes...
        /// The returned bytes can then be decrypted using the appropriate private key
        /// </summary>
        /// <param name="encryptedEntity">Entity containinng encrypted data</param>
        /// <returns>encrypted bytes</returns>
        public byte[] GetEncryptedBytes(MimeEntity encryptedEntity)
        {
            if (encryptedEntity == null)
            {
                throw new EncryptionException(EncryptionError.NullEntity);
            }

            if (!SMIMEStandard.IsEncrypted(encryptedEntity))
            {
                throw new EncryptionException(EncryptionError.NotEncrypted);
            }

            return Convert.FromBase64String(encryptedEntity.Body.Text);
        }

        /// <summary>
        /// OIDs for known cryptographic digest and encryption algorithms.
        /// </summary>
        public static class CryptoOids
        {
            // documentation for these is silly.
#pragma warning disable 1591
            public static readonly Oid SHA1 = new Oid("1.3.14.3.2.26");
            public static readonly Oid SHA256 = new Oid("2.16.840.1.101.3.4.2.1");
            public static readonly Oid SHA384 = new Oid("2.16.840.1.101.3.4.2.2");
            public static readonly Oid SHA512 = new Oid("2.16.840.1.101.3.4.2.3");
            //
            // Encryption
            //
            public static readonly Oid RSA_ThreeDES = new Oid("1.2.840.113549.3.7");
            public static readonly Oid AES128_ECB = new Oid("2.16.840.1.101.3.4.1.1");
            public static readonly Oid AES128_CBC = new Oid("2.16.840.1.101.3.4.1.2");
            public static readonly Oid AES128_OFB = new Oid("2.16.840.1.101.3.4.1.3");
            public static readonly Oid AES128_CFB = new Oid("2.16.840.1.101.3.4.1.4");
            public static readonly Oid AES192_ECB = new Oid("2.16.840.1.101.3.4.1.21");
            static public readonly Oid AES192_CBC = new Oid("2.16.840.1.101.3.4.1.22");
            public static readonly Oid AES192_OFB = new Oid("2.16.840.1.101.3.4.1.23");
            public static readonly Oid AES192_CFB = new Oid("2.16.840.1.101.3.4.1.24");
            public static readonly Oid AES256_ECB = new Oid("2.16.840.1.101.3.4.1.41");
            public static readonly Oid AES256_CBC = new Oid("2.16.840.1.101.3.4.1.42");
            public static readonly Oid AES256_OFB = new Oid("2.16.840.1.101.3.4.1.43");
            public static readonly Oid AES256_CFB = new Oid("2.16.840.1.101.3.4.1.44");

            public static readonly Oid ContentType_Data = new Oid("1.2.840.113549.1.7.1");
#pragma warning restore 1591
        }

        /// <summary>
        /// Maps an algorithm to an OID constant.
        /// </summary>
        /// <param name="type">The <see cref="SMIMECryptographer.DigestAlgorithm"/> to map to an OID</param>
        /// <returns>The OID corresponding to the digest algorithm.</returns>
        public static Oid ToDigestAlgorithmOid(DigestAlgorithm type)
        {
            switch (type)
            {
                default:
                    throw new NotSupportedException();

                case DigestAlgorithm.SHA1:
                    return CryptoOids.SHA1;

                case DigestAlgorithm.SHA256:
                    return CryptoOids.SHA256;

                case DigestAlgorithm.SHA384:
                    return CryptoOids.SHA384;

                case DigestAlgorithm.SHA512:
                    return CryptoOids.SHA512;
            }
        }

        /// <summary>
        /// Maps the supplied <paramref name="type"/> to an instance of <see cref="AlgorithmIdentifier"/>
        /// </summary>
        /// <param name="type">The encryption algorithm to map</param>
        /// <returns>The corresponding <see cref="AlgorithmIdentifier"/></returns>
        public static AlgorithmIdentifier ToAlgorithmID(EncryptionAlgorithm type)
        {
            switch (type)
            {
                default:
                    throw new NotSupportedException();

                case EncryptionAlgorithm.RSA_3DES:
                    return new AlgorithmIdentifier(CryptoOids.RSA_ThreeDES);

                case EncryptionAlgorithm.AES128:
                    return new AlgorithmIdentifier(CryptoOids.AES128_CBC);

                case EncryptionAlgorithm.AES192:
                    return new AlgorithmIdentifier(CryptoOids.AES192_CBC);

                case EncryptionAlgorithm.AES256:
                    return new AlgorithmIdentifier(CryptoOids.AES256_CBC);
            }
        }
    }
}
