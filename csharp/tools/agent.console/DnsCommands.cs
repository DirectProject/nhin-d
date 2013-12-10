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
using System.Net.Mail;
using System.IO;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.DnsResolver;
using Health.Direct.Common.Dns;
using Health.Direct.Common.Certificates;
using Health.Direct.Config.Tools;
using Health.Direct.Config.Tools.Command;
using Health.Direct.Common.Caching;
using Health.Direct.ResolverPlugins;

namespace Health.Direct.Tools.Agent
{
    public class DnsCommands
    {
        IPAddress m_dnsServer;
        DnsRecordPrinter m_recordPrinter;
        bool m_useCache;
        
        public DnsCommands()
        {
            m_dnsServer = IPAddress.Parse("4.2.2.1");
            m_recordPrinter = new DnsRecordPrinter(Console.Out);
        }

        [Command(Name = "Dns_GetServer", Usage = GetServerUsage)]
        public void GetServer(string[] args)
        {
            Console.WriteLine(m_dnsServer);
        }
        const string GetServerUsage =
            "Display the IP address of the dns server to use"
        + Constants.CRLF + "    ipaddress";            
        
        [Command(Name = "Dns_SetServer", Usage=SetServerUsage)]
        public void SetServer(string[] args)
        {
            IPAddress address = IPAddress.Parse(args.GetRequiredValue(0));
            m_dnsServer = address;
        }
        const string SetServerUsage = 
            "Set the IP address of the dns server to use"
        + Constants.CRLF + "    ipaddress";            
                        
        /// <summary>
        /// Resolves certificates for a domain or email address using Dns and Ldap
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_ResolveCert", Usage = ResolveCertUsage)]
        public void ResolveCert(string[] args)
        {
            string domain = args.GetRequiredValue(0);
            string outputFile = args.GetOptionalValue(1, null);
            if (ResolveCertInDns(domain, outputFile))
            {
                return;
            }
            if (ResolveCertInLdap(domain, outputFile))
            {
                return;
            }

            Console.WriteLine("No certs found");
        }

        /// <summary>
        /// Resolves certificates for a domain or email address using Ldap
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "LDAP_ResolveCert", Usage = ResolveCertUsage)]
        public void ResolveLdapCert(string[] args)
        {
            string domain = args.GetRequiredValue(0);
            string outputFile = args.GetOptionalValue(1, null);
            
            if (ResolveCertInLdap(domain, outputFile))
            {
                return;
            }

            Console.WriteLine("No certs found");
        }

        private bool ResolveCertInDns(string domain, string outputFile)
        {
            DnsCertResolver resolver = new DnsCertResolver(m_dnsServer, TimeSpan.FromSeconds(5));
            resolver.Error += resolver_Error;

            MailAddress address = null;
            try
            {
                address = new MailAddress(domain);
            }
            catch
            {
            }
            X509Certificate2Collection certs;
            if (address != null)
            {
                Console.WriteLine("Resolving mail address {0} in DNS", domain);
                certs = resolver.GetCertificates(address);
            }
            else
            {
                certs = resolver.GetCertificatesForDomain(domain);
            }

            if (certs.IsNullOrEmpty())
            {
                return false;
            }

            Console.WriteLine("{0} found in DNS", certs.Count);
            foreach (X509Certificate2 cert in certs)
            {
                Console.WriteLine(cert.SubjectName.Name);
            }

            if (!string.IsNullOrEmpty(outputFile))
            {
                byte[] bytes = certs.Export(X509ContentType.Cert);
                File.WriteAllBytes(outputFile, bytes);
            }
            return true;
        }

        private bool ResolveCertInLdap(string domain, string outputFile)
        {
            LdapCertResolver resolver = new LdapCertResolver(m_dnsServer, TimeSpan.FromSeconds(5));
            resolver.Error += resolver_Error;

            MailAddress address = null;
            try
            {
                address = new MailAddress(domain);
            }
            catch
            {
            }
            X509Certificate2Collection certs;
            if (address != null)
            {
                Console.WriteLine("Resolving mail address {0} in LDAP", domain);
                certs = resolver.GetCertificates(address);
            }
            else
            {
                certs = resolver.GetCertificatesForDomain(domain);
            }

            if (certs.IsNullOrEmpty())
            {
                return false;
            }

            Console.WriteLine("{0} found in LDAP", certs.Count);
            foreach (X509Certificate2 cert in certs)
            {
                Console.WriteLine(cert.SubjectName.Name);
            }

            if (!string.IsNullOrEmpty(outputFile))
            {
                byte[] bytes = certs.Export(X509ContentType.Cert);
                File.WriteAllBytes(outputFile, bytes);
            }
            return true;
        }

        void resolver_Error(ICertificateResolver arg1, Exception arg2)
        {
            Console.WriteLine(arg2.Message);
        }

        private const string ResolveCertUsage =
            "Resolve certificates for an address or domain using Dns"
            + Constants.CRLF + "   domain or email-address"
            + Constants.CRLF + "   outputFile: (optional)";
        
        [Command(Name="Dns_Resolve", Usage=ResolveUsage)]
        public void DnsResolve(string[] args)
        {
            string domain = args.GetRequiredValue(0);
            DnsStandard.RecordType recordType = args.GetOptionalEnum<DnsStandard.RecordType>(1, DnsStandard.RecordType.ANAME);
            
            try
            {
                using(DnsClient client = CreateClient())
                {
                    client.UseUDPFirst = (recordType != DnsStandard.RecordType.CERT);
                    DnsResponse response = client.Resolve(new DnsRequest(recordType, domain));
                    if (response == null)
                    {
                        Console.WriteLine("No matches");
                        return;
                    }                
                    m_recordPrinter.Print(response);
                }
            }
            catch(DnsServerException ex)
            {
                Console.WriteLine(ex.ResponseCode);
            }
        }
        
        const string ResolveUsage =
            "Resolve records for the given domain"
            + Constants.CRLF + "    domain recordType"
            + Constants.CRLF + "\tdomain"
            + Constants.CRLF + "\trecordType: ANAME | CERT | MX | SOA | NS | SRV | AAAA | PTR | CNAME";
        
        [Command(Name = "Dns_Cache_Set", Usage="true | false")]
        public void SetCache(string[] args)
        {
            m_useCache = args.GetRequiredValue<bool>(0);            
            if (!m_useCache)
            {
                DnsResponseCache.Current.RemoveAll();
            }
        }            
        
        [Command(Name = "Dns_Cache_Clear")]
        public void ClearCache(string[] args)
        {
            if (m_useCache)
            {
                DnsResponseCache.Current.RemoveAll();
            }
        }
        
        DnsClient CreateClient()
        {
            return m_useCache ? new DnsClientWithCache (m_dnsServer) : new DnsClient(m_dnsServer);
        }
    }
}
