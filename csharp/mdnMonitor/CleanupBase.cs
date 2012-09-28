using System;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Config.Store;
using Quartz;

namespace Health.Direct.MdnMonitor
{
    public abstract class CleanupBase
    {
        private ConfigStore m_store;
        private ILogger m_logger;

        public ConfigStore Store
        {
            get
            {
                return m_store;
            }
        }

        public ILogger Logger
        {
            get { return m_logger; }
        }

        public abstract void Execute(JobExecutionContext context);

        protected void Load()
        {
            try
            {
                var settings = new MdnSettings();
                m_store = new ConfigStore(settings.ConnectionString, settings.QueryTimeout);
                m_logger = Log.For(this);
            }
            catch (Exception e)
            {
                WriteToEventLog(e);
                var je = new JobExecutionException(e);
                je.UnscheduleAllTriggers = true;
                throw je;
            }
        }

        private static void WriteToEventLog(Exception ex)
        {
            const string source = "Health.Direct.MdnMonitor";

            EventLogHelper.WriteError(source, ex.Message);
            EventLogHelper.WriteError(source, ex.GetBaseException().ToString());
        }
    }
}