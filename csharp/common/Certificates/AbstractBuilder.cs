/* 
 Copyright (c) 2017, Direct Project
 All rights reserved.

 Authors:
    Dávid Koronthály    koronthaly@hotmail.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Security.Cryptography.X509Certificates;
using Org.BouncyCastle.Asn1;
using Org.BouncyCastle.Asn1.X509;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Prng;
using Org.BouncyCastle.Math;
using Org.BouncyCastle.Security;
using Org.BouncyCastle.X509.Extension;
using X509Certificate = Org.BouncyCastle.X509.X509Certificate;

namespace Health.Direct.Common.Certificates
{
    /// <summary>
    /// Abstract X509 certificate builder, using Bouncy Castle library. Inspired by
    /// http://blog.differentpla.net/blog/2013/03/18/using-bouncy-castle-from-net
    /// </summary>
    public abstract class AbstractBuilder
    {
        /// <summary>
        /// Use CryptoApi random number generator. Initialization is costly, so share it with all builders.
        /// </summary>
        protected static readonly SecureRandom SecureRandom = new SecureRandom(new CryptoApiRandomGenerator());

        /// <summary>
        /// Name of the signature algorithm (see RFC 5280 section 4.1.1.2); defaults to "SHA256withRSA".
        /// </summary>
        public string SignatureAlgorithmName { get; set; } = "SHA256withRSA";

        /// <summary>
        /// Certificate authority, used by the builder to generate a new certificate.
        /// Can be null if there is no issuer (e.g. when generating a self-signed certificate).
        /// </summary>
        public X509Certificate Issuer { get; }

        /// <summary>
        /// Issuer's key pair. Can be null if there is no issuer.
        /// </summary>
        public virtual AsymmetricCipherKeyPair IssuerKeyPair { get; }

        /// <summary>
        /// Issuer's serial number. Can be null if there is no issuer.
        /// </summary>
        public virtual BigInteger IssuerSerialNumber => Issuer?.SerialNumber;

        /// <summary>
        /// Issuer's distinguished name. See RFC 5280 section 4.1.2.4. Issuer.
        /// Can be null if there is no issuer.
        /// </summary>
        public virtual X509Name IssuerDN => Issuer?.SubjectDN;

        /// <summary>
        /// Url to retrieve public key of the issuer (using access method id-ad-caIssuers). 
        /// See RFC 5280 section 4.2.2.1. Authority Information Access
        /// </summary>
        public Uri AuthorityInformationAccessUri { get; set; }

        /// <summary>
        /// Create a new builder.
        /// </summary>
        /// <param name="issuer">Certification authority, used to issue a new certificate. Null is allowed.</param>
        protected AbstractBuilder(X509Certificate2 issuer)
        {
            // Self-signed certificate does not have a separate issuer.
            if (issuer == null)
            {
                return;
            }

            // Issuer must be a valid certificate authority.
            if (!issuer.IsCertificateAuthority() || !issuer.HasValidDateRange() || !issuer.HasPrivateKey)
            {
                throw new ArgumentException("Not a valid certificate authority.", nameof(issuer));
            }

            // Convert to Bouncy Castle representation;
            Issuer = DotNetUtilities.FromX509Certificate(issuer);
            IssuerKeyPair = DotNetUtilities.GetKeyPair(issuer.PrivateKey);
        }

        /// <summary>
        /// Encode AuthorityInformationAccessUri to a form required by the extension.
        /// </summary>
        /// <returns>DER encoded sequence or null.</returns>
        protected DerSequence GetAuthorityInfoAccessEncoded()
        {
            if (AuthorityInformationAccessUri == null || !AuthorityInformationAccessUri.IsAbsoluteUri)
            {
                return null;
            }

            var location = new GeneralName(GeneralName.UniformResourceIdentifier, new DerIA5String(AuthorityInformationAccessUri.AbsoluteUri));
            var issuers = new AccessDescription(AccessDescription.IdADCAIssuers, location);
            return new DerSequence(new Asn1EncodableVector(issuers));
        }

        /// <summary>
        /// Extract issuer alternative name is available.
        /// </summary>
        /// <returns>Issuer alternative name or null.</returns>
        protected GeneralNames GetIssuerAlternativeName()
        {
            Asn1OctetString extensionValue = Issuer?.GetExtensionValue(new DerObjectIdentifier(X509Extensions.SubjectAlternativeName.Id));
            return extensionValue == null ? null : GeneralNames.GetInstance(X509ExtensionUtilities.FromExtensionValue(extensionValue));
        }
    }
}
