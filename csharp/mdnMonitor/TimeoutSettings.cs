using System;
using Quartz;

namespace Health.Direct.MdnMonitor
{
    /// <summary>
    /// <c>TimeoutSettings</c> represent the quartznet job-data-map name value store in the jobs.xml config file.
    /// </summary>
    public class TimeoutSettings : MdnSettings
    {
        const string BulkCountSetting = "BulkCount";
        const string ExpiredMinutesSetting = "ExpiredMinutes";
        const string PickupFolderSettings = "PickupFolder";

        private TimeSpan m_exiredMinutes;
        
        /// <summary>
        /// Create <c>TimeoutSettings</c> from Job context in the jobs.xml config file.
        /// </summary>
        /// <param name="context"></param>
        public TimeoutSettings(JobExecutionContext context)
            : base()
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
        public TimeSpan ExpiredMinutes
        {
            get { return m_exiredMinutes; }
        }
        /// <summary>
        /// Location of the pickup folder for message delivery
        /// </summary>
        public string PickupFolder { get; set; }

        /// <summary>
        /// 
        /// </summary>
        public string ErrorCode { get; set; }

        
        private void Load(JobExecutionContext context)
        {
            BulkCount = context.JobDetail.JobDataMap.GetInt(BulkCountSetting);

            int minutes = context.JobDetail.JobDataMap.GetInt(ExpiredMinutesSetting);
            m_exiredMinutes = TimeSpan.FromMinutes(minutes);

            PickupFolder = context.JobDetail.JobDataMap.GetString(PickupFolderSettings);

        }
    }
}
