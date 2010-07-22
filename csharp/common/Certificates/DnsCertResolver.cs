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
        
        public bool AssumeWildcardSupport = true;
        
        public X509Certificate2Collection GetCertificates(MailAddress address)
        {
            using(DnsClient client = new DnsClient(m_serverIP))
            {
                if (m_timeout > 0)
                {
                    client.Timeout = m_timeout;
                }
                
                client.UseUDP = false;
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
