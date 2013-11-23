/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Sean Nolan      seannol@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System.Collections.Generic;

namespace Health.Direct.Common.DnsResolver
{
    /// <summary>
    /// Encapsulates a collection of RRs
    /// </summary>
    public class DnsResourceRecordCollection : List<DnsResourceRecord>
    {
        /// <summary>
        /// Initializes an empty collection.
        /// </summary>
        public DnsResourceRecordCollection()
        {
        }
        
        /// <summary>
        /// Initializes an empty collection of the specified <paramref name="capacity"/>
        /// </summary>
        public DnsResourceRecordCollection(int capacity)
            : base(capacity)
        {
        }
        
        /// <summary>
        /// Provides an eumeration of raw records.
        /// </summary>
        public IEnumerable<RawRecord> Raw
        {
            get
            {
                for (int i = 0, count = this.Count; i < count; ++i)
                {
                    RawRecord raw = this[i] as RawRecord;
                    if (raw != null)
                    {
                        yield return raw;
                    }
                }
            }
        }
        
        /// <summary>
        /// Provides an enumeration of contained A RRs
        /// </summary>
        public IEnumerable<AddressRecord> A
        {
            get
            {
                return this.Enumerate<AddressRecord>(DnsStandard.RecordType.ANAME);
            }
        }

        /// <summary>
        /// Provides an enumeration of contained PTR RRs
        /// </summary>
        public IEnumerable<PtrRecord> PTR
        {
            get
            {
                return this.Enumerate<PtrRecord>(DnsStandard.RecordType.PTR);
            }
        }

        /// <summary>
        /// Provides an enumeration of contained PTR RRs
        /// </summary>
        public IEnumerable<NSRecord> NS
        {
            get
            {
                return this.Enumerate<NSRecord>(DnsStandard.RecordType.NS);
            }
        }

        /// <summary>
        /// Provides an enumeration of contained MX RRs
        /// </summary>
        public IEnumerable<MXRecord> MX
        {
            get
            {
                return this.Enumerate<MXRecord>(DnsStandard.RecordType.MX);
            }
        }

        /// <summary>
        /// Provides an enumeration of contained TXT RRs
        /// </summary>
        public IEnumerable<TextRecord> TXT
        {
            get
            {
                return this.Enumerate<TextRecord>(DnsStandard.RecordType.TXT);
            }
        }

        /// <summary>
        /// Provides an enumeration of contained CERT RRs
        /// </summary>
        public IEnumerable<CertRecord> CERT
        {
            get
            {
                return this.Enumerate<CertRecord>(DnsStandard.RecordType.CERT);
            }
        }

        /// <summary>
        /// Provides an enumeration of contained SOA RRs
        /// </summary>
        public IEnumerable<SOARecord> SOA
        {
            get
            {
                return this.Enumerate<SOARecord>(DnsStandard.RecordType.SOA);
            }
        }
        
        /// <summary>
        /// Provides an enumeration of contained SRV RRs
        /// </summary>
        public IEnumerable<SRVRecord> SRV
        {
            get 
            { 
                return this.Enumerate<SRVRecord>(DnsStandard.RecordType.SRV);
            }
        }
        
        /// <summary>
        /// Provides an enumeration of records of the specified type.
        /// </summary>
        /// <typeparam name="T">The RR type</typeparam>
        /// <param name="type">The RR type to enumerate</param>
        /// <returns>The enumeration of RRs of the specified type.</returns>
        public IEnumerable<T> Enumerate<T>(DnsStandard.RecordType type)
            where T : DnsResourceRecord
        {
            foreach(DnsResourceRecord record in this)
            {
                if (record.Type == type)
                {
                    T typedRecord = record as T;
                    if (typedRecord != null)
                    {
                        yield return typedRecord;
                    }
                }
            }
        }
        
        internal void Serialize(DnsBuffer buffer)
        {
            for (int i = 0, count = this.Count; i < count; ++i)
            {
                this[i].Serialize(buffer);
            }
        }
        
        internal void Deserialize(int recordCount, ref DnsBufferReader reader)
        {
            if (recordCount < 0)
            {
                throw new DnsProtocolException(DnsProtocolError.InvalidRecordCount);
            }

            if (recordCount > 0)
            {
                this.EnsureCapacity(recordCount);
                for (int irecord = 0; irecord < recordCount; ++irecord)
                {
                    this.Add(DnsResourceRecord.Deserialize(ref reader));
                }
            }
        }
        
        internal void EnsureCapacity(int capacity)
        {
            if (capacity > this.Capacity)
            {
                this.Capacity = capacity;
            }
        }
        
        internal int GetMinTTL()
        {
            int ttl = int.MaxValue;
            for (int i = 0, count = this.Count; i < count; ++i)
            {
                int newTTL = this[i].TTL;
                if (newTTL < ttl)
                {
                    ttl = newTTL;
                }
            }
            
            return (ttl == int.MaxValue || ttl < 0) ? 0 : ttl;
        }
    }
}