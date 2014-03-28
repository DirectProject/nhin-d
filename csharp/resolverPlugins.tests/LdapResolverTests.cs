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
using System.Net;
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
        //const string Dns_Server = "184.73.237.102";
        //const string Dns_Server = "10.110.22.16";
        //const string Dns_Sertver = "207.170.210.162";
        const string Dns_Server = "8.8.8.8";
        

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


        public static readonly string TestRealResolversXml = string.Format(@"
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
                      <ServerIP>{0}</ServerIP>
                      <Timeout>5000</Timeout>
                    </DnsResolver>
                    <PluginResolver>
                        <Definition>
                            <TypeName>Health.Direct.ResolverPlugins.LdapCertResolverProxy, Health.Direct.ResolverPlugins</TypeName>
                            <Settings> 
                                <ServerIP>{0}</ServerIP>
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
            ", Dns_Server);

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


        [Theory(Skip = "Requires SRV Lookup and LDAP server running on returned port.")]
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

        [Theory(Skip = "Requires SRV Lookup and LDAP server running on returned port.")]
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


        [Theory(Skip = "Requires SRV Lookup and LDAP server running on returned port.")]
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


        /// <summary>
        /// Purpose/ Description:
        /// Query DNS for X.509 individual Direct address-bound certificate where rfc822name is populated in the certificate. 
        /// Target Certificate: 
        /// A valid address-bound DNS certificate for the Direct address.
        /// Background Certificates: 
        /// A valid domain-bound certificate for the Direct address in a DNS CERT Record. Valid address-bound and domain-bound certificates for the Direct address in an LDAP server with associated SRV record.
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("dts500@direct1.demo.direct-test.com")]
        public void Test500(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = agent.PublicCertResolver;
            Assert.NotNull(resolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = resolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal(subject, certs[0].ExtractEmailNameOrName());
            AssertCert(certs[0], true);

            //
            // Now prove the standard dns resolver will also return the Address Cert.
            //
            resolver = new Common.Certificates.DnsCertResolver(IPAddress.Parse(Dns_Server));
            Assert.NotNull(resolver);

            certs = resolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal(subject, certs[0].ExtractEmailNameOrName());

            AssertCert(certs[0], true);

        }


        /// <summary>
        /// Purpose/ Description:
        /// Query DNS for X.509 Direct domain-bound certificate where the dNSName is populated in the certificate. 
        /// Target Certificate: 
        /// A valid domain-bound certificate for the Direct address in a DNS CERT record. 
        /// Background Certificate: 
        /// An invalid address-bound certificate in a DNS record. Valid address-bound and domain-bound certificates in an LDAP server with associated SRV Record.
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("dts501@direct1.demo.direct-test.com")]
        public void Test501(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = agent.PublicCertResolver;

            var dnsCertResolver = LocateChild<DnsCertResolver>(resolver);
            var diagnostics = new FakeDiagnostics(typeof(DnsCertResolver));
            dnsCertResolver.Error += diagnostics.OnResolverError;

            Assert.NotNull(resolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = resolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal("direct1.demo.direct-test.com", certs[0].ExtractEmailNameOrName());
            AssertCert(certs[0], true);


            //
            // Now prove we can get it from as a domain with no fail over.
            //
            certs = resolver.GetCertificatesForDomain(email.Host);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal("direct1.demo.direct-test.com", certs[0].ExtractEmailNameOrName());

            AssertCert(certs[0], true);

        }




        /// <summary>
        /// Target Certificate: 
        ///A valid address-bound certificate that is larger than 512 bytes in a DNS CERT record for the Direct address.
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("dts502@direct1.demo.direct-test.com")]
        public void Test502(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = agent.PublicCertResolver;
            Assert.NotNull(resolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = resolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal(subject, certs[0].ExtractEmailNameOrName());

            AssertCert(certs[0], true);

        }

        /// <summary>
        /// Purpose/ Description:
        /// Query DNS for LDAP SRV Resource Record and query LDAP for X.509 Cert that is bound to the rfc822name in the certificate. 
        /// Target Certificate: 
        /// A valid address-bound certificate in an LDAP server with the appropriate mail attribute and InetOrgPerson Schema. An SRV Record points to the LDAP instance.
        /// Background Certificate: 
        /// Expired certificates in DNS CERT address-bound and domain-bound resource records for the Direct address. A valid domain-bound certificate in an LDAP server with associated SRV Record.
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("dts505@direct2.demo.direct-test.com")]
        public void Test505(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = agent.PublicCertResolver;
            Assert.NotNull(resolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = resolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal(subject, certs[0].ExtractEmailNameOrName());

            AssertCert(certs[0], true);
        }

        

        /// <summary>
        /// Purpose/ Description:
        /// Query for Direct address from LDAP servers based on priority value. 
        /// Target Certificate: 
        /// A valid address-bound certificate in an LDAP server with the appropriate mail attribute and InetOrgPerson Schema. The associated SRV record has Priority = 0 and Weight = 0
        /// Background Certificate: 
        /// A valid address-bound certificate in an LDAP server with the appropriate mail attribute and InetOrgPerson Schema. The associated SRV has Priority = 1 and Weight = 0
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("dts506@direct2.demo.direct-test.com")]
        public void Test506(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();
            var email = new MailAddress(subject);
                 
            ICertificateResolver resolver = agent.PublicCertResolver;
            Assert.NotNull(resolver);


            X509Certificate2Collection certs = resolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal(subject, certs[0].ExtractEmailNameOrName());

            AssertCert(certs[0], true);
        }

        


        /// <summary>
        /// Purpose/ Description:
        /// Query for Direct address from LDAP servers based on priority value - One LDAP instance unavailable. 
        /// Target Certificate: 
        /// A valid address-bound certificate in an LDAP server with the appropriate mail attribute and InetOrgPerson Schema. The associated SRV has Priority = 1 and Weight = 0
        /// Background Certificate: 
        /// A valid address-bound certificate in an LDAP server with the appropriate mail attribute and InetOrgPerson Schema. The associated SRV Record points to an LDAP instance that is offline and not available. Its Priority = 0 and Weight = 0
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("dts507@direct3.demo.direct-test.com")]
        public void Test507(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = agent.PublicCertResolver;
            Assert.NotNull(resolver);

            var dnsCertResolver = LocateChild<DnsCertResolver>(resolver);
            var diagnosticsForDnsCertResolver = new FakeDiagnostics(typeof(DnsCertResolver));
            dnsCertResolver.Error += diagnosticsForDnsCertResolver.OnResolverError;

            var ldapCertResolver = LocateChild<LdapCertResolverProxy>(resolver);
            var diagnosticsForLdapCertResolver = new FakeDiagnostics(typeof(LdapCertResolver));
            ldapCertResolver.Error += diagnosticsForLdapCertResolver.OnResolverError;


            var email = new MailAddress(subject);
            X509Certificate2Collection certs = resolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.Equal(1, certs.Count);
            Assert.Equal(subject, certs[0].ExtractEmailNameOrName());

            AssertCert(certs[0], true);

            Console.WriteLine("DnsCertResolver Notifications:");
            foreach (var actualErrorMessage in diagnosticsForDnsCertResolver.ActualErrorMessages)
            {
                Console.WriteLine(actualErrorMessage);
            }

            Console.WriteLine("LDAPCertResolver Notifications:");
            foreach (var actualErrorMessage in diagnosticsForLdapCertResolver.ActualErrorMessages)
            {
                Console.WriteLine(actualErrorMessage);
            }

        }

        /// <summary>
        /// Purpose/ Description:
        /// Query LDAP server for domain-bound certificate. 
        /// Target Certificate: 
        /// A valid domain-bound certificate in an LDAP server with the appropriate mail attribute and InetOrgPerson Schema. An SRV Record points to the LDAP instance.
        /// Background Certificate: 
        /// Expired certificates in DNS CERT address-bound and domain-bound resource records for a Direct address. An expired address-bound certificate 
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("dts515@direct2.demo.direct-test.com")]
        public void Test515(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = agent.PublicCertResolver;
            Assert.NotNull(resolver);


            var dnsCertResolver = LocateChild<DnsCertResolver>(resolver);
            var diagnosticsForDnsCertResolver = new FakeDiagnostics(typeof(DnsCertResolver));
            dnsCertResolver.Error += diagnosticsForDnsCertResolver.OnResolverError;

            var ldapCertResolver = LocateChild<LdapCertResolverProxy>(resolver);
            var diagnosticsForLdapCertResolver = new FakeDiagnostics(typeof(LdapCertResolver));
            ldapCertResolver.Error += diagnosticsForLdapCertResolver.OnResolverError;

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = resolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);

            Assert.Equal(0, diagnosticsForDnsCertResolver.ActualErrorMessages.Count);
            Assert.Equal(0, diagnosticsForLdapCertResolver.ActualErrorMessages.Count);

            Assert.Equal("direct2.demo.direct-test.com", certs[0].ExtractEmailNameOrName());
            AssertCert(certs[0], true);


        }


       

        /// <summary>
        ///  Purpose/Description:
        ///     Query for Direct address from LDAP servers based on priority value - one LDAP instance contains a matching entry 
        ///     with no userCertificate attribute.
        ///  Target Certificate:
        ///     A valid address-bound certificate in an LDAP server with the appropriate mail attribute and InetOrgPerson Schema. 
        ///     The associated SRV Record has its Priority = 1 and Weight = 0
        ///  Background Certificates:
        ///     An LDAP Entry with the appropriate mail attribute in the InetOrgPerson Schema, but no userCertificate attribute. 
        ///     The associated SRV Record has its Priority = 1 and Weight = 0
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("dts517@direct3.demo.direct-test.com")]
        public void Test517(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = agent.PublicCertResolver;
            Assert.NotNull(resolver);

            var dnsCertResolver = LocateChild<DnsCertResolver>(resolver);
            var diagnosticsForDnsCertResolver = new FakeDiagnostics(typeof(DnsCertResolver));
            dnsCertResolver.Error += diagnosticsForDnsCertResolver.OnResolverError;

            var ldapCertResolver = LocateChild<LdapCertResolverProxy>(resolver);
            var diagnosticsForLdapCertResolver = new FakeDiagnostics(typeof(LdapCertResolver));
            ldapCertResolver.Error += diagnosticsForLdapCertResolver.OnResolverError;


            var email = new MailAddress(subject);
            X509Certificate2Collection certs = resolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal(subject, certs[0].ExtractEmailNameOrName());

            AssertCert(certs[0], true);

            Assert.Equal(0, diagnosticsForDnsCertResolver.ActualErrorMessages.Count);

            Assert.Equal(2, diagnosticsForLdapCertResolver.ActualErrorMessages.Count);
            Assert.Equal("Error=BindFailure\r\n_ldap._tcp.direct3.demo.direct-test.com:389 Priority:0 Weight:0", diagnosticsForLdapCertResolver.ActualErrorMessages[0]);
            // Assert.Equal("Error=BindFailure\r\n_ldap._tcp.direct3.direct-test.com:389 Priority:0 Weight:0", diagnosticsForLdapCertResolver.ActualErrorMessages[1]);
            
            //This is
            Assert.Equal("Error=NoUserCertificateAttribute\r\ndts517@direct3.demo.direct-test.com SRV:_ldap._tcp.direct3.demo.direct-test.com:10389 Priority:1 Weight:0 LDAP:cn=dts517_no_cert,ou=system", diagnosticsForLdapCertResolver.ActualErrorMessages[1]);


            //
            // OK now lets just use the LDAP resolver because I don't really know that 
            // we fall back to LDAP with above test.
            //

            resolver = LocateChild<LdapCertResolverProxy>(agent.PublicCertResolver);
            Assert.NotNull(resolver);

            email = new MailAddress(subject);
            certs = resolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal(subject, certs[0].ExtractEmailNameOrName());

            AssertCert(certs[0], true);
        }

        /// <summary>
        /// Purpose/ Description:
        /// No valid Certificate found in DNS CERT or LDAP instance. - 
        /// Additional Info: 
        /// In order for this test case to be a success, you must NOT receive an email in response. You will need to verify that your system did NOT send an email because it could not find a certificate for the Direct address. 
        /// Target Certificate: 
        /// None
        /// Background Certificate: 
        /// Invalid address-bound and domain-bound certificates in CERT records for the Direct address. An SRV record points to the LDAP server and is populated with invalid address-bound and domain-bound certificates for the Direct address and domain.
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("dts520@direct5.demo.direct-test.com")]
        public void Test520(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = agent.PublicCertResolver;
            Assert.NotNull(resolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = resolver.GetCertificates(email);

            Assert.True(certs.Count == 1);
            AssertCert(certs[0], false);
        }

        /// <summary>
        /// Purpose/ Description:
        /// No certificate found in DNS CERT or LDAP instance. 
        /// Additional Info: 
        /// In order for this test case to be a success, you must NOT receive an email in response. You will need to verify that your system did NOT send an email because it could not find a certificate for the Direct address.
        /// Target Certificate: 
        /// None
        /// Background Certificate: 
        /// None
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("dts511@direct4.demo.direct-test.com")]
        public void Test511(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = agent.PublicCertResolver;
            Assert.NotNull(resolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = resolver.GetCertificates(email);
            Assert.Null(certs);
        }

        /// <summary>
        /// Purpose/ Description:
        /// No certificate found in DNS CERT and no SRV records 
        /// Additional Info: 
        /// In order for this test case to be a success, you must NOT receive an email in response. You will need to verify that your system did NOT send an email because it could not find a certificate for the Direct address.
        /// Target Certificate: 
        /// None
        /// Background Certificate: 
        /// Invalid address-bound and domain-bound certificates in DNS CERT records for the Direct address.
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("dts512@direct6.demo.direct-test.com")]
        public void Test512(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = agent.PublicCertResolver;
            Assert.NotNull(resolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = resolver.GetCertificates(email);
            AssertCert(certs[0], false);
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
