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

using System;
using System.Collections.Generic;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Agent;
using Health.Direct.Agent.Config;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Extensions;
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


        public const string TestRealResolversXml = @"
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
                            <TypeName>Health.Direct.ResolverPlugins.LdapCertResolverProxy, Health.Direct.ResolverPlugins</TypeName>
                            <Settings> 
                                <ServerIP>8.8.8.8</ServerIP>
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

        /// <summary>
        /// Chain validations status treated as failing trust validation with the certificate.
        /// </summary>
        public static readonly X509ChainStatusFlags DefaultProblemFlags =
            X509ChainStatusFlags.NotTimeValid |
            X509ChainStatusFlags.Revoked |
            X509ChainStatusFlags.NotSignatureValid |
            X509ChainStatusFlags.InvalidBasicConstraints |
            X509ChainStatusFlags.CtlNotTimeValid |
            X509ChainStatusFlags.CtlNotSignatureValid;


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


        [Theory]
        [InlineData("dts517@direct3.direct-test.com")]
        public void Test517(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver pluginResolver = agent.PublicCertResolver;
            Assert.NotNull(pluginResolver);

            var ldapCertResolver = LocateChild<LdapCertResolverProxy>(pluginResolver);
            var diagnosticsForLdapCertResolver = new FakeDiagnostics(typeof(LdapCertResolver));
            ldapCertResolver.Error += diagnosticsForLdapCertResolver.OnResolverError;


            var email = new MailAddress(subject);
            X509Certificate2Collection certs = pluginResolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal("dts517@direct3.direct-test.com", certs[0].ExtractEmailNameOrName());

            AssertCert(certs[0], true);

            Assert.Equal(2, diagnosticsForLdapCertResolver.ActualErrorMessages.Count);
            Assert.Equal("Error=BindFailure\r\n_ldap._tcp.direct3.direct-test.com:389 Priority:0 Weight:0", diagnosticsForLdapCertResolver.ActualErrorMessages[0]);
            Assert.Equal("Error=NoUserCertificateAttribute\r\n_ldap._tcp.direct3.direct-test.com:10389 Priority:1 Weight:0", diagnosticsForLdapCertResolver.ActualErrorMessages[1]);


            //
            // OK now lets just use the LDAP resolver because I don't really know that 
            // we fall back to LDAP with above test.
            //

            pluginResolver = LocateChild<LdapCertResolverProxy>(agent.PublicCertResolver);
            Assert.NotNull(pluginResolver);

            email = new MailAddress(subject);
            certs = pluginResolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal("dts517@direct3.direct-test.com", certs[0].ExtractEmailNameOrName());

            AssertCert(certs[0], true);
        }


        private void AssertCert(X509Certificate2 cert, bool expectValidCert)
        {

            X509Chain chainBuilder = new X509Chain();
            X509ChainPolicy policy = new X509ChainPolicy();
            policy.VerificationFlags = X509VerificationFlags.IgnoreWrongUsage;
            chainBuilder.ChainPolicy = policy;


            chainBuilder.Build(cert);
            X509ChainElementCollection chainElements = chainBuilder.ChainElements;

            // If we don't have a trust chain, then we obviously have a problem...
            Assert.False(chainElements.IsNullOrEmpty(), string.Format("Can't find a trust chain: {0} ", cert.Subject));

            // walk the chain starting at the leaf and see if we hit any issues
            foreach (X509ChainElement chainElement in chainElements)
            {
                if (expectValidCert)
                {
                    AssertChainHasNoProblems(chainElement);
                }
                else
                {
                    AssertChainHasProblems(chainElement);
                }
            }
        }

        private static void AssertChainHasNoProblems(X509ChainElement chainElement)
        {
            X509ChainStatus[] chainElementStatus = chainElement.ChainElementStatus;
            Assert.False(chainElementStatus.IsNullOrEmpty(), "Missing chain status elements.");

            foreach (var chainElementStatu in chainElementStatus)
            {
                Assert.False((chainElementStatu.Status & DefaultProblemFlags) != 0);
            }
        }

        private static void AssertChainHasProblems(X509ChainElement chainElement)
        {
            X509ChainStatus[] chainElementStatus = chainElement.ChainElementStatus;
            Assert.False(chainElementStatus.IsNullOrEmpty(), "Missing chain status elements.");

            foreach (var chainElementStatu in chainElementStatus)
            {
                if ((chainElementStatu.Status & DefaultProblemFlags) != 0)
                {
                    return;  //we expect problems
                }
            }
            Assert.True(false, "Expected chain problems and found none.");
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

    public class FakeDiagnostics
    {
        public bool Called;
        readonly Type m_resolverType;

        public FakeDiagnostics(Type resolverType)
        {
            m_resolverType = resolverType;
        }

        private readonly List<string> _actualErrorMessages = new List<string>();
        public List<string> ActualErrorMessages
        {
            get { return _actualErrorMessages; }
        }

        public void OnResolverError(ICertificateResolver resolver, Exception error)
        {
            Assert.Equal(m_resolverType.Name, resolver.GetType().Name);
            _actualErrorMessages.Add(error.Message);
            //Logger.Error("RESOLVER ERROR {0}, {1}", resolver.GetType().Name, error.Message);
        }
    }
}
