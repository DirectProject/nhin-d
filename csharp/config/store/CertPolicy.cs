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
using System.Text.RegularExpressions;
using Health.Direct.Common.Extensions;
using Health.Direct.Policy.Impl;


namespace Health.Direct.Config.Store
{
    [Table(Name = "CertPolicies")]
    [DataContract(Namespace = ConfigStore.Namespace)]
    public class CertPolicy
    {
        public const int MaxNameLength = 255;
        public const int MaxDescriptionLength = 255;

        string m_Name;
        string m_Description = String.Empty;

        private EntitySet<CertPolicyGroupMap> m_CertPolicyGroupMap = new EntitySet<CertPolicyGroupMap>();

        public CertPolicy()
        {
            CreateDate = DateTimeHelper.Now;
        }
        
        public CertPolicy(string name) : this()
        {
            Name = name;
            Lexicon = "SimpleText";
        }

        public CertPolicy(string name, byte[] data)
            : this()
        {
            Name = name;
            Lexicon = "SimpleText"; 
            Data = data;
        }

        public CertPolicy(string name, string description, byte[] data)
            : this()
        {
            Name = name;
            Description = description;
            Lexicon = "SimpleText";
            Data = data;
        }

        
        [Column(Name = "CertPolicyId", IsPrimaryKey = true, IsDbGenerated = true, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public long ID 
        { 
            get; 
            set; 
        }

        [Association(Name = "FK_CertPolicyGroupMap_CertPolicies", Storage = "m_CertPolicyGroupMap", ThisKey = "ID", OtherKey = "m_CertPolicyId")]
        public ICollection<CertPolicyGroupMap> CertPolicyGroupMap
        {
            set
            {
                m_CertPolicyGroupMap.Assign(value);
            }
            get
            {
                return m_CertPolicyGroupMap;
            }
        }

        [Column(Name = "Name", CanBeNull = false, IsPrimaryKey = false, UpdateCheck = UpdateCheck.Never)]
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

        [Column(Name = "Description", CanBeNull = false, IsPrimaryKey = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = false)]
        public String Description
        {
            get
            {
                return m_Description;
            }
            set
            {
                if (value.Length > MaxDescriptionLength)
                {
                    throw new ConfigStoreException(ConfigStoreError.CertPolicyGroupDescriptionLength);
                }

                m_Description = value;
            }
        }

        /// <summary>
        /// For now thie will always be the <see cref="SimpleTextV1LexiconPolicyParser"/>.
        /// 
        /// </summary>
        [Column(Name = "Lexicon", CanBeNull = false, IsPrimaryKey = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public string Lexicon { 
            get;
            set;
        }

        [Column(Name = "Data", DbType = "varbinary(MAX)", CanBeNull = false, IsPrimaryKey = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public byte[] Data
        {
            get; 
            set;
        }


        [Column(Name = "CreateDate", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
        public DateTime CreateDate
        {
            get;
            set;
        }


        public ICollection<CertPolicyGroup> CertPolicyGroups
        {
            get
            {
                var policyGroups = new ObservableCollection<CertPolicyGroup>(
                        from groupMap in CertPolicyGroupMap select groupMap.CertPolicyGroup);
                policyGroups.CollectionChanged += CertPolicyGroupCollectionChanged;
                return policyGroups;
            }
        }

        private void CertPolicyGroupCollectionChanged(object sender, NotifyCollectionChangedEventArgs e)
        {
            if (NotifyCollectionChangedAction.Add == e.Action)
            {
                foreach (CertPolicyGroup policyGroup in e.NewItems)
                    OnPolicyGroupAdded(policyGroup);
            }

            if (NotifyCollectionChangedAction.Remove == e.Action)
            {
                foreach (CertPolicyGroup policyGroup in e.OldItems)
                    OnPolicyGroupRemoved(policyGroup);
            }
        }


        private void OnPolicyGroupAdded(CertPolicyGroup policyGroup)
        {
            CertPolicyGroupMap map = new CertPolicyGroupMap(true);
            map.CertPolicy = this;
            map.CertPolicyGroup = policyGroup;
        }

        private void OnPolicyGroupRemoved(CertPolicyGroup policyGroup)
        {
            CertPolicyGroupMap map =
                CertPolicyGroupMap.SingleOrDefault(pg => pg.CertPolicy == this && pg.CertPolicyGroup == policyGroup);
            if (map != null)
            {
                map.Remove();
            }
        }

        

        public bool HasData
        {
            get
            {
                return (Data != null && Data.Length > 0);
            }
        }

        public void ValidateHasData()
        {
            if (Data.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.MissingCertPolicyData);
            }
        }

        internal void CopyFixed(CertPolicy source)
        {
            this.ID = source.ID;
            this.CreateDate = source.CreateDate;
            this.Name = source.Name;
        }

        internal void ApplyChanges(CertPolicy source)
        {
            this.Description = source.Description;
            this.Data = source.Data;
            this.Lexicon = source.Lexicon;
        }

        public bool IsNew()
        {
            return ID <= 0;
        }
    }
}
