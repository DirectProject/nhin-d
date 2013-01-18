/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;

namespace Health.Direct.Common.Certificates
{
    /// <summary>
    /// Supports resolution of certificates.
    /// If no certificate for address, methods return null
    /// Throw exceptions if there was an error during retrieval, such as network issues
    /// Implementations may use implementation specific caching policies.
    /// </summary>
    public interface ICertificateResolver
    {
        /// <summary>
        /// Returns the valid certficates for a mail address.
        /// Implementations representing remote certificate stores (e.g., DNS) may throw network exceptions.
        /// </summary>
        /// <param name="address">
        /// A <see cref="System.Net.Mail.MailAddress"/>
        /// </param>
        /// <returns>
        /// A <see cref="System.Security.Cryptography.X509Certificates.X509Certificate2Collection"/> or null if there are no matches.
        /// </returns>
        X509Certificate2Collection GetCertificates(MailAddress address);
        /// <summary>
        /// Returns the valid certificates for a domain
        /// Implementations representing remote certificate stores (e.g., DNS) may throw network exceptions.
        /// </summary>
        /// <param name="domain">domain</param>
        /// <returns>
        /// A <see cref="System.Security.Cryptography.X509Certificates.X509Certificate2Collection"/> or null if there are no matches.
        /// </returns>
        X509Certificate2Collection GetCertificatesForDomain(string domain);

        /// <summary>
        /// Event to subscribe to for notification of errors.
        /// </summary>
        event Action<ICertificateResolver, Exception> Error;
    }
}