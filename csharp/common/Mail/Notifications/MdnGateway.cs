/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Arien Malec     arien.malec@nhindirect.org
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

namespace Health.Direct.Common.Mail.Notifications
{
    /// <summary>
    /// Represents an MDN Gateway as specified by RFC 3798
    /// </summary>
    /// <remarks>
    /// From RFC 3798, 3.2.2, The MDN-Gateway field
    /// mdn-gateway-field = "MDN-Gateway" ":" mta-name-type ";" mta-name
    /// ...
    /// For gateways into Internet Mail, the MTA-name-type will normally be
    /// "smtp", and the mta-name will be the Internet domain name of the
    /// gateway.
    /// </remarks>
    public class MdnGateway
    {
        /// <summary>
        /// The gateway type for gateways to Internet Mail.
        /// </summary>
        public const string DefaultGatewayType = "smtp";
        
        string m_domain;
        string m_type;
        
        /// <summary>
        /// Initializes an instance with the specified <paramref name="domain"/> and the
        /// default type of "smtp"
        /// </summary>
        /// <param name="domain">The domain name of this MDN Gateway</param>
        public MdnGateway(string domain)
            : this(domain, DefaultGatewayType)
        {
        }

        /// <summary>
        /// Initializes an instance with the specified domain and type
        /// </summary>
        /// <param name="domain">The domain name of this MDN Gateway</param>
        /// <param name="type">The gateway type</param>
        public MdnGateway(string domain, string type)
        {
            this.Domain = domain;
            this.Type = type;
        }

        /// <summary>
        /// Gets the gateway type
        /// </summary>
        public string Type
        {
            get
            {
                return m_type;
            }
            private set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new ArgumentException("value was null or empty", "value");
                }
                
                m_type = value;
            }
        }

        /// <summary>
        /// Gets the gateway domain
        /// </summary>
        public string Domain
        {
            get
            {
                return m_domain;
            }
            private set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new ArgumentException("value was null or empty", "value");
                }
                
                m_domain = value;
            }            
        }

        /// <summary>
        /// Returns a string representation following the conventions for RFC 3798
        /// </summary>
        /// <returns>A string representation of this gateway</returns>
        public override string ToString()
        {
            return string.Format("{0};{1}", this.Type, this.Domain);
        }
    }
}