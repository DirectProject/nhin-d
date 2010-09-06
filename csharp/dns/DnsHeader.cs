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
    //                                     1  1  1  1  1  1
    //       0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
    //     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    //     |                      ID                       |
    //     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    //     |QR|   Opcode  |AA|TC|RD|RA|   Z    |   RCODE   |
    //     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    //     |                    QDCOUNT                    |
    //     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    //     |                    ANCOUNT                    |
    //     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    //     |                    NSCOUNT                    |
    //     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    //     |                    ARCOUNT                    |
    //     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+

    public class DnsHeader
    {
        short m_questionCount;
        short m_answerCount;
        short m_nameServerAnswerCount;
        short m_additionalAnswerCount;
        
        internal DnsHeader()
        {
        }
        
        internal DnsHeader(ref DnsBufferReader reader)
        {
            this.Deserialize(ref reader);
        }

        public ushort UniqueID { get; set; } // ID
        public bool IsRequest { get; set; }  // QR
        public Dns.OpCode OpCode { get; set; }  // OpCode (4 bits)
        public bool IsAuthoritativeAnswer { get; set; }  // AA
        public bool IsTruncated { get; set; }  // TC
        public bool IsRecursionDesired { get; set; }  // RD
        public bool IsRecursionAvailable { get; set; }  // RA
        public Dns.ResponseCode ResponseCode { get; set; }  // RCODE (4 bits)

        // QDCOUNT
        public short QuestionCount
        {
            get
            {
                return m_questionCount;
            }
            set
            {
                if (value != 1)
                {
                    // We currenly only support a single question
                    // We will generalize this in a subsequent versionf
                    throw new DnsProtocolException(DnsProtocolError.InvalidQuestionCount);
                }
                
                m_questionCount = value;
            }
        }

        // ANCOUNT
        public short AnswerCount
        {
            get
            {
                return m_answerCount;
            }
            set
            {
                if (value < 0)
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidAnswerCount);
                }
                
                m_answerCount = value;
            }
        }

        // NSCOUNT
        public short NameServerAnswerCount
        {
            get
            {
                return m_nameServerAnswerCount;
            }
            set
            {
                if (value < 0)
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidNameServerAnswerCount);
                }

                m_nameServerAnswerCount = value;
            }
        }

        // ARCOUNT
        public short AdditionalAnswerCount
        {
            get
            {
                return m_additionalAnswerCount;
            }
            set
            {
                if (value < 0)
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidAdditionalAnswerCount);
                }

                m_additionalAnswerCount = value;
            }
        }

        internal void Deserialize(ref DnsBufferReader buffer)
        {
            this.UniqueID = buffer.ReadUShort();

            byte b = buffer.ReadByte();

            this.IsRequest = ((b & 0x80) == 0);
            this.OpCode = (Dns.OpCode)(byte)((b >> 3) & 0x0F);
            this.IsAuthoritativeAnswer = ((b & 0x04) != 0);
            this.IsTruncated = ((b & 0x02) != 0);
            this.IsRecursionDesired = ((b & 0x01) != 0);

            b = buffer.ReadByte();
            this.IsRecursionAvailable = ((b & 0x80) != 0);
            this.ResponseCode = (Dns.ResponseCode) (byte)(b & 0x0F);

            this.QuestionCount = buffer.ReadShort();
            this.AnswerCount = buffer.ReadShort();
            this.NameServerAnswerCount = buffer.ReadShort();
            this.AdditionalAnswerCount = buffer.ReadShort();
        }

        internal void Serialize(DnsBuffer buffer)
        {
            buffer.AddUshort(UniqueID);

            buffer.AddByte((byte)((this.IsRequest ? 0x00 : 0x80) |
                                 ((byte)OpCode << 3) |
                                 (this.IsAuthoritativeAnswer ? 0x04 : 0x00) |
                                 (this.IsTruncated ? 0x02 : 0x00) |
                                 (this.IsRecursionDesired ? 0x01 : 0x00)));

            buffer.AddByte((byte)((this.IsRecursionAvailable ? 0x80 : 0x00) | (byte) this.ResponseCode));
            
            buffer.AddShort(this.QuestionCount);
            buffer.AddShort(this.AnswerCount);
            buffer.AddShort(this.NameServerAnswerCount);
            buffer.AddShort(this.AdditionalAnswerCount);
        }
    }
}
