using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Net;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Agent;
using Health.Direct.Agent.Config;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Extensions;
using Xunit;
using Xunit.Extensions;
using System.Net.Mail;

namespace Health.Direct.ModSpec3.ResolverPlugins.tests
{
   
    /// <summary>
    /// Use Case tests matching the ModSpecPhase3 test cases.
    /// Each test experiments with the DnsResolver and ModSpec3.ResolverPlugins for 
    /// DNS and LDAP resolution. 
    /// </summary>
    public class ResolverTests
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
                            <TypeName>Health.Direct.ModSpec3.ResolverPlugins.DnsCertResolverProxy, Health.Direct.ModSpec3.ResolverPlugins</TypeName>
                            <Settings> 
                                <ServerIP>8.8.8.8</ServerIP>
                                <BackupServerIP>8.8.8.8</BackupServerIP>
                                <Timeout>5000</Timeout>
                            </Settings>
                        </Definition>
                    </PluginResolver>
                    <PluginResolver>
                        <Definition>
                            <TypeName>Health.Direct.ModSpec3.ResolverPlugins.LdapCertResolverProxy, Health.Direct.ModSpec3.ResolverPlugins</TypeName>
                            <Settings> 
                               <ServerIP>8.8.8.8</ServerIP>
                               <Timeout>5000</Timeout>
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
        [InlineData("dts500@direct1.direct-test.com")]
        public void Test500(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver pluginResolver = agent.PublicCertResolver;
            Assert.NotNull(pluginResolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = pluginResolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal("dts500@direct1.direct-test.com", certs[0].ExtractEmailNameOrName());
            AssertCert(certs[0], true);

            //
            // Now prove the standard dns resolver will also return the Address Cert.
            //
            pluginResolver = new Common.Certificates.DnsCertResolver(IPAddress.Parse("8.8.8.8"));
            Assert.NotNull(pluginResolver);

            certs = pluginResolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal("dts500@direct1.direct-test.com", certs[0].ExtractEmailNameOrName());

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
        [InlineData("dts501@direct1.direct-test.com")]
        public void Test501(string subject)
        {
            //Debugger.Launch();
            AgentSettings settings = AgentSettings.Load(TestXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver pluginResolver = agent.PublicCertResolver;

            var dnsCertResolver = LocateChild<DnsCertResolverProxy>(pluginResolver);
            var diagnostics = new FakeDiagnostics(typeof(DnsCertResolver));
            dnsCertResolver.Error += diagnostics.OnResolverError;
            
            Assert.NotNull(pluginResolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = pluginResolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal("direct1.direct-test.com", certs[0].ExtractEmailNameOrName());
            AssertCert(certs[0], true);

            Assert.Equal(1, diagnostics.ActualErrorMessages.Count);
            Assert.Equal("Chain Element has problem dts501@direct1.direct-test.com;NotTimeValid", diagnostics.ActualErrorMessages[0]);
            
            //
            // Now prove we can get it from as a domain with no fail over.
            //
            certs = pluginResolver.GetCertificatesForDomain(email.Host);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal("direct1.direct-test.com", certs[0].ExtractEmailNameOrName());

            AssertCert(certs[0], true);


            //
            // Now prove the standard dns resolver will return the invalid Address Cert.
            //
            pluginResolver = new Common.Certificates.DnsCertResolver(IPAddress.Parse("8.8.8.8"));
            Assert.NotNull(pluginResolver);

            certs = pluginResolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal("dts501@direct1.direct-test.com", certs[0].ExtractEmailNameOrName());

            AssertCert(certs[0], false);

        }

        


        /// <summary>
        /// Works on both DnsResolver and Modphase3...
        /// 
        /// Target Certificate: 
        ///A valid address-bound certificate that is larger than 512 bytes in a DNS CERT record for the Direct address.
        /// </summary>
        /// <param name="subject"></param>
        [Theory]
        [InlineData("dts502@direct1.direct-test.com")]
        public void Test502(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver pluginResolver = agent.PublicCertResolver;
            Assert.NotNull(pluginResolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = pluginResolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal("dts502@direct1.direct-test.com", certs[0].ExtractEmailNameOrName());

            AssertCert(certs[0], true);



            //
            // Now prove the standard dns resolver will return the greater than 512 byte dns cert.
            //
            pluginResolver = new Common.Certificates.DnsCertResolver(IPAddress.Parse("8.8.8.8"));
            Assert.NotNull(pluginResolver);

            certs = pluginResolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal("dts502@direct1.direct-test.com", certs[0].ExtractEmailNameOrName());

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
        [InlineData("dts505@direct2.direct-test.com")]
        public void Test505(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver pluginResolver = agent.PublicCertResolver;
            Assert.NotNull(pluginResolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = pluginResolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal("dts505@direct2.direct-test.com", certs[0].ExtractEmailNameOrName());

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
        [InlineData("dts506@direct2.direct-test.com")]
        public void Test506(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestXml);
            DirectAgent agent = settings.CreateAgent();
            var email = new MailAddress(subject);


            //
            // Proving the 506 test contains a Background Cert in dns domain that should not be there.  This is not documented
            // in the 506 test.
            //
            ICertificateResolver pluginResolver = new Common.Certificates.DnsCertResolver(IPAddress.Parse("8.8.8.8"));
            Assert.NotNull(pluginResolver);

            X509Certificate2Collection certs = pluginResolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal("direct2.direct-test.com", certs[0].ExtractEmailNameOrName());

            //This is not expected according to the 506 test Background Cert info we should not have found a domain cert at all
            AssertCert(certs[0], false);


            //
            // Lets get back to testing the ModSpec3 Resolvers.
            //

            pluginResolver = agent.PublicCertResolver;
            Assert.NotNull(pluginResolver);

            
            certs = pluginResolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal("dts506@direct2.direct-test.com", certs[0].ExtractEmailNameOrName());

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
        [InlineData("dts507@direct3.direct-test.com")]
        public void Test507(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver pluginResolver = agent.PublicCertResolver;
            Assert.NotNull(pluginResolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = pluginResolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.Equal(1, certs.Count);
            Assert.Equal("dts507@direct3.direct-test.com", certs[0].ExtractEmailNameOrName());

            AssertCert(certs[0], true);
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
        [InlineData("dts515@direct2.direct-test.com")]
        public void Test515(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver pluginResolver = agent.PublicCertResolver;
            Assert.NotNull(pluginResolver);


            var dnsCertResolver = LocateChild<DnsCertResolverProxy>(pluginResolver);
            var diagnosticsForDnsCertResolver = new FakeDiagnostics(typeof(DnsCertResolver));
            dnsCertResolver.Error += diagnosticsForDnsCertResolver.OnResolverError;

            var ldapCertResolver = LocateChild<LdapCertResolverProxy>(pluginResolver);
            var diagnosticsForLdapCertResolver = new FakeDiagnostics(typeof(LdapCertResolver));
            ldapCertResolver.Error += diagnosticsForLdapCertResolver.OnResolverError;
            
            var email = new MailAddress(subject);
            X509Certificate2Collection certs = pluginResolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);

            Assert.Equal(2, diagnosticsForDnsCertResolver.ActualErrorMessages.Count);
            Assert.Equal("Chain Element has problem dts515@direct2.direct-test.com;NotTimeValid", diagnosticsForDnsCertResolver.ActualErrorMessages[0]);
            Assert.Equal("Chain Element has problem direct2.direct-test.com;NotTimeValid", diagnosticsForDnsCertResolver.ActualErrorMessages[1]);

            Assert.Equal(1, diagnosticsForLdapCertResolver.ActualErrorMessages.Count);
            Assert.Equal("Chain Element has problem dts515@direct2.direct-test.com;NotTimeValid", diagnosticsForLdapCertResolver.ActualErrorMessages[0]);

            Assert.Equal("direct2.direct-test.com", certs[0].ExtractEmailNameOrName());
            AssertCert(certs[0], true);
            
            
        }

        [Theory]
        [InlineData("dts517@direct3.direct-test.com")]
        public void Test517(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver pluginResolver = agent.PublicCertResolver;
            Assert.NotNull(pluginResolver);

            var ldapCertResolver = LocateChild<ModSpec3.ResolverPlugins.LdapCertResolverProxy>(pluginResolver);
            var diagnosticsForLdapCertResolver = new FakeDiagnostics(typeof(LdapCertResolver));
            ldapCertResolver.Error += diagnosticsForLdapCertResolver.OnResolverError;


            var email = new MailAddress(subject);
            X509Certificate2Collection certs = pluginResolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal("dts517@direct3.direct-test.com", certs[0].ExtractEmailNameOrName());

            AssertCert(certs[0], true);

            Assert.Equal(3, diagnosticsForLdapCertResolver.ActualErrorMessages.Count);
            Assert.Equal("Error=BindFailure\r\n_ldap._tcp.direct3.direct-test.com:389 Priority:0 Weight:0", diagnosticsForLdapCertResolver.ActualErrorMessages[0]);
            Assert.Equal("Error=BindFailure\r\n_ldap._tcp.direct3.direct-test.com:389 Priority:0 Weight:0", diagnosticsForLdapCertResolver.ActualErrorMessages[1]);
            Assert.Equal("Error=NoUserCertificateAttribute\r\ndts517@direct3.direct-test.com_ldap._tcp.direct3.direct-test.com:10389 Priority:1 Weight:0", diagnosticsForLdapCertResolver.ActualErrorMessages[2]);


            //
            // Ok no lets just use the modspec3 LDAP resolver because I don't really know that 
            // we fell back to LDAP with above test.
            //

            pluginResolver = LocateChild<ModSpec3.ResolverPlugins.LdapCertResolverProxy>(agent.PublicCertResolver);
            Assert.NotNull(pluginResolver);

            ldapCertResolver = pluginResolver;
            diagnosticsForLdapCertResolver = new FakeDiagnostics(typeof(LdapCertResolver));
            ldapCertResolver.Error += diagnosticsForLdapCertResolver.OnResolverError;

            email = new MailAddress(subject);
            certs = pluginResolver.GetCertificates(email);
            Assert.NotNull(certs);
            Assert.True(certs.Count == 1);
            Assert.Equal("dts517@direct3.direct-test.com", certs[0].ExtractEmailNameOrName());

            AssertCert(certs[0], true);


            Assert.Equal(3, diagnosticsForLdapCertResolver.ActualErrorMessages.Count);
            Assert.Equal("Error=BindFailure\r\n_ldap._tcp.direct3.direct-test.com:389 Priority:0 Weight:0", diagnosticsForLdapCertResolver.ActualErrorMessages[0]);
            Assert.Equal("Error=BindFailure\r\n_ldap._tcp.direct3.direct-test.com:389 Priority:0 Weight:0", diagnosticsForLdapCertResolver.ActualErrorMessages[1]);
            Assert.Equal("Error=NoUserCertificateAttribute\r\ndts517@direct3.direct-test.com_ldap._tcp.direct3.direct-test.com:10389 Priority:1 Weight:0", diagnosticsForLdapCertResolver.ActualErrorMessages[2]);
                       

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
        [InlineData("dts520@direct5.direct-test.com")]
        public void Test520(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver pluginResolver = agent.PublicCertResolver;
            Assert.NotNull(pluginResolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = pluginResolver.GetCertificates(email);
            Assert.True(certs == null || certs.Count == 0, string.Format("Oops found cert: {0}", certs == null ? "" : certs[0].ExtractEmailNameOrName()));
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
        [InlineData("dts511@direct4.direct-test.com")]
        public void Test511(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver pluginResolver = agent.PublicCertResolver;
            Assert.NotNull(pluginResolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = pluginResolver.GetCertificates(email);
            Assert.True(certs == null || certs.Count == 0, string.Format("Oops found cert: {0}", certs == null ? "" : certs[0].ExtractEmailNameOrName()));
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
        [InlineData("dts512@direct6.direct-test.com")]
        public void Test512(string subject)
        {
            AgentSettings settings = AgentSettings.Load(TestXml);
            DirectAgent agent = settings.CreateAgent();

            ICertificateResolver pluginResolver = agent.PublicCertResolver;
            Assert.NotNull(pluginResolver);

            var email = new MailAddress(subject);
            X509Certificate2Collection certs = pluginResolver.GetCertificates(email);
            Assert.True(certs == null || certs.Count == 0, string.Format("Oops found cert: {0}", certs == null ? "" : certs[0].ExtractEmailNameOrName()));
        }


        static ICertificateResolver LocateChild<T>(ICertificateResolver resolver)
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
        public List<string> ActualErrorMessages { 
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
