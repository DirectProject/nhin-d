using System;

using Health.Direct.Common.Resolver;

namespace Health.Direct.DnsResponder.Tests
{
    public class TestServer
    {
        public static readonly DnsServerSettings DefaultSettings;
        public static TestServer Default;
        
        DnsServerSettings m_settings;
        DnsServer m_server;
        
        static TestServer()
        {
            DefaultSettings = new DnsServerSettings();
            DefaultSettings.Address = "127.0.0.1";
            DefaultSettings.Port = 5353;
            DefaultSettings.UdpServerSettings.MaxOutstandingAccepts = 1;

            Default = new TestServer(TestStore.Default.Store);
            Default.Server.Start();
        }
        
        public TestServer(IDnsStore store)
            : this(store, TestServer.DefaultSettings)
        {
        }

        public TestServer(IDnsStore store, DnsServerSettings settings)
        {
            m_settings = settings;
            
            m_server = new DnsServer(store, settings);
            m_server.UDPResponder.Server.Error += UDPServer_Error;
            m_server.TCPResponder.Server.Error += TCPServer_Error;
        }
        
        public DnsServer Server
        {
            get
            {
                return m_server;
            }
        }
        
        public DnsClient CreateClient()
        {
            DnsServerSettings settings = this.Server.Settings;
            DnsClient client = new DnsClient(settings.Address, settings.Port);
            return client;
        }
        
        void TCPServer_Error(Exception ex)
        {
            //Console.WriteLine("TCP ERROR {0}", ex);
        }

        void UDPServer_Error(Exception ex)
        {
            //Console.WriteLine("UDP ERROR {0}", ex);
        }
    }
}