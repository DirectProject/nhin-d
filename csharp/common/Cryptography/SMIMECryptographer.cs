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
using System.Net.Mime;
using System.Security.Cryptography;
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;

using NHINDirect.Mail;
using NHINDirect.Mime;

namespace NHINDirect.Cryptography
{
    /// <summary>
    /// SMIME uses Pkcs (CMS) cryptography
    /// </summary>
    public class SMIMECryptographer
    {
        public static readonly SMIMECryptographer Default = new SMIMECryptographer();

        EncryptionAlgorithm m_encryptionAlgorithm;
        DigestAlgorithm m_digestAlgorithm;
        bool m_includeEpilogue = true;
        X509IncludeOption m_certChainInclude = X509IncludeOption.EndCertOnly;

        public SMIMECryptographer()
            : this(EncryptionAlgorithm.AES128, DigestAlgorithm.SHA1)
        {
        }

        public SMIMECryptographer(EncryptionAlgorithm encryptionAlgorithm, DigestAlgorithm digestAlgorithm)
        {
            m_encryptionAlgorithm = encryptionAlgorithm;
            m_digestAlgorithm = digestAlgorithm;
        }

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
        /// When signing multipart messages, some mail clients do not include the multipart epilogue
        /// </summary>
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
        public MimeEntity Encrypt(MultipartEntity entity, X509Certificate2 encryptingCertificate)
        {
            return Encrypt(entity.ToEntity(), encryptingCertificate);
        }

        public MimeEntity Encrypt(MultipartEntity entity, X509Certificate2Collection encryptingCertificates)
        {
            return Encrypt(entity.ToEntity(), encryptingCertificates);
        }

        public MimeEntity Encrypt(MimeEntity entity, X509Certificate2 encryptingCertificate)
        {
            return Encrypt(entity, new X509Certificate2Collection(encryptingCertificate));
        }

        public MimeEntity Encrypt(MimeEntity entity, X509Certificate2Collection encryptingCertificates)
        {
            if (entity == null)
            {
                throw new EncryptionException(EncryptionError.NullEntity);
            }

            byte[] messageBytes = DefaultSerializer.Default.SerializeToBytes(entity);     // Serialize message out as ASCII encoded...

            byte[] encryptedBytes = Encrypt(messageBytes, encryptingCertificates);

        	var encryptedEntity = new MimeEntity
        	                      	{
        	                      		ContentType = SMIMEStandard.EncryptedEnvelopeContentTypeHeaderValue,
        	                      		ContentTransferEncoding = TransferEncoding.Base64.AsString(),
        	                      		Body = new Body(Convert.ToBase64String(encryptedBytes,
        	                      		                                       Base64FormattingOptions.InsertLineBreaks))
        	                      	};

        	return encryptedEntity;
        }

        public byte[] Encrypt(byte[] content, X509Certificate2 encryptingCertificate)
        {
            return Encrypt(content, new X509Certificate2Collection(encryptingCertificate));
        }

        public byte[] Encrypt(byte[] content, X509Certificate2Collection encryptingCertificates)
        {
            EnvelopedCms envelope = CreateEncryptedEnvelope(content, encryptingCertificates);
            return envelope.Encode();
        }

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

            var recipients = new CmsRecipientCollection(SubjectIdentifierType.IssuerAndSerialNumber, encryptingCertificates);
            var dataEnvelope = new EnvelopedCms(CreateDataContainer(content), ToAlgorithmID(m_encryptionAlgorithm));
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
        public MimeEntity Decrypt(Message message, X509Certificate2 decryptingCertificate)
        {
            return Decrypt(message.ExtractMimeEntity(), decryptingCertificate);
        }

        public MimeEntity Decrypt(MimeEntity encryptedEntity, X509Certificate2 decryptingCertificate)
        {
            if (decryptingCertificate == null)
            {
                throw new EncryptionException(EncryptionError.NoCertificates);
            }
            if (!decryptingCertificate.HasPrivateKey)
            {
                throw new EncryptionException(EncryptionError.NoPrivateKey);
            }
            if (encryptedEntity == null)
            {
                throw new EncryptionException(EncryptionError.NullEntity);
            }

            if (!SMIMEStandard.IsEncrypted(encryptedEntity))
            {
                throw new EncryptionException(EncryptionError.NotEncrypted);
            }

            byte[] encryptedBytes = Convert.FromBase64String(encryptedEntity.Body.Text);
            byte[] decryptedBytes = Decrypt(encryptedBytes, decryptingCertificate);

			//
            // And turn the encrypted bytes back into an entity
            //
            return DefaultSerializer.Default.Deserialize<MimeEntity>(decryptedBytes);
        }

        public byte[] Decrypt(byte[] encryptedContent, X509Certificate2 decryptingCertificate)
        {
            return Decrypt(encryptedContent, new X509Certificate2Collection(decryptingCertificate));
        }

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

        public EnvelopedCms DeserializeEncryptionEnvelope(byte[] encryptedContent)
        {
            if (encryptedContent == null)
            {
                throw new EncryptionException(EncryptionError.NullContent);
            }

            var dataEnvelope = new EnvelopedCms();
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
        public SignedEntity Sign(Message message, X509Certificate2 signingCertificate)
        {
            return Sign(message.ExtractEntityForSignature(m_includeEpilogue), signingCertificate);
        }

        public SignedEntity Sign(Message message, X509Certificate2Collection signingCertificates)
        {
            return Sign(message.ExtractEntityForSignature(m_includeEpilogue), signingCertificates);
        }

        public SignedEntity Sign(MimeEntity entity, X509Certificate2 signingCertificate)
        {
            return Sign(entity, new X509Certificate2Collection(signingCertificate));
        }

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

        public byte[] Sign(byte[] content, X509Certificate2 signingCertificate)
        {
            SignedCms signature = CreateSignature(content, signingCertificate);
            return signature.Encode();
        }

        public byte[] Sign(byte[] content, X509Certificate2Collection signingCertificates)
        {
            SignedCms signature = CreateSignature(content, signingCertificates);
            return signature.Encode();
        }

        public MimeEntity CreateSignatureEntity(byte[] content, X509Certificate2Collection signingCertificates)
        {
            byte[] signatureBytes = Sign(content, signingCertificates);
            //
            // We create an entity to hold a detached signature
            //
        	var signature = new MimeEntity
        	                	{
        	                		ContentType = SMIMEStandard.SignatureContentTypeHeaderValue,
        	                		ContentTransferEncoding = TransferEncoding.Base64.AsString(),
        	                		Body = new Body(Convert.ToBase64String(signatureBytes))
        	                	};

        	return signature;
        }

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
            var signature = new SignedCms(CreateDataContainer(content), true); // true: Detached Signature            
            signature.ComputeSignature(signer, true);   // true: don't prompt the user

            return signature;
        }

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

            var signature = new SignedCms(CreateDataContainer(content), true); // true: Detached Signature            
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

        public void CheckSignature(SignedEntity signedEntity, X509Certificate2 signerCertificate)
        {
            SignedCms signatureEnvelope = DeserializeDetachedSignature(signedEntity);
            CheckSignature(signatureEnvelope.SignerInfos, signerCertificate);
        }

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

        public SignedCms DeserializeDetachedSignature(byte[] content, byte[] signatureBytes)
        {
            var signature = new SignedCms(CreateDataContainer(content), true);
            signature.Decode(signatureBytes);

            return signature;
        }

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

        public SignedCms DeserializeEnvelopedSignature(byte[] envelopeBytes)
        {
            var signature = new SignedCms();
            signature.Decode(envelopeBytes);

            if (!IsDataContainer(signature.ContentInfo))
            {
                throw new SignatureException(SignatureError.ContentNotDataContainer);
            }

            return signature;
        }

        CmsSigner CreateSigner(X509Certificate2 cert)
        {
            var signer = new CmsSigner(cert)
                         	{
                         		IncludeOption = m_certChainInclude,
                         		DigestAlgorithm = ToDigestAlgorithmOid(m_digestAlgorithm)
                         	};

        	var signingTime = new Pkcs9SigningTime();
            signer.SignedAttributes.Add(signingTime);

            return signer;
        }

        //
        // OIDs
        //
        // Hash Algorithms
        //
		public static class CryptoOids
		{
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
		}

    	static Oid ToDigestAlgorithmOid(DigestAlgorithm type)
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

    	static AlgorithmIdentifier ToAlgorithmID(EncryptionAlgorithm type)
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
