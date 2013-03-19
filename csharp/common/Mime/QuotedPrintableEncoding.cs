/* 
 Copyright (c) 2013, Direct Project
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
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Health.Direct.Common.Mime
{
    /// <summary>
    /// Decodes text encoded with the Quoted Printable Encoding
    /// </summary>
    public class QuotedPrintableDecoder
    {        
        /// <summary>
        /// '=' is the chracter used to prefix a 2 character encoding
        /// </summary>
        public const char EncodingChar = '=';
        
        StringSegment m_segment;
        CharReader m_reader;
        
        /// <summary>
        /// Construct a decoder to parse the given encoded text
        /// </summary>
        /// <param name="encodedText"><see cref="String"/> containing encoded text</param>
        public QuotedPrintableDecoder(string encodedText)
            : this(new StringSegment(encodedText))
        {
        }
        
        /// <summary>
        /// Construct a decoder to parse the given string segment
        /// </summary>
        /// <param name="encodedSegment"><see cref="StringSegment"/> containing encoded text</param>
        public QuotedPrintableDecoder(StringSegment encodedSegment)
        {
            m_segment = encodedSegment;
        }
        
        /// <summary>
        /// Get or set the <see cref="StringSegment"/> to decode
        /// </summary>
        public StringSegment Segment
        {
            get { return m_segment;}
            set { m_segment = value;}
        }
        
        /// <summary>
        /// Return a stream of decoded characters
        /// </summary>
        /// <returns>An enumerator that yields decoded characters</returns>
        public IEnumerable<char> GetChars()
        {
            m_reader = new CharReader(m_segment);

            char ch;
            while ((ch = m_reader.Read()) != CharReader.EOF)
            {
                if (ch != EncodingChar)
                {
                    yield return ch;
                }        
                else
                {
                    char decodedChar = this.DecodeChar();
                    if (decodedChar != char.MinValue)
                    {
                        yield return decodedChar;
                    }
                }
            }
        }
        
        /// <summary>
        /// Decodes the source and returns a string containing decoded characters
        /// </summary>
        /// <returns>Decoded string</returns>
        public string GetString()
        {
            StringBuilder builder = new StringBuilder();
            this.GetString(builder);
            return builder.ToString();
        }
        
        /// <summary>
        /// Appends decoded characters to the given buffer. 
        /// </summary>
        /// <param name="builder">buffer to append to</param>
        public void GetString(StringBuilder builder)
        {
            if (builder == null)
            {
                throw new ArgumentNullException("builder");
            }    
            // The percentage of encoded characters is typically relatively small. 
            // The decoded stream can be no larger than the original.
            // Ensuring capacity up front will reduce buffer copying
            builder.EnsureCapacity(builder.Length + m_segment.Length);
            foreach(char ch in this.GetChars())
            {
                builder.Append(ch);
            }
        }
        
        char DecodeChar()
        {
            char ch = this.ReadOrThrow();
            if (ch == MimeStandard.CR)
            {
                return this.DecodeSoftLineBreak(ch);
            }            
            
            return this.DecodeEncodedChar(ch);
        }

        char DecodeSoftLineBreak(char chPrev)
        {
            char ch = this.ReadOrThrow();
            if (ch != MimeStandard.LF)
            {
                throw new MimeException(MimeError.InvalidCRLF);
            }

            return char.MinValue; // Indicates a soft line break. To be ignored
        }
        
        char DecodeEncodedChar(char chPrev)
        {
            int number = this.DecodeNibble(chPrev) * 16;
            
            char ch = this.ReadOrThrow();            
            number += this.DecodeNibble(ch);
            if (number <= char.MinValue || number > byte.MaxValue)
            {
                throw new MimeException(MimeError.InvalidQuotedPrintableEncodedChar);
            }
            
            return (char) number;
        }
                
        int DecodeNibble(char ch)
        {
            if (ch >= '0' && ch <= '9')
            {
                return (ch - '0');
            }            
            if (ch >= 'a' && ch <= 'f')
            {
                return ((ch - 'a') + 10);
            }            
            if (ch >= 'A' && ch <= 'F')
            {
                return ((ch - 'A') + 10);
            }
            
            throw new MimeException(MimeError.InvalidQuotedPrintableEncodedChar);
        }
        
        char ReadOrThrow()
        {
            char ch = m_reader.Read();
            if (ch == CharReader.EOF)
            {
                throw new MimeException(MimeError.InvalidQuotedPrintableEncodedChar);
            }
            
            return ch;
        }
    }
}
