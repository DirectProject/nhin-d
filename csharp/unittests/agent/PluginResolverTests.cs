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
using System.Security.Cryptography.X509Certificates;
using System.Net.Mail;
using System.Xml;
using System.Xml.Serialization;
using Health.Direct.Agent.Config;
using Health.Direct.Common.Container;
using Health.Direct.Common.Certificates;
using Xunit;

namespace Health.Direct.Agent.Tests
{
    /// <summary>
    /// This plugin resolver actually loads a MachineCertResolver... and proxies calls to it (See Init method)
    /// Example of how you could use plugin resolvers to build "layered" certificate resolution...
    /// Including Adding custom caching and other behavior...
    /// </summary>
    public class MachineResolverProxy : ICertificateResolver, IPlugin
    {
        ICertificateResolver m_innerResolver;

        public MachineResolverProxy()
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

        public event Action<ICertificateResolver, Exception> Error
        {
            add
            {
                m_innerResolver.Error += value;
            }
            remove
            {
                m_innerResolver.Error -= value;
            }
        }

        
        #endregion

        #region IPlugin Members

        public void Init(PluginDefinition pluginDef)
        {
            MachineCertResolverSettings settings = pluginDef.DeserializeSettings<MachineCertResolverSettings>();
            m_innerResolver = settings.CreateResolver();
        }

        #endregion
    }

    /// <summary>
    /// This plugin resolver is a "proxy" over the existing MachineAnchorResolver
    /// Demonstrates how you could "layer" anchor resolvers...
    /// </summary>
    public class MachineAnchorResolverProxy : ITrustAnchorResolver, IPlugin
    {
        ITrustAnchorResolver m_inner;

        public MachineAnchorResolverProxy()
        {
        }

        #region ITrustAnchorResolver Members

        public ICertificateResolver OutgoingAnchors
        {
            get { return m_inner.OutgoingAnchors; }
        }

        public ICertificateResolver IncomingAnchors
        {
            get { return m_inner.IncomingAnchors; }
        }

        #endregion

        #region IPlugin Members

        public void Init(PluginDefinition pluginDef)
        {
            MachineAnchorResolverSettings settings = pluginDef.DeserializeSettings<MachineAnchorResolverSettings>();
            m_inner = settings.CreateResolver();
        }

        #endregion
    }

    public class PluginResolverTests
    {
        public const string TestXml = @"
            <AgentSettings>
                <Domain>exampledomain.com</Domain>
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
                    <DnsResolver>
                        <ServerIP>8.8.8.8</ServerIP>
                        <Timeout>5000</Timeout>
                    </DnsResolver>
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

        public const string TestXmlFailCert = @"
            <AgentSettings>
                <Domain>exampledomain.com</Domain>
                <PrivateCerts>
                    <PluginResolver>
                        <Definition>
                            <TypeName></TypeName>
                            <Settings>
                                <Name>NHINDPrivate</Name>
                            </Settings>
                        </Definition>
                    </PluginResolver>
                </PrivateCerts>
                <PublicCerts>
                    <DnsResolver>
                        <ServerIP>8.8.8.8</ServerIP>
                        <Timeout>5000</Timeout>
                    </DnsResolver>
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
                    <MachineResolver>
                        <Incoming>
                            <Name>NHINDAnchors</Name>
                        </Incoming>
                        <Outgoing>
                            <Name>NHINDAnchors</Name>
                        </Outgoing>
                    </MachineResolver>
                </Anchors>
            </AgentSettings>
        ";

        public const string TestXmlFailAnchor = @"
            <AgentSettings>
                <Domain>exampledomain.com</Domain>
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
                    <DnsResolver>
                        <ServerIP>8.8.8.8</ServerIP>
                        <Timeout>5000</Timeout>
                    </DnsResolver>
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
                        <TypeName></TypeName>
                        <Definition>
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

        [Fact]
        public void TestLoadFail()
        {
            try
            {
                AgentSettings settings = AgentSettings.Load(TestXmlFailCert);
                settings.Validate();
            }
            catch (AgentConfigException ex)
            {
                Assert.True(ex.Error == AgentConfigError.MissingPluginResolverType);
            }

            try
            {
                AgentSettings settings = AgentSettings.Load(TestXmlFailAnchor);
                settings.Validate();
            }
            catch (AgentConfigException ex)
            {
                Assert.True(ex.Error == AgentConfigError.MissingPluginAnchorResolverType);
            }
        }

        [Fact]
        public void TestCertResolverPlugin()
        {
            AgentSettings settings = AgentSettings.Load(TestXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver pluginResolver = LocateChild<MachineResolverProxy>(agent.PrivateCertResolver);
            Assert.NotNull(pluginResolver);

            X509Certificate2Collection certs = pluginResolver.GetCertificatesForDomain("nhind.hsgincubator.com");
            Assert.NotNull(certs);
            Assert.True(certs.Count > 0);
        }

        [Fact]
        public void TestAnchorResolverPlugin()
        {
            AgentSettings settings = AgentSettings.Load(TestXml);
            DirectAgent agent = settings.CreateAgent();

            Assert.NotNull(agent.TrustAnchors);
            Assert.True(agent.TrustAnchors is MachineAnchorResolverProxy);
            
            X509Certificate2Collection certs = agent.TrustAnchors.IncomingAnchors.GetCertificatesForDomain("nhind.hsgincubator.com");
            Assert.NotNull(certs);
            Assert.True(certs.Count > 0);
        }

        ICertificateResolver LocateChild<T>(ICertificateResolver resolver)
        {
            CertificateResolverCollection resolvers = (CertificateResolverCollection)resolver;
            for (int i = 0; i < resolvers.Count; ++i)
            {
                if (resolvers[i] is T)
                {
                    return resolvers[i];
                }
            }

            return null;
        }
    }
}
