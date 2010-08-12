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
    public class AnchorManager : IEnumerable<X509Certificate2>
    {
        ConfigStore m_store;
        AnchorIndex m_incomingAnchors;
        AnchorIndex m_outgoingAnchors;
        
        internal AnchorManager(ConfigStore store)
        {
            m_store = store;
            m_incomingAnchors = new AnchorIndex(this, true);
            m_outgoingAnchors = new AnchorIndex(this, false);
        }

        internal ConfigStore Store
        {
            get
            {
                return m_store;
            }
        }
        
        public X509Certificate2Collection this[string ownerName]
        {
            get 
            {
                return this.Get(ownerName);
            }
        }
        
        public IX509CertificateIndex IncomingAnchors
        {
            get
            {
                return m_incomingAnchors;
            }
        }
        
        public IX509CertificateIndex OutgoingAnchors
        {
            get
            {
                return m_outgoingAnchors;
            }
        }
        
        public void Add(string owner, X509Certificate2 cert)
        {
            this.Add(owner, cert, true, true);
        }
        
        public void Add(string owner, X509Certificate2 cert, bool forIncoming, bool forOutgoing)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db, owner, cert, forIncoming, forOutgoing);
                db.SubmitChanges();
            }
        }

        public void Add(ConfigDatabase db, string owner, X509Certificate2 cert, bool forIncoming, bool forOutgoing)
        {
            if (db == null || cert == null || string.IsNullOrEmpty(owner))
            {
                throw new ArgumentException();
            }

            db.Anchors.InsertOnSubmit(new Anchor(owner, cert, forIncoming, forOutgoing));
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

            return Collect(db.Anchors.Enumerate(owner));
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
            if (db == null || string.IsNullOrEmpty(thumbprint) || string.IsNullOrEmpty(owner))
            {
                throw new ArgumentException();
            }
            
            return (db.Anchors.Find(owner, thumbprint) != null);
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

            db.Anchors.ExecDelete(owner, thumbprint);
        }

        public void Remove(string ownerName)
        {
            if (string.IsNullOrEmpty(ownerName))
            {
                throw new ArgumentException();
            }

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

            db.Anchors.ExecDelete(ownerName);
        }

        public IEnumerator<X509Certificate2> GetEnumerator()
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                foreach (Anchor cert in db.Anchors)
                {
                    yield return cert.ToCertificate();
                }
            }
        }

        internal static X509Certificate2Collection Collect(IEnumerable<Anchor> certs)
        {
            if (certs == null)
            {
                return null;
            }
            
            X509Certificate2Collection collection = new X509Certificate2Collection();
            foreach (Anchor cert in certs)
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
    }
    
    internal class AnchorIndex : IX509CertificateIndex
    {
        AnchorManager m_anchors;
        bool m_forIncoming;
        
        internal AnchorIndex(AnchorManager anchors, bool forIncoming)
        {
            m_anchors = anchors;
            m_forIncoming = forIncoming;
        }
        
        public X509Certificate2Collection this[string owner]
        {
            get 
            { 
                using(ConfigDatabase db = m_anchors.Store.CreateContext())
                {
                    IEnumerable<Anchor> matches = m_forIncoming ? db.Anchors.EnumerateIncoming(owner) 
                                                                : db.Anchors.EnumerateOutgoing(owner);
                    return AnchorManager.Collect(matches);
                }
            }
        }

        public IEnumerator<X509Certificate2> GetEnumerator()
        {
            using (ConfigDatabase db = m_anchors.Store.CreateContext())
            {
                foreach (Anchor cert in db.Anchors)
                {
                    yield return cert.ToCertificate();
                }
            }
        }

        #region IEnumerable Members

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return this.GetEnumerator();
        }

        #endregion
    }
}