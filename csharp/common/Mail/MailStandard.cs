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
    public class MailStandard : MimeStandard
    {
        //
        // Common RFC822/5322 Headers
        //
        public const string ToHeader = "to";
        public const string CcHeader = "cc";
        public const string BccHeader = "bcc";
        public const string FromHeader = "from";
        public const string SenderHeader = "sender";
        public const string MessageIDHeader = "message-id";
        public const string SubjectHeader = "subject";
        public const string DateHeader = "date";
        public const string OrigDateHeader = "orig-date";
        public const string InReplyToHeader = "in-reply-to";
        public const string ReferencesHeader = "references";
        
        public static readonly string[] DestinationHeaders = new[]
        {
            ToHeader, 
            FromHeader,
            CcHeader,
            BccHeader
        };

        public static readonly string[] OriginHeaders = new[]
        {
            FromHeader, 
            SenderHeader,
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
