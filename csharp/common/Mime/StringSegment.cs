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
using System.Collections.Generic;

namespace Health.Direct.Common.Mime
{
    /// <summary>
    /// Class providing efficient management of subsegments of a single long string (e.g., parts of a long textual document or message).
    /// </summary>
    public struct StringSegment : IEquatable<StringSegment>
    {
        /// <summary>
        /// The single null string segment, can be used as a special unique object, or to represent an empty or default segment. 
        /// </summary>
        public static readonly StringSegment Null = new StringSegment(null);
        
        string m_source;
        int m_startIndex;
        int m_endIndex;

        /// <summary>
        /// Creates an instance from a string, encompassing the entire string.
        /// </summary>
        /// <param name="source">The underlying string for this segement.</param>
        public StringSegment(string source)
        {
            m_source = source;
            m_startIndex = 0;
            if (source == null)
            {
                // Guarantees that the calculation: m_endIndex - m_startIndex + 1 will always generate an invalid index value
                m_endIndex = -1;
            }
            else
            {
                m_endIndex = source.Length - 1;
            }
        }
        
        /// <summary>
        /// Constructs a subsegement instance on of an underlying string
        /// </summary>
        /// <param name="source">The underlying string of which this is a segment</param>
        /// <param name="startIndex">The starting position of this segment</param>
        /// <param name="endIndex">The end position of this segemnet.</param>
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
        
        /// <summary>
        /// Indexes the segment by character index as an offset from the start.
        /// </summary>
        /// <param name="index">The character position offset from the start</param>
        /// <returns>The <c>char</c> at the index position.</returns>
        public char this[int index]
        {
            get
            {
                return m_source[m_startIndex + index];
            }
        }
        
        /// <summary>
        /// Gets the underlying string this is a segment of.
        /// </summary>
        /// <value>A <see cref="System.String"/> instance that this is a segment of</value>
        public string Source
        {
            get
            {
                return m_source;
            }
        }
        
        /// <summary>
        /// The length of this segment in characters.
        /// </summary>
        public int Length
        {
            get
            {
                return string.IsNullOrEmpty(m_source)? 0 : m_endIndex - m_startIndex + 1;
            }
        }
        
        /// <summary>
        /// Gets if this segment spans no characters.
        /// </summary>
        /// <value><c>true</c> if this segment spans no characters, <c>false</c> if it does.</value>
        public bool IsEmpty
        {
            get
            {
                return (this.Length == 0);
            }
        }
        
        /// <summary>
        /// Gets if this segment is based on a <c>null</c> string
        /// </summary>
        /// <remarks>Note that the segment may be empty but will not be <c>null</c> unless the underlying <c>string</c> representation is <c>null</c></remarks>
        public bool IsNull
        {
            get
            {
                return (m_source == null);
            }
        }
        
        /// <summary>
        /// The offset from the underlying <c>string</c> 0 index where the first character of this segment is
        /// </summary>
        public int StartIndex
        {
            get
            {
                return m_startIndex;
            }
        }

        /// <summary>
        /// The offset from the underlying <c>string</c> 0 index where the last character of this segment is.
        /// </summary>
        public int EndIndex
        {
            get
            {
                return m_endIndex;
            }
        }
        
        /// <summary>
        /// Returns a <c>string</c> corresponding to the string represented by the segment.
        /// </summary>
        /// <returns>A <see cref="string"/> corresponding to the segment this represents. The return value
        /// is a new string instance unless the segment encompasses the entire base string, in which case it
        /// is a reference to the base string</returns>
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
        
        /// <summary>
        /// Increases the span of this segement to the widest span on the underlying string that includes
        /// both segments.
        /// </summary>
        /// <param name="segment">The segment to widen to</param>
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
        
        /// <summary>
        /// Returns a substring of the segment, from the provided start position (indexed from the start of the segment) to the segement end.
        /// </summary>
        /// <param name="startAt">Start position of the segment</param>
        /// <returns>A <see cref="string"/> repesenting the substring</returns>
        public string Substring(int startAt)
        {
            int length = m_endIndex - startAt + 1;
            if (length < 0)
            {
                throw new IndexOutOfRangeException();
            }
            return m_source.Substring(startAt, length);
        }

        /// <summary>
        /// Returns a substring of <c>length</c> characters of the segment, from the provided start position (indexed from the start of the segment).
        /// </summary>
        /// <param name="startAt">Start position of the segment</param>
        /// <param name="length">The number of characters to substring</param>
        /// <returns>A <see cref="string"/> repesenting the substring</returns>
        public string Substring(int startAt, int length)
        {
            int endIndex = startAt + length;
            if (endIndex < m_startIndex || endIndex-1 > m_endIndex)
            {
                throw new IndexOutOfRangeException();
            }
            return m_source.Substring(startAt, length);
        }
        
        /// <summary>
        /// Determines whether the segment represented by this instance and a 
        /// specified <see cref="String"/> object have the same value.
        /// </summary>
        /// <param name="other">A <see cref="String"/> instance to compare this segement to</param>
        /// <returns><c>true</c> if this segment is equal to the string, <c>false</c> if not</returns>
        public bool Equals(string other)
        {
            if (this.Length != other.Length)
            {
                return false;
            }
            return (string.Compare(m_source, m_startIndex, other, 0, other.Length, StringComparison.OrdinalIgnoreCase) == 0);
        }

        /// <summary>
        /// Determines whether the segment represented by this instance and an 
        /// other  <see cref="StringSegment"/> object have the same value.
        /// </summary>
        /// <param name="other">A <see cref="StringSegment"/> instance to compare this segement to</param>
        /// <returns><c>true</c> if this segment is equal to the other segment, <c>false</c> if not</returns>
        public bool Equals(StringSegment other)
        {
            if (this.Length != other.Length)
            {
                return false;
            }
            return (string.Compare(m_source, m_startIndex, other.Source, other.StartIndex, this.Length, StringComparison.OrdinalIgnoreCase) == 0);
        }
        
        /// <summary>
        /// Determins whether the segment represented by this instance starts with the same
        /// characters as an other <see cref="String"/>
        /// </summary>
        /// <param name="other">The <see cref="String"/> to compare to this segment</param>
        /// <returns><c>true</c> if this segment starts with the same characters as the other
        /// <see cref="String"/>, false otherwise</returns>
        public bool StartsWith(string other)
        {
            int length = this.Length;
            if (other.Length > length)
            {
                return false;
            }
            
            return (string.Compare(m_source, m_startIndex, other, 0, other.Length, StringComparison.OrdinalIgnoreCase) == 0);
        }
        
        /// <summary>
        /// Returns the first position of this segment matching the specified <see cref="String"/>
        /// </summary>
        /// <param name="other"></param>
        /// <returns>The zero based index of the string if found, -1 if not</returns>
        public int IndexOf(string other)
        {
            int length = this.Length;
            if (other.Length > length)
            {
                return -1;
            }
            
            return m_source.IndexOf(other, m_startIndex, length, StringComparison.OrdinalIgnoreCase);
        }

        /// <summary>
        /// Splits the supplied string by <paramref name="separator"/>, returning an enumeration of <see cref="StringSegment"/> instances for each header subpart.
        /// </summary>
        /// <param name="source">String to split.</param>
        /// <param name="separator">The value separator to split on.</param>
        /// <returns>An enumeration of <see cref="StringSegment"/> instances, one for each parsed part.</returns>
        public static IEnumerable<StringSegment> Split(string source, char separator)
        {
            return Split(new StringSegment(source), separator);
        }

        /// <summary>
        /// Splits the supplied string by <paramref name="separator"/>, returning an enumeration of <see cref="StringSegment"/> instances for each header subpart.
        /// </summary>
        /// <param name="source">String to split.</param>
        /// <param name="separator">The value separator to split on.</param>
        /// <param name="quoteChar">Skip over sections enclosed by this character</param>
        /// <returns>An enumeration of <see cref="StringSegment"/> instances, one for each parsed part.</returns>
        public static IEnumerable<StringSegment> Split(string source, char separator, char quoteChar)
        {
            return Split(new StringSegment(source), separator, quoteChar);
        }

        /// <summary>
        /// Splits the supplied <see cref="StringSegment"/> by <paramref name="separator"/>, returning an enumeration of <see cref="StringSegment"/> instances for each header subpart.
        /// </summary>
        /// <param name="source">Segment to split.</param>
        /// <param name="separator">The value separator to split on.</param>
        /// <example>
        /// <code>
        /// StringSegment text = new StringSegment("a, b, c;d, e, f:g, e");
        /// IEnumerable&lt;StringSegment&gt; parts = Split(text, ',');
        /// foreach(StringSegment part in parts)
        /// {
        ///     Console.WriteLine(part);
        /// }
        /// // Prints:
        /// // a
        /// // b
        /// // c;d
        /// // e
        /// // f:g
        /// // e
        /// </code>
        /// </example>
        /// <returns>An enumeration of <see cref="StringSegment"/> instances, one for each parsed part.</returns>
        internal static IEnumerable<StringSegment> Split(StringSegment source, char separator)
        {
            if (source.IsNull || source.IsEmpty)
            {
                yield break;
            }

            int startAt = source.StartIndex;
            CharReader reader = new CharReader(source);
            while (reader.ReadTo(separator, true))
            {
                yield return new StringSegment(source.Source, startAt, reader.Position - 1); // STRUCTS - fast
                startAt = reader.Position + 1;
            }

            StringSegment last = new StringSegment(source.Source, startAt, reader.Position);
            if (!last.IsEmpty)
            {
                yield return last;
            }
        }
        
        /// <summary>
        /// Same as Split above, except automatically consumes quoted sections
        /// </summary>
        internal static IEnumerable<StringSegment> Split(StringSegment source, char separator, char quoteChar)
        {
            if (source.IsNull || source.IsEmpty)
            {
                yield break;
            }

            int startAt = source.StartIndex;
            CharReader reader = new CharReader(source);
            while (reader.ReadTo(separator, true, quoteChar))
            {
                yield return new StringSegment(source.Source, startAt, reader.Position - 1); // STRUCTS - fast
                startAt = reader.Position + 1;
            }

            StringSegment last = new StringSegment(source.Source, startAt, reader.Position);
            if (!last.IsEmpty)
            {
                yield return last;
            }
        }
    }
}