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
using System.Net;

using Health.Direct.Common.DnsResolver;

namespace Health.Direct.DnsResponder
{
    /// <summary>
    /// Trivial dns store - relays all calls to another Dns server
    /// Used for Testing
    /// </summary>
    public class ProxyStore : IDnsStore
    {
        IPAddress m_serverIP;
        TimeSpan? m_timeout;
        
        public ProxyStore(string serverIP)
        {
            m_serverIP = IPAddress.Parse(serverIP);
        }
        
        public TimeSpan Timeout
        {
            get
            {
                return (m_timeout != null) ? m_timeout.Value : DnsClient.DefaultTimeout;
            }
            set
            {
                m_timeout = value;
            }
        }
        
        public DnsResponse Get(DnsRequest request)
        {
            ushort requestID = request.RequestID;
            DnsResponse response = null;
            try
            {
                using (DnsClient client = new DnsClient(m_serverIP))
                {
                    if (m_timeout != null)
                    {
                        client.Timeout = m_timeout.Value;
                    }
                    response = client.Resolve(request);
                }
            }
            finally
            {
                if (response != null)
                {
                    response.RequestID = requestID;
                }            
                request.RequestID = requestID;
            }
            return response;
        }
    }
}