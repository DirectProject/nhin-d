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


using System.Collections;
using System.Collections.Generic;
using System.Security.Cryptography.X509Certificates;
using Org.BouncyCastle.Asn1;
using Org.BouncyCastle.Asn1.X509;

namespace Health.Direct.Policy.X509
{
    ///<summary>
    ///  Extended key usage extension field.
    /// <para>
    /// The policy value of this extension is returned as a collection of strings containing object identifiers (OIDs) of the extended key usages.  
    /// </para>
    /// <para>
    /// If the extension does not exist in the certificate, the policy value returned by this class
    /// evaluates to an empty collection.
    /// </para>
    /// </summary>
    public class ExtendedKeyUsageExtensionField : ExtensionField<List<string>>
    {
        /// <summary>
        /// Create new instance
        /// <param name="required">
        /// Indicates if the field is required to be present in the certificate to be compliant with the policy.
        /// </param>
        /// </summary>
        public ExtendedKeyUsageExtensionField(bool required)
            : base(required)
        {
        }

        /// <inheritdoc />
        public override void InjectReferenceValue(X509Certificate2 value)
        {
            Certificate = value;

            DerObjectIdentifier exValue = GetExtensionValue(value);

            if (exValue == null)
            {
                if (IsRequired())
                    throw new PolicyRequiredException("Extention " + GetExtentionIdentifier().GetDisplay() + " is marked as required by is not present.");
                
                PolicyValue = new PolicyValue<List<string>>(new List<string>());
                return;
            }

            ExtendedKeyUsage usages = ExtendedKeyUsage.GetInstance(exValue);
            IList purposeList = usages.GetAllUsages();
		
		    var usageList = new List<string>();
            foreach (DerObjectIdentifier purpose in purposeList)
            {
                usageList.Add(purpose.Id);
            }

            PolicyValue = new PolicyValue<List<string>>(usageList);
        }

        /// <inheritdoc />
        public override ExtensionIdentifier GetExtentionIdentifier()
        {
            return ExtensionIdentifier.KeyUsage;
        }
    }
}
