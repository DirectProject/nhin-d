/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;
using DnsResolver;

namespace NHINDirect.Certificates
{
    public class DnsCertResolver : ICertificateResolver
    {
        public const int DefaultTimeoutMs = 5000; // Milliseconds
        
        IPAddress m_serverIP;
        string m_fallbackDomain = string.Empty;
        int m_timeout;
        int m_maxRetries = 1;
        
        public DnsCertResolver(IPAddress serverIP)
            : this(serverIP, DnsCertResolver.DefaultTimeoutMs)
        {
        }
        
        public DnsCertResolver(IPAddress serverIP, int timeoutMs)
            : this(serverIP, timeoutMs, null)
        {
        }
        
        public DnsCertResolver(IPAddress serverIP, int timeoutMs, string fallbackDomain)
        {
            if (serverIP == null)
            {
                throw new ArgumentNullException();
            }
            if (timeoutMs < 0)
            {
                throw new ArgumentException();
            }
            m_serverIP = serverIP;
            m_timeout = timeoutMs;
            m_fallbackDomain = fallbackDomain;
        }

        public event Action<DnsCertResolver, Exception> Error;
        
        public IPAddress Server
        {
            get
            {
                return m_serverIP;
            }
        }
        
        public bool HasFallbackDomain
        {
            get
            {
                return !string.IsNullOrEmpty(m_fallbackDomain);
            }            
        }
        
        public int Timeout
        {
            get
            {
                return m_timeout;
            }
        }
        
        public int MaxRetries
        {
            get
            {
                return m_maxRetries;
            }
            set
            {
                if (value <= 0)
                {
                    throw new ArgumentException();
                }
                
                m_maxRetries = value;
            }
        }
        
        public bool AssumeWildcardSupport = true;
        
        public X509Certificate2Collection GetCertificates(MailAddress address)
        {
            using(DnsClient client = new DnsClient(m_serverIP))
            {
                if (m_timeout > 0)
                {
                    client.Timeout = m_timeout;
                }
                
                client.UseUDPFirst = false;
                client.MaxRetries = m_maxRetries;
                X509Certificate2Collection certs = null;
                
                certs = this.ResolveDomain(client, address.Address);
                if (!CertificateResolver.IsNullOrEmpty(certs))
                {
                    return certs;
                }
                
                if (!this.AssumeWildcardSupport)
                {
                    certs = this.ResolveDomain(client, address.Host);
                    if (!CertificateResolver.IsNullOrEmpty(certs))
                    {
                        return certs;
                    }
                }

                if (this.HasFallbackDomain)
                {
                    certs = this.ResolveExtendedDomain(client, address.Address);
                    if (!CertificateResolver.IsNullOrEmpty(certs))
                    {
                        return certs;
                    }
                    
                    if (!this.AssumeWildcardSupport)
                    {
                        certs = this.ResolveExtendedDomain(client, address.Host);
                        if (!CertificateResolver.IsNullOrEmpty(certs))
                        {
                            return certs;
                        }
                    }                    
                }
            }
            
            return null;
        }

        X509Certificate2Collection ResolveDomain(DnsClient client, string name)
        {
            name = name.Replace('@', '.');
            try
            {
                IEnumerable<CertRecord> records = client.ResolveCERT(name);
                if (records != null)
                {  
                    return this.CollectCerts(null, records);
                }
            }
            catch (DnsServerException dnsEx)
            {
                if (dnsEx.ResponseCode != DnsResolver.Dns.ResponseCode.REFUSED)
                {
                    throw;
                }
            }
            catch (Exception ex)
            {
                this.NotifyException(ex);
                throw;
            }

            return null;
        }

        X509Certificate2Collection ResolveExtendedDomain(DnsClient client, string name)
        {
            name = name.Replace('@', '.');
            string extendedName = DNSCert.MakeExtendedDomainName(m_fallbackDomain, name);
            return this.ResolveDomain(client, extendedName);
        }
        
        X509Certificate2Collection CollectCerts(X509Certificate2Collection certs, IEnumerable<CertRecord> records)
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
        
        void NotifyException(Exception ex)
        {
            if (this.Error != null)
            {
                try
                {
                    this.Error(this, ex);
                }
                catch
                {
                }
            }
        }
    }
}
