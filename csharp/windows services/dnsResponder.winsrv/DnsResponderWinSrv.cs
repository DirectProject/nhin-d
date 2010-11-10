using System;
using System.Configuration;
using System.ServiceProcess;
using Config=System.Configuration;

using Health.Direct.Config.Client;

using ClientSettingsSection=Health.Direct.Config.Client.ClientSettingsSection;

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
            ClientSettings dnsRecordServiceSettings = ((ClientSettingsSection)ConfigurationManager.GetSection("ServiceSettingsGroup/DnsRecordManagerServiceSettings")).AsClientSettings();
            ClientSettings certServiceSettings = ((ClientSettingsSection)ConfigurationManager.GetSection("ServiceSettingsGroup/CertificateManagerServiceSettings")).AsClientSettings();
            DnsServerSettings dnsServerSettings = ((DnsServerSettingsSection)ConfigurationManager.GetSection("ServiceSettingsGroup/DnsServerSettings")).AsDnsServerSettings();

            m_store = new DnsRecordStorageService(dnsRecordServiceSettings, certServiceSettings);

            //----------------------------------------------------------------------------------------------------
            //---create the DNS Server instance
            m_dnsServer = new DnsServer(m_store
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
