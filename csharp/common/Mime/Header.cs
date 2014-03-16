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
using System.Text;
using System.Collections.Generic;

namespace Health.Direct.Common.Mime
{
    /// <summary>
    /// Represents a MIME or RFC 5322 header.
    /// </summary>
    public class Header : MimePart
    {
        string m_name;
        string m_value;
        bool m_parsed;

        /// <summary>
        /// Initializes a header instance with source text supplied by the <paramref name="segment"/>
        /// </summary>
        /// <param name="segment">The <see cref="StringSegment"/> supplying source header text.</param>
        public Header(StringSegment segment)
            : base(MimePartType.Header, segment)
        {
            m_parsed = false;
        }

        /// <summary>
        /// Initializes an instance with a header name and value supplied separately.
        /// </summary>
        /// <param name="name">The <see cref="string"/> supplying header name.</param>
        /// <param name="value">The <see cref="string"/> supplying header value</param>
        public Header(string name, string value)
            : base(MimePartType.Header)
        {
            this.Text = MimeSerializer.Default.JoinHeader(name, value);
            m_name = name;
            m_value = value;
            m_parsed = true;
        }

        /// <summary>
        /// Initializes an instance with a header name, value and an unfolded value supplied separately.
        /// </summary>
        /// <param name="name">The <see cref="string"/> supplying header name.</param>
        /// <param name="value">The <see cref="string"/> supplying header value, with line folding applied</param>
        /// <param name="unfoldedValue">The <see cref="string"/> header value, without line folding</param>
        public Header(string name, string value, string unfoldedValue)
            : base(MimePartType.Header)
        {
            if (string.IsNullOrEmpty(unfoldedValue))
            {
                throw new ArgumentException("unfoldedValue");
            }
            this.Text = MimeSerializer.Default.JoinHeader(name, value);
            m_name = name;
            m_value = unfoldedValue;
            base.SetTextTo(unfoldedValue);
            m_parsed = true;
        }

        /// <summary>
        /// Initializes an instance with pair supplying header name and value.
        /// </summary>
        /// <param name="value">The pair where the key is the header name,
        /// and the value is the value</param>
        public Header(KeyValuePair<string, string> value)
            : this(value.Key, value.Value)
        {        
        }

        /// <summary>
        /// Initializes a header instance with source text supplied by the <paramref name="segment"/>
        /// </summary>
        /// <param name="segment">The <see cref="StringSegment"/> supplying source header text.</param>
        /// <param name="unfoldedText">Source header text with line folding removed</param>
        public Header(StringSegment segment, string unfoldedText)
            : base(MimePartType.Header, segment)
        {
            base.SetTextTo(unfoldedText);
        }
        
        /// <summary>
        /// Gets the raw value for this header, including header name, value, and the ':' separator.
        /// Note: this may differ from the value returned by .SourceText, which will include line folding
        /// characters, if any were present. 
        /// </summary>
        /// <remarks>
        /// The Text should contain a ':' separator
        /// Assumes that the text is otherwise well formed. 
        /// </remarks>
        public override string Text
        {
            get
            {
                return base.Text;
            }
            protected set 
            {
                base.Text = value;
                m_parsed = false;
            }
        }

        /// <summary>
        /// Gets the header name for this header.
        /// </summary>
        public string Name
        {
            get
            {
                this.EnsureNameValue();
                return m_name;
            }
        }

        /// <summary>
        /// Gets the header value for this header.
        /// </summary>
        public string Value
        {
            get
            {
                this.EnsureNameValue();
                if (string.IsNullOrEmpty(m_value))
                {
                    throw new MimeException(MimeError.MissingHeaderValue);
                }
                return m_value;    
            }
        }

        /// <summary>
        /// Gets the raw header value for this header.
        /// </summary>
        public string ValueRaw
        {
            get
            {
                this.EnsureNameValue();
                return m_value;
            }
        }

        /// <summary>
        /// Tests if this header is named the supplied <paramref name="name"/>
        /// </summary>
        /// <param name="name">The name to test this header's name against</param>
        /// <returns><c>true</c> if the names match by MIME string comparison rules</returns>
        public bool IsNamed(string name)
        {
            if (string.IsNullOrEmpty(name))
            {
                return false;
            }
            return MimeStandard.Equals(this.Name, name);
        }

        /// <summary>
        /// Tests if this header is named one of the supplied <paramref name="names"/>
        /// </summary>
        /// <param name="names">The names to test this header's name against</param>
        /// <returns><c>true</c> if this header matches one of the supplied names match by MIME string comparison rules</returns>
        public bool IsHeaderNameOneOf(string[] names)
        {
            if (names == null || names.Length == 0)            
            {
                return false;
            }
            
            string name = this.Name;
            for (int i = 0; i < names.Length; ++i)
            {
                if (MimeStandard.Equals(names[i], name))
                {
                    return true;
                }
            }
            
            return false;
        }

        /// <summary>
        /// Creates a shallow clone of this instance.
        /// </summary>
        /// <returns>The shallow clone.</returns>
        public Header Clone()
        {
            return new Header(this.SourceText);
        }
                        
        /// <summary>
        /// If a line exceeds maxLineLength characters in length, folds the line according to the
        /// </summary>
        /// <param name="source">The <see cref="string"/> to fold</param>
        /// <param name="maxLineLength">Maximum length of each line</param>
        /// <returns>Folded text</returns>
        public static string LineFoldText(string source, int maxLineLength)
        {
            if (string.IsNullOrEmpty(source))
            {
                throw new ArgumentNullException("source");
            }
            if (maxLineLength <= 3)
            {
                throw new ArgumentException("maxLineLength");
            }

            int availableLineLength = maxLineLength - 2; // CRLF == 2 chars. No folding space on first line
            if (source.Length <= availableLineLength)
            {
                return source;
            }
                        
            StringBuilder buffer = new StringBuilder(source.Length + 3);
            int startAt = 0;
            while (startAt < source.Length)
            {
                int remainingLength = source.Length - startAt;
                if (remainingLength > availableLineLength)
                {
                    remainingLength = availableLineLength;
                    buffer.Append(source, startAt, remainingLength);
                    buffer.Append(MimeStandard.CRLF);
                    
                    availableLineLength = maxLineLength - 2;
                    buffer.Append(' ');
                    availableLineLength--;
                }
                else
                {
                    buffer.Append(source, startAt, remainingLength);
                }

                startAt += remainingLength;
            }
            return buffer.ToString();
        }
        
        internal override void AppendSourceText(StringSegment segment)
        {
            if (!this.SourceText.IsNull)
            {
                //
                // Header already has text. We need to unfold the new text in...
                //
                StringSegment unfoldedLine = MimeParser.SkipWhitespace(segment);
                if (unfoldedLine.IsEmpty)
                {
                    throw new MimeException(MimeError.InvalidHeader);
                }
                base.AppendText(unfoldedLine.ToString());
            }

            base.AppendSourceText(segment);
        }

        void EnsureNameValue()
        {
            if (m_parsed)
            {
                return;
            }
            
            KeyValuePair<string, string> split = MimeSerializer.Default.SplitHeader(this.Text);
            m_name = split.Key;
            m_value = split.Value;
            m_parsed = true;
        }
    }
}