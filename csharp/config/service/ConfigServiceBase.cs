using System;
using System.Diagnostics;
using System.ServiceModel;

using Health.Direct.Common.Diagnostics;
using Health.Direct.Config.Store;

namespace Health.Direct.Config.Service
{
    public class ConfigServiceBase
    {
        private readonly ConfigStore m_store;
        private readonly ILogger m_logger;

        public ConfigServiceBase()
        {
            try
            {
                m_store = Service.Current.Store;
                m_logger = Log.For(this);
            }
            catch (Exception ex)
            {
                WriteToEventLog(ex);
                throw;
            }
        }

        private static void WriteToEventLog(Exception ex)
        {
            const string source = "Health.Direct.Config.Service";

            EventLogHelper.WriteError(source, ex.Message);
            EventLogHelper.WriteError(source, ex.GetBaseException().ToString());
        }

        private ILogger Logger
        {
            get { return m_logger; }
        }

        protected ConfigStore Store
        {
            get { return m_store; }
        }

        protected FaultException<ConfigStoreFault> CreateFault(string methodName, Exception ex)
        {
            Logger.Error(string.Format("While performing {0}()", methodName), ex);

            ConfigStoreFault fault = ConfigStoreFault.ToFault(ex);
            return new FaultException<ConfigStoreFault>(fault, new FaultReason(fault.ToString()));
        }
    }
}