/* 
 Copyright (c) 2012, Direct Project
 All rights reserved.

 Authors:
    Sean Nolan      sean.nolan@microsoft.com
    Umesh Madan     umeshma@microsoft.com

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Net;
using System.Xml.Serialization;
using Health.Direct.Agent.Config;
using Health.Direct.Common.Certificates;
using Health.Direct.Config.Client;
using Health.Direct.Common.Caching;

namespace Health.Direct.ResolverPlugins
{
    /// <summary>
    /// Configuration for the Anchor Bundle resolver plugin
    /// </summary>
    [XmlType("BundleResolver")]
    public class BundleResolverSettings : TrustAnchorResolverSettings
    {
        /// <summary>
        /// Creates an instance. Normally called through XML deserialization.
        /// </summary>
        public BundleResolverSettings()
        {
        }

        /// <summary>
        /// Client settings
        /// </summary>
        [XmlElement]
        public ClientSettings ClientSettings
        {
            get;
            set;
        }

        /// <summary>
        /// Optional (but you're an idiot if you don't provide) cache settings
        /// </summary>
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
        
        /// <summary>
        /// If false, will NOT try to verify SSL certs. Set to false when using Test servers
        /// </summary>
        [XmlElement]
        public bool VerifySSL = true;

        /// <summary>
        /// The timeout interval used by the resolver. Default is 0, which uses WebClient's default timeout
        /// </summary>
        [XmlElement("Timeout")]
        public int TimeoutMilliseconds = 0;

        /// <summary>
        /// In case of failure, number of times to retry
        /// </summary>
        [XmlElement]
        public int MaxRetries = 1;

        /// <summary>
        /// Creates a BundleResolver using these settings
        /// </summary>
        public override ITrustAnchorResolver CreateResolver()
        {
            BundleResolver resolver = new BundleResolver();
            resolver.Initialize(this);

            return (resolver);
        }

        /// <summary>
        /// Validates settings are ok
        /// </summary>
        public override void Validate()
        {
            if (this.ClientSettings == null)
            {
                throw new ArgumentNullException("BundleResolverSettings: Missing ClientSettings element");
            }
            this.ClientSettings.Validate();

            if (this.CacheSettings != null)
            {
                this.CacheSettings.Validate();
            }
            if (this.MaxRetries < 0)
            {
                throw new ArgumentNullException("BundleResolverSettings: MaxRetries");
            }
        }
    }
}