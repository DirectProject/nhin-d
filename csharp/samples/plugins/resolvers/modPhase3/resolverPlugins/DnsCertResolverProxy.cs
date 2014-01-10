/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Container;

namespace Health.Direct.ModSpec3.ResolverPlugins
{
    /// <summary>
    /// This plugin resolver actually loads a DnsCertResolver... and proxies calls to it (See Init method)
    /// Example of how you could use plugin resolvers to build "layered" certificate resolution...
    /// Including Adding custom caching and other behavior...
    /// </summary>
    public class DnsCertResolverProxy : ICertificateResolver, IPlugin
    {
        ICertificateResolver m_innerResolver;

        /// <summary>
        /// Required default constructor to be activated as a plugin.
        /// </summary>
        public DnsCertResolverProxy()
        {
        }

        #region ICertificateResolver Members

        /// <summary>
        /// Proxy to the implemented certificate resolver.
        /// </summary>
        /// <param name="address">The <see cref="MailAddress"/> instance to resolve. </param>
        /// <returns>An <see cref="X509Certificate2Collection"/> of X509 certifiates for the address,
        /// or <c>null</c> if no certificates are found.</returns>
        public X509Certificate2Collection GetCertificates(MailAddress address)
        {
            return m_innerResolver.GetCertificates(address);
        }

        /// <summary>
        /// Proxy to the implemented certificate resolver.
        /// </summary>
        /// <param name="domain">The domain for which certificates should be resolved.</param>
        /// <returns>An <see cref="X509Certificate2Collection"/> of X509 certifiates for the address,
        /// or <c>null</c> if no certificates are found.</returns>
        public X509Certificate2Collection GetCertificatesForDomain(string domain)
        {
            return m_innerResolver.GetCertificatesForDomain(domain);
        }

        /// <summary>
        /// Event to subscribe to for notification of errors.
        /// </summary>
        public event Action<ICertificateResolver, Exception> Error { 
            add
            {
                CertificateResolverCollection resolvers = m_innerResolver as CertificateResolverCollection;
                if(resolvers == null)
                {
                    m_innerResolver.Error += value;
                    return;
                }
                // BackupServerIP adds a internal resolver. 
                foreach (var resolver in resolvers)
                {
                    resolver.Error += value;
                }
            } 
            remove
            {
                CertificateResolverCollection resolvers = m_innerResolver as CertificateResolverCollection;
                if (resolvers == null)
                {
                    m_innerResolver.Error -= value;
                    return;
                }
                // BackupServerIP adds a internal resolver. 
                foreach (var resolver in resolvers)
                {
                    resolver.Error -= value;
                }
            }
        }

        #endregion

        #region IPlugin Members

        /// <summary>
        /// Plugin Resolver factory method.
        /// Set injected settings and load actual resolver. 
        /// </summary>
        /// <param name="pluginDef"></param>
        public void Init(PluginDefinition pluginDef)
        {
            var settings = pluginDef.DeserializeSettings<DnsCertResolverSettings>();
            m_innerResolver = settings.CreateResolver();
        }

        #endregion
    }
}
