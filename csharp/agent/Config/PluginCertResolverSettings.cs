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
using System.Collections.Generic;
using System.Xml.Serialization;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Container;

namespace Health.Direct.Agent.Config
{
    /// <summary>
    /// A plugin resolver is a CUSTOM certificate resolver. It is arbitrary Type (Assembly + Type) that implements 
    /// ICertificateResolver.
    /// 
    /// You use this object to define the plugin which will be loaded and plugged into the Agent's Certificate
    /// resolution pipeline. 
    /// 
    /// The plugin definition uses Health.Direct.Common.Container.PluginDefinition
    /// If your resolver ALSO implements IPlugin, then you will be called to initialize yourself
    /// with settings (see below)
    /// 
    /// Also see Health.Direct.Agent.Tests.MachineResolverProxy for sample code
    /// </summary>
    /*
        <PluginResolver>
          <Definition>
            <TypeName>Type Name, Assembly Name</TypeName>
            <Settings> 
                <!-- Your OPTIONAL Xml settings.... 
                   If your plugin ALSO implements IPlugin:
                    When your plugin is loaded, we will invoke IPlugin:Init(pluginDefinition)
                    The pluginDefinition object has an XmlNode on it containing your settings
                -->
            </Settings>
          </Definition>
        </PluginResolver>

        *  Example:
        <PrivateCerts>
            <PluginResolver>
                <Definition>
                    <TypeName>Health.Direct.Agent.Tests.MachineResolverProxy, Health.Direct.Agent.Tests</TypeName>
                    <Settings>
                        <Name>NHINDPrivate</Name>
                    </Settings>
                </Definition>
            </PluginResolver>
        </PrivateCerts>
*/
    public class PluginCertResolverSettings : CertResolverSettings
    {
        /// <summary>
        /// Create a new PluginCertResolverSettings
        /// </summary>
        public PluginCertResolverSettings()
        {
        }
        
        /// <summary>
        /// Resolver Type information
        /// <see cref="PluginDefinition"/>
        /// </summary>
        [XmlElement("Definition")]
        public PluginDefinition ResolverDefinition
        {
            get;
            set;
        }
        
        /// <summary>
        /// Validate settings
        /// </summary>
        public override void Validate()
        {
            if (this.ResolverDefinition == null)
            {
                throw new AgentConfigException(AgentConfigError.MissingPluginResolverDefinition);
            }        
            
            if (!this.ResolverDefinition.HasTypeName)
            {
                throw new AgentConfigException(AgentConfigError.MissingPluginResolverType);
            }                
        }
        
        /// <summary>
        /// Create a new plugin resolver from settings
        /// </summary>
        /// <returns>Certificate Resolver <see cref="ICertificateResolver"/></returns>
        public override ICertificateResolver CreateResolver()
        {
            return this.ResolverDefinition.Create<ICertificateResolver>();
        }
    }
}
