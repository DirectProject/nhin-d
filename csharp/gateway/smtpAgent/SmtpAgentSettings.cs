using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NHINDirect.Agent.Config;
using NHINDirect.Diagnostics;
using System.Xml.Serialization;

namespace NHINDirect.SmtpAgent
{
    [XmlType("SmtpAgentConfig")]
    public class SmtpAgentSettings : AgentSettings
    {
        RawMessageSettings m_rawMessageSettings;
        ProcessIncomingSettings m_incomingSettings;
        ProcessOutgoingSettings m_outgoingSettings;
        ProcessBadMessageSettings m_badMessageSettings;
        
        public SmtpAgentSettings()
        {
        }
        
        [XmlElement("Log")]
        public LogFileSettings LogSettings
        {
            get;
            set;
        }
        
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
        
        public override void Validate()
        {
            if (this.LogSettings == null)
            {
                throw new ArgumentNullException("Log Settings not specified");
            }
            this.LogSettings.Validate();
        }
        
        public static SmtpAgentSettings LoadFile(string configFilePath)
        {
            return AgentSettings.LoadFile<SmtpAgentSettings>(configFilePath);
        }
    }
}
