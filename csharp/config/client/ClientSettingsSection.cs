using System.Configuration;

namespace Health.Direct.Config.Client
{
    public class ClientSettingsSection : ConfigurationSection
    {
        private readonly ClientSettings m_settings;

        public ClientSettingsSection()
        {
            m_settings = new ClientSettings();
        }

        public ClientSettings AsClientSettings()
        {
            return m_settings;
        }

        [ConfigurationProperty("Url", DefaultValue = "undefined", IsRequired = true)]
        public string Url
        {
            get
            {
                return (string) this["Url"];
            }
            set
            {
                this["Url"] = value;
                m_settings.Url = value;
            }
        }

        [ConfigurationProperty("MaxReceivedMessageSize", DefaultValue = int.MaxValue, IsRequired = false)]
        public int MaxReceivedMessageSize
        {
            get
            {
                return (int)this["MaxReceivedMessageSize"];
            }
            set
            {
                this["MaxReceivedMessageSize"] = value;
                m_settings.MaxReceivedMessageSize = value;
            }
        }

        [ConfigurationProperty("Secure", DefaultValue = false, IsRequired = false)]
        public bool Secure
        {
            get
            {
                return (bool)this["Secure"];
            }
            set
            {
                this["Secure"] = value;
                m_settings.Secure = value;
            }
        }

        [ConfigurationProperty("ReceiveTimeout", DefaultValue = -1, IsRequired = false)]
        public int ReceiveTimeoutSeconds
        {
            get
            {
                return (int)this["ReceiveTimeout"];
            }
            set
            {
                this["ReceiveTimeout"] = value;
                m_settings.ReceiveTimeoutSeconds = value;
            }
        }

        [ConfigurationProperty("SendTimeout", DefaultValue = -1, IsRequired = false)]
        public int SendTimeoutSeconds
        {
            get
            {
                return (int)this["SendTimeout"];
            }
            set
            {
                this["SendTimeoutSeconds"] = value;
                m_settings.SendTimeoutSeconds = value;
            }
        }
    }
}