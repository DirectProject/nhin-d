using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace DnsResponder
{
    /// <summary>
    /// Simple class to manage a byte buffer
    /// </summary>
    public class Buffer
    {
        static byte[] Empty = new byte[0];
        
        byte[] m_buffer;
        int m_count;
        
        public Buffer()
        {
            m_buffer = Empty;
            m_count = 0;
        }

        public Buffer(int capacity)
        {
            m_buffer = new byte[capacity];
            m_count = 0;
        }

        /// <summary>
        /// Gets the underying buffer.
        /// </summary>
        public byte[] Bytes
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
