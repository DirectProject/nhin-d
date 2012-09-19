using System;
using Quartz;

namespace Health.Direct.MdnMonitor
{
    public class TimeoutSettings : MdnSettings
    {
        const string BulkCountSetting = "BulkCount";
        const string ExpiredMinutesSetting = "ExpiredMinutes";
        const string PickupFolderSettings = "PickupFolder";

        private TimeSpan m_exiredMinutes;
        

        public TimeoutSettings(JobExecutionContext context)
            : base()
        {
            Load(context);
        }

        public int BulkCount { get; set; }
        public TimeSpan ExpiredMinutes
        {
            get { return m_exiredMinutes; }
        }
        public string PickupFolder { get; set; }
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
