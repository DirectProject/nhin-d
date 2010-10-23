using System;

using Health.Direct.Common.Container;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Diagnostics.NLog;

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
                                || string.IsNullOrEmpty(settings.LogSettings.EventLogSource)
                                    ? null 
                                    : settings.LogSettings.EventLogSource;

                EventLogHelper.WriteError(source, "While loading SmtpAgent settings - " + ex);

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