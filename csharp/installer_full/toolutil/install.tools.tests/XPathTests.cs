using System.IO;
using Xunit;

namespace install.tools.tests
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

        [Fact]
        public void ConnectAndDrop()
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
    }
}
