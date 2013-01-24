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
using System.Collections.Generic;
using System.Net;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;

using Health.Direct.Common.Caching;
using Health.Direct.Common.Dns;
using Health.Direct.Common.DnsResolver;

namespace Health.Direct.Common.Certificates
{
    /// <summary>
    /// Implements a certificate resolver using DNS CERT records. Supports the concept of a fallback
    /// domain.
    /// </summary>
    public class DnsCertResolver : ICertificateResolver
    {
        private static readonly TimeSpan DefaultTimeout = TimeSpan.FromSeconds(5);
        IPAddress m_serverIP;
        string m_fallbackDomain = string.Empty;
        bool m_cacheEnabled = false;
        bool m_useRootForCertResolve = false;
        TimeSpan m_timeout;
        int m_maxRetries = 1;
        
        /// <summary>
        /// Create a DNS certificate resolver, using default timeout
        /// </summary>
        /// <param name="serverIP">An <see cref="IPAddress"/> instance providing the IP address of the DNS server</param>
        public DnsCertResolver(IPAddress serverIP)
            : this(serverIP, DefaultTimeout)
        {
        }

        /// <summary>
        /// Create a DNS certificate resolver.
        /// </summary>
        /// <param name="serverIP">An <see cref="IPAddress"/> instance providing the IP address of the DNS server</param>
        /// <param name="timeout">Timeout value</param>
        public DnsCertResolver(IPAddress serverIP, TimeSpan timeout)
            : this(serverIP, timeout, null, false)
        {
        }

        /// <summary>
        /// Creates a DNS certificate resolver with a custom timeout and a fallback domain.
        /// </summary>
        /// <param name="serverIP">An <see cref="IPAddress"/> instance providing the IP address of the DNS server</param>
        /// <param name="timeout">Timeout value</param>
        /// <param name="fallbackDomain">A fallback domain name to try if the main domain name is not resolved.</param>
        public DnsCertResolver(IPAddress serverIP
                               , TimeSpan timeout
                               , string fallbackDomain) : this(serverIP, timeout,fallbackDomain, false)
        {
        }
        
        /// <summary>
        /// Creates a DNS certificate resolver with a custom timeout and a fallback domain.
        /// </summary>
        /// <param name="serverIP">An <see cref="IPAddress"/> instance providing the IP address of the DNS server</param>
        /// <param name="timeout">Timeout value</param>
        /// <param name="fallbackDomain">A fallback domain name to try if the main domain name is not resolved.</param>
        /// <param name="cacheEnabled">boolean flag indicating whether or not to use the client with cache</param>
        public DnsCertResolver(IPAddress serverIP
                               , TimeSpan timeout
                               , string fallbackDomain
                               , bool cacheEnabled)
        {
            if (serverIP == null)
            {
                throw new ArgumentNullException("serverIP");
            }
            if (timeout.Ticks < 0)
            {
                throw new ArgumentException("timeout value was less than zero", "timeout");
            }

            m_serverIP = serverIP;
            m_timeout = timeout;
            m_fallbackDomain = fallbackDomain;
            m_cacheEnabled = cacheEnabled;
        }

        /// <summary>
        /// Event to subscribe to for notification of errors.
        /// </summary>
        public event Action<ICertificateResolver, Exception> Error;
        
        /// <summary>
        /// The DNS <see cref="IPAddress"/> resolved against.
        /// </summary>
        public IPAddress Server
        {
            get
            {
                return m_serverIP;
            }
        }
        
        /// <summary>
        /// Gets if this instance has a fallback domain.
        /// </summary>
        public bool HasFallbackDomain
        {
            get
            {
                return !string.IsNullOrEmpty(m_fallbackDomain);
            }            
        }
        
        /// <summary>
        /// Timeout in milliseconds.
        /// </summary>
        public TimeSpan Timeout
        {
            get
            {
                return m_timeout;
            }
        }
        
        /// <summary>
        /// Number of retries to attempt.
        /// </summary>
        public int MaxRetries
        {
            get
            {
                return m_maxRetries;
            }
            set
            {
                if (value < 1)
                {
                    throw new ArgumentException("value was less than 1", "value");
                }
                
                m_maxRetries = value;
            }
        }

        /// <summary>
        /// Fallback domain for this resolver instance.
        /// </summary>
        public string FallbackDomain
        {
            get
            {
                return m_fallbackDomain;
            }
        }
        
        /// <summary>
        /// Resolve certs directly from the root server
        /// </summary>
        public bool ResolveUsingRootServer
        {
            get
            {
                return m_useRootForCertResolve;
            }
            set
            {
                m_useRootForCertResolve = value;
            }
        }
        
        /// <summary>
        /// Resolves X509 certificates for a mail address.
        /// </summary>
        /// <param name="address">The <see cref="MailAddress"/> instance to resolve. Will try the
        /// fallback domain if this address does not resolve.</param>
        /// <returns>An <see cref="X509Certificate2Collection"/> of X509 certifiates for the address,
        /// or <c>null</c> if no certificates are found.</returns>
        public X509Certificate2Collection GetCertificates(MailAddress address)
        {
            using (DnsClient client = CreateDnsClient())
            {
                //
                // First, try to resolve the full email address directly
                //                
                X509Certificate2Collection certs = this.GetCertificates(client, address.Address);
                if (certs.IsNullOrEmpty())
                {
                    //
                    // No certificates found. Perhaps certificates are available at the the (Domain) level
                    //
                    certs = this.GetCertificates(client, address.Host);
                }
                
                return certs;
            }
        }

        /// <summary>
        /// Resolves X509 certificates for a domain.
        /// </summary>
        /// <param name="domain">The domain for which certificates should be resolved.</param>
        /// <returns>An <see cref="X509Certificate2Collection"/> of X509 certifiates for the address,
        /// or <c>null</c> if no certificates are found.</returns>
        public X509Certificate2Collection GetCertificatesForDomain(string domain)
        {
            if (string.IsNullOrEmpty(domain))
            {
                throw new ArgumentException("domain");
            }
            
            using(DnsClient client = this.CreateDnsClient())
            {
                return this.GetCertificates(client, domain);
            }            
        }

        X509Certificate2Collection GetCertificates(DnsClient client, string domain)
        {
            X509Certificate2Collection certs = this.ResolveDomain(client, domain);
            if (certs.IsNullOrEmpty() && this.HasFallbackDomain)
            {
                certs = this.ResolveExtendedDomain(client, domain);
            }

            return certs;
        }
        
        X509Certificate2Collection ResolveDomain(DnsClient client, string name)
        {
            name = name.Replace('@', '.');
            try
            {
                IEnumerable<CertRecord> records = null;
                
                if (m_useRootForCertResolve)
                {
                    records = client.ResolveCERTFromNameServer(name);
                }
                else
                {
                    records = client.ResolveCERT(name);
                }
                if (records != null)
                {  
                    return CollectCerts(null, records);
                }
            }
            catch (DnsServerException dnsEx)
            {
                if (dnsEx.ResponseCode != DnsStandard.ResponseCode.Refused)
                {
                    throw;
                }
            }
            catch (Exception ex)
            {
                this.Error.NotifyEvent(this, ex);
                throw;
            }

            return null;
        }

        X509Certificate2Collection ResolveExtendedDomain(DnsClient client, string name)
        {
            name = name.Replace('@', '.');
            string extendedName = m_fallbackDomain.ConstructEmailDnsDomainName(name);
            return this.ResolveDomain(client, extendedName);
        }

        static X509Certificate2Collection CollectCerts(X509Certificate2Collection certs, IEnumerable<CertRecord> records)
        {            
            foreach(CertRecord record in records)
            {
                if (certs == null)
                {
                    certs = new X509Certificate2Collection();
                }
                certs.Add(record.Cert.Certificate);
            }
            
            return certs;
        }
        
        DnsClient CreateDnsClient()
        {
            DnsClient client = (m_cacheEnabled) ? new DnsClientWithCache(m_serverIP) : new DnsClient(m_serverIP);
            if (Timeout.Ticks > 0)
            {
                client.Timeout = Timeout;
            }
            
            client.UseUDPFirst = false;
            client.MaxRetries = m_maxRetries;
            
            return client;
        }
                
        
    }
}