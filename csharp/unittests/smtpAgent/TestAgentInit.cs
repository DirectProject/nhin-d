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
using System.IO;
using System.Reflection;
using Xunit;
using Xunit.Extensions;
using Health.Direct.Common.Container;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Agent.Config;
using Health.Direct.Common.Certificates;

namespace Health.Direct.SmtpAgent.Tests
{
    /// <summary>
    /// Test various agent initializations
    /// </summary>
    public class TestAgentInit
    {
        MessageArrivalEventHandler m_handler;

        public TestAgentInit()
        {
            m_handler = new MessageArrivalEventHandler();
        }

        
        public static IEnumerable<object[]> ConfigFileNames
        {
            get
            {
                yield return new[] { "TestSmtpAgentConfig.xml" };
                yield return new[] { "TestSmtpAgentConfigService.xml" };
            }
        }

        [Theory(Skip = "Requires Config Service to be running on the local server")]
        [PropertyData("ConfigFileNames")]
        public void TestWithService(string fileName)
        {
            m_handler.InitFromConfigFile(Fullpath(fileName));
        }

        [Theory]//(Skip = "Requires Config Service to be running on the local server")]
        [PropertyData("DirectTenancConfigFiles")]
        public void TestDirectTenantWithService(string fileName)
        {
            m_handler.InitFromConfigFile(Fullpath(fileName));
        }

        [Fact]
        public void TestContainer()
        {
            SmtpAgentSettings settings = null;
            Assert.DoesNotThrow(() => settings = SmtpAgentSettings.LoadSettings(Fullpath("TestPlugin.xml")));
            Assert.NotNull(settings.Container);
            Assert.True(settings.Container.HasComponents);

            ResetSmtpAgentFactory();
            SmtpAgent agent = SmtpAgentFactory.Create(Fullpath("TestPlugin.xml"));

            ILogFactory logFactory = null;
            Assert.DoesNotThrow(() => logFactory = IoC.Resolve<ILogFactory>());

            IAuditor auditor = null;
            Assert.DoesNotThrow(() => auditor = IoC.Resolve<IAuditor>());
            Assert.True(auditor is DummyAuditor);
        }

        //
        // Use reflection to uninitialize the factory.
        // Do not publicly expose this reset feature as SmtpAgentFactory is correct as designed.
        // But our tests need to reload the singleton in process.
        //
        private void ResetSmtpAgentFactory()
        {
            var initialized = typeof(SmtpAgentFactory).GetField("m_initialized", BindingFlags.NonPublic | BindingFlags.Static);
            initialized.SetValue(null, false);
        }


        [Theory]
        [PropertyData("ConfigFiles")]
        public void TestLoadConfig(string fileName)
        {
            SmtpAgentSettings settings = null;
            
            Assert.DoesNotThrow(() => settings = SmtpAgentSettings.LoadSettings(Fullpath(fileName)));
            Assert.NotNull(settings);
            
            Assert.NotNull(settings.PublicCerts);
            this.Verify(settings.PublicCerts);
            
            Assert.NotNull(settings.PrivateCerts);
            this.Verify(settings.PrivateCerts);
            
            Assert.NotNull(settings.Anchors);
            this.Verify(settings.Anchors);

        }

        
        public static IEnumerable<object[]> ConfigFiles
        {
            get
            {
                yield return new[] { "TestSmtpAgentConfig.xml"};
                yield return new[] { "TestSmtpAgentConfigService.xml" };
                yield return new[] { "TestSmtpAgentConfigServiceProd.xml" };
            }
        }


        [Theory]
        [PropertyData("DirectTenancConfigFiles")]
        public void TestDirectTenantLoadConfig(string fileName)
        {
            SmtpAgentSettings settings = null;

            Assert.DoesNotThrow(() => settings = SmtpAgentSettings.LoadSettings(Fullpath(fileName)));
            Assert.NotNull(settings);

            this.Verify(settings.DomainTenants);
        }


        public static IEnumerable<object[]> DirectTenancConfigFiles
        {
            get
            {
                yield return new[] { "TestDomainTenancyConfig.xml" };
            }
        }

        string Fullpath(string fileName)
        {
            string folderPath = Path.Combine(Directory.GetCurrentDirectory(), "SmtpAgentTestFiles");
            return Path.Combine(folderPath, fileName);
        }
        
        void Verify(CertificateSettings settings)
        {
            Assert.NotNull(settings.Resolvers);
            foreach(CertResolverSettings resolverSettings in settings.Resolvers)
            {
                Assert.DoesNotThrow(() => resolverSettings.Validate());
                
                CertServiceResolverSettings serviceResolverSettings = resolverSettings as CertServiceResolverSettings;
                if (serviceResolverSettings != null)
                {
                    Assert.False(serviceResolverSettings.OrgCertificatesOnly);
                }
                
                ICertificateResolver resolver = null;
                Assert.DoesNotThrow(() => resolver = resolverSettings.CreateResolver());
                Assert.NotNull(resolver);
                
                if (serviceResolverSettings != null)
                {
                    Assert.False(((ConfigCertificateResolver) resolver).OrgCertificatesOnly);
                    
                    serviceResolverSettings.OrgCertificatesOnly = true;
                    resolver = serviceResolverSettings.CreateResolver();

                    Assert.True(((ConfigCertificateResolver)resolver).OrgCertificatesOnly);
                }
            }
        }

        void Verify(TrustAnchorSettings settings)
        {
            Assert.NotNull(settings.Resolver);
            Assert.DoesNotThrow(() => settings.Validate());
            
            AnchorServiceResolverSettings serviceResolverSettings = settings.Resolver as AnchorServiceResolverSettings;
            if (serviceResolverSettings != null)
            {
                Assert.False(serviceResolverSettings.OrgCertificatesOnly);
            }

            ITrustAnchorResolver resolver = null;
            Assert.DoesNotThrow(() => resolver = settings.Resolver.CreateResolver());
            Assert.NotNull(resolver);
            
            if (serviceResolverSettings != null)
            {
                ConfigAnchorResolver serviceResolver = (ConfigAnchorResolver) resolver;
                Assert.False(serviceResolver.OrgCertificatesOnly);
                
                Assert.False(((CertificateResolver) serviceResolver.IncomingAnchors).OrgCertificatesOnly);
                Assert.False(((CertificateResolver)serviceResolver.OutgoingAnchors).OrgCertificatesOnly);
                
                serviceResolverSettings.OrgCertificatesOnly = true;
                serviceResolver = (ConfigAnchorResolver) serviceResolverSettings.CreateResolver();

                Assert.True(((CertificateResolver)serviceResolver.IncomingAnchors).OrgCertificatesOnly);
                Assert.True(((CertificateResolver)serviceResolver.OutgoingAnchors).OrgCertificatesOnly);
            }            
        }

        private void Verify(DomainSettings settings)
        {
            Assert.NotNull(settings.Resolver);
            Assert.DoesNotThrow(() => settings.Validate());

            DomainServiceResolverSettings serviceResolverSettings = settings.Resolver as DomainServiceResolverSettings;
            if (serviceResolverSettings != null)
            {
                Assert.True(serviceResolverSettings.AgentName == "Agent1");
                Assert.True(serviceResolverSettings.ClientSettings.Url == "http://localhost:6692/DomainManagerService.svc/Domains");
            }
        }
    }

    public class DummyAuditor : IAuditor
    {
        public void Log(string category)
        {
        }

        public void Log(string category, string message)
        {
        }
    }
}