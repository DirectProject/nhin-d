/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook     
   
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.IO;
using System.Xml;
using Xunit;

namespace Health.Direct.Install.Tools.Tests
{
    public class XPathTests
    {


        [Fact]
        public void QueryTest()
        {
            var editor = new XPath();
            editor.XmlFilePath = "DirectDnsResponderSvc.exe.config";

            var value = editor.SelectSingleAttribute(@"/configuration/ServiceSettingsGroup/RecordRetrievalServiceSettings/@Url");
            var expected = @"http://localhost/DnsService/RecordRetrievalService.svc/Records";

            Assert.Equal(expected, value);
        }

        [Fact]
        public void QueryFragmentTest()
        {
            var editor = new XPath();
            editor.XmlFilePath = "DirectDnsResponderSvc.exe.config";
            var value = editor.GetFragment(@"/configuration/ServiceSettingsGroup/DnsServerSettings");
            
            Assert.NotEmpty(value);
        }

        /// <summary>
        /// Query and update the XPath location. 
        /// </summary>
        [Fact]
        public void ConnectAndDropDirectDnsResponder_Test()
        {

            File.Copy("DirectDnsResponderSvc.exe.config", "DirectDnsResponderSvc.exe.config.test", true);
            var editor = new XPath();
            editor.XmlFilePath = "DirectDnsResponderSvc.exe.config.test";

            var value = editor.SelectSingleAttribute(@"/configuration/ServiceSettingsGroup/RecordRetrievalServiceSettings/@Url");
            var expected = @"http://localhost/DnsService/RecordRetrievalService.svc/Records";
            Assert.Equal(expected, value);

            editor.SetSingleAttribute(@"/configuration/ServiceSettingsGroup/RecordRetrievalServiceSettings/@Url",
               @"http://SomeServer/DnsService/RecordRetrievalService.svc/Records");


            value = editor.SelectSingleAttribute(@"/configuration/ServiceSettingsGroup/RecordRetrievalServiceSettings/@Url");
            expected = @"http://SomeServer/DnsService/RecordRetrievalService.svc/Records";

            Assert.Equal(expected, value);
        }

        /// <summary>
        /// Query and update the XPath location. 
        /// </summary>
        [Fact]
        public void ConnectAndDropConfigService_Test()
        {

            File.Copy("ConfigService.Web.config", "ConfigService.Web.config.test", true);
            var editor = new XPath();
            editor.XmlFilePath = "ConfigService.Web.config.test";

            var actual = editor.SelectSingleAttribute("configuration/connectionStrings/add[@name=\"configStore\"]/@connectionString");
            var expected = @"Data Source=(localdb)\Projects;Initial Catalog=DirectConfig;Integrated Security=SSPI;";
            Assert.Equal(expected, actual);

            editor.SetSingleAttribute("configuration/connectionStrings/add[@name=\"configStore\"]/@connectionString",
               @"Data Source=(localdb)\Projects;Initial Catalog=DirectConfig;User ID=nhindUser;Password=nhindUser!10");


            actual = editor.SelectSingleAttribute("configuration/connectionStrings/add[@name=\"configStore\"]/@connectionString");
            expected = @"Data Source=(localdb)\Projects;Initial Catalog=DirectConfig;User ID=nhindUser;Password=nhindUser!10";

            Assert.Equal(expected, actual);


            actual = editor.SelectSingleAttribute("configuration/connectionStrings/add[@name=\"configStore\"]/@Unknown");
            expected = null;

            Assert.Equal(expected, actual);
        }


        [Fact]
        public void AddingNodes_Test()
        {

            File.Copy("SmtpAgentConfig.xml", "SmtpAgentConfig.xml.test", true);
            var editor = new XPath();
            editor.XmlFilePath = "SmtpAgentConfig.xml.test";

            //
            // Ensure I am using it right
            //
            var actual = editor.SelectSingleAttribute("/SmtpAgentConfig/DomainManager/Url");
            var expected = "http://localhost/ConfigService/DomainManagerService.svc/Domains";
            Assert.Equal(expected, actual);

            actual = editor.SelectSingleAttribute("/SmtpAgentConfig/MdnMonitor/Url");
            expected = null;
            Assert.Equal(expected, actual);

            editor.CreateFragment("/SmtpAgentConfig/MdnMonitor/Url");

            editor.SetSingleAttribute("/SmtpAgentConfig/MdnMonitor/Url", @"http://localhost/ConfigService/MonitorService.svc/Dispositions");

            actual = editor.SelectSingleAttribute("/SmtpAgentConfig/MdnMonitor/Url");
            expected = @"http://localhost/ConfigService/MonitorService.svc/Dispositions";
            Assert.Equal(expected, actual);
        }


        [Fact]
        public void DeletingNodes_Test()
        {

            File.Copy("SmtpAgentConfig.xml", "SmtpAgentConfig.xml.test", true);
            var editor = new XPath();
            editor.XmlFilePath = "SmtpAgentConfig.xml.test";

            editor.CreateFragment("/SmtpAgentConfig/MdnMonitor/Url");

            editor.SetSingleAttribute("/SmtpAgentConfig/MdnMonitor/Url", @"http://localhost/ConfigService/MonitorService.svc/Dispositions");

            var actual = editor.SelectSingleAttribute("/SmtpAgentConfig/MdnMonitor/Url");
            var expected = @"http://localhost/ConfigService/MonitorService.svc/Dispositions";
            Assert.Equal(expected, actual);

            editor.DeleteFragment("/SmtpAgentConfig/MdnMonitor");
            actual = editor.SelectSingleAttribute("/SmtpAgentConfig/MdnMonitor");
            Assert.Null(actual);
        }


        [Fact]
        public void ReplaceFragment_Test()
        {

            File.Copy("SmtpAgentConfig.xml", "SmtpAgentConfig.xml.test", true);
            var anchorsPlugin = @"<Anchors>
<PluginResolver>
  <!-- NEW Resolver that COMBINES Anchors from multiple sources into a single list-->
  <Definition>
    <TypeName>Health.Direct.ResolverPlugins.MultiSourceAnchorResolver, Health.Direct.ResolverPlugins</TypeName>
    <Settings>
      <!-- New Bundle Resolver -->
      <BundleResolver>
        <ClientSettings>
          <Url>http://localhost/ConfigService/CertificateService.svc/Bundles</Url>
        </ClientSettings>
        <CacheSettings>
          <Cache>true</Cache>
          <NegativeCache>true</NegativeCache>
          <!-- Set cache to longer duration in production -->
          <CacheTTLSeconds>60</CacheTTLSeconds>
        </CacheSettings>
        <MaxRetries>1</MaxRetries>
        <Timeout>30000</Timeout> <!-- In milliseconds -->
        <VerifySSL>true</VerifySSL>
      </BundleResolver>
      <!-- Standard Resolver that pulls from Anchor store -->
      <ServiceResolver>
        <ClientSettings>
          <Url>http://localhost/ConfigService/CertificateService.svc/Anchors</Url>
        </ClientSettings>
        <CacheSettings>
          <Cache>true</Cache>
          <NegativeCache>true</NegativeCache>
          <CacheTTLSeconds>60</CacheTTLSeconds>
        </CacheSettings>
      </ServiceResolver>
    </Settings>
  </Definition>
</PluginResolver>    
</Anchors>";

            var xpath = "/SmtpAgentConfig/Anchors";
            var editor = new XPath();
            editor.XmlFilePath = "SmtpAgentConfig.xml.test";

            //var original = editor.SelectSingleAttribute("xpath");

            //Act
            editor.ReplaceFragment(xpath, anchorsPlugin);


            //Assert
            var updatedDocument = new XmlDocument();
            updatedDocument.Load("SmtpAgentConfig.xml.test");

            var updatedAnchors = updatedDocument.SelectSingleNode(xpath);
            Assert.NotNull(updatedAnchors);

            var settings = new XmlWriterSettings();
            settings.NewLineChars = string.Empty;
            settings.Indent = false;
            settings.IndentChars = "";
            settings.ConformanceLevel = ConformanceLevel.Auto;

            string actualFragment;
            using (var stringWriter = new StringWriter())
            {
                using (XmlWriter writer = XmlWriter.Create(stringWriter, settings))
                {
                    updatedAnchors.WriteTo(writer);
                    writer.Flush();
                    actualFragment = stringWriter.ToString();
                }
            }

            string expectedFragment;
            using (var stringWriter = new StringWriter())
            {
                using (XmlWriter writer = XmlWriter.Create(stringWriter, settings))
                {
                    XmlNode newNode = updatedDocument.CreateDocumentFragment();
                    newNode.InnerXml = anchorsPlugin;
                    newNode.WriteTo(writer);
                    writer.Flush();
                    expectedFragment = stringWriter.ToString();
                }
            }

            var cleanExpectedFragment = new XmlDocument();
            cleanExpectedFragment.LoadXml(expectedFragment);
            expectedFragment = cleanExpectedFragment.OuterXml;

            Assert.Equal(expectedFragment, actualFragment);
        }

        [Fact]
        public void ReplaceFragment_Empty_Test()
        {

            File.Copy("SmtpAgentConfig.xml", "SmtpAgentConfig.xml.test", true);
            var emptyFragment = @"";

            var xpath = "/SmtpAgentConfig/Domain";
            var editor = new XPath();
            editor.XmlFilePath = "SmtpAgentConfig.xml.test";

            //var original = editor.SelectSingleAttribute("xpath");
            var originalDocument = new XmlDocument();
            originalDocument.Load("SmtpAgentConfig.xml.test");
            var domainNode = originalDocument.SelectSingleNode(xpath);
            Assert.NotNull(domainNode);

            //Act
            editor.ReplaceFragment(xpath, emptyFragment);


            //Assert
            var updatedDocument = new XmlDocument();
            updatedDocument.Load("SmtpAgentConfig.xml.test");

            domainNode = updatedDocument.SelectSingleNode(xpath);
            Assert.Null(domainNode);
        }

        /// <summary>
        /// Fragement does not exist so create a container for it.
        /// </summary>
        [Fact]
        public void CreateFragmentBefore_Create_Test()
        {

            File.Copy("SmtpAgentConfig.xml", "SmtpAgentConfig.xml.test", true);
            var domainsFragement = @"<Domains><ServiceResolver><AgentName>SmtpAgent1</AgentName><ClientSettings><Url>http://localhost/ConfigService/DomainManagerService.svc/Domains</Url></ClientSettings><CacheSettings><Cache>true</Cache><CacheTTLSeconds>20</CacheTTLSeconds></CacheSettings></ServiceResolver></Domains>";

            var xpath = "/SmtpAgentConfig/Domains";
            IPath editor = new XPath();
            editor.XmlFilePath = "SmtpAgentConfig.xml.test";

            //var original = editor.SelectSingleAttribute("xpath");

            //Act
            editor.CreateFragmentBefore(domainsFragement, "//DomainManager");


            //Assert
            var updatedDocument = new XmlDocument();
            updatedDocument.Load("SmtpAgentConfig.xml.test");

            var updatedAnchors = updatedDocument.SelectSingleNode(xpath);
            Assert.NotNull(updatedAnchors);

            var settings = new XmlWriterSettings();
            settings.NewLineChars = string.Empty;
            settings.Indent = false;
            settings.IndentChars = "";
            settings.ConformanceLevel = ConformanceLevel.Auto;

            string actualFragment;
            using (var stringWriter = new StringWriter())
            {
                using (var writer = XmlWriter.Create(stringWriter, settings))
                {
                    updatedAnchors.WriteTo(writer);
                    writer.Flush();
                    actualFragment = stringWriter.ToString();
                }
            }

            string expectedFragment;
            using (var stringWriter = new StringWriter())
            {
                using (var writer = XmlWriter.Create(stringWriter, settings))
                {
                    XmlNode newNode = updatedDocument.CreateDocumentFragment();
                    newNode.InnerXml = domainsFragement;
                    newNode.WriteTo(writer);
                    writer.Flush();
                    expectedFragment = stringWriter.ToString();
                }
            }

            var cleanExpectedFragment = new XmlDocument();
            cleanExpectedFragment.LoadXml(expectedFragment);
            expectedFragment = cleanExpectedFragment.OuterXml;

            Assert.Equal(expectedFragment, actualFragment);
        }

        /// <summary>
        /// Fragement does not exist so create a container for it.
        /// </summary>
        [Fact]
        public void CreateFragmentBefore_Create_Test2()
        {
            File.Copy("DirectDnsResponderSvc.exe.config", "DirectDnsResponderSvc.exe.config.test", true);
            var editor = new XPath();
            editor.XmlFilePath = "DirectDnsResponderSvc.exe.config.test";

            var domainsFragement = "<section name=\"CertPolicyServiceResolverSettings\" type=\"Surescripts.Health.Direct.DnsResponder.CertPolicyServiceResolverSettingsSection, Surescripts.Health.Direct.DnsResponder\" allowLocation=\"true\" allowDefinition=\"Everywhere\"/>";
            

            //var original = editor.SelectSingleAttribute("xpath");

            //Act
            editor.CreateFullFragment(domainsFragement, @"/configuration/configSections/sectionGroup");

            
        }


        [Fact]
        public void GetDomain_Test()
        {

            File.Copy("SmtpAgentConfig.xml", "SmtpAgentConfig.xml.test", true);
            var editor = new XPath();
            editor.XmlFilePath = "SmtpAgentConfig.xml.test";

            var actual = editor.SelectSingleAttribute("/SmtpAgentConfig/Domain");
            var expected = "Direct.North.Hobo.Lab";
            Assert.Equal(expected, actual);

            editor.SetSingleAttribute("/SmtpAgentConfig/Domain",
               "Direct.South.Hobo.Lab");

            actual = editor.SelectSingleAttribute("/SmtpAgentConfig/Domain");
            expected = "Direct.South.Hobo.Lab";
            Assert.Equal(expected, actual);
        }

    }
}
