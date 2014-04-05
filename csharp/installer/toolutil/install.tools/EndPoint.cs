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
        bool TestConnection(string endpoint, string expectedFragment);
    }

    [ComVisible(true), GuidAttribute("12A4410A-E00B-42b7-988D-28F73FADFC00")]
    [ProgId("Direct.Installer.EndPointTools")]
    [ClassInterface(ClassInterfaceType.None)]
    public class EndPoint : IEndPoint
    {
        public bool TestWcfSoapConnection(string endpoint)
        {
            if(endpoint.Trim().Length == 0)
            {
                return false;
            }

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

                return results.Contains(GetUrlPath(endpoint), StringComparison.OrdinalIgnoreCase);
            }
            catch
            {
                return false;
            }
        }

        public bool TestConnection(string endpoint, string expectedFragment)
        {
            HttpWebRequest request = WebRequest.Create(endpoint) as HttpWebRequest;
            request.Method = "GET";
            request.ContentType = "text/xml; charset=utf-8";

            try
            {
                HttpWebResponse response = request.GetResponse() as HttpWebResponse;
                StreamReader reader = new StreamReader(response.GetResponseStream());

                string results = reader.ReadToEnd();

                reader.Close();
                response.Close();

                return results.Contains(expectedFragment, StringComparison.OrdinalIgnoreCase);
            }
            catch
            {
                return false;
            }
        }

        private string GetUrlPath(string endpoint)
        {
           Uri uri = new Uri(endpoint);
           return uri.PathAndQuery;
        }


        private string GetServiceAddress(string endpoint)
        {
            Regex regex = new Regex(@"^.+\.svc");
            string svcEndPoint = regex.Match(endpoint).Value;
            return svcEndPoint;
        }
    }
}
