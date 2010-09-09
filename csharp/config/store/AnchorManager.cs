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
using System.Net.Mail;
using NHINDirect.Certificates;

namespace NHINDirect.Config.Store
{
    public class AnchorManager : ITrustAnchorResolver
    {
        ConfigStore m_store;
        AnchorIndex m_incomingIndex;
        AnchorIndex m_outgoingIndex;
        CertificateResolver m_incomingResolver;
        CertificateResolver m_outgoingResolver;
        
        internal AnchorManager(ConfigStore store)
        {
            m_store = store;
            m_incomingIndex = new AnchorIndex(this, true);
            m_outgoingIndex = new AnchorIndex(this, false);
            m_incomingResolver = new CertificateResolver(m_incomingIndex);
            m_outgoingResolver = new CertificateResolver(m_outgoingIndex);
        }

        internal ConfigStore Store
        {
            get
            {
                return m_store;
            }
        }
        
        public IX509CertificateIndex IncomingIndex
        {
            get
            {
                return m_incomingIndex;
            }
        }
        
        
        public IX509CertificateIndex OutgoingIndex
        {
            get
            {
                return m_outgoingIndex;
            }
        }
        
        public ICertificateResolver IncomingAnchors
        {
            get 
            { 
                return m_incomingResolver;
            }
        }

        public ICertificateResolver OutgoingAnchors
        {
            get 
            { 
                return m_outgoingResolver;
            }
        }
        
        public void Add(Anchor anchor)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db, anchor);
                db.SubmitChanges();
            }
        }

        public void Add(IEnumerable<Anchor> anchors)
        {
            if (anchors == null)
            {
                throw new ArgumentNullException("anchors");
            }
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                foreach(Anchor anchor in anchors)
                {
                    this.Add(db, anchor);
                }
                db.SubmitChanges();
            }
        }
        
        public void Add(ConfigDatabase db, Anchor anchor)
        {
            if (db == null)
            {
                throw new ArgumentException();
            }
            if (anchor == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidAnchor);
            }
            
            db.Anchors.InsertOnSubmit(anchor);
        }

        public Anchor[] Get(long[] certificateIDs)
        {
            if (certificateIDs.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
            }

            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return db.Anchors.Get(certificateIDs).ToArray();
            }
        }

        public Anchor[] Get(long lastCertID, int maxResults)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, lastCertID, maxResults).ToArray();
            }
        }

        public IEnumerable<Anchor> Get(ConfigDatabase db, long lastCertID, int maxResults)
        {
            if (db == null)
            {
                throw new ArgumentNullException();
            }

            return db.Anchors.Get(lastCertID, maxResults);
        }

        public Anchor[] Get(string owner)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                return this.Get(db, owner).ToArray();
            }
        }

        public IEnumerable<Anchor> Get(ConfigDatabase db, string owner)
        {
            if (db == null)
            {
                throw new ArgumentNullException();
            }
            if (string.IsNullOrEmpty(owner))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }
            
            return db.Anchors.Get(owner);
        }

        public Anchor[] GetIncoming(string ownerName)
        {
            return this.GetIncoming(ownerName, null);
        }

        public Anchor[] GetIncoming(string ownerName, EntityStatus? status)
        {
            if (string.IsNullOrEmpty(ownerName))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }

            using (ConfigDatabase db = this.Store.CreateContext())
            {
                IEnumerable<Anchor> matches;
                if (status == null)
                {
                    matches = db.Anchors.GetIncoming(ownerName);
                }
                else
                {
                    matches = db.Anchors.GetIncoming(ownerName, status.Value);
                }
                
                return matches.ToArray();
            }
        }

        public Anchor[] GetOutgoing(string ownerName)
        {
            return this.GetOutgoing(ownerName, null);
        }

        public Anchor[] GetOutgoing(string ownerName, EntityStatus? status)
        {
            if (string.IsNullOrEmpty(ownerName))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }

            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                IEnumerable<Anchor> matches;
                
                if (status == null)
                {
                    matches = db.Anchors.GetOutgoing(ownerName);
                }
                else
                {
                    matches = db.Anchors.GetOutgoing(ownerName, status.Value);
                }
                
                return matches.ToArray();                    
            }
        }

        public Anchor Get(string owner, string thumbprint)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, owner, thumbprint);
            }
        }

        public Anchor Get(ConfigDatabase db, string owner, string thumbprint)
        {
            if (db == null)
            {
                throw new ArgumentNullException();
            }
            if (string.IsNullOrEmpty(owner))            
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }
            if (string.IsNullOrEmpty(thumbprint))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidThumbprint);
            }
            return db.Anchors.Get(owner, thumbprint);
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
                throw new ArgumentNullException();
            }

            db.Anchors.ExecUpdateStatus(owner, status);
        }

        public void Remove(long[] certificateIDs)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Remove(db, certificateIDs);

                // We don't commit, because we execute deletes directly
            }
        }

        public void Remove(ConfigDatabase db, long[] certificateIDs)
        {
            if (db == null)
            {
                throw new ArgumentNullException();
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
                db.Anchors.ExecDelete(certificateIDs[i]);
            }
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
            if (db == null)
            {
                throw new ArgumentNullException();
            }
            if (string.IsNullOrEmpty(owner))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }
            if (string.IsNullOrEmpty(thumbprint))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidThumbprint);
            }
            
            db.Anchors.ExecDelete(owner, thumbprint);
        }

        public void Remove(string ownerName)
        {
            if (string.IsNullOrEmpty(ownerName))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }

            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Remove(db, ownerName);
            }
        }

        public void Remove(ConfigDatabase db, string ownerName)
        {
            if (db == null)
            {
                throw new ArgumentNullException();
            }
            if (string.IsNullOrEmpty(ownerName))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }

            db.Anchors.ExecDelete(ownerName);
        }
        
        internal class AnchorIndex : IX509CertificateIndex
        {
            AnchorManager m_anchorManager;       
            bool m_incoming;
            
            internal AnchorIndex(AnchorManager anchors, bool incoming)
            {
                m_anchorManager = anchors;
                m_incoming = incoming;
            }

            public X509Certificate2Collection this[string subjectName]
            {
                get 
                { 
                    Anchor[] anchors = (m_incoming) ? m_anchorManager.GetIncoming(subjectName) : m_anchorManager.GetOutgoing(subjectName);
                    return Anchor.ToX509Collection(anchors);
                }
            }
        }
    }
 }