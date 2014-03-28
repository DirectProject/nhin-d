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
using System.Security.Cryptography.X509Certificates;

using Health.Direct.Common.Certificates;

namespace Health.Direct.Agent.Config
{
    /// <summary>
    /// Configuration for a machine store-based certificate resolver.
    /// </summary>
    [XmlType("MachineCertificateStore")]
    public class MachineCertResolverSettings : CertResolverSettings
    {
        /// <summary>
        /// Creates an instance. Normally called through XML deserialization.
        /// </summary>
        public MachineCertResolverSettings()
        {
            this.Location = StoreLocation.LocalMachine;
        }
        
        /// <summary>
        /// The name of this store.
        /// </summary>
        [XmlElement]
        public string Name 
        {   
            get;
            set;
        }
        
        /// <summary>
        /// The location of this store (machine or user)
        /// </summary>
        [XmlElement]
        public StoreLocation Location 
        { 
            get; 
            set; 
        }

        /// <summary>
        /// Validates configuration settings.
        /// </summary>
        public override void Validate()
        {
            if (string.IsNullOrEmpty(this.Name))
            {
                throw new AgentConfigException(AgentConfigError.MissingMachineStoreName);
            }
        }   
                     
        /// <summary>
        /// Creates the maachine store based certificate resolver.
        /// </summary>
        /// <returns>An instance of a machine-based certificate store resolver.</returns>
        public override ICertificateResolver CreateResolver()
        {
            this.Validate();
            
            using(SystemX509Store store = this.OpenStore())
            {
                return store.CreateResolver();
            }
        }
        
        /// <summary>
        /// Opens the machine-based certificate store.
        /// </summary>
        /// <returns>The configured store.</returns>
        public SystemX509Store OpenStore()
        {
            return new SystemX509Store(CryptoUtility.OpenStoreRead(this.Name, this.Location), null);
        }
    }
}