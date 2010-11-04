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
    /// Provides trusted certificates 
    /// </summary>
    /// <remarks>Trust anchors are root certificates for which leaf certificates that
    /// chain up to the anchor are presumed to be trusted.</remarks>
    public class TrustAnchorResolver : ITrustAnchorResolver
    {
        ICertificateResolver m_outgoingAnchors;
        ICertificateResolver m_incomingAnchors;
        
        /// <summary>
        /// Initializes an instance with a collection of trust anchors used across both incoming and outgoing messages.
        /// </summary>
        /// <param name="anchors">The trust anchors for this resolver.</param>
        public TrustAnchorResolver(X509Certificate2Collection anchors)
            : this(anchors, anchors)
        {
        }

        /// <summary>
        /// Initializes an instance with separate certificates for incoming and outgoing messages.
        /// </summary>
        /// <param name="outgoingAnchors">Trust anchors used to validate messages for sending</param>
        /// <param name="incomingAnchors">Trust anchors used to validate messages on reciept.</param>
        public TrustAnchorResolver(X509Certificate2Collection outgoingAnchors, X509Certificate2Collection incomingAnchors)
            : this(new UniformCertificateResolver(outgoingAnchors), new UniformCertificateResolver(incomingAnchors))
        {
        }

        /// <summary>
        /// Initializes an instance with a store of trust anchors used across both incoming and outgoing messages.
        /// </summary>
        /// <param name="anchors">The store of trust anchors for this resolver.</param>
        public TrustAnchorResolver(IX509CertificateStore anchors)
            : this(anchors, anchors)
        {
        }

        /// <summary>
        /// Initializes an instance with separate certificate stores for incoming and outgoing messages.
        /// </summary>
        /// <param name="outgoingAnchors">Trust anchor store used to validate messages for sending</param>
        /// <param name="incomingAnchors">Trust anchor store used to validate messages on reciept.</param>
        public TrustAnchorResolver(IX509CertificateStore outgoingAnchors, IX509CertificateStore incomingAnchors)
            : this(new UniformCertificateResolver(outgoingAnchors), new UniformCertificateResolver(incomingAnchors))
        {
        }
        
        /// <summary>
        /// Initializes an instance with a resolver for all messages.
        /// </summary>
        /// <param name="anchors">The resolver providing the anchor certificates.</param>
        public TrustAnchorResolver(ICertificateResolver anchors)
            : this(anchors, anchors)
        {
        }
        
        /// <summary>
        /// Initializes an instance with separate resolvers for incoming and outgoing messages.
        /// </summary>
        /// <param name="outgoingAnchors">The resolver to use for resolving trust anchors for outgoing messages</param>
        /// <param name="incomingAnchors">The resolver to use for resolving trust anchors for incoming messages</param>
        public TrustAnchorResolver(ICertificateResolver outgoingAnchors, ICertificateResolver incomingAnchors)
        {
            if (outgoingAnchors == null)
            {
                throw new ArgumentNullException("outgoingAnchors");
            }
            if (incomingAnchors == null)
            {
                throw new ArgumentNullException("incomingAnchors");
            }
            
            m_outgoingAnchors = outgoingAnchors;
            m_incomingAnchors = incomingAnchors;
        }


        /// <summary>
        /// The resolver for trust anchors for outgoing messages.
        /// </summary>
        public ICertificateResolver OutgoingAnchors
        {
            get 
            { 
                return m_outgoingAnchors;
            }
        }

        /// <summary>
        /// The resolver for trust anchors for incoming messages.
        /// </summary>
        public ICertificateResolver IncomingAnchors
        {
            get 
            { 
                return m_incomingAnchors;
            }
        }

        /// <summary>
        /// Loads certificates from a LocalMachine Certificate store named "NHINAnchors"
        /// </summary>
        public static TrustAnchorResolver CreateDefault()
        {
            using (SystemX509Store store = SystemX509Store.OpenAnchor())
            {
                UniformCertificateResolver anchors = new UniformCertificateResolver(store.GetAllCertificates());
                return new TrustAnchorResolver(anchors);
            }
        }
    }
}