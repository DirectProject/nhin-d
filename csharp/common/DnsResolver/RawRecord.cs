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
    /// RDATA for a record we did not know how to parse
    /// </summary>
    public class RawRecord : DnsResourceRecord
    {
        byte[] m_bytes;
        
        internal RawRecord()
        {
        }
        
        /// <summary>
        /// Gets and sets the raw data for this RR
        /// Not all DNS Record are mapped to a custom object by this library. 
        /// Dns Records not specifically parsed are turned into RawRecord objects
        /// </summary>
        /// <param name="name"></param>
        /// <param name="type"></param>
        /// <param name="record"></param>
        public RawRecord(string name, DnsStandard.RecordType type, byte[] record)
            : base(name, type)
        {
            this.RecordBytes = record;
        }
        
        /// <summary>
        /// The raw data for this resource record
        /// </summary>
        public byte[] RecordBytes
        {
            get
            {
                return m_bytes;
            }
            set
            {
                if (value == null || value.Length == 0)
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidRecord);
                }
                
                m_bytes = value;
            }
        }
        
        /// <summary>
        /// Compares a Raw Record to this given resource record
        /// </summary>
        /// <param name="record"></param>
        /// <returns></returns>
        public override bool Equals(DnsResourceRecord record)
        {
            if (!base.Equals(record))
            {
                return false;
            }

            RawRecord rawRecord = record as RawRecord;
            if (rawRecord == null)
            {
                return false;
            }
            
            if (rawRecord.m_bytes.Length != m_bytes.Length)
            {
                return false;
            }
            
            for (int i = 0; i < m_bytes.Length; ++i)
            {
                if (m_bytes[i] != rawRecord.m_bytes[i])               
                {
                    return false;
                }
            }
            
            return true;
        }
        
        /// <summary>
        /// Serializes the raw record
        /// </summary>
        /// <param name="buffer"></param>
        protected override void SerializeRecordData(DnsBuffer buffer)
        {
            buffer.AddBytes(m_bytes);
        }
        
        /// <summary>
        /// Deserializes this raw record 
        /// </summary>
        /// <param name="reader"></param>
        protected override void DeserializeRecordData(ref DnsBufferReader reader)
        {
            this.RecordBytes = reader.ReadBytes(this.RecordDataLength);
        }
    }
}