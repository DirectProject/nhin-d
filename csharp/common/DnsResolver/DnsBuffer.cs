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
    /// A raw data buffer with utilities facilitating reading and writing of DNS wire format data.
    /// </summary>
    public class DnsBuffer
    {
        byte[] m_buffer;
        int m_count;
        
        /// <summary>
        /// Initializes a buffer with capacity == DnsStandard.MaxUdpMessageLength
        /// </summary>
        public DnsBuffer()
            : this(DnsStandard.MaxUdpMessageLength)
        {
        }
        
        /// <summary>
        /// Initializes a buffer with the requested capacity.
        /// </summary>
        /// <param name="capacity">The capacity of this buffer in <c>byte</c>s</param>
        public DnsBuffer(int capacity)
        {
            m_buffer = new byte[capacity];
            m_count = 0;
        }
        
        /// <summary>
        /// Returns the byte at the zero-based index position in this buffer.
        /// </summary>
        /// <param name="index">The zero-based index position to access.</param>
        /// <returns>The <c>byte</c> at the requested index.</returns>
        public byte this[int index]
        {
            get
            {
                if (index >= m_count)
                {
                    throw new IndexOutOfRangeException();
                }
                return m_buffer[index];
            }
            set
            {
                if (index >= m_count)
                {
                    throw new IndexOutOfRangeException();
                }
                m_buffer[index] = value;
            }
        }

        /// <summary>
        /// Gets the underying buffer.
        /// </summary>
        public byte[] Buffer
        {
            get
            {
                return m_buffer;
            }
        }

        /// <summary>
        /// Count of <c>byte</c>s added to this buffer.
        /// </summary>
        public int Count
        {
            get
            {
                return m_count;
            }
            set
            {
                if (value < 0 || value > m_buffer.Length)
                {
                    throw new ArgumentOutOfRangeException("value", "value less than 0 or greater than the buffer length");
                }

                m_count = value;
            }
        }

        /// <summary>
        /// The capacity of this buffer in <c>byte</c>s
        /// </summary>
        public int Capacity
        {
            get
            {
                return m_buffer.Length;
            }
        }

        /// <summary>
        /// Adds a <c>byte</c> value to the buffer, first ensuring the buffer has capacity.
        /// </summary>
        /// <param name="item">The raw <c>byte</c> to add.</param>
        public void AddByte(byte item)
        {
            this.EnsureCapacity();
            m_buffer[m_count++] = item;
        }


        /// <summary>
        /// Adds a <c>byte</c> value to the buffer without ensuring the buffer has capacity.
        /// </summary>
        /// <param name="item">The raw <c>byte</c> to add.</param>
        public void AddByteNoCheck(byte item)
        {
            m_buffer[m_count++] = item;
        }

        /// <summary>
        /// Adds raw <c>byte</c> values to the buffer.
        /// </summary>
        /// <param name="items">The raw data to add.</param>
        public void AddBytes(byte[] items)
        {
            if (items == null)
            {
                throw new ArgumentNullException("items");
            }

            this.ReserveCapacity(items.Length);
            System.Buffer.BlockCopy(items, 0, m_buffer, m_count, items.Length);
            m_count += items.Length;
        }
        
        /// <summary>
        /// Add the given string to the buffer as ASCII bytes
        /// </summary>
        public void AddChars(string item)
        {
            if (item == null)
            {
                throw new ArgumentNullException("item");
            }
            this.AddChars(item, 0, item.Length);
        }
        
        /// <summary>
        /// Add a subset of the given string as ASCII bytes, starting at a zero-based index position.
        /// </summary>
        /// <param name="item">The string from which to add characters</param>
        /// <param name="length">The number of characters to add</param>
        /// <param name="startAt">The zero-based postition of <paramref name="item"/> to start reading from</param>
        public void AddChars(string item, int startAt, int length)
        {
            if (item == null)
            {
                throw new ArgumentNullException("item");
            }
            if (startAt < 0)
            {
                throw new ArgumentException("value was less than 0", "startAt");
            }
            if (length < 0)
            {
                throw new ArgumentException("value was less than 0", "length");
            }
            
            if (length == 0)
            {
                return;
            }
            
            this.ReserveCapacity(length);
            int newCount = m_count;
            for (int i = startAt, max = startAt + length; i < max; ++i)
            {
                m_buffer[newCount++] = (byte)item[i];
            }
            m_count = newCount;
        }

        /// <summary>
        /// Add a <c>short</c> in network order
        /// </summary>
        /// <param name="value">The value to add to the buffer.</param>
        public void AddShort(short value)
        {
            this.ReserveCapacity(2);
            this.AddByteNoCheck((byte)((short)value >> 8));
            this.AddByteNoCheck((byte)(value));
        }

        /// <summary>
        /// Add a <c>ushort</c> in network order
        /// </summary>
        /// <param name="value">The value to add to the buffer.</param>
        public void AddUshort(ushort value)
        {
            this.ReserveCapacity(2);
            this.AddByteNoCheck((byte)(value >> 8));
            this.AddByteNoCheck((byte)(value));
        }

        /// <summary>
        /// Add a <c>int</c> in network order
        /// </summary>
        /// <param name="value">The value to add to the buffer.</param>
        public void AddInt(int value)
        {
            this.ReserveCapacity(4);
            this.AddByteNoCheck((byte)(value >> 24));
            this.AddByteNoCheck((byte)(value >> 16));
            this.AddByteNoCheck((byte)(value >> 8));
            this.AddByteNoCheck((byte)(value));
        }

        /// <summary>
        /// Add a <c>uint</c> in network order
        /// </summary>
        /// <param name="value">The value to add to the buffer.</param>
        public void AddUint(uint value)
        {
            this.ReserveCapacity(4);
            this.AddByteNoCheck((byte)(value >> 24));
            this.AddByteNoCheck((byte)(value >> 16));
            this.AddByteNoCheck((byte)(value >> 8));
            this.AddByteNoCheck((byte)(value));
        }

        /// <summary>
        /// Add a <c>long</c> in network order
        /// </summary>
        /// <param name="value">The value to add to the buffer.</param>
        public void AddLong(long value)
        {
            this.AddInt((int) value >> 32);
            this.AddInt((int) value);
        }
        
        /// <summary>
        /// A DNS label is 63 chars long at most and preceded by a length byte
        /// </summary>
        /// <param name="label"></param>
        public void AddLabel(string label)
        {
            if (label == null)
            {
                throw new ArgumentNullException("label");
            }
            this.AddLabel(label, 0, label.Length);
        }

        /// <summary>
        /// Adds a subset of a string as a DNS label.
        /// </summary>
        /// <param name="source">The source string from which to add characters</param>
        /// <param name="startAt">The zero-based starting position from which to add characters.</param>
        /// <param name="length">The number of characters to add.</param>
        public void AddLabel(string source, int startAt, int length)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }

            if (length < 1)
            {
                throw new ArgumentException("value was less than 1", "length");
            }

            if (length > DnsStandard.MaxLabelLength)
            {
                throw new DnsProtocolException(DnsProtocolError.LabelTooLong);
            }

            this.AddByte((byte)length);
            this.AddChars(source, startAt, length);
        }
        
        /// <summary>
        /// Add a domain name (QNAME, path)...
        /// </summary>
        /// <param name="path">The string representation of the domain name to add</param>
        public void AddDomainName(string path)
        {
            if (string.IsNullOrEmpty(path))
            {
                throw new ArgumentException("value was null or empty", "path");
            }
            //
            // Zero length labels are allowed
            //    
            int labelCount = 0;        
            int labelStart, labelEnd;
            for (labelStart = 0, labelEnd = 0; labelEnd < path.Length; ++labelEnd)
            {
                if (path[labelEnd] == '.')
                {
                    if (labelStart < labelEnd)
                    {
                        this.AddLabel(path, labelStart, labelEnd - labelStart);
                        ++labelCount;
                    }
                    labelStart = labelEnd + 1;
                }
            }
            
            if (labelStart < labelEnd)
            {
                this.AddLabel(path, labelStart, labelEnd - labelStart);
                ++labelCount;
            }
            
            if (labelCount == 0)
            {
                throw new DnsProtocolException(DnsProtocolError.InvalidPath);
            }
            
            this.AddByte((byte) 0);
        }
        
        /// <summary>
        /// Clears this buffer. Does not erase data.
        /// </summary>
        public void Clear()
        {
            m_count = 0;
        }

        /// <summary>
        /// Clears and erases data from this buffer.
        /// </summary>
        public void Erase()
        {
            this.Clear();
            if (m_buffer != null)
            {
                Array.Clear(m_buffer, 0, m_buffer.Length);
            }
        }
        
        /// <summary>
        /// Ensures this buffer has at least one byte of free capacity.
        /// </summary>
        public void EnsureCapacity()
        {
            if (m_count == m_buffer.Length)
            {
                this.Grow(m_count + 1);
            }
        }

        /// <summary>
        /// Ensures this buffer has at least <paramref name="newItemCount"/> capacity.
        /// </summary>
        /// <param name="newItemCount">The number of <c>byte</c>s for which to ensure capacity.</param>
        public void ReserveCapacity(int newItemCount)
        {
            int capacity = m_count + newItemCount;
            if (capacity > m_buffer.Length)
            {
                this.Grow(capacity);
            }
        }
        
        /// <summary>
        /// Creates a reader for this buffer.
        /// </summary>
        /// <returns>The newly initialized reader</returns>
        public DnsBufferReader CreateReader()
        {
            return new DnsBufferReader(m_buffer, 0, m_count);
        }
           
        void Grow(int capacity)
        {
            int newSize = m_buffer.Length * 2;
            if (newSize > capacity)
            {
                capacity = newSize;
            }
            byte[] newBuffer = new byte[capacity];
            System.Buffer.BlockCopy(m_buffer, 0, newBuffer, 0, m_buffer.Length);
            m_buffer = newBuffer;
        }
    }
}