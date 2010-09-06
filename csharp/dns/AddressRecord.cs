﻿/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Sean Nolan      seannol@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;

namespace DnsResolver
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
        
        public AddressRecord(string name, uint address)
            : base(name, Dns.RecordType.ANAME)
        {
            this.Address = address;
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
        
        protected override void SerializeRecordData(DnsBuffer buffer)
        {
            buffer.AddUint(this.Address);
        }
        
        protected override void DeserializeRecordData(ref DnsBufferReader reader)
        {
            this.Address = reader.ReadUint();
        }
    }
}
