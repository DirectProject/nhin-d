using System;
using System.Configuration;
using Health.Direct.Config.Store;

namespace Health.Direct.MdnMonitor
{
    /// <summary>
    /// Configuration settings to store general settings used by all MdnMonitor Jobs
    /// </summary>
    public class MdnSettings
    {
        const string DefaultText = "Direct Monitor Server";
        const string ConfigConnectStringKey = "configStore";
        const string QueryTimeoutKey = "queryTimeout";
        const string ProductNameKey = "ProductName";

        TimeSpan m_dbTimeout;
        string m_productName = DefaultText;

        /// <summary>
        /// Create and load instance of MdnSettings
        /// </summary>
        public MdnSettings()
        {
            Load();
        }

        
        /// <summary>
        /// Connection string to configure store
        /// </summary>
        public string ConnectionString { get; set; }
        /// <summary>
        /// SQL connection timeout.
        /// </summary>
        public TimeSpan QueryTimeout
        {
            get
            {
                return m_dbTimeout;
            }
        }

        /// <summary>
        /// Product Name
        ///     Used as the reporting MTA name in DSNs.
        /// </summary>
        public string ProductName
        {
            get { return m_productName; }
        }
        /// <summary>
        /// Access to settings value via key name.
        /// </summary>
        /// <param name="name"></param>
        /// <returns></returns>
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
            var productName = ConfigurationManager.AppSettings.Get(ProductNameKey);
            m_productName = string.IsNullOrEmpty(productName) ? DefaultText : productName; 
        }
    }
}
