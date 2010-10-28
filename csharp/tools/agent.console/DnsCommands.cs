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
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Mail;
using System.IO;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.DnsResolver;
using Health.Direct.Common.Certificates;
using Health.Direct.Config.Tools.Command;

namespace Health.Direct.Tools.Agent
{
    public class DnsCommands
    {
        IPAddress m_dnsServer;
        string m_fallbackDomain;
        
        public DnsCommands()
        {
            m_dnsServer = IPAddress.Parse("8.8.8.8");
            m_fallbackDomain = "hsgincubator.com";
        }
        
        /// <summary>
        /// Resolves certificates for a domain or email address using Dns
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_ResolveCert", Usage = ResolveCertUsage)]
        public void ResolveCert(string[] args)
        {
            string domain = args.GetRequiredValue(0);
            string outputFile = args.GetOptionalValue(1, null);
            DnsCertResolver resolver = new DnsCertResolver(m_dnsServer, TimeSpan.FromSeconds(5), m_fallbackDomain);

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
                Console.WriteLine("Resolving mail address {0}", domain);
                certs = resolver.GetCertificates(address);
            }
            else
            {
                certs = resolver.GetCertificatesForDomain(domain);
            }
            
            if (certs.IsNullOrEmpty())
            {
                Console.WriteLine("No certs found");
                return;
            }

            Console.WriteLine("{0} found", certs.Count);
            foreach(X509Certificate2 cert in certs)
            {
                Console.WriteLine(cert.SubjectName.Name);
            }
            
            if (!string.IsNullOrEmpty(outputFile))
            {
                byte[] bytes = certs.Export(X509ContentType.Cert);
                File.WriteAllBytes(outputFile, bytes);   
            }
        }
        
        const string ResolveCertUsage =
              "Resolve certificates for an address or domain using Dns\r\n"
            + "   domain or address\r\n"
            + "   outputFile: (optional)\r\n"
            + "   server : (optional)\r\n";

    }
}
