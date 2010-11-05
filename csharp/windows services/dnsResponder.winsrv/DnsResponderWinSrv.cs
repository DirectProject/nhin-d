using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Configuration;

using Health.Direct.DnsResponder;
using Health.Direct.Config.Store;
using Health.Direct.Config.Client;
using Health.Direct.Common;

namespace Health.Direct.DnsResponder.WinSrv
{
    public partial class DnsResponderWinSrv : ServiceBase
    {
        protected DnsResponderTCP m_dnsResponderTCP = null;
        protected DnsResponderUDP m_dnsResponderUDP = null;
        protected DnsServer m_dnsServer = null;
        protected DnsRecordStorageService m_store = null;

        public DnsResponderWinSrv()
        {
            InitializeComponent();
        }

        /// <summary>
        /// method to initialize fields utilized by the service
        /// </summary>
        protected void InitializeService()
        {

            //----------------------------------------------------------------------------------------------------
            //---load the settings from the related sections in app.config
            ClientSettings certServiceSettings = (ClientSettings)ConfigurationManager.GetSection("ServiceSettingsGroup/DnsRecordManagerServiceSettings");
            ClientSettings dnsRecordServiceSettings = (ClientSettings)ConfigurationManager.GetSection("ServiceSettingsGroup/CertificateManagerServiceSettings");
            DnsServerSettings dnsServerSettings = (DnsServerSettings)ConfigurationManager.GetSection("ServiceSettingsGroup/DnsServerSettings");

            m_store = new DnsRecordStorageService(dnsRecordServiceSettings, certServiceSettings);

            //----------------------------------------------------------------------------------------------------
            //---create the DNS Server instance
            DnsServer dnsServer = new DnsServer(m_store
                , dnsServerSettings);

            //----------------------------------------------------------------------------------------------------
            //---init the tcp and udp responders
            m_dnsResponderTCP = new DnsResponderTCP(m_dnsServer);
            m_dnsResponderUDP = new DnsResponderUDP(m_dnsServer);
            m_dnsServer.UDPResponder.Server.Error += UDPServer_Error;
            m_dnsServer.TCPResponder.Server.Error += TCPServer_Error;

        }

        public void StartService(string[] args)
        {
            InitializeService();
            m_dnsResponderTCP.Start();
            m_dnsResponderUDP.Start();
        }

        public void StopService()
        {

            m_dnsResponderTCP.Stop();
            m_dnsResponderUDP.Stop();
            
        }

        protected override void OnStart(string[] args)
        {
            this.StartService(args);
        }

        protected override void OnStop()
        {
            this.StopService();
        }

        void TCPServer_Error(Exception ex)
        {
            //----------------------------------------------------------------------------------------------------
            //---TODO: incorp logging here
            Console.WriteLine("TCP ERROR {0}", ex);
        }

        void UDPServer_Error(Exception ex)
        {
            //----------------------------------------------------------------------------------------------------
            //---TODO: incorp logging here
            Console.WriteLine("UDP ERROR {0}", ex);
        }
    }
}
