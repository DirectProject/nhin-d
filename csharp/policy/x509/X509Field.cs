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
using System.Security.Cryptography.X509Certificates;
using Org.BouncyCastle.Asn1;

namespace Health.Direct.Policy.X509
{
    public abstract class X509Field<T> : IX509Field<T>
    {
        [NonSerialized] protected IPolicyValue<T> PolicyValue;
        [NonSerialized] protected X509Certificate2 Certificate;

        protected bool Required;

        /// <inheritdoc />
        public PolicyExpressionReferenceType GetPolicyExpressionReferenceType()
        {
            return PolicyExpressionReferenceType.Certificate;
        }

       

        /// <inheritdoc />
        public PolicyExpressionType GetExpressionType()
        {
            return PolicyExpressionType.REFERENCE;
        }

        /// <inheritdoc />
        public IPolicyValue<T> GetPolicyValue()
        {
            if (PolicyValue == null)
                throw new InvalidOperationException("Policy value is null");

            return PolicyValue;
        }

        /// <inheritdoc />
        public bool IsRequired()
        {
            return Required;
        }

        /// <inheritdoc />
        public void SetRequired(bool required)
        {
            Required = required;
        }



        /// <summary>
        /// Converts an encoded internal octet string object to a DERObject
        /// </summary>
        /// <param name="ext">The encoded octet string as a byte array</param>
        /// <returns>The converted DerObjectIdentifier</returns>
        protected DerObjectIdentifier GetObject(byte[] ext)
        {
            try
            {
                Asn1InputStream aIn;
                using (aIn = new Asn1InputStream(ext))
                {
                    Asn1Object octs = aIn.ReadObject();
                    using (aIn = new Asn1InputStream(octs.GetDerEncoded()))
                    {
                        return aIn.ReadObject() as DerObjectIdentifier;
                    }
                }
            }
            catch (Exception e)
            {
                throw new PolicyProcessException("Exception processing data ", e);
            }
        }


        /// <summary>
        /// Converts an encoded internal sequence object to a DERObject
        /// </summary>
        /// <param name="ext">The encoded sequence as a byte array</param>
        /// <returns>The converted DERObjectIdentifier</returns>
        protected DerObjectIdentifier GetDERObject(byte[] ext)
        {

            try
            {
                Asn1InputStream aIn;
                using (aIn = new Asn1InputStream(ext))
                {
                    DerSequence seq = (DerSequence) aIn.ReadObject();
                    using (aIn = new Asn1InputStream(seq.GetDerEncoded()))
                    {
                        return aIn.ReadObject() as DerObjectIdentifier;
                    }
                }
            }
            catch (Exception e)
            {
                throw new PolicyProcessException("Exception processing data ", e);
            }

        }

        //TODO: this feels wrong.  Had to do this to compile during the Java port.
        public abstract X509FieldType GetX509FieldType();
        public abstract void InjectReferenceValue(X509Certificate2 value);

        /// <inheritdoc />
        public override String ToString()
        {
            if (PolicyValue == null)
            {
                return "Unevaluated X509 field: " + GetX509FieldType();
            }
            return PolicyValue.ToString();
        }
    }
}