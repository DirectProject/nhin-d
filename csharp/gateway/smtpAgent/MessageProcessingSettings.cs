using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;
using System.IO;

namespace NHINDirect.SmtpAgent
{
    public class MessageProcessingSettings
    {
        public MessageProcessingSettings()
        {
        }

        [XmlElement("CopyFolder")]
        public string CopyFolder
        {
            get;
            set;
        }

        [XmlIgnore]
        internal bool HasCopyFolder
        {
            get
            {
                return !(string.IsNullOrEmpty(this.CopyFolder));
            }
        }
        
        internal virtual void EnsureFolders()
        {
            if (this.HasCopyFolder)
            {
                //
                // If the directory already exists, CreateDirectory does nothing
                //
                Directory.CreateDirectory(this.CopyFolder);
            }
        }
    }

    [XmlType("RawMessage")]
    public class RawMessageSettings : MessageProcessingSettings
    {
        public RawMessageSettings()
            : base()
        {
        }
    }

    [XmlType("ProcessIncoming")]
    public class ProcessIncomingSettings : MessageProcessingSettings
    {
        public ProcessIncomingSettings()
            : base()
        {
        }
    }

    [XmlType("ProcessOutgoing")]
    public class ProcessOutgoingSettings : MessageProcessingSettings
    {
        public ProcessOutgoingSettings()
            : base()
        {
        }
    }

    [XmlType("ProcessBadMessage")]
    public class ProcessBadMessageSettings : MessageProcessingSettings
    {
        public ProcessBadMessageSettings()
            : base()
        {
        }
    }
}
