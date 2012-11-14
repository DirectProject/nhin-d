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
using System.Xml.Serialization;
using System.Security.Cryptography.X509Certificates;

namespace Health.Direct.Agent.Config
{
    /// <summary>
    /// Configuration for TrustModel enforcement
    /// </summary>
    public class TrustModelSettings
    {        
        /// <summary>
        /// Constructor
        /// </summary>
        public TrustModelSettings()
        {
            this.RevocationCheckMode = TrustChainValidator.DefaultRevocationCheckMode;
            this.RevocationCheckGranularity = TrustChainValidator.DefaultRevocationGranularity;
        }
        
        /// <summary>
        /// When attempting to resolve intermediate cert issuers, follow the issuance chain to this depth
        /// <remarks>
        /// This is used to prevent DOS - accidental or otherwise. 
        /// <seealso cref="TrustChainValidator"/>
        /// </remarks>
        /// </summary>
        [XmlElement]
        public int MaxIssuerChainLength
        {
            get;
            set;
        }
        
        /// <summary>
        /// Online, or offline revocation checking
        /// </summary>
        [XmlElement]
        public X509RevocationMode RevocationCheckMode
        {
            get;
            set;
        }
        
        /// <summary>
        /// Whether to check the entire certificate chain, or just the end cert..
        /// </summary>
        [XmlElement]
        public X509RevocationFlag RevocationCheckGranularity
        {
            get;
            set;
        }

        /// <summary>
        /// Revocation Flags
        /// </summary>
        [XmlArray("ProblemFlags")]
        [XmlArrayItem("Flag")]
        public X509ChainStatusFlags[] ProblemFlags
        {
            get;
            set;
        }
        
        /// <summary>
        /// Timeouts used when doing online revocation checking, such as when downloading crls
        /// </summary>
        [XmlElement("Timeout")]
        public int TimeoutMilliseconds
        {
            get;
            set;
        }
        
        /// <summary>
        /// Create a Trust Model from the given settings
        /// </summary>
        /// <returns>TrustModel</returns>
        public TrustModel CreateTrustModel()
        {
            TrustChainValidator validator = new TrustChainValidator();
            validator.RevocationCheckMode = this.RevocationCheckMode;
            validator.RevocationCheckGranularity = this.RevocationCheckGranularity;
            if (this.MaxIssuerChainLength > 0)
            {
                validator.MaxIssuerChainLength = this.MaxIssuerChainLength;
            }                
            if (this.TimeoutMilliseconds > 0)
            {
                validator.ValidationPolicy.UrlRetrievalTimeout = TimeSpan.FromMilliseconds(this.TimeoutMilliseconds);
            }
            
            TrustModel trustModel = new TrustModel(validator);
            if (this.ProblemFlags != null)
            {
                X509ChainStatusFlags flags = X509ChainStatusFlags.NoError;
                foreach(X509ChainStatusFlags flag in this.ProblemFlags)
                {
                    flags = (flags | flag);
                }
                trustModel.CertChainValidator.ProblemFlags = flags;
            }
            
            return trustModel;
        }
                
        internal void Validate()
        {
        }
    }
}