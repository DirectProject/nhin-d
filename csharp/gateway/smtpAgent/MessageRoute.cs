using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Threading;
using Health.Direct.Agent;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Config.Store;
using Health.Direct.Common.Routing;
using Health.Direct.Common.Extensions;
using System.Xml.Serialization;

namespace Health.Direct.SmtpAgent
{        
    /// <summary>
    /// Default Message Route 
    /// - Copies messages to folders
    /// - Uses Round Robin over folders as default
    /// - If a write fails, switches (for that particular episode) to random writes over other folders
    /// </summary>
    public class MessageRoute : Route
    {
        Func<ISmtpMessage, string, bool> m_copyHandler;
        FolderBalancer<ISmtpMessage> m_loadBalancer;
                
        public MessageRoute()
        {
            m_copyHandler = this.CopyToFolder;
            m_loadBalancer = new FolderBalancer<ISmtpMessage>(m_copyHandler);
        }

        /// <summary>
        /// Route the messages to one of these folders
        /// If there are multiple, round robin
        /// DO NOT alter this once the system is running
        /// </summary>
        [XmlElement("CopyFolder")]
        public string[] CopyFolders
        {
            get
            {
                return m_loadBalancer.Receivers;
            }
            set
            {
                m_loadBalancer.Receivers = value;
            }
        }

        [XmlIgnore]
        internal bool HasCopyFolders
        {
            get
            {
                return !(this.CopyFolders.IsNullOrEmpty());
            }
        }
        
        [XmlIgnore]
        public Func<ISmtpMessage, string, bool> CopyMessageHandler
        {
            get
            {
                return m_loadBalancer.DataCopier;
            }
            set
            {
                m_loadBalancer.DataCopier = value ?? this.CopyToFolder;
            }
        }
        
        [XmlIgnore]
        public FolderBalancer<ISmtpMessage> LoadBalancer
        {
            get
            {
                return m_loadBalancer;
            }
        }
        
        public override void Validate()
        {
            base.Validate();
            if (!this.HasCopyFolders)
            {
                throw new SmtpAgentException(SmtpAgentError.NoFoldersInRoute);
            }
        }

        public override void Init()
        {
            this.Validate();            
        }

        public override bool Process(ISmtpMessage message)
        {
            return m_loadBalancer.Process(message);
        }   
             
        bool CopyToFolder(ISmtpMessage message, string folderPath)
        {
            try
            {
                string uniqueFileName = Extensions.CreateUniqueFileName();
                message.SaveToFile(Path.Combine(folderPath, uniqueFileName));
                return true;
            }
            catch
            {
            }
            
            return false;
        }
    }
}
