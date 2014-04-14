using System;
using System.Collections.Generic;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Store;
using Quartz;

namespace Health.Direct.MdnMonitor
{
    
    ///<summary>
    /// An object holding timout conditions and execution code to act on those conditions.
    ///</summary>
    public abstract class Timeout
    {
        private ConfigStore m_store;
        private ILogger m_logger;

        /// <summary>
        /// Reference MDNManager for access to data store.
        /// </summary>
        protected MdnManager MDNManager { get; set; }

        
        /// <summary>
        /// Config Store 
        /// Configured in <c>Load</c>
        /// </summary>
        protected ConfigStore Store
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
        protected ILogger Logger
        {
            get { return m_logger; }
        }

        

        /// <summary>
        /// Get queued mdns that are considered expired based on type of <c>Timeout</c>
        /// </summary>
        /// <param name="settings"></param>
        /// <returns></returns>
        protected abstract IList<Mdn> ExpiredMdns(TimeoutSettings settings);

        /// <summary>
        /// Generate unique mime file name.
        /// </summary>
        /// <returns></returns>
        protected string UniqueFileName()
        {
            return StringExtensions.UniqueString() + ".eml";
        }

        /// <summary>
        /// Load applicatioin settings and job settings
        /// </summary>
        /// <param name="context"></param>
        /// <returns></returns>
        protected TimeoutSettings Load(JobExecutionContext context)
        {
            try
            {
                var settings = new TimeoutSettings(context);
                m_store = new ConfigStore(settings.ConnectionString, settings.QueryTimeout);
                m_logger = Log.For(this);
                
                MDNManager = new MdnManager(new ConfigStore(settings.ConnectionString));

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