/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico  (chris.lomonico@surescripts.com)
  
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

        public static void GetANAMEDnsRecords(this RecordRetrievalServiceClient client
            , DnsResponse response)
        {
            DnsRecord[] records = client.GetMatchingDnsRecords(response.Question.Domain
                , DnsStandard.RecordType.ANAME);
            foreach (DnsRecord record in records)
            {
                DnsBufferReader rdr = new DnsBufferReader(record.RecordData
                    , 0
                    , record.RecordData.Length);
                AddressRecord addressrec = AddressRecord.Deserialize(ref rdr) as AddressRecord;
                if (addressrec != null)
                {
                    response.AnswerRecords.Add(addressrec);
                }
            }

        }

        public static void GetMXDnsRecords(this RecordRetrievalServiceClient client
            , DnsResponse response)
        {
            DnsRecord[] records = client.GetMatchingDnsRecords(response.Question.Domain
                , DnsStandard.RecordType.MX);
            foreach (DnsRecord record in records)
            {
                DnsBufferReader rdr = new DnsBufferReader(record.RecordData
                    , 0
                    , record.RecordData.Length);
                MXRecord addressrec = MXRecord.Deserialize(ref rdr) as MXRecord;
                if (addressrec != null)
                {
                    response.AnswerRecords.Add(addressrec);
                }
            }
        }


        public static void GetSOADnsRecords(this RecordRetrievalServiceClient client
            , DnsResponse response)
        {
            DnsRecord[] records = client.GetMatchingDnsRecords(response.Question.Domain
                , DnsStandard.RecordType.SOA);
            foreach (DnsRecord record in records)
            {
                DnsBufferReader rdr = new DnsBufferReader(record.RecordData
                    , 0
                    , record.RecordData.Length);
                SOARecord addressrec = SOARecord.Deserialize(ref rdr) as SOARecord;
                if (addressrec != null)
                {
                    response.AnswerRecords.Add(addressrec);
                }
            }
        }
    }
}
