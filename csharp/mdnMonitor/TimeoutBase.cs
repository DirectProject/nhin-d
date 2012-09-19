using System;
using System.Collections.Generic;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Store;
using Quartz;

namespace Health.Direct.MdnMonitor
{
    public abstract class TimeoutBase
    {
        private ConfigStore m_store;
        private ILogger m_logger;

        protected ConfigStore Store
        {
            get
            {
                return m_store;
            }
        }
        protected ILogger Logger
        {
            get { return m_logger; }
        }

        public abstract void Execute(JobExecutionContext context);
        protected abstract IList<Mdn> ExpiredMdns(TimeoutSettings settings);

        protected string UniqueFileName()
        {
            return StringExtensions.UniqueString() + ".eml";
        }

        protected TimeoutSettings Load(JobExecutionContext context)
        {
            try
            {
                var settings = new TimeoutSettings(context);
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