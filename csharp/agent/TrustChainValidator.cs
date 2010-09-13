/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Arien Malec     arien.malec@nhindirect.org
  
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
using System.Security.Cryptography.X509Certificates;
using NHINDirect.Certificates;

namespace NHINDirect.Agent
{
    /// <summary>
    /// Validates trust chains for certificates.
    /// </summary>
    public class TrustChainValidator
    {
        /// <summary>
        /// Chain validations status treated as failing trust validation with the certificate.
        /// </summary>
        public static readonly X509ChainStatusFlags DefaultProblemFlags =
                X509ChainStatusFlags.NotTimeValid |
                X509ChainStatusFlags.Revoked |
                X509ChainStatusFlags.NotSignatureValid |
                X509ChainStatusFlags.InvalidBasicConstraints |
                X509ChainStatusFlags.CtlNotTimeValid |
                X509ChainStatusFlags.CtlNotSignatureValid;
        
        X509ChainPolicy m_policy;
        X509ChainStatusFlags m_problemFlags;
        
        /// <summary>
        /// Creates an instance with default chain policy, problem flags.
        /// </summary>
        public TrustChainValidator()
            : this(new X509ChainPolicy(), TrustChainValidator.DefaultProblemFlags)
        {
            m_policy.VerificationFlags = (X509VerificationFlags.IgnoreWrongUsage);
        }

        /// <summary>
        /// Creates an instance, specifying chain policy and problem flags
        /// </summary>
        /// <param name="policy">The <see cref="X509ChainPolicy"/> to use for validating trust chains</param>
        /// <param name="problemFlags">The status flags that will be treated as invalid in trust verification</param>
        public TrustChainValidator(X509ChainPolicy policy, X509ChainStatusFlags problemFlags)
        {
            m_policy = policy;
            m_problemFlags = problemFlags;
        }
        
        /// <summary>
        /// Gets the <see cref="X509ChainPolicy"/> for this validator.
        /// </summary>
        public X509ChainPolicy ValidationPolicy
        {
            get
            {
                return m_policy;
            }
        }
        
        /// <summary>
        /// Gets and sets the <see cref="X509ChainStatusFlags"/> for this validator
        /// </summary>
        public X509ChainStatusFlags ProblemFlags
        {
            get
            {
                return m_problemFlags;
            }
            set
            {
                m_problemFlags = value;
            }
        }
        
        /// <summary>
        /// Validates a certificate by walking the certificate chain for all trust anchor chain, validating the leaf certificate against the chain.
        /// </summary>
        /// <remarks>Currently, all intermediate certificates must be stored in the system.</remarks>
        /// <param name="certificate">The leaf <see cref="X509Certificate2"/> to validate</param>
        /// <param name="anchors">The collection of certificates representing anchors or roots of trust.</param>
        /// <returns><c>true</c> if at least one anchor has a valid chain of certs that verify trust in the leaf certificate,
        /// <c>false</c> if no anchors validate trust in the leaf cert.</returns>
        public bool IsTrustedCertificate(X509Certificate2 certificate, X509Certificate2Collection anchors)
        {
            if (certificate == null)
            {
                throw new ArgumentNullException("certificate");
            }

            // if there are no anchors we should always fail
            if (anchors.IsNullOrEmpty())
            {
                return false;
            }

            X509Chain chainBuilder = new X509Chain();
            chainBuilder.ChainPolicy = m_policy.Clone();
            chainBuilder.ChainPolicy.ExtraStore.Add(anchors);
            
            try
            {
                // We're using the system class as a helper to merely build the chain
                // However, we will review each item in the chain ourselves, because we have our own rules...
                chainBuilder.Build(certificate);
                X509ChainElementCollection chainElements = chainBuilder.ChainElements;

                // If we don't have a trust chain, then we obviously have a problem...
                if (chainElements.IsNullOrEmpty())
                {
                    return false;
                }

                // walk the chain starting at the leaf and see if we hit any issues before the anchor
                foreach (X509ChainElement chainElement in chainElements)
                {                
                    if (this.ChainElementHasProblems(chainElement))
                    {
                        // Whoops... problem with at least one cert in the chain. Stop immediately
                        return false;
                    }

                    bool isAnchor = (anchors.FindByThumbprint(chainElement.Certificate.Thumbprint) != null);
                    if (isAnchor)
                    {
                        // Found a valid anchor!
                        // Because we found an anchor we trust, we can now trust the entire trust chain
                        return true;
                    }
                }
            }
            catch
            {
                // just eat it and drop out to return false
            }

            return (false);
        }
                
        bool ChainElementHasProblems(X509ChainElement chainElement)
        {
            // If the builder finds problems with the cert, it will provide a list of "status" flags for the cert
            X509ChainStatus[] chainElementStatus = chainElement.ChainElementStatus;

            // If the list is empty or the list is null, then there were NO problems with the cert
            if (chainElementStatus.IsNullOrEmpty())
            {
                return false;
            }

            // Return true if there are any status flags we care about
            return chainElementStatus.Any(s => (s.Status & m_problemFlags) != 0);
        }
    }
}
