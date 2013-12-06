/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Joe Shook	    jshook@kryptiq.com
   
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.IO;
using System.Text;
using System.Xml.Serialization;
using Health.Direct.Agent.Config;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Client;
using Health.Direct.Common.Container;

namespace Health.Direct.SmtpAgent
{
    [XmlType("SmtpAgentConfig")]
    public class SmtpAgentSettings : AgentSettings
    {
        public const int DefaultMaxDomainRecipients = 10;
        public const int DefaultServicePointConnectionLimit = 100;

        RawMessageSettings m_rawMessageSettings;
        ProcessIncomingSettings m_incomingSettings;
        ProcessOutgoingSettings m_outgoingSettings;
        ProcessBadMessageSettings m_badMessageSettings;
        InternalMessageSettings m_internalMessageSettings;
        NotificationSettings m_notificationSettings;
        Route[] m_incomingRoutes;
        int m_maxDomainRecipients = DefaultMaxDomainRecipients;
        int m_servicePointConnectionLimit = DefaultServicePointConnectionLimit;
        
        /// <summary>
        /// Max number of .NET connections opened to the middle tier.
        /// </summary>
        [XmlElement("ServicePointConnectionLimit")]
        public int ServicePointConnectionLimit
        {
            get { return m_servicePointConnectionLimit; }
            set { m_servicePointConnectionLimit = value; }
        }



        //--------------------------------------------------------
        //
        // Log Settings
        //
        //--------------------------------------------------------
        /// <summary>
        /// REQUIRED: Log File Parameters
        /// </summary>
        [XmlElement("Log")]
        public LogFileSettings LogSettings
        {
            get;
            set;
        }

        //--------------------------------------------------------
        //
        // Config for connnecting to any Config Web Services
        // See interfaces in configService
        //
        //--------------------------------------------------------

        /// <summary>
        /// If this gateway is configured to interact with a DomainManager web Service. 
        /// </summary>
        [XmlElement("DomainManager")]
        public ClientSettings DomainManagerService
        {
            get;
            set;
        }

        [XmlIgnore]
        public bool HasDomainManagerService
        {
            get
            {
                return (this.DomainManagerService != null);
            }
        }

        /// <summary>
        /// If this gateway is configured to interact with an AddressManager web service
        /// </summary>
        [XmlElement("AddressManager")]
        public ClientSettings AddressManager
        {
            get;
            set;
        }

        [XmlIgnore]
        public bool HasAddressManager
        {
            get
            {
                return (this.AddressManager != null);
            }
        }

        /// <summary>
        /// Limit the # of domain recipients on an incoming message - to prevent DOS attacks
        /// </summary>
        public int MaxIncomingDomainRecipients
        {
            get
            {
                return m_maxDomainRecipients;
            }
            set
            {
                m_maxDomainRecipients = value;
            }
        }


        /// <summary>
        /// If this gateway is configured to interact with an MdnMonitor web service
        /// </summary>
        [XmlElement("MdnMonitor")]
        public ClientSettings MdnMonitor
        {
            get;
            set;
        }

        [XmlIgnore]
        public bool HasMdnManager
        {
            get
            {
                return (MdnMonitor != null);
            }
        }

        
        //--------------------------------------------------------
        //
        // Message Processing
        //
        //--------------------------------------------------------

        /// <summary>
        /// Configure if the Agent should automatically issue MDN messages in reponse
        /// to MDN requests
        /// </summary>
        [XmlElement("Notifications")]
        public NotificationSettings Notifications
        {
            get
            {
                if (m_notificationSettings == null)
                {
                    m_notificationSettings = new NotificationSettings();
                }

                return m_notificationSettings;
            }
            set
            {
                m_notificationSettings = value;
            }
        }

        /// <summary>
        /// Does this server allow messaging WITHIN the domain?
        /// If not, then all messages originating from within the domain are considered OUTGOING
        /// </summary>
        [XmlElement("InternalMessage")]
        public InternalMessageSettings InternalMessage
        {
            get
            {
                if (m_internalMessageSettings == null)
                {
                    m_internalMessageSettings = new InternalMessageSettings();
                }
                return m_internalMessageSettings;
            }
            set
            {
                m_internalMessageSettings = value;
            }
        }

        /// <summary>
        /// Message Routes: 
        /// If working with an Address Service: an address can have an associated Type
        /// When using the FolderRoute you can set up routes for address types, where a route deposits a message in a specific folder.
        /// When using a PluginRoute the plugin can use the address types for routing based on the plugin implementation.
        /// </summary>
        [XmlArray("IncomingRoutes")]
        [XmlArrayItem("Route", typeof(FolderRoute))]
        [XmlArrayItem("PluginRoute", typeof(PluginRoute))]
        public Route[] IncomingRoutes
        {
            get
            {
                if (m_incomingRoutes == null)
                {
                    m_incomingRoutes = new Route[0];
                }

                return m_incomingRoutes;
            }
            set
            {
                m_incomingRoutes = value;
            }
        }

        [XmlIgnore]
        public bool HasRoutes
        {
            get
            {
                return (!m_incomingRoutes.IsNullOrEmpty());
            }
        }

        [XmlIgnore]
        internal bool AllowInternalRelay
        {
            get
            {
                return this.InternalMessage.EnableRelay;
            }
        }

        //--------------------------------------------------------
        //
        // Debugging
        //
        //--------------------------------------------------------
        /// <summary>
        /// (OPTIONAL) save a RAW copy of each message into a folder
        /// </summary>                
        [XmlElement("RawMessage")]
        public RawMessageSettings RawMessage
        {
            get
            {
                if (m_rawMessageSettings == null)
                {
                    m_rawMessageSettings = new RawMessageSettings();
                }

                return m_rawMessageSettings;
            }
            set
            {
                m_rawMessageSettings = value;
            }
        }

        /// <summary>
        /// (OPTIONAL) Configure how incoming messages are processed
        /// </summary>
        [XmlElement("ProcessIncoming")]
        public ProcessIncomingSettings Incoming
        {
            get
            {
                if (m_incomingSettings == null)
                {
                    m_incomingSettings = new ProcessIncomingSettings();
                }

                return m_incomingSettings;
            }
            set
            {
                m_incomingSettings = value;
            }
        }

        /// <summary>
        /// OPTIONAL: Configure how outgoing messages are processed
        /// </summary>
        [XmlElement("ProcessOutgoing")]
        public ProcessOutgoingSettings Outgoing
        {
            get
            {
                if (m_outgoingSettings == null)
                {
                    m_outgoingSettings = new ProcessOutgoingSettings();
                }

                return m_outgoingSettings;
            }
            set
            {
                m_outgoingSettings = value;
            }
        }

        /// <summary>
        /// Optional: Configure how bad messages are processed
        /// </summary>
        [XmlElement("BadMessage")]
        public ProcessBadMessageSettings BadMessage
        {
            get
            {
                if (m_badMessageSettings == null)
                {
                    m_badMessageSettings = new ProcessBadMessageSettings();
                }

                return m_badMessageSettings;
            }
            set
            {
                m_badMessageSettings = value;
            }
        }

        //--------------------------------------------------------
        //
        // IOC Container
        //
        //--------------------------------------------------------
        /// <summary>
        /// Use this to drop in extensions
        /// </summary>
        [XmlElement]
        public SimpleContainerSettings Container
        {
            get;
            set;
        }
        
        [XmlIgnore]
        public bool HasContainer
        {
            get
            {
                return (this.Container != null);
            }
        }
                       
        public override void Validate()
        {
            base.Validate();

            if (this.LogSettings == null)
            {
                throw new SmtpAgentException(SmtpAgentError.MissingLogSettings);
            }

            this.LogSettings.Validate();
            this.InternalMessage.Validate();
            this.Notifications.Validate();
            this.RawMessage.Validate();
            this.BadMessage.Validate();
            this.Incoming.Validate();
            this.Outgoing.Validate();

            if (this.HasDomainManagerService)
            {
                this.DomainManagerService.Validate();
            }
            if (this.HasAddressManager)
            {
                this.AddressManager.Validate();
            }
            if (!m_incomingRoutes.IsNullOrEmpty())
            {
                Array.ForEach<Route>(m_incomingRoutes, x => x.Validate());
            }
        }

        public void EnsureFolders()
        {
            this.RawMessage.EnsureFolders();
            this.Incoming.EnsureFolders();
            this.Outgoing.EnsureFolders();
            this.BadMessage.EnsureFolders();
        }

        public static SmtpAgentSettings LoadSettings(string configFilePath)
        {
            ExtensibleXmlSerializer serializer = new ExtensibleXmlSerializer();
            serializer.AddElementOption<CertificateSettings>("Resolvers", "ServiceResolver", typeof(CertServiceResolverSettings));
            serializer.AddElementOption<TrustAnchorSettings>("Resolver", "ServiceResolver", typeof(AnchorServiceResolverSettings));
            serializer.AddElementOption<DomainSettings>("Resolver", "ServiceResolver", typeof(DomainServiceResolverSettings));

            using (Stream stream = File.OpenRead(configFilePath))
            {
                return serializer.Deserialize<SmtpAgentSettings>(stream);
            }
        }

        public override string ToString()
        {
            StringBuilder builder = new StringBuilder(GetType().Name).Append("(");
            builder.Append("Incoming=").Append(Incoming).Append(", ");
            builder.Append("Outgoing=").Append(Outgoing);
            return builder.Append(")").ToString();
        }
    }
}
