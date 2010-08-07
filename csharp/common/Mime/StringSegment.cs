/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

namespace NHINDirect.Mime
{
    public struct StringSegment : IEquatable<StringSegment>
    {
        public static readonly StringSegment Null = new StringSegment(null);
        
        string m_source;
        int m_startIndex;
        int m_endIndex;

    	public StringSegment(string source)
        {
            m_source = source;
            m_startIndex = 0;
            if (source == null)
            {
                m_endIndex = -1;
            }
            else
            {
                m_endIndex = source.Length - 1;
            }
        }
        
        public StringSegment(string source, int startIndex, int endIndex)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            if (startIndex < 0 || startIndex > source.Length)
            {
                throw new ArgumentException("startIndex less than 0 or greater than length of source", "startIndex");
            }
            if (endIndex < -1 || endIndex >= source.Length)
            {
                throw new ArgumentException("endIndex less than -1 or greater than equal to length of source", "endIndex");
            }

            m_source = source;
            m_startIndex = startIndex;
            m_endIndex = endIndex;
        }
        
        public char this[int index]
        {
            get
            {
                return m_source[m_startIndex + index];
            }
        }
                
        public string Source
        {
            get
            {
                return m_source;
            }
        }
        
        public int Length
        {
            get
            {
                return string.IsNullOrEmpty(m_source)? 0 : m_endIndex - m_startIndex + 1;
            }
        }
        
        public bool IsEmpty
        {
            get
            {
                return (this.Length == 0);
            }
        }
        
        public bool IsNull
        {
            get
            {
                return (m_source == null);
            }
        }
        
        public int StartIndex
        {
            get
            {
                return m_startIndex;
            }
        }

        public int EndIndex
        {
            get
            {
                return m_endIndex;
            }
        }
        
        public override string ToString()
        {
            int length = this.Length;
            if (length == 0)
            {
                return string.Empty;
            }
            //
            // If the segment covers the entire string, we don't need an allocation
            //
            if (length == m_source.Length)
            {
                return m_source;
            }

            return m_source.Substring(m_startIndex, length);
        }
        
        public void Union(StringSegment segment)
        {
            if (segment.m_source == null)
            {
                throw new ArgumentNullException("segment", "Cannot union with null segment");
            }
            
            if (m_source == null)
            {
                m_source = segment.m_source;
                m_startIndex = segment.m_startIndex;
                m_endIndex = segment.m_endIndex;
                return;
            }
            
            if (!object.ReferenceEquals(m_source, segment.m_source))
            {
                throw new InvalidOperationException("Segments from different strings");
            }

            if (segment.m_startIndex < m_startIndex)
            {
                m_startIndex = segment.m_startIndex;
            }

            if (segment.m_endIndex > m_endIndex)
            {
                m_endIndex = segment.m_endIndex;
            }
        }
        
        public string Substring(int startAt)
        {
            int length = m_endIndex - startAt + 1;
            if (length < 0)
            {
                throw new IndexOutOfRangeException();
            }
            return m_source.Substring(startAt, length);
        }

        public string Substring(int startAt, int length)
        {
            int endIndex = startAt + length;
            if (endIndex < m_startIndex || endIndex-1 > m_endIndex)
            {
                throw new IndexOutOfRangeException();
            }
            return m_source.Substring(startAt, length);
        }
        
        public bool Equals(string other)
        {
            if (this.Length != other.Length)
            {
                return false;
            }
            return (string.Compare(m_source, m_startIndex, other, 0, other.Length, StringComparison.OrdinalIgnoreCase) == 0);
        }
        
        public bool Equals(StringSegment other)
        {
            if (this.Length != other.Length)
            {
                return false;
            }
            return (string.Compare(m_source, m_startIndex, other.Source, other.StartIndex, this.Length, StringComparison.OrdinalIgnoreCase) == 0);
        }
        
        public bool StartsWith(string other)
        {
            int length = this.Length;
            if (other.Length > length)
            {
                return false;
            }
            
            return (string.Compare(m_source, m_startIndex, other, 0, other.Length, StringComparison.OrdinalIgnoreCase) == 0);
        }
        
        public int IndexOf(string other)
        {
            int length = this.Length;
            if (other.Length > length)
            {
                return -1;
            }
            
            return m_source.IndexOf(other, m_startIndex, length, StringComparison.OrdinalIgnoreCase);
        }
    }
}
