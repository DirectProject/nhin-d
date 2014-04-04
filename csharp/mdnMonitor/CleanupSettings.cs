using Quartz;

namespace Health.Direct.MdnMonitor
{
    /// <summary>
    /// <c>CleanupSettins</c> represent the quartznet job-data-map name value store in the jobs.xml config file.
    /// </summary>
    public class CleanupSettings : MdnSettings
    {
        const string DaysSetting = "Days";
        const string BulkCountSetting = "BulkCount";
        const int BulkCountDefault = 100;

        /// <summary>
        /// Create <c>TimeoutSettings</c> from Job context in the jobs.xml config file.
        /// </summary>
        /// <param name="context"></param>
        public CleanupSettings(JobExecutionContext context)
            : base()
        {
            Load(context);
        }

        /// <summary>
        /// Number of days to retains.
        /// </summary>
        public int Days { get; set; }

        /// <summary>
        /// Number of messages to process.
        /// Or number of records to query.
        /// </summary>
        public int BulkCount { get; set; }

        private void Load(JobExecutionContext context)
        {
            Days = context.JobDetail.JobDataMap.GetInt(DaysSetting);
            BulkCount = context.JobDetail.JobDataMap.GetInt(BulkCountSetting);
            if (BulkCount == 0)
            {
                BulkCount = BulkCountDefault; 
            }
        }
}
}
