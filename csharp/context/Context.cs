/* 
 Copyright (c) 2010-2017, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using MimeKit;
using MimePart = MimeKit.MimePart;

namespace Health.Direct.Context
{
    /// <summary>
    /// Express context of a Direct message.
    /// Represents the Direct Context Metadata as defined in the
    /// "Implementation Guide for Expressing Context in Direct Messaging"
    /// </summary>
    /// <remarks>
    /// Location of document...
    /// </remarks>
    public class Context: MimePart
    {
        /// <summary>
        /// Default file name of <see cref="MimeKit.ContentDisposition"/>.
        /// </summary>
        public const string FileNameValue = "metadata.txt";

        /// <summary>
        /// <see cref="Metadata"/> content
        /// </summary>
        public Metadata Metadata { get; }

        /// <summary>
        /// Initializes an empty instances
        /// </summary>
        public Context(): this("text", "plain")
        {
            Metadata = new Metadata();
            ContentId = Guid.NewGuid().ToString("N");
            ContentDisposition = new ContentDisposition(MimeStandard.DispositionType.Attachment);
            ContentDisposition.FileName = FileNameValue;
        }

        private Context(string mediaType, string mediaSubtype) : base (mediaType, mediaSubtype)
        {
            Metadata = new Metadata();
        }

        /// <summary>
        /// Initializes an empty instances
        /// </summary>
        public Context(string mediaType, string mediaSubtype, string contentId, string filename = FileNameValue) 
            : base (mediaType, mediaSubtype)
        {
            Metadata = new Metadata();
            ContentId = contentId;
            ContentDisposition.FileName = filename;
        }

        /// <summary>
        /// Initializes an empty instances
        /// </summary>
        public Context(string mediaType, string mediaSubtype, string contentId, Metadata metadata, string filename = FileNameValue)
            : base(mediaType, mediaSubtype)
        {
            Metadata = metadata;
            ContentId = contentId;
            ContentDisposition = new ContentDisposition(MimeStandard.DispositionType.Attachment);
            ContentDisposition.FileName = filename;
        }
    }
}
