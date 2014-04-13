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
    /// Represents an in-memory store of certificates.
    /// </summary>
    public class MemoryX509Store : CertificateStore
    {
        X509Certificate2Collection m_certs;
        
        /// <summary>
        /// Initializes an empty store.
        /// </summary>
        public MemoryX509Store()
            : base()
        {
            m_certs = new X509Certificate2Collection();
        }
        
        /// <summary>
        /// Initializes a store using the given certificate collection
        /// </summary>
        /// <param name="certs">The certificates to add to this store.</param>
        public MemoryX509Store(X509Certificate2Collection certs)
            : this()
        {
            if (certs == null)
            {
                throw new ArgumentNullException("certs");
            }
            
            m_certs.Add(certs);
        }
        
        /// <summary>
        /// Initializes a store and adds certificates from a keyfile.
        /// </summary>
        /// <param name="filePath">The path to the keyfile</param>
        /// <param name="password">The keyfile password</param>
        /// <param name="flags">The <see cref="X509KeyStorageFlags"/> for the keyfile</param>
        public MemoryX509Store(string filePath, string password, X509KeyStorageFlags flags)
            : this()
        {
            this.ImportKeyFile(filePath, password, flags);
        }

        /// <summary>
        /// Indexes this store by subject name.
        /// </summary>
        /// <param name="subjectName">The subject name to retrieve.</param>
        /// <returns>The collection of certificates for the supplied <paramref name="subjectName"/></returns>
        public override X509Certificate2Collection this[string subjectName]
        {
            get 
            { 
                return m_certs.Find(X509FindType.FindBySubjectName, subjectName, false);
            }
        }

        /// <summary>
        /// Tests if this store contains the supplied <paramref name="cert"/>
        /// </summary>
        /// <param name="cert">The certificate to test</param>
        /// <returns><c>true</c> if this store contains <paramref name="cert"/>, <c>false</c> if not</returns>
        public override bool Contains(X509Certificate2 cert)
        {
            return m_certs.Contains(cert);
        }


        /// <summary>
        /// Adds the supplied <paramref name="cert"/> to this store 
        /// </summary>
        /// <param name="cert">The certificate to add</param>
        public override void Add(X509Certificate2 cert)
        {
            lock(m_certs)
            {
                this.ValidateCriteria(cert);
                m_certs.Add(cert);
            }
        }

        /// <summary>
        /// Removes the supplied <paramref name="cert"/> from this store 
        /// </summary>
        /// <param name="cert">The certificate to remove</param>
        public override void Remove(X509Certificate2 cert)
        {
            lock(m_certs)
            {
                m_certs.Remove(cert);
            }
        }

        /// <summary>
        /// Gets an enumeration of the certificates for this store.
        /// </summary>
        /// <returns>An enumeration of the certificates for this store.</returns>
        public override IEnumerator<X509Certificate2> GetEnumerator()
        {
            X509Certificate2Collection certs;
            lock(m_certs)
            {
                certs = new X509Certificate2Collection(m_certs);
            }
            return certs.Enumerate().GetEnumerator();
        }
        
        /// <summary>
        /// Return a copy of this store
        /// </summary>
        /// <returns></returns>
        public MemoryX509Store Clone()
        {
            return new MemoryX509Store(m_certs);
        }
    }
}