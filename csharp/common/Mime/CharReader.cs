/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

namespace Health.Direct.Common.Mime
{
    /// <summary>
    /// CharReader tokenizes a text buffer, the <see cref="Read"/> method returns a single
    /// char at a time and returns back the <see cref="EOF"/> token when the end of the buffer
    /// is reached. The ReadTo method will consume characters and optionally ignore
    /// any escape sequence in the buffer. The escape character is defined as <see cref="MimeStandard.Escape"/>.
    /// 
    /// CharReader is a struct for perfomance reasons. Don't change without consulting the author(s) :)
    /// </summary>
    public struct CharReader
    {
        /// <summary>
        /// EOF is a sentinal value returned when the end of the buffer is reached.
        /// </summary>
        public const char EOF = char.MinValue;

        private readonly string m_source;
        private readonly int m_maxPosition;

        private int m_position;
        
        /// <summary>
        /// Constructs a new CharReader from an existing <see cref="StringSegment"/>.
        /// </summary>
        /// <param name="source">The segment to obtain the buffer from.</param>
        public CharReader(StringSegment source)
            : this(source.Source, source.StartIndex, source.StartIndex + source.Length)
        {
        }

        /// <summary>
        /// Constructs a new CharReader from the <paramref name="source"/>.
        /// </summary>
        /// <param name="source">The text to construct the buffer from.</param>
        public CharReader(string source)
            : this(source, 0, (source ?? "").Length)
        {
        }

        /// <summary>
        /// Constructs a new CharReader from <paramref name="source"/> between <paramref name="position"/> and <paramref name="maxPosition"/>
        /// </summary>
        /// <param name="source">The text to construct the buffer from.</param>
        /// <param name="position">The initial position of the buffer</param>
        /// <param name="maxPosition">The maximum position to which to scan the buffer.</param>
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

        /// <summary>
        /// The current position in the buffer.
        /// </summary>
        public int Position
        {
            get
            {
                return m_position - 1;
            }
        }
        
        /// <summary>
        /// The maximum position (last character) that the reader will scan until
        /// When Position >= MaxPosition, IsDone is true
        /// </summary>
        public int MaxPosition
        {
            get { return m_maxPosition;}
        }
        
        /// <summary>
        /// Returns true if the current <see cref="Position"/> has passed the end of the buffer.
        /// </summary>
        public bool IsDone
        {
            get
            {
                return (m_position >= m_maxPosition);
            }
        }
        
        /// <summary>
        /// Returns the current character at <see cref="Position"/> in the buffer. As a result of this
        /// call the <see cref="Position"/> is incremented.
        /// </summary>
        /// <returns>Returns the current character or <see cref="EOF"/> when the end of the buffer is reached.</returns>
        public char Read()
        {
            return IsDone ? EOF : m_source[m_position++];
        }

        /// <summary>
        /// Read up the specified character, or to the end, skipping escaped characters.
        /// </summary>
        /// <param name="chTo">The character to read to</param>
        /// <param name="ignoreEscape">Indicates if the reader should skip escape characters</param>
        /// <returns><c>true</c> if the character was found, <c>false</c> if not, and reader is at end</returns>
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

                escaped = ignoreEscape && (ch == MimeStandard.Escape);
            }
            
            return false;
        }
        
        /// <summary>
        /// Read up the specified character, or to the end, skippng escaped characters.
        /// </summary>
        /// <param name="chTo">The character to read to</param>
        /// <param name="ignoreEscape">Indicates if the reader should skip escape characters</param>
        /// <param name="quoteChar">Skip any sub-strings enclosed by this character</param>
        /// <returns><c>true</c> if the character was found, <c>false</c> if not, and reader is at end</returns>
        public bool ReadTo(char chTo, bool ignoreEscape, char quoteChar)
        {
            char ch;
            bool escaped = false;
            while ((ch = Read()) != EOF)
            {
                if (escaped)
                {
                    escaped = false;
                    continue;
                }
                
                if (ch == MimeStandard.Escape && ignoreEscape)
                {
                    escaped = true;
                    continue;
                }
                
                if (ch == quoteChar)
                {
                    if (!ReadTo(quoteChar, ignoreEscape))
                    {
                        break;
                    }
                }                
                else if (ch == chTo)
                {
                    return true;
                }
            }

            return false;
        }

        /// <summary>
        /// Peeks at the next character that will be read from the buffer, but does not advance
        /// the <see cref="Position"/>.
        /// </summary>
        /// <returns>Returns the current character or <see cref="EOF"/> if at the end of the buffer.</returns>
        public char Peek()
        {
            return IsDone ? EOF : m_source[m_position];
        }

        /// <summary>
        /// Returns the character at <see cref="Position"/> in the buffer - the last character read
        /// </summary>
        /// <returns>Returns the current character</returns>
        public char Current()
        {
            int position = this.Position;
            if (position < 0)
            {
                throw new IndexOutOfRangeException();
            }
            return m_source[position];
        }
                        
        /// <summary>
        /// Returns the specified part of the reader's buffer as a <see cref="StringSegment"/>.
        /// </summary>
        /// <param name="startIndex">The first position in the buffer to return.</param>
        /// <param name="endIndex">The last position in the buffer to return.</param>
        /// <returns>A new StringSegment containing the reader's buffer between <paramref name="startIndex"/> and <paramref name="endIndex"/></returns>
        public StringSegment GetSegment(int startIndex, int endIndex)
        {
            return new StringSegment(m_source, startIndex, endIndex);
        }

        /// <summary>
        /// Returns the specified part of the reader's buffer as a <see cref="string"/>.
        /// </summary>
        /// <param name="startIndex">The first position in the buffer to return.</param>
        /// <param name="endIndex">The last position in the buffer to return.</param>
        /// <returns>A new string</returns>
        public string Substring(int startIndex, int endIndex)
        {
            return m_source.Substring(startIndex, endIndex - startIndex + 1);
        }
        
        /// <summary>
        /// Get the remaining segment - starting at Position
        /// </summary>
        /// <returns></returns>
        public StringSegment GetRemainder()
        {
            return new StringSegment(m_source, m_position, m_maxPosition - 1);
        }
    }
}