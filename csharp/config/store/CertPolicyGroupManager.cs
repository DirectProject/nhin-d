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
using Health.Direct.Common.Extensions;

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
        public static readonly DataLoadOptions GroupMapDataLoadOptions = new DataLoadOptions();
        public static readonly DataLoadOptions OwnerMapDataLoadOptions = new DataLoadOptions();

        

        static CertPolicyGroupManager()
        {
            DataLoadOptions.LoadWith<CertPolicyGroup>(c => c.CertPolicyGroupMaps);
            DataLoadOptions.LoadWith<CertPolicyGroupMap>(map => map.CertPolicy);
            DataLoadOptions.LoadWith<CertPolicyGroup>(map => map.CertPolicyGroupDomainMaps);

            GroupMapDataLoadOptions.LoadWith<CertPolicyGroupMap>(map => map.CertPolicy);
            GroupMapDataLoadOptions.LoadWith<CertPolicyGroupMap>(map => map.CertPolicyGroup);

            OwnerMapDataLoadOptions.LoadWith<CertPolicyGroupDomainMap>(map => map.CertPolicyGroup);
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


        public CertPolicyGroup Get(long id)
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                var certPolicyGroup = this.Get(db, id);
                FixUpModel(certPolicyGroup);
                return certPolicyGroup;
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

        /// <summary>
        /// Get PolicyGroupMap by name with policies
        /// </summary>
        /// <param name="name">Name of the policy</param>
        /// <returns></returns>
        public CertPolicyGroupMap[] GetWithPolicies(string name)
        {
            using (ConfigDatabase db = this.Store.CreateContext(GroupMapDataLoadOptions))
            {
                var maps = this.GetWithPolicies(db, name);
                return maps;
            }
        }

        /// <summary>
        /// Get PolicyGroupOwnerMap by name with owners
        /// </summary>
        /// <param name="name">Name of the policy</param>
        /// <returns></returns>
        public CertPolicyGroupDomainMap[] GetWithOwners(string name)
        {
            using (ConfigDatabase db = this.Store.CreateContext(OwnerMapDataLoadOptions))
            {
                var maps = this.GetWithOwners(db, name);
                return maps;
            }
        }



        //
        // This object mapping is missing.  Not sure why it was not automatic
        //
        private static void FixUpModel(CertPolicyGroup certPolicyGroup)
        {
            if (certPolicyGroup == null) return;
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


        public CertPolicyGroupMap[] GetWithPolicies(ConfigDatabase db, string name)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (string.IsNullOrEmpty(name))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyGroupName);
            }

            return db.CertPolicyGroups.GetWithPolicies(name).ToArray();
        }


        public CertPolicyGroupDomainMap[] GetWithOwners(ConfigDatabase db, string name)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (string.IsNullOrEmpty(name))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyGroupName);
            }

            return db.CertPolicyGroups.GetWithOwners(name).ToArray();
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

        public bool PolicyGroupMapExists(string policyName, string groupName, CertPolicyUse policyUse, bool incoming, bool outgoing)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return db.CertPolicyGroupMaps.Exists(policyName, groupName, policyUse, incoming, outgoing);

            }
        }

        public bool PolicyGroupMapExists(string groupName, string owner)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return db.CertPolicyGroupDomainMaps.Exists(groupName, owner);

            }
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



        public void AddPolicyUse(string policyName, string groupName, CertPolicyUse policyUse, bool incoming, bool outgoing)
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                this.AddPolicyUse(db, policyName, groupName, policyUse, incoming, outgoing);
                db.SubmitChanges();
            }
        }


        protected void AddPolicyUse(ConfigDatabase db, string policyName, string groupName, CertPolicyUse policyUse, bool incoming, bool outgoing)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (String.IsNullOrEmpty(policyName))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyName);
            }
            if (String.IsNullOrEmpty(groupName))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyGroupName);
            }
            CertPolicyGroup group = db.CertPolicyGroups.Get(groupName);
            CertPolicy policy = db.CertPolicyGroups.GetPolicy(policyName);
            group.CertPolicies.Add(policy);
            CertPolicyGroupMap map = group.CertPolicyGroupMaps.First(m => m.IsNew);
            map.Use = policyUse;
            map.ForIncoming = incoming;
            map.ForOutgoing = outgoing;
        }


        public void AssociateToOwner(string groupName, string owner)
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                this.AssociateToOwner(db, groupName, owner);
                db.SubmitChanges();
            }
        }

        protected void AssociateToOwner(ConfigDatabase db, string groupName, string owner)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (string.IsNullOrEmpty(owner))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
            }
            CertPolicyGroup group = db.CertPolicyGroups.Get(groupName);

            CertPolicyGroupDomainMap map = new CertPolicyGroupDomainMap(true)
            {
                CertPolicyGroup = group,
                Owner = owner
            };
            group.CertPolicyGroupDomainMaps.Add(map);

        }

        public void DisassociateFromDomain(string owner, long policyGroupID)
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
        /// Disassociate all Policy Groups associated to an owner
        /// </summary>
        /// <param name="owner"></param>
        public void DisassociateFromDomain(string owner)
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
        public void DisassociateFromDomains(long policyGroupId)
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

        public void Remove(string groupName)
        {
            using (ConfigDatabase db = this.Store.CreateContext(DataLoadOptions))
            {
                this.Remove(db, groupName);
                // We don't commit, because we execute deletes directly
            }
        }

        public void Remove(ConfigDatabase db, string groupName)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (string.IsNullOrEmpty(groupName))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyName);
            }
            db.CertPolicyGroups.ExecDelete(groupName);
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
