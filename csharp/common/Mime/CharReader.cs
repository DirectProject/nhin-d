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
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace NHINDirect.Mime
{
    public struct CharReader
    {
        string m_source;
        int m_position;
        int m_maxPosition;
        
        public CharReader(StringSegment source)
        {
            m_source = source.Source;
            m_position = source.StartIndex;
            m_maxPosition = m_position + source.Length;
        }

        public CharReader(string source)
        {
            if (string.IsNullOrEmpty(source))
            {
                throw new ArgumentException();
            }

            m_source = source;
            m_position = 0;
            m_maxPosition = m_position + source.Length;
        }
        
        public string Source
        {
            get
            {
                return m_source;
            }
        }

        public int Position
        {
            get
            {
                return m_position - 1;
            }
        }
        
        public bool IsDone
        {
            get
            {
                return (m_position == m_maxPosition);
            }
        }
        
        public char Read()
        {
            if (m_position == m_maxPosition)
            {
                return char.MinValue;
            }

            return m_source[m_position++];
        }
        
        public bool ReadTo(char chTo)
        {
            char ch;
            while ((ch = this.Read()) != char.MinValue)
            {
                if (ch == chTo)
                {
                    return true;
                }
            }
            
            return false;
        }
        
        public char Peek()
        {
            int next = m_position + 1;
            if (next >= m_maxPosition)
            {
                return char.MinValue;
            }
            
            return m_source[next];
        }
        
        public bool IsNext(char ch)
        {
            return (this.Peek() == ch);
        }
        
        public char PeekPrev()
        {
            int prev = m_position - 1;
            if (prev < 0)
            {
                return char.MinValue;
            }
            
            return m_source[prev];
        }
        
        public bool IsPrev(char ch)
        {
            return (this.PeekPrev() == ch);
        }
    }
}
