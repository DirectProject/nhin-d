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
using System.Security.Cryptography.X509Certificates;

namespace NHINDirect.Certificates
{
    public class TrustAnchorResolver : ITrustAnchorResolver
    {
        ICertificateResolver m_outgoingAnchors;
        ICertificateResolver m_incomingAnchors;
        
        public TrustAnchorResolver(X509Certificate2Collection anchors)
            : this(anchors, anchors)
        {
        }

        public TrustAnchorResolver(X509Certificate2Collection outgoingAnchors, X509Certificate2Collection incomingAnchors)
            : this(new UniformCertificateStore(outgoingAnchors), new UniformCertificateStore(incomingAnchors))
        {
        }

        public TrustAnchorResolver(IX509CertificateStore anchors)
            : this(anchors, anchors)
        {
        }

        public TrustAnchorResolver(IX509CertificateStore outgoingAnchors, IX509CertificateStore incomingAnchors)
            : this(new UniformCertificateStore(outgoingAnchors), new UniformCertificateStore(incomingAnchors))
        {
        }
                
        public TrustAnchorResolver(ICertificateResolver anchors)
            : this(anchors, anchors)
        {
        }
        
        public TrustAnchorResolver(ICertificateResolver outgoingAnchors, ICertificateResolver incomingAnchors)
        {
            if (outgoingAnchors == null || incomingAnchors == null)
            {
                throw new ArgumentNullException();
            }
            
            m_outgoingAnchors = outgoingAnchors;
            m_incomingAnchors = incomingAnchors;
        }


        public ICertificateResolver OutgoingAnchors
        {
            get 
            { 
                return m_outgoingAnchors;
            }
        }

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
                UniformCertificateStore anchors = new UniformCertificateStore(store.GetAllCertificates());
                return new TrustAnchorResolver(anchors);
            }
        }
    }
}
