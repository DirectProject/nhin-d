/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.Xml.Serialization;
using Health.Direct.Common.Domains;

namespace Health.Direct.Agent.Config
{
    /// <summary>
    /// Settings for Domain tenant selection.
    /// </summary>
    [XmlType("DomainSettings")]
    public class DomainSettings
    {
        /// <summary>
        /// Creates an instance, normally called from one of the <see cref="AgentSettings"/> static factory methods.
        /// </summary>
        public DomainSettings()
        {
        }


        /// <summary>
        /// Settings for the domain tenancy selection.
        /// </summary>
        [XmlElement("PluginResolver", typeof(PluginDomainResolverSettings))]
        public DomainResolverSettings Resolver
        {
            get;
            set;
        }

        /// <summary>
        /// Validates the configuration settings.
        /// </summary>
        public void Validate()
        {
            if (this.Resolver == null)
            {
                throw new AgentConfigException(AgentConfigError.MissingDomainTenancySettings);
            }
            this.Resolver.Validate();
        }

        /// <summary>
        /// Creates the configured domain resolver.
        /// </summary>
        /// <returns>The configured domain resolver.</returns>
        public IDomainResolver CreateResolver()
        {
            return Resolver.CreateResolver();
        }
    }
}
