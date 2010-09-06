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
using System.Text;

namespace DnsResolver
{
    public struct DnsBufferReader
    {
        byte[] m_buffer;
        int m_count;
        int m_index;
        StringBuilder m_stringBuilder;

        public DnsBufferReader(byte[] buffer, int startAt, int count)
        {
            if (buffer == null)
            {
                throw new ArgumentNullException();
            }
            m_buffer = buffer;
            m_count = count;
            m_index = startAt;
            m_stringBuilder = null;
        }

        public DnsBufferReader(byte[] buffer, int startAt, int count, StringBuilder sb)
        {
            if (buffer == null)
            {
                throw new ArgumentNullException();
            }
            
            m_buffer = buffer;
            m_count = count;
            m_index = startAt;
            m_stringBuilder = sb;
        }

        public int Index
        {
            get
            {
                return m_index;
            }
        }

        public byte Current
        {
            get
            {
                return this[m_index];
            }
        }

        public byte this[int index]
        {
            get
            {
                if (index >= m_count)
                {
                    throw new IndexOutOfRangeException();
                }

                return this.m_buffer[index];
            }
        }
        
        public bool IsDone
        {
            get
            {
                return (m_index >= m_count);
            }
        }
        
        public StringBuilder StringBuilder
        {
            get
            {
                return m_stringBuilder;
            }
        }

        public DnsBufferReader Clone()
        {
            return new DnsBufferReader(m_buffer, m_index, m_count, m_stringBuilder);
        }

        public DnsBufferReader Clone(int startAt)
        {
            return new DnsBufferReader(m_buffer, startAt, m_count, m_stringBuilder);
        }

        public void Reset()
        {
            this.m_index = 0;
        }

        public void Advance()
        {
            m_index++;
        }

        public byte ReadByte()
        {
            return this[m_index++];
        }

        public char ReadChar()
        {
            return (char)this.ReadByte();
        }

        public ushort ReadUShort()
        {
            return (ushort)((this[m_index++] << 8) | this[m_index++]);
        }

        public short ReadShort()
        {
            return (short)((this[m_index++] << 8) | this[m_index++]);
        }

        public int ReadInt()
        {
            return (int)((this[m_index++] << 24) | (this[m_index++] << 16) | (this[m_index++] << 8) | (this[m_index++]));
        }

        public uint ReadUint()
        {
            return (uint)((this[m_index++] << 24) | (this[m_index++] << 16) | (this[m_index++] << 8) | (this[m_index++]));
        }

        public long ReadLong()
        {
            return (long)(this.ReadInt() << 32 | this.ReadInt());
        }

        public byte[] ReadBytes(int count)
        {
            byte[] buffer = new byte[count];
            int maxIndex = m_index + count;
            int iDest = 0;
            while (m_index < maxIndex)
            {
                buffer[iDest++] = this.ReadByte();
            }

            return buffer;
        }

        public byte[] ReadBytes()
        {
            return this.ReadBytes(m_count - m_index);
        }

        public string ReadString()
        {
            return this.ReadString(m_count);
        }

        public string ReadString(int endAt)
        {
            StringBuilder sb = this.EnsureStringBuilder();
            int maxIndex = Math.Min(m_count, endAt);
            while (m_index < maxIndex)
            {
                sb.Append(this.ReadChar());
            }

            return sb.ToString();
        }

        public string ReadDomainName()
        {
            StringBuilder sb = this.EnsureStringBuilder();
            this.ReadLabel();
            return sb.ToString();
        }

        void ReadLabel()
        {
            while (true)
            {
                if (this.Current == 0)
                {
                    // end of string
                    this.Advance();
                    break;
                }

                if ((this.Current & 0xC0) != 0)
                {
                    // rest of string is found elsewhere. go get it.
                    int stringStartAt = (this.ReadShort() & 0x3FFF);
                    this.Clone(stringStartAt).ReadLabel();
                    break;
                }

                StringBuilder sb = this.StringBuilder;
                // found a segment
                if (sb.Length != 0)
                {
                    sb.Append(".");
                }

                int maxIndex = this.ReadByte() + this.Index;
                while (this.Index < maxIndex)
                {
                    sb.Append(this.ReadChar());
                }
            }
        }

        public StringBuilder EnsureStringBuilder()
        {
            if (m_stringBuilder == null)
            {
                m_stringBuilder = new StringBuilder(64);
            }
            else
            {
                m_stringBuilder.Length = 0;
            }

            return m_stringBuilder;
        }
    }
}
