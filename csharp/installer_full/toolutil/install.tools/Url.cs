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


using System;
using System.Runtime.InteropServices;


namespace Health.Direct.Install.Tools
{

    [ComVisible(true), GuidAttribute("E28695C2-4899-43b6-ADD6-6CA2AE30D175")]
    [InterfaceType(ComInterfaceType.InterfaceIsDual)]
    public interface IUrl
    {
        string FullUrl { get; }
        string Host(string url);
        string Port(string url);
        string HostPort(string url);
        string Scheme(string url);

        IUrl UpdateUrlHost(string url, string host);

        bool ValidUrl(string url);
    }

    [ComVisible(true), GuidAttribute("59934DE5-BEAF-4462-A06E-4047F64AAF3D")]
    [ProgId("Direct.UrlTools")]
    [ClassInterface(ClassInterfaceType.None)]
    public class Url : IUrl
    {
        private string _url = "";
        private const int DEFAULT_PORT = 80;
        private const int DEFAULT_SECURE_PORT = 443;
        private const string DEFAULT_SCHEME = "http";
        private const string DEFAULT_SECURE_SCHEME = "https";

        public string FullUrl
        {
            get { return _url; } 
        }
        
        /// <summary>
        /// Return the Host of a well formed Uri.
        /// If the Url is not well formed return empty string.
        /// </summary>
        /// <param name="url"></param>
        /// <returns></returns>
        public string Host(string url)
        {
            try
            {
                Uri uri = new Uri(url);
                return uri.Host;
            }
            catch(UriFormatException ex)
            {
                return string.Empty;
            }
        }

        /// <summary>
        /// Return the Port of a well formed Uri.
        /// If the Url is not well formed return empty string.
        /// </summary>
        /// <param name="url"></param>
        /// <returns></returns>
        public string Port(string url)
        {
            try
            {
                Uri uri = new Uri(url);
                return uri.Port.ToString();
            }
            catch (UriFormatException ex)
            {
                return string.Empty;
            }
        }

        /// <summary>
        /// Return the Host and Port of a well formed Uri.
        /// If the Url is not well formed return empty string.
        /// </summary>
        /// <param name="url"></param>
        /// <returns></returns>
        public string HostPort(string url)
        {
            try
            {
                Uri uri = new Uri(url);
                switch (uri.Scheme)
                {
                    case DEFAULT_SCHEME:
                        if (uri.Port == DEFAULT_PORT)
                        {
                            return uri.Host;
                        }
                        break;
                    case DEFAULT_SECURE_SCHEME:
                        if (uri.Port == DEFAULT_SECURE_PORT)
                        {
                            return uri.Host;
                        }
                        break;
                }
                return uri.Host + ":" + uri.Port;
            }
            catch (UriFormatException ex)
            {
                return string.Empty;
            }
        }

        /// <summary>
        /// Return the Scheme of a well formed Uri
        /// If the Url is not well formed return empty string.
        /// </summary>
        /// <param name="url"></param>
        /// <returns></returns>
        public string Scheme(string url)
        {
            try
            {
                Uri uri = new Uri(url);
                return uri.Scheme;
            }
            catch (UriFormatException ex)
            {
                return string.Empty;
            }
        }

        /// <summary>
        /// Update a well formed url to a new host name.
        /// </summary>
        /// <param name="url"></param>
        /// <param name="host"></param>
        /// <returns></returns>
        public IUrl UpdateUrlHost(string url, string host)
        {
            Uri uri = new Uri(url);
            int port;
            string[] hostParts = host.Split(':');
            if (hostParts.Length == 2)
            {
                host = hostParts[0];
                port = int.Parse(hostParts[1]);
            }
            else
            {
                switch (uri.Scheme)
                {
                    case DEFAULT_SECURE_SCHEME:
                        port = DEFAULT_SECURE_PORT;
                        break;
                    default:
                        port = DEFAULT_PORT;
                        break;
                }
            }
            return UpdateUrlHost(url, host, port);
        }

        private IUrl UpdateUrlHost(string url, string host, int port)
        {
            Uri uri = new Uri(url);
            UriBuilder uriBuilder = new UriBuilder(uri.Scheme, host, port, uri.PathAndQuery);
            _url = uriBuilder.Uri.ToString();
            return this;
        }

        public bool ValidUrl(string url)
        {
            try
            {
                Uri uri = new Uri(url);
            }
            catch (Exception ex)
            {
                return false;
            }
            return true;
        }
    }
}

