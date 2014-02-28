/* 
 Copyright (c) 2014, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     Joseph.Shook@Surescipts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections;
using System.Collections.Generic;

namespace Health.Direct.Config.Store
{
    public class CertPolicyGroupManager : IEnumerable<CertPolicyGroup>
    {
        ConfigStore m_store;

        internal CertPolicyGroupManager(ConfigStore store)
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


        public CertPolicyGroup Add(CertPolicyGroup group)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db, group);
                db.SubmitChanges();
                return group;
            }
        }

        public CertPolicyGroup Add(ConfigDatabase db, CertPolicyGroup group)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (group == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyGroup);
            }
            
            db.CertPolicyGroups.InsertOnSubmit(group);
            return group;
        }

        public int Count()
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return db.CertPolicyGroups.GetCount();
            }
        }


        public CertPolicyGroup Get(string name)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, name);
            }
        }

        public CertPolicyGroup Get(ConfigDatabase db, string name)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (string.IsNullOrEmpty(name))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyGroupName);
            }

            return db.CertPolicyGroups.Get(name);
        }

        public CertPolicyGroup GetGroup(string name)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.GetGroup(db, name);
            }
        }

        public CertPolicyGroup GetGroup(ConfigDatabase db, string name)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (string.IsNullOrEmpty(name))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyGroupName);
            }

            return db.CertPolicyGroups.Get(name);
        }


        public void Update(CertPolicyGroup policy)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Update(db, policy);
                db.SubmitChanges();
            }
        }


        protected void Update(ConfigDatabase db, CertPolicyGroup policy)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (policy == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
            }

            CertPolicyGroup update = new CertPolicyGroup();
            update.CopyFixed(policy);

            db.CertPolicyGroups.Attach(update);
            update.ApplyChanges(policy);
        }

        public void RemoveAll(ConfigDatabase db)
        {
            db.CertPolicyGroups.ExecDeleteAll();
        }

        public void RemoveAll()
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.RemoveAll(db);
            }
        }

        public IEnumerator<CertPolicyGroup> GetEnumerator()
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                foreach (CertPolicyGroup policy in db.CertPolicyGroups)
                {
                    yield return policy;
                }
            }
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return GetEnumerator();
        }

        
    }
}
