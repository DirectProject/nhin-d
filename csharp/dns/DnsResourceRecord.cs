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
    //                                  1  1  1  1  1  1
    //    0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
    //  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    //  |                                               |
    //  /                                               /
    //  /                      NAME                     /
    //  |                                               |
    //  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    //  |                      TYPE                     |
    //  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    //  |                     CLASS                     |
    //  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    //  |                      TTL                      |
    //  |                                               |
    //  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    //  |                   RDLENGTH                    |
    //  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--|
    //  /                     RDATA                     /
    //  /                                               /
    //  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    
    public abstract class DnsResourceRecord
    {
        static Func<Dns.RecordType, DnsResourceRecord> s_recordObjectFactory = DnsResourceRecord.CreateRecordObject;
        DnsResourceRecordHeader m_header;        
        
        internal DnsResourceRecord()
        {
        }
        //
        // NAME
        //
        public string Name
        {
            get
            {
                return m_header.Name;  
            }
            set
            {
                if (value == null)
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidRecordName);
                }
                m_header.Name = value;
            }
        }
        // TYPE
        public Dns.RecordType Type
        {
            get
            {
                return m_header.Type;
            }
            set
            {
                this.m_header.Type = value;
            }
        }

        // CLASS
        public Dns.Class Class
        {
            get
            {
                return m_header.Class;
            }
            set
            {
                m_header.Class = value;
            }
        }

        // TTL
        public int TTL
        {
            get
            {
                return m_header.TTL;
            }
            set
            {
                m_header.TTL = value;
            }
        }
        
        
        // RDLENGTH
        public short RecordDataLength
        {
            get
            {
                return m_header.RecordDataLength;
            }
            set
            {
                m_header.RecordDataLength = value;
            }
        }

        internal void Deserialize(ref DnsResourceRecordHeader header, ref DnsBufferReader reader)
        {
            m_header = header;
            this.DeserializeRecordData(ref reader);
        }
        
        protected abstract void DeserializeRecordData(ref DnsBufferReader reader);
        
        internal static DnsResourceRecord Deserialize(ref DnsBufferReader reader)
        {
            //
            // We have to parse the header before we can figure out what kind of record this is
            //
            DnsResourceRecordHeader header = new DnsResourceRecordHeader();
            header.Parse(ref reader);

            DnsResourceRecord record = DnsResourceRecord.CreateRecordObject(header.Type);
            record.Deserialize(ref header, ref reader);

            return record;
        }

        public static DnsResourceRecord CreateRecordObject(Dns.RecordType recordType)
        {
            DnsResourceRecord record;
            switch (recordType)
            {
                default:
                    record = new RawRecord();
                    break;

                case Dns.RecordType.ANAME:
                    record = new AddressRecord();
                    break;

                case Dns.RecordType.NS:
                    record = new NSRecord();
                    break;

                case Dns.RecordType.CNAME:
                    record = new CNameRecord();
                    break;

                case Dns.RecordType.SOA:
                    record = new SOARecord();
                    break;

                case Dns.RecordType.TXT:
                    record = new TextRecord();
                    break;

                case Dns.RecordType.MX:
                    record = new MXRecord();
                    break;
                
                case Dns.RecordType.PTR:
                    record = new PtrRecord();
                    break;
                    
                case Dns.RecordType.CERT:
                    record = new CertRecord();
                    break;
            }
            
            return record;
        }
        
        public static Func<Dns.RecordType, DnsResourceRecord> ResourceRecordFactory
        {
            get
            {
                return s_recordObjectFactory;
            }
            set
            {
                s_recordObjectFactory = value ?? DnsResourceRecord.CreateRecordObject;
            }
        }
        
        internal struct DnsResourceRecordHeader
        {
            string m_name;
            int m_ttl;
            short m_recordDataLength;
            
            internal string Name
            {
                get
                {
                    return m_name;
                }
                set
                {
                    if (value == null)
                    {
                        throw new DnsProtocolException(DnsProtocolError.InvalidRecordName);
                    }
                    
                    m_name = value;
                }
            }
            
            internal Dns.RecordType Type;
            internal int TTL
            {
                get
                {
                    return m_ttl;
                }
                set
                {
                    if (value < 0)
                    {
                        throw new DnsProtocolException(DnsProtocolError.InvalidTTL);
                    }
                    
                    m_ttl = value;
                }
            }
            internal Dns.Class Class;
            internal short RecordDataLength
            {
                get
                {
                    return m_recordDataLength;
                }
                set
                {
                    if (value <= 0)
                    {
                        throw new DnsProtocolException(DnsProtocolError.InvalidRecordSize);
                    }

                    m_recordDataLength = value;
                }
            }

            internal void Parse(ref DnsBufferReader reader)
            {
                this.Name = reader.ReadString();
                this.Type = (Dns.RecordType) reader.ReadShort();

                this.Class = (Dns.Class) reader.ReadShort();
                this.TTL = reader.ReadInt();
                this.RecordDataLength = reader.ReadShort();
            }
        }
    }    
}
