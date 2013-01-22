/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Health.Direct.Common.DnsResolver
{
    /// <summary>
    /// See RFC2782 for format: http://tools.ietf.org/html/rfc2782
    /// </summary>
    public class SRVRecord : DnsResourceRecord
    {
        string m_target;
        
        internal SRVRecord()
        {
        }

        /// <summary>
        /// Create a new SRV record object
        /// </summary>
        /// <param name="name">domain name</param>
        /// <param name="weight">relative weight of this server</param>
        /// <param name="port">port the server is listening on</param>
        /// <param name="target">Domain name of target host</param>
        public SRVRecord(string name, ushort weight, ushort port, string target)
            : this(name, weight, port, target, 0)
        {
        }

        /// <summary>
        /// Create a new SRV record object
        /// </summary>
        /// <param name="name">domain name</param>
        /// <param name="weight">relative weight of this server</param>
        /// <param name="port">port the server is listening on</param>
        /// <param name="target">Domain name of target host</param>
        /// <param name="priority">contact priority</param>
        public SRVRecord(string name, ushort weight, ushort port, string target, ushort priority)
            : base(name, DnsStandard.RecordType.SRV)
        {
            this.Priority = priority;
            this.Weight = weight;
            this.Port = port;
            this.Target = target;
        }
        
        /// <summary>
        /// Server priority - smaller is better. 
        /// Start with smallest first. 
        /// </summary>
        public ushort Priority
        {
            get;
            set;
        }
        
        /// <summary>
        /// For loadbalancing. First pick priority, then weight
        /// </summary>
        public ushort Weight
        {
            get;
            set;
        }
        
        /// <summary>
        /// TCP Port # 
        /// </summary>
        public ushort Port
        {
            get;
            set;
        }

        /// <summary>
        /// Domain name of Target server. Resolve actual IP using Address or AAAA  record
        /// </summary>
        public string Target
        {
            get
            {
                return m_target;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidSRVRecord);
                }

                m_target = value;
            }
        }
        
        /// <summary>
        /// Deserialize the record from the give buffer
        /// </summary>
        /// <param name="buffer">buffer containing dns info</param>
        protected override void SerializeRecordData(DnsBuffer buffer)
        {
            buffer.AddUshort(this.Priority);
            buffer.AddUshort(this.Weight);
            buffer.AddUshort(this.Port);
            buffer.AddDomainName(m_target);
        }

        /// <summary>
        /// Reads data into this RR from the DNS wire format data in <paramref name="reader"/>
        /// </summary>
        /// <param name="reader">Reader in which wire format data for this RR is already buffered.</param>
        protected override void DeserializeRecordData(ref DnsBufferReader reader)
        {
            this.Priority = reader.ReadUShort();
            this.Weight = reader.ReadUShort();
            this.Port = reader.ReadUShort();
            this.Target = reader.ReadDomainName();
        }

        /// <summary>
        /// Returns a default string representation of this SRV record
        /// </summary>
        /// <returns>The SRV record text as seen in DNS</returns>
        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            sb.Append(Name)
                .Append(":")
                .Append(Port)
                .Append(" Priority:")
                .Append(Priority)
                .Append(" Weight:")
                .Append(Weight);
            return sb.ToString();
        }
    }
}
