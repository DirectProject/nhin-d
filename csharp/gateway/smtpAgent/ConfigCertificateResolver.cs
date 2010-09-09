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
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;
using NHINDirect.Config.Store;
using NHINDirect.Certificates;
using NHINDirect.Config.Client;
using NHINDirect.Config.Client.DomainManager;
using NHINDirect.Config.Client.CertificateService;

namespace NHINDirect.SmtpAgent
{
    public class ConfigCertificateResolver : ICertificateResolver, IX509CertificateIndex
    {
        ClientSettings m_certClientSettings;
        ClientSettings m_addressClientSettings;
        CertificateResolver m_resolver;
        
        public ConfigCertificateResolver(ClientSettings certClientSettings)
            : this(certClientSettings, null)
        {
        }
        
        public ConfigCertificateResolver(ClientSettings certClientSettings, ClientSettings addressClientSettings)
        {
            if (certClientSettings == null)
            {
                throw new ArgumentNullException("certClientSettings");
            }
            
            m_certClientSettings = certClientSettings;
            m_addressClientSettings = addressClientSettings;
            m_resolver = new CertificateResolver(this);
        }

        public X509Certificate2Collection GetCertificates(MailAddress address)
        {
            if (m_addressClientSettings != null)
            {
                Address registeredAddress = this.ResolveAddress(address);
                if (registeredAddress == null)
                {
                    return null;
                }
            }
            
            return m_resolver.GetCertificates(address);
        }

        public X509Certificate2Collection this[string subjectName]
        {
            get 
            { 
                using(CertificateStoreClient client = this.CreateCertClient())
                {
                    return Certificate.ToX509Collection(client.GetCertificatesForOwner(subjectName, EntityStatus.Enabled));
                }
            }
        }
        
        Address ResolveAddress(MailAddress address)
        {
            AddressManagerClient client = this.CreateAddressClient();
            return client.GetAddress(address);
        }
        
        CertificateStoreClient CreateCertClient()
        {
            return new CertificateStoreClient(m_certClientSettings.Binding, m_certClientSettings.Endpoint);
        }
        
        AddressManagerClient CreateAddressClient()
        {
            return new AddressManagerClient(m_addressClientSettings.Binding, m_addressClientSettings.Endpoint);
        }
    }
}
    