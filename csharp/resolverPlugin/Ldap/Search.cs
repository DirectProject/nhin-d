using System;
using System.DirectoryServices.Protocols;

namespace Health.Direct.ResolverPlugins.Ldap
{
    /// <summary>
    /// Ldap SearchRequests...
    /// </summary>
    public static class Search
    {
        private const String NAMING_CONTEXTS_ATTRIBUTE = "namingContexts";
        private const String WILDCARD_OBJECT_CLASS_SEARCH = "objectclass=*";
        private const String CERT_ATTRIBUTE_BINARY = "userSMIMECertificate;binary";
        private const String CERT_ATTRIBUTE = "userSMIMECertificate";
        private const String EMAIL_ATTRIBUTE = "mail";
        
        /// <summary>
        /// Build a NamingContext ladap request....
        /// </summary>
        public static SearchRequest NamingContextRequest()
        {

            return new SearchRequest("", WILDCARD_OBJECT_CLASS_SEARCH, SearchScope.Base,
                                            new[] { NAMING_CONTEXTS_ATTRIBUTE });

        }
        /// <summary>
        /// Build a mime cert subtree ldap request...
        /// </summary>
        /// <param name="distinqishedName"></param>
        /// <param name="subjectName"></param>
        /// <returns></returns>
        public static SearchRequest MimeCertRequest(string distinqishedName, string subjectName)
        {
            return new SearchRequest(distinqishedName, EMAIL_ATTRIBUTE + "=" + subjectName, SearchScope.Subtree,
                                                    new[] { CERT_ATTRIBUTE, CERT_ATTRIBUTE_BINARY });

        }
    }
}
