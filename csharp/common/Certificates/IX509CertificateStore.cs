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
using System.Collections.Generic;
using System.Security.Cryptography.X509Certificates;

namespace Health.Direct.Common.Certificates
{
    /// <summary>
    /// Interface supporting indexing certificates by subject name.
    /// </summary>
    public interface IX509CertificateIndex
    {
        /// <summary>
        /// Locate all certs whose distinguished name satisfies: (E=subjectName OR CN=subjectName)
        /// </summary>
        /// <returns>null if not found</returns>        
        /// 
        X509Certificate2Collection this[string subjectName] { get; }
    }

    /// <summary>
    /// Interface supporting a store of certificates.
    /// </summary>
    public interface IX509CertificateStore : IX509CertificateIndex, IEnumerable<X509Certificate2>, IDisposable
    {   
        /// <summary>
        /// Optional criteria that all certificates in this store match
        /// </summary>
        Predicate<X509Certificate2> Criteria
        {
            get;
            set;
        }

        /// <summary>
        /// Tests if this store contains the supplied <paramref name="cert"/>
        /// </summary>
        /// <param name="cert">The certificate to test</param>
        /// <returns><c>true</c> if this store contains <paramref name="cert"/>, <c>false</c> if not</returns>
        bool Contains(X509Certificate2 cert);

        /// <summary>
        /// Adds the supplied <paramref name="cert"/> to this store 
        /// </summary>
        /// <param name="cert">The certificate to add</param>
        void Add(X509Certificate2 cert);
        /// <summary>
        /// Adds the supplied <paramref name="certs"/> to this store 
        /// </summary>
        /// <param name="certs">The certificates to add</param>
        void Add(IEnumerable<X509Certificate2> certs);

        /// <summary>
        /// Removes the supplied <paramref name="cert"/> from this store 
        /// </summary>
        /// <param name="cert">The certificate to remove</param>
        void Remove(X509Certificate2 cert);
        /// <summary>
        /// Removes the supplied <paramref name="certs"/> from this store 
        /// </summary>
        /// <param name="certs">The certificate to remove</param>
        void Remove(IEnumerable<X509Certificate2> certs);

        /// <summary>
        /// Removes certificates from this store whose subject name matches <paramref name="subjectName"/> 
        /// </summary>
        /// <param name="subjectName">The subject name for which to remove certificates.</param>
        void Remove(string subjectName);

        /// <summary>
        /// Updates a certificate in this store.
        /// </summary>
        /// <param name="cert">The certificate to update.</param>
        void Update(X509Certificate2 cert);
        /// <summary>
        /// Updates certificates in this store.
        /// </summary>
        /// <param name="certs">The certificates to update.</param>
        void Update(IEnumerable<X509Certificate2> certs);
        
        /// <summary>
        /// Gets a collection of all certificates in this store.
        /// </summary>
        /// <returns>The collection of all certificates in this store.</returns>
        X509Certificate2Collection GetAllCertificates();             
    }
}