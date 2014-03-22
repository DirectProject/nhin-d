/* 
 Copyright (c) 2014, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections;
using System.Collections.Generic;
using System.Data.Linq;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Config.Store
{
    public class CertPolicyManager : IEnumerable<CertPolicy>
    {
        ConfigStore m_store;
        ICertPolicyValidator m_validator;

        internal CertPolicyManager(ConfigStore store, ICertPolicyValidator validator)
        {
            m_store = store;
            m_validator = validator;
        }

        internal CertPolicyManager(ConfigStore store)
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

        public static readonly DataLoadOptions DataLoadOptions = new DataLoadOptions();

        static CertPolicyManager()
        {
            DataLoadOptions.LoadWith<CertPolicy>(c => c.CertPolicyGroupMap);
            DataLoadOptions.LoadWith<CertPolicyGroupMap>(map => map.CertPolicyGroup);
        }
        

        public CertPolicy Add(CertPolicy policy)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db, policy);
                db.SubmitChanges();
                return policy;
            }
        }

        public CertPolicy Add(ConfigDatabase db, CertPolicy policy)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (policy == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicy);
            }
            policy.ValidateHasData();

            if (!m_validator.IsValidLexicon(policy))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicy);
            }

            db.CertPolicies.InsertOnSubmit(policy);
            return policy;
        }

        public void Add(IEnumerable<CertPolicy> policies)
        {
            if (policies == null)
            {
                throw new ArgumentNullException("policies");
            }
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                foreach (CertPolicy policy in policies)
                {
                    this.Add(db, policy);
                }
                db.SubmitChanges();
            }
        }

        public int Count()
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return db.CertPolicies.GetCount();
            }
        }


        public CertPolicy Get(string name)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext(DataLoadOptions))
            {
                return this.Get(db, name);
            }
        }

        public CertPolicy Get(ConfigDatabase db, string name)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (string.IsNullOrEmpty(name))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyName);
            }

            return db.CertPolicies.Get(name);
        }

        public CertPolicy Get(long policyID)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return db.CertPolicies.Get(policyID);
            }
        }

        public CertPolicy[] Get(long[] policyIDs)
        {
            if (policyIDs.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
            }

            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return db.CertPolicies.Get(policyIDs).ToArray();
            }
        }

        public CertPolicy[] Get(long lastID, int maxResults)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, lastID, maxResults).ToArray();
            }
        }

        public IEnumerable<CertPolicy> Get(ConfigDatabase db, long lastID, int maxResults)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            return db.CertPolicies.Get(lastID, maxResults);
        }


        public CertPolicy[] GetIncomingByOwner(string owner)
        {
            if (string.IsNullOrEmpty(owner))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }

            using (ConfigDatabase db = this.Store.CreateReadContext(DataLoadOptions))
            {
                var cpgs = db.CertPolicies.GetIncoming(owner);
                return cpgs.ToArray();
            }
        }

        public CertPolicy[] GetIncomingByOwner(string owner, CertPolicyUse use)
        {
            if (string.IsNullOrEmpty(owner))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }

            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                var cpgs = db.CertPolicies.GetIncoming(owner, use);
                return cpgs.ToArray();
            }
        }

        public CertPolicy[] GetOutgoingByOwner(string owner)
        {
            if (string.IsNullOrEmpty(owner))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }

            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                var cpgs = db.CertPolicies.GetOutgoing(owner);
                return cpgs.ToArray();
            }
        }

        public CertPolicy[] GetOutgoingByOwner(string owner, CertPolicyUse use)
        {
            if (string.IsNullOrEmpty(owner))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }

            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                var cpgs = db.CertPolicies.GetOutgoing(owner, use);
                return cpgs.ToArray();
            }
        }


        public void Update(CertPolicy policy)
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                this.Update(db, policy);
                db.SubmitChanges();
            }
        }


        protected void Update(ConfigDatabase db, CertPolicy policy)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (policy == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
            }

            CertPolicy update = new CertPolicy();
            update.CopyFixed(policy);
            db.CertPolicies.Attach(update);
            update.ApplyChanges(policy);
            //foreach (CertPolicyGroupMap certPolicyGroupMap in policy.CertPolicyGroupMap)
            //{
            //    if (certPolicyGroupMap.IsNew)
            //    {
            //        db.CertPolicyGroupMaps.InsertOnSubmit(certPolicyGroupMap);
            //        if (certPolicyGroupMap.CertPolicyGroup.IsNew())
            //        {
            //            db.CertPolicyGroups.InsertOnSubmit(certPolicyGroupMap.CertPolicyGroup);
            //        }
            //    }
            //}

        }

        public void Remove(long policyId)
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                this.Remove(db, policyId);
            }
        }

        public void Remove(ConfigDatabase db, long policyId)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            db.CertPolicies.ExecDelete(policyId);
        }

        public void Remove(long[] policyIds)
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                this.Remove(db, policyIds);
                // We don't commit, because we execute deletes directly
            }
        }

        public void Remove(ConfigDatabase db, long[] policyIds)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (policyIds.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
            }

            for (int i = 0; i < policyIds.Length; ++i)
            {
                db.CertPolicies.ExecDelete(policyIds[i]);
            }
        }

        public void RemoveAll(ConfigDatabase db)
        {
            db.CertPolicies.ExecDeleteAll();
        }

        public void RemoveAll()
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                this.RemoveAll(db);
            }
        }

        public IEnumerator<CertPolicy> GetEnumerator()
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                foreach (CertPolicy policy in db.CertPolicies)
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
