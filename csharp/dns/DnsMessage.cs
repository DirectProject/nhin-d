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
using System.Threading;

namespace DnsResolver
{
    /// <summary>
    /// A base DNS message, consisting of a header and a question
    /// Actual DNS messages are DNSRequest and DnsResponse
    /// </summary>
    public class DnsMessage
    {
        DnsHeader m_header;
        DnsQuestion m_question;
        
        protected DnsMessage()
        {
            m_header = new DnsHeader();
            m_question = new DnsQuestion();       
            this.Clear();
        }
        
        /// <summary>
        /// Instantiates a Dns Message
        /// </summary>
        /// <param name="qType"></param>
        /// <param name="qName"></param>
        protected DnsMessage(Dns.RecordType qType, string qName)
            : this()
        {
            m_header.QuestionCount = 1;
            m_question = new DnsQuestion(qName, qType, Dns.Class.IN);
        }
        
        /// <summary>
        /// Instantiates a new message object by deserializing from the given reader
        /// </summary>
        /// <param name="reader"></param>
        protected DnsMessage(ref DnsBufferReader reader)
        {
            this.Deserialize(ref reader);
        }
                        
        /// <summary>
        /// Gets the Dns Header for this message
        /// </summary>        
        public DnsHeader Header
        {
            get
            {
                return m_header;
            }
        }
        
        /// <summary>
        /// Gets the Dns Question included in this message
        /// </summary>
        public DnsQuestion Question
        {
            get
            {
                return m_question;
            }            
        }
        
        /// <summary>
        /// Gets or sets this message's request ID
        /// </summary>
        public ushort RequestID
        {
            get
            {
                return m_header.UniqueID;
            }
            set
            {
                m_header.UniqueID = value;
            }
        }

        /// <summary>
        /// Returns true if this message indicates a Successful operation
        /// </summary>
        public bool IsSuccess
        {
            get
            {
                return (this.m_header != null && m_header.ResponseCode == Dns.ResponseCode.SUCCESS);
            }
        }
        
        /// <summary>
        /// Returns true if this message indicates that the domain name being queries was not found
        /// </summary>
        public bool IsNameError
        {
            get
            {
                return (this.m_header != null && m_header.ResponseCode == Dns.ResponseCode.NAME_ERROR);
            }
        }
        
        /// <summary>
        /// Reset...
        /// </summary>
        public virtual void Clear()
        {
            m_header.IsRequest = true;
            m_header.OpCode = Dns.OpCode.QUERY;
            m_header.IsAuthoritativeAnswer = false;
            m_header.IsTruncated = false;
            m_header.IsRecursionDesired = true;
            m_header.IsRecursionAvailable = false;
            m_header.ResponseCode = Dns.ResponseCode.SUCCESS;
            m_header.AnswerCount = 0;
            m_header.NameServerAnswerCount = 0;
            m_header.AdditionalAnswerCount = 0;
        }

        /// <summary>
        /// Serialize this Dns Message into the given DnsBuffer
        /// </summary>
        /// <param name="buffer">buffer to serialize into</param>
        public virtual void Serialize(DnsBuffer buffer)
        {
            if (buffer == null)
            {
                throw new ArgumentNullException();
            }
            m_header.Serialize(buffer);
            m_question.Serialize(buffer);
        }
        
        /// <summary>
        /// Deserialize this message
        /// </summary>
        /// <param name="reader"></param>        
        protected virtual void Deserialize(ref DnsBufferReader reader)
        {
            m_header = new DnsHeader(ref reader);
            m_question = new DnsQuestion(ref reader);
        }
        
        public virtual void Validate()
        { 
            if (m_header == null)
            {
                throw new DnsProtocolException(DnsProtocolError.InvalidHeader);
            }
            if (m_question == null)
            {
                throw new DnsProtocolException(DnsProtocolError.InvalidQuestion);
            }
        }
    }
}
