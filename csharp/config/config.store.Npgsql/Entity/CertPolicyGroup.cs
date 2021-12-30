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


using System.Collections.ObjectModel;

namespace Health.Direct.Config.Store.Entity
{
    public class CertPolicyGroup
    {
        public const int MaxNameLength = 400;
        public const int MaxDescriptionLength = 255;

        string m_Name;
        string m_Description = string.Empty;

        public CertPolicyGroup()
        {
            CreateDate = DateTimeHelper.Now; }
        

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

        public CertPolicyGroup(CertPolicyGroup policy)
            : this()
        {

            Name = policy.Name;
            Description = policy.Description;
        }

        public long ID
        {
            get;
            set;
        }

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


        public string Description
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

        public ICollection<CertPolicyGroupMap> CertPolicyGroupMaps { get; set; } = new List<CertPolicyGroupMap>();



        public ICollection<CertPolicyGroupDomainMap> CertPolicyGroupDomainMaps { get; set; } =
            new List<CertPolicyGroupDomainMap>();

        public DateTime CreateDate
        {
            get;
            set;
        }

        public ICollection<CertPolicy> CertPolicies
        {
            get
            {
                if (!CertPolicyGroupMaps.Any())
                {
                    return new List<CertPolicy>();
                }
                var policies = new ObservableCollection<CertPolicy>(
                        from groupMap in CertPolicyGroupMaps select groupMap.CertPolicy);

                return policies;
            }
        }

        public void CopyFixed(CertPolicyGroup source)
        {
            this.ID = source.ID;
            this.Name = source.Name;
            this.CreateDate = source.CreateDate;
            
        }

        public void ApplyChanges(CertPolicyGroup source)
        {
            this.Description = source.Description;
            //this.CertPolicyGroupMap = source.CertPolicyGroupMap;
            //this.m_certPolicyGroupDomainMap.Assign(source.CertPolicyGroupDomainMap);
        }
        
    }

    
    public static class CertPolicyGroupExt
    {
        public static void Add(this ICollection<CertPolicy> policies, CertPolicy policy, CertPolicyGroupMap certPolicyGroupMap)
        {
            policies.Add(policy);
            
            // Find the new map and add Use attributes
            foreach (var certPolicy in policies)
            {
                foreach (var policyGroupMap in certPolicy.CertPolicyGroupMaps)
                {
                    if (policyGroupMap.CertPolicy.CertPolicyId == certPolicyGroupMap.CertPolicy.CertPolicyId
                        && policyGroupMap.CertPolicyGroup.ID == certPolicyGroupMap.CertPolicyGroup.ID)
                    {
                        policyGroupMap.PolicyUse = certPolicyGroupMap.PolicyUse;
                        policyGroupMap.ForIncoming = certPolicyGroupMap.ForIncoming;
                        policyGroupMap.ForOutgoing = certPolicyGroupMap.ForOutgoing;
                        return;
                    }
                }
            }
        }
    }
}
