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

using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Policies;
using Health.Direct.Policy.X509.Standard;
using Org.BouncyCastle.Asn1;
using Org.BouncyCastle.Asn1.X509;

namespace Health.Direct.Policy.X509
{
    /// <summary>
    /// Authority info access (AIA) extension field.
    /// <para>
    /// The policy value of this extension is returned as a collection of strings.  The AIA field is combination of two attributes: the access method and access location.  
    /// The string returned in the collection is a concatenation of the access method and the access location delimited by the : character.
    /// </para>
    /// <para>
    /// Access methods are enumerated by the <see cref="Standard.AuthorityInfoAccessMethodIdentifier" /> class.  The concatenated string uses the string literals "OCSP" and "caIssuers" as the access method.
    /// </para>
    /// <para>
    /// If the extension does not exist in the certificate, the policy value returned by this class evaluates to an empty collection. 
    /// </para>
    /// </summary>
    public class AuthorityInfoAccessExtentionField : ExtensionField<IList<String>>
    {
        /// <summary>
        /// Create new instance
        /// <param name="required">
        /// Indicates if the field is required to be present in the certificate to be compliant with the policy.
        /// </param>
        /// </summary>
        public AuthorityInfoAccessExtentionField(bool required)
            : base(required)
        {
        }

        /// <inheritdoc />
        public override void InjectReferenceValue(X509Certificate2 value)
        {
            Certificate = value;

            Asn1Object exValue = GetExtensionValue(value);

            if (exValue == null)
            {
                if (IsRequired())
                    throw new PolicyRequiredException("Extention " + ExtentionIdentifier.Display +
                                                      " is marked as required by is not present.");
                var emptyList = new List<string>();
                PolicyValue = new PolicyValue<IList<string>>(emptyList);
                return;
            }

            AuthorityInformationAccess aia = AuthorityInformationAccess.GetInstance(exValue);

            IList<String> retVal = new List<String>();
            foreach (var accessDescription in aia.GetAccessDescriptions())
            {
                string accessMethod = Standard.AuthorityInfoAccessMethodIdentifier.FromId(
                    accessDescription.AccessMethod.ToString()).Name;

                retVal.Add(accessMethod + ":" + accessDescription.AccessLocation.Name);
            }

            if (!retVal.Any() && IsRequired())
                throw new PolicyRequiredException("Extention " + ExtentionIdentifier.Display +
                                                  " is marked as required by is not present.");

            PolicyValue = new PolicyValue<IList<string>>(retVal);
        }

        /// <inheritdoc />
        public override ExtensionIdentifier ExtentionIdentifier
        {
            get { return ExtensionIdentifier.AuthorityInfoAccessSyntax; }
        }
    }

}
