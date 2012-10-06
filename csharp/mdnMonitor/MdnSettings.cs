using System;
using System.Configuration;
using Health.Direct.Config.Store;

namespace Health.Direct.MdnMonitor
{
    public class MdnSettings
    {
        
        const string ConfigConnectStringKey = "configStore";
        const string QueryTimeoutKey = "queryTimeout";
        
        TimeSpan m_dbTimeout;

        public MdnSettings()
        {
            Load();
        }

        
        
        public string ConnectionString { get; set; }
        public TimeSpan QueryTimeout
        {
            get
            {
                return m_dbTimeout;
            }
        }

        public string GetSetting(string name)
        {
            string value = ConfigurationManager.AppSettings[name];
            if (string.IsNullOrEmpty(value))
            {
                throw new ConfigurationErrorsException(string.Format("Timeout Setting {0} not found", name));
            }

            return value;
        }

        private void Load()
        {
            ConnectionString = ConfigurationManager.ConnectionStrings[ConfigConnectStringKey].ConnectionString;

            TimeSpan timeout;
            if (!TimeSpan.TryParse(GetSetting(QueryTimeoutKey), out timeout))
            {
                timeout = ConfigStore.DefaultTimeout;
            }

            m_dbTimeout = timeout;
            if (m_dbTimeout.Ticks <= 0)
            {
                throw new ArgumentException("Invalid query timeout in config");
            }


        }
    }
}
