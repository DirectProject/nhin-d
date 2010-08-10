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
using System.Data;
using System.Data.Sql;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Data.Linq;
using System.Data.Linq.Mapping;
using NHINDirect.Certificates;

namespace NHINDirect.ConfigStore
{
    public class CertificateManager : IX509CertificateIndex
    {
        ConfigStore m_store;

        internal CertificateManager(ConfigStore store)
        {
            m_store = store;
        }

        internal ConfigStore Store
        {
            get
            {
                return m_store;
            }
        }

        public X509Certificate2Collection this[string owner]
        {
            get 
            { 
                return this.Get(owner);
            }
        }
        
        public void Add(string owner, X509Certificate2 cert)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db, owner, cert);
                db.SubmitChanges();
            }            
        }

        public void Add(ConfigDatabase db, string owner, X509Certificate2 cert)
        {
            if (cert == null || string.IsNullOrEmpty(owner) || db == null)
            {
                throw new ArgumentException();
            }

            db.Certificates.InsertOnSubmit(new Certificate(owner, cert));
        }
        
        public bool Contains(string owner, string thumbprint)
        {                        
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                return this.Contains(db, owner, thumbprint);
            }        
        }

        public bool Contains(ConfigDatabase db, string owner, string thumbprint)
        {
            if (db == null || string.IsNullOrEmpty(owner) || string.IsNullOrEmpty(thumbprint))
            {
                throw new ArgumentException();
            }
            
            return (db.Certificates.Find(owner, thumbprint) != null);
        }
        
        public X509Certificate2Collection Get(string owner)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                return this.Get(db, owner);
            }
        }

        public X509Certificate2Collection Get(ConfigDatabase db, string owner)
        {
            if (db == null || string.IsNullOrEmpty(owner))
            {
                throw new ArgumentException();
            }
            return this.Collect(db.Certificates.Enumerate(owner));
        }
        
        public void Remove(string owner, string thumbprint)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Remove(db, owner, thumbprint);
            }
        }

        public void Remove(ConfigDatabase db, string owner, string thumbprint)
        {
            if (db == null || string.IsNullOrEmpty(owner) || string.IsNullOrEmpty(thumbprint))
            {
                throw new ArgumentException();
            }
            
            db.Certificates.ExecDelete(owner, thumbprint);
        }

        public void Remove(string ownerName)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Remove(db, ownerName);
            }
        }

        public void Remove(ConfigDatabase db, string ownerName)
        {
            if (db == null || string.IsNullOrEmpty(ownerName))
            {
                throw new ArgumentException();
            }

            db.Certificates.ExecDelete(ownerName);
        }
        
        public IEnumerator<X509Certificate2> GetEnumerator()
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                foreach (Certificate cert in db.Certificates)
                {
                    yield return cert.ToCertificate();
                }
            }
        }
                
        X509Certificate2Collection Collect(IEnumerable<Certificate> certs)
        {
            X509Certificate2Collection collection = new X509Certificate2Collection();
            foreach (Certificate cert in certs)
            {
                collection.Add(cert.ToCertificate());
            }
            return collection;
        }

        #region IEnumerable Members

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return this.GetEnumerator();
        }

        #endregion

        #region IDisposable Members

        public void Dispose()
        {
            
        }

        #endregion
    }
}
