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
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;

using Health.Direct.Common.Certificates;

namespace Health.Direct.Agent
{
    /// <summary>
    /// Represents a single S/MIME signature on a message.
    /// </summary>
    public class MessageSignature
    {
        bool? m_signatureValid;
        SignerInfo m_signer;
        bool m_usesOrgCertificate;
        bool? m_thumbprintVerified;
        
        /// <summary>
        /// Create a signature from <see cref="SignerInfo"/>
        /// </summary>
        /// <param name="signer">The <see cref="SignerInfo"/> for this signature</param>
        /// <param name="usesOrgCertificate"><c>true</c> if the signature is at an organizational,
        /// not an individual level, <c>false</c> if an individual signature.</param>
        public MessageSignature(SignerInfo signer, bool usesOrgCertificate)
        {
            if (signer == null)
            {
                throw new ArgumentNullException("signer");
            }            
            
            m_signer = signer;
            m_signatureValid = null;
            m_usesOrgCertificate = usesOrgCertificate;
            m_thumbprintVerified = null;
        }    
        
        /// <summary>
        /// The <see cref="SignerInfo"/> for this signature.
        /// </summary>
        public SignerInfo SignerInfo
        {
            get
            {
                return m_signer;
            }
        }
        
        /// <summary>
        /// The <see cref="X509Certificate2"/> for this signature.
        /// </summary>
        public X509Certificate2 Certificate
        {
            get
            {
                return m_signer.Certificate;
            }
        }
        
        /// <summary>
        /// Has the certificate for this signature been verified by thumbprint against the
        /// sender address's certificates?
        /// </summary>
        /// <value><c>true</c> if the signature is verified, <c>false</c> if not,
        /// <c>null</c> if verification has not been performed.</value>
        public bool? IsThumbprintVerified
        {
            get
            {
                return m_thumbprintVerified;
            }
        }
        
        /// <summary>
        /// Is this signature valid (signed by the certificate it purports to be signed by)?
        /// </summary>
        public bool IsSignatureValid
        {
            get
            {
                return this.CheckSignature();
            }
        }    
        
        /// <summary>
        /// Has this signature been signed by an organizational level signature?
        /// </summary>
        /// <value><c>true</c> if this has been signed by an organizational level certificate,
        /// <c>false</c> if by an individual level certificate.</value>
        public bool UsesOrgCertificate
        {
            get
            {
                return m_usesOrgCertificate;
            }
        }
        
        /// <summary>
        /// Check the validity of this signature.
        /// </summary>
        /// <returns><c>true</c> if valid, <c>false</c> if invalid.</returns>
        public bool CheckSignature()
        {
            if (m_signatureValid == null)
            {
                try
                {
                    m_signer.CheckSignature(true);
                    m_signatureValid = true;
                }
                catch
                {
                    m_signatureValid = false;
                }
            }
                        
            return m_signatureValid.Value;
        }
        
        internal bool CheckThumbprint(DirectAddress messageSender)
        {
            if (m_thumbprintVerified == null)
            {
                if (messageSender.HasCertificates)
                {
                    m_thumbprintVerified = (messageSender.Certificates.FindByThumbprint(this.Certificate.Thumbprint) != null);
                }
                else
                {
                    m_thumbprintVerified = false;
                }
            }
            
            return m_thumbprintVerified.Value;
        }        
    }
}