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
using System.Globalization;
using System.Security.Cryptography.X509Certificates;
using Org.BouncyCastle.Asn1.X509;
using Org.BouncyCastle.Crypto.Operators;
using Org.BouncyCastle.Math;
using Org.BouncyCastle.X509;
using Org.BouncyCastle.X509.Extension;

namespace Health.Direct.Common.Certificates
{
    /// <summary>
    /// Certificate revocation list builder, using Bouncy Castle.
    /// </summary>
    public class CertificateRevocationListBuilder : AbstractBuilder
    {
        /// <summary>
        /// CRL Number, as defined by RFC 5280 section 5.2.3.
        /// </summary>
        public BigInteger CrlNumber { get; }

        /// <summary>
        /// This Update, as defined by RFC 5280 section 5.1.2.4.
        /// </summary>
        public DateTime ThisUpdate { get; set; }

        /// <summary>
        /// Next Update, as defined by RFC 5280 section 5.1.2.5.
        /// </summary>
        public DateTime NextUpdate { get; set; }

        /// <summary>
        /// The list of revoked certificates (serial number + revocation date),
        /// as defined by RFC 5280 section 5.1.2.6.
        /// </summary>
        public IList<Tuple<BigInteger, DateTime>> RevokedCertificates { get; }

        /// <summary>
        /// Create a new CertificateRevocationListBuilder.
        /// </summary>
        /// <param name="issuer">Certificate authority used to issue the CRL.</param>
        /// <param name="crlNumber">Unique CRL number.</param>
        public CertificateRevocationListBuilder(X509Certificate2 issuer, ulong crlNumber)
            : base(issuer)
        {
            // Base class does the validation when issuer is not null.
            if (issuer == null)
            {
                throw new ArgumentNullException(nameof(issuer));
            }

            // Bouncy Castle cannot construct BigInteger from a number.
            CrlNumber = new BigInteger(crlNumber.ToString(CultureInfo.InvariantCulture));

            // Per RFC 5280 the date should be in UTC.
            ThisUpdate = DateTime.UtcNow;

            // Per DirectTrust Community X.509 Certificate Policy
            // a new CRL must be generated at least every 30 days.
            NextUpdate = ThisUpdate.AddDays(30);

            // List of revoked certificates.
            RevokedCertificates = new List<Tuple<BigInteger, DateTime>>();
        }

        /// <summary>
        /// Add certificate to the list of revoked certificates.
        /// </summary>
        /// <param name="serialNumber">Serial number of the revoked certificate.</param>
        /// <param name="revocationDate">Date and time when the certificate was revoked.</param>
        public void AddRevokedCertificate(BigInteger serialNumber, DateTime? revocationDate = null)
        {
            RevokedCertificates.Add(new Tuple<BigInteger, DateTime>(serialNumber, revocationDate ?? DateTime.Now));
        }

        /// <summary>
        /// Add certificate to the list of revoked certificates.
        /// </summary>
        /// <param name="certificate">Revoked certificate.</param>
        /// <param name="revocationDate">Date and time when the certificate was revoked.</param>
        public void AddRevokedCertificate(X509Certificate2 certificate, DateTime? revocationDate = null)
        {
            AddRevokedCertificate(new BigInteger(certificate.SerialNumber, 16), revocationDate);
        }

        /// <summary>
        /// Generate and return a new certificate revocation list.
        /// </summary>
        /// <returns>Certificate revocation list.</returns>
        public X509Crl Generate()
        {
            var generator = new X509V2CrlGenerator();

            // RFC 5280 section 5.1.2.3. Issuer Name
            generator.SetIssuerDN(IssuerDN);

            // RFC 5280 section 5.1.2.4. This Update
            generator.SetThisUpdate(ThisUpdate);

            // RFC 5280 section 5.1.2.5. Next Update
            generator.SetNextUpdate(NextUpdate);

            // RFC 5280 section 5.1.2.6. Revoked Certificates
            foreach (var revokedCertificate in RevokedCertificates)
            {
                var serialNumber = revokedCertificate.Item1;
                var revocationDate = revokedCertificate.Item2;
                var reason = CrlReason.PrivilegeWithdrawn;

                generator.AddCrlEntry(serialNumber, revocationDate, reason);
            }

            // RFC 5280 section 5.2.1. Authority Key Identifier
            generator.AddExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(Issuer));

            // RFC 5280 section 5.2.2. Issuer Alternative Name
            var issuerAlternativeName = GetIssuerAlternativeName();
            if (issuerAlternativeName != null)
            {
                generator.AddExtension(X509Extensions.IssuerAlternativeName, false, issuerAlternativeName);
            }

            // RFC 5280 section 5.2.3. CRL Number
            generator.AddExtension(X509Extensions.CrlNumber, false, new CrlNumber(CrlNumber));

            // RFC 5280 section 5.2.7. Authority Information Access
            var authorityInfoAccess = GetAuthorityInfoAccessEncoded();
            if (authorityInfoAccess != null)
            {
                generator.AddExtension(X509Extensions.AuthorityInfoAccess, false, authorityInfoAccess);
            }

            // Generate and return.
            return generator.Generate(new Asn1SignatureFactory(SignatureAlgorithmName, IssuerKeyPair.Private, SecureRandom));
        }
    }
}
