/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico
    Umesh Madan     umeshma@microsoft.com
    Ali Emami       aliemami@microsoft.com
 
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
        private DnsServer m_dnsServer;
        private IDnsStore m_store;
        Diagnostics m_diagnostics;
        
        public DnsResponderWinSrv()
        {
            InitializeComponent();

            try
            {
                m_diagnostics = new Diagnostics(this);
            }
            catch (Exception ex)
            {
                Diagnostics.WriteEventLog(ex);
                throw;
            }
        }

        /// <summary>
        /// method to initialize fields utilized by the service
        /// </summary>
        private void InitializeService()
        {
            m_diagnostics.ServiceInitializing();

            // load the settings from the related sections in app.config
            DnsServerSettings dnsServerSettings = DnsServerSettingsSection.GetSection().AsDnsServerSettings();

            if (dnsServerSettings.ResolutionMode == DnsResolutionMode.AuthoritativeResolution)
            {
                AuthoritativeResolutionSettings settings = 
                    AuthoritativeResolutionSettingsSection.GetSection().AsAuthoritativeResolutionSettings(); 
                m_store = new AuthoritativeRecordResolver(settings);
            }
            else if (dnsServerSettings.ResolutionMode == DnsResolutionMode.RecordStorageService)
            {
                ClientSettings recordRetrievalSettings = ClientSettingsSection.GetSection().AsClientSettings();
                m_store = new DnsRecordStorageService(recordRetrievalSettings);
            }
            else
            {
                throw new System.Configuration.ConfigurationErrorsException("Unknown resolution mode"); 
            }

            // create the DNS Server instance
            m_dnsServer = new DnsServer(m_store, dnsServerSettings);
            //
            // Hook up events for logging/debugging
            //
            m_diagnostics.HookEvents(m_dnsServer);

            m_diagnostics.ServiceInitializingComplete(dnsServerSettings);
        }

        public void StartService(string[] args)
        {
            try
            {
                InitializeService();
                
                m_diagnostics.ServerStarting();
                
                m_dnsServer.Start();
                
                m_diagnostics.ServerStarted();
            }
            catch(Exception ex)
            {
                Diagnostics.WriteEventLog(ex);
                throw;
            }
        }

        public void StopService()
        {
            try
            {
                m_diagnostics.ServerStopping();
                
                m_dnsServer.Stop();
                
                m_diagnostics.ServerStopped();
            }
            catch(Exception ex)
            {
                Diagnostics.WriteEventLog(ex);
                throw;
            }
        }

        protected override void OnStart(string[] args)
        {
            this.StartService(args);
        }

        protected override void OnStop()
        {
            this.StopService();
        }
    }
}
