using System;
using System.Collections.Generic;

namespace Health.Direct.Policy.X509.Standard
{
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