using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Security.Cryptography.X509Certificates;
using System.Net.Sockets;
using System.Threading;
using NHINDirect.Certificates;
using DnsResolver;
using System.Diagnostics;

namespace DnsResponder
{
    class ServerTest
    {
        DnsServerSettings m_settings;
        DnsServer m_server;

        public ServerTest()
        {
            m_settings = new DnsServerSettings();
            m_settings.Address = "127.0.0.1";
            m_settings.Port = 5353;
            m_server = new DnsServer(new RelayStore("8.8.8.8"), m_settings);
            //m_server.TCPResponder.TcpServer.ConnectionAccepted += TCPServer_ConnectionAccepted;
            //m_server.TCPResponder.TcpServer.ConnectionClosed += TCPServer_ConnectionClosed;
            m_server.TCPResponder.TcpServer.Error += TCPServer_Error;
            m_server.UDPResponsder.Server.Error += TCPServer_Error;
            m_server.TCPResponder.Responding += TCPResponder_Responding;
        }

        public void Run(bool useUdp)
        {
            m_server.Start();

            ThreadPool.QueueUserWorkItem(this.Run, "www.bing.com");
            ThreadPool.QueueUserWorkItem(this.Run, "www.microsoft.com");
            ThreadPool.QueueUserWorkItem(this.Run, "www.live.com");

            DnsRecordPrinter printer = new DnsRecordPrinter(Console.Out);
            DnsClient client = new DnsClient(m_settings.Address, m_settings.Port);
            client.UseUDPFirst = useUdp;
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
        
        void Run(object state)
        {
            string search = (string) state;
            DnsClient client = new DnsClient(m_settings.Address, m_settings.Port);
            client.UseUDPFirst = false;
            client.Timeout = 100000;

            Stopwatch watch = new Stopwatch();
            watch.Start();
            for (int i = 0; i < 100; ++i)
            {
                //Console.Write('.');
                client.ResolveA(search);
            }
            watch.Stop();
            Console.WriteLine(watch.ElapsedMilliseconds);
            Console.WriteLine(1000 * ((double)100 / watch.ElapsedMilliseconds));
        }

        void TCPResponder_Responding(DnsResponse arg2)
        {
            Print("Responding");
        }

        void TCPServer_Error(Exception ex)
        {
            Console.WriteLine("==========");
            Console.WriteLine("==========");
            Print("ERROR");
            Console.WriteLine(ex);
            Console.WriteLine("==========");
            Console.WriteLine("==========");
        }

        void TCPServer_ConnectionAccepted(Socket arg2)
        {
            Print("Accepted");
        }

        void TCPServer_ConnectionClosed(Socket arg2)
        {
            Print("Closed");
        }

        void Print(string format, params object[] args)
        {
            Console.WriteLine("{0} [Thread {1}]", string.Format(format, args), Thread.CurrentThread.ManagedThreadId);
        }
    }
}
