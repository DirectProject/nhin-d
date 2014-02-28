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
using Health.Direct.Common.Extensions;
using Health.Direct.Policy.Impl;


namespace Health.Direct.Config.Store
{
    [Table(Name = "CertPolicy")]
    [DataContract(Namespace = ConfigStore.Namespace)]
    public class CertPolicy
    {
        public const int MaxNameLength = 255;

        string m_Name;
        private EntitySet<CertPolicyGroupMap> m_CertPolicyGroupMap = new EntitySet<CertPolicyGroupMap>();

        public CertPolicy()
        {
            CreateDate = DateTimeHelper.Now;
            Status = EntityStatus.New;
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

        [Column(Name = "Name", CanBeNull = false, IsPrimaryKey = false )]
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

        /// <summary>
        /// For now thie will always be the <see cref="SimpleTextV1LexiconPolicyParser"/>.
        /// 
        /// </summary>
        [Column(Name = "Lexicon", CanBeNull = false, IsPrimaryKey = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
        public string Lexicon { 
            get;
            set;
        }

        [Column(Name = "Data", DbType = "varbinary(MAX)", CanBeNull = false, IsPrimaryKey = false, UpdateCheck = UpdateCheck.WhenChanged)]
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

        [Column(Name = "Status", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
        public EntityStatus Status
        {
            get;
            set;
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
            this.Status = source.Status;
            this.Description = source.Description;
            this.Data = source.Data;
            this.CertPolicyGroupMap = source.CertPolicyGroupMap;
        }
    }
}
