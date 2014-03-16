/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;

using Health.Direct.Common.Extensions;
using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;

namespace Health.Direct.Admin.Console.Models.Repositories
{
    public class CertificateRepository : ICertificateRepository
    {
        private readonly ICertificateStore m_client;

        public CertificateRepository(ICertificateStore client)
        {
            m_client = client;
        }

        protected ICertificateStore Client { get { return m_client; } }
        
        public IQueryable<Certificate> Query()
        {
            return Client.EnumerateCertificates(0, int.MaxValue, null).AsQueryable();
        }

        public Certificate Add(Certificate certificate)
        {
            return Client.AddCertificate(certificate);
        }

        public void Delete(Certificate certificate)
        {
            Client.RemoveCertificates(new[] {certificate.ID});
        }

        public Certificate Update(Certificate certificate)
        {
            throw new NotSupportedException("Updates are not supported on Certificates");
        }

        public Certificate ChangeStatus(Certificate certificate, EntityStatus status)
        {
            Client.SetCertificateStatus(new[] { certificate.ID}, status);
            certificate.Status = status;
            return certificate;
        }

        public IEnumerable<Certificate> Resolve(string owner, bool showData)
        {
            MailAddress address = new MailAddress(owner);
            var options = new CertificateGetOptions { IncludeData = showData };

            Certificate[] certs = Client.GetCertificatesForOwner(address.Address, options);

            if (certs.IsNullOrEmpty())
            {
                certs = Client.GetCertificatesForOwner(address.Host, options);
            }

            return certs;
        }

        public Certificate Get(long id)
        {
            // TODO: Replace with GetCertificateById()
            return (from certificate in Query()
                    where certificate.ID == id
                    select certificate).SingleOrDefault();
        }

        public Certificate Get(string owner, string thumbprint)
        {
            return (from certificate in Client.GetCertificatesForOwner(owner, null)
                    where certificate.Thumbprint == thumbprint
                    select certificate).SingleOrDefault();
        }
    }
}