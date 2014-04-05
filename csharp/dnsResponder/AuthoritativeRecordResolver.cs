/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Ali Emami     aliemami@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Net;

using Health.Direct.Common.DnsResolver;
using Health.Direct.Common.Dns;

namespace Health.Direct.DnsResponder
{
    /// <summary>
    /// Resolves DNS requests using the authoritative (root) nameserver for the requested domain. 
    /// </summary>
    public class AuthoritativeRecordResolver : IDnsStore
    {
        bool m_doCache; 
        TimeSpan m_timeoutMilliseconds;
        int m_dnsResolutionPort;
        List<IPEndPoint> m_primaryDnsServers;
        int m_maxNameServersToAttempt = 3; 

        /// <summary>
        /// Constructs the resolver from settings.
        /// </summary>        
        public AuthoritativeRecordResolver(AuthoritativeResolutionSettings settings)
        {
            if (settings == null)
            {
                throw new ArgumentNullException("settings"); 
            }

            if (settings.TimeoutMilliseconds < 1)
            {
                throw new ArgumentOutOfRangeException("TimeoutMilliseconds"); 
            }

            if (settings.DnsResolutionPort < 1)
            {
                throw new ArgumentOutOfRangeException("DnsResolutionPort"); 
            }

            m_doCache = settings.Cache;
            m_timeoutMilliseconds = new TimeSpan(0, 0, 0, 0, settings.TimeoutMilliseconds);
            m_dnsResolutionPort = settings.DnsResolutionPort;
            ParsePrimaryNameServers(settings.PrimaryNameServer);            
        }

        /// <summary>
        /// True to cache responses.
        /// </summary>
        public bool Cache
        {
            get
            {
                return m_doCache;
            }
        }

        /// <summary>
        /// Timeout for DNS queries that are executed.
        /// </summary>
        public TimeSpan Timeout
        {
            get
            {
                return m_timeoutMilliseconds; 
            }            
        }

        /// <summary>
        /// Port used for making DNS queries.
        /// </summary>
        public int DnsResolutionPort
        {
            get
            {
                return m_dnsResolutionPort;
            }
        }

        /// <summary>
        /// The name servers used to find the authoritative name servers for a DNS query.
        /// </summary>
        public IList<IPEndPoint> PrimaryNameServers
        {
            get
            {
                return m_primaryDnsServers;
            }
        }

        private void ParsePrimaryNameServers(DnsIPEndpointSettings[] primaryDnsServers)
        {
            if (primaryDnsServers == null)
            {
                throw new ArgumentNullException("primaryDnsServers");
            }

            if (primaryDnsServers.Length < 1)
            {
                throw new ArgumentException("NoPrimaryDns");
            }

            foreach (DnsIPEndpointSettings endpoint in primaryDnsServers)
            {
                if (m_primaryDnsServers == null)
                {
                    m_primaryDnsServers = new List<IPEndPoint>(primaryDnsServers.Length);
                }

                m_primaryDnsServers.Add(new IPEndPoint(IPAddress.Parse(endpoint.Address), endpoint.Port));
            }
        }

        /// <summary>
        /// Handle the DNS query.
        /// </summary>        
        public DnsResponse Get(DnsRequest request)
        {
            if (request == null)
            {
                throw new ArgumentNullException("request"); 
            }

            DnsQuestion question = request.Question;
            if (question == null || question.Class != DnsStandard.Class.IN)
            {
                throw new DnsServerException(DnsStandard.ResponseCode.NotImplemented);
            }

            /* Don't handle NS and SOA record recursilvey since these are retrieved from the primary
             * DNS servers and are cached. So, we would always end up retrieving the cached values 
             * from the primary DNS servers. */
            if (request.Question.Type != DnsStandard.RecordType.ANAME &&
                request.Question.Type != DnsStandard.RecordType.CERT &&
                request.Question.Type != DnsStandard.RecordType.CNAME &&
                request.Question.Type != DnsStandard.RecordType.MX)
            {
                throw new DnsServerException(DnsStandard.ResponseCode.NotImplemented);
            }

            DnsResponse response = ProcessRequest(request);

            return response; 
        }        

        DnsResponse ProcessRequest(DnsRequest request)
        {
            IEnumerable<IPAddress> nameServers = GetNameServers(request.Question.Domain);
            
            if (nameServers == null)
            {
                throw new DnsServerException(DnsStandard.ResponseCode.NameError); 
            }

            bool useUdp = request.Question.Type == DnsStandard.RecordType.CERT ? false : true;

            int count = 0; 
            DnsResponse response = null; 
            foreach (IPAddress nameserver in nameServers)
            {                 
                DnsClient client = null;
                try
                {
                    if (m_doCache)
                    {
                        client = new DnsClientWithCache(new IPEndPoint(nameserver, m_dnsResolutionPort));
                    }
                    else
                    {
                        client = new DnsClient(new IPEndPoint(nameserver, m_dnsResolutionPort));
                    }

                    client.Timeout = Timeout;
                    client.UseUDPFirst = useUdp;
                    DnsRequest newRequest = new DnsRequest(new DnsQuestion(request.Question));
                    response = client.Resolve(newRequest);

                    if (response != null)
                    {
                        // Clone the response before returning it since the response may be cached
                        // and we don't want the cached response to be modified.
                        response = response.Clone();
                        
                        // updates the TTL of records to reflect the elapsed time since the 
                        // record was cached. 
                        response.UpdateRecordsTTL(); 
                        break;
                    }
                }
                catch (DnsException)
                {
                    continue; 
                }
                finally
                {
                    if (client != null)
                    {
                        client.Dispose();
                    }
                }

                count++;
                if (count > m_maxNameServersToAttempt)
                {
                    break; 
                }
            }

            if (response == null)
            {
                throw new DnsServerException(DnsStandard.ResponseCode.ServerFailure); 
            }

            response.RequestID = request.RequestID;            

            return response; 
        }        

        private IEnumerable<IPAddress> GetNameServers(string domain)
        {
            IEnumerable<IPAddress> nameServers = null;
            foreach (IPEndPoint endpoint in m_primaryDnsServers)
            {
                DnsClient client = null;
                try
                {
                    if (m_doCache)
                    {
                        client = new DnsClientWithCache(endpoint);
                    }
                    else
                    {
                        client = new DnsClient(endpoint);
                    }

                    client.Timeout = Timeout;
                    nameServers = client.GetNameServers(domain);

                    if (nameServers == null)
                    {
                        continue;
                    }

                    foreach (IPAddress nameServer in nameServers)
                    {
                        if (nameServer != IPAddress.None)
                        {
                            return nameServers;
                        }
                    }                    
                }
                finally
                {
                    if (client != null)
                    {
                        client.Dispose(); 
                    }
                }
            }

            return null;
        }
    }
}
