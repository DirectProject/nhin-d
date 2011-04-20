using System;
using System.IO;
using System.Net;
using System.Runtime.InteropServices;
using System.Text.RegularExpressions;


namespace Health.Direct.Install.Tools
{
    [ComVisible(true), GuidAttribute("718716E9-758D-4420-A75B-20EBA4BEFCDC")]
    [InterfaceType(ComInterfaceType.InterfaceIsDual)]
    public interface IEndPoint
    {
        bool TestWcfSoapConnection(string endpoint);
    }

    [ComVisible(true), GuidAttribute("12A4410A-E00B-42b7-988D-28F73FADFC00")]
    [ProgId("Direct.Installer.EndPointTools")]
    [ClassInterface(ClassInterfaceType.None)]
    public class EndPoint : IEndPoint
    {
        public bool TestWcfSoapConnection(string endpoint)
        {
            string svcEndPoint = GetServiceAddress(endpoint);
            string wsdlUrl = svcEndPoint + "?wsdl";
            HttpWebRequest request = WebRequest.Create(wsdlUrl) as HttpWebRequest;
            request.Method = "GET";
            request.ContentType = "text/xml; charset=utf-8";

            try
            {

                HttpWebResponse response = request.GetResponse() as HttpWebResponse;
                StreamReader reader = new StreamReader(response.GetResponseStream());

                string results = reader.ReadToEnd();

                reader.Close();
                response.Close();

                return results.Contains(endpoint, StringComparison.OrdinalIgnoreCase)
                    || results.Contains(GetHostName(endpoint), StringComparison.OrdinalIgnoreCase);
            }
            catch
            {
                return false;
            }
        }

        private string GetHostName(string endpoint)
        {
            if(endpoint.Contains("localhost", StringComparison.OrdinalIgnoreCase))
            {
                string fqdn = Dns.GetHostByName("LocalHost").HostName;
                string result = Regex.Replace(endpoint, "localhost", fqdn, RegexOptions.IgnoreCase);
                return result;
            }
            return endpoint;
        }

        private string GetServiceAddress(string endpoint)
        {
            Regex regex = new Regex(@"^.+\.svc");
            string svcEndPoint = regex.Match(endpoint).Value;
            return svcEndPoint;
        }
    }
}
