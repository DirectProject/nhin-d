/* 
 Copyright (c) 2012, Direct Project
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
using System.Linq;
using System.Text;
using System.Net;

namespace Health.Direct.Common.Web
{
    /// <summary>
    /// Enhances WebClient to configure Timeouts and Retries
    /// </summary>
    public class WebDownloader : WebClient
    {
        int m_maxRetries;
        
        /// <summary>
        /// Create a new downloader
        /// </summary>
        public WebDownloader()
            : base()
        {
            m_maxRetries = 1;
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
            get
            {
                return m_maxRetries;
            }
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
        /// Download bytes
        /// </summary>
        /// <param name="uri">uri to download</param>
        /// <returns>data</returns>
        public byte[] DownloadDataWithRetry(Uri uri)
        {
            int attempt = 0;
            int maxAttempts = m_maxRetries + 1;
            while (attempt < maxAttempts)
            {
                ++attempt;
                try
                {
                    byte[] data = this.DownloadData(uri);
                    return data;
                }
                catch
                {
                    if (attempt >= maxAttempts)
                    {
                        throw;
                    }
                }
            }            
            
            throw new WebException("Max attempts reached");
        }
        
        /// <summary>
        /// Create a new WebRequest. See documentation for System.Net.WebClient for details.
        /// </summary>
        /// <param name="address">Uri for the resource</param>
        /// <returns>WebRequest</returns>
        protected override WebRequest GetWebRequest(Uri address)
        {
            WebRequest request = base.GetWebRequest(address);
            if (this.TimeoutMS > 0)
            {
                request.Timeout = this.TimeoutMS;
            }
            
            return request;
        }
    }
}
