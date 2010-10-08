using System;
using System.ServiceModel;

using NHINDirect.Config.Store;
using NHINDirect.Diagnostics;

namespace NHINDirect.Config.Service
{
    public class ConfigServiceBase
    {
        private readonly ConfigStore m_store;
        private readonly ILogger m_logger;

        public ConfigServiceBase()
        {
            m_store = Service.Current.Store;
            m_logger = Log.For(this);
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