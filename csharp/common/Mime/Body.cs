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

namespace Health.Direct.Common.Mime
{
    /// <summary>
    /// Represents the body of an RFC 5322 message.
    /// </summary>
    /// <remarks>
    /// Note that a body, both in the RFC 5322 spec and in this implementation is not a full
    /// MIME entity, and will need to be combined with the appropriate <c>Content-</c> headers
    /// for proper interpretation.
    /// </remarks>
    public class Body : MimePart
    {
        /// <summary>
        /// Initializes an empty body
        /// </summary>
        public Body()
            : base(MimePartType.Body)
        {
        }

        /// <summary>
        /// Intializes a body from a <see cref="StringSegment"/>
        /// </summary>
        /// <param name="body">The body text <see cref="StringSegment"/></param>
        public Body(StringSegment body)
            : base(MimePartType.Body, body)
        {
        }
        
        /// <summary>
        /// Initializes a body from a <see cref="string"/> representation of the body text
        /// </summary>
        /// <param name="body">A <see cref="String"/> providing the body text</param>
        public Body(string body)
            : base(MimePartType.Body)
        {
            this.Text = body;
        }        
                
        
        internal Body(Body body)
            : base(MimePartType.Body, body.SourceText)
        {
        }
    }
}