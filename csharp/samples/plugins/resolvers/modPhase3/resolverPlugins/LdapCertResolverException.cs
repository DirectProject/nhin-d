/* 
 Copyright (c) 2012, Direct Project
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
using Health.Direct.Common;

namespace Health.Direct.ModSpec3.ResolverPlugins
{
    /// <summary>
    /// Exception for DSN processing errors
    /// </summary>
    public class LdapCertResolverException : DirectException<LDAPError>
    {
        /// <summary>
        /// Creates an LdapCertResolverException with the specified error
        /// </summary>
        /// <param name="error">error code</param>
        public LdapCertResolverException(LDAPError error)
            : base(error)
        {
        }

        /// <summary>
        /// Creates an LdapCertResolverException with the specified error
        /// </summary>
        /// <param name="error">error code</param>
        /// <param name="innerException">Inner exception</param>
        public LdapCertResolverException(LDAPError error, Exception innerException)
            : base(error, innerException)
        {
        }

        /// <summary>
        /// Creates an LdapCertResolverException with the specified error
        /// </summary>
        /// <param name="error">error code</param>
        /// <param name="message"></param>
        /// <param name="innerException">Inner exception</param>
        public LdapCertResolverException(LDAPError error, string message, Exception innerException)
            : base(error, message, innerException)
        {
        }
    }
}
