/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Sean Nolan      seannol@microsoft.com
    Ali Emami       aliemami@microsoft.com
 
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
    /// Represents the question section of a DNS message
    /// </summary>
    /// <remarks>
    /// See RFC 1035, Section 4.1.2, Question Section Format.
    /// <para>
    /// The question section is used to carry the "question" in most queries,
    /// i.e., the parameters that define what is being asked.  The section
    /// contains QDCOUNT (usually 1) entries, each of the following format:
    /// <code>
    ///                                 1  1  1  1  1  1
    ///   0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// |                                               |
    /// /                     QNAME                     /
    /// /                                               /
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// |                     QTYPE                     |
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// |                     QCLASS                    |
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// </code>
    /// </para>
    /// </remarks>
    public class DnsQuestion
    {
        string m_domain;
        
        internal DnsQuestion()
        {
        }

        internal DnsQuestion(ref DnsBufferReader reader)
        {
            this.Deserialize(ref reader);
        }
        
        /// <summary>
        /// Initializes an instance for the specified domain and type
        /// </summary>
        /// <param name="domain">The domain we are querying.</param>
        /// <param name="type">The record type we are querying.</param>
        public DnsQuestion(string domain, DnsStandard.RecordType type)
            : this(domain, type, DnsStandard.Class.IN)
        {
        }

        /// <summary>
        /// Initializes an instance for the specified domain and type
        /// </summary>
        /// <param name="domain">The domain we are querying.</param>
        /// <param name="type">The record type we are querying.</param>
        /// <param name="qClass">Use to define a non Internet DNS query</param>
        public DnsQuestion(string domain, DnsStandard.RecordType type, DnsStandard.Class qClass)
        {
            this.Domain = domain;
            this.Type = type;
            this.Class = qClass;
        }

        /// <summary>
        /// Initializes an instance from the specified question parameter. 
        /// </summary>
        /// <param name="question">
        /// The question used to initialize the new question instance.
        /// </param>
        public DnsQuestion(DnsQuestion question)
        {
            if (question == null)
            {
                throw new ArgumentNullException("question"); 
            }

            this.Domain = question.Domain;
            this.Type = question.Type;
            this.Class = question.Class; 
        }

        /// <summary>
        /// Gets and sets the domain name.
        /// This is actually a domain name, rather than a QNAME, despite the method name.
        /// </summary>
        /// <remarks>
        /// It gets transformed to a QNAME in the underlying code.
        /// 
        /// RFC 1035, Section 4.1.2
        /// <para>
        /// A domain name represented as a sequence of labels, where
        /// each label consists of a length octet followed by that
        /// number of octets.  The domain name terminates with the
        /// zero length octet for the null label of the root.  Note
        /// that this field may be an odd number of octets; no
        /// padding is used.
        /// </para>
        /// </remarks>
        public string Domain
        {
            get
            {
                return this.m_domain;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidQName);
                }
                    
                this.m_domain = value;
            }
        }

        /// <summary>
        /// Gets and sets the QTYPE
        /// </summary>
        /// <remarks>
        /// See <see cref="DnsStandard.RecordType"/> for details.
        /// </remarks>
        public DnsStandard.RecordType Type
        {
            get;
            set;
        }

        /// <summary>
        /// Gets and sets the QClass
        /// </summary>
        /// <remarks>
        /// See <see cref="DnsStandard.Class"/> for details.
        /// </remarks>
        public DnsStandard.Class Class
        {
            get;
            set;
        }
        
        /// <summary>
        /// Tests this instance for equality with the other <paramref name="question"/>
        /// </summary>
        /// <param name="question">The other question.</param>
        /// <returns><c>true</c> if the instances represent the same question, <c>false</c> otherwise.</returns>
        public bool Equals(DnsQuestion question)
        {
            if (question == null)
            {
                return false;
            }
            
            return (
                       DnsStandard.Equals(question.Domain, this.Domain)
                       &&  question.Type == this.Type
                       &&  question.Class == this.Class
                   );
        }
        
        internal void Deserialize(ref DnsBufferReader reader)
        {
            this.Domain = reader.ReadDomainName();
            this.Type = (DnsStandard.RecordType) reader.ReadShort();
            this.Class = (DnsStandard.Class) reader.ReadShort();
        }

        internal void Serialize(DnsBuffer buffer)
        {
            buffer.AddDomainName(this.Domain);
            buffer.AddShort((short)Type);
            buffer.AddShort((short)Class);
        }

        internal string CollectLogInfo()
        {
            return string.Format("{0};{1}", this.Type, this.Domain);
        }
    }
}