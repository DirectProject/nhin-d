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
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Collections.Specialized;
using System.Data.Linq;
using System.Data.Linq.Mapping;
using System.Linq;
using System.Runtime.Serialization;

namespace Health.Direct.Config.Store
{
    [Table(Name = "CertPolicyGroup")]
    [DataContract(Namespace = ConfigStore.Namespace)]
    public class CertPolicyGroup
    {
        public const int MaxNameLength = 400;

        string m_Name;
        private EntitySet<CertPolicyGroupMap> m_certPolicyGroupMap = new EntitySet<CertPolicyGroupMap>();
        private EntitySet<CertPolicyGroupDomainMap> m_certPolicyGroupDomainMap = new EntitySet<CertPolicyGroupDomainMap>();

        public CertPolicyGroup()
        {
            CreateDate = DateTimeHelper.Now;
            Status = EntityStatus.New;
        }

        public CertPolicyGroup(string name)
            : this()
        {
            Name = name;
        }

        public CertPolicyGroup(string name, string description)
            : this()
        {
            Name = name;
            Description = description;
        }

        [Column(Name = "CertPolicyGroupId", IsPrimaryKey = true, IsDbGenerated = true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public long ID
        {
            get;
            set;
        }

        [Column(Name = "Name", CanBeNull = false, IsPrimaryKey = false)]
        [DataMember(IsRequired = true)]
        public string Name
        {
            get
            {
                return m_Name;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyName);
                }

                if (value.Length > MaxNameLength)
                {
                    throw new ConfigStoreException(ConfigStoreError.CertPolicyNameLength);
                }

                m_Name = value;
            }
        }


        [Column(Name = "Description", CanBeNull = true, IsPrimaryKey = false)]
        [DataMember(IsRequired = false)]
        public string Description
        {
            get;
            set;
        }

        [Association(Name = "FK_CertPolicyGroupMap_CertPolicy", Storage = "m_certPolicyGroupMap", ThisKey = "ID", OtherKey = "m_CertPolicyGroupId")]
        public ICollection<CertPolicyGroupMap> CertPolicyGroupMap
        {
            set
            {
                m_certPolicyGroupMap.Assign(value);
            }
            get { return m_certPolicyGroupMap; }
        }

        [Association(Name = "FK_CertPolicyGroupDomainMap_CertPolicyGroup", Storage = "m_certPolicyGroupDomainMap", ThisKey = "ID", OtherKey = "m_CertPolicyGroupId")]
        public ICollection<CertPolicyGroupDomainMap> CertPolicyGroupDomainMap
        {
            set
            {
                m_certPolicyGroupDomainMap.Assign(value);
            }
            get { return m_certPolicyGroupDomainMap; }
        }

        [Column(Name = "CreateDate", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
        public DateTime CreateDate
        {
            get;
            set;
        }

        [Column(Name = "Status", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
        public EntityStatus Status
        {
            get;
            set;
        }

        public ICollection<CertPolicy> CertPolicies
        {
            get
            {
                var policies = new ObservableCollection<CertPolicy>(
                        from groupMap in CertPolicyGroupMap select groupMap.CertPolicy);
                policies.CollectionChanged += CertPolicyGroupCollectionChanged;
                return policies;
            }
        }

        private void CertPolicyGroupCollectionChanged(object sender, NotifyCollectionChangedEventArgs e)
        {
            if (NotifyCollectionChangedAction.Add == e.Action)
            {
                foreach (CertPolicy policy in e.NewItems)
                    OnPolicyAdded(policy);
            }

            if (NotifyCollectionChangedAction.Remove == e.Action)
            {
                foreach (CertPolicy policy in e.OldItems)
                    OnPolicyRemoved(policy);
            }
        }


        private void OnPolicyAdded(CertPolicy policyGroup)
        {
            CertPolicyGroupMap map = new CertPolicyGroupMap();
            map.CertPolicyGroup = this;
            map.CertPolicy = policyGroup;
        }

        private void OnPolicyRemoved(CertPolicy policy)
        {
            CertPolicyGroupMap map =
                CertPolicyGroupMap.SingleOrDefault(pg => pg.CertPolicyGroup == this && pg.CertPolicy == policy);
            if (map != null)
            {
                map.Remove();
            }
        }



        internal void CopyFixed(CertPolicyGroup source)
        {
            this.ID = source.ID;
            this.CreateDate = source.CreateDate;
            this.Name = source.Name;
        }

        internal void ApplyChanges(CertPolicyGroup source)
        {
            this.Status = source.Status;
            this.Description = source.Description;
            this.CertPolicyGroupMap = source.CertPolicyGroupMap;
            this.m_certPolicyGroupDomainMap.Assign(source.CertPolicyGroupDomainMap);
        }
        
    }
}
