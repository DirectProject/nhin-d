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
using Health.Direct.Common;

namespace Health.Direct.Agent.Config
{
    /// <summary>
    /// Error enumeration for <see cref="AgentConfigException"/>
    /// </summary>
    public enum AgentConfigError
    {
        /// <summary>
        /// Unknown error
        /// </summary>
        Unknown = 0,
        /// <summary>
        /// Domain list misformed or invalid
        /// </summary>
        InvalidDomainList,
        /// <summary>
        /// Missing domain tenancy settings
        /// </summary>
        MissingDomainTenancySettings,
        /// <summary>
        /// Missing domain group
        /// </summary>
        MissingAgentName,
        /// <summary>
        /// Missing private certificate settings
        /// </summary>
        MissingPrivateCertSettings,
        /// <summary>
        /// Missing public certificate settings
        /// </summary>
        MissingPublicCertSettings,
        /// <summary>
        /// Missing trust anchor settings
        /// </summary>
        MissingAnchorSettings,
        /// <summary>
        /// Missing certificate resolver
        /// </summary>
        MissingResolver,
        /// <summary>
        /// Missing a resolver for private certificates
        /// </summary>
        MissingPrivateCertResolver,
        /// <summary>
        /// Missing a resolver for public certificates
        /// </summary>
        MissingPublicCertResolver,
        /// <summary>
        /// Missing a resolver for trust anchors
        /// </summary>
        MissingAnchorResolverSettings,
        /// <summary>
        /// Missing settings for incoming trust anchors
        /// </summary>
        MissingIncomingAnchors,
        /// <summary>
        /// Missing settings for outgoing trust anchors
        /// </summary>
        MissingOutgoingAnchors,
        /// <summary>
        /// Missing the name for a machine level certificate store.
        /// </summary>
        MissingMachineStoreName,
        /// <summary>
        /// Missing the IP address for the DNS server
        /// </summary>
        MissingDnsServerIP,
        /// <summary>
        /// Plugin Resolver defined improperly
        /// </summary>
        MissingPluginResolverDefinition,
        /// <summary>
        /// No typename for plugin resolver
        /// </summary>
        MissingPluginResolverType,
        /// <summary>
        /// Plugin Anchor Resolver defined improperly
        /// </summary>
        MissingPluginAnchorResolverDefinition,
        /// <summary>
        /// No typename for Plugin Anchor resolver
        /// </summary>
        MissingPluginAnchorResolverType
    }

    /// <summary>
    /// Exception for agent configurations
    /// </summary>
    public class AgentConfigException : DirectException<AgentConfigError>
    {
        /// <summary>
        /// Creates an exception for agent configuration errors.
        /// </summary>
        /// <param name="error">The error type</param>
        public AgentConfigException(AgentConfigError error)
            : base(error)
        {
        }
    }
}