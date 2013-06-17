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
    /// Represents a local machine store for certificates.
    /// </summary>
    public class SystemX509Store : CertificateStore
    {
        X509Store m_store;
        
        /// <summary>
        /// Initializes an instance from an existing store with a criteria on for accepted certificates.
        /// </summary>
        /// <param name="store">The store to draw certificates from</param>
        /// <param name="criteria">The predicate to test accepted certificates.</param>
        public SystemX509Store(X509Store store, Predicate<X509Certificate2> criteria)
            : base(criteria)
        {
            if (store == null)
            {
                throw new ArgumentNullException("store");
            }
            
            m_store = store;
        }
                
        /// <summary>
        /// Indexes certificates by the subject name attribute.
        /// </summary>
        /// <param name="subjectName">The subject name for which to look up certificates.</param>
        /// <returns>The certificates for the subject name.</returns>
        public override X509Certificate2Collection this[string subjectName]
        {
            get
            {
                return m_store.Certificates.Find(X509FindType.FindBySubjectName, subjectName, false);
            }
        }
                
        /// <summary>
        /// Indicates if the certificate store contains the specified <paramref name="cert"/>
        /// </summary>
        /// <param name="cert">The certificate to search for.</param>
        /// <returns><c>true</c> if the store contains the certificate, <c>false</c> if not.</returns>
        public override bool Contains(X509Certificate2 cert)
        {
            return m_store.Certificates.Contains(cert);
        }
        
        /// <summary>
        /// Adds the <paramref name="cert"/> to this store.
        /// </summary>
        /// <param name="cert">The certificate to add. Must match the criteria for this store.</param>
        public override void Add(X509Certificate2 cert)
        {
            if (cert == null)
            {
                throw new ArgumentNullException("cert");
            }

            lock (m_store)
            {
                this.ValidateCriteria(cert);
                m_store.Add(cert);
            }
        }

        /// <summary>
        /// Removes a certificate from this store.
        /// </summary>
        /// <param name="cert">The certificate to remove.</param>
        public override void Remove(X509Certificate2 cert)
        {
            lock(m_store)
            {
                m_store.Remove(cert);
            }
        }
                        
        /// <summary>
        /// Returns the enumeration of all the certificates matching the store criteria in this store.
        /// </summary>
        /// <returns>The enumeration of store certificates.</returns>
        public override IEnumerator<X509Certificate2> GetEnumerator()
        {
            X509Certificate2Collection certs;
            lock(m_store)
            {
                certs = m_store.Certificates;
            }
            
            return certs.Enumerate(this.Criteria).GetEnumerator();
        }
                
        /// <summary>
        /// Frees resources for this instance.
        /// </summary>
        public override void Dispose()
        {
            if (m_store != null)
            {
                m_store.Close();
                m_store = null;
            }
        }
        
        /*
            Names of the default Direct Project Machine Certificate Stores
        */        
        /// <summary>
        /// The default machine store for anchor certificates
        /// </summary>
        public const string AnchorCertsStoreName = "NHINDAnchors";
        /// <summary>
        /// The default machine store for private certificates
        /// </summary>
        public const string PrivateCertsStoreName = "NHINDPrivate";
        /// <summary>
        /// The default maching store for external public certificates.
        /// </summary>
        public const string ExternalCertsStoreName = "NHINDExternal";
        /*
            These methods work with the default Direct Project Certificate Stores
        */
        
        /// <summary>
        /// Creates the default Direct Project machine stores if they don't already exist
        /// </summary>
        public static void CreateAll()
        {
            SystemX509Store store;
            
            store = SystemX509Store.OpenAnchorEdit();
            store.Dispose();
            
            store = SystemX509Store.OpenExternalEdit();
            store.Dispose();
            
            store = SystemX509Store.OpenPrivateEdit();
            store.Dispose();
        }
        
        /// <summary>
        /// Opens the default anchor machine certificate store for reads.
        /// </summary>
        /// <returns>The default anchor machine certificate store.</returns>
        public static SystemX509Store OpenAnchor()
        {
            return new SystemX509Store(CryptoUtility.OpenStoreRead(AnchorCertsStoreName, StoreLocation.LocalMachine), null);
        }
        /// <summary>
        /// Opens the default anchor machine certificate store for reads and writes.
        /// </summary>
        /// <returns>The default anchor machine certificate store.</returns>
        public static SystemX509Store OpenAnchorEdit()
        {
            return new SystemX509Store(CryptoUtility.OpenStoreReadWrite(AnchorCertsStoreName, StoreLocation.LocalMachine), null);
        }

        /// <summary>
        /// Opens the default private certificate store for reads
        /// </summary>
        /// <returns>The default private certificate store.</returns>
        public static SystemX509Store OpenPrivate()
        {
            return new SystemX509Store(CryptoUtility.OpenStoreRead(PrivateCertsStoreName, StoreLocation.LocalMachine),
                                       x => x.HasPrivateKey);
        }

        /// <summary>
        /// Opens the default private certificate store for reads and writes
        /// </summary>
        /// <returns>The default private certificate store.</returns>
        public static SystemX509Store OpenPrivateEdit()
        {
            return new SystemX509Store(CryptoUtility.OpenStoreReadWrite(PrivateCertsStoreName, StoreLocation.LocalMachine),
                                       x => x.HasPrivateKey);
        }

        /// <summary>
        /// Opens the default external machine certificate store for reads.
        /// </summary>
        /// <returns>The default external certificate store.</returns>
        public static SystemX509Store OpenExternal()
        {
            return new SystemX509Store(CryptoUtility.OpenStoreRead(ExternalCertsStoreName, StoreLocation.LocalMachine), null);
        }

        /// <summary>
        /// Opens the default external machine certificate store for reads and writes.
        /// </summary>
        /// <returns>The default external certificate store.</returns>
        public static SystemX509Store OpenExternalEdit()
        {
            return new SystemX509Store(CryptoUtility.OpenStoreReadWrite(ExternalCertsStoreName, StoreLocation.LocalMachine), null);
        }        
    }
}