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
using Health.Direct.Agent;
using Health.Direct.Agent.Config;
using Health.Direct.Common.Certificates;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.ResolverPlugins.Tests
{
   

    public class LdapResolverTests
    {
        #region data

        
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
                    <PluginResolver>
                        <Definition>
                            <TypeName>Health.Direct.ResolverPlugins.Tests.Fakes.DnsFakeResolver, Health.Direct.ResolverPlugins.Tests</TypeName>
                            <Settings> 
                               <ServerIP>0.0.0.0</ServerIP>
                            </Settings>
                        </Definition>
                    </PluginResolver>
                    <PluginResolver>
                        <Definition>
                            <TypeName>Health.Direct.ResolverPlugins.LdapCertResolverProxy, Health.Direct.ResolverPlugins</TypeName>
                            <Settings> 
                                <!--<ServerIP>10.110.1.11</ServerIP>--> <!-- Windows Dns Server -->
                                <!--<ServerIP>184.72.234.183</ServerIP>-->
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


        public const string TestXmlBackupServerIP = @"
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
                    <PluginResolver>
                        <Definition>
                            <TypeName>Health.Direct.ResolverPlugins.Tests.Fakes.DnsFakeResolver, Health.Direct.ResolverPlugins.Tests</TypeName>
                            <Settings> 
                               <ServerIP>0.0.0.0</ServerIP>
                            </Settings>
                        </Definition>
                    </PluginResolver>
                    <PluginResolver>
                        <Definition>
                            <TypeName>Health.Direct.ResolverPlugins.LdapCertResolverProxy, Health.Direct.ResolverPlugins</TypeName>
                            <Settings> 
                                <ServerIP>0.0.0.0</ServerIP> <!-- Windows Dns Server -->
                                <BackupServerIP>8.8.8.8</BackupServerIP>
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


        [Theory]//(Skip = "Requires SRV Lookup and LDAP server running on returned port.")]
        [InlineData("direct.securehealthemail.com")]
        public void TestLdapCertResolverPlugin(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver pluginResolver = LocateChild<LdapCertResolverProxy>(agent.PublicCertResolver);
            Assert.NotNull(pluginResolver);

            X509Certificate2Collection certs = pluginResolver.GetCertificatesForDomain(subject);
            Assert.NotNull(certs);
            Assert.True(certs.Count > 0);
        }

        [Theory]//(Skip = "Requires SRV Lookup and LDAP server running on returned port.")]
        [InlineData("gm2552@direct.securehealthemail.com")]
        public void TestDnsFallbackToLdapCertResolverPlugin(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver pluginResolver = agent.PublicCertResolver;
            Assert.NotNull(pluginResolver);


            X509Certificate2Collection certs = pluginResolver.GetCertificates(new MailAddress(subject));
            Assert.NotNull(certs);
            Assert.True(certs.Count > 0);
        }


        [Theory]//(Skip = "Requires SRV Lookup and LDAP server running on returned port.")]
        [InlineData("gm2552@direct.securehealthemail.com")]
        public void TestDnsFallbackToLdapCertResolverBackupIPPlugin(string subject)
        {
            // System.Diagnostics.Debugger.Break();

            AgentSettings settings = AgentSettings.Load(TestXmlBackupServerIP);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver pluginResolver = agent.PublicCertResolver;
            Assert.NotNull(pluginResolver);

            X509Certificate2Collection certs = pluginResolver.GetCertificates(new MailAddress(subject));
            Assert.NotNull(certs);
            Assert.True(certs.Count > 0);
        }

        ICertificateResolver LocateChild<T>(ICertificateResolver resolver)
        {
            var resolvers = (CertificateResolverCollection)resolver;
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
