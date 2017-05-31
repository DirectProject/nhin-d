using System.Xml.Serialization;
using Health.Direct.Common.Caching;

namespace Health.Direct.SmtpAgent.Config
{
    /// <summary>
    /// Extended settings for AddressManager.
    /// </summary>
    [XmlType]
    public class AddressManagerSettings
    {
        /// <summary>
        /// Simple constructor
        /// </summary>
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

        /// <summary>
        /// Hold <see cref="CacheSettings"/>
        /// </summary>
        [XmlElement]
        public CacheSettings CacheSettings
        {
            get;
            set;
        }
    }
}