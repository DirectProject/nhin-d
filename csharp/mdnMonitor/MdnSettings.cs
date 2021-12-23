using System;
using System.Text.Json.Serialization;

namespace Health.Direct.MdnMonitor
{
    /// <summary>
    /// Configuration settings to store general settings used by all MdnMonitor Jobs
    /// </summary>
    public class MdnSettings
    {
        public const string ConnectionStringName = nameof(ConnectionString);
        public const string QueryTimeoutName = nameof(QueryTimeout);
        public const string ProductNameName = nameof(ProductName);

        /// <summary>
        /// Connection string to configure store
        /// </summary>
        public string ConnectionString { get; set; }

        /// <summary>
        /// SQL connection timeout.
        /// </summary>
        [JsonConverter(typeof(TimeSpanConverter))]
        public TimeSpan QueryTimeout { get; set; }

        /// <summary>
        /// Product Name
        ///     Used as the reporting MTA name when direct monitor generates a DSN.
        /// </summary>
        public string ProductName { get; set; } = "Direct Monitor Server";
    }
}
