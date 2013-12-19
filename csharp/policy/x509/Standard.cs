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
    public class Standard
    {
        /// <summary>
        /// General name types as describe in section 4.2.1.6 of RFC5280
        /// <remarks>
        /// <![CDATA[
        /// SubjectAltName ::= GeneralNames<br/>
        ///  
        /// GeneralNames ::= SEQUENCE SIZE (1..MAX) OF GeneralName<br/>
        ///  
        /// GeneralName ::= CHOICE {<br/>
        ///      otherName                       [0]     OtherName,<br/>
        ///      rfc822Name                      [1]     IA5String,<br/>
        ///      dNSName                         [2]     IA5String,<br/>
        ///      x400Address                     [3]     ORAddress,<br/>
        ///      directoryName                   [4]     Name,<br/>
        ///      ediPartyName                    [5]     EDIPartyName,<br/>
        ///      uniformResourceIdentifier       [6]     IA5String,<br/>
        ///      iPAddress                       [7]     OCTET STRING,<br/>
        ///      registeredID                    [8]     OBJECT IDENTIFIER }<br/>
        /// ]]>
        /// </remarks>
        /// </summary>
        public enum GeneralNameType
        {
            OtherName = 0,
            RFC822Name = 1,
            DNSName = 2,
            X400Address = 3,
            DirectoryName = 4,
            EdiPartyName = 5,
            UniformResourceIdentifier = 6,
            IPAddress = 7,
            RegisteredId = 8
        }

        internal const string GeneralName_OtherName = "otherName";

        public static string ToString(GeneralNameType type)
        {
            switch (type)
            {
                default:
                    throw new NotSupportedException();

                case GeneralNameType.OtherName:
                    return GeneralName_OtherName;
            }
        }

        public static TEnum FromTag<TEnum>(int tagNo)
        {
            return (TEnum)Enum.ToObject(typeof(Enum), tagNo);
        }


        public class AuthorityInfoAccessMethodIdentifier
        {

            public static readonly AuthorityInfoAccessMethodIdentifier OCSP 
                = new AuthorityInfoAccessMethodIdentifier("1.3.6.1.5.5.7.48.1", "OCSP");
            public static readonly AuthorityInfoAccessMethodIdentifier CA_ISSUERS 
                = new AuthorityInfoAccessMethodIdentifier("1.3.6.1.5.5.7.48.2", "caIssuers");

            public static IEnumerable<AuthorityInfoAccessMethodIdentifier> Values
            {
                get
                {
                    yield return OCSP;
                    yield return CA_ISSUERS;
                }
            }

            /// <summary>
            /// Gets the object identifier (OID) of the access method.
            /// </summary>
            public string Id { get { return m_id; } }
            
            /// <summary>
            /// Gets the name of the access method.
            /// </summary>
            public string Name { get { return m_name; } }
            
            private string m_id;
            private string m_name;

            private AuthorityInfoAccessMethodIdentifier(String id, String name)
            {
                m_id = id;
                m_name = name;
            }


            /// <summary>
            /// Gets an AuthorityInfoAccessMethodIdentifier from an access id.
            /// </summary>
            /// <param name="id">The id of the access method.</param>
            /// <returns>The AuthorityInfoAccessMethodIdentifier that matches the request id.  If the request id does not match a known access method id, then null is returned;</returns>
            public static AuthorityInfoAccessMethodIdentifier FromId(String id)
            {
                if (id.Equals(OCSP.Id))
                    return OCSP;
                if (id.Equals(CA_ISSUERS.Id))
                    return CA_ISSUERS;

                return null;
            }
        }
    }

}
