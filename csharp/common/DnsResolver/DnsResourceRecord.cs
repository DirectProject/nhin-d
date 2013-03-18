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

namespace Health.Direct.Common.DnsResolver
{
    /// <summary>
    /// Represents an RR
    /// </summary>
    /// <remarks>
    /// See RFC 1035, 3.2.1
    /// RR top level format:
    /// <code>
    ///                                  1  1  1  1  1  1
    ///    0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
    ///  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    ///  |                                               |
    ///  /                                               /
    ///  /                      NAME                     /
    ///  |                                               |
    ///  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    ///  |                      TYPE                     |
    ///  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    ///  |                     CLASS                     |
    ///  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    ///  |                      TTL                      |
    ///  |                                               |
    ///  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    ///  |                   RDLENGTH                    |
    ///  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--|
    ///  /                     RDATA                     /
    ///  /                                               /
    ///  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    ///  </code>
    /// </remarks>
    public abstract class DnsResourceRecord
    {
        static Func<DnsStandard.RecordType, DnsResourceRecord> s_recordObjectFactory = DnsResourceRecord.CreateRecordObject;
        DnsResourceRecordHeader m_header;        
        
        internal DnsResourceRecord()
        {
        }
        
        /// <summary>
        /// Instantiates a new Dns Resource Record
        /// </summary>
        /// <param name="name">the domain name for which this is a record</param>
        /// <param name="type">the record type</param>
        protected DnsResourceRecord(string name, DnsStandard.RecordType type)
        {
            this.Name = name;
            this.Type = type;
            this.Class = DnsStandard.Class.IN;
        }
        
        //
        // NAME
        //
        /// <summary>
        /// Gets and sets the Name
        /// </summary>
        /// <remarks>
        /// Per RFC 1035, a name is:
        /// <para>
        /// an owner name, i.e., the name of the node to which this
        /// resource record pertains.</para>
        /// Generally a domain name, or (in cases such as SRV), a pseudo-domain.
        /// </remarks>
        public string Name
        {
            get
            {
                return m_header.Name;  
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidRecordName);
                }
                m_header.Name = value;
            }
        }

        /// <summary>
        /// Gets or sets the record type
        /// </summary>
        /// <remarks>See remarks for <see cref="DnsStandard.RecordType"/></remarks>
        public DnsStandard.RecordType Type
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

        /// <summary>
        /// Gets and sets the class
        /// </summary>
        /// <remarks>See remarks for <see cref="Class"/></remarks>
        public DnsStandard.Class Class
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

        /// <summary>
        /// Gets and sets the TTL
        /// </summary>
        /// <remarks>
        /// RFC 1035, 3.2.1
        /// <para>
        /// a 32 bit signed integer that specifies the time interval
        /// that the resource record may be cached before the source
        /// of the information should again be consulted.  Zero
        /// values are interpreted to mean that the RR can only be
        /// used for the transaction in progress, and should not be
        /// cached.  For example, SOA records are always distributed
        /// with a zero TTL to prohibit caching.  Zero values can
        /// also be used for extremely volatile data.
        /// </para>
        /// </remarks>
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
                
        /// <summary>
        /// Gets and sets the RDLENGTH
        /// </summary>
        /// <remarks>
        /// RFC 1035, 2.3.1: "an unsigned 16 bit integer that specifies the length in
        /// octets of the RDATA field."</remarks>
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

        /// <summary>
        /// Tests equality between this RR and the other <paramref name="record"/>
        /// </summary>
        /// <remarks>
        /// Compares all fields except TTL , since that can vary
        /// </remarks>
        /// <returns><c>true</c> if equal</returns>
        public virtual bool Equals(DnsResourceRecord record)
        {
            if (record == null)
            {
                return false;
            }
            
            return (
                       this.Type == record.Type
                       &&  this.Class == record.Class
                       &&  DnsStandard.Equals(this.Name, record.Name)
                   );
        }

        /// <summary>
        /// Serialize this DnsResourceRecord into a byte array, as per the DNS RFC
        /// </summary>
        /// <returns>buffer containing serialized record</returns>
        public byte[] Serialize()
        {
            DnsBuffer buff = new DnsBuffer();
            this.Serialize(buff);
            return buff.Buffer;
        }
        
        /// <summary>
        /// Serialize this DnsResourceRecord into the given buffer, as per the DNS RFC
        /// </summary>
        /// <param name="buffer">buffer to write into</param>
        public void Serialize(DnsBuffer buffer)
        {
            if (buffer == null)
            {
                throw new ArgumentNullException("buffer");
            }
            
            m_header.Serialize(buffer);
            int headerSize = buffer.Count;
            int recordLengthOffset = headerSize - sizeof(short);
            this.SerializeRecordData(buffer);
            this.RecordDataLength = (short) (buffer.Count - headerSize);
            
            buffer.Buffer[recordLengthOffset++] = (byte) ((short) this.RecordDataLength >> 8);
            buffer.Buffer[recordLengthOffset] = ((byte)(this.RecordDataLength));            
        }
        
        /// <summary>
        /// Deserialize the buffer into a DnsResourceRecord object
        /// </summary>
        /// <param name="reader">reader over a buffer containing raw Dns record bytes</param>
        /// <returns>DnsResourceRecord</returns>                
        public static DnsResourceRecord Deserialize(ref DnsBufferReader reader)
        {
            //
            // We have to parse the header before we can figure out what kind of record this is
            //
            DnsResourceRecordHeader header = new DnsResourceRecordHeader();
            header.Deserialize(ref reader);

            DnsResourceRecord record = DnsResourceRecord.CreateRecordObject(header.Type);
            record.Deserialize(ref header, ref reader);

            return record;
        }
        
        /// <summary>
        /// Override to serialize record specific information
        /// </summary>
        /// <param name="buffer"></param>        
        protected virtual void SerializeRecordData(DnsBuffer buffer)
        {
            throw new NotSupportedException();
        }
        
        /// <summary>
        /// Override to deserialize record specific information
        /// </summary>
        /// <param name="reader"></param>
        protected abstract void DeserializeRecordData(ref DnsBufferReader reader);
        
        /// <summary>
        /// Factory for DnsResourceRecord objects
        /// </summary>
        /// <param name="recordType"></param>
        /// <returns></returns>
        public static DnsResourceRecord CreateRecordObject(DnsStandard.RecordType recordType)
        {
            DnsResourceRecord record;
            switch (recordType)
            {
                default:
                    record = new RawRecord();
                    break;

                case DnsStandard.RecordType.ANAME:
                    record = new AddressRecord();
                    break;

                case DnsStandard.RecordType.NS:
                    record = new NSRecord();
                    break;

                case DnsStandard.RecordType.CNAME:
                    record = new CNameRecord();
                    break;

                case DnsStandard.RecordType.SOA:
                    record = new SOARecord();
                    break;

                case DnsStandard.RecordType.TXT:
                    record = new TextRecord();
                    break;

                case DnsStandard.RecordType.MX:
                    record = new MXRecord();
                    break;
                
                case DnsStandard.RecordType.PTR:
                    record = new PtrRecord();
                    break;
                    
                case DnsStandard.RecordType.CERT:
                    record = new CertRecord();
                    break;
                
                case DnsStandard.RecordType.SRV:
                    record = new SRVRecord();
                    break;
            }
            
            return record;
        }
        
        /// <summary>
        /// Gets and sets the function used to map from record types to specific resource records.
        /// </summary>
        public static Func<DnsStandard.RecordType, DnsResourceRecord> ResourceRecordFactory
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
            
            internal DnsStandard.RecordType Type;
            
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
            
            internal DnsStandard.Class Class;
            
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
            
            internal void Serialize(DnsBuffer buffer)
            {
                buffer.AddDomainName(this.Name);
                buffer.AddShort((short) this.Type);
                buffer.AddShort((short) this.Class);
                buffer.AddInt(this.TTL);
                buffer.AddShort(this.RecordDataLength);
            }
            
            internal void Deserialize(ref DnsBufferReader reader)
            {
                this.Name = reader.ReadDomainName();
                this.Type = (DnsStandard.RecordType) reader.ReadShort();

                this.Class = (DnsStandard.Class) reader.ReadShort();
                this.TTL = reader.ReadInt();
                this.RecordDataLength = reader.ReadShort();
            }
        }
    }
}