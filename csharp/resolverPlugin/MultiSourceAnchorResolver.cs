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
using System.Security.Cryptography.X509Certificates;
using System.Net.Mail;
using System.Linq;
using System.Text;
using Health.Direct.Agent;
using Health.Direct.Agent.Config;
using Health.Direct.Common;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Container;
using Health.Direct.SmtpAgent;

namespace Health.Direct.ResolverPlugins
{
    /// <summary>
    /// An Anchor Resolver that can incorporate multiple child resolvers
    /// </summary>
    public class MultiSourceAnchorResolver : ITrustAnchorResolver, IPlugin
    {
        AnchorCertificateResolvers m_inbound;
        AnchorCertificateResolvers m_outbound;

        /// <summary>
        /// Construct a new AnchorResolverPipeline
        /// </summary>
        public MultiSourceAnchorResolver()
        {
        }
        
        /// <summary>
        /// For initialization as a plugin
        /// </summary>
        /// <param name="pluginDef">Plugin definition</param>
        public void Init(PluginDefinition pluginDef)
        {
            MultiSourceAnchorResolverSettings settings = pluginDef.DeserializeSettings<MultiSourceAnchorResolverSettings>();
            settings.Validate();
            this.Init(settings);
        }

        /// <summary>
        /// Initialize with settings
        /// </summary>
        /// <param name="settings">pipeline settings</param>
        public void Init(MultiSourceAnchorResolverSettings settings)
        {
            if (settings == null)
            {
                throw new ArgumentNullException("settings");
            }

            //
            // We will combine the individual certificate resolvers for each TrustAnchorResolver 
            //
            m_inbound = new AnchorCertificateResolvers();
            m_outbound = new AnchorCertificateResolvers();

            foreach (TrustAnchorResolverSettings anchorSettings in settings.Resolvers)
            {
                ITrustAnchorResolver resolver = anchorSettings.CreateResolver();
                if (resolver.IncomingAnchors != null)
                {
                    m_inbound.Add(resolver.IncomingAnchors);
                }
                if (resolver.OutgoingAnchors != null)
                {
                    m_outbound.Add(resolver.OutgoingAnchors);
                }
            }
        }

        /// <summary>
        /// Certificate resolver to resolve anchors for outgoing messages
        /// </summary>
        public ICertificateResolver OutgoingAnchors
        {
            get 
            {
                if (m_outbound == null)
                {
                    m_outbound = new AnchorCertificateResolvers();
                }
                return m_outbound;
            }
        }

        /// <summary>
        /// Certificate resolver to resolve anchors for incoming messages
        /// </summary>
        public ICertificateResolver IncomingAnchors
        {
            get 
            {
                if (m_inbound == null)
                {
                    m_inbound = new AnchorCertificateResolvers();
                }
                return m_inbound;
            }
        }

        /// <summary>
        /// This will walk over EACH configured resolver and call it, asking for certificates
        /// </summary>
        internal class AnchorCertificateResolvers : CertificateResolverCollection
        {
            public override X509Certificate2Collection GetCertificates(MailAddress address)
            {
                X509Certificate2Collection matches = new X509Certificate2Collection();
                foreach (ICertificateResolver resolver in this)
                {
                    try
                    {
                        X509Certificate2Collection certs = resolver.GetCertificates(address);
                        if (!certs.IsNullOrEmpty())
                        {
                            matches.Add(certs);
                        }
                    }
                    catch (Exception ex)
                    {
                        this.NotifyException(resolver, ex);
                    }
                }

                return matches;
            }

            public override X509Certificate2Collection GetCertificatesForDomain(string domain)
            {
                X509Certificate2Collection matches = new X509Certificate2Collection();
                foreach (ICertificateResolver resolver in this)
                {
                    try
                    {
                        X509Certificate2Collection certs = resolver.GetCertificatesForDomain(domain);
                        if (!certs.IsNullOrEmpty())
                        {
                            matches.Add(certs);
                        }
                    }
                    catch (Exception ex)
                    {
                        this.NotifyException(resolver, ex);
                    }
                }

                return matches;
            }
        }
    }
}
