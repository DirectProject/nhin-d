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
	// 
    //public struct CharReader - john.theisen - recommend against using structs for mutable types, when passed
	// around to functions, the state can change and upon return it may be expected that the values have changed,
	// thus creates issues for some one that was expecting this to be pass by reference and not by value.
	//
	public class CharReader
    {
		public const char EOF = char.MinValue;
		private readonly string m_source;
		private readonly int m_maxPosition;

		private int m_position;
        
        public CharReader(StringSegment source)
			: this(source.Source, source.StartIndex, source.StartIndex + source.Length)
        {
        }

        public CharReader(string source)
			: this(source, 0, source.Length)
        {
        }

    	private CharReader(string source, int position, int maxPosition)
    	{
			if (string.IsNullOrEmpty(source))
			{
				throw new ArgumentException("source was null or empty", "source");
			}

			m_source = source;
    		m_position = position;
    		m_maxPosition = maxPosition;
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
                return (m_position >= m_maxPosition);
            }
        }
        
        public char Read()
        {
        	return IsDone ? EOF : m_source[m_position++];
        }

		public bool ReadTo(char chTo)
		{
			return ReadTo(chTo, false);
		}

		public bool ReadTo(char chTo, bool ignoreEscape)
        {
            char ch;
			bool escaped = false;
            while ((ch = Read()) != EOF)
            {
                if (!escaped && ch == chTo)
                {
                    return true;
                }

            	escaped = (ch == MimeStandard.Escape) && !ignoreEscape;
            }
            
            return false;
        }
        
		public StringSegment GetSegment(int startIndex, int endIndex)
		{
			return new StringSegment(m_source, startIndex, endIndex);
		}
    }
}
