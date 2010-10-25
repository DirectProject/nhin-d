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
using System.Xml.Serialization;

using Health.Direct.Common.Certificates;

namespace Health.Direct.Agent.Config
{
    /// <summary>
    /// Configuration for a machine store based set of trust anchors.
    /// </summary>
    [XmlType("MachineAnchorStore")]
    public class MachineAnchorResolverSettings : TrustAnchorResolverSettings
    {
        /// <summary>
        /// Creates an instance, noramlly called from XML deserialization.
        /// </summary>
        public MachineAnchorResolverSettings()
        {
        }
        
        /// <summary>
        /// Configuration settings for trust anchors for incoming messages.
        /// </summary>
        [XmlElement]
        public MachineCertResolverSettings Incoming 
        {
            get; 
            set;
        }

        /// <summary>
        /// Configuration settings for trust anchors for outgoing messages.
        /// </summary>
        [XmlElement]
        public MachineCertResolverSettings Outgoing 
        { 
            get; 
            set; 
        }

        /// <summary>
        /// Validates the configuration settings.
        /// </summary>
        public override void Validate()
        {
            if (this.Incoming == null)
            {
                throw new AgentConfigException(AgentConfigError.MissingIncomingAnchors);
            }
            this.Incoming.Validate();
            
            if (this.Outgoing == null)
            {
                throw new AgentConfigException(AgentConfigError.MissingOutgoingAnchors);
            }
            this.Outgoing.Validate();
        }
        
        /// <summary>
        /// Creates the machine store trust anchor resolver from configuration settings.
        /// </summary>
        /// <returns>The configured machine store trust anchor resolver.</returns>
        public override ITrustAnchorResolver CreateResolver()
        {
            this.Validate();

            SystemX509Store outgoing = null;
            SystemX509Store incoming = null;
            
            try
            {
                outgoing = this.Outgoing.OpenStore();
                incoming = this.Incoming.OpenStore();

                return new TrustAnchorResolver((IX509CertificateStore)outgoing, (IX509CertificateStore) incoming);
            }
            finally
            {
                if (outgoing != null)
                {
                    outgoing.Dispose();
                }
                if (incoming != null)
                {
                    incoming.Dispose();
                }
            }
        }
    }
}