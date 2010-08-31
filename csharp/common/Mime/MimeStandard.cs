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
using System.Net.Mime;
using NHINDirect.Cryptography;

namespace NHINDirect.Mime
{
    public class MimeStandard
    {
        //
        // Character Tokens
        //
        public const char CR = '\r';
        public const char LF = '\n';
        public const string CRLF = "\r\n";
        public const char Escape = '\\';
        public const char NameValueSeparator = ':';
        public const char BoundaryChar = '-';
        public const string BoundarySeparator = "--";
        //
        // Headers
        //
        public const string HeaderPrefix = "Content-";
        public const string VersionHeader = "MIME-Version";

        public const string ContentTypeHeader = "Content-Type";
        public const string ContentIDHeader = "Content-ID";
        public const string ContentDispositionHeader = "Content-Disposition";
        public const string ContentDescriptionHeader = "Content-Description";
        public const string ContentTransferEncodingHeader = "Content-Transfer-Encoding";
        //
        // Encodings
        //
        public const string TransferEncodingBase64 = "base64";
        public const string TransferEncoding7Bit = "7bit";
        public const string TransferEncodingQuoted = "quoted-printable";
        //
        // Mime/Content-Type
        //
		public class MediaType
		{
			public const string TextPlain = "text/plain";
			public const string Default = TextPlain;
			public const string Multipart = "multipart";
			public const string MultipartMixed = "multipart/mixed";
		}
        //
        // Used to implement Parsing Operations
        //
        
        public static bool IsWhitespace(char ch)
        {
            //
            // CR/LF are reserved characters in MIME
            //
            return (ch == ' ' || ch == '\t');
        }

        //
        // MIME RFC: 
        //  - All string comparisions are case-insensitive        
        //  - locale independant - i.e. ordinal
        //
        public static StringComparer Comparer
        {
            get
            {
                return StringComparer.OrdinalIgnoreCase;
            }
        }
        
        public static StringComparison Comparison
        {
            get
            {
                return StringComparison.OrdinalIgnoreCase;
            }
        }
        
        public static bool Equals(string x, string y)
        {
            return string.Equals(x, y, Comparison);
        }

        public static bool StartsWith(string x, string y)
        {
            return x.StartsWith(y, Comparison);
        }

        public static bool Contains(string x, string y)
        {
            return (x.IndexOf(y, Comparison) >= 0);
        }

        public static string AsString(TransferEncoding encoding)
        {
            switch (encoding)
            {
                default:
                    throw new NotSupportedException();

                case TransferEncoding.Base64:
                    return MimeStandard.TransferEncodingBase64;

                case TransferEncoding.SevenBit:
                    return MimeStandard.TransferEncoding7Bit;

                case TransferEncoding.QuotedPrintable:
                    return MimeStandard.TransferEncodingQuoted;
            }
        }
    }
}
