using System;
using Quartz;

namespace Health.Direct.MdnMonitor
{
    /// <summary>
    /// <c>TimeoutSettings</c> represent the quartz.net job-data-map name value store in the jobs.xml config file.
    /// </summary>
    public class TimeoutSettings : MdnSettings
    {
        public const string BulkCountSetting = "BulkCount";
        public const string ExpiredMinutesSetting = "ExpiredMinutes";
        public const string PickupFolderSettings = "PickupFolder";

        private TimeSpan _expiredMinutes;
        
        /// <summary>
        /// Create <c>TimeoutSettings</c> from Job context in the jobs.xml config file.
        /// </summary>
        /// <param name="context"></param>
        public TimeoutSettings(IJobExecutionContext context)
        {
            Load(context);
        }

        /// <summary>
        /// Number of messages to process.
        /// Or number of records to query.
        /// </summary>
        public int BulkCount { get; set; }
        
        /// <summary>
        /// The number of minutes until a notification correlation is considered expired.
        /// </summary>
        public TimeSpan ExpiredMinutes => _expiredMinutes;

        /// <summary>
        /// Location of the pickup folder for message delivery
        /// </summary>
        public string PickupFolder { get; set; }

        /// <summary>
        /// 
        /// </summary>
        public string ErrorCode { get; set; }

        
        private void Load(IJobExecutionContext context)
        {
            BulkCount = context.JobDetail.JobDataMap.GetInt(BulkCountSetting);
            int minutes = context.JobDetail.JobDataMap.GetInt(ExpiredMinutesSetting);
            _expiredMinutes = TimeSpan.FromMinutes(minutes);
            PickupFolder = context.JobDetail.JobDataMap.GetString(PickupFolderSettings);
            ProductName = context.JobDetail.JobDataMap.GetString(ProductNameName);
            ConnectionString = context.JobDetail.JobDataMap.GetString(ConnectionStringName);
            QueryTimeout = context.JobDetail.JobDataMap.GetTimeSpanValue(QueryTimeoutName);
        }
    }
}
