using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NHINDirect.Agent.Config;
using NHINDirect.Diagnostics;
using System.Xml.Serialization;

namespace NHINDirect.ScriptAgent
{
    [XmlType("SmtpAgentConfig")]
    public class SmtpAgentSettings : AgentSettings
    {
        public SmtpAgentSettings()
        {
        }
        
        [XmlElement("Log")]
        public LogFileSettings LogSettings
        {
            get;
            set;
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
