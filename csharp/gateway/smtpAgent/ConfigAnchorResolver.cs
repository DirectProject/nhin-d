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
using System;
using System.Security.Cryptography.X509Certificates;

using Health.Direct.Common.Certificates;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;
using Health.Direct.Common.Caching;

namespace Health.Direct.SmtpAgent
{
    public class ConfigAnchorResolver : ITrustAnchorResolver
    {
        CertificateResolver m_incomingResolver;
        CertificateResolver m_outgoingResolver;
        bool m_orgCertsOnly = false;
        
        /// <summary>
        /// Create a resolver that resolvers anchors from the middle tier
        /// </summary>
        /// <param name="clientSettings">Settings to set up WCF connections to the middle tier</param>
        /// <param name="cacheSettings">Optional: if caching is enabled. Else null</param>
        public ConfigAnchorResolver(ClientSettings clientSettings, CacheSettings cacheSettings)
        {
            if (clientSettings == null)
            {
                throw new ArgumentNullException("clientSettings");
            }

            CacheSettings incomingCacheSettings = new CacheSettings(cacheSettings) { Name = "AnchorCache.incoming" };
            CacheSettings outgoingCacheSettings = new CacheSettings(cacheSettings) { Name = "AnchorCache.outgoing" };

            m_incomingResolver = new CertificateResolver(new AnchorIndex(clientSettings, true), incomingCacheSettings);
            m_outgoingResolver = new CertificateResolver(new AnchorIndex(clientSettings, false), outgoingCacheSettings);
        }

        /// <summary>
        /// If true, will NEVER look for address specific certificates
        /// False by default.
        /// 
        /// Use this if you are never going to issue or store user specific certificates. 
        /// This will eliminate 1 roundtrip to the anchor store for every message. 
        /// 
        /// You should only use this setting for your own private keys and anchors. 
        ///
        /// </summary>
        public bool OrgCertificatesOnly
        {
            get { return m_orgCertsOnly;}
            set
            {
                m_incomingResolver.OrgCertificatesOnly = value;
                m_outgoingResolver.OrgCertificatesOnly = value;
                m_orgCertsOnly = value;
            }
        }
        
        public ICertificateResolver IncomingAnchors
        {
            get 
            { 
                return m_incomingResolver;
            }
        }

        public ICertificateResolver OutgoingAnchors
        {
            get 
            { 
                return m_outgoingResolver;
            }
        }        

        internal class AnchorIndex : IX509CertificateIndex
        {
            ClientSettings m_clientSettings;
            bool m_incoming;
            
            internal AnchorIndex(ClientSettings clientSettings, bool incoming)
            {
                m_clientSettings = clientSettings;
                m_incoming = incoming;
            }

            public X509Certificate2Collection this[string subjectName]
            {
                get 
                {
                    X509Certificate2Collection matches;
                    using(AnchorStoreClient client = this.CreateClient())
                    {
                        if (m_incoming)
                        {
                            matches = client.GetIncomingAnchorX509Certificates(subjectName, EntityStatus.Enabled);
                        }
                        else
                        {
                            matches = client.GetOutgoingAnchorX509Certificates(subjectName, EntityStatus.Enabled);
                        }
                    }
                   
                    return matches;
                }
            }

            AnchorStoreClient CreateClient()
            {
                return new AnchorStoreClient(m_clientSettings.Binding, m_clientSettings.Endpoint);
            }
        }
    }
}