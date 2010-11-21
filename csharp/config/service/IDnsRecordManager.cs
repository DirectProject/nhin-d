/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico     chris.lomonico@surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System.ServiceModel;

using Health.Direct.Common.DnsResolver;
using Health.Direct.Config.Store;

namespace Health.Direct.Config.Service
{
    [ServiceContract(Namespace = Service.Namespace)]
    public interface IDnsRecordManager
    {
        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void AddDnsRecords(DnsRecord[] dnsRecords);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        DnsRecord AddDnsRecord(DnsRecord record);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        int Count(DnsStandard.RecordType? recordType);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        DnsRecord[] GetLastDnsRecords(long lastRecordID
            , int maxResults
            , DnsStandard.RecordType typeID);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        DnsRecord GetDnsRecord(long recordID);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        DnsRecord[] GetDnsRecords(long[] recordIDs);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        DnsRecord[] GetMatchingDnsRecords(string domainName);


        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        DnsRecord[] GetMatchingDnsRecordsByType(string domainName
            , DnsStandard.RecordType typeID);


        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void RemoveDnsRecord(DnsRecord dnsRecord);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void RemoveDnsRecordByID(long recordID);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void UpdateDnsRecord(DnsRecord dnsRecord);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void UpdateDnsRecords(System.Collections.Generic.IEnumerable<DnsRecord> dnsRecords);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        DnsRecord[] EnumerateDnsRecords(long lastID, int maxResults);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        DnsRecord[] EnumerateDnsRecordsByType(long lastID, int maxResults, DnsStandard.RecordType type);
    }
}