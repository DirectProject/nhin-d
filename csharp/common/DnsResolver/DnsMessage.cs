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
    /// A base DNS message, consisting of a header and a question
    /// Actual DNS messages are DNSRequest and DnsResponse
    /// </summary>
    public class DnsMessage
    {
        DnsHeader m_header;
        DnsQuestion m_question;
        
        /// <summary>
        /// Construct a new Dns message
        /// </summary>
        protected DnsMessage()
            : this(new DnsQuestion())
        {
        }
        
        /// <summary>
        /// Instantiates a Dns Message
        /// </summary>
        /// <param name="qType"></param>
        /// <param name="qName"></param>
        protected DnsMessage(DnsStandard.RecordType qType, string qName)
            : this(new DnsQuestion(qName, qType, DnsStandard.Class.IN))
        {
        }
        
        /// <summary>
        /// Instantiates a Dns message
        /// </summary>
        /// <param name="question"></param>
        protected DnsMessage(DnsQuestion question)
            : this(new DnsHeader(), question)
        {
            m_header.Init();  // Since we created the header, we'll init it with some defaults
        }
        
        /// <summary>
        /// Instantiates a message with the given header and question
        /// </summary>
        /// <param name="header">the header - take as is, assumed configured correctly</param>
        /// <param name="question">question to ask</param>        
        protected DnsMessage(DnsHeader header, DnsQuestion question)
        {
            if (header == null || question == null)
            {
                throw new ArgumentNullException();
            }
            m_header = header;
            m_header.QuestionCount = 1;
            m_question = question;
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
            protected set 
            {
                if (value == null)
                {
                    throw new ArgumentNullException();
                }
                m_question = value;
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
                return (this.m_header != null && m_header.ResponseCode == DnsStandard.ResponseCode.Success);
            }
        }
        
        /// <summary>
        /// Returns true if this message indicates that the domain name being queries was not found
        /// </summary>
        public bool IsNameError
        {
            get
            {
                return (this.m_header != null && m_header.ResponseCode == DnsStandard.ResponseCode.NameError);
            }
        }
        
        /// <summary>
        /// Returns true if this message indicates a server failure
        /// </summary>
        public bool IsServerFailure
        {
            get
            {
                return (this.m_header != null && m_header.ResponseCode == DnsStandard.ResponseCode.ServerFailure);
            }
        }
        
        /// <summary>
        /// Reset...
        /// </summary>
        public virtual void Clear()
        {
            m_header.Init();
        }

        /// <summary>
        /// Serialize this Dns Message into the given DnsBuffer
        /// </summary>
        /// <param name="buffer">buffer to serialize into</param>
        public virtual void Serialize(DnsBuffer buffer)
        {
            if (buffer == null)
            {
                throw new ArgumentNullException("buffer");
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
        
        /// <summary>
        /// Validates the message. Throws DnsProtcolException if validation fails
        /// </summary>
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