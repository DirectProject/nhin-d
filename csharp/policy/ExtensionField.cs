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
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Policies;
using Health.Direct.Policy.Extensions;
using Health.Direct.Policy.X509;
using Org.BouncyCastle.Asn1;

namespace Health.Direct.Policy
{
    public abstract class ExtensionField<T> : TBSField<T>, IExtensionField<T>
    {
        public ExtensionField(bool required)
            : base(required)
        {
        }

        public override TBSFieldName Name
        {
            get { return TBSFieldName.Extenstions; }
        }

        public abstract ExtensionIdentifier ExtentionIdentifier { get; }


        virtual public bool IsCritical()
        {
            if (Certificate == null)
			    throw new InvalidOperationException("Certificate value is null");

            List<string> criticalOIDs = Certificate.GetCriticalExtensionOIDs();

            return criticalOIDs.Contains(ExtentionIdentifier.Id);
        }

        /// <summary>
        /// Gets the specified certificate extension field from the certificate as a <see cref="DerObjectIdentifier"/>.  
        /// The extension field is determined by the concrete implementation of <see cref="GetExtentionIdentifier"/>
        /// <param name="cert">The certificate to extract the extension field from.</param>
        /// <returns>The extension field as DerObjectIdentifier.  If the extension does not exist in the certificate, then null is returned. </returns>
        /// <exception cref="PolicyProcessException">TODO:</exception>
	    /// </summary>
        protected Asn1Object GetExtensionValue(X509Certificate2 cert)
        {
    	    string oid = ExtentionIdentifier.Id;
            
            X509Extension x509Extension = cert.Extensions[oid];
            if (x509Extension != null)
            {
                byte[] bytes = x509Extension.RawData;
                if (bytes == null)
                {
                    return null;
                }

                return GetObject(bytes);
            }
            return null;
        }
    }
}
