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
using System.Text;

namespace Health.Direct.Common.DnsResolver
{
    /// <summary>
    /// Reader specialized to perform reads of DNS responses from a buffer.
    /// </summary>
    /// <remarks>
    /// This reader reads in DNS network byte ordering. See RFC 1035, 2.3.2. The character set for DNS is ASCII, transported in octets.
    /// </remarks>
    public struct DnsBufferReader
    {
        byte[] m_buffer;
        int m_count;
        int m_index;
        StringBuilder m_stringBuilder;

        /// <summary>
        /// Initializes an instance with a buffer, starting postion, and count.
        /// </summary>
        /// <param name="buffer">The buffer supplying data</param>
        /// <param name="startAt">The starting postition in the buffer from which to read</param>
        /// <param name="count">The number of bytes read into in the buffer.</param>
        public DnsBufferReader(byte[] buffer, int startAt, int count)
        {
            if (buffer == null)
            {
                throw new ArgumentNullException("buffer");
            }
            m_buffer = buffer;
            m_count = count;
            m_index = startAt;
            m_stringBuilder = null;
        }

        /// <summary>
        /// Initializes an instance with a buffer, starting postion, count and a <see cref="StringBuilder"/>
        /// </summary>
        /// <param name="buffer">The buffer supplying data</param>
        /// <param name="startAt">The starting postition in the buffer from which to read</param>
        /// <param name="count">The number of bytes read into in the buffer.</param>
        /// <param name="sb">A <see cref="StringBuilder"/> for reading strings, will be destructively altered by this reader</param>
        public DnsBufferReader(byte[] buffer, int startAt, int count, StringBuilder sb)
        {
            if (buffer == null)
            {
                throw new ArgumentNullException("buffer");
            }
            
            m_buffer = buffer;
            m_count = count;
            m_index = startAt;
            m_stringBuilder = sb;
        }

        /// <summary>
        /// Gets the current zero-based index postition in the buffer from which bytes are read.
        /// </summary>
        public int Index
        {
            get
            {
                return m_index;
            }
        }

        /// <summary>
        /// Returns the current character read in the buffer.
        /// </summary>
        public byte Current
        {
            get
            {
                return this[m_index];
            }
        }

        /// <summary>
        /// Returns the character in the buffer at the indexed position.
        /// </summary>
        /// <param name="index">Zero-based offset from the start of the buffer.</param>
        /// <exception cref="IndexOutOfRangeException">On attempts to read outside of the currently read area of the buffer.</exception>
        /// <returns>The <see cref="byte"/> in the buffer at the indexed position.</returns>
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
        
        /// <summary>
        /// Gets if this reader has reached the end of data read into the buffer.
        /// </summary>
        public bool IsDone
        {
            get
            {
                return (m_index >= m_count);
            }
        }
        
        /// <summary>
        /// Gets the <see cref="StringBuilder"/> used to create strings from the buffer.
        /// </summary>
        public StringBuilder StringBuilder
        {
            get
            {
                return m_stringBuilder;
            }
        }

        /// <summary>
        /// Returns a shallow clone of this instance.
        /// </summary>
        /// <returns>The shallow clone</returns>
        public DnsBufferReader Clone()
        {
            return new DnsBufferReader(m_buffer, m_index, m_count, m_stringBuilder);
        }

        /// <summary>
        /// Returns a shallow clone of this instance with a new starting position.
        /// </summary>
        /// <param name="startAt">The zero based index from which to start reading in the buffer.</param>
        /// <returns>The shallow clone</returns>
        public DnsBufferReader Clone(int startAt)
        {
            return new DnsBufferReader(m_buffer, startAt, m_count, m_stringBuilder);
        }

        /// <summary>
        /// Resets the reader to the beginning of the buffer.
        /// </summary>
        public void Reset()
        {
            this.m_index = 0;
        }

        /// <summary>
        /// Advances the index by one position in the buffer.
        /// </summary>
        public void Advance()
        {
            m_index++;
        }

        /// <summary>
        /// Reads a <see cref="byte"/> from the index and advances the current buffer position.
        /// </summary>
        /// <returns>The <see cref="byte"/> at the current position.</returns>
        public byte ReadByte()
        {
            return this[m_index++];
        }
        
        /// <summary>
        /// Reads a <see cref="char"/> from the buffer and advances the buffer index.
        /// </summary>
        /// <remarks>
        /// DNS uses ASCII characters, so this method reads bytes as characters.
        /// </remarks>
        /// <returns>The <see cref="char"/> at the current buffer index.</returns>
        public char ReadChar()
        {
            // DNS uses 8-bit characters
            return (char)this.ReadByte();
        }


        /// <summary>
        /// Reads a <see cref="ushort"/> from the buffer and advances the buffer index.
        /// </summary>
        /// <returns>The <see cref="ushort"/> starting from the current buffer index.</returns>
        public ushort ReadUShort()
        {
            return (ushort)((this[m_index++] << 8) | this[m_index++]);
        }

        /// <summary>
        /// Reads a <see cref="short"/> from the buffer and advances the buffer index.
        /// </summary>
        /// <returns>The <see cref="short"/> starting from the current buffer index.</returns>
        public short ReadShort()
        {
            return (short)((this[m_index++] << 8) | this[m_index++]);
        }

        /// <summary>
        /// Reads an <see cref="int"/> from the buffer and advances the buffer index.
        /// </summary>
        /// <returns>The <see cref="int"/> starting from the current buffer index.</returns>
        public int ReadInt()
        {
            return (int)((this[m_index++] << 24) | (this[m_index++] << 16) | (this[m_index++] << 8) | (this[m_index++]));
        }

        /// <summary>
        /// Reads a <see cref="uint"/> from the buffer and advances the buffer index.
        /// </summary>
        /// <returns>The <see cref="uint"/> starting from the current buffer index.</returns>
        public uint ReadUint()
        {
            return (uint)((this[m_index++] << 24) | (this[m_index++] << 16) | (this[m_index++] << 8) | (this[m_index++]));
        }


        /// <summary>
        /// Reads a <see cref="long"/> from the buffer and advances the buffer index.
        /// </summary>
        /// <returns>The <see cref="long"/> starting from the current buffer index.</returns>
        public long ReadLong()
        {
            return (long)(this.ReadInt() << 32 | this.ReadInt());
        }

        /// <summary>
        /// Reads an array of <paramref name="count"/> <see cref="byte"/> from the buffer and advances the buffer
        /// </summary>
        /// <param name="count">The number of <see cref="byte"/>s to read</param>
        /// <returns>The array</returns>
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

        /// <summary>
        /// Reads the rest of the buffer into a <see cref="byte"/> array.
        /// </summary>
        /// <returns>The array of <see cref="byte"/> from the current position to the end of the buffer.</returns>
        public byte[] ReadBytes()
        {
            return this.ReadBytes(m_count - m_index);
        }
        
        /// <summary>
        /// Reads a raw string. The string does not have a leading length byte nor are pointers resolved
        /// Reads till the end of
        /// </summary>
        /// <param name="endAt">Index of the byte up to which we pull in the string</param>
        /// <returns></returns>
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

        /// <summary>
        /// Reads a string from the current buffer, accounting for pointers, and advances the buffer.
        /// </summary>
        /// <returns>The string from the current buffer</returns>
        public string ReadDomainName()
        {
            StringBuilder sb = this.EnsureStringBuilder();
            this.ReadLabel();
            return sb.ToString();
        }
        /// <summary>
        /// Tests if the current index position is a pointer label.
        /// </summary>
        /// <remarks>RFC 1035, 4.1.4, Message compression:
        /// <para>
        /// In order to reduce the size of messages, the domain system utilizes a
        /// compression scheme which eliminates the repetition of domain names in a
        /// message.  In this scheme, an entire domain name or a list of labels at
        /// the end of a domain name is replaced with a pointer to a prior occurance
        /// of the same name.
        ///
        /// The pointer takes the form of a two octet sequence:
        /// <code>
        /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        /// | 1  1|                OFFSET                   |
        /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        /// </code>
        /// </para>
        /// </remarks>
        /// <returns><c>true</c> if the current position is a pointer, <c>false</c> otherwise</returns>
        bool IsPointer()
        {
            // 0xC0 = 0b11000000, bitmask for pointer octet
            return ((this.Current & 0xC0) != 0);
        }
               
        bool IsPlainLabel()
        {
            return ((this.Current & 0xC0) == 0);
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

                if (this.IsPointer())
                {
                    // rest of string is found elsewhere. go get it.
                    // 0x3FFF = 0b0011111111111111, bitmask for pointer value.
                    int stringStartAt = (this.ReadShort() & 0x3FFF);
                    this.Clone(stringStartAt).ReadLabel();
                    break;
                }
                
                if (!this.IsPlainLabel())
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidLabelType);
                }
                
                StringBuilder sb = this.StringBuilder;
                // found a segment
                if (sb.Length != 0)
                {
                    sb.Append(".");
                }
                //
                // The current byte contains the string length
                //
                int maxIndex = this.ReadByte() + this.Index;
                while (this.Index < maxIndex)
                {
                    sb.Append(this.ReadChar());
                }
            }
        }

        /// <summary>
        /// Ensures that there is a current <see cref="StringBuilder"/> for the reader.
        /// </summary>
        /// <returns>The current <see cref="StringBuilder"/></returns>
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