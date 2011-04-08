/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    JoeShook@Gmail.com
   
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.IO;
using Xunit;

namespace Health.Direct.Install.Tools.tests
{
    public class XPathTests
    {


        [Fact]
        public void QueryTest()
        {
            Xpath editor = new Xpath();
            editor.XmlFilePath = "DirectDnsResponderSvc.exe.config";

            string value = editor.SelectSingleAttribute(@"/configuration/ServiceSettingsGroup/RecordRetrievalServiceSettings/@Url");
            string expected = @"http://localhost/DnsService/RecordRetrievalService.svc/Records";

            Assert.Equal(expected, value);
        }

        /// <summary>
        /// Query and update the XPath location. 
        /// </summary>
        [Fact]
        public void ConnectAndDropDirectDnsResponder_Test()
        {
            
            File.Copy("DirectDnsResponderSvc.exe.config", "DirectDnsResponderSvc.exe.config.test", true);
            Xpath editor = new Xpath();
            editor.XmlFilePath = "DirectDnsResponderSvc.exe.config.test";

            string value = editor.SelectSingleAttribute(@"/configuration/ServiceSettingsGroup/RecordRetrievalServiceSettings/@Url");
            string expected = @"http://localhost/DnsService/RecordRetrievalService.svc/Records";
            Assert.Equal(expected, value);

            editor.SetSingleAttribute(@"/configuration/ServiceSettingsGroup/RecordRetrievalServiceSettings/@Url",
               @"http://SomeServer/DnsService/RecordRetrievalService.svc/Records" );


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
            Xpath editor = new Xpath();
            editor.XmlFilePath = "ConfigService.Web.config.test";

            string actual = editor.SelectSingleAttribute("configuration/connectionStrings/add[@name=\"configStore\"]/@connectionString");
            string expected = @"Data Source=.\SQLEXPRESS;Initial Catalog=DirectConfig;Integrated Security=SSPI;";
            Assert.Equal(expected, actual);

            editor.SetSingleAttribute("configuration/connectionStrings/add[@name=\"configStore\"]/@connectionString",
               @"Data Source=.\SQLEXPRESS;Initial Catalog=DirectConfig;User ID=nhindUser;Password=nhindUser!10");


            actual = editor.SelectSingleAttribute("configuration/connectionStrings/add[@name=\"configStore\"]/@connectionString");
            expected = @"Data Source=.\SQLEXPRESS;Initial Catalog=DirectConfig;User ID=nhindUser;Password=nhindUser!10";

            Assert.Equal(expected, actual);
        }

    }
}
