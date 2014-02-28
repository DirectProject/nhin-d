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
using System.Data.Linq;
using System.Data.Linq.Mapping;
using System.Runtime.Serialization;

namespace Health.Direct.Config.Store
{
    [Table(Name = "CertPolicyGroup")]
    [DataContract(Namespace = ConfigStore.Namespace)]
    public class CertPolicyGroup
    {
        public const int MaxNameLength = 400;

        string m_Name;
        private EntitySet<CertPolicyGroupMap> m_CertPolicyGroupMap = new EntitySet<CertPolicyGroupMap>();
        private EntitySet<CertPolicyGroupDomainMap> m_CertPolicyGroupDomainMap = new EntitySet<CertPolicyGroupDomainMap>();

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

        [Association(Name = "FK_CertPolicyGroupMap_CertPolicy", Storage = "m_CertPolicyGroupMap", ThisKey = "ID", OtherKey = "m_CertPolicyGroupId")]
        public EntitySet<CertPolicyGroupMap> CertPolicyGroupMap
        {
            set
            {
                m_CertPolicyGroupMap = value;
            }
            get
            {
                return m_CertPolicyGroupMap ?? (m_CertPolicyGroupMap = new EntitySet<CertPolicyGroupMap>());
            }
        }

        [Association(Name = "FK_CertPolicyGroupDomainMap_CertPolicyGroup", Storage = "m_CertPolicyGroupDomainMap", ThisKey = "ID", OtherKey = "m_CertPolicyGroupId")]
        public EntitySet<CertPolicyGroupDomainMap> CertPolicyGroupDomainMap
        {
            set
            {
                m_CertPolicyGroupDomainMap = value;
            }
            get
            {
                return m_CertPolicyGroupDomainMap ?? (m_CertPolicyGroupDomainMap = new EntitySet<CertPolicyGroupDomainMap>());
            }
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
            this.m_CertPolicyGroupDomainMap = source.CertPolicyGroupDomainMap;
        }
        
    }
}
