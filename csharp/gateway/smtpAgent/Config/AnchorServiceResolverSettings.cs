using System.Xml.Serialization;
using Health.Direct.Agent.Config;
using Health.Direct.Common.Caching;
using Health.Direct.Common.Certificates;
using Health.Direct.Config.Client;

namespace Health.Direct.SmtpAgent.Config
{
    public class AnchorServiceResolverSettings : TrustAnchorResolverSettings
    {
        [XmlElement]
        public ClientSettings ClientSettings
        {
            get;
            set;
        }

        [XmlElement]
        public CacheSettings CacheSettings
        {
            get;
            set; 
        }

        /// <summary>
        /// If true, will NEVER look for address specific certificates
        /// False by default.
        /// 
        /// Use this if you are never going to issue user specific certificates. 
        /// This will eliminate 1 roundtrip to the Config Service for every message. 
        /// </summary>
        [XmlElement]
        public bool OrgCertificatesOnly = false;

        public override ITrustAnchorResolver CreateResolver()
        {
            ConfigAnchorResolver resolver = new ConfigAnchorResolver(this.ClientSettings, this.CacheSettings);
            resolver.OrgCertificatesOnly = this.OrgCertificatesOnly;
            
            return resolver;
        }

        public override void Validate()
        {
            if (this.ClientSettings == null)
            {
                throw new SmtpAgentException(SmtpAgentError.MissingAnchorResolverClientSettings);
            }
            this.ClientSettings.Validate();

            if (this.CacheSettings != null)
            {
                this.CacheSettings.Validate(); 
            }
        }
    }
}