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
using System.IO;

namespace Health.Direct.Common.Mime
{
    /// <summary>
    /// A very basic mime/entity writer
    /// The idea is that we are working off EXISTING WELL FORMED messages whose lexical structure we cannot change 
    /// (esp because of cryptography). Therefore, the assumption is that the orginal authoring application had done 
    /// the needful with canonicalization etc. 
    /// </summary>
    public class MimeWriter : IDisposable
    {        
        TextWriter m_writer;
        
        /// <summary>
        /// Initializes an instance with the supplied <see cref="TextWriter"/>
        /// </summary>
        /// <param name="writer">The writer to use for this instance.</param>
        public MimeWriter(TextWriter writer)
        {
            SetWriter(writer);
        }
        
        /// <summary>
        /// Sets the writer for this instance.
        /// </summary>
        /// <param name="writer">The writer to use for this instance.</param>
        public void SetWriter(TextWriter writer)
        {
            if (writer == null)
            {
                throw new ArgumentNullException("writer");
            }
            Close();
            m_writer = writer;
        }

        /// <summary>
        /// Writes a collection of headers.
        /// </summary>
        /// <param name="headers">The headers to write.</param>
        public void Write(HeaderCollection headers)
        {
            if (headers == null)
            {
                throw new ArgumentNullException("headers");
            }
            
            foreach(Header header in headers)
            {
                Write(header);
            }
        }
        
        /// <summary>
        /// Writes a header
        /// </summary>
        /// <param name="header">The header to write.</param>
        public void Write(Header header)
        {
            if (header == null)
            {
                throw new ArgumentNullException("header");
            }
            
            WriteLine(header.SourceText);
        }
        

        /// <summary>
        /// Writes a body
        /// </summary>
        /// <param name="body">The body to write.</param>
        public void Write(Body body)
        {
            if (body == null)
            {
                throw new ArgumentNullException("body");
            }
            
            Write(body.SourceText);
        }
        
        // not referenced in any current code...
        //public void WriteHeader(string name, string value)
        //{
        //    if (string.IsNullOrEmpty(name))
        //    {
        //        throw new ArgumentException("name was null or empty", "name");
        //    }
            
        //    m_writer.Write(name);
        //    if (!string.IsNullOrEmpty(value))
        //    {
        //        m_writer.Write(MimeStandard.NameValueSeparator);
        //        m_writer.Write(value);
        //    }
            
        //    WriteCRLF();
        //}
                
        /// <summary>
        /// Writes a boundary between body parts of a multipart message.
        /// </summary>
        /// <param name="boundary">The boundary string.</param>
        /// <param name="isLast"><c>true</c> if this is the last part to write so that the
        /// epilogue will be written, <c>false</c> otherwise.</param>
        public void WriteMimeBoundary(string boundary, bool isLast)
        {
            //
            // As per MIME spec, boundaries START with a CRLF
            //
            WriteCRLF();
            if (isLast)
            {
                WriteLine(MimeStandard.BoundarySeparator + boundary + MimeStandard.BoundarySeparator);
            }
            else
            {
                WriteLine(MimeStandard.BoundarySeparator + boundary);
            }
        }
        
        /// <summary>
        /// Writes the supplied <see cref="StringSegment"/>
        /// </summary>
        /// <param name="text">The segment to write.</param>
        public void Write(StringSegment text)
        {
            string source = text.Source;
            for(int i = text.StartIndex, max = text.EndIndex; i <= max; ++i)
            {
                m_writer.Write(source[i]);
            }
        }

        /// <summary>
        /// Writes the supplied <see cref="StringSegment"/> with a MIME newline.
        /// </summary>
        /// <param name="text">The segment to write.</param>
        public void WriteLine(StringSegment text)
        {
            Write(text);
            WriteCRLF();
        }

        /// <summary>
        /// Writes the supplied <c>string</c> with a MIME newline.
        /// </summary>
        /// <param name="text">The <c>string</c> to write.</param>
        public void WriteLine(string text)
        {
            m_writer.Write(text);
            WriteCRLF();
        }
        
        /// <summary>
        /// Writes a MIME compliant newline (CRLF)
        /// </summary>
        public void WriteCRLF()
        {
            m_writer.Write(MimeStandard.CRLF);
        }        
        
        /// <summary>
        /// Closes the writer.
        /// </summary>
        public void Close()
        {
            if (m_writer != null)
            {
                m_writer.Close();
                m_writer = null;
            }        
        }
        
        #region IDisposable Members

        /// <summary>
        /// Frees resources for this instance.
        /// </summary>
        public void Dispose()
        {
            Close();
        }

        #endregion
    }
}