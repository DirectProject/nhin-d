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
using System;
using System.Net;

namespace Health.Direct.Common.DnsResolver
{
    /// <summary>
    /// Represents a DNS A record RDATA
    /// </summary>
    /// <remarks>
    /// RFC 1035, 3.4.1
    ///+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    ///|                    ADDRESS                    |
    ///+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// </remarks>
    public class AddressRecord : DnsResourceRecord
    {
        uint m_address;
        IPAddress m_ipAddress;        
        
        internal AddressRecord()
        {
        }
        
        /// <summary>
        /// Initializes a new instance with the supplied data.
        /// </summary>
        /// <param name="name">the domain name for which this is a record</param>
        /// <param name="address">The address as a 32-bit integer</param>
        public AddressRecord(string name, uint address)
            : base(name, DnsStandard.RecordType.ANAME)
        {
            this.Address = address;
        }

        /// <summary>
        /// Initializes a new instance with the supplied data
        /// </summary>
        /// <param name="name">the domain name for which this is a record</param>
        /// <param name="address">IP4 address</param>
        public AddressRecord(string name, IPAddress address)
            : this(name, address.ToIPV4())
        {
        }
        
        /// <summary>
        /// Initializes a new instance with the supplied data
        /// </summary>
        /// <param name="name">the domain name for which this is a record</param>
        /// <param name="address">IP address in dot notation</param>
        public AddressRecord(string name, string address)
            : this(name, IPAddress.Parse(address))
        {
        }
        
        /// <summary>
        /// Gets and sets the address as a 32-bit integer.
        /// </summary>
        public uint Address
        {
            get
            {
                return m_address;
            }
            set
            {
                try
                {
                    //
                    // IPAddress constructor expects Host in NETWORK order!
                    //
                    IPAddress address = new IPAddress((uint)IPAddress.HostToNetworkOrder((int)value));
                    m_address = value;
                    m_ipAddress = address;
                }
                catch(Exception ex) 
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidARecord, ex);
                }
            }
        }
        
        /// <summary>
        /// Gets the address as an <see cref="IPAddress"/>
        /// </summary>
        public IPAddress IPAddress
        {
            get
            {
                return m_ipAddress;
            }
        }

        /// <summary>
        /// Tests equality between this TXT record and the other <paramref name="record"/>.
        /// </summary>
        /// <param name="record">The other record.</param>
        /// <returns><c>true</c> if the RRs are equal, <c>false</c> otherwise.</returns>
        public override bool Equals(DnsResourceRecord record)
        {
            if (!base.Equals(record))
            {
                return false;
            }
            
            AddressRecord addressRecord = record as AddressRecord;
            if (addressRecord == null)
            {
                return false;
            }
            
            return (this.Address == addressRecord.Address);
        }

        /// <summary>
        /// Writes this RR in DNS wire format to the <paramref name="buffer"/>
        /// </summary>
        /// <param name="buffer">The buffer to which DNS wire data are written</param>
        protected override void SerializeRecordData(DnsBuffer buffer)
        {
            buffer.AddUint(this.Address);
        }

        /// <summary>
        /// Reads data into this RR from the DNS wire format data in <paramref name="reader"/>
        /// </summary>
        /// <param name="reader">Reader in which wire format data for this RR is already buffered.</param>
        protected override void DeserializeRecordData(ref DnsBufferReader reader)
        {
            this.Address = reader.ReadUint();
        }
    }
}