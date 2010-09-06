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
    /// Represents an SOA DNS RR
    /// </summary>
    /// <remarks>
    /// RFC 1035, 
    /// <code>
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// /                     MNAME                     /
    /// /                                               /
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// /                     RNAME                     /
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// |                    SERIAL                     |
    /// |                                               |
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// |                    REFRESH                    |
    /// |                                               |
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// |                     RETRY                     |
    /// |                                               |
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// |                    EXPIRE                     |
    /// |                                               |
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// |                    MINIMUM                    |
    /// |                                               |
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// </code>
    /// 
    /// where:
    /// 
    /// MNAME           The &lt;domain-name&gt; of the name server that was the
    ///                 original or primary source of data for this zone.
    /// 
    /// RNAME           A &lt;domain-name&gt; which specifies the mailbox of the
    ///                 person responsible for this zone.
    /// 
    /// SERIAL          The unsigned 32 bit version number of the original copy
    ///                 of the zone.  Zone transfers preserve this value.  This
    ///                 value wraps and should be compared using sequence space
    ///                 arithmetic.
    /// 
    /// REFRESH         A 32 bit time interval before the zone should be
    ///                 refreshed.
    /// 
    /// RETRY           A 32 bit time interval that should elapse before a
    ///                 failed refresh should be retried.
    /// 
    /// EXPIRE          A 32 bit time value that specifies the upper limit on
    ///                 the time interval that can elapse before the zone is no
    ///                 longer authoritative.
    ///                 
    /// MINIMUM         The unsigned 32 bit minimum TTL field that should be
    ///                 exported with any RR from this zone.
    /// </remarks>
    public class SOARecord : DnsResourceRecord
    {
        string m_mname;
        string m_rname;
        
        internal SOARecord()
        {
        }
        
        /// <summary>
        /// Gets and sets the domain name for this SOA (MNAME)
        /// </summary>
        /// <value>A <see cref="string"/> representation of the domain name</value>
        public string DomainName
        {
            get
            {
                return m_mname;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidSOARecord);
                }
                m_mname = value;
            }
        }

        /// <summary>
        /// Gets and sets the mailbox of the responsible name for this SOA (MNAME)
        /// </summary>
        /// <value>A <see cref="string"/> representation of the email or mailbox name (generally hostmaster)</value>
        public string ResponsibleName
        {
            get
            {
                return m_rname;
            }
            set
            {
                if (value == null)
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidSOARecord);
                }
                
                m_rname = value;
            }
        }
        
        /// <summary>
        /// Gets and sets the serial number for this SOA (SERIAL)
        /// </summary>
        public int SerialNumber
        {
            get;
            set;
        }

        /// <summary>
        /// Gets and sets the refresh interval
        /// </summary>
        /// <value>A 32-bit value representing number of seconds</value>
        public int Refresh
        {
            get;
            set;
        }

        /// <summary>
        /// Gets and sets the retry interval
        /// </summary>
        /// <value>A 32-bit value representing number of seconds</value>
        public int Retry
        {
            get;
            set;
        }

        /// <summary>
        /// Gets and sets the expiry interval
        /// </summary>
        /// <value>A 32-bit value representing number of seconds</value>
        public int Expire
        {
            get;
            set;
        }

        /// <summary>
        /// Gets and sets the minimum TTL
        /// </summary>
        /// <value>A 32-bit value representing number of seconds</value>
        public int Minimum
        {
            get;
            set;
        }

        /// <summary>
        /// Reads values into this instance from the reader
        /// </summary>
        /// <param name="reader">A reader which has a buffer already filled with raw data for this RR.</param>
        protected override void DeserializeRecordData(ref DnsBufferReader reader)
        {
            this.DomainName = reader.ReadString();
            this.ResponsibleName = reader.ReadString();
            this.SerialNumber = reader.ReadInt();
            this.Refresh = reader.ReadInt();
            this.Retry = reader.ReadInt();
            this.Expire = reader.ReadInt();
            this.Minimum = reader.ReadInt();
        }
    }
}
