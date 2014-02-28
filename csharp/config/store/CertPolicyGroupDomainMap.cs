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
    [Table(Name = "CertPolicyGroupDomainMap")]
    [DataContract(Namespace = ConfigStore.Namespace)]
    public class CertPolicyGroupDomainMap
    {
        public const int MaxOwnerLength = 400;
        string m_owner;

        [Column(IsPrimaryKey = true, Name = "CertPolicyGroupId")]
        private long m_CertPolicyGroupId;
        private EntityRef<CertPolicyGroup> m_CertPolicyGroup = new EntityRef<CertPolicyGroup>();
        
        /// <summary>
        /// Relationshp to Domain.  Not enforced in SQL schema. 
        /// </summary>
        [Column(Name = "Owner", CanBeNull = false, IsPrimaryKey = true)]
        [DataMember(IsRequired = true)]
        public string Owner
        {
            get
            {
                return m_owner;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
                }

                if (value.Length > MaxOwnerLength)
                {
                    throw new ConfigStoreException(ConfigStoreError.OwnerLength);
                }

                m_owner = value;
            }
        }


        [Association(Name = "FK_CertPolicyGroupMap_CertPolicyGroup", IsForeignKey = true, Storage = "m_CertPolicyGroup", ThisKey = "m_CertPolicyGroupId")]
        [DataMember(IsRequired = true)]
        public CertPolicyGroup CertPolicyGroup
        {
            get { return m_CertPolicyGroup.Entity; }
            set { m_CertPolicyGroup.Entity = value; }
        }


        [Column(Name = "CreateDate", CanBeNull = false, UpdateCheck = UpdateCheck.WhenChanged)]
        [DataMember(IsRequired = true)]
        public DateTime CreateDate
        {
            get;
            set;
        }
    }
}
