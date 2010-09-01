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
using NHINDirect.Mime;

namespace NHINDirect.Mail
{
    /// <summary>
    /// Defines constants and functions relating to RFC 5322.
    /// </summary>
    public class MailStandard : MimeStandard
    {
        //
        // Common RFC822/5322 Headers
        //        
        public class Headers
        {
            public const string To = "To";
            public const string Cc = "Cc";
            public const string Bcc= "Bcc";
            public const string From = "From";
            public const string Sender = "Sender";
            public const string MessageID = "Message-ID";
            public const string Subject = "Subject";
            public const string Date = "Date";
            public const string OrigDate = "Orig-Date";
            public const string InReplyTo = "In-Reply-To";
            public const string References = "References";
        }
        
        public static readonly string[] DestinationHeaders = new[]
        {
            Headers.To, 
            Headers.From,
            Headers.Cc,
            Headers.Bcc
        };

        public static readonly string[] OriginHeaders = new[]
        {
            Headers.From, 
            Headers.Sender,
        };

        public const char MailAddressSeparator = ',';
        //
        // MIME Types
        //
		public new class MediaType : MimeStandard.MediaType
		{
			public const string WrappedMessage = "message/rfc822";
		}
    }
}
