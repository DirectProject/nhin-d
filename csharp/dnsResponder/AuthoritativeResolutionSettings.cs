/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Ali Emami   aliemami@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System.Xml;
using System.Xml.Serialization;
using Health.Direct.Common.DnsResolver;

namespace Health.Direct.DnsResponder
{
    /// <summary>
    /// Settings used for authoritative resolution of DNS requests. 
    /// </summary>    
    public class AuthoritativeResolutionSettings
    {
        bool m_cache = false;
        int m_timeoutMilliseconds = (int) DnsClient.DefaultTimeout.TotalMilliseconds;
        int m_dnsResolutionPort = DnsStandard.DnsPort;
        DnsIPEndpointSettings[] m_PrimaryNameServers;
        
        public AuthoritativeResolutionSettings()
        {
        }

        /// <summary>
        /// Cache DNS resolution results. 
        /// </summary>
        [XmlElement]
        public bool Cache
        {
            get { return m_cache; }
            set { m_cache = value; }
        }
        
        /// <summary>
        /// The timeout in milliseconds for DNS queries.
        /// </summary>
        [XmlElement]
        public int TimeoutMilliseconds
        {
            get { return m_timeoutMilliseconds; }
            set { m_timeoutMilliseconds = value; }
        }

        /// <summary>
        /// The port used to resolve DNS requests against the authoritative name servers.
        /// </summary>
        [XmlElement]
        public int DnsResolutionPort
        {
            get { return m_dnsResolutionPort; }
            set { m_dnsResolutionPort = value; }
        }

        /// <summary>
        /// The primary name servers used to lookup the authoritative name servers for 
        /// a DNS query.
        /// </summary>
        [XmlElement]
        public DnsIPEndpointSettings[] PrimaryNameServer
        {
            get { return m_PrimaryNameServers; }
            set { m_PrimaryNameServers = value; }          
        }
    }
}
