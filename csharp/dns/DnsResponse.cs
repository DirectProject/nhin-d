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
    /// <summary>
    /// Server response
    /// </summary>
    public class DnsResponse : DnsMessage
    {
        DnsResourceRecordCollection m_answerRecords;
        DnsResourceRecordCollection m_nameServerRecords;
        DnsResourceRecordCollection m_additionalRecords;

        public DnsResponse(ref DnsBufferReader reader)
            : base(ref reader)
        {
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

        public override void Serialize(DnsBuffer buffer)
        {
            this.UpdateAnswerCounts();
            
            base.Serialize(buffer);
            
            if (this.HasAnswerRecords)            
            {
                this.AnswerRecords.Serialize(buffer);
            }
            if (this.HasNameServerRecords)
            {
                this.NameServerRecords.Serialize(buffer);
            }
            if (this.HasAdditionalRecords)
            {
                this.AdditionalRecords.Serialize(buffer);
            }
        } 
        
        void UpdateAnswerCounts()
        {
            if (this.HasAnswerRecords)
            {
                if (this.AnswerRecords.Count > short.MaxValue)
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidAnswerCount);
                }
                this.Header.AnswerCount = (short)this.AnswerRecords.Count;
            }
            if (this.HasNameServerRecords)
            {
                if (this.NameServerRecords.Count > short.MaxValue)
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidNameServerAnswerCount);
                }
                this.Header.NameServerAnswerCount = (short)this.NameServerRecords.Count;
            }
            if (this.HasAdditionalRecords)
            {
                if (this.AdditionalRecords.Count > short.MaxValue)
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidAdditionalAnswerCount);
                }
                this.Header.AdditionalAnswerCount = (short)this.AdditionalRecords.Count;
            }
        }
        
        protected override void Deserialize(ref DnsBufferReader reader)
        {
            base.Deserialize(ref reader);
            if (reader.IsDone)
            {
                //
                // No answers!
                //
                return;
            }
            
            if (this.Header.AnswerCount > 0)
            {
                this.AnswerRecords.Deserialize(this.Header.AnswerCount, ref reader);
            }
            if (this.Header.NameServerAnswerCount > 0)
            {
                this.NameServerRecords.Deserialize(this.Header.NameServerAnswerCount, ref reader);
            }
            if (this.Header.AdditionalAnswerCount > 0)
            {
                this.AdditionalRecords.Deserialize(this.Header.AdditionalAnswerCount, ref reader);
            }
        }
    }
}
