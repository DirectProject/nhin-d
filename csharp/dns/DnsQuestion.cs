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

namespace DnsResolver
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
        string m_qname;
        
        internal DnsQuestion()
        {
        }

        internal DnsQuestion(ref DnsBufferReader reader)
        {
            this.Parse(ref reader);
        }
        
        /// <summary>
        /// Initializes an instance of an Internet DNS question with specified QNAME (<paramref name="qName"/>),
        /// and question type (<paramref name="qType"/>).
        /// </summary>
        /// <param name="qName">The QNAME</param>
        /// <param name="qType">The question type</param>
        public DnsQuestion(string qName, Dns.RecordType qType)
            : this(qName, qType, Dns.Class.IN)
        {
        }

        /// <summary>
        /// Initializes an instance of a question with specified domain (<paramref name="qName"/>),
        /// question type (<paramref name="qType"/>), and class (<paramref name="qClass"/>
        /// </summary>
        /// <param name="qName">The QNAME</param>
        /// <param name="qType">The question type</param>
        /// <param name="qClass">The question class</param>
        public DnsQuestion(string qName, Dns.RecordType qType, Dns.Class qClass)
        {
            this.QName = qName;
            this.QType = qType;
            this.QClass = qClass;
        }
        
        /// <summary>
        /// Gets and sets the domain name.
        /// </summary>
        /// <remarks>
        /// This is actually a domain name, rather than a QNAME, despite the method name.
        /// 
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
        public string QName
        {
            get
            {
                return this.m_qname;
            }
            set
            {
                if (value == null)
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidQName);
                }
                
                this.m_qname = value;
            }
        }

        /// <summary>
        /// Gets and sets the QTYPE
        /// </summary>
        /// <remarks>
        /// See <see cref="Dns.RecordType"/> for details.
        /// </remarks>
        public Dns.RecordType QType
        {
            get;
            set;
        }

        /// <summary>
        /// Gets and sets the QClass
        /// </summary>
        /// <remarks>
        /// See <see cref="Dns.Class"/> for details.
        /// </remarks>
        public Dns.Class QClass
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
                    string.Equals(question.QName, this.QName, StringComparison.OrdinalIgnoreCase)
                &&  question.QType == this.QType
                &&  question.QClass == this.QClass
            );
        }
        
        internal void Parse(ref DnsBufferReader reader)
        {
            this.QName = reader.ReadString();
            this.QType = (Dns.RecordType) reader.ReadShort();
            this.QClass = (Dns.Class) reader.ReadShort();
        }

        internal void ToBytes(DnsBuffer buffer)
        {
            buffer.AddPath(this.QName);
            buffer.AddShort((short)QType);
            buffer.AddShort((short)QClass);
        }
    }
}
