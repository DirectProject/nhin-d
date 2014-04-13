/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;

using Health.Direct.Common.DnsResolver;

namespace Health.Direct.DnsResponder
{
    /// <summary>
    /// A trivial Memory Dns Store.
    /// </summary>
    public class MemoryStore : IDnsStore
    {
        DnsRecordTable m_records;
        
        public MemoryStore()
            : this(0)
        {
        }
        
        public MemoryStore(int capacity)
        {
            m_records = new DnsRecordTable(capacity);
        }
        
        public DnsRecordTable Records
        {
            get
            {
                return m_records;
            }
        }
        
        public DnsResponse Get(DnsRequest request)
        {
            if (request == null)
            {
                throw new ArgumentNullException();
            }
            
            DnsQuestion question = request.Question;
            if (question == null || question.Class != DnsStandard.Class.IN)
            {
                return null;
            }
            
            IEnumerable<DnsResourceRecord> matches = m_records[request.Question.Domain];
            if (matches == null)
            {
                return null;
            }
            
            return this.CreateResponse(request, matches);
        }
        
        DnsResponse CreateResponse(DnsRequest request, IEnumerable<DnsResourceRecord> matches)
        {
            DnsStandard.RecordType questionType = request.Question.Type;
            DnsResponse response = new DnsResponse(request);
            int matchCount = 0;
            foreach (DnsResourceRecord record in matches)
            {
                if (record.Type == questionType)
                {
                    ++matchCount;
                    switch (record.Type)
                    {
                        default:
                            response.AnswerRecords.Add(record);
                            break;

                        case DnsStandard.RecordType.NS:
                        case DnsStandard.RecordType.SOA:
                            response.AnswerRecords.Add(record);
                            break;
                    }
                }
            }
            
            if (matchCount == 0)
            {
                throw new DnsServerException(DnsStandard.ResponseCode.NameError);
            }
            
            return response;
        }
    }
}