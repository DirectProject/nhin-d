/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Mail;
using System.Threading;
using System.Security.Cryptography.X509Certificates;

namespace NHINDirect.Certificates
{
    public class SystemX509Store : CertificateStore
    {
        X509Store m_store;
        
        public SystemX509Store(X509Store store, Predicate<X509Certificate2> criteria)
            : base(criteria)
        {
            if (store == null)
            {
                throw new ArgumentNullException();
            }
            
            m_store = store;
        }
                
        public override X509Certificate2Collection this[string subjectName]
        {
            get
            {
                return m_store.Certificates.Find(X509FindType.FindBySubjectName, subjectName, false);
            }
        }
                
        public override bool Contains(X509Certificate2 cert)
        {
            return m_store.Certificates.Contains(cert);
        }
        
        public override void Add(X509Certificate2 cert)
        {
            if (cert == null)
            {
                throw new ArgumentNullException();
            }

            lock (m_store)
            {
                this.ValidateCriteria(cert);
                m_store.Add(cert);
            }
        }

        public override void Remove(X509Certificate2 cert)
        {
            lock(m_store)
            {
                m_store.Remove(cert);
            }
        }
                        
        public override IEnumerator<X509Certificate2> GetEnumerator()
        {
            X509Certificate2Collection certs;
            lock(m_store)
            {
                certs = m_store.Certificates;
            }
            
            return certs.Enumerate(this.Criteria).GetEnumerator();
        }
                
        public override void Dispose()
        {
            if (m_store != null)
            {
                m_store.Close();
                m_store = null;
            }
        }
        
        public const string AnchorCertsStoreName = "NHINDAnchors";
        public const string PrivateCertsStoreName = "NHINDPrivate";
        public const string ExternalCertsStoreName = "NHINDExternal";
        
        public static SystemX509Store OpenAnchor()
        {
            return new SystemX509Store(Extensions.OpenStoreRead(AnchorCertsStoreName, StoreLocation.LocalMachine), null);
        }
        public static SystemX509Store OpenAnchorEdit()
        {
            return new SystemX509Store(Extensions.OpenStoreReadWrite(AnchorCertsStoreName, StoreLocation.LocalMachine), null);
        }
        public static SystemX509Store OpenPrivate()
        {
            return new SystemX509Store(Extensions.OpenStoreRead(PrivateCertsStoreName, StoreLocation.LocalMachine),
                                        x => x.HasPrivateKey);
        }
        public static SystemX509Store OpenPrivateEdit()
        {
            return new SystemX509Store(Extensions.OpenStoreReadWrite(PrivateCertsStoreName, StoreLocation.LocalMachine),
                                        x => x.HasPrivateKey);
        }
        public static SystemX509Store OpenExternal()
        {
            return new SystemX509Store(Extensions.OpenStoreRead(ExternalCertsStoreName, StoreLocation.LocalMachine), null);
        }
        public static SystemX509Store OpenExternalEdit()
        {
            return new SystemX509Store(Extensions.OpenStoreReadWrite(ExternalCertsStoreName, StoreLocation.LocalMachine), null);
        }
    }
}
