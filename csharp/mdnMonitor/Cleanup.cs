using System;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Config.Store;
using Quartz;

namespace Health.Direct.MdnMonitor
{
    /// <summary>
    /// An object with execution code to clean up expired data.
    /// </summary>
    public abstract class Cleanup
    {
        private ConfigStore m_store;
        private ILogger m_logger;

        /// <summary>
        /// Config Store 
        /// Configured in <c>Load</c>
        /// </summary>
        public ConfigStore Store
        {
            get
            {
                return m_store;
            }
        }

        /// <summary>
        /// Logger
        /// Configured in <c>Load</c>.
        /// </summary>
        public ILogger Logger
        {
            get { return m_logger; }
        }

        /// <summary>
        /// Load applicatioin settings.
        /// </summary>
        protected CleanupSettings Load(JobExecutionContext context)
        {
            try
            {
                var settings = new CleanupSettings(context);
                m_store = new ConfigStore(settings.ConnectionString, settings.QueryTimeout);
                m_logger = Log.For(this);

                return settings;
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