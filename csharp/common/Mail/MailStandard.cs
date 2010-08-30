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
        /// <summary>
        /// Constant for name of the RFC 5322 <c>to</c> header
        /// </summary>
        public const string ToHeader = "to";
        /// <summary>
        /// Constant for name of the RFC 5322 <c>cc</c> header
        /// </summary>
        public const string CcHeader = "cc";
        /// <summary>
        /// Constant for name of the RFC 5322 <c>bcc</c> header
        /// </summary>
        public const string BccHeader = "bcc";
        /// <summary>
        /// Constant for name of the RFC 5322 <c>from</c> header
        /// </summary>
        public const string FromHeader = "from";
        /// <summary>
        /// Constant for name of the RFC 5322 <c>sender</c> header
        /// </summary>
        public const string SenderHeader = "sender";
        /// <summary>
        /// Constant for name of the RFC 5322 <c>message-id</c> header
        /// </summary>
        public const string MessageIDHeader = "message-id";
        /// <summary>
        /// Constant for name of the RFC 5322 <c>subject</c> header
        /// </summary>
        public const string SubjectHeader = "subject";
        /// <summary>
        /// Constant for name of the RFC 5322 <c>date</c> header
        /// </summary>
        public const string DateHeader = "date";
        /// <summary>
        /// Constant for name of the RFC 5322 <c>orig-date</c> header
        /// </summary>
        public const string OrigDateHeader = "orig-date";
        /// <summary>
        /// Constant for name of the RFC 5322 <c>in-reply-to</c> header
        /// </summary>
        public const string InReplyToHeader = "in-reply-to";
        /// <summary>
        /// Constant for name of the RFC 5322 <c>references</c> header
        /// </summary>
        public const string ReferencesHeader = "references";
        
        /// <summary>
        /// RFC 5322 headers defining the destination of emails.
        /// </summary>
        public static readonly string[] DestinationHeaders = new[]
        {
            ToHeader, 
            FromHeader,
            CcHeader,
            BccHeader
        };

        /// <summary>
        /// RFC 5322 headers defining the originators of emails.
        /// </summary>
        public static readonly string[] OriginHeaders = new[]
        {
            FromHeader, 
            SenderHeader,
        };

        /// <summary>
        /// RFC 5322 separator character for mail addresses.
        /// </summary>
        public const char MailAddressSeparator = ',';
        //
        // MIME Types
        //
        /// <summary>
        /// <c>content-type</c> value for RFC 5322 (and previous RFC version) messages.
        /// </summary>
		public new class MediaType : MimeStandard.MediaType
		{
            /// <summary>
            /// <c>content-type</c> value for RFC 5322 (and previous RFC version) messages.
            /// </summary>
            public const string WrappedMessage = "message/rfc822";
		}
    }
}
