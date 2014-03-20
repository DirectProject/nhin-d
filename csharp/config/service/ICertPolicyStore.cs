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



using System.ServiceModel;
using Health.Direct.Config.Store;

namespace Health.Direct.Config.Service
{
    [ServiceContract(Namespace = Service.Namespace)]
    public interface ICertPolicyStore
    {
        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        CertPolicy GetPolicyByName(string policyName);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        CertPolicy GetPolicyByID(long policyId);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        CertPolicy[] GetIncomingPoliciesByDomain(string domainName);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        CertPolicy[] GetOutgoingPoliciesByDomain(string domainName);

        [OperationContract]
        [FaultContract(typeof (ConfigStoreFault))]
        CertPolicy[] EnumerateCertPolicies(long lastCertPolicyID, int maxResults);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        CertPolicy AddPolicy(CertPolicy policy);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void RemovePolicies(long[] policyIDs);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void UpdatePolicyAttributes(CertPolicy certPolicy);

        [OperationContract]
        [FaultContract(typeof (ConfigStoreFault))]
        CertPolicyGroup[] EnumerateCertPolicyGroups(long lastCertPolicyGroupID, int maxResults);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        CertPolicyGroup GetPolicyGroupByName(string policyGroupName);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        CertPolicyGroup GetPolicyGroupByID(long policyGroupID);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        CertPolicyGroup AddPolicyGroup(CertPolicyGroup policy);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void RemovePolicyGroups(long[] policyGroupIDs);

        [OperationContract]
        [FaultContract(typeof (ConfigStoreFault))]
        void UpdateGroupAttributes(CertPolicyGroup policyGroup);

        [OperationContract]
        [FaultContract(typeof (ConfigStoreFault))]
        void AddPolicyUseToGroup(CertPolicyGroupMap certPolicyGroupMap);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void RemovePolicyUseFromGroup(long policyGroupMapId);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void AssociatePolicyGroupToDomain(string domain, long policyGroupID);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void DisassociatePolicyGroupFromDomain(string domain, long policyGroupID);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void DisassociatePolicyGroupsFromDomain(string domain);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void DisassociatePolicyGroupFromDomains(long policyGroupID);

        
        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        CertPolicyGroup[] GetPolicyGroupsByDomain(string domain);

        
    }
}
