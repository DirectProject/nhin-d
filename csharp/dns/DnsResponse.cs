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
    public class DnsResponse
    {
        DnsHeader m_header;
        DnsQuestion m_question;
        DnsResourceRecordCollection m_answerRecords;
        DnsResourceRecordCollection m_nameServerRecords;
        DnsResourceRecordCollection m_additionalRecords;

        public DnsResponse(ref DnsBufferReader reader)
        {
            this.Parse(ref reader);
        }
        
        public bool IsSuccess
        {
            get
            {
                return (this.m_header != null && m_header.ResponseCode == Dns.ResponseCode.SUCCESS);
            }
        }
        
        public bool IsNameError
        {
            get
            {
                return (this.m_header != null && m_header.ResponseCode == Dns.ResponseCode.NAME_ERROR);
            }
        }
        
        public DnsHeader Header
        {
            get
            {
                return m_header;
            }
        }

        public ushort RequestID
        {
            get
            {
                return m_header.UniqueID;
            }
        }

        public DnsQuestion Question
        {
            get
            {
                return m_question;
            }
        }
        
        public DnsResourceRecordCollection AnswerRecords
        {
            get
            {
                if (m_answerRecords == null)
                {
                    m_answerRecords = new DnsResourceRecordCollection();
                }
                return m_answerRecords;
            }
        }
        
        public bool HasAnswerRecords
        {
            get
            {
                return (m_answerRecords != null && m_answerRecords.Count > 0);
            }
        }        
        
        public DnsResourceRecordCollection NameServerRecords
        {
            get
            {
                if (m_nameServerRecords == null)
                {
                    m_nameServerRecords = new DnsResourceRecordCollection();
                }
                return m_nameServerRecords;
            }
            
        }

        public bool HasNameServerRecords
        {
            get
            {
                return (m_nameServerRecords != null && m_nameServerRecords.Count > 0);
            }
        }

        public DnsResourceRecordCollection AdditionalRecords
        {
            get
            {
                if (m_additionalRecords == null)
                {
                    m_additionalRecords = new DnsResourceRecordCollection();
                }
                return m_additionalRecords;
            }
        }
        
        public bool HasAdditionalRecords
        {
            get
            {
                return (m_additionalRecords != null && m_additionalRecords.Count > 0);
            }
        }
                                
        internal void Parse(ref DnsBufferReader reader)
        {
            m_header = new DnsHeader(ref reader);
            m_question = new DnsQuestion(ref reader);
            if (reader.IsDone)
            {
                //
                // No answers!
                //
                return;
            }
            
            this.AnswerRecords.Deserialize(this.Header.AnswerCount, ref reader);
            this.NameServerRecords.Deserialize(this.Header.NameServerAnswerCount, ref reader);
            this.AdditionalRecords.Deserialize(this.Header.AdditionalAnswerCount, ref reader);
        }
    }
}
