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
using System.Security.Cryptography.X509Certificates;

namespace Health.Direct.Common.Certificates
{
    /// <summary>
    /// Represents a certificate store where any address or domain uses the same collection of certificates
    /// Typically used at an organization level: where the same certificates apply to the entire organization.
    /// </summary>b
    public class UniformCertificateResolver : CertificateResolver
    {
        X509Certificate2Collection m_certs;
        
        /// <summary>
        /// Initializes an instance with the single certificate for the organization.
        /// </summary>
        /// <param name="cert">The certificate to use for the organization.</param>
        public UniformCertificateResolver(X509Certificate2 cert)
            : this(new X509Certificate2Collection(cert))
        {
        }

        /// <summary>
        /// Initializes an instance with a collection of certificates that may be used
        /// for the organization.
        /// </summary>
        /// <param name="certs">The certificates to use for the organization.</param>
        public UniformCertificateResolver(X509Certificate2Collection certs)
        {
            this.Certificates = certs;
        }

        /// <summary>
        /// Initializes an instance with a store of certificates that may be used
        /// for the organization.
        /// </summary>
        /// <param name="certs">The certificates to use for the organization.</param>
        public UniformCertificateResolver(IX509CertificateStore certs)
            : this(certs.GetAllCertificates())
        {
        }
        
        /// <summary>
        /// Gets and sets the certificates that may be used for the organization.
        /// </summary>
        public X509Certificate2Collection Certificates
        {
            get
            {
                return m_certs;
            }
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException("value");
                }

                m_certs = value;
            }
        }

        /// <summary>
        /// Actually resolves a certificate for the given name. Override to customize. 
        /// </summary>
        /// <param name="name">Return certificates for this name</param>
        /// <returns>
        /// The <see cref="X509Certificate2Collection"/> of certificates for the requested name.
        /// </returns>
        protected override X509Certificate2Collection Resolve(string name)
        {
            return m_certs;            
        }
    }
}