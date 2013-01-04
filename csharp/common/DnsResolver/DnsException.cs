/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Sean Nolan      seannol@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

namespace Health.Direct.Common.DnsResolver
{
    /// <summary>
    /// Represents program excecution errors relating to DNS requests or responses.
    /// </summary>
    public class DnsException : Exception
    {
        /// <summary>
        /// Initializes a default instance.
        /// </summary>
        public DnsException()
        {
        }

        /// <summary>
        /// Initializes an instance that was triggered by the provided original exception.
        /// </summary>
        /// <param name="inner">The underlying exception that triggered this exception.</param>
        public DnsException(Exception inner)
            : base(null, inner)
        {
        }
        
        /// <summary>
        /// Initializes an instance that was triggered by the provided original exception.
        /// </summary>
        /// <param name="message">message for this exception</param>
        public DnsException(string message)
            : base(message)
        {
        }
    }

    /// <summary>
    /// Represents program excecution errors relating to DNS failures on request occuring at the DNS server
    /// </summary>
    /// <remarks>
    /// See remarks for <see cref="ResponseCode"/> for more details.
    /// </remarks>
    public class DnsServerException : DnsException
    {
        DnsStandard.ResponseCode m_responseCode;
        
        /// <summary>
        /// Initializes an instace with the specified <paramref name="responseCode"/>
        /// </summary>
        /// <param name="responseCode">The server response code that triggered this exception.</param>
        public DnsServerException(DnsStandard.ResponseCode responseCode)
        {
            m_responseCode = responseCode;
        }
        
        /// <summary>
        /// The response code that triggered this exception.
        /// </summary>
        public DnsStandard.ResponseCode ResponseCode
        {
            get
            {
                return m_responseCode;
            }
            
        }

        /// <summary>
        /// A string representation of this exception.
        /// </summary>
        public override string ToString()
        {
            return string.Format("ERROR={0}{1}{2}", m_responseCode, Environment.NewLine, base.ToString());
        }
    }
}