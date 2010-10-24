using System.Xml.Serialization;

using Health.Direct.Agent.Config;
using Health.Direct.Common.Certificates;
using Health.Direct.Config.Client;

namespace Health.Direct.SmtpAgent
{
    [XmlType("ServiceResolver")]
    public class CertServiceResolverSettings : CertResolverSettings
    {
        [XmlElement]        
        public ClientSettings ClientSettings
        {
            get;
            set;
        }
        
        public override ICertificateResolver CreateResolver()
        {
            return new ConfigCertificateResolver(this.ClientSettings);            
        }

        public override void Validate()
        {
            if (this.ClientSettings == null)
            {
                throw new SmtpAgentException(SmtpAgentError.MissingCertResolverClientSettings);
            }
            this.ClientSettings.Validate();
        }
    }

    public class AnchorServiceResolverSettings : TrustAnchorResolverSettings
    {
        [XmlElement]
        public ClientSettings ClientSettings
        {
            get;
            set;
        }
    
        public override ITrustAnchorResolver CreateResolver()
        {
            return new ConfigAnchorResolver(this.ClientSettings);
        }

        public override void Validate()
        {
            if (this.ClientSettings == null)
            {
                throw new SmtpAgentException(SmtpAgentError.MissingAnchorResolverClientSettings);
            }
            this.ClientSettings.Validate();
        }
    }
}