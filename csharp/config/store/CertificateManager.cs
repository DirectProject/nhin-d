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
using System.Linq;
using System.Security.Cryptography.X509Certificates;

using Health.Direct.Common.Certificates;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Config.Store
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
                
        public Certificate Add(Certificate cert)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db, cert);
                db.SubmitChanges();
                return cert;
            }                        
        }

        public void Add(IEnumerable<Certificate> certs)
        {
            if (certs == null)
            {
                throw new ArgumentNullException("certs");
            }            
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                foreach(Certificate cert in certs)
                {
                    this.Add(db, cert);
                }
                db.SubmitChanges();
            }
        }
        
        public void Add(ConfigDatabase db, Certificate cert)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            
            if (cert == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidCertificate);
            }
            
            cert.ValidateHasData();
            db.Certificates.InsertOnSubmit(cert);
        }
                
        public Certificate Get(long certID)
        {
            using(ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, certID);
            }
        }

        public Certificate[] Get(long[] certIDs)
        {
            if (certIDs.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
            }
            
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return db.Certificates.Get(certIDs).ToArray();
            }
        }
        
        public Certificate Get(ConfigDatabase db, long certID)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            
            return db.Certificates.Get(certID);
        }
        
        public Certificate[] Get(long lastCertID, int maxResults)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, lastCertID, maxResults).ToArray();
            }
        }

        public IEnumerable<Certificate> Get(ConfigDatabase db, long lastCertID, int maxResults)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            return db.Certificates.Get(lastCertID, maxResults).ToArray();
        }
        
        public Certificate Get(string owner, string thumbprint)
        {
            using(ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, owner, thumbprint);
            }
        }
        
        public Certificate Get(ConfigDatabase db, string owner, string thumbprint)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            return db.Certificates.Get(owner, thumbprint);
        }

        public Certificate[] Get(string owner)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {                
                return this.Get(db, owner).ToArray();
            }
        }

        public IEnumerable<Certificate> Get(ConfigDatabase db, string owner)
        {
            return this.Get(db, owner, (EntityStatus?) null);
        }

        public Certificate[] Get(string owner, EntityStatus? status)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, owner, status).ToArray();
            }
        }

        public IEnumerable<Certificate> Get(ConfigDatabase db, string owner, EntityStatus? status)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            if (string.IsNullOrEmpty(owner))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }
            
            if (status == null)
            {
                return db.Certificates.Get(owner);
            }
            
            return db.Certificates.Get(owner, status.Value);
        }
                
        public void SetStatus(long[] certificateIDs, EntityStatus status)
        {
            if (certificateIDs.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
            }

            using (ConfigDatabase db = this.Store.CreateContext())
            {
                //
                // Todo: optimize this by using an 'in' query.. 
                //
                for (int i = 0; i < certificateIDs.Length; ++i)
                {
                    this.SetStatus(db, certificateIDs[i], status);
                }
                //db.SubmitChanges(); // Not needed, since we do a direct update
            }
        }
        
        public void SetStatus(long certificateID, EntityStatus status)
        {
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                this.SetStatus(db, certificateID, status);
                //db.SubmitChanges(); // Not needed, since we do a direct update
            }
        }
        
        public void SetStatus(ConfigDatabase db, long certificateID, EntityStatus status)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            
            db.Certificates.ExecUpdateStatus(certificateID, status);
        }

        public void SetStatus(string owner, EntityStatus status)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.SetStatus(db, owner, status);
                //db.SubmitChanges(); // Not needed, since we do a direct update
            }
        }

        public void SetStatus(ConfigDatabase db, string owner, EntityStatus status)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            db.Certificates.ExecUpdateStatus(owner, status);
        }
           
        public void Remove(long certificateID)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Remove(db, certificateID);
            }
        }
        
        public void Remove(ConfigDatabase db, long certificateID)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            
            db.Certificates.ExecDelete(certificateID);
        }
        
        public void Remove(long[] certificateIDs)
        {
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                this.Remove(db, certificateIDs);

                // We don't commit, because we execute deletes directly
            }
        }
        
        public void Remove(ConfigDatabase db, long[] certificateIDs)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (certificateIDs.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
            }
            //
            // Todo: this in a single query
            //
            for (int i = 0; i < certificateIDs.Length; ++i)
            {
                db.Certificates.ExecDelete(certificateIDs[i]);
            }
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
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            
            if (string.IsNullOrEmpty(ownerName))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }
            
            db.Certificates.ExecDelete(ownerName);
        }

        public void RemoveAll(ConfigDatabase db)
        {
            db.Certificates.ExecTruncate();
        }

        public void RemoveAll()
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                RemoveAll(db);
            }
        }

        public X509Certificate2Collection this[string subjectName]
        {
            get 
            { 
                return Certificate.ToX509Collection(this.Get(subjectName));
            }
        }
    }
}