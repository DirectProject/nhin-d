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

namespace Health.Direct.Common.Mime
{
    /// <summary>
    /// MIME processing errors that may trigger <see cref="MimeException"/> exceptions.
    /// </summary>
    public enum MimeError
    {
        /// <summary>
        /// Unknown unexpected event.
        /// </summary>
        Unexpected = 0,
        /// <summary>
        /// The MIME entity does not terminate lines properly.
        /// </summary>
        InvalidCRLF,
        /// <summary>
        /// The content could not be interpreted as a MIME entity
        /// </summary>
        InvalidMimeEntity,
        /// <summary>
        /// A header was expected but could not be processed as a header.
        /// </summary>
        InvalidHeader,
        /// <summary>
        /// A MIME body was expected but could not be processed as a MIME body.
        /// </summary>
        InvalidBody,
        /// <summary>
        /// A subpart of a multipart MIME entity was expected, but could not be processed as a subpart.
        /// </summary>
        InvalidBodySubpart,
        /// <summary>
        /// A header did not have a name value separator
        /// </summary>
        MissingNameValueSeparator,
        /// <summary>
        /// A header had a missing value part
        /// </summary>
        MissingHeaderValue,
        /// <summary>
        /// A MIME body was expected but was not found
        /// </summary>
        MissingBody,
        /// <summary>
        /// The actual content type did not match the expected content type.
        /// </summary>
        ContentTypeMismatch,
        /// <summary>
        /// The actual transfer encoding did not match the expected transfer encoding.
        /// </summary>
        TransferEncodingMismatch,
        /// <summary>
        /// This type of transfer encoding is not supported by the system.
        /// </summary>
        TransferEncodingNotSupported,
        /// <summary>
        /// Base64 encoded content was expected but not provided.
        /// </summary>
        Base64EncodingRequired,
        /// <summary>
        /// A MIME body was declared multipart but was not provided as multipart.
        /// </summary>
        NotMultipart,
        /// <summary>
        /// A multipart message was missing a boundary separator.
        /// </summary>
        MissingBoundarySeparator,
        /// <summary>
        /// Quotable Printable encoding - characters encoded incorrectly
        /// </summary>
        InvalidQuotedPrintableEncodedChar,
        /// <summary>
        /// Quotable Printable encoding - Invalid soft line break
        /// </summary>
        InvalidQuotedPrintableSoftLineBreak,
        /// <summary>
        /// Invalid field parameter
        /// </summary>
        InvalidFieldParameter
    }
}