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
    public static class Extensions
    {
        public static bool IsMediaType(this ContentType contentType, string mediaType)
        {
            return (MimeStandard.Equals(contentType.MediaType, mediaType));
        }
        
        public static bool IsParameter(this ContentType contentType, string parameter, string value)
        {
            string paramValue = contentType.Parameters[parameter];
            if (paramValue == null)
            {
                return false;
            }
            
            return MimeStandard.Equals(paramValue, value);
        }

		public static string AsString(this DigestAlgorithm algorithm)
		{
			switch (algorithm)
			{
				default:
					throw new NotSupportedException();

				case DigestAlgorithm.SHA1:
					return "sha1";

				case DigestAlgorithm.SHA256:
					return "sha256";

				case DigestAlgorithm.SHA384:
					return "sha384";

				case DigestAlgorithm.SHA512:
					return "sha512";
			}
		}

		public static string AsString(this TransferEncoding encoding)
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
