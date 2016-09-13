/* 
 Copyright (c) 2016, Direct Project
 All rights reserved.

 Authors:
    Joe Shook      Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Mime;
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;
using Net.Pkcs11Interop.Common;
using Net.Pkcs11Interop.HighLevelAPI;
using Org.BouncyCastle.Asn1;
using Org.BouncyCastle.Asn1.Cms;
using Org.BouncyCastle.Asn1.Pkcs;
using Org.BouncyCastle.Asn1.Utilities;
using Org.BouncyCastle.Asn1.X509;
using Org.BouncyCastle.Cms;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Digests;
using Org.BouncyCastle.Crypto.IO;
using Org.BouncyCastle.X509.Store;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.Security;
using Org.BouncyCastle.Utilities.IO;
using Health.Direct.Common.Cryptography;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;
using EncryptionException = Health.Direct.Common.Cryptography.EncryptionException;
using IssuerAndSerialNumber = Org.BouncyCastle.Asn1.Cms.IssuerAndSerialNumber;
using RecipientInfo = Org.BouncyCastle.Asn1.Cms.RecipientInfo;
using KeyTransRecipientInfo = Org.BouncyCastle.Asn1.Cms.KeyTransRecipientInfo;
using SignedData = Org.BouncyCastle.Asn1.Cms.SignedData;
using Time = Org.BouncyCastle.Asn1.Cms.Time;
using X509Certificate = Org.BouncyCastle.X509.X509Certificate;

namespace Health.Direct.Hsm
{

    public class HsmCryptographer : SMIMECryptographerBase, ISmimeCryptographer, IDisposable
    {
        private Session m_sessionApplication;
        private Pkcs11 m_pkcs11;
        private TokenSettings m_tokenSettings;
        private Slot m_slot;
        private bool m_loggedIn;
        /// <summary>
        /// Flag indicating whether instance has been disposed
        /// </summary>
        private bool m_disposed;


        /// <inheritdoc />
        public event Action<ISmimeCryptographer, Exception> Error;

        /// <inheritdoc />
        public event Action<ISmimeCryptographer, string> Warning;

        public HsmCryptographer() { }

        public TokenSettings TokenSettings
        {
            get { return m_tokenSettings; }
            set { m_tokenSettings = value; }
        }

        /// <summary>
        /// Initializes an instance, specifying the encryption and digest algorithm to use.
        /// </summary>
        /// <param name="settings"><see cref="TokenSettings"/></param>
        public void Init(TokenSettings settings)
        {
            lock (settings)
            {
                try
                {
                    m_tokenSettings = settings;
                    EncryptionAlgorithm = settings.DefaultEncryption;
                    DigestAlgorithm = settings.DefaultDigest;
                    IncludeMultipartEpilogueInSignature = true;
                    IncludeCertChainInSignature = X509IncludeOption.EndCertOnly;

                    InitializePkcs11(settings);
                    EnsureLoggedInSession(settings);
                }
                catch (Exception ex)
                {
                    Error.NotifyEvent(this, ex);
                }
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="settings"></param>
        private void InitializePkcs11(TokenSettings settings)
        {
            m_pkcs11 = new Pkcs11(settings.Pkcs11LibraryPath, settings.UseOsLocking);
            m_slot = Pkcs11Util.FindSlot(m_pkcs11, settings);

            if (m_slot == null)
            {
                throw new HsmInitException(
                    string.Format(
                        "Did not find an available slot with TokenLable:{0}",
                        settings.TokenLabel));
            }
        }

        /// <summary>
        /// From PKCS#11 V2.20: CRYPTOGRAPHIC TOKEN INTERFACE STANDARD, section 6.7.4 Session events.
        /// 
        /// In Cryptoki, all sessions that an application has with a token must have the same
        /// login/logout status(i.e., for a given application and token, one of the following holds: all
        /// sessions are public sessions; all sessions are SO sessions; or all sessions are user
        /// sessions). When an application’s session logs into a token, all of that application’s
        /// sessions with that token become logged in, and when an application’s session logs out of
        ///  a token, all of that application’s sessions with that token become logged out. Similarly,
        /// for example, if an application already has a R/O user session open with a token, and then
        /// opens a R/W session with that token, the R/W session is automatically logged in. 
        /// </summary>
        /// <param name="settings"></param>
        private void EnsureLoggedInSession(TokenSettings settings)
        {
            m_sessionApplication = m_slot.OpenSession(true);
            var sessionInfo = m_sessionApplication.GetSessionInfo();
            //
            // If another session has logged in then we should also be logged in.
            //
            if (sessionInfo.State != CKS.CKS_RO_USER_FUNCTIONS)
            {
                m_sessionApplication.Login(CKU.CKU_USER, settings.NormalUserPin);
            }

            m_loggedIn = true;
        }

        /// <summary>
        /// Default cryptographer is the default Direct Project.
        /// If your implementation is full featured then set this to yourself.
        /// </summary>
        public ISmimeCryptographer DefaultCryptographer { get; set; }

        public MimeEntity Encrypt(MimeEntity entity, X509Certificate2 encryptingCertificate)
        {
            throw new NotImplementedException();
        }

        public MimeEntity Encrypt(MimeEntity entity, X509Certificate2Collection encryptingCertificates)
        {
            throw new NotImplementedException();
        }

        public MimeEntity DecryptEntity(byte[] encryptedBytes, X509Certificate2 decryptingCertificate)
        {
            try
            {
                if (decryptingCertificate == null)
                {
                    throw new EncryptionException(EncryptionError.NoCertificates);
                }

                // TODO: introduce buffering if you are using large files
                // CMSEnvelopeData is a PKCS# structure  rfc4134
                var envelopedData = new CmsEnvelopedData(encryptedBytes);
                var envData = EnvelopedData.GetInstance(envelopedData.ContentInfo.Content);

                using (var session = GetSession())
                {
                    if (session == null)
                    {
                        return null;
                    }

                    foreach (Asn1Sequence asn1Set in envData.RecipientInfos)
                    {
                        var recip = RecipientInfo.GetInstance(asn1Set);
                        var keyTransRecipientInfo = KeyTransRecipientInfo.GetInstance(recip.Info);

                        var sessionKey = Pkcs11Util.Decrypt(session, keyTransRecipientInfo, decryptingCertificate);

#if DEBUG
                        Console.WriteLine(Asn1Dump.DumpAsString(envData));
#endif
                        if (sessionKey == null)
                        {
                            continue;
                        }

                        var recipientId = new RecipientID();
                        var issuerAndSerialNumber = (IssuerAndSerialNumber)keyTransRecipientInfo.RecipientIdentifier.ID;
                        recipientId.Issuer = issuerAndSerialNumber.Name;
                        recipientId.SerialNumber = issuerAndSerialNumber.SerialNumber.Value;
                        var recipientInformation = envelopedData.GetRecipientInfos().GetRecipients(recipientId);
                        var recipients = new ArrayList(recipientInformation);

                        //
                        // read the encrypted content info
                        //
                        var encInfo = envData.EncryptedContentInfo;
                        var encAlg = encInfo.ContentEncryptionAlgorithm;
                        var readable = new CmsProcessableByteArray(encInfo.EncryptedContent.GetOctets());
                        var keyParameter = ParameterUtilities.CreateKeyParameter(encAlg.Algorithm.Id, sessionKey);

                        // Todo: does this work with multi recipient?
                        foreach (RecipientInformation recipient in recipients)
                        {
                            var cmsReadable = GetReadable(keyParameter, encAlg, readable);
                            var cmsTypedStream = new CmsTypedStream(cmsReadable.GetInputStream());
                            var contentBytes = StreamToByteArray(cmsTypedStream.ContentStream);
                            var mimeEntity = MimeSerializer.Default.Deserialize<MimeEntity>(contentBytes);
                            return mimeEntity;
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Error.NotifyEvent(this, ex);
            }

            return null;
        }

        public SignedEntity Sign(Message message, X509Certificate2Collection signingCertificates)
        {
            return Sign(message.ExtractEntityForSignature(IncludeMultipartEpilogueInSignature), signingCertificates);
        }

        public CmsReadable GetReadable(
            KeyParameter sKey,
            Org.BouncyCastle.Asn1.X509.AlgorithmIdentifier algorithm,
            CmsProcessableByteArray readable)
        {
            IBufferedCipher cipher;

            try
            {
                cipher = CipherUtilities.GetCipher(algorithm.Algorithm);
                var asn1Enc = algorithm.Parameters;
                var asn1Params = asn1Enc == null ? null : asn1Enc.ToAsn1Object();
                ICipherParameters cipherParameters = sKey;

                if (asn1Params != null && !(asn1Params is Asn1Null))
                {
                    cipherParameters = ParameterUtilities.GetCipherParameters(
                        algorithm.Algorithm, cipherParameters, asn1Params);
                }
                else
                {
                    string alg = algorithm.Algorithm.Id;
                    if (alg.Equals(CmsEnvelopedGenerator.DesEde3Cbc)
                        || alg.Equals(CmsEnvelopedGenerator.IdeaCbc)
                        || alg.Equals(CmsEnvelopedGenerator.Cast5Cbc))
                    {
                        cipherParameters = new ParametersWithIV(cipherParameters, new byte[8]);
                    }
                }

                cipher.Init(false, cipherParameters);
            }
            catch (SecurityUtilityException e)
            {
                throw new CmsException("couldn't create cipher.", e);
            }
            catch (InvalidKeyException e)
            {
                throw new CmsException("key invalid in message.", e);
            }
            catch (IOException e)
            {
                throw new CmsException("error decoding algorithm parameters.", e);
            }

            try
            {
                return new CmsProcessableInputStream(
                    new CipherStream(readable.GetInputStream(), cipher, null));
            }
            catch (IOException e)
            {
                throw new CmsException("error reading content.", e);
            }
        }

        public static byte[] StreamToByteArray(Stream inStream)
        {
            return Streams.ReadAll(inStream);
        }

        public SignedEntity Sign(MimeEntity entity, X509Certificate2 signingCertificate)
        {
            try
            {
                return Sign(entity, new X509Certificate2Collection(signingCertificate));
            }
            catch (Exception ex)
            {
                Error.NotifyEvent(this, ex);
            }

            return null;
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
        private byte[] Sign(byte[] content, X509Certificate2Collection signingCertificates)
        {
#if DEBUG

            Console.WriteLine(signingCertificates[0].ToString(true));
#endif
            using (var session = GetSession())
            {
                if (session == null)
                {
                    return null;
                }

                var signature = CreateSignature(session, content, signingCertificates);

                // Construct top level ContentInfo
                var contentInfo = new Org.BouncyCastle.Asn1.Pkcs.ContentInfo(
                    new DerObjectIdentifier(PkcsObjectIdentifiers.SignedData.Id),
                    signature);

                return contentInfo.GetDerEncoded();
            }
        }

        private Session GetSession()
        {
            if (m_slot != null && m_loggedIn && !m_sessionApplication.Disposed)
            {
                try
                {
                    // normal operation
                    return m_slot?.OpenSession(true);
                }
                catch (Exception ex)
                {
                    // probably a network outage [Method C_OpenSession returned CKR_DEVICE_ERROR]
                    Error.NotifyEvent(this, ex);

                    lock (m_tokenSettings)
                    {
                        try
                        {
                            if (m_sessionApplication.GetSessionInfo().State != CKS.CKS_RO_USER_FUNCTIONS)
                            {
                                // can talk to token but our application session is not logged in
                                m_loggedIn = false;
                            }
                        }
                        catch (Exception)
                        {
                            // [Method C_GetSessionInfo returned CKR_DEVICE_ERROR]
                            m_loggedIn = false;
                        }
                    }

                    return null;
                }
            }

            // recover from outage
            lock (m_tokenSettings)
            {
                try
                {
                    Warning.NotifyEvent(this, "Attempting to connect to Token");

                    m_pkcs11?.Dispose();
                    m_slot = null;
                    m_sessionApplication = null;

                    InitializePkcs11(m_tokenSettings);
                    EnsureLoggedInSession(m_tokenSettings);
                    return m_slot.OpenSession(true);

                }
                catch (Exception ex)
                {
                    Error.NotifyEvent(this, ex);

                    return null;
                }
            }
        }

        //var bcCert = CertificateUtilities.BuildBouncyCastleCollection(signingCertificates);
        //ICollection<Org.BouncyCastle.X509.X509Certificate>

        private SignedData CreateSignature(
            Session session,
            byte[] content,
            X509Certificate2Collection signingCertificates)
        {
            var digestOid = ToDigestAlgorithmOid(DigestAlgorithm).Value;
            var hashGenerator = GetHashGenerator(DigestAlgorithm);
            var dataHash = ComputeDigest(hashGenerator, content);

            // Construct SignerInfo.signedAttrs
            var signedAttributesVector = new Asn1EncodableVector();

            // Add PKCS#9 contentType signed attribute as Data
            signedAttributesVector.Add(
                new Org.BouncyCastle.Asn1.Cms.Attribute(
                    new DerObjectIdentifier(PkcsObjectIdentifiers.Pkcs9AtContentType.Id),
                    new DerSet(new DerObjectIdentifier(PkcsObjectIdentifiers.Data.Id))));

            // Add PKCS#9 messageDigest signed attribute with hash der string encoded
            signedAttributesVector.Add(
                new Org.BouncyCastle.Asn1.Cms.Attribute(
                    new DerObjectIdentifier(PkcsObjectIdentifiers.Pkcs9AtMessageDigest.Id),
                    new DerSet(new DerOctetString(dataHash))));

            // Add PKCS#9 signingTime signed attribute
            signedAttributesVector.Add(
                new Org.BouncyCastle.Asn1.Cms.Attribute(
                    new DerObjectIdentifier(PkcsObjectIdentifiers.Pkcs9AtSigningTime.Id),
                    new DerSet(new Time(new DerUtcTime(DateTime.UtcNow)))));

            var signedAttributes = new DerSet(signedAttributesVector);



            // Sign SignerInfo.signedAttrs with PKCS#1 v1.5 RSA signature using private key stored on PKCS#11 compatible device
            byte[] pkcs1Digest = ComputeDigest(hashGenerator, signedAttributes.GetDerEncoded());
            byte[] pkcs1DigestInfo = CreateDigestInfo(pkcs1Digest, digestOid);

            // Construct SignedData.digestAlgorithms
            var digestAlgorithmsVector = new Asn1EncodableVector();
            // Construct SignedData.certificates
            var certificatesVector = new Asn1EncodableVector();
            // Construct SignedData.signerInfos
            var signerInfosVector = new Asn1EncodableVector();

            // Construct SignedData.encapContentInfo
            var encapContentInfo = new Org.BouncyCastle.Asn1.Cms.ContentInfo(
                new DerObjectIdentifier(PkcsObjectIdentifiers.Data.Id),
                null); //Always a detached signature.


            foreach (var signingCertificate in signingCertificates)
            {
                var bcSigningCertificate = CertificateUtilities.ToBouncyCastleObject(signingCertificate.RawData);
                // Get public key from certificate
                var pubKeyParams = bcSigningCertificate.GetPublicKey(); //AsymmetricKeyParameter
                if (!(pubKeyParams is RsaKeyParameters))
                    throw new NotSupportedException("Unsupported keys.  Currently supporting RSA keys only.");

                var rsaPubKeyParams = (RsaKeyParameters)pubKeyParams;

                byte[] pkcs1Signature;

                if (signingCertificate.HasPrivateKey)
                {
                    pkcs1Signature = GeneratePkcs1Signature(signingCertificate, pkcs1DigestInfo);
                }
                else
                {
                    pkcs1Signature = GeneratePkcs1Signature(session, rsaPubKeyParams, bcSigningCertificate, pkcs1DigestInfo);
                }

                // Construct SignerInfo
                var signerInfo = new Org.BouncyCastle.Asn1.Cms.SignerInfo(
                    new SignerIdentifier(new IssuerAndSerialNumber(bcSigningCertificate.IssuerDN, bcSigningCertificate.SerialNumber)),
                    new Org.BouncyCastle.Asn1.X509.AlgorithmIdentifier(new DerObjectIdentifier(digestOid), null),
                    signedAttributes,
                    new Org.BouncyCastle.Asn1.X509.AlgorithmIdentifier(new DerObjectIdentifier(PkcsObjectIdentifiers.RsaEncryption.Id), null),
                    new DerOctetString(pkcs1Signature),
                    null);

                digestAlgorithmsVector.Add(new Org.BouncyCastle.Asn1.X509.AlgorithmIdentifier(new DerObjectIdentifier(digestOid), null));
                certificatesVector.Add(X509CertificateStructure.GetInstance(Asn1Object.FromByteArray(bcSigningCertificate.GetEncoded())));
                signerInfosVector.Add(signerInfo.ToAsn1Object());
            }

            // Construct SignedData
            var signedData = new SignedData(
                new DerSet(digestAlgorithmsVector),
                encapContentInfo,
                new BerSet(certificatesVector),
                null,
                new DerSet(signerInfosVector));

            return signedData;
        }

        private static byte[] GeneratePkcs1Signature(Session session, RsaKeyParameters rsaPubKeyParams,
            X509Certificate bcSigningCertificate, byte[] pkcs1DigestInfo)
        {
            //Correlate with HSM
            var privKeySearchTemplate = new List<ObjectAttribute>
            {
                new ObjectAttribute(CKA.CKA_CLASS, CKO.CKO_PRIVATE_KEY),
                new ObjectAttribute(CKA.CKA_KEY_TYPE, CKK.CKK_RSA),
                new ObjectAttribute(CKA.CKA_MODULUS, rsaPubKeyParams.Modulus.ToByteArrayUnsigned()),
                new ObjectAttribute(CKA.CKA_PUBLIC_EXPONENT, rsaPubKeyParams.Exponent.ToByteArrayUnsigned())
            };

            var privateKeyHandles = session.FindAllObjects(privKeySearchTemplate);

            if (privateKeyHandles.Count != 1)
                throw new HsmObjectNotFoundException(
                    string.Format(
                        "Private key correlation failed for signing cert \r\n{0} \r\n CKA_MODULUS: {1} \r\n CKA_PUBLIC_EXPONENT {2},",
                        bcSigningCertificate,
                        rsaPubKeyParams.Modulus,
                        rsaPubKeyParams.Exponent));

            byte[] pkcs1Signature;

            using (var mechanism = new Mechanism(CKM.CKM_RSA_PKCS))
            {
                pkcs1Signature = session.Sign(mechanism, privateKeyHandles.Single(), pkcs1DigestInfo);
            }
            return pkcs1Signature;
        }

        private static byte[] GeneratePkcs1Signature(X509Certificate2 signingCertificate, byte[] pkcs1DigestInfo)
        {
            AsymmetricCipherKeyPair key = DotNetUtilities.GetKeyPair(signingCertificate.PrivateKey);
            ISigner signer = SignerUtilities.GetSigner("RSA");
            signer.Init(true, key.Private);
            signer.BlockUpdate(pkcs1DigestInfo, 0, pkcs1DigestInfo.Length);

            return signer.GenerateSignature();
        }

        /// <summary>
        /// Computes hash of the data
        /// </summary>
        /// <param name="hashGenerator"><see cref="IDigest"/> algorithm implementation</param>
        /// <param name="content">The <c>byte</c> array to sign</param>
        /// <returns>Hash of data</returns>
        private byte[] ComputeDigest(IDigest hashGenerator, byte[] content)
        {
            if (hashGenerator == null)
                throw new ArgumentNullException(nameof(hashGenerator));

            if (content == null)
                throw new ArgumentNullException(nameof(content));

            var hash = new byte[hashGenerator.GetDigestSize()];

            hashGenerator.Reset();
            hashGenerator.BlockUpdate(content, 0, content.Length);
            hashGenerator.DoFinal(hash, 0);

            return hash;
        }

        /// <summary>
        /// Returns implementation of specified digest algorithm
        /// </summary>
        /// <param name="algorithm">Digest algorithm</param>
        /// <returns>Implementation of specified digest algorithm</returns>
        private static IDigest GetHashGenerator(DigestAlgorithm algorithm)
        {
            IDigest digest;

            switch (algorithm)
            {
                default:
                    throw new NotSupportedException("Unsupported hash algorithm");
                case DigestAlgorithm.SHA1:
                    digest = new Sha1Digest();
                    break;
                case DigestAlgorithm.SHA256:
                    digest = new Sha256Digest();
                    break;
                case DigestAlgorithm.SHA384:
                    digest = new Sha384Digest();
                    break;
                case DigestAlgorithm.SHA512:
                    digest = new Sha512Digest(); break;
            }

            return digest;
        }

        /// <summary>
        /// Creates PKCS#1 DigestInfo
        /// </summary>
        /// <param name="digest">Hash value</param>
        /// <param name="digestOid">Hash algorithm OID</param>
        /// <returns>DER encoded PKCS#1 DigestInfo</returns>
        private static byte[] CreateDigestInfo(byte[] digest, string digestOid)
        {
            var derObjectIdentifier = new DerObjectIdentifier(digestOid);
            var algorithmIdentifier = new Org.BouncyCastle.Asn1.X509.AlgorithmIdentifier(derObjectIdentifier, null);
            DigestInfo digestInfo = new DigestInfo(algorithmIdentifier, digest);
            return digestInfo.GetDerEncoded();
        }

        public SignedEntity Sign(MimeEntity entity, X509Certificate2Collection signingCertificates)
        {
            if (entity == null)
            {
                throw new Common.Cryptography.SignatureException(SignatureError.NullEntity);
            }

            byte[] entityBytes = MimeSerializer.Default.SerializeToBytes(entity);
            MimeEntity signature = CreateSignatureEntity(entityBytes, signingCertificates);

            if (signature == null)
            {
                return null;
            }

            return new SignedEntity(DigestAlgorithm, entity, signature);
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
        private MimeEntity CreateSignatureEntity(byte[] content, X509Certificate2Collection signingCertificates)
        {
            byte[] signatureBytes = Sign(content, signingCertificates);

            if (signatureBytes == null)
            {
                return null;
            }
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
        /// Not Implemented.
        /// </summary>
        /// <param name="entity">The <see cref="SignedEntity"/> to deserialize</param>
        /// <returns>The corresponding <see cref="SignedCms"/></returns>
        public SignedCms DeserializeDetachedSignature(SignedEntity entity)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// Not Implemented.
        /// </summary>
        /// <param name="envelopeEntity">The entity containing the enveloped signature</param>
        /// <returns>the corresponding <see cref="SignedCms"/></returns>
        public SignedCms DeserializeEnvelopedSignature(MimeEntity envelopeEntity)
        {
            throw new NotImplementedException();
        }

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        protected virtual void Dispose(bool disposing)
        {
            if (!m_disposed)
            {
                if (disposing)
                {
                    m_pkcs11?.Dispose();
                }
            }

            // Dispose unmanaged objects
            m_disposed = true;
        }

        /// <summary>
        /// Class destructor that disposes object if caller forgot to do so
        /// </summary>
        ~HsmCryptographer()
        {
            Dispose(false);
        }
    }

    public class HsmObjectNotFoundException : Exception
    {
        public HsmObjectNotFoundException(string message) : base(message)
        {
        }
    }

    public class HsmInitException : Exception
    {
        public HsmInitException(string message) : base(message)
        {
        }
    }
}
