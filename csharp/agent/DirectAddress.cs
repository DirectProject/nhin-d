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
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;

using Health.Direct.Common.Extensions;

namespace Health.Direct.Agent
{
    /// <summary>
    /// Represents a Direct address (a <see cref="MailAddress"/> with an associated set of certificates.
    /// </summary>
    public class DirectAddress : MailAddress
    {
        X509Certificate2Collection m_certificates;
        TrustEnforcementStatus m_trustStatus;
        X509Certificate2Collection m_trustAnchors;
        bool m_resolvedCertificates;

        /// <summary>
        /// Creates a Direct address without associated certificates. (The associated certificates
        /// and trust anchors must be later set from, e.g., an external store or source).
        /// </summary>
        /// <param name="address">The <c>string</c> representation of the address.</param>
        public DirectAddress(string address)
            : this(address, null)
        {
        }

        /// <summary>
        /// Creates a Direct address without associated certificates. (The associated certificates
        /// and trust anchors must be later set from, e.g., an external store or source).
        /// </summary>
        /// <param name="address">The <see cref="MailAddress"/> representation of the address.</param>
        public DirectAddress(MailAddress address)
            : this(address.ToString())
        {
        }
        
        /// <summary>
        /// Creates a Direct address with associated certificates.
        /// </summary>
        /// <param name="address">The <c>string</c> representation of the address.</param>
        /// <param name="certificates">The certificates to associate with the address.</param>
        public DirectAddress(string address, X509Certificate2Collection certificates)
            : base(address)
        {            
            this.m_certificates = certificates;
        }
                
        /// <summary>
        /// Gets and sets the certificates associated with this address
        /// </summary>
        /// <value>An <see cref="X509Certificate2Collection"/> of certficates for this address</value>
        public X509Certificate2Collection Certificates
        {
            get
            {
                return this.m_certificates;
            }
            set
            {
                this.m_certificates = value;
            }
        }

        /// <summary>
        /// Gets and sets the flag indicating public certs can be resovled.
        /// </summary>
        /// <value>Flag indicating the Security portion of "Security And Trust"</value>
        public bool ResolvedCertificates
        {
            get
            {
                return this.m_resolvedCertificates;
            }
            set
            {
                this.m_resolvedCertificates = value;
            }
        }

        /// <summary>
        /// Gets if this address has associated certificates
        /// </summary>
        /// <value><c>true</c> if this address has certificates, <c>false</c> if not</value>
        public bool HasCertificates
        {
            get
            {
                return !this.m_certificates.IsNullOrEmpty();
            }
        }
        
        /// <summary>
        /// Gets and sets the trust anchors for this address
        /// </summary>
        public X509Certificate2Collection TrustAnchors
        {
            get
            {
                return this.m_trustAnchors;
            }
            set
            {
                this.m_trustAnchors = value;
            }
        }

        /// <summary>
        /// Gets if this address has associated trust anchors
        /// </summary>
        /// <value><c>true</c> if this address has trust anchors, <c>false</c> if not</value>
        public bool HasTrustAnchors
        {
            get
            {
                return (this.m_trustAnchors != null && this.m_trustAnchors.Count > 0);
            }
        }
        
        /// <summary>
        /// Gets and sets the result of trust checking for this address.
        /// </summary>
        /// <value>A <see cref="TrustEnforcementStatus"/> value indicated the result of trust checking</value>
        public TrustEnforcementStatus Status
        {
            get
            {
                return this.m_trustStatus;
            }
            set
            {
                this.m_trustStatus = value;
            }
        }
        

        /// <summary>
        /// An arbitrary Tag that an integrator may associate with this address during pre/post processing of messages. 
        /// </summary>
        public object Tag
        {
            get;
            set;
        }
        
        /// <summary>
        /// Returns <c>true</c> if the trust status for this address meets minimal trust, <c>false</c> if not.
        /// </summary>
        /// <param name="minTrustStatus">The <see cref="TrustEnforcementStatus"/> treated as minimally trustworthy</param>
        /// <returns><c>true</c> if trusted, <c>false</c> if not</returns>
        public bool IsTrusted(TrustEnforcementStatus minTrustStatus)
        {
            return (this.m_trustStatus >= minTrustStatus);
        }        
    }
}