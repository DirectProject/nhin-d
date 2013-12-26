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

namespace Health.Direct.Policy.X509.Standard
{
    public class RDNAttributeIdentifier
    {
        //
        // From RFC5280 section 4.1.2.4 and RFC4519
        //

        
        /// <summary>
        /// <para>Common name attribute</para> 
        /// RDN Name: CN 
        /// </summary>
        public static readonly RDNAttributeIdentifier COMMON_NAME = new RDNAttributeIdentifier("2.5.4.3", "CN");

        /// <summary>
        /// <para>Country attribute</para>
        /// RDN Name: C
        /// </summary>
        public static readonly RDNAttributeIdentifier COUNTRY = new RDNAttributeIdentifier("2.5.4.6", "C");

        /// <summary>
        /// <para>Organization attribute</para>
        /// RDN Name: O
        /// </summary>
        public static readonly RDNAttributeIdentifier ORGANIZATION = new RDNAttributeIdentifier("2.5.4.10", "O");

        /// <summary>
        /// <para>Organizational unit attribute</para>
        /// RDN Name: OU
        /// </summary>
        public static readonly RDNAttributeIdentifier ORGANIZATIONAL_UNIT = new RDNAttributeIdentifier("2.5.4.11", "OU");

        /// <summary>
        /// <paraState>State attribute></para>
        /// RDN Name: ST
        /// </summary>
        public static readonly RDNAttributeIdentifier STATE = new RDNAttributeIdentifier("2.5.4.8", "ST");

        /// <summary>
        /// <para>Locality (city) attribute</para>
        /// RDN Name: L
        /// </summary>
        public static readonly RDNAttributeIdentifier LOCALITY = new RDNAttributeIdentifier("2.5.4.7", "L");

        /// <summary>
        /// <para>Legacy email attribute</para>
        /// RDN Name: E
        /// </summary>
        public static readonly RDNAttributeIdentifier EMAIL = new RDNAttributeIdentifier("1.2.840.113549.1.9.1", "E");

        /// <summary>
        /// <para>Domain component attribute</para>
        /// RDN Name: DC
        /// </summary>
        public static readonly RDNAttributeIdentifier DOMAIN_COMPONENT = new RDNAttributeIdentifier("0.9.2342.19200300.100.1.25", "DC");

        /// <summary>
        /// <para>Distinguished name qualifier attribute</para>
        /// RDN Name: DNQUALIFIER
        /// </summary>
        public static readonly RDNAttributeIdentifier DISTINGUISHED_NAME_QUALIFIER = new RDNAttributeIdentifier("2.5.4.46", "DNQUALIFIER");

        /// <summary>
        /// <para>Serial number attribute</para>
        /// RDN Name: SERIALNUMBER
        /// </summary>
        public static readonly RDNAttributeIdentifier SERIAL_NUMBER = new RDNAttributeIdentifier("2.5.4.5", "SERIALNUMBER");

        /// <summary>
        /// <para>Surname attribute</para>
        /// RDN Name: SN
        /// </summary>
        public static readonly RDNAttributeIdentifier SURNAME = new RDNAttributeIdentifier("2.5.4.4", "SN");

        /// <summary>
        /// <para>Title name attribute</para>
        /// RDN Name: TITLE
        /// </summary>
        public static readonly RDNAttributeIdentifier TITLE = new RDNAttributeIdentifier("2.5.4.12", "TITLE");

        /// <summary>
        /// <para>Given name attribute</para>
        /// RDN Name: GIVENNAME
        /// </summary>
        public static readonly RDNAttributeIdentifier GIVEN_NAME = new RDNAttributeIdentifier("2.5.4.42", "GIVENNAME");

        /// <summary>
        /// <para>Initials attribute</para>
        /// RDN Name: INITIALS
        /// </summary>
        public static readonly RDNAttributeIdentifier INITIALS = new RDNAttributeIdentifier("2.5.4.43", "INITIALS");

        /// <summary>
        /// <para>Pseudonym attribute</para>
        /// RDN Name: PSEUDONYM
        /// </summary>
        public static readonly RDNAttributeIdentifier PSEUDONYM = new RDNAttributeIdentifier("2.5.4.65", "PSEUDONYM");

        /// <summary>
        /// <para>General qualifier attribute</para>
        /// RDN Name: GERNERAL_QUALIFIER
        /// </summary>
        public static readonly RDNAttributeIdentifier GERNERAL_QUALIFIER = new RDNAttributeIdentifier("2.5.4.64", "GERNERAL_QUALIFIER");

        /// <summary>
        /// <para>Distinguished name attribute</para>
        /// <para>This attribute is overloaded by the policy engine and returns the full relative distinguished name using RFC2253 formatting</para>
        /// RDN Name: GERNERAL_QUALIFIER
        /// </summary>
        public static readonly RDNAttributeIdentifier DISTINGUISHED_NAME = new RDNAttributeIdentifier("2.5.4.49", "DN");


        public static IEnumerable<RDNAttributeIdentifier> Values
        {
            get
            {
                yield return COMMON_NAME;
                yield return COUNTRY;
                yield return ORGANIZATION;
                yield return ORGANIZATIONAL_UNIT;
                yield return STATE;
                yield return LOCALITY;
                yield return EMAIL;
                yield return DOMAIN_COMPONENT;
                yield return DISTINGUISHED_NAME_QUALIFIER;
                yield return SERIAL_NUMBER;
                yield return SURNAME;
                yield return TITLE;
                yield return GIVEN_NAME;
                yield return INITIALS;
                yield return PSEUDONYM;
                yield return GERNERAL_QUALIFIER;
                yield return DISTINGUISHED_NAME;
            }
        }

        readonly String m_id;
        readonly String m_name;
        readonly static IDictionary<String, RDNAttributeIdentifier> m_nameFieldMap;

        static RDNAttributeIdentifier()
        {
            m_nameFieldMap = new Dictionary<string, RDNAttributeIdentifier>();

            foreach (var rdnAtrId in Values)
            {
                m_nameFieldMap.Add(rdnAtrId.Name, rdnAtrId);
            }
        }

        private RDNAttributeIdentifier(string id, string name)
        {
            m_id = id;
            m_name = name;
        }

        /// <summary>
        /// Gets the object identifier (OID) of the RDN attribute.
        /// </summary>
        /// <value></value>
        public string OID
        {
            get { return m_id; }
        }

        /// <summary>
        /// Gets the name of the attribute as it is commonly displayed in an X509 certificate viewer
        /// </summary>
        /// <value>The name of the attribute as it is commonly displayed in an X509 certificate viewer</value>
        public string Name
        {
            get { return m_name; }
        }

        /// <inheritdoc />
        public override String ToString()
        {
            return m_name;
        }

        /// <summary>
        /// Gets the RDNAttributeIdentifier associated with the RDN name.  This method also accepts a parsed token ending with the 
        /// RDN name.the RDNAttributeIdentifier associated with the RDN name.
        /// </summary>
        /// <param name="name">Name or parsed token used to lookup the RDNAttributeIdentifier.</param>
        /// <returns>he RDNAttributeIdentifier associated with the RDN name.   If the name does not represent a known RDN, then null is returned.</returns>
        public static RDNAttributeIdentifier FromName(String name)
        {
            String lookupName;
            int idx = name.LastIndexOf(".", StringComparison.CurrentCulture);
            if (idx >= 0)
                lookupName = name.Substring(idx + 1);
            else
                lookupName = name;
            RDNAttributeIdentifier rdnAtrId;
            m_nameFieldMap.TryGetValue(lookupName, out rdnAtrId);
            return rdnAtrId;
        }

    }
}