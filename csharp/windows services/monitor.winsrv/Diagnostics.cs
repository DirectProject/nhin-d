using System;
using Health.Direct.Common.Diagnostics;

namespace Health.Direct.Monitor.WinSrv
{
    internal class Diagnostics
    {
        public const string EventLogSourceName = "Health.Direct.Monitor.WinSrv";

        readonly ILogger m_logger;

        internal Diagnostics(MdnMonitorWinSrv service)
        {
            m_logger = Log.For(service);
        }

        internal ILogger Logger
        {
            get
            {
                return m_logger;
            }
        }

        internal void ServiceInitializing()
        {
            WriteEvent("Service initializing");
        }

        internal void ServiceInitializingComplete()
        {
            WriteEvent("Service initialized successfully");
        }
        

        internal void ServerStarting()
        {
            WriteEvent("Server starting");
        }
        internal void ServerStarted()
        {
            WriteEvent("Server started successfully");
        }
        internal void ServerStopping()
        {
            WriteEvent("Server stopping");
        }
        internal void ServerStopped()
        {
            WriteEvent("Server stopped");
        }

        internal void WriteEvent(string message)
        {
            m_logger.Info(message);
            WriteEventLog(message);
        }
        
        internal void WriteEvent(Exception ex)
        {
            m_logger.Error(ex);
            WriteEventLog(ex);
        }

        internal static void WriteEventLog(string message)
        {
            EventLogHelper.WriteInformation(EventLogSourceName, message);
        }

        internal static void WriteEventLog(Exception ex)
        {
            EventLogHelper.WriteError(EventLogSourceName, ex.ToString());
        }
    }
}
