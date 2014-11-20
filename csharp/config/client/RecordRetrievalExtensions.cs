/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico  (chris.lomonico@surescripts.com)
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Net.Mail;

using Health.Direct.Common.Extensions;
using Health.Direct.Config.Client.RecordRetrieval;
using Health.Direct.Config.Store;
using Health.Direct.Common.DnsResolver;

namespace Health.Direct.Config.Client
{
    public static class RecordRetrievalExtensions
    {
        public static void GetANAMERecords(this RecordRetrievalServiceClient client, string domain, DnsResourceRecordCollection recordCollection)
        {
            client.GetMatches(domain, recordCollection, DnsStandard.RecordType.ANAME);
        }

        public static void GetMXRecords(this RecordRetrievalServiceClient client, string domain, DnsResourceRecordCollection recordCollection)
        {
            client.GetMatches(domain, recordCollection, DnsStandard.RecordType.MX);
        }

        public static void GetSOARecords(this RecordRetrievalServiceClient client, string domain, DnsResourceRecordCollection recordCollection)
        {
            client.GetMatches(domain, recordCollection, DnsStandard.RecordType.SOA);
        }

        public static void GetCNAMERecords(this RecordRetrievalServiceClient client, string domain, DnsResourceRecordCollection recordCollection)
        {
            client.GetMatches(domain, recordCollection, DnsStandard.RecordType.CNAME);
        }

        public static void GetNSRecords(this RecordRetrievalServiceClient client, string domain, DnsResourceRecordCollection recordCollection)
        {
            client.GetMatches(domain, recordCollection, DnsStandard.RecordType.NS);
        }

        public static void GetMatches(this RecordRetrievalServiceClient client, string domain, DnsResourceRecordCollection resourceRecords, DnsStandard.RecordType recordType)
        {
            DnsRecord[] matches = client.GetMatchingDnsRecords(domain, recordType);
            if (matches.IsNullOrEmpty())
            {
                return;
            }

            foreach (DnsRecord record in matches)
            {
                DnsResourceRecord responseRecord = record.Deserialize();
                if (responseRecord != null && responseRecord.Type == recordType)
                {
                    resourceRecords.Add(responseRecord);
                }
            }
        }
    }
}
