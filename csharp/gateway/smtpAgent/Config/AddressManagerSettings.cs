using System.Xml.Serialization;

namespace Health.Direct.SmtpAgent.Config
{
    /// <summary>
    /// Extended settings for AddressManager.
    /// </summary>
    [XmlType]
    public class AddressManagerSettings
    {
        public AddressManagerSettings()
        {
            EnableDomainSearch = false;
        }

        /// <summary>
        /// Enable searching the email address and the domain.
        /// Usefull for Org level certs only HISPs.
        /// </summary>
        [XmlElement]
        public bool EnableDomainSearch { get; set; }
    }
}