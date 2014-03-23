/* 
 Copyright (c) 2012, Direct Project
 All rights reserved.

 Authors:
    Sean Nolan      sean.nolan@microsoft.com
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
using System.Net;
using System.Security.Cryptography.X509Certificates;
using System.Security.Cryptography.Pkcs;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Web;

namespace Health.Direct.Common.Certificates
{
    /// <summary>
    /// Downloads an Anchor Bundle from a given URL
    /// </summary>
    public class AnchorBundleDownloader
    {
        int m_maxRetries;

        /// <summary>
        /// Create a new Anchor Bundle Downloader
        /// </summary>
        public AnchorBundleDownloader()
        {
            this.VerifySSL = true;
        }
        
        /// <summary>
        /// Default is true. You can set this to false if you want to bypass SSL checking for TEST sites
        /// </summary>
        public bool VerifySSL
        {
            get
            {
                return (ServicePointManager.ServerCertificateValidationCallback != null);
            }
            set
            {
                if (value)
                {
                    ServicePointManager.ServerCertificateValidationCallback = delegate { return (true); };
                }
                else
                {
                    ServicePointManager.ServerCertificateValidationCallback = null;
                }
            }
        }

        /// <summary>
        /// Timeout in milliseconds. Default is 0, which causes us to use the WebClient's default timeout
        /// </summary>
        public int TimeoutMS
        {
            get; set;
        }

        /// <summary>
        /// In case of failures. Default is 1
        /// </summary>                
        public int MaxRetries
        {
            get { return m_maxRetries; }
            set
            {
                if (value < 0)
                {
                    throw new ArgumentException("value < 0");
                }
                m_maxRetries = value;
            }
        }
        
        /// <summary>
        /// Download raw bundle bytes
        /// </summary>
        /// <param name="bundleUri">Uri from where to retreive the bundle</param>
        /// <returns>Byte array</returns>
        public byte[] DownloadRaw(Uri bundleUri)
        {
            if (bundleUri == null)
            {
                throw new ArgumentNullException("bundleUri");
            }
            
            using (WebDownloader client = new WebDownloader())
            {
                client.TimeoutMS = this.TimeoutMS;
                client.MaxRetries = m_maxRetries;

                return client.DownloadDataWithRetry(bundleUri);
            }
        }

        /// <summary>
        /// Downloads an Anchor bundle from the given Uri and saves it to a file
        /// </summary>
        /// <param name="bundleUri">Uri from where to retreive the bundle</param>
        /// <param name="filePath">Save bundle to file</param>
        public void DownloadToFile(Uri bundleUri, string filePath)
        {
            if (string.IsNullOrEmpty(filePath))
            {
                throw new ArgumentException("filePath");
            }
            
            byte[] bytes = this.DownloadRaw(bundleUri);
            File.WriteAllBytes(filePath, bytes);
        }
        
        /// <summary>
        /// Downloads an Anchor bundle from the given Uri
        /// </summary>
        /// <param name="bundleUri">Uri from where to retreive the bundle</param>
        /// <returns>Anchor Bundle</returns>
        public AnchorBundle Download(Uri bundleUri)
        {
            if (bundleUri == null)
            {
                throw new ArgumentNullException("bundleUri");
            }
            
            byte[] bundleData = this.DownloadRaw(bundleUri);
            if (bundleData.IsNullOrEmpty())
            {
                throw new InvalidOperationException("Empty bytes received");
            }
            
            return new AnchorBundle(bundleData);
        }

        /// <summary>
        /// Downloads the bundle and returns the certificates contained within.
        /// Does not validate signatures
        /// </summary>
        /// <param name="bundleUri">Uri from where to retreive the bundle</param>
        /// <returns>Collection of X509 Certificates</returns>
        public X509Certificate2Collection DownloadCertificates(Uri bundleUri)
        {
            AnchorBundle bundle = this.Download(bundleUri);
            return bundle.Certificates;
        }
    }
}
