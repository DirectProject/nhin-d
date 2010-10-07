/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Configuration;
using System.ServiceModel;
using System.Diagnostics;

using Health.Net.Direct.Diagnostics.NLog;

using NHINDirect.Config.Store;
using System.Data.Sql;

using NHINDirect.Container;
using NHINDirect.Diagnostics;

namespace NHINDirect.Config.Service
{
    public class Service
    {
        public readonly static Service Current = new Service();
        
        public const string Namespace = ConfigStore.Namespace;

        readonly ServiceSettings m_settings;
        readonly ConfigStore m_store;
        private readonly ILogger m_logger;
                
        public Service()
        {   
            try
            {
                InitializeContainer();

                m_logger = Log.For(this);

                m_settings = new ServiceSettings();
                Logger.Info("Starting Service");

                m_store = new ConfigStore(m_settings.StoreConnectString);
                Logger.Info("Service Started Successfully");
            }
            catch(Exception ex)
            {
                string msg = string.Format("Fatal error while initializing: {0}", ex);
                EventLog.WriteEntry("nhinConfigService", msg, EventLogEntryType.Error);
                throw;
            }
        }

        private ILogger Logger
        {
            get { return m_logger; }
        }

        private static void InitializeContainer()
        {
            LogFileSettings settings = LogFileSection.GetAsSettings();

            // setup the container here... grrr... we're duplicating this!
            IoC.Initialize(new SimpleDependencyResolver()
                               .Register<ILogFactory>(new NLogFactory(settings))
                );
        }

        public string Name 
        {
            get
            {
                // A property instead of a constant in case we want to make this configurable some day
                return "ConfigService";
            }
        }
        
        public ServiceSettings Settings
        {
            get
            {
                return m_settings;
            }
        }
                
        public ConfigStore Store
        {
            get
            {
                return m_store;
            }
        }
        
        public static FaultException<ConfigStoreFault> CreateFault(Exception ex)
        {
            ConfigStoreFault fault = ConfigStoreFault.ToFault(ex);
            return new FaultException<ConfigStoreFault>(fault, new FaultReason(fault.ToString()));
        }
    }
}
