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
        public CertPolicy[] GetPolicies()
        {
            throw new NotImplementedException();
        }

        public CertPolicy GetPolicyByName(string policyName)
        {
            throw new NotImplementedException();
        }

        public CertPolicy GetPolicyById(long policyId)
        {
            throw new NotImplementedException();
        }

        public CertPolicy[] GetPoliciesByDomain(string domain, bool incoming)
        {
            throw new NotImplementedException();
        }


        public CertPolicy AddPolicy(CertPolicy policy)
        {
            throw new NotImplementedException();
        }

        public void RemovePolicies(long[] policyIds)
        {
            throw new NotImplementedException();
        }

        public void UpdatePolicyAttributes(long policyId, string policyName, byte[] policyData, string Description)
        {
            throw new NotImplementedException();
        }

        public CertPolicyGroup[] GetPolicyGroups()
        {
            throw new NotImplementedException();
        }

        public CertPolicyGroup GetPolicyGroupByName(string policyGroupName)
        {
            throw new NotImplementedException();
        }

        public CertPolicyGroup GetPolicyGroupById(long policyGroupId)
        {
            throw new NotImplementedException();
        }

        public CertPolicyGroup AddPolicyGroup(CertPolicyGroup policy)
        {
            throw new NotImplementedException();
        }

        public void RemovePolicyGroups(long[] policyGroupIds)
        {
            throw new NotImplementedException();
        }

        public void UpdateGroupAttributes(long policyGroupId, string policyGroupName, string Description)
        {
            throw new NotImplementedException();
        }

        public void AddPolicyUseToGroup(long policyGroupId, long policyId, CertPolicyUse policyUse, bool incoming, bool outgoing)
        {
            throw new NotImplementedException();
        }

        public void RemovePolicyUseFromGroup(long policyGroupMapId)
        {
            throw new NotImplementedException();
        }

        public void AssociatePolicyGroupToDomain(string domain, long policyGroupId)
        {
            throw new NotImplementedException();
        }

        public void DisassociatePolicyGroupFromDomain(string domain, long policyGroupId)
        {
            throw new NotImplementedException();
        }

        public void DisassociatePolicyGroupsFromDomain(string domain)
        {
            throw new NotImplementedException();
        }

        public void DisassociatePolicyGroupFromDomains(long policyGroupId)
        {
            throw new NotImplementedException();
        }

        public CertPolicyGroupDomainMap[] GetPolicyGroupDomainMap()
        {
            throw new NotImplementedException();
        }

        public CertPolicyGroup[] GetPolicyGroupsByDomain(string domain)
        {
            throw new NotImplementedException();
        }

        #endregion
    }
}