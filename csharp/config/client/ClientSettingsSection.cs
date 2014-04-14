using System;
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
            m_settings.Url = this.Url;
            m_settings.MaxReceivedMessageSize = this.MaxReceivedMessageSize;
            m_settings.Secure = this.Secure;
            m_settings.ReceiveTimeoutSeconds = this.ReceiveTimeoutSeconds;
            m_settings.SendTimeoutSeconds = this.SendTimeoutSeconds;

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
            }
        }

        [ConfigurationProperty("MaxReceivedMessageSize", DefaultValue = Int32.MaxValue, IsRequired = false)]
        public int MaxReceivedMessageSize
        {
            get
            {
                return (int)this["MaxReceivedMessageSize"];
            }
            set
            {
                this["MaxReceivedMessageSize"] = value;
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
            }
        }

        public static ClientSettingsSection GetSection()
        {
            return ((ClientSettingsSection)ConfigurationManager.GetSection("ServiceSettingsGroup/RecordRetrievalServiceSettings"));
        }
    }
}