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
using System.Security.Cryptography;
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Common.Cryptography
{
    /// <summary>
    /// Encapsulates use of S/MIME PKCS7 (CMS) cryptography
    /// </summary>
    public class SMIMECryptographer
    {
        /// <summary>
        /// The default set of cryptographic algorithms.
        /// </summary>
        public static readonly SMIMECryptographer Default = new SMIMECryptographer();

        EncryptionAlgorithm m_encryptionAlgorithm;
        DigestAlgorithm m_digestAlgorithm;
        bool m_includeEpilogue = true;
        X509IncludeOption m_certChainInclude = X509IncludeOption.EndCertOnly;

        /// <summary>
        /// Initializes an instance with the default set of encryption and digest algorithms.
        /// </summary>
        public SMIMECryptographer()
            : this(EncryptionAlgorithm.AES128, DigestAlgorithm.SHA1)
        {
        }

        /// <summary>
        /// Initializes an instance, specifying the encryption and digest alorigthm to use.
        /// </summary>
        /// <param name="encryptionAlgorithm">The <see cref="EncryptionAlgorithm"/> to use in this cryptographer</param>
        /// <param name="digestAlgorithm">The <see cref="DigestAlgorithm"/> to use in this cryptographer</param>
        public SMIMECryptographer(EncryptionAlgorithm encryptionAlgorithm, DigestAlgorithm digestAlgorithm)
        {
            m_encryptionAlgorithm = encryptionAlgorithm;
            m_digestAlgorithm = digestAlgorithm;
        }

        /// <summary>
        /// Gets and sets the <see cref="EncryptionAlgorithm"/> used by this crytographer
        /// </summary>
        public EncryptionAlgorithm EncryptionAlgorithm
        {
            get
            {
                return m_encryptionAlgorithm;
            }
            set
            {
                m_encryptionAlgorithm = value;
            }
        }

        /// <summary>
        /// Gets and sets the <see cref="DigestAlgorithm"/> used by this cryptographer
        /// </summary>
        public DigestAlgorithm DigestAlgorithm
        {
            get
            {
                return m_digestAlgorithm;
            }
            set
            {
                m_digestAlgorithm = value;
            }
        }
        /// <summary>
        /// Gets and sets whether this cryptograher includes the epilogue to the multipart message in the signature
        /// </summary>
        /// <remarks>
        /// When signing multipart messages, some mail clients do not include the multipart epilogue
        /// </remarks>
        public bool IncludeMultipartEpilogueInSignature
        {
            get
            {
                return m_includeEpilogue;
            }
            set
            {
                m_includeEpilogue = value;
            }
        }

        /// <summary>
        /// Gets and sets if this cryptogrpaher should include the entire certificate chain in the signature.
        /// </summary>
        /// <remarks>
        /// Generally, on the leaf user certificate is included, but including the entire chain can help
        /// recievers validate trust.
        /// </remarks>
        public X509IncludeOption IncludeCertChainInSignature
        {
            get
            {
                return m_certChainInclude;
            }
            set
            {
                m_certChainInclude = value;
            }
        }

        //-----------------------------------------------------
        //
        // Encryption
        //
        //-----------------------------------------------------
        /// <summary>
        /// Takes a <see cref="MultipartEntity"/>, encrypts it, and returns a MimeEntity with the associated
        /// content headers for encrypted content and the encrypted body.
        /// </summary>
        /// <param name="entity">The <see cref="MultipartEntity"/>, including content header and body, to encrypt</param>
        /// <param name="encryptingCertificate">The certificate used for encrytion</param>
        /// <returns>The encrypted mime entity.</returns>
        public MimeEntity Encrypt(MultipartEntity entity, X509Certificate2 encryptingCertificate)
        {
            return Encrypt(entity.ToEntity(), encryptingCertificate);
        }

        /// <summary>
        /// Takes a <see cref="MultipartEntity"/>, encrypts it with a collection of certificates,
        /// and returns a MimeEntity with the associated
        /// content headers for encrypted content and the encrypted body.
        /// </summary>
        /// <remarks>
        /// As specified in the S/MIME and CMS RFCs, encryption uses symetric encryption to encrypt the body, and
        /// certificate-based asymetric encryption to encrypt the encryption key used. With multiple certificates,
        /// there will be multiple copies of the encrypted encryption key.
        /// </remarks>
        /// <param name="entity">The <see cref="MultipartEntity"/>, including content header and body, to encrypt</param>
        /// <param name="encryptingCertificates">The collection of certificate used for encrytion</param>
        /// <returns>The encrypted mime entity.</returns>
        public MimeEntity Encrypt(MultipartEntity entity, X509Certificate2Collection encryptingCertificates)
        {
            return Encrypt(entity.ToEntity(), encryptingCertificates);
        }

        /// <summary>
        /// Takes a MIME entity and returns a new encrypted MIME entity.
        /// </summary>
        /// <param name="entity">The <see cref="MimeEntity"/> including content headers</param>
        /// <param name="encryptingCertificate">The certificate used for encrytion</param>
        /// <returns>The encrypted <see cref="MimeEntity"/></returns>
        public MimeEntity Encrypt(MimeEntity entity, X509Certificate2 encryptingCertificate)
        {
            return Encrypt(entity, new X509Certificate2Collection(encryptingCertificate));
        }

        /// <summary>
        /// Takes a MIME entity and returns a new encrypted MIME entity.
        /// </summary>
        /// <remarks>
        /// As specified in the S/MIME and CMS RFCs, encryption uses symetric encryption to encrypt the body, and
        /// certificate-based asymetric encryption to encrypt the encryption key used. With multiple certificates,
        /// there will be multiple copies of the encrypted encryption key.
        /// </remarks>
        /// <param name="entity">The <see cref="MimeEntity"/> including content headers</param>
        /// <param name="encryptingCertificates">The certificates used for encryption</param>
        /// <returns>The encrypted <see cref="MimeEntity"/></returns>
        public MimeEntity Encrypt(MimeEntity entity, X509Certificate2Collection encryptingCertificates)
        {
            if (entity == null)
            {
                throw new EncryptionException(EncryptionError.NullEntity);
            }

            byte[] messageBytes = DefaultSerializer.Default.SerializeToBytes(entity);     // Serialize message out as ASCII encoded...

            byte[] encryptedBytes = Encrypt(messageBytes, encryptingCertificates);

            MimeEntity encryptedEntity = new MimeEntity
                                             {
                                                 ContentType = SMIMEStandard.EncryptedEnvelopeContentTypeHeaderValue,
                                                 ContentTransferEncoding = TransferEncoding.Base64.AsString(),
                                                 Body = new Body(Convert.ToBase64String(encryptedBytes,
                                                                                        Base64FormattingOptions.InsertLineBreaks))
                                             };

            encryptedEntity.ContentDisposition = SMIMEStandard.EncryptedEnvelopeDisposition;
            return encryptedEntity;
        }

        /// <summary>
        /// Encrypts raw data and returns the encrypted raw data (without content headers)
        /// </summary>
        /// <param name="content">The content to encrypt</param>
        /// <param name="encryptingCertificate">The certificate used for encrytion</param>
        /// <returns>The encrypted raw data.</returns>
        public byte[] Encrypt(byte[] content, X509Certificate2 encryptingCertificate)
        {
            return Encrypt(content, new X509Certificate2Collection(encryptingCertificate));
        }

        /// <summary>
        /// Encrypts raw data and returns the encrypted raw data (without content headers)
        /// </summary>
        /// <param name="content">The content to encrypt</param>
        /// <param name="encryptingCertificates">The collection of certificate used for encrytion</param>
        /// <returns>The encrypted raw data.</returns>
        public byte[] Encrypt(byte[] content, X509Certificate2Collection encryptingCertificates)
        {
            EnvelopedCms envelope = CreateEncryptedEnvelope(content, encryptingCertificates);
            return envelope.Encode();
        }


        /// <summary>
        /// Encrypts raw data and returns a <see cref="EnvelopedCms"/> instance with the encrypted data.
        /// </summary>
        /// <param name="content">The content to encrypt</param>
        /// <param name="encryptingCertificates">The collection of certificate used for encrytion</param>
        /// <returns>The encrypted <see cref="EnvelopedCms"/> instance.</returns>
        public EnvelopedCms CreateEncryptedEnvelope(byte[] content, X509Certificate2Collection encryptingCertificates)
        {
            if (content == null)
            {
                throw new EncryptionException(EncryptionError.NullContent);
            }
            if (encryptingCertificates == null || encryptingCertificates.Count == 0)
            {
                throw new EncryptionException(EncryptionError.NoCertificates);
            }

            CmsRecipientCollection recipients = new CmsRecipientCollection(SubjectIdentifierType.IssuerAndSerialNumber, encryptingCertificates);
            EnvelopedCms dataEnvelope = new EnvelopedCms(CreateDataContainer(content), ToAlgorithmID(m_encryptionAlgorithm));
            dataEnvelope.Encrypt(recipients);

            return dataEnvelope;
        }

        //-----------------------------------------------------
        //
        // Decryption
        //
        //-----------------------------------------------------
        //
        // Cryptography is performed only on the Mime portions of the message, not the RFC822 headers
        //
        
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
        /// Decrypts a <see cref="Message"/> with a certificate
        /// </summary>
        /// <param name="message">The <see cref="Message"/> to decrypt</param>
        /// <param name="decryptingCertificate">The <see cref="X509Certificate2"/> that encrypted the message</param>
        /// <returns>A <see cref="MimeEntity"/> holding the decrypted message</returns>
        public MimeEntity Decrypt(Message message, X509Certificate2 decryptingCertificate)
        {
            return Decrypt(message.ExtractMimeEntity(), decryptingCertificate);
        }

        /// <summary>
        /// Decrypts a <see cref="MimeEntity"/> with a certificate
        /// </summary>
        /// <param name="encryptedEntity">The <see cref="MimeEntity"/> to decrypt</param>
        /// <param name="decryptingCertificate">The <see cref="X509Certificate2"/> that encrypted the message</param>
        /// <returns>A <see cref="MimeEntity"/> holding the decrypted message</returns>
        public MimeEntity Decrypt(MimeEntity encryptedEntity, X509Certificate2 decryptingCertificate)
        {
            return this.DecryptEntity(this.GetEncryptedBytes(encryptedEntity), decryptingCertificate);
        }
        
        /// <summary>
        /// Decrypt the given encryptedByte array into a MimeEntity
        /// </summary>
        /// <param name="encryptedBytes">source encrypted bytes</param>
        /// <param name="decryptingCertificate">The <see cref="X509Certificate2"/> that encrypted the message</param>
        /// <returns>A <see cref="MimeEntity"/> holding the decrypted message</returns>
        public MimeEntity DecryptEntity(byte[] encryptedBytes, X509Certificate2 decryptingCertificate)
        {
            if (encryptedBytes.IsNullOrEmpty())
            {
                throw new ArgumentException("encryptedBytes");
            }
            
            if (decryptingCertificate == null)
            {
                throw new EncryptionException(EncryptionError.NoCertificates);
            }
            if (!decryptingCertificate.HasPrivateKey)
            {
                throw new EncryptionException(EncryptionError.NoPrivateKey);
            }

            byte[] decryptedBytes = Decrypt(encryptedBytes, decryptingCertificate);
            //
            // And turn the encrypted bytes back into an entity
            //
            return DefaultSerializer.Default.Deserialize<MimeEntity>(decryptedBytes);
        }
        
        /// <summary>
        /// Decrypts encrypted raw content with a certificate
        /// </summary>
        /// <param name="encryptedContent">The raw data to decrypt</param>
        /// <param name="decryptingCertificate">The <see cref="X509Certificate2"/> that encrypted the message</param>
        /// <returns>A <c>byte</c> array holding the decrypted content</returns>
        public byte[] Decrypt(byte[] encryptedContent, X509Certificate2 decryptingCertificate)
        {
            return Decrypt(encryptedContent, new X509Certificate2Collection(decryptingCertificate));
        }

        /// <summary>
        /// Decrypts encrypted raw content with a collection of certificates, at least one of which can decrypt the encryption key.
        /// </summary>
        /// <remarks>
        /// See <see cref="EnvelopedCms.Decrypt()"/> for more information on underlying processing.
        /// </remarks>
        /// <param name="encryptedContent">The raw data to decrypt</param>
        /// <param name="decryptingCertificates">The <see cref="X509Certificate2Collection"/> of certificates, at least one of which encrypted the message</param>
        /// <returns>A <c>byte</c> array holding the decrypted content</returns>
        public byte[] Decrypt(byte[] encryptedContent, X509Certificate2Collection decryptingCertificates)
        {
            if (decryptingCertificates == null || decryptingCertificates.Count == 0)
            {
                throw new EncryptionException(EncryptionError.NoCertificates);
            }

            EnvelopedCms dataEnvelope = DeserializeEncryptionEnvelope(encryptedContent);

            dataEnvelope.Decrypt(decryptingCertificates);

            ContentInfo contentInfo = dataEnvelope.ContentInfo;
            if (!IsDataContainer(contentInfo))
            {
                throw new EncryptionException(EncryptionError.ContentNotDataContainer);
            }

            return contentInfo.Content;
        }

        /// <summary>
        /// Deserializes an encrypted <c>byte</c> array of encrypted data as a <see cref="EnvelopedCms"/> instance
        /// </summary>
        /// <param name="encryptedContent">The raw encrypted data</param>
        /// <returns>An instance of <see cref="EnvelopedCms"/> containing the encrypted data</returns>
        public EnvelopedCms DeserializeEncryptionEnvelope(byte[] encryptedContent)
        {
            if (encryptedContent == null)
            {
                throw new EncryptionException(EncryptionError.NullContent);
            }

            EnvelopedCms dataEnvelope = new EnvelopedCms();
            dataEnvelope.Decode(encryptedContent);
            return dataEnvelope;
        }

        internal ContentInfo CreateDataContainer(byte[] content)
        {
            return new ContentInfo(CryptoOids.ContentType_Data, content);
        }

        internal bool IsDataContainer(ContentInfo contentInfo)
        {
            return (contentInfo.ContentType.Value == CryptoOids.ContentType_Data.Value);
        }

        //-----------------------------------------------------
        //
        // Signatures
        //
        //-----------------------------------------------------
        //
        // Cryptography is performed only on the Mime portions of the message, not the RFC822 headers
        // Some mail readers ignore the epilogue when calculating signatures!
        //

        /// <summary>
        /// Creates a detatched signed entity from a message and an signing certificate
        /// </summary>
        /// <remarks>
        /// Cryptography is performed only on the Mime portions of the message, not the RFC822 headers
        /// Some mail readers ignore the epilogue when calculating signatures!
        /// </remarks>
        /// <param name="message">The <see cref="Message"/> entity to sign</param>
        /// <param name="signingCertificate">The certificate with which to sign.</param>
        /// <returns>A <see cref="SignedEntity"/> instance holding the signature.</returns>
        public SignedEntity Sign(Message message, X509Certificate2 signingCertificate)
        {
            return Sign(message.ExtractEntityForSignature(m_includeEpilogue), signingCertificate);
        }

        /// <summary>
        /// Creates a detatched signed entity from a message and a collection of signing certificate
        /// </summary>
        /// <remarks>
        /// Cryptography is performed only on the Mime portions of the message, not the RFC822 headers
        /// Some mail readers ignore the epilogue when calculating signatures!
        /// </remarks>
        /// <param name="message">The <see cref="Message"/> entity to sign</param>
        /// <param name="signingCertificates">The certificates with which to sign.</param>
        /// <returns>A <see cref="SignedEntity"/> instance holding the signatures.</returns>
        public SignedEntity Sign(Message message, X509Certificate2Collection signingCertificates)
        {
            return Sign(message.ExtractEntityForSignature(m_includeEpilogue), signingCertificates);
        }

        /// <summary>
        /// Creates a detatched signed entity from a <see cref="MimeEntity"/> and a signing certificate
        /// </summary>
        /// <remarks>
        /// Cryptography is performed only on the Mime portions of the message, not the RFC822 headers
        /// Some mail readers ignore the epilogue when calculating signatures!
        /// </remarks>
        /// <param name="entity">The <see cref="MimeEntity"/> to sign</param>
        /// <param name="signingCertificate">The certificate with which to sign.</param>
        /// <returns>A <see cref="SignedEntity"/> instance holding the signature.</returns>
        public SignedEntity Sign(MimeEntity entity, X509Certificate2 signingCertificate)
        {
            return Sign(entity, new X509Certificate2Collection(signingCertificate));
        }

        /// <summary>
        /// Creates a detatched signed entity from a <see cref="MimeEntity"/> and collection of a signing certificates
        /// </summary>
        /// <remarks>
        /// Cryptography is performed only on the Mime portions of the message, not the RFC822 headers
        /// Some mail readers ignore the epilogue when calculating signatures!
        /// </remarks>
        /// <param name="entity">The <see cref="MimeEntity"/> to sign</param>
        /// <param name="signingCertificates">The certificates with which to sign.</param>
        /// <returns>A <see cref="SignedEntity"/> instance holding the signatures.</returns>
        public SignedEntity Sign(MimeEntity entity, X509Certificate2Collection signingCertificates)
        {
            if (entity == null)
            {
                throw new SignatureException(SignatureError.NullEntity);
            }

            byte[] entityBytes = DefaultSerializer.Default.SerializeToBytes(entity);
            MimeEntity signature = CreateSignatureEntity(entityBytes, signingCertificates);

            return new SignedEntity(m_digestAlgorithm, entity, signature);
        }

        /// <summary>
        /// Creates a signed data from source raw data and a signing certificate
        /// </summary>
        /// <remarks>
        /// Cryptography is performed only on the Mime portions of the message, not the RFC822 headers
        /// Some mail readers ignore the epilogue when calculating signatures!
        /// </remarks>
        /// <param name="content">The <c>byte</c> array to sign</param>
        /// <param name="signingCertificate">The certificate with which to sign.</param>
        /// <returns>Raw data holding the signature.</returns>
        public byte[] Sign(byte[] content, X509Certificate2 signingCertificate)
        {
            SignedCms signature = CreateSignature(content, signingCertificate);
            return signature.Encode();
        }

        /// <summary>
        /// Creates a signed data from source raw data and a collection of signing certificates
        /// </summary>
        /// <remarks>
        /// Cryptography is performed only on the Mime portions of the message, not the RFC822 headers
        /// Some mail readers ignore the epilogue when calculating signatures!
        /// </remarks>
        /// <param name="content">The <c>byte</c> array to sign</param>
        /// <param name="signingCertificates">The certificates with which to sign.</param>
        /// <returns>Raw data holding the signatures.</returns>
        public byte[] Sign(byte[] content, X509Certificate2Collection signingCertificates)
        {
            SignedCms signature = CreateSignature(content, signingCertificates);
            return signature.Encode();
        }


        /// <summary>
        /// Creates a signed entity from source raw data and a collection of signing certificates
        /// </summary>
        /// <remarks>
        /// Cryptography is performed only on the Mime portions of the message, not the RFC822 headers
        /// Some mail readers ignore the epilogue when calculating signatures!
        /// </remarks>
        /// <param name="content">The <c>byte</c> array to sign</param>
        /// <param name="signingCertificates">The certificates with which to sign.</param>
        /// <returns>The <see cref="MimeEntity"/> holding the signatures.</returns>
        public MimeEntity CreateSignatureEntity(byte[] content, X509Certificate2Collection signingCertificates)
        {
            byte[] signatureBytes = Sign(content, signingCertificates);
            //
            // We create an entity to hold a detached signature
            //
            MimeEntity signature = new MimeEntity
                                       {
                                           ContentType = SMIMEStandard.SignatureContentTypeHeaderValue,
                                           ContentTransferEncoding = TransferEncoding.Base64.AsString(),
                                           Body = new Body(Convert.ToBase64String(signatureBytes))
                                       };
            signature.ContentDisposition = SMIMEStandard.SignatureDisposition;
            return signature;
        }

        /// <summary>
        /// Creates <see cref="SignedCms"/> for raw content and a signing certificate
        /// </summary>
        /// <param name="content">The <c>byte</c> array to sign</param>
        /// <param name="signingCertificate">The certificate with which to sign.</param>
        /// <returns>An instance of <see cref="SignedCms"/> holdling the signature</returns>
        public SignedCms CreateSignature(byte[] content, X509Certificate2 signingCertificate)
        {
            if (content == null)
            {
                throw new SignatureException(SignatureError.NullContent);
            }
            if (signingCertificate == null)
            {
                throw new SignatureException(SignatureError.NoCertificates);
            }

            CmsSigner signer = CreateSigner(signingCertificate);
            SignedCms signature = new SignedCms(CreateDataContainer(content), true); // true: Detached Signature            
            signature.ComputeSignature(signer, true);   // true: don't prompt the user

            return signature;
        }

        /// <summary>
        /// Creates <see cref="SignedCms"/> for raw content and a collection of signing certificate
        /// </summary>
        /// <param name="content">The <c>byte</c> array to sign</param>
        /// <param name="signingCertificates">The certificates with which to sign.</param>
        /// <returns>An instance of <see cref="SignedCms"/> holdling the signatures</returns>
        public SignedCms CreateSignature(byte[] content, X509Certificate2Collection signingCertificates)
        {
            if (content == null)
            {
                throw new SignatureException(SignatureError.NullContent);
            }

            if (signingCertificates == null)
            {
                throw new SignatureException(SignatureError.NoCertificates);
            }

            SignedCms signature = new SignedCms(CreateDataContainer(content), true); // true: Detached Signature            
            for (int i = 0, count = signingCertificates.Count; i < count; ++i)
            {
                CmsSigner signer = CreateSigner(signingCertificates[i]);
                signature.ComputeSignature(signer, true);  // true: don't prompt the user
            }

            return signature;
        }

        //-----------------------------------------------------
        //
        // Signature Validation
        //
        //-----------------------------------------------------

        /// <summary>
        /// Checks that a signature was signed by the signer certificate.
        /// </summary>
        /// <param name="signedEntity">The signed entity to check</param>
        /// <param name="signerCertificate">The signer certificaet that purports to sign the entity</param>
        /// <exception cref="SignatureException">If the entity was not signed by the claimed certificate</exception>
        public void CheckSignature(SignedEntity signedEntity, X509Certificate2 signerCertificate)
        {
            SignedCms signatureEnvelope = DeserializeDetachedSignature(signedEntity);
            CheckSignature(signatureEnvelope.SignerInfos, signerCertificate);
        }

        /// <summary>
        /// Checks that a collection of signature was signed by the signer certificate.
        /// </summary>
        /// <param name="signers">The collection of <see cref="SignerInfo"/>  to check</param>
        /// <param name="signerCertificate">The signer certificate that purports to sign the entity</param>
        /// <exception cref="SignatureException">If the entity was not signed by the claimed certificate</exception>
        public void CheckSignature(SignerInfoCollection signers, X509Certificate2 signerCertificate)
        {
            if (signerCertificate == null)
            {
                throw new SignatureException(SignatureError.NoCertificates);
            }
            if (signers == null || signers.Count == 0)
            {
                throw new SignatureException(SignatureError.NoSigners);
            }
            //
            // Find the signer
            //
            SignerInfo signer = signers.FindByThumbprint(signerCertificate.Thumbprint);
            if (signer == null)
            {
                throw new SignatureException(SignatureError.NoSigners);
            }

            signer.CheckSignature(true);
        }

        /// <summary>
        /// Transforms a <see cref="SignedEntity"/> to the associated <see cref="SignedCms"/> instance
        /// </summary>
        /// <param name="entity">The <see cref="SignedEntity"/> to deserialize</param>
        /// <returns>The corresponding <see cref="SignedCms"/></returns>
        public SignedCms DeserializeDetachedSignature(SignedEntity entity)
        {
            if (entity == null)
            {
                throw new SignatureException(SignatureError.NullEntity);
            }

            // Serialize entity out as ASCII encoded...
            byte[] contentBytes = DefaultSerializer.Default.SerializeToBytes(entity.Content);
            byte[] signatureBytes = Convert.FromBase64String(entity.Signature.Body.Text);

            return DeserializeDetachedSignature(contentBytes, signatureBytes);
        }

        /// <summary>
        /// Transforms a detached signature, represented as the entity and the signature raw data
        /// to the associated <see cref="SignedCms"/> instance
        /// </summary>
        /// <param name="content">The raw content of the entity</param>
        /// <param name="signatureBytes">the raw content of the signature</param>
        /// <returns>The corresponding <see cref="SignedCms"/></returns>
        public SignedCms DeserializeDetachedSignature(byte[] content, byte[] signatureBytes)
        {
            SignedCms signature = new SignedCms(CreateDataContainer(content), true);
            signature.Decode(signatureBytes);

            return signature;
        }

        /// <summary>
        /// Tranforms an enveloped signature to the corresponding <see cref="SignedCms"/>
        /// </summary>
        /// <param name="envelopeEntity">The entity containing the enveloped signature</param>
        /// <returns>the corresponding <see cref="SignedCms"/></returns>
        public SignedCms DeserializeEnvelopedSignature(MimeEntity envelopeEntity)
        {
            if (envelopeEntity == null)
            {
                throw new SignatureException(SignatureError.NullEntity);
            }

            if (!SMIMEStandard.IsSignedEnvelope(envelopeEntity))
            {
                throw new SignatureException(SignatureError.NotSignatureEnvelope);
            }

            byte[] envelopeBytes = Convert.FromBase64String(envelopeEntity.Body.Text);

            return DeserializeEnvelopedSignature(envelopeBytes);
        }

        /// <summary>
        /// Tranforms an enveloped signature to the corresponding <see cref="SignedCms"/>
        /// </summary>
        /// <param name="envelopeBytes">The raw data containing the enveloped signature</param>
        /// <returns>the corresponding <see cref="SignedCms"/></returns>
        public SignedCms DeserializeEnvelopedSignature(byte[] envelopeBytes)
        {
            SignedCms signature = new SignedCms();
            signature.Decode(envelopeBytes);

            if (!IsDataContainer(signature.ContentInfo))
            {
                throw new SignatureException(SignatureError.ContentNotDataContainer);
            }

            return signature;
        }

        CmsSigner CreateSigner(X509Certificate2 cert)
        {
            CmsSigner signer = new CmsSigner(cert)
                                   {
                                       IncludeOption = m_certChainInclude,
                                       DigestAlgorithm = ToDigestAlgorithmOid(m_digestAlgorithm)
                                   };

            Pkcs9SigningTime signingTime = new Pkcs9SigningTime();
            signer.SignedAttributes.Add(signingTime);

            return signer;
        }

        //
        // OIDs
        //
        // Hash Algorithms
        //
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
        /// <param name="type">The <see cref="DigestAlgorithm"/> to map to an OID</param>
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