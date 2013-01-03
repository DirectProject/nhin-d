/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Ali Emami       aliemami@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System.Xml.Serialization;

using Health.Direct.Agent.Config;
using Health.Direct.Common.Certificates;
using Health.Direct.Config.Client;
using Health.Direct.Common.Caching;

namespace Health.Direct.SmtpAgent
{
    [XmlType("ServiceResolver")]
    public class CertServiceResolverSettings : CertResolverSettings
    {
        [XmlElement]        
        public ClientSettings ClientSettings
        {
            get;
            set;
        }

        /// <summary>
        /// If true, will NEVER look for address specific certificates
        /// False by default.
        /// 
        /// Use this if you are never going to issue user specific certificates. 
        /// This will eliminate 1 roundtrip to the Config Service for every message. 
        /// </summary>
        [XmlElement]
        public bool OrgCertificatesOnly = false;
        
        public override ICertificateResolver CreateResolver()
        {
            ConfigCertificateResolver resolver = new ConfigCertificateResolver(this.ClientSettings);
            resolver.OrgCertificatesOnly = this.OrgCertificatesOnly;
            return resolver;
        }

        public override void Validate()
        {
            if (this.ClientSettings == null)
            {
                throw new SmtpAgentException(SmtpAgentError.MissingCertResolverClientSettings);
            }
            this.ClientSettings.Validate();
        }
    }

    public class AnchorServiceResolverSettings : TrustAnchorResolverSettings
    {
        [XmlElement]
        public ClientSettings ClientSettings
        {
            get;
            set;
        }

        [XmlElement]
        public CacheSettings CacheSettings
        {
            get;
            set; 
        }

        /// <summary>
        /// If true, will NEVER look for address specific certificates
        /// False by default.
        /// 
        /// Use this if you are never going to issue user specific certificates. 
        /// This will eliminate 1 roundtrip to the Config Service for every message. 
        /// </summary>
        [XmlElement]
        public bool OrgCertificatesOnly = false;

        public override ITrustAnchorResolver CreateResolver()
        {
            ConfigAnchorResolver resolver = new ConfigAnchorResolver(this.ClientSettings, this.CacheSettings);
            resolver.OrgCertificatesOnly = this.OrgCertificatesOnly;
            
            return resolver;
        }

        public override void Validate()
        {
            if (this.ClientSettings == null)
            {
                throw new SmtpAgentException(SmtpAgentError.MissingAnchorResolverClientSettings);
            }
            this.ClientSettings.Validate();

            if (this.CacheSettings != null)
            {
                this.CacheSettings.Validate(); 
            }
        }
    }    
}