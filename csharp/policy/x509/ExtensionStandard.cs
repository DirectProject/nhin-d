/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Joe Shook      jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/


using System.Collections.Generic;


namespace Health.Direct.Policy.X509
{
    public class ExtensionStandard
    {
        public class Field
        {
            public readonly string Id;
            public string RfcName;
            public string Display;

            public static List<Field> Map;

            /// <summary>
            /// Lookup data for object identifiers (OIDs) supported in X509 certificate extension fields.
            /// </summary>
            /// <param name="id"></param>
            /// <param name="rfcName"></param>
            /// <param name="display"></param>
            public Field(string id, string rfcName, string display)
            {
                Id = id;
                RfcName = rfcName;
                Display = display;

            }
            static Field()
            {
                Map = new List<Field>();
                Map.Add(new KeyUsage());
            }
        }

        /// <summary>
        /// From RFC 5280 section 4.2.1.3
        /// The key usage extension defines the purpose (e.g., encipherment,
        ///signature, certificate signing) of the key contained in the
        /// certificate. 
        /// <see cref="KeyUsage"/> for possible possible bit string
        /// </summary>
        public class KeyUsage : Field
        {
            public KeyUsage() : base("2.5.29.15", "KeyUsage", "Key Usage") { }
        }

        /// <inheritdoc />
        public class SubjectAltName : Field
        {
            public SubjectAltName() : base("2.5.29.17", "SubjectAltName", "Subject Alternative Name") { }
        }

        /// <inheritdoc />
        public class SubjectDirectoryAttributes : Field
        {
            public SubjectDirectoryAttributes() : base("2.5.29.9", "SubjectDirectoryAttributes", "Subject Key Attributes") { }
        }

        /// <inheritdoc />
        public class SubjectKeyIdentifier : Field
        {
            public SubjectKeyIdentifier() : base("2.5.29.9", "SubjectKeyIdentifier", "Subject Key Identifier") { }
        }

        /// <inheritdoc />
        public class IssuerAltName : Field
        {
            public IssuerAltName() : base("2.5.29.18", "IssuerAltName", "Issuer Alternative Name") { }
        }

        /// <inheritdoc />
        public class AuthorityKeyIdentifier : Field
        {
            public AuthorityKeyIdentifier() : base("2.5.29.35", "AuthorityKeyIdentifier", "Authority Key Identifier") { }
        }

        /// <inheritdoc />
        public class CertificatePolicies : Field
        {
            public CertificatePolicies() : base("2.5.29.32", "CertificatePolicies", "Certificate Policies") { }
        }

        /// <inheritdoc />
        public class BasicConstraints : Field
        {
            public BasicConstraints() : base("2.5.29.19", "BasicConstraints", "Policy Mappings") { }
        }

        /// <inheritdoc />
        public class PolicyMappings : Field
        {
            public PolicyMappings() : base("2.5.29.33", "PolicyMappings", "Policy Mappings") { }
        }

        /// <inheritdoc />
        public class NameConstraints : Field
        {
            public NameConstraints() : base("2.5.29.30", "NameConstraints", "Name Constraints") { }
        }

        /// <inheritdoc />
        public class PolicyConstraints : Field
        {
            public PolicyConstraints() : base("2.5.29.36", "PolicyConstraints", "Policy Constraints") { }
        }

        /// <inheritdoc />
        public class ExtKeyUsageSyntax : Field
        {
            public ExtKeyUsageSyntax() : base("2.5.29.37", "ExtKeyUsageSyntax", "Extended Key Usage") { }
        }

        /// <inheritdoc />
        public class CRLDistributionPoints : Field
        {
            public CRLDistributionPoints() : base("2.5.29.31", "CRLDistributionPoints", "CRL Distribution Points") { }
        }

        /// <inheritdoc />
        public class InhibitAnyPolicy : Field
        {
            public InhibitAnyPolicy() : base("2.5.29.54", "InhibitAnyPolicy", "Inhibit Any Policy") { }
        }

        /// <inheritdoc />
        public class FreshestCRL : Field
        {
            public FreshestCRL() : base("2.5.29.46", "FreshestCRL", "Freshest CRL") { }
        }

        /// <inheritdoc />
        public class AuthorityInfoAccessSyntax : Field
        {
            public AuthorityInfoAccessSyntax() : base("1.3.6.1.5.5.7.1.1", "AuthorityInfoAccessSyntax", "Authority Information Access") { }

        }
        /// <inheritdoc />
        public class SubjectInfoAccessSyntax : Field
        {
            public SubjectInfoAccessSyntax() : base("1.3.6.1.5.5.7.1.11", "SubjectInfoAccessSyntax", "Subject Information Access") { }
        }
    }
}
