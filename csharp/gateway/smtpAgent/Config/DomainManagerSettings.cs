using System.Xml.Serialization;
using Health.Direct.Common.Caching;

namespace Health.Direct.SmtpAgent.Config
{
    /// <summary>
    /// Extended settings for DomainManagerSettings.
    /// </summary>
    [XmlType]
    public class DomainManagerSettings
    {
        /// <summary>
        /// Simple constructor
        /// </summary>
        public DomainManagerSettings() { }

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
