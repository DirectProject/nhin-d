using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Linq;
using System.ServiceProcess;
using System.Text;using System.Configuration;

using Health.Direct.DnsResponder;
using Health.Direct.Config.Store;

namespace Health.Direct.DnsResponder.WinSrv
{
    public partial class DnsResponderWinSrv : ServiceBase
    {
        protected DnsResponderTCP m_dnsResponderTCP = null;
        protected DnsResponderUDP m_dnsResponderUDP = null;
        protected DnsServer m_dnsServer = null;
        protected DBStore m_store = null;

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
           //---TODO: load this from configuration
           DnsServerSettings settings = new DnsServerSettings();
            
            //----------------------------------------------------------------------------------------------------
            //---create the DNS Server instance
            m_dnsServer = new DnsServer(new DBStore()
                , settings);

            //----------------------------------------------------------------------------------------------------
            //---init the tcp and udp responders
            m_dnsResponderTCP = new DnsResponderTCP(m_dnsServer);
            m_dnsResponderUDP = new DnsResponderUDP(m_dnsServer);
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
    }
}
