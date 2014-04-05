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

namespace Health.Direct.Common.DnsResolver
{
    /// <summary>
    /// Represents a CNAME DNS RDATA
    /// </summary>
    /// <remarks>
    /// See RFC 1035, Section 3.3.1
    /// 
    /// Data layout:
    /// <code>
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// /                     CNAME                     /
    /// /                                               /
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// </code>
    /// </remarks>
    public class CNameRecord : DnsResourceRecord
    {
        string m_name;
        
        internal CNameRecord()
        {
        }
        
        /// <summary>
        /// Create a new CNameRecord
        /// </summary>
        /// <param name="name">the domain name for which this is a record</param>
        /// <param name="cname">the cname (alias) for this domain</param>
        public CNameRecord(string name, string cname)
            : base(name, DnsStandard.RecordType.CNAME)
        {
            this.CName = cname;
        }
        
        /// <summary>
        /// Gets and sets the CName as a string (a dotted domain name)
        /// </summary>
        public string CName
        {
            get
            {
                return m_name;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidCNameRecord);
                }
                
                m_name = value;
            }
        }

        /// <summary>
        /// Tests equality between this CName record and the other <paramref name="record"/>.
        /// </summary>
        /// <param name="record">The other record.</param>
        /// <returns><c>true</c> if the RRs are equal, <c>false</c> otherwise.</returns>
        public override bool Equals(DnsResourceRecord record)
        {
            if (!base.Equals(record))
            {
                return false;
            }

            CNameRecord cnameRecord = record as CNameRecord;
            if (cnameRecord == null)
            {
                return false;
            }

            return (DnsStandard.Equals(m_name, cnameRecord.CName));
        }
        
        /// <summary>
        /// Serialize the CName record
        /// </summary>
        /// <param name="buffer"></param>
        protected override void SerializeRecordData(DnsBuffer buffer)
        {
            buffer.AddDomainName(m_name);
        }
        /// <summary>
        /// Creates an instance from the DNS message from a DNS reader.
        /// </summary>
        /// <param name="reader">The DNS reader</param>
        protected override void DeserializeRecordData(ref DnsBufferReader reader)
        {
            m_name = reader.ReadDomainName();
        }
    }
}