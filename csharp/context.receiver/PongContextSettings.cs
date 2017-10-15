using System.IO;
using System.Xml.Serialization;
using Health.Direct.SmtpAgent;

namespace Health.Direct.Context.Loopback.Receiver
{
    [XmlType("PongContextSettings")]
    public class PongContextSettings
    {
        public PongContextSettings()
        {
        }

        /// <summary>
        /// Smtp pickup folder
        /// </summary>
        [XmlElement("PickupFolder")]
        public string PickupFolder
        {
            get;
            set;
        }

        [XmlIgnore]
        internal bool HasPickupFolder
        {
            get
            {
                return !(string.IsNullOrEmpty(PickupFolder));
            }
        }

        public void Validate()
        {
            if (!this.HasPickupFolder)
            {
                return;
            }

            if (!Directory.Exists(PickupFolder))
            {
                throw new SmtpAgentException(SmtpAgentError.MailPickupFolderDoesNotExist);
            }
        }
    }
}