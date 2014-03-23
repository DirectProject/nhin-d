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

using Health.Direct.Common.Extensions;

namespace Health.Direct.Common.Certificates
{
    /// <summary>
    /// Abstract class for certificate storage and resolution.
    /// </summary>
    public abstract class CertificateStore : IX509CertificateStore
    {
        Predicate<X509Certificate2> m_criteria;
        
        /// <summary>
        /// Initializes a store without certificate validation criteria.
        /// </summary>
        protected CertificateStore()
        {
        }
        
        /// <summary>
        /// Initializes a store with the supplied <paramref name="criteria"/> for validating added certificates.
        /// </summary>
        /// <param name="criteria">The predicate to validate incoming certificates for goodness</param>
        protected CertificateStore(Predicate<X509Certificate2> criteria)
            : this()
        {
            m_criteria = criteria;
        }
        
        /// <summary>
        /// Gets and sets the validation criteria for certficates in this store.
        /// </summary>
        public Predicate<X509Certificate2> Criteria
        {
            get
            {
                return m_criteria;
            }
            set
            {
                m_criteria = value;
            }
        }

        /// <summary>
        /// Indexes this store by subject name.
        /// </summary>
        /// <param name="subjectName">The subject name to retrieve.</param>
        /// <returns>The collection of certificates for the supplied <paramref name="subjectName"/></returns>
        public abstract X509Certificate2Collection this[string subjectName]
        {
            get;
        }
        
        /// <summary>
        /// Tests if this store contains the supplied <paramref name="cert"/>
        /// </summary>
        /// <param name="cert">The certificate to test</param>
        /// <returns><c>true</c> if this store contains <paramref name="cert"/>, <c>false</c> if not</returns>
        public abstract bool Contains(X509Certificate2 cert);

        /// <summary>
        /// Adds the supplied <paramref name="cert"/> to this store 
        /// </summary>
        /// <param name="cert">The certificate to add</param>
        public abstract void Add(X509Certificate2 cert);

        /// <summary>
        /// Removes the supplied <paramref name="cert"/> from this store 
        /// </summary>
        /// <param name="cert">The certificate to remove</param>
        public abstract void Remove(X509Certificate2 cert);

        /// <summary>
        /// Tests if a certificate matches the validity criteria for this store.
        /// </summary>
        /// <param name="cert">The certificate to test</param>
        /// <returns><c>true</c> if the certificate matches store criteria, <c>false</c> if not</returns>
        public bool MatchesCriteria(X509Certificate2 cert)
        {
            if (cert == null)
            {
                return false;
            }

            return (m_criteria == null || m_criteria(cert));
        }

        /// <summary>
        /// Validates the <paramref name="cert"/> against this store's criteria.
        /// </summary>
        /// <param name="cert">The certificate to validate.</param>
        protected void ValidateCriteria(X509Certificate2 cert)
        {
            if (!this.MatchesCriteria(cert))
            {
                throw new ArgumentException("Criteria mismatch");
            }
        }


        /// <summary>
        /// Adds certificates from <paramref name="source"/> that meet this store's criteria
        /// </summary>
        /// <param name="source">The <see cref="X509Store"/> to add certificates from.</param>
        public void CopyFrom(X509Store source)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }

            this.Update(source.Certificates.Enumerate(this.Criteria));
        }

        /// <summary>
        /// Adds certificates from <paramref name="source"/> that meet the supplied <paramref name="criteria"/>
        /// </summary>
        /// <param name="source">The <see cref="X509Store"/> to add certificates from.</param>
        /// <param name="criteria">The predicate to filter <paramref name="source"/> certificates by</param>
        public void CopyFrom(X509Store source, Predicate<X509Certificate2> criteria)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }

            this.Update(source.Certificates.Enumerate(criteria));
        }
        // TODO: why different logic above (filtering by criteria) and below (not filtering)?

        /// <summary>
        /// Adds certificates to this store from <paramref name="certs"/>.
        /// </summary>
        /// <param name="certs">The certificates to add to this store.</param>
        public void Add(IEnumerable<X509Certificate2> certs)
        {
            if (certs == null)
            {
                throw new ArgumentNullException("certs");
            }
            foreach(X509Certificate2 cert in certs)
            {
                this.Add(cert);
            }
        }

        /// <summary>
        /// Adds certificates to this store from <paramref name="certs"/>.
        /// </summary>
        /// <param name="certs">The certificates to add to this store.</param>
        public void Add(X509Certificate2Collection certs)
        {
            if (certs == null)
            {
                throw new ArgumentNullException("certs");
            }
            for (int i = 0, count = certs.Count; i < count; ++i)
            {
                this.Add(certs[i]);
            }        
        }

        /// <summary>
        /// Adds certificates to this store from a keyfile.
        /// </summary>
        /// <param name="filePath">The path to the keyfile</param>
        /// <param name="flags">The <see cref="X509KeyStorageFlags"/> for the keyfile</param>
        public void ImportKeyFile(string filePath, X509KeyStorageFlags flags)
        {
            this.ImportKeyFile(filePath, null, flags);
        }

        /// <summary>
        /// Adds certificates to this store from a keyfile.
        /// </summary>
        /// <param name="filePath">The path to the keyfile</param>
        /// <param name="password">The keyfile password</param>
        /// <param name="flags">The <see cref="X509KeyStorageFlags"/> for the keyfile</param>
        public void ImportKeyFile(string filePath, string password, X509KeyStorageFlags flags)
        {
            X509Certificate2Collection certs = new X509Certificate2Collection();
            certs.Import(filePath, password, flags);
            this.Add(certs);
        }

        /// <summary>
        /// Adds certificates to this store from a folder.
        /// </summary>
        /// <param name="folderPath">The path to a folder containing certificate files</param>
        /// <param name="flags">The <see cref="X509KeyStorageFlags"/> for the keyfile</param>
        public void ImportFolder(string folderPath, X509KeyStorageFlags flags)
        {
            string[] files = System.IO.Directory.GetFiles(folderPath);
            if (files.IsNullOrEmpty())
            {
                return;
            }
            
            X509Certificate2Collection certs = new X509Certificate2Collection();
            foreach(string filePath in files)
            {
                certs.Clear();
                certs.Import(filePath, null, flags);
                this.Add(certs);
            }
        }

        /// <summary>
        /// Exports this store as a keyfile
        /// </summary>
        /// <param name="filePath">The path to which to export.</param>
        /// <param name="password">The password for the new keyfile</param>
        /// <param name="type">The <see cref="X509ContentType"/> for the new keyfile.</param>
        public void ExportKeyFile(string filePath, string password, X509ContentType type)
        {
            X509Certificate2Collection certs = new X509Certificate2Collection();
            certs.Add(this);
            byte[] blob = certs.Export(type, password);
            
            System.IO.File.WriteAllBytes(filePath, blob);
        }
               
        /// <summary>
        /// Removes an enumeration of certificates from this store.
        /// </summary>
        /// <param name="certs">The certificates to remove.</param>
        public void Remove(IEnumerable<X509Certificate2> certs)
        {
            if (certs == null)
            {
                throw new ArgumentNullException("certs");
            }
            foreach (X509Certificate2 cert in certs)
            {
                this.Remove(cert);
            }
        }

        /// <summary>
        /// Removes a collection of certificates from this store.
        /// </summary>
        /// <param name="certs">The certificates to remove.</param>
        public void Remove(X509Certificate2Collection certs)
        {
            if (certs == null)
            {
                throw new ArgumentNullException("certs");
            }
            for (int i = 0, count = certs.Count; i < count; ++i)
            {
                this.Remove(certs[i]);
            }
        }
        
        /// <summary>
        /// Removes all certificates matching a subject name.
        /// </summary>
        /// <param name="subjectName">The subject name to match.</param>
        public void Remove(string subjectName)
        {
            X509Certificate2Collection certs = this[subjectName];
            if (certs != null)
            {
                this.Remove(certs);
            }
        }
        
        /// <summary>
        /// Updates a specified certificate.
        /// </summary>
        /// <param name="cert">The certificate to update.</param>
        public void Update(X509Certificate2 cert)
        {
            this.ValidateCriteria(cert);
            if (this.Contains(cert))
            {
                this.Remove(cert);
            }
            this.Add(cert);
        }

        /// <summary>
        /// Updates certificates in this store.
        /// </summary>
        /// <param name="certs">The certificates to update.</param>
        public void Update(IEnumerable<X509Certificate2> certs)
        {
            if (certs == null)
            {
                throw new ArgumentNullException("certs");
            }
            foreach (X509Certificate2 cert in certs)
            {
                this.Update(cert);
            }
        }
        
        /// <summary>
        /// Gets all certificates for this store.
        /// </summary>
        /// <returns>The certificates for this store.</returns>
        public virtual X509Certificate2Collection GetAllCertificates()
        {
            X509Certificate2Collection certs = new X509Certificate2Collection();
            certs.Add(this);
            return certs;
        }
        
        /// <summary>
        /// Gets an enumeration of the certificates for this store.
        /// </summary>
        /// <returns>An enumeration of the certificates for this store.</returns>
        public abstract IEnumerator<X509Certificate2> GetEnumerator();

        /// <summary>
        /// Gets an enumerator for this store.
        /// </summary>
        /// <remarks>Use the typesafe enumerator by preference.</remarks>
        /// <returns>An <see cref="System.Collections.IEnumerator"/> for this store.</returns>
        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return this.GetEnumerator();
        }

        /// <summary>
        /// Returns a <see cref="CertificateIndex"/> from this store
        /// </summary>
        /// <returns>The <see cref="CertificateIndex"/> for this store.</returns>
        public CertificateIndex Index()
        {
            return new CertificateIndex(this);
        }

        /// <summary>
        /// Creates a new ICertificateResolver that can resolve certificates against this store
        /// </summary>
        /// <returns>A <see cref="CertificateResolver">CerticateResolver</see> for this store</returns>
        public ICertificateResolver CreateResolver()
        {
            return new CertificateResolver(this.Index(), null /* disable caching */);
        }

        /// <summary>
        /// Frees resources for this instance.
        /// </summary>
        public virtual void Dispose()
        {
        }
    }
}