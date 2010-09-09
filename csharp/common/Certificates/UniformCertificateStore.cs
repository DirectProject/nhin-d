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

namespace NHINDirect.Certificates
{
    /// <summary>
    /// Represents a certificate store for an entire organization that is represented by the same organization certificate
    /// </summary>
    public class UniformCertificateStore : ICertificateResolver
    {
        X509Certificate2Collection m_certs;
        
        /// <summary>
        /// Initializes an instance with the single certificate for the organization.
        /// </summary>
        /// <param name="cert">The certificate to use for the organization.</param>
        public UniformCertificateStore(X509Certificate2 cert)
        {
            if (cert == null)
            {
                throw new ArgumentNullException("cert");
            }
            
            X509Certificate2Collection certs = new X509Certificate2Collection();
            certs.Add(cert);
            this.Certificates = certs;
        }

        /// <summary>
        /// Initializes an instance with a collection of certificates that may be used
        /// for the organization.
        /// </summary>
        /// <param name="certs">The certificates to use for the organization.</param>
        public UniformCertificateStore(X509Certificate2Collection certs)
        {
            this.Certificates = certs;
        }

        /// <summary>
        /// Initializes an instance with a store of certificates that may be used
        /// for the organization.
        /// </summary>
        /// <param name="certs">The certificates to use for the organization.</param>
        public UniformCertificateStore(IX509CertificateStore certs)
        {
            if (certs == null)
            {
                throw new ArgumentNullException();
            }
            this.Certificates = certs.GetAllCertificates();
        }
        
        /// <summary>
        /// Gets and sets the certificates that may be used for the organization.
        /// </summary>
        public X509Certificate2Collection Certificates
        {
            get
            {
                return this.m_certs;
            }
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException();
                }

                this.m_certs = value;
            }
        }

        /// <summary>
        /// Gets the certificates for an address; always returns the same collection
        /// </summary>
        /// <param name="address">The address for which to retrieve certificates</param>
        /// <returns>The organizational certificates.</returns>
        public X509Certificate2Collection GetCertificates(MailAddress address)
        {
            if (address == null)
            {
                throw new ArgumentNullException();
            }

            return this.m_certs;
        }
    }
}
