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
#pragma warning disable 0169        // Ignore warnings. Fields used by LINQ

    [Table(Name = "CertPolicyGroupMap")]
    [DataContract(Namespace = ConfigStore.Namespace)]
    public class CertPolicyGroupMap
    {
        public CertPolicyGroupMap()
        {
            CreateDate = DateTimeHelper.Now;
            Use = CertPolicyUse.Trust;
        }

        public CertPolicyGroupMap(CertPolicyUse use, bool forIncoming, bool forOutgoing): this()
        {
            Use = use;
            ForIncoming = forIncoming;
            ForOutgoing = forOutgoing;
        }

        [Column(IsPrimaryKey = true, Name = "CertPolicyGroupId")]
        private long m_CertPolicyGroupId;
        private EntityRef<CertPolicyGroup> m_certPolicyGroup = new EntityRef<CertPolicyGroup>();

        [Column(IsPrimaryKey = true, Name = "CertPolicyId")]
        private long m_CertPolicyId;
        private EntityRef<CertPolicy> m_certPolicy = new EntityRef<CertPolicy>();

        [Association(Name = "FK_CertPolicyGroupMap_CertPolicyGroup", IsForeignKey = true, Storage = "m_certPolicyGroup", ThisKey = "m_CertPolicyGroupId")]
        [DataMember(IsRequired = true)]
        public CertPolicyGroup CertPolicyGroup
        {
            get { return m_certPolicyGroup.Entity; }
            set
            {
                CertPolicyGroup originalCertPolicyGroup = m_certPolicyGroup.Entity;
                CertPolicyGroup newCertPolicyGroup = value;

                if (originalCertPolicyGroup != newCertPolicyGroup)
                {
                    m_certPolicyGroup.Entity = null;
                    if (originalCertPolicyGroup != null)
                    {
                        originalCertPolicyGroup.CertPolicyGroupMap.Remove(this);
                    }
                    m_certPolicyGroup.Entity = newCertPolicyGroup;
                    newCertPolicyGroup.CertPolicyGroupMap.Add(this);
                }
            }
        }

        [Association(Name = "FK_CertPolicyGroupMap_CertPolicy", IsForeignKey = true, Storage = "m_certPolicy", ThisKey = "m_CertPolicyId")]
        [DataMember(IsRequired = true)]
        public CertPolicy CertPolicy
        {
            get { return m_certPolicy.Entity; }
            set
            {
                CertPolicy originalCertPolicy = m_certPolicy.Entity;
                CertPolicy newCertPolicy = value;

                if (originalCertPolicy != newCertPolicy)
                {
                    m_certPolicy.Entity = null;
                    if (originalCertPolicy != null)
                    {
                        originalCertPolicy.CertPolicyGroupMap.Remove(this);
                    }
                    m_certPolicy.Entity = newCertPolicy;
                    newCertPolicy.CertPolicyGroupMap.Add(this);
                }
            }
        }

        [Column(Name = "PolicyUse", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
        public CertPolicyUse Use
        {
            get;
            set;
        }

        [Column(Name = "ForIncoming", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
        public bool ForIncoming
        {
            get;
            set;
        }

        [Column(Name = "ForOutgoing", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
        public bool ForOutgoing
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

        public void Remove( ) {
            ConfigDatabase.RemoveAssociativeRecord(this);

            CertPolicy originalCertPolicy = CertPolicy;
            originalCertPolicy.CertPolicyGroupMap.Remove(this);

            CertPolicyGroup originalCertPolicyGroup = CertPolicyGroup;
            originalCertPolicyGroup.CertPolicyGroupMap.Remove(this);
        }
    }
}
