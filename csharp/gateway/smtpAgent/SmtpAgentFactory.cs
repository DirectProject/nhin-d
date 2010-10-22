using System;
using System.Diagnostics;

using Health.Direct.Diagnostics.NLog;

using NHINDirect.Container;
using NHINDirect.Diagnostics;

namespace Health.Direct.SmtpAgent
{
    public static class SmtpAgentFactory
    {
        private static readonly object m_initSync = new object();
        private static bool m_initialized;

        public static SmtpAgent Create(string configFilePath)
        {
            SmtpAgentSettings settings = null;
            try
            {
                // move this to some package initializer...
                settings = SmtpAgentSettings.LoadSettings(configFilePath);
                InitializeContainer(settings);
                Log.For<MessageArrivalEventHandler>().Debug(settings);
                return new SmtpAgent(settings);
            }
            catch (Exception ex)
            {
                // if we blow up here we should write out to the EventLog w/o any logger
                // dependencies, etc...
                string source = settings == null 
                                || settings.LogSettings == null 
                                || String.IsNullOrEmpty(settings.LogSettings.EventLogSource)
                                    ? "nhin" 
                                    : settings.LogSettings.EventLogSource;

                EventLog.WriteEntry(source, "While loading SmtpAgent settings" + ex, EventLogEntryType.Error, 1);

                throw;
            }
        }

        private static void InitializeContainer(SmtpAgentSettings settings)
        {
            lock (m_initSync)
            {
                if (!m_initialized)
                {
                    IoC.Initialize(new SimpleDependencyResolver())
                        .Register<ILogFactory>(new NLogFactory(settings.LogSettings))
                        .Register<IAuditor>(new EventLogAuditor())
                        ;
                    m_initialized = true;
                }
            }
        }
    }
}