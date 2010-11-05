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
        /// Initializes an instance with the supplied values and default values for refresh, retry, expire and minimum.
        /// </summary>
        /// <param name="name">The domain name for which this is a record</param>
        /// <param name="domainName">The domain name of the name server that was the primary source for this zone</param>
        /// <param name="responsibleName">Email mailbox of the hostmaster</param>
        /// <param name="serialNumber">Version number of the original copy of the zone.</param>
        public SOARecord(string name, string domainName, string responsibleName, int serialNumber)
            : this(name, domainName, responsibleName, serialNumber, 0, 0, 0, 0)
        {
        }

        /// <summary>
        /// Initializes an instance with the supplied values.
        /// </summary>
        /// <param name="name">The domain name for which this is a record</param>
        /// <param name="domainName">The domain name of the name server that was the primary source for this zone</param>
        /// <param name="responsibleName">Email mailbox of the hostmaster</param>
        /// <param name="serialNumber">Version number of the original copy of the zone.</param>
        /// <param name="refresh">Number of seconds before the zone should be refreshed.</param>
        /// <param name="retry">Number of seconds before failed refresh should be retried.</param>
        /// <param name="expire">Number of seconds before records should be expired if not refreshed</param>
        /// <param name="minimum">Minimum TTL for this zone.</param>
        public SOARecord(string name, string domainName, string responsibleName, int serialNumber, int refresh, int retry, int expire, int minimum)
            : base(name, DnsStandard.RecordType.SOA)
        {
            this.DomainName = domainName;
            this.ResponsibleName = responsibleName;
            this.SerialNumber = serialNumber;
            this.Refresh = refresh;
            this.Retry = retry;
            this.Expire = expire;
            this.Minimum = minimum;
        }
        
        /// <summary>
        /// The domain name of the name server that was the primary source for this zone
        /// </summary>
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
        /// Tests equality between this SOA record and the other <paramref name="record"/>.
        /// </summary>
        /// <param name="record">The other record.</param>
        /// <returns><c>true</c> if the RRs are equal, <c>false</c> otherwise.</returns>
        public override bool Equals(DnsResourceRecord record)
        {
            if (!base.Equals(record))
            {
                return false;
            }

            SOARecord soaRecord = record as SOARecord;
            if (soaRecord == null)
            {
                return false;
            }
            
            return (
                       DnsStandard.Equals(m_mname, soaRecord.m_mname)
                       &&  DnsStandard.Equals(m_rname, soaRecord.m_rname)
                       &&  this.SerialNumber == soaRecord.SerialNumber
                       &&  this.Refresh == soaRecord.Refresh
                       &&  this.Retry == soaRecord.Retry
                       &&  this.Expire == soaRecord.Expire
                       &&  this.Minimum == soaRecord.Minimum 
                   );
        }

        /// <summary>
        /// Writes this RR in DNS wire format to the <paramref name="buffer"/>
        /// </summary>
        /// <param name="buffer">The buffer to which DNS wire data are written</param>
        protected override void SerializeRecordData(DnsBuffer buffer)
        {
            buffer.AddDomainName(m_mname);
            buffer.AddDomainName(m_rname);
            buffer.AddInt(this.SerialNumber);
            buffer.AddInt(this.Refresh);
            buffer.AddInt(this.Retry);
            buffer.AddInt(this.Expire);
            buffer.AddInt(this.Minimum);
        }

        /// <summary>
        /// Reads data into this RR from the DNS wire format data in <paramref name="reader"/>
        /// </summary>
        /// <param name="reader">Reader in which wire format data for this RR is already buffered.</param>
        protected override void DeserializeRecordData(ref DnsBufferReader reader)
        {
            this.DomainName = reader.ReadDomainName();
            this.ResponsibleName = reader.ReadDomainName();
            this.SerialNumber = reader.ReadInt();
            this.Refresh = reader.ReadInt();
            this.Retry = reader.ReadInt();
            this.Expire = reader.ReadInt();
            this.Minimum = reader.ReadInt();
        }
    }
}