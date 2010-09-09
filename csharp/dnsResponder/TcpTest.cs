using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Security.Cryptography.X509Certificates;
using System.Net.Sockets;
using System.Threading;
using NHINDirect.Certificates;
using DnsResolver;

namespace DnsResponder
{
    class TcpTest
    {
        DnsServerSettings m_settings;
        DnsServer m_server;

        public TcpTest()
        {
            m_settings = new DnsServerSettings();
            m_settings.Address = "127.0.0.1";
            m_settings.Port = 5353;
            m_server = new DnsServer(new RelayStore("8.8.8.8"), m_settings);
            m_server.TCPServer.ConnectionAccepted += TCPServer_ConnectionAccepted;
            m_server.TCPServer.ConnectionClosed += TCPServer_ConnectionClosed;
            m_server.TCPServer.Error += TCPServer_Error;
            m_server.TCPResponder.Responding += TCPResponder_Responding;
        }

        public void Run()
        {
            m_server.Start();

            DnsRecordPrinter printer = new DnsRecordPrinter(Console.Out);
            DnsClient client = new DnsClient(m_settings.Address, m_settings.Port);
            client.UseUDPFirst = false;
            client.Timeout = 100000;

            string line;

            Print("UI IN");
            while ((line = Console.ReadLine()) != null)
            {
                line = line.Trim();
                if (string.IsNullOrEmpty(line))
                {
                    continue;
                }

                if (line == "quit" || line == "exit")
                {
                    break;
                }
                try
                {
                    IEnumerable<AddressRecord> matches = client.ResolveA(line);
                    if (matches == null)
                    {
                        Console.WriteLine("===No matches===");
                        continue;
                    }

                    foreach (AddressRecord match in matches)
                    {
                        printer.Print(match);
                    }
                }
                catch (Exception ex)
                {
                    Console.WriteLine(ex);
                }
            }

            m_server.Stop();
        }


        void TCPResponder_Responding(DnsResponder arg1, DnsResponse arg2)
        {
            Print("Responding");
        }

        void TCPServer_Error(TcpServer arg1, Exception ex)
        {
            Console.WriteLine("==========");
            Console.WriteLine("==========");
            Print("ERROR");
            Console.WriteLine(ex);
            Console.WriteLine("==========");
            Console.WriteLine("==========");
        }

        void TCPServer_ConnectionAccepted(TcpServer arg1, Socket arg2)
        {
            Print("Accepted");
        }

        void TCPServer_ConnectionClosed(TcpServer arg1, Socket arg2)
        {
            Print("Closed");
        }

        void Print(string format, params object[] args)
        {
            Console.WriteLine("{0} [Thread {1}]", string.Format(format, args), Thread.CurrentThread.ManagedThreadId);
        }
    }
}
