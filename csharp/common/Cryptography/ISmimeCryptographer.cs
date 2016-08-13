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
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;

namespace Health.Direct.Common.Cryptography
{

    /// <summary>
    /// Supports creation of Cryptographers.
    /// </summary>
    public interface ISmimeCryptographer
    {
        /// <summary>
        /// Event to subscribe to for notification of errors.
        /// </summary>
        event Action<ISmimeCryptographer, Exception> Error;

        /// <summary>
        /// Event to subscribe to for notification of warnings. 
        /// </summary>
        event Action<ISmimeCryptographer, string> Warning;

        /// <summary>
        /// Access to the default cryptographer.
        /// If your cryptographer does not implment all of the features or you want to use the default software cryptographer then set it's reference here.
        /// </summary>
        ISmimeCryptographer DefaultCryptographer { get; set; }

        /// <summary>
        /// Gets and sets the <see cref="EncryptionAlgorithm"/> used by this crytographer
        /// </summary>
        EncryptionAlgorithm EncryptionAlgorithm { get; set; }

        /// <summary>
        /// Gets and sets the <see cref="DigestAlgorithm"/> used by this cryptographer
        /// </summary>
        DigestAlgorithm DigestAlgorithm { get; set; }

        /// <summary>
        /// Gets and sets whether this cryptograher includes the epilogue to the multipart message in the signature
        /// </summary>
        /// <remarks>
        /// When signing multipart messages, some mail clients do not include the multipart epilogue
        /// </remarks>
        bool IncludeMultipartEpilogueInSignature { get; set; }

        /// <summary>
        /// Gets and sets if this cryptogrpaher should include the entire certificate chain in the signature.
        /// </summary>
        /// <remarks>
        /// Generally, on the leaf user certificate is included, but including the entire chain can help
        /// recievers validate trust.
        /// </remarks>
        X509IncludeOption IncludeCertChainInSignature { get; set; }



        /// <summary>
        /// Takes a MIME entity and returns a new encrypted MIME entity.
        /// </summary>
        /// <param name="entity">The <see cref="MimeEntity"/> including content headers</param>
        /// <param name="encryptingCertificate">The certificate used for encrytion</param>
        /// <returns>The encrypted <see cref="MimeEntity"/></returns>
        MimeEntity Encrypt(MimeEntity entity, X509Certificate2 encryptingCertificate);


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
        MimeEntity Encrypt(MimeEntity entity, X509Certificate2Collection encryptingCertificates);

        /// <summary>
        /// Decrypt the given encryptedByte array into a MimeEntity
        /// </summary>
        /// <param name="encryptedBytes">source encrypted bytes</param>
        /// <param name="decryptingCertificate">The <see cref="X509Certificate2"/> that encrypted the message</param>
        /// <returns>A <see cref="MimeEntity"/> holding the decrypted message</returns>
        MimeEntity DecryptEntity(byte[] encryptedBytes, X509Certificate2 decryptingCertificate);

        /// <summary>
        /// Creates a detatched signed entity from a <see cref="MimeEntity"/> and a signing certificate
        /// </summary>
        /// <remarks>
        /// Cryptography is performed only on the Mime portions of the message, not the RFC822 headers
        /// Some mail readers ignore the epilogue when calculating signatures!
        /// </remarks>
        /// <param name="message">The <see cref="Message"/> to sign</param>
        /// <param name="signingCertificates">The certificates with which to sign.</param>
        /// <returns>A <see cref="SignedEntity"/> instance holding the signature.</returns>
        SignedEntity Sign(Message message, X509Certificate2Collection signingCertificates);

        /// <summary>
        /// Creates a detached signed entity from a <see cref="MimeEntity"/> and a signing certificate
        /// </summary>
        /// <remarks>
        /// Cryptography is performed only on the Mime portions of the message, not the RFC822 headers
        /// Some mail readers ignore the epilogue when calculating signatures!
        /// </remarks>
        /// <param name="entity">The <see cref="MimeEntity"/> to sign</param>
        /// <param name="signingCertificate">The certificate with which to sign.</param>
        /// <returns>A <see cref="SignedEntity"/> instance holding the signature.</returns>
        SignedEntity Sign(MimeEntity entity, X509Certificate2 signingCertificate);

        /// <summary>
        /// Creates a detached signed entity from a <see cref="MimeEntity"/> and collection of a signing certificates
        /// </summary>
        /// <remarks>
        /// Cryptography is performed only on the Mime portions of the message, not the RFC822 headers
        /// Some mail readers ignore the epilogue when calculating signatures!
        /// </remarks>
        /// <param name="entity">The <see cref="MimeEntity"/> to sign</param>
        /// <param name="signingCertificates">The certificates with which to sign.</param>
        /// <returns>A <see cref="SignedEntity"/> instance holding the signatures.</returns>
        SignedEntity Sign(MimeEntity entity, X509Certificate2Collection signingCertificates);

        /// <summary>
        /// Transforms a <see cref="SignedEntity"/> to the associated <see cref="SignedCms"/> instance
        /// </summary>
        /// <param name="entity">The <see cref="SignedEntity"/> to deserialize</param>
        /// <returns>The corresponding <see cref="SignedCms"/></returns>
        SignedCms DeserializeDetachedSignature(SignedEntity entity);

        /// <summary>
        /// Tranforms an enveloped signature to the corresponding <see cref="SignedCms"/>
        /// </summary>
        /// <param name="envelopeEntity">The entity containing the enveloped signature</param>
        /// <returns>the corresponding <see cref="SignedCms"/></returns>
        SignedCms DeserializeEnvelopedSignature(MimeEntity envelopeEntity);

        /// <summary>
        /// Given a MimeEntity:
        /// - Checks that the MimeEntity is encrypted
        /// - Converts the entity body to bytes...
        /// The returned bytes can then be decrypted using the appropriate private key
        /// </summary>
        /// <param name="encryptedEntity">Entity containinng encrypted data</param>
        /// <returns>encrypted bytes</returns>
        byte[] GetEncryptedBytes(MimeEntity encryptedEntity);
    }
}
