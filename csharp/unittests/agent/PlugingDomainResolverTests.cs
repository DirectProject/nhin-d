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
using System.Linq;
using System.Net.Mail;
using System.Xml.Serialization;
using Health.Direct.Agent.Config;
using Health.Direct.Common.Container;
using Health.Direct.Common.Domains;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Mail;
using Xunit;

namespace Health.Direct.Agent.Tests
{
    /// <summary>
    /// This plugin resolver actually loads a MachineCertResolver... and proxies calls to it (See Init method)
    /// Example of how you could use plugin resolvers to build "layered" certificate resolution...
    /// Including Adding custom caching and other behavior...
    /// </summary>
    public class CustomDomainResolverProxy : IDomainResolver, IPlugin
    {
        IDomainResolver m_innerResolver;

        public CustomDomainResolverProxy()
        {
        }

        public IEnumerable<string> Domains
        {
            get { return m_innerResolver.Domains; }
        }

        public bool IsManaged(string domain)
        {
            return m_innerResolver.IsManaged(domain);
        }

        public bool HsmEnabled(string address)
        {
            return false;
        }

        public bool Validate(string[] domains)
        {
            return m_innerResolver.Validate(domains);
        }


        #region IPlugin Members

        public void Init(PluginDefinition pluginDef)
        {
            var settings = pluginDef.DeserializeSettings<CustomStaticDomainResolverSettings>();
            m_innerResolver = settings.CreateResolver();
        }

        #endregion


    }


    /// <summary>
    /// Test Configuration for a plugin domain resolver.
    /// </summary>
    [XmlType("StaticDomainStore")]
    public class CustomStaticDomainResolverSettings : DomainResolverSettings
    {

        /// <summary>
        /// The domains the agent manages
        /// </summary>
        [XmlElement("Domain", typeof(string))]
        public string[] Domains
        {
            get;
            set;
        }

        /// <summary>
        /// Validates the configuration settings.
        /// </summary>
        public override void Validate()
        {
            if (Domains.IsNullOrEmpty())
            {
                throw new AgentConfigException(AgentConfigError.InvalidDomainList);
            }
        }

        public override IDomainResolver CreateResolver()
        {
            this.Validate();

            return new PluggedStaticDomainResolver(Domains);

        }
    }

    public class PluggedStaticDomainResolver : IDomainResolver
    {
        private Dictionary<string, string> m_domains = new Dictionary<string, string>(MailStandard.Comparer);


        public PluggedStaticDomainResolver(string[] domains)
        {
            foreach (var domain in domains)
            {
                m_domains.Add(domain, domain);
            }
        }

        public IEnumerable<string> Domains
        {
            get { return m_domains.Keys; }
        }

        public bool IsManaged(string domain)
        {
            if (string.IsNullOrEmpty(domain))
            {
                throw new ArgumentException("value was null or empty", "domain");
            }

            return m_domains.ContainsKey(domain);
        }

        public bool HsmEnabled(string address)
        {
            return false;
        }

        public bool Validate(string[] domains)
        {
            if (domains == null || domains.Length == 0)
            {
                return false;
            }

            for (int i = 0; i < domains.Length; ++i)
            {
                string domain = domains[i];
                if (string.IsNullOrEmpty(domain))
                {
                    return false;
                }
            }

            return true;
        }
    }

    public class PluggingDomainResolverTests
    {
        #region data

        public const string TestXml = @"
            <AgentSettings>
                 <Domains>
                    <PluginResolver>
                        <Definition>
                            <TypeName>Health.Direct.Agent.Tests.CustomDomainResolverProxy, Health.Direct.Agent.Tests</TypeName>
                            <Settings>
                                <Domain>redmond.hsgincubator.com</Domain>
                                <Domain>nhind.hsgincubator.com</Domain>
                                <Domain>test.lab</Domain>
                            </Settings>
                        </Definition>
                    </PluginResolver>
                </Domains>
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
                <PublicCerts>                    
                    <PluginResolver>
                        <Definition>
                            <TypeName>Health.Direct.Agent.Tests.MachineResolverProxy, Health.Direct.Agent.Tests</TypeName>
                            <Settings>
                                <Name>NHINDPrivate</Name>
                            </Settings>
                        </Definition>
                    </PluginResolver>
                </PublicCerts>
                <Anchors>
                    <PluginResolver>
                        <Definition>
                            <TypeName>Health.Direct.Agent.Tests.MachineAnchorResolverProxy, Health.Direct.Agent.Tests</TypeName>
                            <Settings>
                                <Incoming>
                                    <Name>NHINDAnchors</Name>
                                </Incoming>
                                <Outgoing>
                                    <Name>NHINDAnchors</Name>
                                </Outgoing>
                            </Settings>
                        </Definition>
                    </PluginResolver>
                </Anchors>        
            </AgentSettings>
            ";

        public const string TestMissingDomainsXml = @"
            <AgentSettings>
                 
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
                <PublicCerts>                    
                    <PluginResolver>
                        <Definition>
                            <TypeName>Health.Direct.Agent.Tests.MachineResolverProxy, Health.Direct.Agent.Tests</TypeName>
                            <Settings>
                                <Name>NHINDPrivate</Name>
                            </Settings>
                        </Definition>
                    </PluginResolver>
                </PublicCerts>
                <Anchors>
                    <PluginResolver>
                        <Definition>
                            <TypeName>Health.Direct.Agent.Tests.MachineAnchorResolverProxy, Health.Direct.Agent.Tests</TypeName>
                            <Settings>
                                <Incoming>
                                    <Name>NHINDAnchors</Name>
                                </Incoming>
                                <Outgoing>
                                    <Name>NHINDAnchors</Name>
                                </Outgoing>
                            </Settings>
                        </Definition>
                    </PluginResolver>
                </Anchors>        
            </AgentSettings>
            ";

        #endregion

        [Theory]
        [InlineData("redmond.hsgincubator.com", true)]
        [InlineData("nhind.hsgincubator.com", true)]
        [InlineData("test.lab", true)]
        [InlineData("google.com", false)]
        public void TestPluggedDomainResolver(string domain, bool result)
        {
            AgentSettings settings = AgentSettings.Load(TestXml);
            DirectAgent agent = settings.CreateAgent();

            Assert.True(agent.Domains.Domains.Count() > 0);
            Assert.True(agent.Domains.IsManaged(new MailAddress("hobojoe@" + domain)) == result);

        }

        [Fact]
        public void TestMissingPluggedDomainResolver()
        {
            AgentSettings settings = AgentSettings.Load(TestMissingDomainsXml);
            Assert.Throws<ArgumentException>(() => settings.CreateAgent());
        }
    }
}
