/* 
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

namespace DnsResolver
{
    
    /// <summary>
    /// Represents RDATA for an MX DNS RR.
    /// </summary>
    /// <remarks>
    /// RFC 1035, 3.3.9. MX RDATA format
    /// <code>
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// |                  PREFERENCE                   |
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// /                   EXCHANGE                    /
    /// /                                               /
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// </code>
    /// where:
    ///
    /// PREFERENCE      A 16 bit integer which specifies the preference given to
    ///                 this RR among others at the same owner.  Lower values
    ///                 are preferred.
    ///
    /// EXCHANGE        A %lt;domain-name%gt; which specifies a host willing to act as
    ///                 a mail exchange for the owner name.
    /// </remarks>
    public class MXRecord : DnsResourceRecord
    {
        string m_exchange;

        internal MXRecord()
        {
        }
        
        public MXRecord(string name, string exchange)
            : this(name, exchange, 10)
        {
        }
        
        public MXRecord(string name, string exchange, short preference)
            : base(name, Dns.RecordType.MX)
        {
            this.Preference = preference;
            this.Exchange = exchange;
        }
        
        public short Preference
        {
            get;
            set;
        }

        /// <summary>
        /// The mail exchange (SMTP server) domain name
        /// </summary>
        /// <value>A <see cref="string"/> representation of the domain name.</value>
        public string Exchange
        {
            get
            {
                return m_exchange;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidMXRecord);
                }
                
                m_exchange = value;
            }
        }

        public override bool Equals(DnsResourceRecord record)
        {
            if (!base.Equals(record))
            {
                return false;
            }
            
            MXRecord mxRecord = record as MXRecord;
            if (mxRecord == null)
            {
                return false;
            }
            
            return (
                    Dns.Equals(m_exchange, mxRecord.m_exchange)
                &&  this.Preference == mxRecord.Preference
            );
        }
        
        protected override void SerializeRecordData(DnsBuffer buffer)
        {
            buffer.AddShort(this.Preference);
            buffer.AddDomainName(m_exchange);
        }
        
        protected override void DeserializeRecordData(ref DnsBufferReader reader)
        {
            this.Preference = reader.ReadShort();
            this.Exchange = reader.ReadDomainName();
        }
    }
}
