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
using Health.Direct.Common.Mime;

namespace Health.Direct.Common.Mail
{
    /// <summary>
    /// Defines constants and functions relating to RFC 5322.
    /// </summary>
    public class MailStandard : MimeStandard
    {
        /// <summary>
        /// Character used to enclose quoted strings
        /// </summary>
        public const char DQUOTE = '"';        
        /// <summary>
        /// Maximum allowed characters per line, INCLUDING the CRLF at the end
        /// </summary>
        public const int MaxCharsInLine = 1000; 
        /// <summary>
        /// Header values for common RFC822/5322 Headers
        /// </summary>
        public class Headers
        {
            /// <summary>
            /// Header value for the <c>To</c> header
            /// </summary>
            public const string To = "To";
            /// <summary>
            /// Header value for the <c>CC</c> header
            /// </summary>
            public const string Cc = "Cc";
            /// <summary>
            /// Header value for the <c>BCC</c> header
            /// </summary>
            public const string Bcc = "Bcc";
            /// <summary>
            /// Header value for the <c>From</c> header
            /// </summary>
            public const string From = "From";
            /// <summary>
            /// Header value for the <c>Sender</c> header
            /// </summary>
            public const string Sender = "Sender";
            /// <summary>
            /// Header value for the <c>Message-ID</c> header
            /// </summary>
            public const string MessageID = "Message-ID";
            /// <summary>
            /// Header value for the <c>Subject</c> header
            /// </summary>
            public const string Subject = "Subject";
            /// <summary>
            /// Header value for the <c>Date</c> header
            /// </summary>
            public const string Date = "Date";
            /// <summary>
            /// Header value for the <c>Orig-Date</c> header
            /// </summary>
            public const string OrigDate = "Orig-Date";
            /// <summary>
            /// Header value for the <c>In-Reply-To</c> header
            /// </summary>
            public const string InReplyTo = "In-Reply-To";
            /// <summary>
            /// Header value for the <c>References</c> header
            /// </summary>
            public const string References = "References";
        }
        
        /// <summary>
        /// The set of headers that signal the destinations of a message
        /// </summary>
        public static readonly string[] DestinationHeaders = new[]
                                                                 {
                                                                     Headers.To, 
                                                                     Headers.From,
                                                                     Headers.Cc,
                                                                     Headers.Bcc
                                                                 };

        /// <summary>
        /// The set of headers that signal the origination of a message
        /// </summary>
        public static readonly string[] OriginHeaders = new[]
                                                            {
                                                                Headers.From, 
                                                                Headers.Sender,
                                                            };

        /// <summary>
        /// The separator character for mail address headers
        /// </summary>
        public const char MailAddressSeparator = ',';
        //
        // MIME Types
        //
        /// <summary>
        /// Extension to MediaType for wrapped messages.
        /// </summary>
        public new class MediaType : MimeStandard.MediaType
        {
            /// <summary>
            /// The <c>Content-Type</c> for fully wrapped messages
            /// </summary>
            public const string WrappedMessage = "message/rfc822";
        }
    }
}