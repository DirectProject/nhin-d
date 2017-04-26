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
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Extensions;
using Org.BouncyCastle.Asn1;
using Org.BouncyCastle.Asn1.X509;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Generators;
using Org.BouncyCastle.Crypto.Operators;
using Org.BouncyCastle.Math;
using Org.BouncyCastle.Pkcs;
using Org.BouncyCastle.X509;
using Org.BouncyCastle.X509.Extension;

namespace Health.Direct.Common.Certificates
{
    /// <summary>
    /// Build X509 certificate that satisfies requirements of Direct Project.
    /// See http://blog.differentpla.net/blog/2013/03/18/using-bouncy-castle-from-net
    /// </summary>
    public class CertificateBuilder : AbstractBuilder
    {
        public const int DefaultCertificateAuthorityKeyStrength = 4096;
        public const int DefaultCertificateKeyStrength = 2048;
        public const int DefaultValidityPeriodInMonths = 18;

        // Implemented using backing field to support delayed initialization.
        private AsymmetricCipherKeyPair _subjectKeyPair;

        /// <summary>
        /// Returns true if the builder is initialized to create a certificate authority.
        /// </summary>
        public bool IsCertificateAuthority
        {
            get { return BasicConstraints.IsCA(); }
        }

        /// <summary>
        /// Returns true is the builder is initialized to create a self-signed certificate.
        /// </summary>
        public bool IsSelfSigned
        {
            get { return Issuer == null; }
        }

        /// <summary>
        /// Issuer's serial number. For self-signed certificates,
        /// it is identical to the value of the SerialNumber property.
        /// </summary>
        public override BigInteger IssuerSerialNumber
        {
            get { return IsSelfSigned ? SerialNumber : base.IssuerSerialNumber; }
        }

        /// <summary>
        /// Issuer's distinguished name. For self-signed certificates,
        /// it is identical to the value of the SubjectDN property.
        /// </summary>
        public override X509Name IssuerDN
        {
            get { return IsSelfSigned ? SubjectDN : base.IssuerDN; }
        }

        /// <summary>
        /// Issuer's serial number. For self-signed certificates,
        /// it is identical to the value of the SubjectKeyPair property.
        /// </summary>
        public override AsymmetricCipherKeyPair IssuerKeyPair
        {
            get { return IsSelfSigned ? SubjectKeyPair : base.IssuerKeyPair; }
        }

        /// <summary>
        /// Basic Constraints, as defined by RFC 5280 section 4.2.1.9.
        /// </summary>
        public BasicConstraints BasicConstraints { get; }

        /// <summary>
        /// Size of the public key in bits. Defaults to 4096 for
        /// certificate authorities and 2048 for end entities.
        /// </summary>
        public int KeyStrength { get; set; }

        /// <summary>
        /// Serial Number, as defined by RFC 5280 section 4.1.2.2.
        /// </summary>
        public BigInteger SerialNumber { get; set; }

        /// <summary>
        /// Not Before, as defined by RFC 5280 section 4.1.2.5.
        /// </summary>
        public DateTime NotBefore { get; set; }

        /// <summary>
        /// Not After, as defined by RFC 5280 section 4.1.2.5.
        /// </summary>
        public DateTime NotAfter { get; set; }

        /// <summary>
        /// Key Usage, as defined by RFC 5280 section 4.2.1.3.
        /// </summary>
        public X509KeyUsageFlags KeyUsage { get; set; }

        /// <summary>
        /// Certificate Policies, as defined by RFC 5280 section 4.2.1.4.
        /// </summary>
        public IList<string> Policies { get; }

        /// <summary>
        /// CRL Distribution Points, as defined by RFC 5280 section 4.2.1.13.
        /// Only a single HTTP URL is supported.
        /// </summary>
        public Uri CrlDistributionPointUri { get; set; }

        /// <summary>
        /// Subject Alternative Name, as defined by RFC 5280 section 4.2.1.6.
        /// </summary>
        public GeneralNames SubjectAlternativeName { get; set; }

        /// <summary>
        /// Subject's distinguished name. See RFC 5280 section 4.1.2.6. Subject
        /// </summary>
        public X509Name SubjectDN { get; set; }

        /// <summary>
        /// Key pair of the new certificate. Created on demand, using KeyStrength bits.
        /// </summary>
        public AsymmetricCipherKeyPair SubjectKeyPair
        {
            get
            {
                if (_subjectKeyPair == null)
                {
                    var keyPairGenerator = new RsaKeyPairGenerator();
                    keyPairGenerator.Init(new KeyGenerationParameters(SecureRandom, KeyStrength));
                    _subjectKeyPair = keyPairGenerator.GenerateKeyPair();
                }

                return _subjectKeyPair;
            }
        }

        private CertificateBuilder(X509Certificate2 issuer, bool certificateAuthority, int pathLenConstraint = 0)
            : base(issuer)
        {
            // Initialize key strength
            KeyStrength = certificateAuthority ? DefaultCertificateAuthorityKeyStrength : DefaultCertificateKeyStrength;

            // Initialize serial number; can be changed.
            SerialNumber = BigInteger.ProbablePrime(120, SecureRandom);

            // Decide what kind of certificate will be issued.
            BasicConstraints = certificateAuthority
                ? new BasicConstraints(pathLenConstraint)
                : new BasicConstraints(false);

            // Validity period.
            NotBefore = DateTime.UtcNow;
            NotAfter = NotBefore.AddMonths(DefaultValidityPeriodInMonths);

            // Certificate policies; see DirectTrustCertificatePolicies
            Policies = new List<string>();
        }

        /// <summary>
        /// Create a new CertificateBuilder, initialized to generate a self-signed certification authority.
        /// </summary>
        /// <param name="pathLenConstraint">
        /// Limits the number of intermediate certificates that may follow this certificate in a valid certification path.
        /// </param>
        public CertificateBuilder(int pathLenConstraint)
            : this(null, true, pathLenConstraint)
        {
            // Certificate authority is used to issue other certificates and CRL.
            KeyUsage = X509KeyUsageFlags.KeyCertSign | X509KeyUsageFlags.CrlSign;
        }

        /// <summary>
        /// Create a new CertificateBuilder, initialized to generate an intermediate certification authority, signed by the issuer.
        /// </summary>
        /// <param name="issuer">Certificate authority used to issue the certificate.</param>
        /// <param name="pathLenConstraint">
        /// Limits the number of intermediate certificates that may follow this certificate in a valid certification path.
        /// </param>
        public CertificateBuilder(X509Certificate2 issuer, int pathLenConstraint)
            : this(issuer, true, pathLenConstraint)
        {
            if (issuer == null)
            {
                throw new ArgumentNullException(nameof(issuer));
            }

            // Certificate authority is used to issue other certificates and CRL.
            KeyUsage = X509KeyUsageFlags.KeyCertSign | X509KeyUsageFlags.CrlSign;
        }

        /// <summary>
        /// Create a new CertificateBuilder, initialized to generate end entity certificate, signed by the issuer.
        /// </summary>
        /// <param name="issuer">Certificate authority used to issue the certificate.</param>
        public CertificateBuilder(X509Certificate2 issuer)
            : this(issuer, false)
        {
            if (issuer == null)
            {
                throw new ArgumentNullException(nameof(issuer));
            }

            // Direct end entity certificates are used to sign and encrypt email.
            KeyUsage = X509KeyUsageFlags.DigitalSignature | X509KeyUsageFlags.KeyEncipherment;
        }

        /// <summary>
        /// Initialize the SubjectAlternativeName to a domain.
        /// </summary>
        /// <param name="domain">Domain (host) name. Not a full URL.</param>
        public void SetSubjectAlternativeNameToDomain(string domain)
        {
            if (domain.IsNullOrWhiteSpace())
            {
                throw new ArgumentNullException(nameof(domain));
            }

            if (SubjectAlternativeName != null)
            {
                throw new InvalidOperationException("SubjectAlternativeName was already set.");
            }

            SubjectAlternativeName = new GeneralNames(new GeneralName(GeneralName.DnsName, new DerIA5String(domain)));
        }

        /// <summary>
        /// Initialize the SubjectAlternativeName to an email.
        /// </summary>
        /// <param name="emailAddress">Email address.</param>
        public void SetSubjectAlternativeNameToEmail(string emailAddress)
        {
            if (emailAddress.IsNullOrWhiteSpace())
            {
                throw new ArgumentNullException(nameof(emailAddress));
            }

            if (SubjectAlternativeName != null)
            {
                throw new InvalidOperationException("SubjectAlternativeName was already set.");
            }

            SubjectAlternativeName = new GeneralNames(new GeneralName(GeneralName.Rfc822Name, new DerIA5String(emailAddress)));
        }

        /// <summary>
        /// Generate and return a new X509 certificate.
        /// </summary>
        /// <returns></returns>
        public X509Certificate2 Generate()
        {
            var generator = new X509V3CertificateGenerator();

            // RFC 5280 section 4.1.2.2. Serial Number
            generator.SetSerialNumber(SerialNumber);

            // RFC 5280 section 4.1.2.4. Issuer
            generator.SetIssuerDN(IssuerDN);

            // RFC 5280 section 4.1.2.5. Validity
            if (!IsSelfSigned && NotBefore < Issuer.NotBefore)
            {
                NotBefore = Issuer.NotBefore;
            }

            if (!IsSelfSigned && Issuer.NotAfter < NotAfter)
            {
                NotAfter = Issuer.NotAfter;
            }

            generator.SetNotBefore(NotBefore);
            generator.SetNotAfter(NotAfter);

            // RFC 5280 section 4.1.2.6. Subject; also see Direct Project 1.2 section 4.1.1.2.
            generator.SetSubjectDN(SubjectDN);

            // RFC 5280 section 4.1.2.7. Subject Public Key Info
            generator.SetPublicKey(SubjectKeyPair.Public);

            // RFC 5280 section 4.2.1.1. Authority Key Identifier
            if (IsSelfSigned)
            {
                var authorityKeyIdentifier = new AuthorityKeyIdentifier(
                    SubjectPublicKeyInfoFactory.CreateSubjectPublicKeyInfo(SubjectKeyPair.Public),
                    new GeneralNames(new GeneralName(SubjectDN)),
                    SerialNumber);
                generator.AddExtension(X509Extensions.AuthorityKeyIdentifier, false, authorityKeyIdentifier);
            }
            else
            {
                generator.AddExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(Issuer));
            }

            // RFC 5280 section 4.2.1.2. Subject Key Identifier
            var subjectKeyIdentifier = new SubjectKeyIdentifier(SubjectPublicKeyInfoFactory.CreateSubjectPublicKeyInfo(SubjectKeyPair.Public));
            generator.AddExtension(X509Extensions.SubjectKeyIdentifier.Id, false, subjectKeyIdentifier);

            // RFC 5280 section 4.2.1.3. Key Usage
            generator.AddExtension(X509Extensions.KeyUsage, false, new KeyUsage((int)KeyUsage));

            // RFC 5280 section 4.2.1.4. Certificate Policies
            if (Policies.Any())
            {
                var policies = Policies.Select(x => new PolicyInformation(new DerObjectIdentifier(x))).ToArray();
                generator.AddExtension(X509Extensions.CertificatePolicies, false, new CertificatePolicies(policies));
            }

            // RFC 5280 section 4.2.1.6. Subject Alternative Name; also see Direct Project 1.2 section 4.1.1.1.
            generator.AddExtension(X509Extensions.SubjectAlternativeName, false, SubjectAlternativeName);

            // RFC 5280 section 4.2.1.9. Basic Constraints
            generator.AddExtension(X509Extensions.BasicConstraints, true, BasicConstraints);

            // RFC 5280 section 4.2.1.12. Extended Key Usage
            generator.AddExtension(X509Extensions.ExtendedKeyUsage, false, new ExtendedKeyUsage(KeyPurposeID.IdKPEmailProtection));

            // RFC 5280 section 4.2.1.13. CRL Distribution Points
            var crlDistPoint = GetCrlDistributionPoints();
            if (crlDistPoint != null)
            {
                generator.AddExtension(X509Extensions.CrlDistributionPoints, false, crlDistPoint);
            }

            // RFC 5280 section 4.2.2.1. Authority Information Access
            var authorityInfoAccess = GetAuthorityInfoAccessEncoded();
            if (authorityInfoAccess != null)
            {
                generator.AddExtension(X509Extensions.AuthorityInfoAccess, false, authorityInfoAccess);
            }

            // Generate a new certificate.
            var certificate = generator.Generate(new Asn1SignatureFactory(SignatureAlgorithmName, (IssuerKeyPair ?? SubjectKeyPair).Private, SecureRandom));

            // Create a PKCS12 store (a.PFX file) in memory, and add the public and private key to that.
            var store = new Pkcs12Store();
            var certificateEntry = new X509CertificateEntry(certificate);

            string friendlyName = certificate.SubjectDN.ToString();
            store.SetCertificateEntry(friendlyName, certificateEntry);
            store.SetKeyEntry(friendlyName, new AsymmetricKeyEntry(SubjectKeyPair.Private), new[] { certificateEntry });

            using (var stream = new MemoryStream())
            {
                // The password is required by the API, but will not be used beyond this scope.
                const string password = "password";
                store.Save(stream, password.ToCharArray(), SecureRandom);
                return new X509Certificate2(
                    stream.ToArray(),
                    password,
                    X509KeyStorageFlags.PersistKeySet | X509KeyStorageFlags.Exportable);
            }
        }

        private CrlDistPoint GetCrlDistributionPoints()
        {
            if (CrlDistributionPointUri == null || !CrlDistributionPointUri.IsAbsoluteUri)
            {
                return null;
            }

            var generalName = new GeneralName(GeneralName.UniformResourceIdentifier, new DerIA5String(CrlDistributionPointUri.AbsoluteUri));
            return new CrlDistPoint(new[] { new DistributionPoint(new DistributionPointName(new GeneralNames(generalName)), null, null) });
        }
    }
}
