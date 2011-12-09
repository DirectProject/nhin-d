/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Agent.Config;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Container;

namespace Health.Direct.ResolverPlugins
{
    /// <summary>
    /// This plugin resolver actually loads a LdapCertResolver... and proxies calls to it (See Init method)
    /// Example of how you could use plugin resolvers to build "layered" certificate resolution...
    /// Including Adding custom caching and other behavior...
    /// </summary>
    public class LdapCertResolverProxy : ICertificateResolver, IPlugin
    {
        ICertificateResolver m_innerResolver;

        public LdapCertResolverProxy()
        {
        }

        #region ICertificateResolver Members

        public X509Certificate2Collection GetCertificates(MailAddress address)
        {
            return m_innerResolver.GetCertificates(address);
        }

        public X509Certificate2Collection GetCertificatesForDomain(string domain)
        {
            return m_innerResolver.GetCertificatesForDomain(domain);
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
            var settings = pluginDef.DeserializeSettings<LdapCertResolverSettings>();
            m_innerResolver = settings.CreateResolver();
        }

        #endregion
    }
}
