/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Xml.Serialization;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Agent.Config
{
    /// <summary>
    /// Settings for agent certificate resolution.
    /// </summary>
    [XmlType("certificateStore")]
    public class CertificateSettings
    {
        /// <summary>
        /// Creates an instance, normally called from one of the <see cref="AgentSettings"/> static factory methods.
        /// </summary>
        public CertificateSettings()
        {
        }
        
        /// <summary>
        /// Settings for the certificate resolver.
        /// </summary>
        [XmlElement("DnsResolver", typeof(DnsCertResolverSettings))]
        [XmlElement("MachineResolver", typeof(MachineCertResolverSettings))]
        [XmlElement("PluginResolver", typeof(PluginCertResolverSettings))]
        public CertResolverSettings[] Resolvers
        {
            get;
            set;
        }
        
        /// <summary>
        /// Validates settings.
        /// </summary>
        public void Validate()
        {
            this.Validate(AgentConfigError.MissingResolver);
        }
        
        internal void Validate(AgentConfigError error)
        {
            if (this.Resolvers.IsNullOrEmpty())
            {
                throw new AgentConfigException(error);
            }
            foreach(CertResolverSettings resolverSettings in this.Resolvers)
            {
                resolverSettings.Validate();
            }
        }
        
        /// <summary>
        /// Create a resolver as defined in the Resolvers setting
        /// </summary>
        /// <returns>The certificate resolver instance corresponding to the settings.</returns>
        public ICertificateResolver CreateResolver()
        {
            if (this.Resolvers == null)
            {
                throw new NotSupportedException();
            }
            
            CertificateResolverCollection resolvers = new CertificateResolverCollection();
            foreach (CertResolverSettings resolverSettings in this.Resolvers)
            {
                resolvers.Add(resolverSettings.CreateResolver());
            }
            
            return resolvers;
        }
    }
}