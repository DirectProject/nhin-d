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
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;
using System.Security.Cryptography.X509Certificates;
using NHINDirect.Certificates;
using System.Net;

namespace NHINDirect.Agent.Config
{
    /// <summary>
    /// Settings for a DNS-based certificate resolver.
    /// </summary>
    [XmlType("DnsCertificateStore")]
    public class DnsCertResolverSettings : CertResolverSettings
    {
        bool m_assumeWildcard = false;
        
        /// <summary>
        /// Creates an instance, normally called from the XML load.
        /// </summary>
        public DnsCertResolverSettings()
        {
        }
        
        /// <summary>
        /// The IP address (in <c>string</c> form) used by the resolver.
        /// </summary>
        [XmlElement]
        public string ServerIP
        {
            get;
            set;
        }
        
        /// <summary>
        /// The timeout interval used by the resolver.
        /// </summary>
        [XmlElement]
        public int Timeout
        {
            get;
            set;
        }
        
        /// <summary>
        /// A default fallback domain used to remap addresses that are not able to be managed for DNS certificates.
        /// </summary>
        [XmlElement]
        public string FallbackDomain
        {
            get;
            set;
        }

        /// <summary>
        /// Does this resolver assume the DNS responser implements wildcard support?
        /// </summary>
        public bool AssumeWildcardSupport
        {
            get
            {
                return m_assumeWildcard;
            }
            set
            {
                m_assumeWildcard = value;
            }
        }
        
        /// <summary>
        /// Validates the configuration settings.
        /// </summary>
        public override void Validate()
        {
            if (string.IsNullOrEmpty(this.ServerIP))
            {
                throw new AgentConfigException(AgentConfigError.MissingDnsServerIP);
            }
        }
        
        /// <summary>
        /// Creates the DNS certificate resolver from the configured settings.
        /// </summary>
        /// <returns>A configured DNS certificate resolver.</returns>
        public override ICertificateResolver CreateResolver()
        {
            this.Validate();
            DnsCertResolver resolver = new DnsCertResolver(IPAddress.Parse(this.ServerIP), this.Timeout, this.FallbackDomain);
            resolver.AssumeWildcardSupport = m_assumeWildcard;
            return resolver;
        }
    }
}
