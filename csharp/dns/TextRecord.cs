/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Sean Nolan      seannol@microsoft.com
 
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

namespace DnsResolver
{
    /// <summary>Represents a TXT RR</summary>
    /// <remarks>
    /// RFC 1035, 3.3.14, TXT RDATA format
    /// <code>
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// /                   TXT-DATA                    /
    /// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    /// </code>
    /// where:
    /// TXT-DATA        One or more &lt;character-string&gt;s.
    /// <para>
    /// TXT RRs are used to hold descriptive text.  The semantics of the text
    /// depends on the domain where it is found.
    /// </para>
    /// </remarks>
    public class TextRecord : DnsResourceRecord
    {
        IList<string> m_strings;
        
        internal TextRecord()
        {
            // nothing
        }

        /// <summary>
        /// Returns the strings for the TXT RR
        /// </summary>
        public IList<string> Strings
        {
            get
            {
                return this.m_strings;
            }
            set
            {
                if (value == null)
                {
                    throw new DnsProtocolException(DnsProtocolError.InvalidTextRecord);
                }

                m_strings = value;
            }
        }
        
        /// <summary>
        /// Gets if this TXT RR has strings associated with it.
        /// </summary>
        public bool HasStrings
        {
            get
            {
                return (this.m_strings != null && this.m_strings.Count > 0);
            }
        }

        /// <summary>
        /// Updates this instance with raw wire data from the reader.
        /// </summary>
        /// <param name="reader">The reader with buffer data for the TXT RR pre-loaded.</param>
        protected override void DeserializeRecordData(ref DnsBufferReader reader)
        {
            List<string> stringList = new List<string>();

            int maxIndex = reader.Index + this.RecordDataLength;
            while (reader.Index < maxIndex)
            {
                StringBuilder sb = reader.EnsureStringBuilder();
                int cb = reader.ReadByte();
                while (cb-- > 0)
                {
                    sb.Append(reader.ReadChar());
                }
                stringList.Add(sb.ToString());
            }

            Strings = stringList;
        }
    }
}
