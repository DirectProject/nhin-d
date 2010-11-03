using System;

using Health.Direct.Agent.Config;
using Health.Direct.Common.Diagnostics;

using System.Xml.Serialization;

namespace Health.Direct.Sample.ScriptAgent
{
    [XmlType("SmtpAgentConfig")]
    public class SmtpAgentSettings : AgentSettings
    {
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
                throw new Exception("Log Settings not specified");
            }
            this.LogSettings.Validate();
        }
        
        public new static SmtpAgentSettings LoadFile(string configFilePath)
        {
            return LoadFile<SmtpAgentSettings>(configFilePath);
        }
    }
}