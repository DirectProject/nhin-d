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
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;

using Health.Direct.Common.Certificates;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Store;

namespace Health.Direct.SmtpAgent
{
    /// <summary>
    /// Resolve Certificates from the Configuration Service. 
    /// Used by the Mail Gateway to resolve Private Certs from the Config Service.
    /// 
    /// To resolve certificates, calls the CertificateService using the supplied settings.
    /// Before resolving certificates for an address, can verify that addresses exist by calling the AddressService using supplied settings
    /// </summary>
    public class ConfigCertificateResolver : CertificateResolver
    {
        ClientSettings m_certClientSettings;
        ClientSettings m_addressClientSettings;

        /// <summary>
        /// Create a new resolver
        /// </summary>
        /// <param name="certClientSettings">Settings for the certificate service to use for certificate resolution</param>    
        public ConfigCertificateResolver(ClientSettings certClientSettings)
            : this(certClientSettings, null)
        {
        }
        
        /// <summary>
        /// Create a new resolver
        /// </summary>
        /// <param name="certClientSettings">Resolve certificates from this certificate service</param>
        /// <param name="addressClientSettings">Optional Address Service. If NOT null, then before returning certificates, check if addresses exist by calling the address service</param>
        public ConfigCertificateResolver(ClientSettings certClientSettings, ClientSettings addressClientSettings)
        {
            if (certClientSettings == null)
            {
                throw new ArgumentNullException("certClientSettings");
            }
            
            m_certClientSettings = certClientSettings;
            m_addressClientSettings = addressClientSettings;
        }
                
        /// <summary>
        /// Resolve certificates for the given mail address
        /// 1. Optionally checks if the mail address exists
        /// 2. Then resolves appropriate certificates for the address
        /// </summary>
        /// <param name="address"></param>
        /// <returns></returns>
        public override X509Certificate2Collection GetCertificates(MailAddress address)
        {            
            if (m_addressClientSettings != null)
            {
                // We are configured to verify that the mail address exists
                // Resolve certificates for the address only if the address is valid
                Address registeredAddress = this.ResolveAddress(address);
                if (registeredAddress == null)
                {
                    return null;
                }
            }
            
            return base.GetCertificates(address);
        }
                
        protected override X509Certificate2Collection Resolve(string name)
        {
            using(CertificateStoreClient client = this.CreateCertClient())
            {
                return Certificate.ToX509Collection(client.GetCertificatesForOwner(name, EntityStatus.Enabled));
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