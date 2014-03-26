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
using System.Linq;

using Health.Direct.Common.DnsResolver;

namespace Health.Direct.DnsResponder
{
    /// <summary>
    /// A trivial in-memory record store 
    /// </summary>
    public class DnsRecordTable
    {
        Dictionary<string, DnsResourceRecordCollection> m_records;
        
        public DnsRecordTable()
            : this(0)
        {
        }
        
        public DnsRecordTable(int capacity)
        {
            m_records = new Dictionary<string, DnsResourceRecordCollection>(capacity, StringComparer.OrdinalIgnoreCase);
        }
        
        public int Count
        {
            get
            {
                return m_records.Count;
            }
        }
                
        public IEnumerable<DnsResourceRecord> this[string domainName]
        {
            get
            {
                if (string.IsNullOrEmpty(domainName))
                {
                    throw new ArgumentException("domainName");
                }
                
                DnsResourceRecordCollection matches = null;
                if (m_records.TryGetValue(domainName, out matches))
                {
                    return matches;
                }
                
                return null;
            }
        }

        public IEnumerable<DnsResourceRecord> this[string domainName, DnsStandard.RecordType type]
        {
            get
            {
                IEnumerable<DnsResourceRecord> matches = this[domainName];
                if (matches == null)
                {
                    return null;
                }
                
                return (
                           from record in matches
                           where record.Type == type
                           select record
                       );
            }
        }
        
        public IEnumerable<string> Domains
        {
            get
            {
                return m_records.Keys;
            }
        }
        
        public IEnumerable<KeyValuePair<string, DnsResourceRecordCollection>> DomainRecords
        {
            get
            {
                return m_records;
            }
        }
        
        public IEnumerable<DnsResourceRecord> Records
        {
            get
            {
                foreach(DnsResourceRecordCollection recordList in m_records.Values)
                {
                    foreach(DnsResourceRecord record in recordList)
                    {
                        yield return record;
                    }
                }
            }
        }
        
        public void Add(DnsResourceRecord record)
        {
            if (record == null)
            {
                throw new ArgumentNullException();
            }
            
            DnsResourceRecordCollection recordList;
            if (!m_records.TryGetValue(record.Name, out recordList))
            {
                recordList = new DnsResourceRecordCollection();
                m_records.Add(record.Name, recordList);
            }
            
            recordList.Add(record);
        }
        
        public void TrimToSize()
        {
            foreach(DnsResourceRecordCollection records in m_records.Values)
            {
                records.TrimExcess();
            }
        }
    }
}