/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Security.Cryptography.X509Certificates;

using NHINDirect.Config.Store;
using NHINDirect.Certificates;
using NHINDirect.Config.Client;
using NHINDirect.Config.Client.CertificateService;

namespace Health.Direct.SmtpAgent
{
    public class ConfigAnchorResolver : ITrustAnchorResolver
    {
        CertificateResolver m_incomingResolver;
        CertificateResolver m_outgoingResolver;

        public ConfigAnchorResolver(ClientSettings clientSettings)
        {
            if (clientSettings == null)
            {
                throw new ArgumentNullException("clientSettings");
            }
            
            m_incomingResolver = new CertificateResolver(new AnchorIndex(clientSettings, true));
            m_outgoingResolver = new CertificateResolver(new AnchorIndex(clientSettings, false));
        }

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
        
        internal class AnchorIndex : IX509CertificateIndex
        {
            ClientSettings m_clientSettings;
            bool m_incoming;
            
            internal AnchorIndex(ClientSettings clientSettings, bool incoming)
            {
                m_clientSettings = clientSettings;
                m_incoming = incoming;
            }
            
            public X509Certificate2Collection this[string subjectName]
            {
                get 
                { 
                    X509Certificate2Collection matches;
                    using(AnchorStoreClient client = this.CreateClient())
                    {
                        if (m_incoming)
                        {
                            matches = client.GetIncomingAnchorX509Certificates(subjectName, EntityStatus.Enabled);
                        }
                        else
                        {
                            matches = client.GetOutgoingAnchorX509Certificates(subjectName, EntityStatus.Enabled);
                        }
                    }
                                        
                    return matches;
                }
            }

            AnchorStoreClient CreateClient()
            {
                return new AnchorStoreClient(m_clientSettings.Binding, m_clientSettings.Endpoint);
            }
        }
    }
}