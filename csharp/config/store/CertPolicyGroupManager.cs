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
using System.Data.Linq;
using System.Linq;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Mail;

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

        public static readonly DataLoadOptions DataLoadOptions = new DataLoadOptions();

        static CertPolicyGroupManager()
        {
            DataLoadOptions.LoadWith<CertPolicyGroup>(c => c.CertPolicyGroupMaps);
            DataLoadOptions.LoadWith<CertPolicyGroupMap>(map => map.CertPolicy);
            DataLoadOptions.LoadWith<CertPolicyGroup>(map => map.CertPolicyGroupDomainMaps);
        }

        public CertPolicyGroup Add(CertPolicyGroup @group)
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                this.Add(db, @group);
                db.SubmitChanges();
                return @group;
            }
        }

        public CertPolicyGroup Add(ConfigDatabase db, CertPolicyGroup @group)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (@group == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyGroup);
            }

            db.CertPolicyGroups.InsertOnSubmit(@group);
            return @group;
        }

        public int Count()
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return db.CertPolicyGroups.GetCount();
            }
        }

        /// <summary>
        /// Get PolicyGroup by name
        /// </summary>
        /// <param name="name">Name of the policy</param>
        /// <returns></returns>
        public CertPolicyGroup Get(string name)
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                var certPolicyGroup = this.Get(db, name);
                FixUpModel(certPolicyGroup);
                return certPolicyGroup;
            }
        }

        public CertPolicyGroup Get(long id)
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                var certPolicyGroup = this.Get(db, id);
                FixUpModel(certPolicyGroup);
                return certPolicyGroup;
            }
        }

        //
        // This object mapping is missing.  Not sure why it was not automatic
        //
        private static void FixUpModel(CertPolicyGroup certPolicyGroup)
        {
            foreach (var certPolicyGroupMap in certPolicyGroup.CertPolicyGroupMaps)
            {
                certPolicyGroupMap.CertPolicyGroup = certPolicyGroup;
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

        public CertPolicyGroup Get(ConfigDatabase db, long id)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            return db.CertPolicyGroups.Get(id);
        }


        public CertPolicyGroup[] GetByDomains(string[] owners)
        {
            if (owners.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }

            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                var cpgs = db.CertPolicyGroups.GetByOwners(owners);
                return cpgs.ToArray();
            }
        }

        public CertPolicyGroup[] Get(long lastID, int maxResults)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return this.Get(db, lastID, maxResults).ToArray();
            }
        }

        public IEnumerable<CertPolicyGroup> Get(ConfigDatabase db, long lastID, int maxResults)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            return db.CertPolicyGroups.Get(lastID, maxResults);
        }


        public void Update(CertPolicyGroup policyGroup)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Update(db, policyGroup);
                db.SubmitChanges();
            }
        }


        protected void Update(ConfigDatabase db, CertPolicyGroup policyGroup)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (policyGroup == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
            }

            CertPolicyGroup update = new CertPolicyGroup();
            update.CopyFixed(policyGroup);
            db.CertPolicyGroups.Attach(update);
            update.ApplyChanges(policyGroup);

        }

        public void AddAssociation(CertPolicyGroup policyGroup)
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                this.AddAssociation(db, policyGroup);
                db.SubmitChanges();
            }
        }

        protected void AddAssociation(ConfigDatabase db, CertPolicyGroup policyGroup)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (policyGroup == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
            }

            db.CertPolicyGroups.Attach(policyGroup);
            foreach (CertPolicyGroupMap certPolicyGroupMap in policyGroup.CertPolicyGroupMaps)
            {
                if (certPolicyGroupMap.IsNew)
                {
                    db.CertPolicyGroupMaps.InsertOnSubmit(certPolicyGroupMap);
                    if (certPolicyGroupMap.CertPolicy.IsNew())
                    {
                        db.CertPolicies.InsertOnSubmit(certPolicyGroupMap.CertPolicy);
                    }
                }
            }
            foreach (CertPolicyGroupDomainMap domainMap in policyGroup.CertPolicyGroupDomainMaps)
            {
                if (domainMap.IsNew)
                {
                    db.CertPolicyGroupDomainMaps.InsertOnSubmit(domainMap);
                    if (domainMap.CertPolicyGroup.IsNew())
                    {
                        db.CertPolicyGroups.InsertOnSubmit(domainMap.CertPolicyGroup);
                    }
                }
            }
        }

        public void AddPolicyUse(CertPolicyGroupMap certPolicyGroupMap)
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                this.AddPolicyUse(db, certPolicyGroupMap);
                db.SubmitChanges();
            }
        }

        protected void AddPolicyUse(ConfigDatabase db, CertPolicyGroupMap certPolicyGroupMap)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (certPolicyGroupMap == null)
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyUse);
            }
            CertPolicyGroup policyGroup = db.CertPolicyGroups.Get(certPolicyGroupMap.CertPolicyGroup.ID);
            CertPolicy policy = db.CertPolicies.Get(certPolicyGroupMap.CertPolicy.ID);
            policyGroup.CertPolicies.Add(policy, certPolicyGroupMap);
        }

        public void AssociateToDomain(string owner, long policyGroupID)
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                this.AssociateToDomain(db, owner, policyGroupID);
                db.SubmitChanges();
            }
        }

        protected void AssociateToDomain(ConfigDatabase db, string owner, long policyGroupID)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (string.IsNullOrEmpty(owner))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }
            CertPolicyGroup policyGroup = db.CertPolicyGroups.Get(policyGroupID);
            if (policyGroup.CertPolicyGroupDomainMaps.All(map => map.Owner != owner))
            {
                CertPolicyGroupDomainMap map = new CertPolicyGroupDomainMap(true)
                {
                    CertPolicyGroup = policyGroup,
                    Owner = owner
                };
                policyGroup.CertPolicyGroupDomainMaps.Add(map);
            }
        }

        public void DissAssociateFromDomain(string owner, long policyGroupID)
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                CertPolicyGroup policyGroup = db.CertPolicyGroups.Get(policyGroupID);
                if (policyGroup.CertPolicyGroupDomainMaps.Any(map => map.Owner == owner))
                {
                    CertPolicyGroupDomainMap[] maps =
                    {
                        new CertPolicyGroupDomainMap(true)
                        {
                            CertPolicyGroup = policyGroup,
                            Owner = owner
                        }
                    };
                    this.RemoveDomain(db, maps);
                    // We don't commit, because we execute deletes directly
                }
            }
        }

        /// <summary>
        /// Dissassoicate all Policy Groups associated to an owner
        /// </summary>
        /// <param name="owner"></param>
        public void DissAssociateFromDomain(string owner)
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                db.CertPolicyGroupDomainMaps.ExecDelete(owner);
            }
        }

        /// <summary>
        /// Remove all 
        /// </summary>
        /// <param name="policyGroupId"></param>
        public void DissAssociateFromDomains(long policyGroupId)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                db.CertPolicyGroupDomainMaps.ExecDelete(policyGroupId);
            }
        }
        

        public void Remove(long policyGroupId)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Remove(db, policyGroupId);
            }
        }

        public void Remove(ConfigDatabase db, long policyGroupId)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            db.CertPolicyGroups.ExecDelete(policyGroupId);
        }

        public void Remove(long[] policyGroupIds)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Remove(db, policyGroupIds);
                // We don't commit, because we execute deletes directly
            }
        }

        public void Remove(ConfigDatabase db, long[] policyGroupIds)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (policyGroupIds.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
            }

            for (int i = 0; i < policyGroupIds.Length; ++i)
            {
                db.CertPolicyGroups.ExecDelete(policyGroupIds[i]);
            }
        }

        public void RemovePolicy(CertPolicyGroupMap[] map)
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.RemovePolicy(db, map);
                // We don't commit, because we execute deletes directly
            }
        }


        public void RemovePolicy(ConfigDatabase db, CertPolicyGroupMap[] map)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (map.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
            }

            for (int i = 0; i < map.Length; ++i)
            {
                db.CertPolicyGroups.ExecDeleteGroupMap(map[i]);
            }
        }

        public void RemoveDomain(CertPolicyGroupDomainMap[] map)
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                this.RemoveDomain(db, map);
                // We don't commit, because we execute deletes directly
            }
        }

        public void RemoveDomain(ConfigDatabase db, CertPolicyGroupDomainMap[] map)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (map.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
            }

            for (int i = 0; i < map.Length; ++i)
            {
                db.CertPolicyGroups.ExecDeleteDomainMap(map[i]);
            }
        }

        public void RemovePolicyUseFromGroup(long mapId)
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                this.RemovePolicyUseFromGroup(db, mapId);
                // We don't commit, because we execute deletes directly
            }
        }

        public void RemovePolicyUseFromGroup(ConfigDatabase db, long mapId)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            db.CertPolicyGroupMaps.ExecDeleteGroupMap(mapId);
        }


        public void RemoveAll(ConfigDatabase db)
        {
            db.CertPolicyGroups.ExecDeleteAll();
        }

        public void RemoveAll()
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                this.RemoveAll(db);
            }
        }

        public IEnumerator<CertPolicyGroup> GetEnumerator()
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
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
