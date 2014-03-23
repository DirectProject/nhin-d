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
using System.Collections.Generic;
using System.Net;
using System.Security.Cryptography.X509Certificates;
using System.Security.Cryptography.Pkcs;
using Health.Direct.Common.Container;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;
using Health.Direct.Common.Caching;
using Health.Direct.Common.Certificates;

namespace Health.Direct.ResolverPlugins
{
    /// <summary>
    /// Implements a trust anchor resolver that downloads from configured bundle URLs
    /// </summary>
    public class BundleResolver : ITrustAnchorResolver, IPlugin
    {
        /// <summary>
        /// Constructor. Doesn't do anything.
        /// </summary>
        public BundleResolver()
        {
        }

        /// <summary>
        /// Tuck away settings and create resolvers. 
        /// </summary>
        /// <param name="settings"></param>
        public void Initialize(BundleResolverSettings settings)
        {
            m_settings = settings;

            CacheSettings incomingCacheSettings =
                new CacheSettings(m_settings.CacheSettings) { Name = "BundleCache.incoming" };

            CacheSettings outgoingCacheSettings =
                new CacheSettings(m_settings.CacheSettings) { Name = "BundleCache.outgoing" };

            m_incomingResolver =
                new CertificateResolver(new BundleAnchorIndex(m_settings, true), incomingCacheSettings);

            m_outgoingResolver =
                new CertificateResolver(new BundleAnchorIndex(m_settings, false), outgoingCacheSettings);
        }

        #region IPlugin

        /// <summary>
        /// Plugin initialization method
        /// </summary>
        /// <param name="pluginDef"></param>
        public void Init(PluginDefinition pluginDef)
        {
            this.Initialize(pluginDef.DeserializeSettings<BundleResolverSettings>());
        }

        #endregion

        #region ITrustAnchorResolver

        /// <summary>
        /// Factory providing certificate resolver for outgoing messages. 
        /// </summary>
        public ICertificateResolver OutgoingAnchors
        { get { return(m_outgoingResolver); } }

        /// <summary>
        /// Factory providing certificate resolver for incoming messages. 
        /// </summary>
        public ICertificateResolver IncomingAnchors
        { get { return(m_incomingResolver); } }

        #endregion

        /// <summary>
        /// If true, will NEVER look for address specific certificates
        /// False by default.
        /// 
        /// Use this if you are never going to issue or store user specific certificates. 
        /// This will eliminate 1 roundtrip to the anchor store for every message. 
        /// 
        /// You should only use this setting for your own private keys and anchors. 
        /// </summary>
        public bool OrgCertificatesOnly
        {
            get
            {
                return m_settings.OrgCertificatesOnly;
            }
            set
            {
                m_settings.OrgCertificatesOnly = value;
                m_incomingResolver.OrgCertificatesOnly = value;
                m_outgoingResolver.OrgCertificatesOnly = value;
            }
        }

        private BundleResolverSettings m_settings;
        private CertificateResolver m_incomingResolver;
        private CertificateResolver m_outgoingResolver;
    }
}