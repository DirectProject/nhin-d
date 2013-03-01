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
using System.Xml.Serialization;
using Health.Direct.Agent.Config;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Certificates;
using Health.Direct.Config.Client;
using Health.Direct.Common.Caching;
using Health.Direct.SmtpAgent;

namespace Health.Direct.ResolverPlugins
{
    /// <summary>
    /// Settings used to create an MultiSourceAnchorResolver
    /// </summary>
    [XmlType("MultiSourceAnchorResolverSettings")]
    public class MultiSourceAnchorResolverSettings : TrustAnchorResolverSettings
    {
        /// <summary>
        /// Constructor
        /// </summary>
        public MultiSourceAnchorResolverSettings()
        {
        }

        /// <summary>
        /// Child resolvers
        /// </summary>
        [XmlElement("BundleResolver", typeof(BundleResolverSettings))]
        [XmlElement("ServiceResolver", typeof(AnchorServiceResolverSettings))]
        [XmlElement("MachineResolver", typeof(MachineAnchorResolverSettings))]
        [XmlElement("PluginResolver", typeof(PluginAnchorResolverSettings))]
        public TrustAnchorResolverSettings[] Resolvers
        {
            get;
            set;
        }

        /// <summary>
        /// Validate settings
        /// </summary>
        public override void Validate()
        {
            if (this.Resolvers.IsNullOrEmpty())
            {
                throw new AgentConfigException(AgentConfigError.MissingAnchorResolverSettings);
            }

            foreach (TrustAnchorResolverSettings settings in this.Resolvers)
            {
                if (settings == null)
                {
                    throw new AgentConfigException(AgentConfigError.MissingAnchorResolverSettings);
                }
                settings.Validate();
            }
        }

        /// <summary>
        /// Create new resolver
        /// </summary>
        /// <returns>New resolver</returns>
        public override ITrustAnchorResolver CreateResolver()
        {
            MultiSourceAnchorResolver resolver = new MultiSourceAnchorResolver();
            resolver.Init(this);
            return resolver;
        }
    }
}
