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
    /// Represents NS DNS RDATA
    /// </summary>
    /// <remarks>
    /// RFC 1035, Section 3.3.11, NS RDATA format
    /// <code>
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// /                   NSDNAME                     /
    /// /                                               /
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// </code>
    ///
    /// where:
    /// 
    /// NSDNAME         A &lt;domain-name&gt; which specifies a host which should be
    ///                 authoritative for the specified class and domain.
    /// </remarks>
    public class NSRecord : DnsResourceRecord
    {
        string m_nameserver;
        
        internal NSRecord()
        {
        }
        
        /// <summary>
        /// Initializes an instance with the supplied data.
        /// </summary>
        /// <param name="name">The domain name for which this is a record</param>
        /// <param name="nameserver">The authoritative nameserver for this domain.</param>
        public NSRecord(string name, string nameserver)
            : base(name, DnsStandard.RecordType.NS)
        {
            this.NameServer = nameserver;
        }
        
        /// <summary>
        /// The authoritative nameserver for this domain.
        /// </summary>
        public string NameServer
        {
            get
            {
                return m_nameserver;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidNSRecord);
                }
                
                m_nameserver = value;
            }
        }

        /// <summary>
        /// Tests equality between this NS record and the other <paramref name="record"/>.
        /// </summary>
        /// <param name="record">The other record.</param>
        /// <returns><c>true</c> if the RRs are equal, <c>false</c> otherwise.</returns>
        public override bool Equals(DnsResourceRecord record)
        {
            if (!base.Equals(record))
            {
                return false;
            }
            
            NSRecord nsRecord = record as NSRecord;
            if (nsRecord == null)
            {
                return false;
            }
            
            return (DnsStandard.Equals(m_nameserver, nsRecord.NameServer));
        }

        /// <summary>
        /// Writes this RR in DNS wire format to the <paramref name="buffer"/>
        /// </summary>
        /// <param name="buffer">The buffer to which DNS wire data are written</param>
        protected override void SerializeRecordData(DnsBuffer buffer)
        {
            buffer.AddDomainName(m_nameserver);
        }

        /// <summary>
        /// Reads data into this RR from the DNS wire format data in <paramref name="reader"/>
        /// </summary>
        /// <param name="reader">Reader in which wire format data for this RR is already buffered.</param>
        protected override void DeserializeRecordData(ref DnsBufferReader reader)
        {
            this.NameServer = reader.ReadDomainName();
        }
    }
}