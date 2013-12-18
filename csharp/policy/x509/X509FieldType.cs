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

namespace Health.Direct.Policy.X509
{
    public class X509FieldType
    {
        public static readonly X509FieldType Signature = new X509FieldType("Signature", "Signature", null);
        public static readonly X509FieldType SignatureAlgorithm = new X509FieldType("Algorithm", "Algorithm", () => new SignatureAlgorithmField());
        public static readonly X509FieldType TBS = new X509FieldType("TbsCertificate", "To Be Signed Certificate", null);

        public static IEnumerable<X509FieldType> Values
        {
            get
            {
                yield return Signature;
                yield return SignatureAlgorithm;
                yield return TBS;
            }
        }

        readonly String rfcName;
        readonly String display;
        readonly Func<X509Field<string>> referenceClass;
        static readonly Dictionary<String, X509FieldType> TokenFieldMap;

        private X509FieldType(string rfcName
            , string display
            , Func<X509Field<string>> referenceClass)
        {
            this.rfcName = rfcName;
            this.display = display;
            this.referenceClass = referenceClass;
        }

        static X509FieldType()
        {
            TokenFieldMap = new Dictionary<string, X509FieldType>();
            foreach (var x509FieldType in Values)
            {
                TokenFieldMap.Add(x509FieldType.GetFieldToken(), x509FieldType);
                TokenFieldMap.Add(x509FieldType.GetFieldToken() + "+", x509FieldType);
            }
        }

        /// <summary>
        /// Gets the name of the field as defined by RFC5280.</summary>
        /// <returns>The name of the field as defined by RFC5280.</returns>
        public String GetRfcName()
        {
            return rfcName;
        }

        /// <summary>
        /// Gets a human readable display name of the field.</summary>
        /// <returns>A human readable display name of the field.</returns>
        public String GetDisplay()
        {
            return display;
        }

        /// <summary>
        /// Gets the class implementing the field type.
        /// </summary>
        /// <returns>The class implementing field type</returns>
        public X509Field<string> GetReferenceClass()
        {
            return referenceClass();
        }

        /// <summary>
        /// Gets the token of the field used in a lexicon parser.
        /// </summary>
        /// <returns>The token of the field used in a lexicon parser.</returns>
        public String GetFieldToken()
        {
            return "X509." + rfcName;
        }

        /// <summary>
	    /// Gets the field type associated with a specific token string.
        /// <param name="token">The token used to look up the <see cref="X509FieldType"/>.</param> 
        /// <returns>The <see cref="X509FieldType"/> associated with the token.  If the token does not represent a known field, then null is returned.</returns>
        /// </summary>
        public static X509FieldType FromToken(String token)
        {
            X509FieldType x509FieldType;
            if (TokenFieldMap.TryGetValue(token, out x509FieldType))
            {
                return x509FieldType;
            }
            return null;
        }
	
    }
}
