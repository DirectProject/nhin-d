/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    jshook@kryptiq.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

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
        private const String CERT_ATTRIBUTE_BINARY = "userCertificate;binary";
        private const String CERT_ATTRIBUTE = "userCertificate";
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
