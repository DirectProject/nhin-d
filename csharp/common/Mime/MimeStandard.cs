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
using System.Net.Mime;

namespace Health.Direct.Common.Mime
{
    /// <summary>
    /// Implements constants and utility functions for the MIME standard (RFC TODO).
    /// </summary>
    public class MimeStandard
    {
        //
        // Character Tokens
        //
        /// <summary>
        /// The MIME standard CR character
        /// </summary>
        public const char CR = '\r';
        /// <summary>
        /// The MIME standard LF character
        /// </summary>
        public const char LF = '\n';
        /// <summary>
        /// CRLF is the standard line terminator for RFC 5322 and MIME.
        /// </summary>
        public const string CRLF = "\r\n";
        /// <summary>
        /// The MIME escape character.
        /// </summary>
        public const char Escape = '\\';
        /// <summary>
        /// The MIME separator between header and header value.
        /// </summary>
        public const char NameValueSeparator = ':';
        /// <summary>
        /// The character from which the standard MIME boundary is constructed
        /// </summary>
        public const char BoundaryChar = '-';
        /// <summary>
        /// The standard MIME boundary separator.
        /// </summary>
        public const string BoundarySeparator = "--";
        //
        // Headers
        //
        /// <summary>
        /// The prefix string for MIME content headers
        /// </summary>
        public const string HeaderPrefix = "Content-";
        /// <summary>
        /// The prefix string for MIME extension headers
        /// </summary>
        public const string SpecialHeaderPrefix = "X-";
        /// <summary>
        /// The standard MIME version header.
        /// </summary>
        public const string VersionHeader = "MIME-Version";

        /// <summary>
        /// The standard string representation of the <c>Content-Type</c> MIME header
        /// </summary>
        public const string ContentTypeHeader = "Content-Type";
        /// <summary>
        /// The standard string representation of the <c>Content-ID</c> MIME header
        /// </summary>
        public const string ContentIDHeader = "Content-ID";
        /// <summary>
        /// The standard string representation of the <c>Content-Disposition</c> MIME header
        /// </summary>
        public const string ContentDispositionHeader = "Content-Disposition";
        /// <summary>
        /// The standard string representation of the <c>Content-Description</c> MIME header
        /// </summary>
        public const string ContentDescriptionHeader = "Content-Description";
        /// <summary>
        /// The standard string representation of the <c>Content-Transfer-Encoding</c> MIME header
        /// </summary>
        public const string ContentTransferEncodingHeader = "Content-Transfer-Encoding";
        //
        // Encodings
        //
        /// <summary>
        /// The standard string representation for the Base 64 <c>Content-Transfer-Encoding</c>
        /// </summary>
        public const string TransferEncodingBase64 = "base64";
        /// <summary>
        /// The standard string representation for the ASCII 7-bit clean <c>Content-Transfer-Encoding</c>
        /// </summary>
        public const string TransferEncoding7Bit = "7bit";
        /// <summary>
        /// The standard string representation for the ASCII quoted printable <c>Content-Transfer-Encoding</c>
        /// </summary>
        public const string TransferEncodingQuoted = "quoted-printable";
        //
        // Mime/Content-Type
        //
        /// <summary>
        /// Default <c>Content-Type</c> media type values as per the IANA registry.
        /// </summary>
        public class MediaType
        {
            /// <summary>
            /// The <c>text/plain media type</c>
            /// </summary>
            public const string TextPlain = "text/plain";
            /// <summary>
            /// The default media type to assume if the actual media type can not be understood.
            /// </summary>
            public const string Default = TextPlain;
            /// <summary>
            /// The prefix for <c>multipart</c> content.
            /// </summary>
            public const string Multipart = "multipart";
            /// <summary>
            /// The <c>multipart/mixed</c> media type.
            /// </summary>
            public const string MultipartMixed = "multipart/mixed";

        }
        //
        // Used to implement Parsing Operations
        //
        internal const int MaxLineLength = 1000;               
        /// <summary>
        /// Tests if the <paramref name="ch"/> is MIME whitespace.
        /// </summary>
        /// <param name="ch">The <see cref="char"/> to test</param>
        /// <returns><c>true</c> if <paramref name="ch"/> is MIME whitespace, <c>false</c> otherwise</returns>
        public static bool IsWhitespace(char ch)
        {
            //
            // CR/LF are reserved characters in MIME
            //
            return (ch == ' ' || ch == '\t');
        }

        /// <summary>
        /// Implements the <see cref="StringComparer"/> for MIME string comparisons.
        /// </summary>
        /// <remarks>
        /// MIME RFC: 
        ///  - All string comparisions are case-insensitive        
        ///  - locale independant - i.e. ordinal
        /// </remarks>
        public static StringComparer Comparer
        {
            get
            {
                return StringComparer.OrdinalIgnoreCase;
            }
        }

        /// <summary>
        /// Implements the <see cref="StringComparison"/> function for MIME string comparisons.
        /// </summary>
        /// <remarks>
        /// MIME RFC: 
        ///  - All string comparisions are case-insensitive        
        ///  - locale independant - i.e. ordinal
        /// </remarks>
        public static StringComparison Comparison
        {
            get
            {
                return StringComparison.OrdinalIgnoreCase;
            }
        }
        
        /// <summary>
        /// Implements equality comparison for MIME strings
        /// </summary>
        /// <param name="x">The reference string to test for equality</param>
        /// <param name="y">The target string to test for equality.</param>
        /// <returns></returns>
        public static bool Equals(string x, string y)
        {
            return string.Equals(x, y, Comparison);
        }

        /// <summary>
        /// Implements initial string comparison for MIME strings
        /// </summary>
        /// <param name="x">The string to test if it starts with <paramref name="y"/></param>
        /// <param name="y">The string to test if it is an initial substring of <paramref name="x"/></param>
        /// <returns><c>true</c> if <paramref name="x"/> starts with <paramref name="y"/> under
        /// MIME comparison rules, <c>false</c> otherwise</returns>
        public static bool StartsWith(string x, string y)
        {
            return x.StartsWith(y, Comparison);
        }

        /// <summary>
        /// Implements string inclusion testing for MIME strings
        /// </summary>
        /// <param name="x">The string to test if it contains <paramref name="y"/></param>
        /// <param name="y">The string to test if it is contained in <paramref name="x"/></param>
        /// <returns><c>true</c> if <paramref name="x"/> contains <paramref name="y"/> under
        /// MIME comparison rules, <c>false</c> otherwise</returns>
        public static bool Contains(string x, string y)
        {
            return (x.IndexOf(y, Comparison) >= 0);
        }

        /// <summary>
        /// Returns a  string representation of <paramref name="encoding"/> compatable with the <c>micalg</c> parameter
        /// </summary>
        /// <param name="encoding">The <see cref="TransferEncoding"/> to stringify.</param>
        /// <returns>The string representation of the encoding compatable with the <c>Content-Transfer-Encoding</c> header</returns>
        public static string ToString(TransferEncoding encoding)
        {
            switch (encoding)
            {
                default:
                    throw new MimeException(MimeError.TransferEncodingNotSupported);

                case TransferEncoding.Base64:
                    return MimeStandard.TransferEncodingBase64;

                case TransferEncoding.SevenBit:
                    return MimeStandard.TransferEncoding7Bit;

                case TransferEncoding.QuotedPrintable:
                    return MimeStandard.TransferEncodingQuoted;
            }
        }
        
        /// <summary>
        /// Parse the text representing the value of a ContentTransferEncodingHeader.
        /// </summary>
        /// <param name="value">header value</param>
        /// <returns>The <see cref="TransferEncoding"/> enum that represents the value.</returns>
        public static TransferEncoding ToTransferEncoding(string value)
        {
            value = value.Trim().ToLower();
            switch(value)
            {
                default:
                    return TransferEncoding.Unknown;
                    
                case MimeStandard.TransferEncodingBase64:
                    return TransferEncoding.Base64;
                
                case MimeStandard.TransferEncoding7Bit:
                    return TransferEncoding.SevenBit;
                    
                case MimeStandard.TransferEncodingQuoted:
                    return TransferEncoding.QuotedPrintable;    
            }
        }
    }
}