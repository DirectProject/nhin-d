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
using System.IO;
using System.Xml.Serialization;

using Health.Direct.Common.Certificates;
using Health.Direct.Common.Cryptography;
using Health.Direct.Common.Domains;

namespace Health.Direct.Agent.Config
{
    /// <summary>
    /// An XML-serializable representation of agent configuration settings.
    /// </summary>
    /// <example>
    /// <code>
    /// AgentSettings settings = AgentSettings.LoadFile("config.xml");
    /// settings.Validate()
    /// DirectAgent agent = settings.CreateAgent();
    /// </code>
    /// </example>
    [XmlType("AgentSettings")]
    public class AgentSettings
    {
        CryptographerSettings m_cryptographerSettings;
        bool m_allowNonWrappedIncoming = true;
        bool m_wrapOutgoing = true;
        
        /// <summary>
        /// Basic constructor (normally called via XML serialization in one of the Load* static methods in this class)
        /// </summary>
        public AgentSettings()
        {
        }
        
        /// <summary>
        /// By default, the agent will accept messages that are NOT wrapped with message/rfc822 - i.e. sent by any SMIME client
        /// </summary>
        [XmlElement]
        public bool AllowNonWrappedIncoming
        {
            get
            {
                return m_allowNonWrappedIncoming;
            }
            set
            {
                m_allowNonWrappedIncoming = value;
            }
        }
        
        /// <summary>
        /// By default, the agent will wrap all outgoing messages into message/rfc822 before encrypting/signing
        /// </summary>
        [XmlElement]
        public bool WrapOutgoing
        {
            get
            {
                return m_wrapOutgoing;
            }
            set
            {
                m_wrapOutgoing = value;
            }
        }

        /// <summary>
        /// The domains the agent manages
        /// </summary>
        [XmlElement("Domain", typeof(string))]
        public string[] Domains
        {
            get;
            set;
        }

        /// <summary>
        /// <see cref="DomainSettings"/> for domain tenant selection
        /// </summary>
        [XmlElement("Domains")]
        public DomainSettings DomainTenants
        {
            get;
            set;
        }

        /// <summary>
        /// <see cref="CertificateSettings"/> for private certificates
        /// </summary>
        [XmlElement("PrivateCerts")]
        public CertificateSettings PrivateCerts
        {
            get;
            set;
        }

        /// <summary>
        /// <see cref="CertificateSettings"/> for public certificates
        /// </summary>
        [XmlElement("PublicCerts")]
        public CertificateSettings PublicCerts
        {
            get;
            set;
        }

        /// <summary>
        /// <see cref="TrustAnchorSettings"/> for trust anchors used by the agent.
        /// </summary>
        [XmlElement("Anchors")]
        public TrustAnchorSettings Anchors
        {
            get;
            set;
        }   
        
        /// <summary>
        /// <see cref="CryptographerSettings"/> defining the cryptography methods used by the agent.
        /// </summary>
        [XmlElement("Cryptographer")]
        public CryptographerSettings Cryptographer
        {
            get
            {
                if (m_cryptographerSettings == null)
                {
                    m_cryptographerSettings = new CryptographerSettings();
                }
                
                return m_cryptographerSettings;
            }
            set
            {
                m_cryptographerSettings = value;
            }
        }
        
        /// <summary>
        /// Settings that control how the trust model is enforced
        /// OPTIONAL: if not supplied, system runs with defaults
        /// </summary>
        [XmlElement("Trust")]
        public TrustModelSettings Trust
        {
            get;
            set;
        }
        
        /// <summary>
        /// Validates settings, throwing <see cref="AgentConfigException"/> for validation errors.
        /// </summary>
        /// <exception cref="AgentConfigException">When configuration settings are missing or malformed.</exception>
        public virtual void Validate()
        {
            ValidateAgentDomains();
                        
            if (this.PrivateCerts == null)
            {
                throw new AgentConfigException(AgentConfigError.MissingPrivateCertSettings);
            }
            this.PrivateCerts.Validate(AgentConfigError.MissingPrivateCertResolver);
            
            if (this.PublicCerts == null)
            {
                throw new AgentConfigException(AgentConfigError.MissingPublicCertSettings);
            }
            this.PublicCerts.Validate(AgentConfigError.MissingPublicCertResolver);
            
            if (this.Anchors == null)
            {
                throw new AgentConfigException(AgentConfigError.MissingAnchorSettings);
            }
            this.Anchors.Validate();
            
            if (this.Trust != null)
            {
                this.Trust.Validate();
            }
        }

        //
        // AgentDomains can either statically configure domains or use a DomainSettings
        // configuration to plug in a domain configuration solution.
        // Static configured domains are validated at initialization.  Custom solutions
        // validate at runtime as needed.
        // Static configured domains are domains in the root confuration.
        //
        private void ValidateAgentDomains()
        {
            if (this.Domains == null)
            return;
            
            if (!AgentDomains.Validate(this.Domains))
            {
                throw new AgentConfigException(AgentConfigError.InvalidDomainList);
            }
        }

        /// <summary>
        /// Creates a agent from settings.
        /// </summary>
        /// <returns>The configured agent instance.</returns>
        public DirectAgent CreateAgent()
        {
            this.Validate();
            
            ICertificateResolver privateCerts = this.PrivateCerts.CreateResolver();
            ICertificateResolver publicCerts = this.PublicCerts.CreateResolver();
            ITrustAnchorResolver trustAnchors = this.Anchors.Resolver.CreateResolver();
            TrustModel trustModel = (this.Trust != null) ? this.Trust.CreateTrustModel() : TrustModel.Default;
            SMIMECryptographer cryptographer = this.Cryptographer.Create();

            IDomainResolver domainResolver = this.CreateResolver();
            
            DirectAgent agent = new DirectAgent(domainResolver, privateCerts, publicCerts, trustAnchors, trustModel, cryptographer);
            agent.AllowNonWrappedIncoming = m_allowNonWrappedIncoming;
            agent.WrapMessages = m_wrapOutgoing;
            
            return agent;
        }

        //
        // Agent hands back a StaticDomainTenancy to maintain original static domain configuration
        // New DomainSettings hands back a configured IDomainTenancy
        //
        private IDomainResolver CreateResolver()
        {
            if(this.DomainTenants == null)
            {
                return new StaticDomainResolver(this.Domains);
            }
            return this.DomainTenants.CreateResolver();
        }

        /// <summary>
        /// Load agent settings from an XML string containing settings.
        /// </summary>
        /// <param name="configXml">An XML string containing settings</param>
        /// <returns>Agent settings</returns>
        public static AgentSettings Load(string configXml)
        {
            return Load<AgentSettings>(configXml);
        }

        /// <summary>
        /// Load agent settings from an XML string containing settings.
        /// </summary>
        /// <param name="configXml">An XML string containing settings</param>
        /// <returns>Agent settings</returns>
        /// <typeparam name="T">Must be <see cref="AgentSettings"/></typeparam>
        public static T Load<T>(string configXml)
            where T : AgentSettings
        {
            XmlSerializer serializer = new XmlSerializer(typeof(T));
            using (StringReader reader = new StringReader(configXml))
            {
                return (T) serializer.Deserialize(reader);
            }
        }

        /// <summary>
        /// Loads agent configurations from a file.
        /// </summary>
        /// <param name="filePath">A string containing a valid path and filename of a valid XML configuration file.</param>
        /// <returns>The settings defined in the file</returns>
        public static AgentSettings LoadFile(string filePath)
        {
            return LoadFile<AgentSettings>(filePath);
        }

        /// <summary>
        /// Loads agent configurations from a file.
        /// </summary>
        /// <typeparam name="T">Must be <see cref="AgentSettings"/></typeparam>
        /// <param name="filePath">A string containing a valid path and filename of a valid XML configuration file.</param>
        /// <returns>The settings defined in the file</returns>
        public static T LoadFile<T>(string filePath)
            where T : AgentSettings
        {
            using(StreamReader reader = new StreamReader(filePath))
            {
                return Load<T>(reader.ReadToEnd());
            }
        }
    }
}