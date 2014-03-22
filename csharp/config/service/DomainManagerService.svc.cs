/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Chris Lomonico  chris.lomonico@surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Linq;
using System.Runtime.ConstrainedExecution;
using System.ServiceModel;
using Health.Direct.Common.DnsResolver;
using Health.Direct.Config.Store;

namespace Health.Direct.Config.Service
{
    public class DomainManagerService : ConfigServiceBase, IDomainManager, IAddressManager, IDnsRecordManager, ICertPolicyStore
    {
        #region IDomainManager Members

        public Domain AddDomain(Domain domain)
        {
            try
            {
                return Store.Domains.Add(domain);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddDomain", ex);
            }
        }

        public void UpdateDomain(Domain domain)
        {
            try
            {
                Store.Domains.Update(domain);
            }
            catch (Exception ex)
            {
                throw CreateFault("UpdateDomain", ex);
            }
        }

        public int GetDomainCount()
        {
            try
            {
                return Store.Domains.Count();
            }
            catch (Exception ex)
            {
                throw CreateFault("GetDomainCount", ex);
            }
        }

        public Domain GetDomain(long id)
        {
            try
            {
                return Store.Domains.Get(id);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetDomain", ex);
            }
        }

        public Domain[] GetDomains(string[] domainNames, EntityStatus? status)
        {
            try
            {
                return Store.Domains.Get(domainNames, status);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetDomains", ex);
            }
        }

        public Domain[] GetAgentDomains(string agentName, EntityStatus? status)
        {
            try
            {
                return Store.Domains.Get(agentName, status);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetDomains", ex);
            }
        }


        public void RemoveDomain(string domainName)
        {
            try
            {
                Store.Domains.Remove(domainName);
            }
            catch (Exception ex)
            {
                throw CreateFault("RemoveDomain", ex);
            }
        }

        public Domain[] EnumerateDomains(string lastDomainName, int maxResults)
        {
            try
            {
                return Store.Domains.Get(lastDomainName, maxResults);
            }
            catch (Exception ex)
            {
                throw CreateFault("EnumerateDomains", ex);
            }
        }

        #endregion

        #region IAddressManager Members

        public Address AddAddress(Address address)
        {
            try
            {
                return Store.Addresses.Add(address);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddAddress", ex);
            }
        }

        public void AddAddresses(Address[] addresses)
        {
            try
            {
                Store.Addresses.Add(addresses);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddAddresses", ex);
            }
        }

        public void UpdateAddresses(Address[] addresses)
        {
            try
            {
                Store.Addresses.Update(addresses);
            }
            catch (Exception ex)
            {
                throw CreateFault("UpdateAddresses", ex);
            }
        }

        public int GetAddressCount(string domainName)
        {
            try
            {
                Domain domain = Store.Domains.Get(domainName);
                if (domain == null)
                {
                    return 0;
                }

                return Store.Addresses.Count(domain.ID);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetAddressCount", ex);
            }
        }

        public Address[] GetAddresses(string[] emailAddresses, EntityStatus? status)
        {
            try
            {
                return Store.Addresses.Get(emailAddresses, status);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetAddresses", ex);
            }
        }

        public Address[] GetAddressesOrDomain(string[] emailAddresses, EntityStatus? status)
        {
            try
            {
                return Store.Addresses.Get(emailAddresses, true, status);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetAddressesOrDomain", ex);
            }
        }
        

        public Address[] GetAddressesByID(long[] addressIDs, EntityStatus? status)
        {
            try
            {
                return Store.Addresses.Get(addressIDs, status);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetAddressesByID", ex);
            }
        }

        public void RemoveAddresses(string[] emailAddresses)
        {
            try
            {
                Store.Addresses.Remove(emailAddresses);
            }
            catch (Exception ex)
            {
                throw CreateFault("RemoveAddresses", ex);
            }
        }

        public void RemoveDomainAddresses(long domainID)
        {
            try
            {
                Store.Addresses.RemoveDomain(domainID);
            }
            catch (Exception ex)
            {
                throw CreateFault("RemoveDomainAddresses", ex);
            }
        }

        public void SetDomainAddressesStatus(long domainID, EntityStatus status)
        {
            try
            {
                Store.Addresses.SetStatus(domainID, status);
            }
            catch (Exception ex)
            {
                throw CreateFault("SetDomainAddressesStatus", ex);
            }
        }

        public Address[] EnumerateDomainAddresses(string domainName, string lastAddress, int maxResults)
        {
            try
            {
                Domain domain = Store.Domains.Get(domainName);
                if (domain == null)
                {
                    return null;
                }

                return Store.Addresses.Get(domain.ID, lastAddress, maxResults);
            }
            catch (Exception ex)
            {
                throw CreateFault("EnumerateDomainAddresses", ex);
            }
        }

        public Address[] EnumerateAddresses(string lastAddress, int maxResults)
        {
            try
            {
                return Store.Addresses.Get(lastAddress, maxResults);
            }
            catch (Exception ex)
            {
                throw CreateFault("EnumerateAddresses", ex);
            }
        }

        #endregion

        #region IDnsRecordManager Members

        public void AddDnsRecords(DnsRecord[] dnsRecords)
        {
            Store.DnsRecords.Add(dnsRecords);
        }

        public DnsRecord AddDnsRecord(DnsRecord record)
        {
            Store.DnsRecords.Add(record);
            return record;
        }

        public int Count(DnsStandard.RecordType? recordType)
        {
            return Store.DnsRecords.Count(recordType);
        }

        public DnsRecord[] GetLastDnsRecords(long lastRecordID, int maxResults, DnsStandard.RecordType typeID)
        {
            return  Store.DnsRecords.Get(lastRecordID
                , maxResults
                , typeID);
        }

        public DnsRecord GetDnsRecord(long recordID)
        {
            return  Store.DnsRecords.Get(recordID);
        }

        public DnsRecord[] GetDnsRecords(long[] recordIDs)
        {
            return Store.DnsRecords.Get(recordIDs);
        }

        public void RemoveDnsRecord(DnsRecord dnsRecord)
        {
            Store.DnsRecords.Remove(dnsRecord);
        }

        public void RemoveDnsRecordByID(long recordID)
        {
            Store.DnsRecords.Remove(recordID);
        }

        public void UpdateDnsRecord(DnsRecord dnsRecord)
        {
            Store.DnsRecords.Remove(dnsRecord);
        }

        public void UpdateDnsRecords(System.Collections.Generic.IEnumerable<DnsRecord> dnsRecords)
        {
            Store.DnsRecords.Update(dnsRecords);
        }

        public DnsRecord[] GetMatchingDnsRecords(string domainName)
        {
            return Store.DnsRecords.Get(domainName);
        }

        public DnsRecord[] GetMatchingDnsRecordsByType(string domainName
            , DnsStandard.RecordType typeID)
        {
            return Store.DnsRecords.Get(domainName
                , typeID);
        }

        public DnsRecord[] EnumerateDnsRecords(long lastID, int maxResults)
        {
            try
            {
                return Store.DnsRecords.Get(lastID, maxResults);
            }
            catch (Exception ex)
            {
                throw CreateFault("EnumerateDnsRecords", ex);
            }
        }

        public DnsRecord[] EnumerateDnsRecordsByType(long lastID, int maxResults, DnsStandard.RecordType type)
        {
            try
            {
                return Store.DnsRecords.Get(lastID, maxResults, type);
            }
            catch (Exception ex)
            {
                throw CreateFault("EnumerateDnsRecordsByType", ex);
            }
        }

        #endregion

        #region ICertPolcyStore
        public CertPolicy GetPolicyByName(string policyName)
        {
            try
            {
                return (Store.CertPolicies.Get(policyName));
            }
            catch (Exception ex)
            {
                throw CreateFault("GetPolicyByName", ex);
            }
        }

        public int GetCertPoliciesCount()
        {
            try
            {
                return Store.CertPolicies.Count();
            }
            catch (Exception ex)
            {
                throw CreateFault("GetCertPoliciesCount", ex);
            }
        }

        public CertPolicy GetPolicyByID(long policyID)
        {
            try
            {
                CertPolicy[] bundles = Store.CertPolicies.Get(new long[] {policyID});
                return (bundles[0]);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetBundle", ex);
            }
        }


        public CertPolicy[] GetIncomingPoliciesByOwner(string owner, CertPolicyUse use)
        {
            try
            {
                return Store.CertPolicies.GetIncomingByOwner(owner, use);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetIncomingPoliciesByOwnerAndUse", ex);
            }
        }

        public CertPolicy[] GetOutgoingPoliciesByOwner(string owner, CertPolicyUse use)
        {
            try
            {
                return Store.CertPolicies.GetOutgoingByOwner(owner, use);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetOutgoingPoliciesByOwner", ex);
            }
        }

        public bool PolicyToGroupExists(string policyName, string groupName, CertPolicyUse policyUse, bool incoming, bool outgoing)
        {
            try
            {
                return Store.CertPolicyGroups.PolicyGroupMapExists(policyName, groupName, policyUse, incoming, outgoing);
            }
            catch (Exception ex)
            {
                throw CreateFault("PolicyToGroupExists", ex);
            }
        }


        public CertPolicy[] EnumerateCertPolicies(long lastCertPolicyID, int maxResults)
        {
            try
            {
                return Store.CertPolicies.Get(lastCertPolicyID, maxResults);
            }
            catch (Exception ex)
            {
                throw CreateFault("EnumerateCertPolicies", ex);
            }
        }

        public CertPolicy AddPolicy(CertPolicy policy)
        {
            try
            {
                CertPolicy certPolicy = new CertPolicy(policy);
                return Store.CertPolicies.Add(certPolicy);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddPolicy", ex);
            }
        }

        public void RemovePolicies(long[] policyIDs)
        {
            try
            {
                Store.CertPolicies.Remove(policyIDs);
            }
            catch (Exception ex)
            {
                throw CreateFault("RemovePolicies", ex);
            }
        }

        public void UpdatePolicyAttributes(CertPolicy policy)
        {
            try
            {
                Store.CertPolicies.Update(policy);
            }
            catch (Exception ex)
            {
                throw CreateFault("UpdatePolicyAttributes", ex);
            }
        }

        public CertPolicyGroup[] EnumerateCertPolicyGroups(long lastCertPolicyGroupID, int maxResults)
        {
            try
            {
                return Store.CertPolicyGroups.Get(lastCertPolicyGroupID, maxResults);
            }
            catch (Exception ex)
            {
                throw CreateFault("EnumerateCertPolicyGroups", ex);
            }
        }

        public CertPolicyGroup GetPolicyGroupByName(string policyGroupName)
        {
            try
            {
                return Store.CertPolicyGroups.Get(policyGroupName);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetPolicyGroupByName", ex);
            }
        }

        public CertPolicyGroup GetPolicyGroupByID(long policyGroupID)
        {
            try
            {
                return Store.CertPolicyGroups.Get(policyGroupID);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetPolicyGroupByID", ex);
            }
        }

        public CertPolicyGroup AddPolicyGroup(CertPolicyGroup policyGroup)
        {
            try
            {
                CertPolicyGroup certPolicyGroup = new CertPolicyGroup(policyGroup);
                return Store.CertPolicyGroups.Add(certPolicyGroup);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddPolicyGroup", ex);
            }
        }

        public void RemovePolicyGroups(long[] policyGroupIDs)
        {
            try
            {
                Store.CertPolicyGroups.Remove(policyGroupIDs);
            }
            catch (Exception ex)
            {
                throw CreateFault("RemovePolicyGroups", ex);
            }
        }

        public void UpdateGroupAttributes(CertPolicyGroup policyGroup)
        {
            try
            {
                Store.CertPolicyGroups.Update(policyGroup);
            }
            catch (Exception ex)
            {
                throw CreateFault("UpdateGroupAttributes", ex);
            }
        }

        public void AddPolicyToGroup(string policyName, string groupName, CertPolicyUse policyUse, bool incoming, bool outgoing)
        {
            try
            {
                CertPolicy certPolicy = Store.CertPolicies.Get(policyName);
                if (certPolicy == null)
                {
                    throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyName);
                }
                CertPolicyGroup policyGroup = Store.CertPolicyGroups.Get(groupName);
                if (policyGroup == null)
                {
                    throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyGroupName);
                }
                //todo: hacky.  Should look closer at CreateFault in the throw below to see if we can accuratly examine this exception
                //and turn it into a ConfigStoreError.UniqueConstraint 
                if (PolicyToGroupExists(policyName, groupName, policyUse, incoming, outgoing))
                {
                    ConfigStoreFault fault = new ConfigStoreFault(ConfigStoreError.UniqueConstraint,
                        "Duplicate policy to group mapping");
                    throw new FaultException<ConfigStoreFault>(fault, new FaultReason(fault.ToString()));
                }
                policyGroup.CertPolicies.Add(new CertPolicy(certPolicy));
                policyGroup.CertPolicyGroupMaps.First(m => m.IsNew).Use = policyUse;
                policyGroup.CertPolicyGroupMaps.First(m => m.IsNew).ForIncoming = incoming;
                policyGroup.CertPolicyGroupMaps.First(m => m.IsNew).ForOutgoing = outgoing;

                Store.CertPolicyGroups.AddAssociation(policyGroup);
            }
            catch (FaultException faultEx)
            {
                throw faultEx;
            }
            catch (Exception ex)
            {
                throw CreateFault("AddPolicyToGroup", ex);
            }
        }

        public void RemovePolicyUseFromGroup(long policyGroupMapID)
        {
            try
            {
                Store.CertPolicyGroups.RemovePolicyUseFromGroup(policyGroupMapID);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddPolicy", ex);
            }
        }

        public void AssociatePolicyGroupToDomain(string owner, long policyGroupID)
        {
            try
            {
                Store.CertPolicyGroups.AssociateToDomain(owner, policyGroupID);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddPolicy", ex);
            }
        }

        public void DisassociatePolicyGroupFromDomain(string owner, long policyGroupID)
        {
            try
            {
                Store.CertPolicyGroups.DissAssociateFromDomain(owner, policyGroupID);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddPolicy", ex);
            }
        }

        public void DisassociatePolicyGroupsFromDomain(string owner)
        {
            try
            {
                Store.CertPolicyGroups.DissAssociateFromDomain(owner);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddPolicy", ex);
            }
        }

        public void DisassociatePolicyGroupFromDomains(long policyGroupID)
        {
            try
            {
                Store.CertPolicyGroups.DissAssociateFromDomains(policyGroupID);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddPolicy", ex);
            }
        }

        public CertPolicyGroup[] GetPolicyGroupsByDomain(string owner)
        {
            try
            {
                return (Store.CertPolicyGroups.GetByDomains(new[] {owner}));
            ;
            }
            catch (Exception ex)
            {
                throw CreateFault("GetPolicyByName", ex);
            }
        }


        #endregion
    }
}