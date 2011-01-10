/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Arien Malec     arien.malec@nhindirect.org
    Sean Nolan      seannol@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Linq;
using System.Security.Cryptography.X509Certificates;

using Health.Direct.Common.Certificates;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Agent
{
    /// <summary>
    /// Validates trust chains for certificates.
    /// </summary>
    public class TrustChainValidator
    {
        /// <summary>
        /// The default setting for MaxIssuerChainLength
        /// </summary>
        public const int DefaultMaxIssuerChainLength = 5;
        /// <summary>
        /// Default setting for X509ChainPolicy.RevocationFlag
        /// </summary>
        public const X509RevocationFlag DefaultRevocationGranularity = X509RevocationFlag.ExcludeRoot;
        /// <summary>
        /// Default revocation checking mode 
        /// </summary>
        public const X509RevocationMode DefaultRevocationCheckMode = X509RevocationMode.Online;
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
        ICertificateResolver m_certResolver;
        int m_maxIssuerChainLength = DefaultMaxIssuerChainLength;
        
        /// <summary>
        /// Creates an instance with default chain policy, problem flags.
        /// </summary>
        public TrustChainValidator()
            : this(new X509ChainPolicy(), TrustChainValidator.DefaultProblemFlags)
        {
            m_policy.VerificationFlags = (X509VerificationFlags.IgnoreWrongUsage);
            this.RevocationCheckGranularity = DefaultRevocationGranularity;
            this.RevocationCheckMode = DefaultRevocationCheckMode;
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
        /// <remarks>
        ///     Revocation Checking Defaults:
        ///         RevocationFlag = X509RevocationFlag.EntireChain
        ///         RevocationMode = X509RevocationMode.Online
        /// </remarks>
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
        /// Controls how Certificate Revocation is checked. 
        /// <remarks>
        ///  The certificate chain engine will inspect CRLs and use other means to verify that a certificate was not revoked. 
        ///  In Online Mode, the engine will synchronously fetch fresh CRLs as necessary
        ///  In Offline Mode, the engine will only use cached information, or any CRLs directly installed on the machine.
        /// </remarks>
        /// </summary>
        public X509RevocationMode RevocationCheckMode
        {
            get
            {
                return m_policy.RevocationMode;
            }
            set
            {
                m_policy.RevocationMode = value;
            }
        }
        
        /// <summary>
        /// Controls the granularity of revocation checks
        /// </summary>
        public X509RevocationFlag RevocationCheckGranularity
        {
            get
            {
                return m_policy.RevocationFlag;
            }
            set
            {
                m_policy.RevocationFlag = value;
            }
        }
        
        /// <summary>
        /// Get or set the CertificateResolver used by this resolver to resolve intermediate certificate issuers
        /// Can be NULL. If null, does not attempt to resolve intermediate certificates
        /// </summary>
        public ICertificateResolver IssuerResolver
        {
            get
            {
                return m_certResolver;
            }
            set
            {
                m_certResolver = value;
            }
        }
        
        internal bool HasCertificateResolver
        {
            get
            {
                return (m_certResolver != null);
            }
        }
        
        /// <summary>
        /// Gets or sets the maximum depth upto which this validator will resolve intermediate issuers by calling CertificateResolver. 
        /// For example, with a value of 2, the validator will at most resolve a leaf certificate's grandparent
        /// </summary>
        public int MaxIssuerChainLength
        {
            get
            {
                return m_maxIssuerChainLength;
            }
            set
            {
                if (value <= 0)
                {
                    throw new ArgumentException();
                }

                m_maxIssuerChainLength = value;
            }
        }
        
        /// <summary>
        /// Event fired if there was an error during certificate validation
        /// </summary>
        public event Action<X509Certificate2, Exception> Error;
        /// <summary>
        /// Event fired when a certificate is untrusted
        /// </summary>
        public event Action<X509Certificate2> Untrusted;
        /// <summary>
        /// Event fired if a certificate has a problem.
        /// </summary>
        public event Action<X509ChainElement> Problem;
        
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
            if (CollectionExtensions.IsNullOrEmpty(anchors))
            {
                this.NotifyUntrusted(certificate);
                return false;
            }

            X509Chain chainBuilder = new X509Chain();
            chainBuilder.ChainPolicy = m_policy.Clone();
            
            chainBuilder.ChainPolicy.ExtraStore.Add(anchors);
            if (this.HasCertificateResolver)
            {
                this.ResolveIntermediateIssuers(certificate, chainBuilder.ChainPolicy.ExtraStore);
            }
            
            try
            {
                // We're using the system class as a helper to merely build the chain
                // However, we will review each item in the chain ourselves, because we have our own rules...
                chainBuilder.Build(certificate);
                X509ChainElementCollection chainElements = chainBuilder.ChainElements;

                // If we don't have a trust chain, then we obviously have a problem...
                if (chainElements.IsNullOrEmpty())
                {
                    this.NotifyUntrusted(certificate);
                    return false;
                }

                // walk the chain starting at the leaf and see if we hit any issues before the anchor
                foreach (X509ChainElement chainElement in chainElements)
                {                
                    if (this.ChainElementHasProblems(chainElement))
                    {
                        this.NotifyProblem(chainElement);
                        
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
            catch(Exception ex)
            {
                this.NotifyError(certificate, ex);
                // just eat it and drop out to return false
            }
            
            this.NotifyUntrusted(certificate);
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
        
        /// <summary>
        /// Resolve intermediate issuers for the given certificate
        /// </summary>
        /// <param name="certificate">The leaf <see cref="X509Certificate2"/>certificate</param>
        /// <returns>Issuer collection</returns>
        public X509Certificate2Collection ResolveIntermediateIssuers(X509Certificate2 certificate)
        {
            X509Certificate2Collection issuers = new X509Certificate2Collection();
            this.ResolveIntermediateIssuers(certificate, issuers);
            return issuers;
        }

        /// <summary>
        /// Resolve intermediate issuers for the given certificate
        /// </summary>
        /// <param name="certificate">The leaf <see cref="X509Certificate2"/>certificate</param>
        /// <param name="issuers">The collection of issuers to populate with resolved issuers</param>
        /// <returns>Issuer collection</returns>
        public void ResolveIntermediateIssuers(X509Certificate2 certificate, X509Certificate2Collection issuers)
        {
            if (certificate == null)
            {
                throw new ArgumentNullException("certificate");
            }
            if (issuers == null)
            {
                throw new ArgumentException("issuers");
            }
            this.ResolveIssuers(certificate, issuers, 0);
        }

        void ResolveIssuers(X509Certificate2 certificate, X509Certificate2Collection issuers, int chainLength)
        {
            //
            // only look at simpleNames because intermediates are always going to be org-level, not email, certs
            //
            string issuerName = certificate.GetNameInfo(X509NameType.SimpleName, true); // true == "for issuer"
            //
            // If the issuer name matches the Cert name, we have a self-signed cert
            //
            if (certificate.MatchName(issuerName))
            {
                return;
            }
            //
            // If the issuer is already known, then we are good
            //
            if (issuers.FindByName(issuerName) != null)
            {
                return;
            }

            if (chainLength == m_maxIssuerChainLength)
            {
                //
                // Chain too long. Ignore...
                //
                return;
            }
            //
            // Retrieve the issuer's certificate
            //
            X509Certificate2Collection issuerCertificates = m_certResolver.SafeGetCertificates(certificate.ExtractEmailNameOrName(true));
            if (CollectionExtensions.IsNullOrEmpty(issuerCertificates))
            {
                return;
            }
            //
            // Recursively fetch the issuers who issued this set of certificates
            //
            foreach (X509Certificate2 issuerCertificate in issuerCertificates)
            {
                if (issuerCertificate.MatchName(issuerName) && !issuers.ContainsThumbprint(issuerCertificate.Thumbprint))
                {
                    //
                    // New issuer
                    //
                    issuers.Add(issuerCertificate);
                    //
                    // And keep working up the chain
                    //
                    this.ResolveIssuers(issuerCertificate, issuers, chainLength + 1);
                }
            }
        }
        
        void NotifyUntrusted(X509Certificate2 cert)
        {
            if (this.Untrusted != null)
            {
                try
                {
                    this.Untrusted(cert);
                }
                catch
                {
                }
            }
        }
        
        void NotifyProblem(X509ChainElement chainElement)
        {
            if (this.Problem != null)
            {
                try
                {
                    this.Problem(chainElement);
                }
                catch
                {
                }
            }
        }
        
        void NotifyError(X509Certificate2 cert, Exception exception)
        {
            if (this.Error != null)
            {
                try
                {
                    this.Error(cert, exception);
                }
                catch
                {
                }
            }
        }
    }
}