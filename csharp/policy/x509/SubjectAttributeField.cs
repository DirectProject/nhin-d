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
using Health.Direct.Policy.Extensions;
using Health.Direct.Policy.X509.Standard;

namespace Health.Direct.Policy.X509
{
    public class SubjectAttributeField : IssuerAttributeField
    {
        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="required">Indicates if the field is required to be present in the certificate to be compliant with the policy.</param>
        /// <param name="rdnAttributeId">Id of the attribute to extract from the subject RDN</param>
        public SubjectAttributeField(bool required, RDNAttributeIdentifier rdnAttributeId)
            : base(required, rdnAttributeId)
        {
        }

        public SubjectAttributeField(bool requird, string rdnAttribute)
            : this(requird, RDNAttributeIdentifier.FromName(rdnAttribute))
        {
        }

       
        /// <inheritdoc />
        public override TBSFieldName Name
        {
            get { return TBSFieldName.Subject; }
        }


        /// <inheritdoc />
        public override void InjectReferenceValue(X509Certificate2 value)
        {
            Certificate = value;

            if (RdnAttributeId.Equals(RDNAttributeIdentifier.DISTINGUISHED_NAME))
            {
                var str = new List<String> { Certificate.SubjectName.RemoveSpaces() };
                PolicyValue = PolicyValueFactory.GetInstance<IList<String>>(str);
                return;
            }

            X500DistinguishedName distinguishedName = Certificate.SubjectName;
            var values = distinguishedName.GetRdns(GetRDNAttributeFieldId().Name);


            if (!values.Any() && IsRequired())
                throw new PolicyRequiredException(Name + " field attribute " + RdnAttributeId.Name + " is marked as required but is not present.");

            PolicyValue = PolicyValueFactory.GetInstance<IList<String>>(values);
            
        }
    }
}