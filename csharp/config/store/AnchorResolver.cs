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
using System.Net.Mail;

using Health.Direct.Common.Certificates;

namespace Health.Direct.Config.Store
{
    public class AnchorResolver : ITrustAnchorResolver
    {
        AnchorManager m_anchorManager;
        AnchorCertResolver m_incomingResolver;
        AnchorCertResolver m_outgoingResolver;
        
        public AnchorResolver(AnchorManager anchorManager)
        {
            if (anchorManager == null)
            {
                throw new ArgumentNullException("anchorManager");
            }
            
            m_anchorManager = anchorManager;
            m_incomingResolver = new AnchorCertResolver(anchorManager, true);
            m_outgoingResolver = new AnchorCertResolver(anchorManager, false);
        }
        
        #region ITrustAnchorResolver Members

        public ICertificateResolver IncomingAnchors
        {
            get 
            { 
                return m_incomingResolver;
            }
        }

        public ICertificateResolver OutgoingAnchors
        {
            get 
            { 
                return m_outgoingResolver;
            }
        }

        #endregion

        internal class AnchorCertResolver : ICertificateResolver
        {
            AnchorManager m_anchorManager;
            bool m_incoming;

            internal AnchorCertResolver(AnchorManager manager, bool incoming)
            {
                m_anchorManager = manager;
                m_incoming = incoming;
            }

            public event Action<ICertificateResolver, Exception> Error;

            public X509Certificate2Collection GetCertificates(MailAddress address)
            {
                if (address == null)
                {
                    throw new ArgumentNullException("address");
                }
                
                return this.GetCertificatesForDomain(address.Address);
            }
            
            public X509Certificate2Collection GetCertificatesForDomain(string domain)
            {
                if (string.IsNullOrEmpty(domain))
                {
                    throw new ArgumentException("domain");
                }
                
                try
                {
                    Anchor[] anchors = null;
                    if (m_incoming)
                    {
                        anchors = m_anchorManager.GetIncoming(domain);
                    }
                    else
                    {
                        anchors = m_anchorManager.GetOutgoing(domain);
                    }

                    return this.ToCerts(anchors);
                }
                catch(Exception ex)
                {
                    this.Error.NotifyEvent(this, ex);
                    throw;
                }
            }

            X509Certificate2Collection ToCerts(Anchor[] anchors)
            {
                if (anchors == null)
                {
                    return null;
                }
                X509Certificate2Collection certs = new X509Certificate2Collection();
                for (int i = 0; i < anchors.Length; ++i)
                {
                    certs.Add(anchors[i].ToX509Certificate());
                }

                return certs;
            }
        }
    }
}