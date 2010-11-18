/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.ServiceProcess;

using Health.Direct.Config.Client;
using Health.Direct.Common.Diagnostics;

namespace Health.Direct.DnsResponder.WinSrv
{
    public partial class DnsResponderWinSrv : ServiceBase
    {
        const string EventLogSourceName = "Health.Direct.DnsResponder.WinSrv";

        private DnsServer m_dnsServer;
        private DnsRecordStorageService m_store;
        private readonly ILogger m_logger;

        public DnsResponderWinSrv()
        {
            InitializeComponent();

            try
            {
                m_logger = Log.For(this);
            }
            catch (Exception ex)
            {
                WriteToEventLog(ex);
                throw;
            }
        }

        private ILogger Logger
        {
            get { return m_logger; }
        }

        private static void WriteToEventLog(Exception ex)
        {
            EventLogHelper.WriteError(EventLogSourceName, ex.Message);
            EventLogHelper.WriteError(EventLogSourceName, ex.GetBaseException().ToString());
        }

        //private static void WriteToEventLogWarn(string message)
        //{
        //    EventLogHelper.WriteWarning(EventLogSourceName, message);
        //}

        private static void WriteToEventLogInfo(string message)
        {
            EventLogHelper.WriteInformation(EventLogSourceName, message);
        }

        /// <summary>
        /// method to initialize fields utilized by the service
        /// </summary>
        private void InitializeService()
        {
            WriteToEventLogInfo("Service is being initialized");

            // load the settings from the related sections in app.config
            ClientSettings recordRetrievalSettings = ClientSettingsSection.GetSection().AsClientSettings();
            DnsServerSettings dnsServerSettings = DnsServerSettingsSection.GetSection().AsDnsServerSettings();

            m_store = new DnsRecordStorageService(recordRetrievalSettings);

            // create the DNS Server instance
            m_dnsServer = new DnsServer(m_store, dnsServerSettings);

            // setup the error listener
            m_dnsServer.Error += Server_Error;

            WriteToEventLogInfo("Service has been fully initialized");
        }

        public void StartService(string[] args)
        {
            InitializeService();
            m_dnsServer.Start();
        }

        public void StopService()
        {
            m_dnsServer.Stop();
        }

        protected override void OnStart(string[] args)
        {
            this.StartService(args);
        }

        protected override void OnStop()
        {
            this.StopService();
        }

        static void Server_Error(Exception ex)
        {
            WriteToEventLog(ex);
        }
    }
}
