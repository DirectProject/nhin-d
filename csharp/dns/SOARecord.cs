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
    /*
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /                     MNAME                     /
    /                                               /
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /                     RNAME                     /
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                    SERIAL                     |
    |                                               |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                    REFRESH                    |
    |                                               |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                     RETRY                     |
    |                                               |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                    EXPIRE                     |
    |                                               |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                    MINIMUM                    |
    |                                               |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    */
    public class SOARecord : DnsResourceRecord
    {
        string m_mname;
        string m_rname;
        
        internal SOARecord()
        {
        }
        
        public SOARecord(string name, string domainName, string responsibleName, int serialNumber)
            : this(name, domainName, responsibleName, serialNumber, 0, 0, 0, 0)
        {
        }
        
        public SOARecord(string name, string domainName, string responsibleName, int serialNumber, int refresh, int retry, int expire, int minimum)
            : base(name, Dns.RecordType.SOA)
        {
            this.DomainName = domainName;
            this.ResponsibleName = responsibleName;
            this.SerialNumber = serialNumber;
        }
        
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
        
        public int SerialNumber
        {
            get;
            set;
        }
        public int Refresh
        {
            get;
            set;
        }
        public int Retry
        {
            get;
            set;
        }
        public int Expire
        {
            get;
            set;
        }
        public int Minimum
        {
            get;
            set;
        }

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
                    Dns.Equals(m_mname, soaRecord.m_mname)
                &&  Dns.Equals(m_rname, soaRecord.m_rname)
                &&  this.SerialNumber == soaRecord.SerialNumber
                &&  this.Refresh == soaRecord.Refresh
                &&  this.Retry == soaRecord.Retry
                &&  this.Expire == soaRecord.Expire
                &&  this.Minimum == soaRecord.Minimum 
            );
        }
        
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
