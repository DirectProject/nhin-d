/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    Joseph.Shook@Surescripts.com

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Mail;
using System.Runtime.InteropServices;
using System.Security.Cryptography.X509Certificates;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;
using Health.Direct.Agent;
using Health.Direct.Agent.Certificates;
using Health.Direct.Agent.Config;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Extensions;
using Health.Direct.ResolverPlugins;
using Xunit;

namespace Health.Direct.SmtpAgent.Tests
{
    public class DCDTTests : IClassFixture<DCDTResolverFixture>
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

        public void SetFixture(DCDTResolverFixture data)
        {
        }

        /// <summary>
        /// This test case verifies that your system can query DNS for address-bound CERT records and discover a valid address-bound X.509 certificate for a Direct address.
        /// http://sitenv.org/direct-certificate-discovery-tool-2015
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("d1@domain1.dcdt31prod.sitenv.org")]
        public void TestD1(string subject)
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
            AssertCert(certs[0], true, DefaultProblemFlags);

            //
            // Now prove the standard dns resolver will also return the Address Cert.
            //
            resolver = new DnsCertResolver(IPAddress.Parse(Dns_Server));
            Assert.NotNull(resolver);

            certs = resolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal(subject, certs[0].ExtractEmailNameOrName());

            AssertCert(certs[0], true, DefaultProblemFlags);
        }

        /// <summary>
        /// This test case verifies that your system can query DNS for domain-bound CERT records and discover a valid domain-bound X.509 certificate for a Direct address.
        /// http://sitenv.org/direct-certificate-discovery-tool-2015
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("d2@domain1.dcdt31prod.sitenv.org")]
        public void TestD2(string subject)
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
            Assert.Equal(2, certs.Count);

            //
            // find invalid cert
            //
            var cert = certs.FindByName("D1_invB");
            Assert.Equal("domain1.dcdt31prod.sitenv.org", cert.GetNameInfo(X509NameType.DnsName, false));
            AssertCert(cert, false, DefaultProblemFlags);

            cert = certs.FindByName("D2_valB");
            Assert.Equal("domain1.dcdt31prod.sitenv.org", cert.GetNameInfo(X509NameType.DnsName, false));
            AssertCert(cert, true, DefaultProblemFlags);

            //
            // Now prove we can get it as a domain with no fail over.
            //
            certs = resolver.GetCertificatesForDomain(email.Host);
            cert = certs.FindByName("D1_invB");
            Assert.Equal("domain1.dcdt31prod.sitenv.org", cert.GetNameInfo(X509NameType.DnsName, false));
            AssertCert(cert, false, DefaultProblemFlags);

            cert = certs.FindByName("D2_valB");
            Assert.Equal("domain1.dcdt31prod.sitenv.org", cert.GetNameInfo(X509NameType.DnsName, false));
            AssertCert(cert, true, DefaultProblemFlags);
        }

        /// <summary>
        /// This test case verifies that your system can query DNS for domain-bound CERT records and discover a valid domain-bound X.509 certificate for a Direct address.
        /// http://sitenv.org/direct-certificate-discovery-tool-2015
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("d2@domain1.dcdt31prod.sitenv.org")]
        public void TestD2_Via_Agent_TrustModel(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = agent.PublicCertResolver;

            var dnsCertResolver = LocateChild<DnsCertResolver>(resolver);
            var diagnostics = new FakeDiagnostics(typeof(DnsCertResolver));
            dnsCertResolver.Error += diagnostics.OnResolverError;

            Assert.NotNull(resolver);

            //
            // Build up an outgoing message to feed to the TrustModel enforce routine.
            //
            var email = string.Format(@"From: HoboJoe@redmond.hsgincubator.com
To: {0}
Message-ID: {1}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?", subject, Guid.NewGuid().ToString("N"));

            var recipient = new DirectAddress(subject);
            var recipients = new DirectAddressCollection() { recipient };
            var sender = new DirectAddress("HoboJoe@redmond.hsgincubator.com");
            var outgoingMessage = new OutgoingMessage(email, recipients, sender);
            outgoingMessage.EnsureRecipientsCategorizedByDomain(agent.Domains);
            var emailAddress = new MailAddress(subject);

            outgoingMessage.Sender.TrustAnchors = agent.TrustAnchors.OutgoingAnchors.GetCertificates(outgoingMessage.Sender);

            //skip private certs...
            X509Certificate2Collection certs = dnsCertResolver.GetCertificates(emailAddress);
            recipient.Certificates = certs;
            recipient.ResolvedCertificates = true;

            var diagnosticsChainValidator = new FakeChainValidatorDiagnostics();
            agent.TrustModel.CertChainValidator.Problem += diagnosticsChainValidator.OnChainProblem;
            agent.TrustModel.Enforce(outgoingMessage);

            Assert.Equal(1, outgoingMessage.Recipients.Certificates.Count());
            var certCollection = new X509Certificate2Collection();
            certCollection.Add(outgoingMessage.Recipients.Certificates);
            Assert.Equal(1, certCollection.Count);
            Assert.True(certCollection.FindByName("D2_valB") != null);

            //
            // Assert problem details 
            // Usefull to logging at the host level
            //
            Assert.Equal(1, diagnosticsChainValidator.ActualErrorMessages.Count);
            Assert.Contains("Trust ERROR A required certificate is not within its validity period when verifying against the current system clock or the timestamp in the signed file.", diagnosticsChainValidator.ActualErrorMessages[0]);
            Assert.Contains("CN=D1_invB", diagnosticsChainValidator.ActualErrorMessages[0]);
        }

        /// <summary>
        /// This test case verifies that your system can query DNS for SRV records and discover a valid address-bound X.509 certificate for a Direct address in the associated LDAP server.
        /// http://sitenv.org/direct-certificate-discovery-tool-2015
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("d3@domain2.dcdt31prod.sitenv.org")]
        public void TestD3(string subject)
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

            AssertCert(certs[0], true, DefaultProblemFlags);
        }

        /// <summary>
        /// This test case verifies that your system can query DNS for SRV records and discover a valid domain-bound X.509 certificate for a Direct address in the associated LDAP server.
        /// http://sitenv.org/direct-certificate-discovery-tool-2015
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("d4@domain2.dcdt31prod.sitenv.org")]
        public void TestD4(string subject)
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

            var cert = certs.FindByName("D4_valD");
            Assert.Equal("domain2.dcdt31prod.sitenv.org", cert.GetNameInfo(X509NameType.DnsName, false));
            AssertCert(cert, true, DefaultProblemFlags);

            Assert.Equal(0, diagnosticsForDnsCertResolver.ActualErrorMessages.Count);
            Assert.Equal(0, diagnosticsForLdapCertResolver.ActualErrorMessages.Count);
        }

        /// <summary>
        /// This test case verifies that your system can query DNS for address-bound CERT records and finds, but does not select the associated invalid address-bound X.509 certificate.
        /// http://sitenv.org/direct-certificate-discovery-tool-2015
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("d5@domain1.dcdt31prod.sitenv.org")]
        public void TestD5(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = agent.PublicCertResolver;
            Assert.NotNull(resolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = resolver.GetCertificates(email);
            Assert.Equal(1, certs.Count);
            var cert = certs.FindByName("D5_invA");
            //
            // Assert cert chain fails
            //
            AssertCert(cert, false, DefaultProblemFlags);
        }

        /// <summary>
        /// This test case verifies that your system can query DNS for domain-bound CERT records and finds, but does not select the associated invalid domain-bound X.509 certificate.
        /// http://sitenv.org/direct-certificate-discovery-tool-2015
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("d6@domain4.dcdt31prod.sitenv.org")]
        public void TestD6(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = agent.PublicCertResolver;
            Assert.NotNull(resolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = resolver.GetCertificates(email);
            Assert.Equal(1, certs.Count);
            var cert = certs.FindByName("D6_invB");
            //
            // Assert cert chain fails
            //
            AssertCert(cert, false, DefaultProblemFlags);
        }

        /// <summary>
        /// Verify that your system did NOT send an email because it could not find a certificate for the Direct address. To pass this test case, you must NOT receive an email in response.
        /// http://sitenv.org/direct-certificate-discovery-tool-2015
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("d7@domain2.dcdt31prod.sitenv.org")]
        public void TestD7(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = agent.PublicCertResolver;
            Assert.NotNull(resolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = resolver.GetCertificates(email);
            Assert.Equal(1, certs.Count);
            var cert = certs.FindByName("D7_invC");
            //
            // Assert cert chain fails
            //
            AssertCert(cert, false, DefaultProblemFlags);
        }

        /// <summary>
        /// This test case verifies that your system can query DNS for SRV records and finds, but does not select the invalid domain-bound X.509 certificate in the associated LDAP server.
        /// http://sitenv.org/direct-certificate-discovery-tool-2015
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("d8@domain5.dcdt31prod.sitenv.org")]
        public void TestD8(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = agent.PublicCertResolver;
            Assert.NotNull(resolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = resolver.GetCertificates(email);
            Assert.Equal(1, certs.Count);
            var cert = certs.FindByName("D8_invD");
            //
            // Assert cert chain fails
            //
            AssertCert(cert, false, DefaultProblemFlags);
        }

        /// <summary>
        ///  This test case verifies that your system can query DNS for address-bound CERT records and select the valid address-bound X.509 certificate instead of the invalid address-bound X.509 certificate.
        /// http://sitenv.org/direct-certificate-discovery-tool-2015
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("d9@domain1.dcdt31prod.sitenv.org")]
        public void TestD9(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = agent.PublicCertResolver;
            Assert.NotNull(resolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = resolver.GetCertificates(email);
            Assert.Equal(2, certs.Count);
            var cert = certs.FindByName("D9_invA");
            //
            // Assert cert chain fails
            //
            AssertCert(cert, false, DefaultProblemFlags);

            cert = certs.FindByName("D9_valA");
            //
            // Assert cert chain fails
            //
            AssertCert(cert, true, DefaultProblemFlags);
        }

        /// <summary>
        /// This test case verifies that your system can query DNS for SRV records and attempts to connect to an LDAP server based on the priority value specified in the SRV records until a successful connection is made. Your system should first attempt to connect to an LDAP server associated with an SRV record containing the lowest priority value (highest priority). Since this LDAP server is unavailable, your system should then attempt to connect to the LDAP server associated with an SRV record containing the second lowest priority value (second highest priority) and discover the valid address-bound X.509 certificate in the available LDAP server.
        /// http://sitenv.org/direct-certificate-discovery-tool-2015
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("d10@domain3.dcdt31prod.sitenv.org")]
        public void TestD10(string subject)
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

            AssertCert(certs[0], true, DefaultProblemFlags);

            Assert.Equal(1, diagnosticsForLdapCertResolver.ActualErrorMessages.Count);
            Assert.Equal("Error=BindFailure\r\n_ldap._tcp.domain3.dcdt31prod.sitenv.org:10389 Priority:0 Weight:0", diagnosticsForLdapCertResolver.ActualErrorMessages[0]);
        }

        /// <summary>
        /// This test case verifies that your system does not find any certificates when querying DNS for CERT records and does not find any SRV records in DNS.
        /// http://sitenv.org/direct-certificate-discovery-tool-2015
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("d11@domain6.dcdt31prod.sitenv.org")]
        public void TestD11(string subject)
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
        /// This test case verifies that your system can query DNS for SRV records and attempts to connect to an LDAP server associated with the only SRV record that should be found. Since this LDAP server is unavailable or does not exist and no additional SRV records should have been found, your system should not discover any X.509 certificates in either DNS CERT records or LDAP servers.
        /// http://sitenv.org/direct-certificate-discovery-tool-2015
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("d12@domain7.dcdt31prod.sitenv.org")]
        public void TestD12(string subject)
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
            Assert.Null(certs);

            Assert.Equal(1, diagnosticsForLdapCertResolver.ActualErrorMessages.Count);
            Assert.Equal("Error=BindFailure\r\n_ldap._tcp.domain7.dcdt31prod.sitenv.org:10389 Priority:0 Weight:0", diagnosticsForLdapCertResolver.ActualErrorMessages[0]);
        }

        /// <summary>
        /// This test case verifies that your system does not discover any certificates in DNS CERT records or LDAP servers when no certificates should be found.
        /// http://sitenv.org/direct-certificate-discovery-tool-2015
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("d13@domain8.dcdt31prod.sitenv.org")]
        public void TestD13(string subject)
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
            Assert.Empty(certs);

            Assert.Equal(0, diagnosticsForLdapCertResolver.ActualErrorMessages.Count);
        }

        /// <summary>
        /// This test case verifies that your system can query DNS for address-bound CERT records and discover a valid address-bound X.509 certificate that is larger than 512 bytes using a TCP connection.
        /// http://sitenv.org/direct-certificate-discovery-tool-2015
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("d14@domain1.dcdt31prod.sitenv.org")]
        public void TestD14(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = agent.PublicCertResolver;
            Assert.NotNull(resolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = resolver.GetCertificates(email);
            Assert.Equal(1, certs.Count);
            var cert = certs.FindByName("D14_valA");
            //
            // Assert cert chain is good
            //
            AssertCert(cert, true, DefaultProblemFlags);
        }

        /// <summary>
        /// This test case verifies that your system can query DNS for SRV records and discover a valid address-bound X.509 certificate in the LDAP server associated with an SRV record containing the lowest priority value (highest priority).
        /// http://sitenv.org/direct-certificate-discovery-tool-2015
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("d15@domain2.dcdt31prod.sitenv.org")]
        public void TestD15(string subject)
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
            Assert.Equal(1, certs.Count);
            var cert = certs.FindByName("D15_valC");
            //
            // Assert cert chain is good
            //
            AssertCert(cert, true, DefaultProblemFlags);

            //
            //  Note: this test has an invalid cert at priority 1, but the LDAP resolver does not retrieve it because it found one at priority 0
            //
        }

        /// <summary>
        /// This test case verifies that your system can query DNS for SRV records and discover a valid address-bound X.509 certificate in the LDAP server associated with an SRV record containing the lowest priority value (highest priority) and the highest weight value when SRV records with the same priority value exist.
        /// http://sitenv.org/direct-certificate-discovery-tool-2015
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("d16@domain5.dcdt31prod.sitenv.org")]
        public void TestD16(string subject)
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
            Assert.Equal(1, certs.Count);
            var cert = certs.FindByName("D16_valC");
            //
            // Assert cert chain is good
            //
            AssertCert(cert, true, DefaultProblemFlags);

            //
            //  Note: this test has a second cert at priority 0 with a weight of 0, but the LDAP resolver does not retrieve it because it found one at priority 0, with a weight of 100
            //
        }

        /// <summary>
        /// Special setup.  Download Anchor from DCDT and install in NHindAnchor Machine Cert Store.
        /// 
        /// 
        /// </summary>
        /// <param name="subject"></param>
        /// <param name="ip">Dns server IP</param>
        /// <param name="commonName">Filter extra anchors so it is easier to debug</param>
        [Theory]
        [InlineData(
            "d17@domain9.dcdt31prod.sitenv.org",
            "8.8.8.8",
            "CN=demo31.direct-test.com_ca_root",
            @"..\..\..\..\unittests\smtpAgent\Anchors\dcdt31prod.sitenv.org_ca_root.der")]
        public void TestD17(string subject, string ip, string commonName, string anchorFile)
        {
            var anchorText = File.ReadAllBytes(anchorFile);
            var anchor = new X509Certificate2(anchorText);

            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = new DnsCertResolver(IPAddress.Parse(ip));
            Assert.NotNull(resolver);

            var dnsCertResolver = resolver;
            var diagnosticsForDnsCertResolver = new FakeDiagnostics(typeof(DnsCertResolver));
            dnsCertResolver.Error += diagnosticsForDnsCertResolver.OnResolverError;

            //
            // Build up an outgoing message to feed to the TrustModel enforce routine.
            //
            var email = string.Format(@"From: HoboJoe@redmond.hsgincubator.com
To: {0}
Message-ID: {1}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?", subject, Guid.NewGuid().ToString("N"));

            var recipient = new DirectAddress(subject);
            var recipients = new DirectAddressCollection() { recipient };
            var sender = new DirectAddress("HoboJoe@redmond.hsgincubator.com");
            var outgoingMessage = new OutgoingMessage(email, recipients, sender);
            outgoingMessage.EnsureRecipientsCategorizedByDomain(agent.Domains);
            var emailAddress = new MailAddress(subject);

            outgoingMessage.Sender.TrustAnchors =
                agent.TrustAnchors.OutgoingAnchors.GetCertificates(outgoingMessage.Sender);

            //skip private certs...
            X509Certificate2Collection certs = dnsCertResolver.GetCertificates(emailAddress);
            recipient.Certificates = certs;
            recipient.ResolvedCertificates = true;

            var diagnosticsChainValidator = new FakeChainValidatorDiagnostics();
            agent.TrustModel.CertChainValidator.Problem += diagnosticsChainValidator.OnChainProblem;
            agent.TrustModel.Enforce(outgoingMessage);

            Assert.Equal(1, outgoingMessage.Recipients.Certificates.Count());
            var certCollection = new X509Certificate2Collection();
            certCollection.Add(outgoingMessage.Recipients.Certificates);
            Assert.True(certCollection.FindByName("D17_valA") != null);

            //
            // Assert problem details 
            // Usefull to logging at the host level
            //
            Assert.Equal(2, diagnosticsChainValidator.ActualErrorMessages.Count);

            Assert.Contains("Trust ERROR The certificate is revoked.", diagnosticsChainValidator.ActualErrorMessages[0]);
            Assert.True(diagnosticsChainValidator.ActualErrorMessages.Any(a => a.Contains("CN=D17_invB")));
            Assert.Contains("Trust ERROR The certificate is revoked.", diagnosticsChainValidator.ActualErrorMessages[1]);
            Assert.True(diagnosticsChainValidator.ActualErrorMessages.Any(a => a.Contains("CN=D17_invC")));
            Assert.False(diagnosticsChainValidator.ActualErrorMessages.Any(a => a.Contains("CN=D17_invA")));

            //
            // Assert certificates resolved details
            //

            Assert.Equal(3, certs.Count);
            var cert = certs.FindByName("D17_valA");
            //
            // Assert cert chain is good
            //
            AssertCert(cert, true, DefaultProblemFlags, anchor);

            cert = certs.FindByName("D17_invB");
            //
            // Assert cert chain is bad
            // 
            //
            AssertCert(cert, false, X509ChainStatusFlags.Revoked, anchor);

            cert = certs.FindByName("D17_invC");
            //
            // Assert cert chain is bad
            //
            AssertCert(cert, false, DefaultProblemFlags, anchor);
        }

        [Theory]
        [InlineData(
            "d18@domain10.dcdt31prod.sitenv.org",
            "8.8.8.8",
            @"..\..\..\..\unittests\smtpAgent\Anchors\dcdt31prod.sitenv.org_ca_root.der")]
        public void TestD18(string subject, string ip, string anchorFile)
        {
            var anchorText = File.ReadAllBytes(anchorFile);
            var anchor = new X509Certificate2(anchorText);

            AgentSettings settings = AgentSettings.Load(TestRealResolversXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver resolver = new DnsCertResolver(IPAddress.Parse(ip));
            Assert.NotNull(resolver);

            var dnsCertResolver = resolver;
            var diagnosticsForDnsCertResolver = new FakeDiagnostics(typeof(DnsCertResolver));
            dnsCertResolver.Error += diagnosticsForDnsCertResolver.OnResolverError;

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = resolver.GetCertificates(email);
            Assert.Equal(1, certs.Count);
            var cert = certs.FindByName("D18_valA");

            //
            // Assert cert chain is good
            //
            AssertCert(cert, true, DefaultProblemFlags, anchor);

            //
            //  Note: this test has a second domain cert domain10.demo31.direct-test.com that is not discovered because we found an address cert first
            //
        }

        private void AssertCert(X509Certificate2 cert, bool expectValidCert, X509ChainStatusFlags x509StatusFlags, X509Certificate2 anchor = null)
        {
            if (anchor == null)
            {
                var anchorText = File.ReadAllBytes(@"..\..\..\..\unittests\smtpAgent\Anchors\dcdt31prod.sitenv.org_ca_root.der");
                anchor = new X509Certificate2(anchorText);
            }
            
            var policy = new X509ChainPolicy();
            policy.ExtraStore.Add(anchor);
            policy.VerificationFlags = X509VerificationFlags.IgnoreWrongUsage;
           

            X509Chain chainBuilder;
            using (X509ChainEngine secureChainEngine = new X509ChainEngine(anchor))
            {
                secureChainEngine.BuildChain(cert, policy, out chainBuilder);
            }

            X509ChainElementCollection chainElements = chainBuilder.ChainElements;

            // If we don't have a trust chain, then we obviously have a problem...
            Assert.False(chainElements.IsNullOrEmpty(), string.Format("Can't find a trust chain: {0} ", cert.Subject));

            bool foundAnchor = false;

            // walk the chain starting at the leaf and see if we hit any issues
            foreach (X509ChainElement chainElement in chainElements)
            {
                if (anchor.Thumbprint == chainElement.Certificate.Thumbprint)
                {
                    foundAnchor = true;
                    continue;
                }

                if (expectValidCert)
                {
                    AssertChainHasNoProblems(chainElement, x509StatusFlags);
                }
                else
                {
                    AssertChainHasProblems(chainElement, x509StatusFlags);
                }
            }

            Assert.True(foundAnchor, "Did not chain to an anchor");
        }
        private static void AssertChainHasNoProblems(X509ChainElement chainElement, X509ChainStatusFlags x509StatusFlags)
        {
            X509ChainStatus[] chainElementStatus = chainElement.ChainElementStatus;

            if (chainElementStatus.IsNullOrEmpty())
            {
                return;
            }

            foreach (var chainElementStatu in chainElementStatus)
            {
                Assert.False((chainElementStatu.Status & x509StatusFlags) != 0);
            }
        }

        private static void AssertChainHasProblems(X509ChainElement chainElement, X509ChainStatusFlags x509StatusFlags)
        {
            X509ChainStatus[] chainElementStatus = chainElement.ChainElementStatus;

            if (chainElementStatus.IsNullOrEmpty())
            {
                return;
            }

            foreach (var chainElementStatu in chainElementStatus)
            {
                if ((chainElementStatu.Status & x509StatusFlags) != 0)
                {
                    return;  //we expect problems
                }
            }
            Assert.True(false, "Expected chain problems and found none." + String.Join("", "", chainElementStatus.Select(s => s.Status.ToString()).ToArray()));
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

    public class DCDTResolverFixture : IDisposable
    {

        [DllImport("user32.dll", EntryPoint = "FindWindow")]
        private static extern IntPtr FindWindow(string lp1, string lp2);

        [DllImport("USER32.DLL")]
        public static extern bool SetForegroundWindow(IntPtr hWnd);

        public DCDTResolverFixture()
        {
            Console.WriteLine("DCDTResolverFixture ctor: This should only be run once");

            InstallAnchorsInTrustedRootUserStore();
            InstallAnchorsInMachineStore();
        }

        private void InstallAnchorsInTrustedRootUserStore()
        {
            //
            // Ensure certs installed
            //

            var store = new X509Store(StoreName.AuthRoot, StoreLocation.CurrentUser);
            try
            {
                store.Open(OpenFlags.ReadWrite);

                var file = @".\Anchors\staging.direct-test.com_ca_root.der";

                if (!AnchorExists(store, file))
                {
                    InstallAnchor(store, file);
                    //CloseDialog();  Should be no dialog with AuthRoot store name
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
            }
            finally
            {
                store.Close();
            }

            try
            {
                store.Open(OpenFlags.ReadWrite);

                var file = @".\Anchors\demo31.direct-test.com_ca_root.cer";

                if (!AnchorExists(store, file))
                {
                    InstallAnchor(store, file);
                    //CloseDialog(); Should be no dialog with AuthRoot store name
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
            }
            finally
            {
                store.Close();
            }
        }

        private void InstallAnchorsInMachineStore()
        {
            var anchorStore = new X509Store("NHINDAnchors", StoreLocation.LocalMachine);
            anchorStore.Open(OpenFlags.ReadWrite);

            var file = @".\Anchors\dcdt31prod.sitenv.org_ca_root.der";

            if (!AnchorExists(anchorStore, file))
            {
                anchorStore.Add(new X509Certificate2(X509Certificate.CreateFromCertFile(file)));
            }

            anchorStore.Close();
        }

        private void CloseDialog()
        {
            Thread.Sleep(500);

            var iHandle = FindWindow("#32770", "Security Warning");
            if (iHandle != IntPtr.Zero)
            {
                SetForegroundWindow(iHandle);
                SendKeys.SendWait("%Y");
            }
        }

        async void InstallAnchor(X509Store store, string file)
        {
            if (!AnchorExists(store, file))
            {
                await Task.Run(() => store.Add(new X509Certificate2(X509Certificate.CreateFromCertFile(file))));
            }
        }

        bool AnchorExists(X509Store store, string file)
        {
            var cert = new X509Certificate2(X509Certificate.CreateFromCertFile(file));
            var found = store.Certificates.FindByThumbprint(cert.Thumbprint);
            return found != null;
        }

        public void SomeMethod()
        {
            Console.WriteLine("DCDTResolverFixture::SomeMethod()");
        }

        public void Dispose()
        {
            Console.WriteLine("DCDTResolverFixture: Disposing DCDTResolverFixture");
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

    public class FakeChainValidatorDiagnostics
    {
        public bool Called;

        private readonly List<string> _actualErrorMessages = new List<string>();
        public List<string> ActualErrorMessages
        {
            get { return _actualErrorMessages; }
        }

        public void OnChainProblem(X509ChainElement chainElement)
        {
            foreach (var chainElementStatus in chainElement.ChainElementStatus
                .Where(s => (s.Status & TrustChainValidator.DefaultProblemFlags) != 0))
            {
                var problem = string.Format("Trust ERROR {0}, {1}", chainElementStatus.StatusInformation, chainElement.Certificate);
                _actualErrorMessages.Add(problem);
            }
        }
    }
}
